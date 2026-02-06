
package kz.jarkyn.backend.operation.service;


import kz.jarkyn.backend.operation.sorts.TurnoverSorts;
import kz.jarkyn.backend.operation.specifications.TurnoverSpecifications;
import kz.jarkyn.backend.party.model.WarehouseEntity;
import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.good.model.GoodEntity;
import kz.jarkyn.backend.operation.model.message.TurnoverFixMessage;
import kz.jarkyn.backend.operation.mode.TurnoverEntity;
import kz.jarkyn.backend.operation.repository.TurnoverRepository;
import kz.jarkyn.backend.core.config.AppRabbitTemplate;
import kz.jarkyn.backend.core.config.RabbitRoutingKeys;
import kz.jarkyn.backend.document.core.model.ItemEntity;
import kz.jarkyn.backend.document.core.repository.ItemRepository;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

@Service
public class TurnoverService {
    private static final Logger log = LoggerFactory.getLogger(TurnoverService.class);

    private final TurnoverRepository turnoverRepository;
    private final AppRabbitTemplate appRabbitTemplate;
    private final EntityManager entityManager;
    private final ItemRepository itemRepository;

    public TurnoverService(
            TurnoverRepository turnoverRepository,
            AppRabbitTemplate appRabbitTemplate,
            EntityManager entityManager,
            ItemRepository itemRepository
    ) {
        this.turnoverRepository = turnoverRepository;
        this.appRabbitTemplate = appRabbitTemplate;
        this.entityManager = entityManager;
        this.itemRepository = itemRepository;
    }

    @Transactional(readOnly = true)
    public List<StockDto> findStockAtMoment(
            List<Pair<WarehouseEntity, GoodEntity>> goodsPair, Instant moment
    ) {
        return goodsPair.stream()
                .collect(groupingBy(Pair::getFirst, mapping(Pair::getSecond, Collectors.toList())))
                .entrySet().stream()
                .flatMap(entry -> {
                    Map<GoodEntity, Integer> remainMap = findRemindAtMoment(entry.getKey(), entry.getValue(), moment);
                    Map<GoodEntity, BigDecimal> costPriceMap = findCostPriceAtMoment(entry.getKey(), entry.getValue(), moment);
                    return entry.getValue().stream().map(good ->
                            new StockDto(entry.getKey(), good, remainMap.get(good), costPriceMap.get(good)));
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StockDto> findStockByDocument(DocumentEntity document) {
        List<ItemEntity> items = itemRepository.findByDocument(document);
        List<Pair<WarehouseEntity, GoodEntity>> goodsPair = items.stream()
                .map(itemEntity -> Pair.of(document.getWarehouse(), itemEntity.getGood()))
                .distinct().toList();
        if (!Boolean.TRUE.equals(document.getCommited())) {
            return findStockAtMoment(goodsPair, document.getMoment());
        }

        Map<GoodEntity, List<TurnoverEntity>> turnoverByGood = turnoverRepository
                .findAll(TurnoverSpecifications.document(document)).stream()
                .collect(groupingBy(TurnoverEntity::getGood));

        Map<GoodEntity, StockDto> fallback = findStockAtMoment(goodsPair, document.getMoment()).stream()
                .collect(toMap(StockDto::getGood, Function.identity()));

        return items.stream().map(ItemEntity::getGood).map(good -> {
            List<TurnoverEntity> turnovers = turnoverByGood.getOrDefault(good, List.of());
            if (turnovers.isEmpty()) {
                log.warn("TURNOVER_STOCK: No turnover rows for committed document. documentId={}, goodId={}",
                        document.getId(), good.getId());
                return fallback.get(good);
            }
            TurnoverEntity turnover = turnovers.getFirst();
            return new StockDto(turnover.getWarehouse(), turnover.getGood(),
                    turnover.getRemain(), turnover.getCostPricePerUnit());
        }).toList();
    }


    @Transactional
    public void create(DocumentEntity document, GoodEntity good, Integer quantity, BigDecimal costPricePerUnit) {
        TurnoverEntity turnover = new TurnoverEntity();
        turnover.setDocument(document);
        turnover.setGood(good);
        turnover.setQuantity(quantity);
        turnover.setWarehouse(document.getWarehouse());
        turnover.setMoment(document.getMoment());
        turnover.setCostPricePerUnit(costPricePerUnit);
        turnover.setRemain(0);
        turnoverRepository.save(turnover);
        sendFixMessage(turnover.getWarehouse(), turnover.getGood(), turnover.getMoment());
    }

    @Transactional
    public void delete(DocumentEntity document, GoodEntity good) {
        TurnoverEntity turnover = turnoverRepository.findOne(TurnoverSpecifications.document(document)
                        .and(TurnoverSpecifications.warehouseAndGoodEquals(document.getWarehouse(), good)))
                .orElseThrow();
        turnoverRepository.findAll(TurnoverSpecifications.lastInflow(turnover)).forEach(outflow -> {
            outflow.setLastInflow(null);
            outflow.setLastInflowUsedQuantity(null);
        });
        turnoverRepository.delete(turnover);
    }

    @Transactional
    public void delete(DocumentEntity document) {
        List<TurnoverEntity> turnovers = turnoverRepository.findAll(Specification
                .where(TurnoverSpecifications.document(document)));
        turnoverRepository.deleteAll(turnovers);
        for (TurnoverEntity turnover : turnovers) {
            sendFixMessage(turnover.getWarehouse(), turnover.getGood(), turnover.getMoment());
        }
    }

    private Map<GoodEntity, Integer> findRemindAtMoment(
            WarehouseEntity warehouse, List<GoodEntity> goods, Instant moment) {
        Map<GoodEntity, Integer> lastTurnoverMap = turnoverRepository
                .findLastByMomentLessThan(warehouse, goods, moment).stream()
                .collect(toMap(TurnoverEntity::getGood, turnover -> turnover.getRemain() + turnover.getQuantity()));
        Map<GoodEntity, Integer> defaultMap = goods.stream().distinct().collect(toMap(good -> good, _ -> 0));

        LinkedHashMap<GoodEntity, Integer> result = new LinkedHashMap<>();
        Stream.of(lastTurnoverMap, defaultMap).forEach(map -> map.forEach(result::putIfAbsent));
        return result;
    }


    private Map<GoodEntity, BigDecimal> findCostPriceAtMoment(
            WarehouseEntity warehouse, List<GoodEntity> goods, Instant moment) {
        Map<GoodEntity, BigDecimal> firstInflowMap = turnoverRepository
                .findFirstInflow(warehouse, goods, moment).stream()
                .collect(toMap(TurnoverEntity::getGood, TurnoverEntity::getCostPricePerUnit));
        Map<GoodEntity, BigDecimal> defaultMap = goods.stream().distinct()
                .collect(toMap(good -> good, _ -> BigDecimal.ZERO));

        LinkedHashMap<GoodEntity, BigDecimal> result = new LinkedHashMap<>();
        Stream.of(firstInflowMap, defaultMap).forEach(map -> map.forEach(result::putIfAbsent));
        return result;
    }

    @Transactional
    public void fix(WarehouseEntity warehouse, GoodEntity good, Instant moment) {
        fixRemind(warehouse, good, moment);
        fixCostPrice(warehouse, good, moment);
    }

    @RabbitListener(queues = "${rabbitmq.queue.turnover-fix:turnover-fix}", concurrency = "4")
    @Transactional
    public void onTurnoverFix(TurnoverFixMessage message) {
        WarehouseEntity warehouse = entityManager.find(WarehouseEntity.class, message.getWarehouseId());
        if (warehouse == null) {
            log.error("TURNOVER_LISTENER: Warehouse not found: {}", message.getWarehouseId());
            return;
        }
        GoodEntity good = entityManager.find(GoodEntity.class, message.getGoodId());
        if (good == null) {
            log.error("TURNOVER_LISTENER: Good not found: {}", message.getGoodId());
            return;
        }
        fix(warehouse, good, message.getMoment());
    }

    private void sendFixMessage(WarehouseEntity warehouse, GoodEntity good, Instant moment) {
        TurnoverFixMessage message = new TurnoverFixMessage(warehouse.getId(), good.getId(), moment);
        appRabbitTemplate.sendAfterCommit(RabbitRoutingKeys.TURNOVER_FIX, message);
    }

    private void fixRemind(WarehouseEntity warehouse, GoodEntity good, Instant moment) {
        Specification<TurnoverEntity> spec = Specification
                .where(TurnoverSpecifications.warehouseAndGoodEquals(warehouse, good))
                .and(TurnoverSpecifications.momentGreaterThanEqual(moment));
        List<TurnoverEntity> turnovers = turnoverRepository.findAll(spec, TurnoverSorts.byMomentAsc());
        Integer remain = findRemindAtMoment(warehouse, List.of(good), moment).get(good);
        for (TurnoverEntity turnover : turnovers) {
            turnover.setRemain(remain);
            remain += turnover.getQuantity();
        }
    }

    private void fixCostPrice(WarehouseEntity warehouse, GoodEntity good, Instant moment) {
        List<TurnoverEntity> outflows = turnoverRepository.findAll(Specification
                        .where(TurnoverSpecifications.warehouseAndGoodEquals(warehouse, good))
                        .and(TurnoverSpecifications.momentGreaterThanEqual(moment))
                        .and(TurnoverSpecifications.isOutflow())
                , TurnoverSorts.byMomentAsc());

        TurnoverEntity lastOutflow = turnoverRepository.findOne(Specification
                        .where(TurnoverSpecifications.warehouseAndGoodEquals(warehouse, good))
                        .and(TurnoverSpecifications.momentLessThan(moment))
                        .and(TurnoverSpecifications.isOutflow())
                , TurnoverSorts.byMomentDesc()).orElse(null);

        TurnoverEntity lastUsedInflow = lastOutflow != null ? lastOutflow.getLastInflow() : null;
        Instant lastUsedInflowMoment = lastUsedInflow != null ? lastUsedInflow.getMoment() : Instant.EPOCH;
        List<TurnoverEntity> notUsedInflows = turnoverRepository.findAll(Specification
                .where(TurnoverSpecifications.warehouseAndGoodEquals(warehouse, good))
                .and(TurnoverSpecifications.momentGreaterThanEqual(lastUsedInflowMoment))
                .and(TurnoverSpecifications.isIncome()));
        int inflowUsedInflowUserdQuantity = lastUsedInflow != null ? lastOutflow.getLastInflowUsedQuantity() : 0;

        int notUsedInflowIndex = 0;
        for (TurnoverEntity outflow : outflows) {
            int remainingOutflowQuantity = -outflow.getQuantity();
            BigDecimal totalCostPrice = BigDecimal.ZERO;
            while (remainingOutflowQuantity > 0 && notUsedInflowIndex < notUsedInflows.size()) {
                TurnoverEntity inflow = notUsedInflows.get(notUsedInflowIndex);
                int available = inflow.getQuantity() - inflowUsedInflowUserdQuantity;
                if (available < 0) {
                    throw new IllegalStateException("Inflow overused: inflowUsedQuantity=" + inflowUsedInflowUserdQuantity
                            + " > inflow.getQuantity()=" + inflow.getQuantity()
                            + " (inflowId=" + inflow.getId() + ", good=" + inflow.getGood().getName() + ")");
                }
                int readyToUseQuantity = Math.min(available, remainingOutflowQuantity);
                BigDecimal costPrice = inflow.getCostPricePerUnit().multiply(BigDecimal.valueOf(readyToUseQuantity));
                totalCostPrice = totalCostPrice.add(costPrice);
                inflowUsedInflowUserdQuantity += readyToUseQuantity;
                remainingOutflowQuantity -= readyToUseQuantity;
                if (inflowUsedInflowUserdQuantity == inflow.getQuantity()) {
                    notUsedInflowIndex++;
                    inflowUsedInflowUserdQuantity = 0;
                }
            }
            BigDecimal costPerUnit = totalCostPrice.divide(BigDecimal.valueOf(-outflow.getQuantity()), RoundingMode.HALF_UP);
            outflow.setCostPricePerUnit(costPerUnit);
            if (notUsedInflowIndex < notUsedInflows.size()) {
                outflow.setLastInflow(notUsedInflows.get(notUsedInflowIndex));
                outflow.setLastInflowUsedQuantity(inflowUsedInflowUserdQuantity);
            } else {
                outflow.setLastInflow(notUsedInflows.getLast());
                outflow.setLastInflowUsedQuantity(notUsedInflows.getLast().getQuantity());
            }
        }
    }


    public static class StockDto {
        private final WarehouseEntity warehouse;
        private final GoodEntity good;
        private final Integer remain;
        private final BigDecimal costPricePerUnit;

        public StockDto(WarehouseEntity warehouse, GoodEntity good, Integer remain, BigDecimal costPricePerUnit) {
            this.warehouse = warehouse;
            this.good = good;
            this.remain = remain;
            this.costPricePerUnit = costPricePerUnit;
        }

        public WarehouseEntity getWarehouse() {
            return warehouse;
        }

        public GoodEntity getGood() {
            return good;
        }

        public Integer getRemain() {
            return remain;
        }

        public BigDecimal getCostPricePerUnit() {
            return costPricePerUnit;
        }
    }
}

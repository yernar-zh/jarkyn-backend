
package kz.jarkyn.backend.operation.service;


import kz.jarkyn.backend.operation.sorts.TurnoverSorts;
import kz.jarkyn.backend.operation.specifications.TurnoverSpecifications;
import kz.jarkyn.backend.warehouse.model.WarehouseEntity;
import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.warehouse.model.GoodEntity;
import kz.jarkyn.backend.operation.mode.TurnoverEntity;
import kz.jarkyn.backend.operation.repository.TurnoverRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

@Service
public class TurnoverService {
    private final TurnoverRepository turnoverRepository;

    public TurnoverService(
            TurnoverRepository turnoverRepository
    ) {
        this.turnoverRepository = turnoverRepository;
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
        fix(turnover.getWarehouse(), turnover.getGood(), turnover.getMoment());
    }

    @Transactional
    public void delete(DocumentEntity document) {
        List<TurnoverEntity> turnovers = turnoverRepository.findAll(Specification
                .where(TurnoverSpecifications.document(document)));
        turnoverRepository.deleteAll(turnovers);
        for (TurnoverEntity turnover : turnovers) {
            fix(turnover.getWarehouse(), turnover.getGood(), turnover.getMoment());
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
        // TODO
        Map<GoodEntity, BigDecimal> firstInflowMap = turnoverRepository
                .findFirstInflow(warehouse, goods, moment).stream()
                .collect(toMap(TurnoverEntity::getGood, TurnoverEntity::getCostPricePerUnit));
        Map<GoodEntity, BigDecimal> defaultMap = goods.stream().distinct()
                .collect(toMap(good -> good, _ -> BigDecimal.ZERO));

        LinkedHashMap<GoodEntity, BigDecimal> result = new LinkedHashMap<>();
        Stream.of(firstInflowMap, defaultMap).forEach(map -> map.forEach(result::putIfAbsent));
        return result;
    }

    private void fix(WarehouseEntity warehouse, GoodEntity good, Instant moment) {
        fixRemind(warehouse, good, moment);
        fixCostPrice(warehouse, good, moment);
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
        TurnoverEntity lastOutflow = turnoverRepository.findOne(Specification
                        .where(TurnoverSpecifications.warehouseAndGoodEquals(warehouse, good))
                        .and(TurnoverSpecifications.momentLessThan(moment))
                        .and(TurnoverSpecifications.isOutflow())
                , TurnoverSorts.byMomentDesc());
        if (lastOutflow == null) return;
        List<TurnoverEntity> outflows = turnoverRepository.findAll(Specification
                        .where(TurnoverSpecifications.warehouseAndGoodEquals(warehouse, good))
                        .and(TurnoverSpecifications.momentGreaterThanEqual(moment))
                        .and(TurnoverSpecifications.isOutflow())
                , TurnoverSorts.byMomentAsc());
        TurnoverEntity lastInflow = lastOutflow.getLastInflow();
        List<TurnoverEntity> inflows;
        int inflowUsedQuantity = 0;
        int inflowIndex = 0;
        if (lastInflow == null) {
            inflows = List.of();
        } else {
            inflows = turnoverRepository.findAll(Specification
                            .where(TurnoverSpecifications.warehouseAndGoodEquals(warehouse, good))
                            .and(TurnoverSpecifications.momentGreaterThanEqual(moment))
                            .and(TurnoverSpecifications.isIncome())
                    , TurnoverSorts.byMomentAsc());
            inflowUsedQuantity = lastInflow.getLastInflowUsedQuantity();
        }
        for (TurnoverEntity outflow : outflows) {
            int remainingOutflowQuantity = -outflow.getQuantity();
            BigDecimal totalCostPrice = BigDecimal.ZERO;
            while (remainingOutflowQuantity > 0 && inflowIndex < inflows.size()) {
                TurnoverEntity inflow = inflows.get(inflowIndex);
                int available = inflow.getQuantity() - inflowUsedQuantity;
                if (available <= 0) {
                    throw new IllegalStateException("Inflow overused: inflowUsedQuantity=" + inflowUsedQuantity
                                                    + " > inflow.getQuantity()=" + inflow.getQuantity()
                                                    + " (inflowId=" + inflow.getId() + ", good=" + inflow.getGood().getName() + ")");
                }
                int used = Math.min(available, remainingOutflowQuantity);
                BigDecimal costPrice = inflow.getCostPricePerUnit().multiply(BigDecimal.valueOf(used));
                totalCostPrice = totalCostPrice.add(costPrice);
                inflowUsedQuantity += used;
                remainingOutflowQuantity -= used;
                if (inflowUsedQuantity == inflow.getQuantity()) {
                    inflowIndex++;
                    inflowUsedQuantity = 0;
                }
            }
            BigDecimal costPerUnit = totalCostPrice.divide(BigDecimal.valueOf(-outflow.getQuantity()), RoundingMode.HALF_UP);
            outflow.setCostPricePerUnit(costPerUnit);
            outflow.setLastInflow(inflows.get(inflowIndex));
            outflow.setLastInflowUsedQuantity(inflowUsedQuantity);
        }
    }


    public static class StockDto {
        private final WarehouseEntity warehouse;
        private final GoodEntity good;
        private final Integer remain;
        private final BigDecimal costPrice;

        public StockDto(WarehouseEntity warehouse, GoodEntity good, Integer remain, BigDecimal costPrice) {
            this.warehouse = warehouse;
            this.good = good;
            this.remain = remain;
            this.costPrice = costPrice;
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

        public BigDecimal getCostPrice() {
            return costPrice;
        }
    }
}

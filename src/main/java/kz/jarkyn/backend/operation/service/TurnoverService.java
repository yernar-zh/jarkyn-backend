
package kz.jarkyn.backend.operation.service;


import kz.jarkyn.backend.warehouse.model.WarehouseEntity;
import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.warehouse.model.GoodEntity;
import kz.jarkyn.backend.operation.mode.TurnoverEntity;
import kz.jarkyn.backend.operation.repository.TurnoverRepository;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
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
    public List<TurnoverEntity> findByDocument(DocumentEntity document) {
        return turnoverRepository.findByDocument(document);
    }

    @Transactional(readOnly = true)
    public List<Pair<Pair<WarehouseEntity, GoodEntity>, Integer>> findRemindAtMoment(
            List<Pair<WarehouseEntity, GoodEntity>> goodsPair, Instant moment
    ) {
        Map<Pair<WarehouseEntity, GoodEntity>, TurnoverEntity> map = goodsPair.stream()
                .collect(groupingBy(Pair::getFirst, mapping(Pair::getSecond, Collectors.toList())))
                .entrySet().stream()
                .map(entry -> turnoverRepository.findLastByGoodAndMoment(entry.getKey(), entry.getValue(), moment))
                .flatMap(Collection::stream)
                .collect(toMap(turnover -> Pair.of(turnover.getWarehouse(), turnover.getGood()), identity(),
                        (t1, t2) -> t1.getLastModifiedAt().isAfter(t2.getLastModifiedAt()) ? t1 : t2));
        return goodsPair.stream().distinct().map(pair -> Pair.of(pair,
                        Optional.ofNullable(map.get(pair))
                                .map(turnover -> turnover.getRemain() + turnover.getQuantity())
                                .orElse(0)))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Pair<WarehouseEntity, Pair<Integer, BigDecimal>>> findRemindAndCostAtMoment(
            List<WarehouseEntity> warehouses, GoodEntity good, Instant moment
    ) {
        return findRemindAtMoment(warehouses.stream().map(warehouse -> Pair.of(warehouse, good)).toList(), moment)
                .stream().map(pair -> {
                    // TODO
                    BigDecimal costPrisePerUnit = turnoverRepository.findFirstInflowByGoodAtMoment(
                                    pair.getFirst().getFirst(), good, moment)
                            .map(TurnoverEntity::getCostPricePerUnit).orElse(BigDecimal.ZERO);
                    return Pair.of(pair.getFirst().getFirst(), Pair.of(pair.getSecond(), costPrisePerUnit));
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
        fix(turnover.getWarehouse(), turnover.getGood(), turnover.getMoment());
    }

    @Transactional
    public void delete(DocumentEntity document) {
        List<TurnoverEntity> turnovers = turnoverRepository.findByDocument(document);
        turnoverRepository.deleteAll(turnovers);
        for (TurnoverEntity turnover : turnovers) {
            fix(turnover.getWarehouse(), turnover.getGood(), turnover.getMoment());
        }
    }

    @Transactional
    protected void fix(WarehouseEntity warehouse, GoodEntity good, Instant moment) {
        // Fix remind
        List<TurnoverEntity> turnovers = turnoverRepository
                .findByWarehouseAndGoodAndMomentGreaterThanEqual(warehouse, good, moment).stream()
                .sorted(Comparator.comparing(TurnoverEntity::getMoment)
                        .thenComparing(turnover -> turnover.getDocument().getLastModifiedAt()))
                .toList();
        Integer remain = findRemindAtMoment(List.of(Pair.of(warehouse, good)), moment).getFirst().getSecond();
        for (TurnoverEntity turnover : turnovers) {
            turnover.setRemain(remain);
            remain += turnover.getQuantity();
        }

        // Fix costPrice
        TurnoverEntity lastOutflow = turnoverRepository.findLastOutflowByGoodAndMoment(warehouse, good, moment).orElse(null);
        if (lastOutflow == null) return;
        List<TurnoverEntity> outflows = turnoverRepository.findByWarehouseAndGoodAndMomentGreaterThanEqual(
                        lastOutflow.getWarehouse(), lastOutflow.getGood(), lastOutflow.getMoment())
                .stream().filter(turnoverEntity -> turnoverEntity.getQuantity() < 0)
                .sorted(Comparator.comparing(TurnoverEntity::getMoment).thenComparing(turnover -> turnover.getDocument().getLastModifiedAt()))
                .toList();
        TurnoverEntity lastInflow = lastOutflow.getLastInflow();
        List<TurnoverEntity> inflows;
        int inflowUsedQuantity = 0;
        int inflowIndex = 0;
        if (lastInflow == null) {
            inflows = List.of();
        } else {
            inflows = turnoverRepository.findByWarehouseAndGoodAndMomentGreaterThanEqual(
                            lastOutflow.getWarehouse(), lastOutflow.getGood(), lastOutflow.getMoment())
                    .stream().filter(turnoverEntity -> turnoverEntity.getQuantity() > 0)
                    .sorted(Comparator.comparing(TurnoverEntity::getMoment).thenComparing(turnover -> turnover.getDocument().getLastModifiedAt()))
                    .toList();
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

    }
}

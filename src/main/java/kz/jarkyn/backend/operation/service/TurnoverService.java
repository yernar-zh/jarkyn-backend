
package kz.jarkyn.backend.operation.service;


import kz.jarkyn.backend.warehouse.model.WarehouseEntity;
import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.warehouse.model.GoodEntity;
import kz.jarkyn.backend.operation.mode.TurnoverEntity;
import kz.jarkyn.backend.operation.repository.TurnoverRepository;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

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

    @Transactional
    public void create(DocumentEntity document, GoodEntity good, Integer quantity) {
        TurnoverEntity turnover = new TurnoverEntity();
        turnover.setDocument(document);
        turnover.setGood(good);
        turnover.setQuantity(quantity);
        turnover.setWarehouse(document.getWarehouse());
        turnover.setMoment(document.getMoment());
        turnover.setRemain(0);
        turnoverRepository.save(turnover);
        fixBalances(turnover.getWarehouse(), turnover.getGood(), turnover.getMoment());
    }

    @Transactional
    public void delete(DocumentEntity document) {
        List<TurnoverEntity> turnovers = turnoverRepository.findByDocument(document);
        turnoverRepository.deleteAll(turnovers);
        for (TurnoverEntity turnover : turnovers) {
            fixBalances(turnover.getWarehouse(), turnover.getGood(), turnover.getMoment());
        }
    }

    @Transactional
    protected void fixBalances(WarehouseEntity warehouse, GoodEntity good, Instant moment) {
        List<TurnoverEntity> turnovers = turnoverRepository
                .findByWarehouseAndGoodAndMomentGreaterThanEqual(warehouse, good, moment).stream()
                .sorted(Comparator.comparing(TurnoverEntity::getMoment)
                        .thenComparing(turnoverEntity -> turnoverEntity.getDocument().getLastModifiedAt()))
                .toList();
        Integer remain = findRemindAtMoment(List.of(Pair.of(warehouse, good)), moment).getFirst().getSecond();
        for (TurnoverEntity turnover : turnovers) {
            turnover.setRemain(remain);
            remain += turnover.getQuantity();
        }
    }
}


package kz.jarkyn.backend.stock.service;


import kz.jarkyn.backend.counterparty.model.WarehouseEntity;
import kz.jarkyn.backend.counterparty.repository.WarehouseRepository;
import kz.jarkyn.backend.counterparty.service.WarehouseService;
import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.good.model.GoodEntity;
import kz.jarkyn.backend.stock.mapper.TurnoverMapper;
import kz.jarkyn.backend.stock.mode.TurnoverEntity;
import kz.jarkyn.backend.stock.mode.dto.StockResponse;
import kz.jarkyn.backend.stock.repository.TurnoverRepository;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TurnoverService {
    private final TurnoverRepository turnoverRepository;
    private final TurnoverMapper turnoverMapper;
    private final WarehouseRepository warehouseRepository;

    public TurnoverService(
            TurnoverRepository turnoverRepository,
            TurnoverMapper turnoverMapper
            , WarehouseRepository warehouseRepository) {
        this.turnoverRepository = turnoverRepository;
        this.turnoverMapper = turnoverMapper;
        this.warehouseRepository = warehouseRepository;
    }

    @Transactional
    public void create(DocumentEntity document, GoodEntity good, Integer quantity, BigDecimal costPrice) {
        TurnoverEntity turnover = new TurnoverEntity();
        turnover.setDocument(document);
        turnover.setGood(good);
        turnover.setQuantity(quantity);
        turnover.setCostPrice(costPrice);
        turnover.setWarehouse(document.getWarehouse());
        turnover.setMoment(document.getMoment());
        StockResponse stock = findStock(turnover.getDocument().getWarehouse(), List.of(turnover.getGood()))
                .stream().findFirst().orElseThrow();
        turnover.setRemain(stock.getRemain());
        if (quantity < 0) {
            turnover.setCostPrice(stock.getCostPrice());
        }
        turnoverRepository.save(turnover);
    }

    @Transactional(readOnly = true)
    public List<TurnoverEntity> findByDocument(DocumentEntity document) {
        return turnoverRepository.findByDocument(document);
    }

    @Transactional(readOnly = true)
    public List<StockResponse> findStock(GoodEntity good) {
        return findStock(null, List.of(good));
    }

    @Transactional(readOnly = true)
    public List<StockResponse> findStock(WarehouseEntity warehouseFilter, List<GoodEntity> list) {
        List<WarehouseEntity> warehouses = warehouseRepository.findAll().stream()
                .filter(warehouse -> warehouseFilter == null || warehouse.equals(warehouseFilter)).toList();
        Map<Pair<WarehouseEntity, GoodEntity>, TurnoverEntity> map = turnoverRepository.findLastByGood(list).stream()
                .collect(Collectors.toMap(turnover ->
                        Pair.of(turnover.getWarehouse(), turnover.getGood()), Function.identity()));
        return warehouses.stream().flatMap(warehouse -> list.stream().map(good -> {
            TurnoverEntity lastTurnover = map.get(Pair.of(warehouse, good));
            if (lastTurnover == null) {
                return turnoverMapper.toStockResponse(warehouse, good, 0, BigDecimal.ZERO);
            }
            return turnoverMapper.toStockResponse(warehouse, good,
                    lastTurnover.getRemain() + lastTurnover.getQuantity(), lastTurnover.getCostPrice());
        })).toList();
    }
}

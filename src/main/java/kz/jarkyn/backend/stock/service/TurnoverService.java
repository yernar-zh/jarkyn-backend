
package kz.jarkyn.backend.stock.service;


import kz.jarkyn.backend.counterparty.model.WarehouseEntity;
import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.good.model.GoodEntity;
import kz.jarkyn.backend.stock.mapper.TurnoverMapper;
import kz.jarkyn.backend.stock.mode.TurnoverEntity;
import kz.jarkyn.backend.stock.mode.dto.TurnoverRequest;
import kz.jarkyn.backend.stock.mode.dto.TurnoverResponse;
import kz.jarkyn.backend.stock.repository.TurnoverRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class TurnoverService {
    private final TurnoverRepository turnoverRepository;
    private final TurnoverMapper turnoverMapper;

    public TurnoverService(
            TurnoverRepository turnoverRepository,
            TurnoverMapper turnoverMapper) {
        this.turnoverRepository = turnoverRepository;
        this.turnoverMapper = turnoverMapper;
    }

    @Transactional
    public void create(TurnoverRequest request) {
        TurnoverEntity turnover = turnoverMapper.toEntity(request);
        turnover.setWarehouse(turnover.getDocument().getWarehouse());
        turnover.setMoment(turnover.getDocument().getMoment());
        Optional<TurnoverResponse> lastTurnover = findLast(
                turnover.getDocument().getWarehouse(), List.of(turnover.getGood()))
                .stream().findFirst();
        turnover.setRemain(lastTurnover.map(o -> o.getRemain() + o.getQuantity()).orElse(0));
        if (turnover.getCostPrice() == null) {
            turnover.setCostPrice(lastTurnover.map(TurnoverResponse::getCostPrice).orElse(BigDecimal.ZERO));
        }
        turnoverRepository.save(turnover);
    }

    @Transactional(readOnly = true)
    public List<TurnoverResponse> findByDocument(DocumentEntity document) {
        return turnoverMapper.toResponse(turnoverRepository.findByDocument(document));
    }

    @Transactional(readOnly = true)
    public List<TurnoverResponse> findLast(GoodEntity good) {
        return findLast(null, List.of(good));
    }

    @Transactional(readOnly = true)
    public List<TurnoverResponse> findLast(WarehouseEntity warehouse, List<GoodEntity> list) {
        return turnoverRepository.findLastByGood(list).stream()
                .filter(turnover -> warehouse == null || turnover.getWarehouse().equals(warehouse))
                .map(turnoverMapper::toResponse).toList();
    }
}

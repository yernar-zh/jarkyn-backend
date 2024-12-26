
package kz.jarkyn.backend.stock.service;


import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.good.model.GoodEntity;
import kz.jarkyn.backend.good.repository.GoodRepository;
import kz.jarkyn.backend.good.service.GoodService;
import kz.jarkyn.backend.stock.mapper.TurnoverMapper;
import kz.jarkyn.backend.stock.mode.TurnoverEntity;
import kz.jarkyn.backend.stock.mode.dto.TurnoverRequest;
import kz.jarkyn.backend.stock.mode.dto.TurnoverResponse;
import kz.jarkyn.backend.stock.repository.TurnoverRepository;
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

    public TurnoverService(
            TurnoverRepository turnoverRepository,
            TurnoverMapper turnoverMapper,
            GoodService goodService) {
        this.turnoverRepository = turnoverRepository;
        this.turnoverMapper = turnoverMapper;
    }

    @Transactional
    public void create(TurnoverRequest request) {
        TurnoverEntity turnover = turnoverMapper.toEntity(request);
        turnover.setMoment(turnover.getDocument().getMoment());
        TurnoverResponse lastTurnover = findLast(turnover.getGood());
        turnover.setRemain(lastTurnover.getRemain() + lastTurnover.getQuantity());
        if (turnover.getCostPrice() == null) {
            turnover.setCostPrice(lastTurnover.getCostPrice());
        }
        turnoverRepository.save(turnover);
    }

    @Transactional(readOnly = true)
    public List<TurnoverResponse> findByDocument(DocumentEntity document) {
        return turnoverMapper.toResponse(turnoverRepository.findByDocument(document));
    }

    @Transactional(readOnly = true)
    public TurnoverResponse findLast(GoodEntity good) {
        return findLast(List.of(good)).stream().findFirst().orElseThrow();
    }

    @Transactional(readOnly = true)
    public List<TurnoverResponse> findLast(List<GoodEntity> list) {
        Map<GoodEntity, TurnoverEntity> turnovers = turnoverRepository.findLastByGood(list).stream()
                .collect(Collectors.toMap(TurnoverEntity::getGood, Function.identity()));
        return list.stream().map(good -> {
            TurnoverEntity turnover = turnovers.get(good);
            if (turnover != null) return turnoverMapper.toResponse(turnover);
            else return turnoverMapper.toResponse(good, 0, 0, BigDecimal.ZERO);
        }).toList();
    }
}


package kz.jarkyn.backend.stock.service;


import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.good.model.GoodEntity;
import kz.jarkyn.backend.stock.mode.TurnoverEntity;
import kz.jarkyn.backend.stock.repository.TurnoverRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TurnoverService {
    private final TurnoverRepository turnoverRepository;

    public TurnoverService(TurnoverRepository turnoverRepository) {
        this.turnoverRepository = turnoverRepository;
    }

    @Transactional
    public void create() {
    }

    @Transactional(readOnly = true)
    public Map<GoodEntity, Integer> findRemain(List<GoodEntity> list) {
        Map<GoodEntity, Integer> remains = turnoverRepository.findRemain(list)
                .stream().collect(Collectors.toMap(
                        x -> x.get("good", GoodEntity.class),
                        x -> x.get("remain", Integer.class)));
        return list.stream().distinct().collect(Collectors.toMap(
                good -> good,
                good -> remains.getOrDefault(good, 0)));
    }

    @Transactional(readOnly = true)
    public List<TurnoverEntity> findByDocument(DocumentEntity document) {
        return turnoverRepository.findByDocument(document);
    }
}

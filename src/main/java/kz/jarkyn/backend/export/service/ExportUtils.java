package kz.jarkyn.backend.export.service;

import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.document.core.model.dto.ItemRequest;
import kz.jarkyn.backend.party.model.dto.CounterpartyResponse;
import kz.jarkyn.backend.party.service.CounterpartyService;
import kz.jarkyn.backend.good.model.dto.GoodResponse;
import kz.jarkyn.backend.good.service.GoodService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class ExportUtils {

    private final GoodService goodService;
    private final CounterpartyService counterpartyService;

    public ExportUtils(
            GoodService goodService,
            CounterpartyService counterpartyService
    ) {
        this.goodService = goodService;
        this.counterpartyService = counterpartyService;
    }

    public double calcItemsSum(List<ItemRequest> items) {
        List<ItemRequest> safeItems = (items == null) ? Collections.emptyList() : items;
        return safeItems.stream()
                .filter(Objects::nonNull)
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add).doubleValue();
    }

    public GoodResponse findGood(IdDto id) {
        return goodService.findApiById(id.getId());
    }

    public CounterpartyResponse findCounterparty(IdDto id) {
        return counterpartyService.findApiById(id.getId());
    }
}

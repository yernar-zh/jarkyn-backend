
package kz.jarkyn.backend.document.sale.service;


import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.document.core.model.dto.ItemResponse;
import kz.jarkyn.backend.document.core.service.ItemService;
import kz.jarkyn.backend.document.payment.model.PaymentInForSaleEntity;
import kz.jarkyn.backend.document.payment.repository.PaymentInForSaleRepository;
import kz.jarkyn.backend.document.sale.model.SaleEntity;
import kz.jarkyn.backend.document.sale.model.dto.SaleDetailResponse;
import kz.jarkyn.backend.document.sale.model.dto.SaleRequest;
import kz.jarkyn.backend.document.sale.repository.SaleRepository;
import kz.jarkyn.backend.document.sale.mapper.SaleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SaleService {
    private final SaleRepository saleRepository;
    private final SaleMapper saleMapper;
    private final ItemService itemService;
    private final PaymentInForSaleRepository paymentInForSaleRepository;

    public SaleService(
            SaleRepository saleRepository,
            SaleMapper saleMapper,
            ItemService itemService,
            PaymentInForSaleRepository paymentInForSaleRepository
    ) {
        this.saleRepository = saleRepository;
        this.saleMapper = saleMapper;
        this.itemService = itemService;
        this.paymentInForSaleRepository = paymentInForSaleRepository;
    }

    @Transactional(readOnly = true)
    public SaleDetailResponse findApiById(UUID id) {
        SaleEntity sale = saleRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        List<ItemResponse> items = itemService.findApiByDocument(sale);
        List<PaymentInForSaleEntity> saleRepositoryBySale = paymentInForSaleRepository.findBySale(sale);
        return saleMapper.toDetailResponse(sale, items, saleRepositoryBySale);
    }

    @Transactional
    public UUID createApi(SaleRequest request) {
        SaleEntity sale = saleRepository.save(saleMapper.toEntity(request));
        itemService.editApi(sale, request.getItems());
        return sale.getId();
    }
}

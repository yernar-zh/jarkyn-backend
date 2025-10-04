package kz.jarkyn.backend.document.core.service;


import kz.jarkyn.backend.document.bind.model.BindDocumentEntity;
import kz.jarkyn.backend.document.bind.repository.BindDocumentRepository;
import kz.jarkyn.backend.document.bind.specifications.BindDocumentSpecifications;
import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.document.core.model.DocumentSearchEntity;
import kz.jarkyn.backend.document.core.repository.DocumentSearchRepository;
import kz.jarkyn.backend.document.supply.model.SupplyEntity;
import kz.jarkyn.backend.document.supply.repository.SupplyRepository;
import kz.jarkyn.backend.global.service.CoverageService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;


@Service
public class DocumentSearchService {
    private final SupplyRepository supplyRepository;
    private final DocumentSearchRepository documentSearchRepository;
    private final BindDocumentRepository bindDocumentRepository;
    private final DocumentTypeService documentTypeService;
    private final CoverageService coverageService;

    public DocumentSearchService(SupplyRepository supplyRepository,
                                 DocumentSearchRepository documentSearchRepository,
                                 BindDocumentRepository bindDocumentRepository,
                                 DocumentTypeService documentTypeService,
                                 CoverageService coverageService) {
        this.supplyRepository = supplyRepository;
        this.documentSearchRepository = documentSearchRepository;
        this.bindDocumentRepository = bindDocumentRepository;
        this.documentTypeService = documentTypeService;
        this.coverageService = coverageService;
    }

    @RabbitListener(queues = "${rabbitmq.queue.supply-search}")
    public void supplySearch(UUID supplyId) {
        SupplyEntity supply = supplyRepository.findById(supplyId).orElseThrow();
        DocumentSearchEntity documentSearch = fillBase(supply);
        BigDecimal paidAmount = bindDocumentRepository.findAll(Specification.allOf(
                        BindDocumentSpecifications.relatedDocument(supply),
                        BindDocumentSpecifications.primaryDocumentType(documentTypeService.findPaymentOut())))
                .stream().map(BindDocumentEntity::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        fillPaid(documentSearch, paidAmount);
        documentSearchRepository.save(documentSearch);
    }

    private DocumentSearchEntity fillBase(DocumentEntity document) {
        DocumentSearchEntity documentSearch = documentSearchRepository.findById(document.getId()).orElseGet(() -> {
            DocumentSearchEntity newDocumentSearch = new DocumentSearchEntity();
            newDocumentSearch.setId(document.getId());
            return newDocumentSearch;
        });

        documentSearch.setId(document.getId());
        documentSearch.setType(document.getType());
        documentSearch.setName(document.getName());
        documentSearch.setOrganization(document.getOrganization());
        documentSearch.setCounterparty(document.getCounterparty());
        documentSearch.setWarehouse(document.getWarehouse());
        documentSearch.setMoment(document.getMoment());
        documentSearch.setCurrency(document.getCurrency());
        documentSearch.setExchangeRate(document.getExchangeRate());
        documentSearch.setAmount(document.getAmount());
        documentSearch.setDeleted(document.getDeleted());
        documentSearch.setCommited(document.getCommited());
        documentSearch.setComment(document.getComment());
        return documentSearch;
    }

    private void fillPaid(DocumentSearchEntity documentSearch, BigDecimal paidAmount) {
        documentSearch.setPaidAmount(paidAmount);
        documentSearch.setNotPaidAmount(documentSearch.getAmount().subtract(paidAmount));
        if (paidAmount.compareTo(BigDecimal.ZERO) == 0) {
            documentSearch.setPaidCoverage(coverageService.findNone());
        } else if (paidAmount.compareTo(documentSearch.getAmount()) >= 0) {
            documentSearch.setPaidCoverage(coverageService.findFull());
        } else {
            documentSearch.setPaidCoverage(coverageService.findPartial());
        }

    }

}
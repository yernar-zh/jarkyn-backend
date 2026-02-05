package kz.jarkyn.backend.document.core.service;


import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.transaction.Transactional;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.core.search.CriteriaAttributes;
import kz.jarkyn.backend.core.search.CriteriaFilters;
import kz.jarkyn.backend.core.search.Search;
import kz.jarkyn.backend.core.search.SearchFactory;
import kz.jarkyn.backend.core.utils.PrefixSearch;
import kz.jarkyn.backend.document.bind.model.BindDocumentEntity;
import kz.jarkyn.backend.document.bind.repository.BindDocumentRepository;
import kz.jarkyn.backend.document.bind.specifications.BindDocumentSpecifications;
import kz.jarkyn.backend.document.core.model.*;
import kz.jarkyn.backend.document.core.repository.DocumentRepository;
import kz.jarkyn.backend.document.core.repository.DocumentSearchRepository;
import kz.jarkyn.backend.document.core.repository.ItemRepository;
import kz.jarkyn.backend.document.expense.model.ExpenseEntity;
import kz.jarkyn.backend.document.expense.repository.ExpenseRepository;
import kz.jarkyn.backend.document.payment.model.PaymentOutEntity;
import kz.jarkyn.backend.document.payment.repository.PaymentOutRepository;
import kz.jarkyn.backend.document.sale.model.SaleEntity;
import kz.jarkyn.backend.document.sale.repository.SaleRepository;
import kz.jarkyn.backend.document.supply.model.SupplyEntity;
import kz.jarkyn.backend.document.supply.repository.SupplyRepository;
import kz.jarkyn.backend.global.service.CoverageService;
import kz.jarkyn.backend.good.model.GoodEntity_;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;


@Service
public class DocumentSearchService {
    private static final Logger log = LoggerFactory.getLogger(DocumentSearchService.class);

    private final SupplyRepository supplyRepository;
    private final SaleRepository saleRepository;
    private final DocumentSearchRepository documentSearchRepository;
    private final BindDocumentRepository bindDocumentRepository;
    private final DocumentTypeService documentTypeService;
    private final CoverageService coverageService;
    private final ItemRepository itemRepository;
    private final SearchFactory searchFactory;
    private final DocumentRepository documentRepository;
    private final PaymentOutRepository paymentOutRepository;
    private final ExpenseRepository expenseRepository;

    public DocumentSearchService(SupplyRepository supplyRepository,
                                 SaleRepository saleRepository,
                                 DocumentSearchRepository documentSearchRepository,
                                 BindDocumentRepository bindDocumentRepository,
                                 DocumentTypeService documentTypeService,
                                 CoverageService coverageService,
                                 ItemRepository itemRepository,
                                 SearchFactory searchFactory,
                                 DocumentRepository documentRepository,
                                 PaymentOutRepository paymentOutRepository,
                                 ExpenseRepository expenseRepository) {
        this.supplyRepository = supplyRepository;
        this.saleRepository = saleRepository;
        this.documentSearchRepository = documentSearchRepository;
        this.bindDocumentRepository = bindDocumentRepository;
        this.documentTypeService = documentTypeService;
        this.coverageService = coverageService;
        this.itemRepository = itemRepository;
        this.searchFactory = searchFactory;
        this.documentRepository = documentRepository;
        this.paymentOutRepository = paymentOutRepository;
        this.expenseRepository = expenseRepository;
    }

    public <T> PageResponse<T> findApiByFilter(
            Class<T> responseClass, QueryParams queryParams, DocumentTypeEntity documentType) {
        CriteriaAttributes.Builder<DocumentSearchEntity> attributesBuilder = CriteriaAttributes
                .<DocumentSearchEntity>builder()
                .add("id", (root) -> root.get(DocumentSearchEntity_.id))
                .addEnumType("type", (root) -> root.get(DocumentSearchEntity_.type))
                .add("name", (root) -> root.get(DocumentSearchEntity_.name))
                .addReference("organization", (root) -> root.get(DocumentSearchEntity_.organization))
                .addReference("warehouse", (root) -> root.join(DocumentSearchEntity_.warehouse, JoinType.LEFT))
                .addReference("account", (root) -> root.join(DocumentSearchEntity_.account, JoinType.LEFT))
                .addReference("counterparty", (root) -> root.join(DocumentSearchEntity_.counterparty, JoinType.LEFT))
                .add("moment", (root) -> root.get(DocumentSearchEntity_.moment))
                .addEnumType("currency", (root) -> root.get(DocumentSearchEntity_.currency))
                .add("exchangeRate", (root) -> root.get(DocumentSearchEntity_.exchangeRate))
                .add("amount", (root) -> root.get(DocumentSearchEntity_.amount))
                .add("deleted", (root) -> root.get(DocumentSearchEntity_.deleted))
                .add("commited", (root) -> root.get(DocumentSearchEntity_.commited))
                .add("comment", (root) -> root.get(DocumentSearchEntity_.comment))
                .add("search", (root) -> root.get(DocumentSearchEntity_.search))

                .add("paidAmount", (root) -> root.get(DocumentSearchEntity_.paidAmount))
                .add("notPaidAmount", (root) -> root.get(DocumentSearchEntity_.notPaidAmount))
                .addEnumType("paidCoverage", (root) -> root.join(DocumentSearchEntity_.paidCoverage, JoinType.LEFT))

                .add("attachedAmount", (root) -> root.get(DocumentSearchEntity_.attachedAmount))
                .add("notAttachedAmount", (root) -> root.get(DocumentSearchEntity_.notAttachedAmount))
                .add("attachedCoverage", (root) -> root.join(DocumentSearchEntity_.attachedCoverage, JoinType.LEFT))

                .add("overheadCostAmount", (root) -> root.get(DocumentSearchEntity_.overheadCostAmount))
                .add("receiptNumber", (root) -> root.get(DocumentSearchEntity_.receiptNumber))
                .add("discount", (root) -> root.get(DocumentSearchEntity_.discount))
                .add("surcharge", (root) -> root.get(DocumentSearchEntity_.surcharge));

        List<UUID> itemsGoodId = queryParams.getFilters()
                .stream().filter(filter -> filter.getName().equals("items.good.id"))
                .filter(filter -> filter.getType().equals(QueryParams.Filter.Type.EQUAL_TO))
                .map(QueryParams.Filter::getValues)
                .findFirst().stream().flatMap(Collection::stream)
                .map(UUID::fromString).toList();

        CriteriaFilters.Builder<DocumentSearchEntity> filterBuilder = CriteriaFilters.builder();
        if (!itemsGoodId.isEmpty()) {
            filterBuilder.add((root, query, cb, map) -> {
                Subquery<UUID> subQuery = query.subquery(UUID.class);
                Root<ItemEntity> itemRoot = subQuery.from(ItemEntity.class);
                subQuery.select(itemRoot.get(ItemEntity_.id));
                subQuery.where(itemRoot.get(ItemEntity_.good).get(GoodEntity_.id).in(itemsGoodId));
                return cb.exists(subQuery);
            });
        }
        if (documentType != null) {
            filterBuilder.add((root, query, cb, map) ->
                    cb.equal(root.get(DocumentSearchEntity_.type), documentType));
        }

        Search<T> search = searchFactory.createCriteriaSearch(
                responseClass, List.of("search"), QueryParams.Sort.MOMENT_DESC,
                DocumentSearchEntity.class, attributesBuilder.build(), filterBuilder.build());
        return search.getResult(queryParams);
    }

    @RabbitListener(queues = "${rabbitmq.queue.document-search}")
    @Transactional()
    public void documentSearch(UUID documentId) {
        Optional<DocumentEntity> documentOpt = documentRepository.findById(documentId);
        if (documentOpt.isEmpty()) {
            log.error("Document with id {} not found", documentId);
            return;
        }
        DocumentEntity document = documentOpt.get();
        DocumentSearchEntity documentSearch = fillBase(document);

        if (documentTypeService.isSupply(document.getType())) {
            fillSupply(documentSearch, supplyRepository.findById(documentId).orElseThrow());
        }
        if (documentTypeService.isSale(document.getType())) {
            fillSale(documentSearch, saleRepository.findById(documentId).orElseThrow());
        }
        if (documentTypeService.isPaymentOut(document.getType())) {
            fillPaymentOut(documentSearch, paymentOutRepository.findById(documentId).orElseThrow());
        }
        if (documentTypeService.isExpense(document.getType())) {
            fillExpense(documentSearch, expenseRepository.findById(documentId).orElseThrow());
        }


        documentSearchRepository.save(documentSearch);
    }

    private void fillSupply(DocumentSearchEntity documentSearch, SupplyEntity supply) {
        BigDecimal paidAmount = bindDocumentRepository.findAll(Specification.allOf(
                        BindDocumentSpecifications.relatedDocument(supply),
                        BindDocumentSpecifications.primaryDocumentType(documentTypeService.findPaymentOut())))
                .stream().map(BindDocumentEntity::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        fillPaid(documentSearch, paidAmount);

        BigDecimal overheadCostAmount = bindDocumentRepository.findAll(Specification.allOf(
                        BindDocumentSpecifications.relatedDocument(supply),
                        BindDocumentSpecifications.primaryDocumentType(documentTypeService.findExpense())))
                .stream()
                .map(bindDocumentEntity -> bindDocumentEntity.getAmount().multiply(bindDocumentEntity.getPrimaryDocument().getExchangeRate()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        documentSearch.setOverheadCostAmount(overheadCostAmount);

        fillSearch(documentSearch, supply.getName(), supply.getComment(), supply.getCounterparty().getName());
        fillDiscount(documentSearch, supply);
    }

    private void fillSale(DocumentSearchEntity documentSearch, SaleEntity sale) {
        BigDecimal paidAmount = bindDocumentRepository.findAll(Specification.allOf(
                        BindDocumentSpecifications.relatedDocument(sale),
                        BindDocumentSpecifications.primaryDocumentType(documentTypeService.findPaymentIn())))
                .stream().map(BindDocumentEntity::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        fillPaid(documentSearch, paidAmount);

        BigDecimal overheadCostAmount = bindDocumentRepository.findAll(Specification.allOf(
                        BindDocumentSpecifications.relatedDocument(sale),
                        BindDocumentSpecifications.primaryDocumentType(documentTypeService.findExpense())))
                .stream()
                .map(bindDocumentEntity -> bindDocumentEntity.getAmount().multiply(bindDocumentEntity.getPrimaryDocument().getExchangeRate()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        documentSearch.setOverheadCostAmount(overheadCostAmount);

        fillSearch(documentSearch, sale.getName(), sale.getComment(), sale.getCounterparty().getName());
        fillDiscount(documentSearch, sale);
    }

    private void fillPaymentOut(DocumentSearchEntity documentSearch, PaymentOutEntity paymentOut) {
        fillAttached(documentSearch, paymentOut.getAmount());
        fillSearch(documentSearch, paymentOut.getName(), paymentOut.getComment(),
                paymentOut.getCounterparty().getName(), paymentOut.getReceiptNumber());
    }

    private void fillExpense(DocumentSearchEntity documentSearch, ExpenseEntity expense) {
        fillAttached(documentSearch, expense.getAmount());
        fillSearch(documentSearch, expense.getName(), expense.getComment(), expense.getCounterparty().getName());
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
        documentSearch.setAccount(document.getAccount());
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

    private void fillAttached(DocumentSearchEntity documentSearch, BigDecimal attached) {
        documentSearch.setAttachedAmount(attached);
        documentSearch.setNotAttachedAmount(documentSearch.getAmount().subtract(attached));
        if (attached.compareTo(BigDecimal.ZERO) == 0) {
            documentSearch.setAttachedCoverage(coverageService.findNone());
        } else if (attached.compareTo(documentSearch.getAmount()) >= 0) {
            documentSearch.setAttachedCoverage(coverageService.findFull());
        } else {
            documentSearch.setAttachedCoverage(coverageService.findPartial());
        }
    }

    private void fillSearch(DocumentSearchEntity documentSearch, String... texts) {
        String searchText = Arrays.stream(texts)
                .map(PrefixSearch::split)
                .flatMap(Collection::stream)
                .distinct()
                .collect(java.util.stream.Collectors.joining(" "));
        documentSearch.setSearch(searchText);
    }


    private void fillDiscount(DocumentSearchEntity documentSearch, DocumentEntity document) {
        BigDecimal itemAmount = itemRepository.findByDocument(document).stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal difference = document.getAmount().subtract(itemAmount);
        if (difference.compareTo(BigDecimal.ZERO) < 0) {
            documentSearch.setDiscount(difference.abs());
            documentSearch.setSurcharge(BigDecimal.ZERO);
        } else {
            documentSearch.setDiscount(BigDecimal.ZERO);
            documentSearch.setSurcharge(difference);
        }
    }

}


package kz.jarkyn.backend.document.core.service;


import kz.jarkyn.backend.core.search.CriteriaAttributes;
import kz.jarkyn.backend.core.sorts.EntitySorts;
import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.document.core.model.DocumentEntity_;
import kz.jarkyn.backend.document.core.model.DocumentTypeEntity;
import kz.jarkyn.backend.document.core.model.dto.DocumentRequest;
import kz.jarkyn.backend.document.core.model.dto.ItemRequest;
import kz.jarkyn.backend.document.core.repository.DocumentRepository;
import kz.jarkyn.backend.document.core.specifications.DocumentSpecifications;
import kz.jarkyn.backend.export.service.ExportCurrency;
import kz.jarkyn.backend.export.service.ExportService;
import kz.jarkyn.backend.global.service.CurrencyService;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final ExportService exportService;
    private final CurrencyService currencyService;

    public DocumentService(
            DocumentRepository documentRepository,
            ExportService exportService,
            CurrencyService currencyService) {
        this.documentRepository = documentRepository;
        this.exportService = exportService;
        this.currencyService = currencyService;
    }

    @Transactional(readOnly = true)
    public String findNextName(Class<? extends DocumentEntity> type) {
        throw new RuntimeException();
    }


    @Transactional(readOnly = true)
    public String findNextName(DocumentTypeEntity type) {
        String lastName = documentRepository.findOne(DocumentSpecifications.type(type), EntitySorts.byNameDesc())
                .map(DocumentEntity::getName)
                .orElse("000000");
        Matcher matcher = Pattern.compile("(\\d{6})").matcher(lastName);
        if (!matcher.find()) {
            throw new RuntimeException("unsupported name : " + lastName);
        }
        int lastNameId = Integer.parseInt(matcher.group(1));
        return String.format("%06d", lastNameId + 1);
    }

    public void validateName(DocumentEntity document) {
        if (document.getName() == null) {
            throw new RuntimeException("document name is null");
        }
        String name = document.getName();
        Matcher matcher = Pattern.compile("(\\d{6})").matcher(name);
        if (!matcher.find()) {
            throw new RuntimeException("unsupported name : " + name);
        }
    }

    @Transactional(readOnly = true)
    public Resource generateXlsx(ExportService.Template template, DocumentRequest request, List<ItemRequest> items) {
        String currencyCode = currencyService.findApiById(request.getCurrency().getId()).getCode();
        boolean fraction = existFraction(request.getAmount(), items);
        return exportService.generateXlsx(
                template,
                Map.of("document", request),
                ExportCurrency.of(fraction, currencyCode));
    }

    @Transactional(readOnly = true)
    public Resource generatePdf(ExportService.Template template, DocumentRequest request, List<ItemRequest> items) {
        String currencyCode = currencyService.findApiById(request.getCurrency().getId()).getCode();
        boolean fraction = existFraction(request.getAmount(), items);
        return exportService.generatePdf(
                template,
                Map.of("document", request),
                ExportCurrency.of(fraction, currencyCode));
    }

    private boolean existFraction(BigDecimal amount, List<ItemRequest> items) {
        Stream<BigDecimal> itemPrices = (items == null) ? Stream.empty() : items.stream().map(ItemRequest::getPrice);
        return Stream.concat(itemPrices, Stream.of(amount))
                .filter(Objects::nonNull)
                .map(BigDecimal::stripTrailingZeros)
                .anyMatch(value -> value.scale() > 0);
    }

    @Deprecated
    public <T extends DocumentEntity> CriteriaAttributes.Builder<T> generateCriteriaAttributesBuilderFor() {
        return CriteriaAttributes.<T>builder()
                .add("id", (root) -> root.get(DocumentEntity_.id))
                .addEnumType("type", (root) -> root.get(DocumentEntity_.type))
                .add("name", (root) -> root.get(DocumentEntity_.name))
                .addReference("organization", (root) -> root.get(DocumentEntity_.organization))
                .addReference("counterparty", (root) -> root.get(DocumentEntity_.counterparty))
                .add("moment", (root) -> root.get(DocumentEntity_.moment))
                .addEnumType("currency", (root) -> root.get(DocumentEntity_.currency))
                .add("exchangeRate", (root) -> root.get(DocumentEntity_.exchangeRate))
                .add("amount", (root) -> root.get(DocumentEntity_.amount))
                .add("deleted", (root) -> root.get(DocumentEntity_.deleted))
                .add("commited", (root) -> root.get(DocumentEntity_.commited))
                .add("comment", (root) -> root.get(DocumentEntity_.comment));
    }

}


package kz.jarkyn.backend.document.core.service;


import kz.jarkyn.backend.core.search.CriteriaAttributes;
import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.document.core.model.DocumentEntity_;
import kz.jarkyn.backend.document.core.model.DocumentTypeEntity;
import kz.jarkyn.backend.document.core.repository.DocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DocumentService {
    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Transactional(readOnly = true)
    public String findNextName(Class<? extends DocumentEntity> type) {
        throw new RuntimeException();
    }


    @Transactional(readOnly = true)
    public String findNextName(DocumentTypeEntity type) {
        String namePrefix = getNamePrefix(type);
        String lastName = documentRepository.getLastNameByNamePrefix(namePrefix);
        if (lastName == null) {
            return namePrefix + "-00001";
        }
        Matcher matcher = Pattern.compile(namePrefix + "-(\\d{5})").matcher(lastName);
        if (!matcher.find()) {
            throw new RuntimeException("unsupported name : " + lastName);
        }
        int lastNameId = Integer.parseInt(matcher.group(1));
        return String.format("%s-%05d", namePrefix, lastNameId + 1);
    }

    public void validateName(DocumentEntity document) {
        if (document.getName() == null) {
            throw new RuntimeException("document name is null");
        }
        String namePrefix = getNamePrefix(document.getType());
        String name = document.getName();
        Matcher matcher = Pattern.compile(namePrefix + "-(\\d{5})").matcher(name);
        if (!matcher.find()) {
            throw new RuntimeException("unsupported name : " + name);
        }
    }

    private String getNamePrefix(DocumentTypeEntity type) {
        return switch (DocumentTypeService.DocumentTypeCode.valueOf(type.getCode())) {
            case SALE -> "SL";
            case SUPPLY -> "SP";
            case PAYMENT_IN -> "PI";
            case PAYMENT_OUT -> "PO";
        };
    }

    public <T extends DocumentEntity> CriteriaAttributes.Builder<T> generateCriteriaAttributesBuilderFor(Class<T> type) {
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

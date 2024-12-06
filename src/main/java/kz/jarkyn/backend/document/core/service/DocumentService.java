
package kz.jarkyn.backend.document.core.service;


import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.document.core.repository.DocumentRepository;
import kz.jarkyn.backend.document.sale.model.SaleEntity;
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
        String namePrefix = switch (type.getSimpleName()) {
            case "SaleEntity" -> "SL";
            case "PaymentInEntity" -> "PI";
            default -> throw new RuntimeException("unsupported document : " + type.getSimpleName());
        };
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
        String namePrefix = switch (document.getClass().getSimpleName()) {
            case "SaleEntity" -> "SL";
            case "PaymentInEntity" -> "PI";
            default -> throw new RuntimeException("unsupported document : " + document.getClass().getSimpleName());
        };
        String name = document.getName();
        Matcher matcher = Pattern.compile(namePrefix + "-(\\d{5})").matcher(name);
        if (!matcher.find()) {
            throw new RuntimeException("unsupported name : " + name);
        }
    }


}

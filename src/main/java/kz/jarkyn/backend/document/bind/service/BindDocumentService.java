package kz.jarkyn.backend.document.bind.service;


import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.utils.EntityDivider;
import kz.jarkyn.backend.document.bind.specifications.BindDocumentSpecifications;
import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.document.bind.mapper.BindDocumentMapper;
import kz.jarkyn.backend.document.bind.model.BindDocumentEntity;
import kz.jarkyn.backend.document.bind.model.dto.BindDocumentResponse;
import kz.jarkyn.backend.document.bind.model.dto.BindDocumentRequest;
import kz.jarkyn.backend.document.bind.repository.BindDocumentRepository;
import kz.jarkyn.backend.document.core.model.DocumentTypeEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BindDocumentService {
    private final BindDocumentRepository bindDocumentRepository;
    private final BindDocumentMapper bindDocumentMapper;
    private final AuditService auditService;

    public BindDocumentService(
            BindDocumentRepository bindDocumentRepository,
            BindDocumentMapper bindDocumentMapper,
            AuditService auditService) {
        this.bindDocumentRepository = bindDocumentRepository;
        this.bindDocumentMapper = bindDocumentMapper;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<BindDocumentResponse> findResponseByPrimaryDocument(DocumentEntity primaryDocument) {
        List<BindDocumentEntity> bindDocuments = bindDocumentRepository
                .findAll(BindDocumentSpecifications.primaryDocument(primaryDocument));
        BigDecimal primaryNotBindAmount = bindDocuments
                .stream().map(BindDocumentEntity::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add)
                .negate().add(primaryDocument.getAmount());
        return bindDocuments.stream().map(bindDocument -> {
            DocumentEntity relatedDocument = bindDocument.getRelatedDocument();
            BigDecimal relatedNotBindAmount = bindDocumentRepository
                    .findAll(BindDocumentSpecifications.relatedDocument(relatedDocument))
                    .stream().map(BindDocumentEntity::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add)
                    .negate().add(relatedDocument.getAmount());
            return bindDocumentMapper.toResponse(bindDocument, primaryNotBindAmount, relatedNotBindAmount);
        }).toList();
    }

    @Transactional(readOnly = true)
    public List<BindDocumentResponse> findResponseByRelatedDocument(
            DocumentEntity relatedDocument, DocumentTypeEntity primaryDocumentType) {
        List<BindDocumentEntity> bindDocuments = bindDocumentRepository.findAll(Specification.allOf(
                BindDocumentSpecifications.relatedDocument(relatedDocument),
                BindDocumentSpecifications.primaryDocumentType(primaryDocumentType)));
        BigDecimal relatedNotBindAmount = bindDocuments
                .stream().map(BindDocumentEntity::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add)
                .negate().add(relatedDocument.getAmount());
        return bindDocuments.stream().map(bindDocument -> {
            DocumentEntity primaryDocument = bindDocument.getPrimaryDocument();
            BigDecimal primaryNotBindAmount = bindDocumentRepository
                    .findAll(BindDocumentSpecifications.primaryDocument(primaryDocument))
                    .stream().map(BindDocumentEntity::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add)
                    .negate().add(primaryDocument.getAmount());
            return bindDocumentMapper.toResponse(bindDocument, primaryNotBindAmount, relatedNotBindAmount);
        }).toList();
    }

    @Transactional
    public void save(DocumentEntity primaryDocument, List<BindDocumentRequest> bindDocumentRequests) {
        EntityDivider<BindDocumentEntity, BindDocumentRequest> divider = new EntityDivider<>(
                bindDocumentRepository.findAll(BindDocumentSpecifications.primaryDocument(primaryDocument)), bindDocumentRequests);
        for (EntityDivider<BindDocumentEntity, BindDocumentRequest>.Entry entry : divider.newReceived()) {
            BindDocumentEntity bindDocument = bindDocumentMapper.toEntity(entry.getReceived());
            bindDocument.setPrimaryDocument(primaryDocument);
            bindDocumentRepository.save(bindDocument);
            auditService.saveEntity(bindDocument, bindDocument.getPrimaryDocument(), "bindDocuments");
        }
        for (EntityDivider<BindDocumentEntity, BindDocumentRequest>.Entry entry : divider.edited()) {
            bindDocumentMapper.editEntity(entry.getCurrent(), entry.getReceived());
            auditService.saveEntity(entry.getCurrent(), entry.getCurrent().getPrimaryDocument(), "bindDocuments");
        }
        for (BindDocumentEntity bindDocument : divider.skippedCurrent()) {
            bindDocumentRepository.delete(bindDocument);
            auditService.delete(bindDocument, bindDocument.getPrimaryDocument());
        }
    }
}

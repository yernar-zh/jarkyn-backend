package kz.jarkyn.backend.audit.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.jarkyn.backend.audit.config.AuditIgnore;
import kz.jarkyn.backend.audit.mapper.ChangeMapper;
import kz.jarkyn.backend.audit.model.AuditEntity;
import kz.jarkyn.backend.audit.model.dto.*;
import kz.jarkyn.backend.audit.model.message.AuditSaveMessage;
import kz.jarkyn.backend.audit.repository.AuditRepository;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.ReferenceEntity;
import kz.jarkyn.backend.core.model.dto.Pair;
import kz.jarkyn.backend.core.model.dto.Triple;
import kz.jarkyn.backend.core.sorts.EntitySorts;
import kz.jarkyn.backend.audit.specifications.AuditSpecifications;
import kz.jarkyn.backend.core.config.AppRabbitTemplate;
import kz.jarkyn.backend.core.config.RabbitRoutingKeys;
import kz.jarkyn.backend.core.model.AbstractEntity;
import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.user.model.SessionEntity;
import kz.jarkyn.backend.user.service.AuthService;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import kz.jarkyn.backend.core.utils.RequestMoment;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AuditService {
    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    private static final String CREATE = "CREATE";
    private static final String EDITE = "EDITE";

    private final AuditRepository auditRepository;
    private final ChangeMapper changeMapper;
    private final ObjectMapper objectMapper;
    private final AuthService authService;
    private final AppRabbitTemplate appRabbitTemplate;
    private final ObjectProvider<RequestMoment> requestMomentProvider;
    private final EntityManager entityManager;

    public AuditService(
            AuditRepository auditRepository,
            ChangeMapper changeMapper,
            ObjectMapper objectMapper,
            AuthService authService,
            AppRabbitTemplate appRabbitTemplate,
            ObjectProvider<RequestMoment> requestMomentProvider,
            EntityManager entityManager) {
        this.auditRepository = auditRepository;
        this.changeMapper = changeMapper;
        this.objectMapper = objectMapper;
        this.authService = authService;
        this.appRabbitTemplate = appRabbitTemplate;
        this.requestMomentProvider = requestMomentProvider;
        this.entityManager = entityManager;
    }

    private Instant getCurrentMoment() {
        if (RequestContextHolder.getRequestAttributes() != null) {
            RequestMoment requestMoment = requestMomentProvider.getIfAvailable();
            if (requestMoment != null) {
                return requestMoment.getMoment();
            }
        }
        return Instant.now();
    }

    public MainEntityChangeResponse findLastChange(UUID entityId) {
        AuditEntity audit = auditRepository.findOne(
                        AuditSpecifications.relatedEntityId(entityId), EntitySorts.byCreatedDesc())
                .orElseThrow(ExceptionUtils.entityNotFound());
        String action = audit.getEntityId().equals(entityId) ? audit.getAction() : EDITE;
        return changeMapper.toMainEntityChange(
                audit.getMoment(), audit.getSession().getUser(),
                action, List.of(), List.of());
    }

    public List<MainEntityChangeResponse> findChanges(UUID entityId) {
        List<List<AuditEntity>> changeGroups = auditRepository.findAll(AuditSpecifications.relatedEntityId(entityId))
                .stream()
                .collect(Collectors.groupingBy(AuditEntity::getMoment)).entrySet()
                .stream().sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue).toList();
        Map<UUID, Map<String, String>> beforeValueMap = new HashMap<>();
        return changeGroups.stream().map(changeGroupAudits -> {
            List<Pair<String, EntityChangeResponse>> changeEntityPairs = changeGroupAudits
                    .stream().collect(Collectors.groupingBy(AuditEntity::getEntityId))
                    .values()
                    .stream().map(entityChangeAudits -> {
                        Set<String> existKeys = entityChangeAudits
                                .stream()
                                .map(AuditEntity::getFieldName)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toSet());
                        Map<String, String> beforeFiledValues = beforeValueMap
                                .computeIfAbsent(entityChangeAudits.getFirst().getEntityId(), _ -> new HashMap<>());
                        String action = entityChangeAudits.stream()
                                .map(AuditEntity::getAction)
                                .findFirst().orElseThrow();
                        // fieldName, beforeFieldValue, afterFieldValue
                        List<Triple<String, String, String>> existChanges = entityChangeAudits
                                .stream()
                                .filter(audit -> existKeys.contains(audit.getFieldName()))
                                .map(audit -> {
                                    String beforeFiledValue = beforeFiledValues.get(audit.getFieldName());
                                    beforeFiledValues.put(audit.getFieldName(), audit.getFieldValue());
                                    return Triple.of(audit.getFieldName(), beforeFiledValue, audit.getFieldValue());
                                }).toList();
                        // fieldName, beforeFieldValue, afterFieldValue
                        List<Triple<String, String, String>> noChanges = beforeFiledValues.entrySet().stream()
                                .filter(entry -> !existKeys.contains(entry.getKey()))
                                .map(entry -> Triple.of(entry.getKey(), entry.getValue(), entry.getValue()))
                                .toList();
                        // fieldName, beforeFieldValue, afterFieldValue
                        List<Triple<String, String, String>> changes = Stream
                                .of(existChanges, noChanges)
                                .flatMap(Collection::stream).toList();
                        String entityName = changes.stream()
                                .map(Triple::getFirst)
                                .map(name -> name.split("\\."))
                                .max(Comparator.comparing(parts -> parts.length))
                                .filter(parts -> parts.length > 1)
                                .map(parts -> parts[0])
                                .orElse(null);
                        List<FieldChangeResponse> fieldChanges = changes.stream().map(triple ->
                                changeMapper.toFieldChangeResponse(
                                        entityName != null ? triple.getFirst().substring(entityName.length() + 1) : triple.getFirst(),
                                        toJsonNode(triple.getSecond()),
                                        toJsonNode(triple.getThird()))
                        ).toList();
                        return Pair.of(entityName, changeMapper.toEntityChangeResponse(action, fieldChanges));
                    }).toList();
            String action = changeEntityPairs.stream()
                    .filter(pair -> pair.getFirst() == null)
                    .map(pair -> pair.getSecond().getAction())
                    .findFirst().orElse(EDITE);
            List<FieldChangeResponse> fieldChanges = changeEntityPairs.stream()
                    .filter(pair -> pair.getFirst() == null)
                    .map(Pair::getSecond).map(EntityChangeResponse::getFieldChanges)
                    .findFirst().orElse(List.of())
                    .stream()
                    .filter(_ -> action.equals(EDITE))
                    .filter(fieldChange -> !Objects.equals(fieldChange.getBefore(), fieldChange.getAfter()))
                    .toList();
            List<EntityGroupChangeResponse> entityGroupChanges = changeEntityPairs.stream()
                    .filter(pair -> pair.getFirst() != null)
                    .collect(Collectors.groupingBy(Pair::getFirst, Collectors.mapping(Pair::getSecond, Collectors.toList())))
                    .entrySet().stream()
                    .map(entry -> changeMapper.toEntityGroupChangeResponse(entry.getKey(), entry.getValue()))
                    .filter(_ -> action.equals(EDITE))
                    .toList();
            AuditEntity firstChangeGroupAudit = changeGroupAudits.getFirst();
            return changeMapper.toMainEntityChange(
                    firstChangeGroupAudit.getMoment(), firstChangeGroupAudit.getSession().getUser(),
                    action, fieldChanges, entityGroupChanges);
        }).sorted(Comparator.comparing(MainEntityChangeResponse::getMoment).reversed()).toList();
    }

    @Transactional
    public void saveEntity(AbstractEntity entity) {
        saveEntity(entity, entity, null);
    }

    @Transactional
    public void saveEntity(AbstractEntity entity, AbstractEntity relatedEntity, String entityName) {
        saveEntity(entity, relatedEntity, entityName, authService.getCurrentSession(), getCurrentMoment());
    }

    public void saveEntity(
            AbstractEntity entity, AbstractEntity relatedEntity, String entityName,
            SessionEntity session, Instant instant) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> clazz = entity.getClass(); clazz != null && clazz != Object.class; clazz = clazz.getSuperclass()) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        }
        try {
            for (Field field : fields) {
                if (field.isAnnotationPresent(AuditIgnore.class)) {
                    continue;
                }
                field.setAccessible(true);
                save(entity, relatedEntity.getId(),
                        entityName != null ? entityName + "." + field.getName() : field.getName(),
                        field.get(entity), session, instant);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    public void saveEntityAsync(AbstractEntity entity, AbstractEntity relatedEntity, String entityName) {
        AuditSaveMessage message = new AuditSaveMessage(
                entity.getId(), entity.getClass().getName(),
                relatedEntity.getId(), relatedEntity.getClass().getName(),
                entityName, authService.getCurrentSession().getId(), getCurrentMoment()
        );
        appRabbitTemplate.sendAfterCommit(RabbitRoutingKeys.AUDIT_SAVE, message);
    }

    public void saveEntityAsync(AbstractEntity entity) {
        saveEntityAsync(entity, entity, null);
    }

    @RabbitListener(queues = "${rabbitmq.queue.audit-save}", concurrency = "4")
    @Transactional
    public void onAuditSave(AuditSaveMessage msg) {
        try {
            AbstractEntity entity = load(msg.getEntityClass(), msg.getEntityId());
            if (entity == null) return; // nothing to do
            AbstractEntity related = msg.getRelatedEntityClass() != null && msg.getRelatedEntityId() != null
                    ? load(msg.getRelatedEntityClass(), msg.getRelatedEntityId())
                    : entity;
            if (related == null) related = entity;
            SessionEntity session = entityManager.find(SessionEntity.class, msg.getSessionId());
            if (session == null) {
                log.error("AUDIT_LISTENER: Session not found: {}", msg.getSessionId());
                return;
            }
            saveEntity(entity, related, msg.getEntityName(), session, msg.getInstant());
        } catch (Exception e) {
            log.error("AUDIT_LISTENER", e);
        }
    }

    @Transactional
    public void delete(AbstractEntity entity) {
        delete(entity, entity);
    }

    @Transactional
    public void delete(AbstractEntity entity, AbstractEntity relatedEntity) {
        addAction(entity, relatedEntity, "DELETE");
    }

    @Transactional
    public void commit(AbstractEntity entity) {
        addAction(entity, entity, "COMMIT");
    }

    @Transactional
    public void undoCommit(AbstractEntity entity) {
        addAction(entity, entity, "UNDO_COMMIT");
    }

    @Transactional
    public void archive(AbstractEntity entity) {
        addAction(entity, entity, "ARCHIVE");
    }

    @Transactional
    public void unarchive(AbstractEntity entity) {
        addAction(entity, entity, "UNARCHIVE");
    }

    private void save(AbstractEntity entity, UUID relatedEntityId, String fieldName, Object fieldValueObj,
                      SessionEntity session, Instant instant) {
        try {
            AuditEntity oldAudit = auditRepository.findOne(Specification
                            .where(AuditSpecifications.entityId(entity.getId()))
                            .and(AuditSpecifications.relatedEntityId(relatedEntityId))
                            .and(AuditSpecifications.fieldName(fieldName))
                    , EntitySorts.byCreatedDesc()).orElse(null);

            JsonNode fieldValue;
            if ((fieldValueObj instanceof AbstractEntity)) {
                if (fieldValueObj instanceof ReferenceEntity reference) {
                    fieldValue = objectMapper.createObjectNode()
                            .put("id", String.valueOf(reference.getId()))
                            .put("name", reference.getName())
                            .put("archived", reference.getArchived());
                } else if (fieldValueObj instanceof DocumentEntity document) {
                    fieldValue = objectMapper.createObjectNode()
                            .put("id", String.valueOf(document.getId()))
                            .put("name", document.getName())
                            .put("deleted", document.getDeleted())
                            .set("type", objectMapper.createObjectNode()
                                    .put("id", String.valueOf(document.getType().getId()))
                                    .put("name", document.getType().getName())
                                    .put("code", document.getType().getCode())
                                    .put("archived", document.getType().getArchived()));
                } else {
                    throw new RuntimeException("Unsupported field type: " + fieldValueObj.getClass());
                }
                if (oldAudit != null && Objects.equals(
                        objectMapper.readTree(oldAudit.getFieldValue()).get("id"),
                        fieldValue.get("id")))
                    return;
            } else {
                fieldValue = objectMapper.valueToTree(fieldValueObj);
                if (oldAudit != null && Objects.equals(oldAudit.getFieldValue(),
                        objectMapper.writeValueAsString(fieldValue))) return;
            }
            AuditEntity newAudit = new AuditEntity();
            newAudit.setMoment(instant);
            newAudit.setSession(session);
            newAudit.setEntityId(entity.getId());
            newAudit.setRelatedEntityId(relatedEntityId);
            newAudit.setAction(oldAudit == null ? CREATE : EDITE);
            newAudit.setFieldName(fieldName);
            newAudit.setFieldValue(objectMapper.writeValueAsString(fieldValue));
            auditRepository.save(newAudit);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void addAction(AbstractEntity entity, AbstractEntity relatedEntity, String action) {
        SessionEntity session = authService.getCurrentSession();
        AuditEntity newAudit = new AuditEntity();
        newAudit.setMoment(getCurrentMoment());
        newAudit.setSession(session);
        newAudit.setEntityId(entity.getId());
        newAudit.setRelatedEntityId(relatedEntity.getId());
        newAudit.setAction(action);
        newAudit.setFieldName(null);
        newAudit.setFieldValue(null);
        auditRepository.save(newAudit);
    }

    private JsonNode toJsonNode(String json) {
        if (json == null) return null;
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private AbstractEntity load(String className, UUID id) {
        if (className == null || id == null) return null;
        try {
            Class<?> entityClass = Class.forName(className);
            Object obj = entityManager.find(entityClass, id);
            return (obj instanceof AbstractEntity) ? (AbstractEntity) obj : null;
        } catch (ClassNotFoundException e) {
            log.error("AUDIT_LISTENER: Class not found {}", className, e);
            return null;
        }
    }

}

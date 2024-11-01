package kz.jarkyn.backend.config;

import kz.jarkyn.backend.audit.model.enity.RevisionInfoEntity;
import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class RevisionInfoListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RevisionInfoEntity revisionInfo = (RevisionInfoEntity) revisionEntity;
        revisionInfo.setUserId(userId);
    }
}
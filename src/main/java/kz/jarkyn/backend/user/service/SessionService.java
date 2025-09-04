package kz.jarkyn.backend.user.service;


import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.user.model.SessionEntity;
import kz.jarkyn.backend.user.repository.SessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class SessionService {
    private final SessionRepository sessionRepository;
    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Transactional(readOnly = true)
    public SessionEntity findById(UUID id) {
        return sessionRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
    }
}
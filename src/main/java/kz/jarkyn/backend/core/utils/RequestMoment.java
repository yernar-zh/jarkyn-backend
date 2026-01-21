package kz.jarkyn.backend.core.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.time.Instant;

@Component
@RequestScope
public class RequestMoment {
    private final Instant moment = Instant.now();

    public Instant getMoment() {
        return moment;
    }
}

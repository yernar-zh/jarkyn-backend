package kz.jarkyn.backend.operation.sorts;

import kz.jarkyn.backend.operation.mode.TurnoverEntity_;
import org.springframework.data.domain.Sort;

public final class TurnoverSorts {
    private TurnoverSorts() {
    }

    public static Sort byMomentAsc() {
        return Sort.by(
                Sort.Order.asc(TurnoverEntity_.MOMENT),
                Sort.Order.asc(TurnoverEntity_.LAST_MODIFIED_AT)
        );
    }

    public static Sort byMomentDesc() {
        return Sort.by(
                Sort.Order.desc(TurnoverEntity_.MOMENT),
                Sort.Order.desc(TurnoverEntity_.LAST_MODIFIED_AT)
        );
    }
}
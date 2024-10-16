package kz.jarkyn.backend.service.utils;

import kz.jarkyn.backend.exception.ApiValidationException;
import kz.jarkyn.backend.model.common.AbstractEntity;
import kz.jarkyn.backend.model.common.api.IdApi;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EntityDivider<C extends AbstractEntity, R extends IdApi> {
    private final List<Entry> edited;
    private final List<Entry> newReceived;
    private final List<C> skippedCurrent;

    public EntityDivider(List<C> current, List<R> received) {
        edited = new ArrayList<>();
        newReceived = new ArrayList<>();
        skippedCurrent = new ArrayList<>();

        Map<UUID, C> currentMap = current.stream().collect(Collectors.toMap(AbstractEntity::getId, Function.identity()));
        if (currentMap.size() != current.size()) {
            throw new ApiValidationException("Current list contains duplicates");
        }
        Set<UUID> editedReceivedSet = new HashSet<>();
        for (int i = 0; i < received.size(); i++) {
            R receivedI = received.get(i);
            if (receivedI.getId() == null || !currentMap.containsKey(receivedI.getId())) {
                newReceived.add(new Entry(null, receivedI, i));
            } else {
                edited.add(new Entry(Objects.requireNonNull(currentMap.get(receivedI.getId())), receivedI, i));
                editedReceivedSet.add(receivedI.getId());
            }
        }
        for (Map.Entry<UUID, C> entry : currentMap.entrySet()) {
            if (!editedReceivedSet.contains(entry.getKey())) {
                skippedCurrent.add(entry.getValue());
            }
        }
    }

    public List<Entry> edited() {
        return edited;
    }

    public List<C> skippedCurrent() {
        return skippedCurrent;
    }

    public List<Entry> newReceived() {
        return newReceived;
    }

    public class Entry {
        private final C current;
        private final R received;
        private final Integer receivedPosition;

        private Entry(C current, R received, Integer receivedPosition) {
            this.current = current;
            this.received = received;
            this.receivedPosition = receivedPosition;
        }

        public C getCurrent() {
            return Objects.requireNonNull(current);
        }

        public R getReceived() {
            return received;
        }

        public Integer getReceivedPosition() {
            return receivedPosition;
        }
    }
}
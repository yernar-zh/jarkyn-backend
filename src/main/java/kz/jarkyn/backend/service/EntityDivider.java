package kz.jarkyn.backend.service;

import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EntityDivider<C, R, K> {
    private final Map<K, C> current;
    private final Map<K, R> received;

    public EntityDivider(
            List<C> current, Function<C, K> currentMapper,
            List<R> received, Function<R, K> receivedMapper) {
        this.current = current.stream().collect(Collectors.toMap(currentMapper, Function.identity()));
        this.received = received.stream().collect(Collectors.toMap(receivedMapper, Function.identity()));
    }

    public List<Pair<C, R>> edited() {
        return current.entrySet().stream()
                .filter(kuEntry -> received.containsKey(kuEntry.getKey()))
                .map(kuEntry -> Pair.of(kuEntry.getValue(), received.get(kuEntry.getKey()))).toList();
    }

    public List<C> skippedCurrent() {
        return current.entrySet().stream()
                .filter(kuEntry -> !received.containsKey(kuEntry.getKey()))
                .map(Map.Entry::getValue).toList();
    }

    public List<R> newReceived() {
        return received.entrySet().stream()
                .filter(kuEntry -> !current.containsKey(kuEntry.getKey()))
                .map(Map.Entry::getValue).toList();
    }
}
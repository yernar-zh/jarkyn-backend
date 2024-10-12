package kz.jarkyn.backend.service.utils;

import org.springframework.data.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EntityDivider<C, R, K> {
    private final Map<K, C> current;
    private final Map<K, Pair<R, Integer>> received;

    public EntityDivider(
            List<C> current, Function<C, K> currentMapper,
            List<R> received, Function<R, K> receivedMapper) {
        this.current = current.stream().collect(Collectors.toMap(currentMapper, Function.identity()));
        this.received = new HashMap<>();
        for (int i = 0; i < received.size(); i++) {
            this.received.put(receivedMapper.apply(received.get(i)), Pair.of(received.get(i), i));
        }
    }

    public List<Pair<C, Pair<R, Integer>>> edited() {
        return current.entrySet().stream()
                .filter(kuEntry -> received.containsKey(kuEntry.getKey()))
                .map(kuEntry -> Pair.of(kuEntry.getValue(), received.get(kuEntry.getKey()))).toList();
    }

    public List<C> skippedCurrent() {
        return current.entrySet().stream()
                .filter(kuEntry -> !received.containsKey(kuEntry.getKey()))
                .map(Map.Entry::getValue).toList();
    }

    public List<Pair<R, Integer>> newReceived() {
        return received.entrySet().stream()
                .filter(kuEntry -> !current.containsKey(kuEntry.getKey()))
                .map(Map.Entry::getValue).toList();
    }
}
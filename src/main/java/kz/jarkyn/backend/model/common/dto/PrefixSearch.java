package kz.jarkyn.backend.model.common.dto;

import java.util.*;

public class PrefixSearch {
    private static final String ENG_LETTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String KZ_LETTERS = "аәбвгғдеёжзийкқлмнңоөпрстуұүфхһцчшщъыіьэюя";
    private static final String NUMBERS = "0123456789";
    private static final Map<Character, Integer> charMap = new HashMap<>() {{
        String[] alphabets = new String[]{ENG_LETTERS, KZ_LETTERS, NUMBERS};
        for (int i = 0; i < alphabets.length; i++) {
            for (char chr : alphabets[i].toCharArray()) {
                put(chr, i);
            }
        }
    }};

    private final TreeSet<String> set;

    public PrefixSearch() {
        this.set = new TreeSet<>();
    }

    public void add(String text) {
        set.addAll(split(text));
    }

    public Boolean contains(String pattern) {
        List<String> patternKeys = split(pattern);
        for (String patternKey : patternKeys) {
            String value = set.ceiling(patternKey);
            if (value == null || !value.startsWith(patternKey)) {
                return false;
            }
        }
        return true;
    }

    private List<String> split(String text) {
        List<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        int lastAlphabetIndex = -1;
        for (char chr : (text.toLowerCase() + " ").toCharArray()) {
            Integer alphabetIndex = charMap.get(chr);
            if ((alphabetIndex == null || lastAlphabetIndex != alphabetIndex) && !sb.isEmpty()) {
                result.add(sb.toString());
                sb = new StringBuilder();
            }
            if (alphabetIndex != null) {
                sb.append(chr);
                lastAlphabetIndex = alphabetIndex;
            }
        }
        return result;
    }
}

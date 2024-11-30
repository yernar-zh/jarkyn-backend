package kz.jarkyn.backend.core.utils;

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

    public PrefixSearch(String... texts) {
        this.set = new TreeSet<>();
        for (String text : texts) {
            set.addAll(split(text));
        }
    }

    public Boolean contains(String search) {
        List<String> patterns = split(search);
        for (String pattern : patterns) {
            String value = set.ceiling(pattern);
            if (value == null || !value.startsWith(pattern)) {
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

package kz.jarkyn.backend.core.utils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public void addText(String text) {
        set.addAll(split(text));
    }

    public void addPhoneNumber(String phoneNumber) {
        Matcher matcher = Pattern.compile("\\+7(\\d{3})(\\d{3})(\\d{4})").matcher(phoneNumber);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
        set.addAll(List.of(matcher.group(1), matcher.group(2), matcher.group(3)));
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

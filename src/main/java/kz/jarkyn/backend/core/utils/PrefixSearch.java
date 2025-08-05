package kz.jarkyn.backend.core.utils;

import java.util.*;

public class PrefixSearch {
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

    public static List<String> split(String text) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int lastAlphabetType = -1;
        for (char chr : (text.toLowerCase() + " ").toCharArray()) {
            int alphabetType;
            if (chr >= '0' && chr <= '9') alphabetType = 0;
            else if (chr >= 'a' && chr <= 'z') alphabetType = 1;
            else if ((chr >= 'а' && chr <= 'я') || "әғқңөұүіһ".indexOf(chr) >= 0) alphabetType = 2;
            else alphabetType = -1;
            if ((lastAlphabetType != alphabetType) && !current.isEmpty()) {
                result.add(current.toString());
                current = new StringBuilder();
            }
            if (alphabetType != -1) {
                current.append(chr);
                lastAlphabetType = alphabetType;
            }
        }
        return result;
    }
}

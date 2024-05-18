package com.example.nettydemo.util;

public class StringUtil {

    public static String realInput(String input) {
        if (input == null || input.length() == 0) {
            return input;
        }
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (c == '\b' && sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
                continue;
            }
            sb.append(c);
        }
        return sb.toString();
    }
}

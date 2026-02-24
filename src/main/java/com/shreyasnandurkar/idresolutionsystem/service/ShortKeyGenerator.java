package com.shreyasnandurkar.idresolutionsystem.service;

import java.security.SecureRandom;

public class ShortKeyGenerator {

    private static final SecureRandom random = new SecureRandom();

    public static String generateShortKey(){
        StringBuilder shortKey = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            shortKey.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return shortKey.toString();
    }
}

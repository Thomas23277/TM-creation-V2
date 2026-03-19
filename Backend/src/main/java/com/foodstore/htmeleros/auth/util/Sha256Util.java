package com.foodstore.htmeleros.auth.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class Sha256Util {
    public static String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] dig = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(dig);
        } catch (Exception e) {
            throw new RuntimeException("Error hasheando contraseña", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}

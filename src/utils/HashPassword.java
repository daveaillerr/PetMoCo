package utils;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

public class HashPassword {
    /**
     * Simple Helper Method to hash passwords using SHA-256.
     * Keeps it standard, dependency-free, and straightforward.
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 hashing algorithm not found", e);
        }
    }
}

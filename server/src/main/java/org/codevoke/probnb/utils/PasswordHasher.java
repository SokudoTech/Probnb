package org.codevoke.probnb.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Random;

public class PasswordHasher {
    private static final int ITERATIONS = 30000;
    private static final int ITERATION_DEVIATION = 5000;
    private static final int KEY_LENGTH = 256;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String SEPARATOR = "$";

    public static String hash(String password) {
        try {
            char[] passwordChars = password.toCharArray();
            byte[] saltBytes = new byte[32]; new Random().nextBytes(saltBytes);
            int iterations = ITERATIONS + new Random().nextInt(ITERATION_DEVIATION);

            PBEKeySpec spec = new PBEKeySpec(
                    passwordChars,
                    saltBytes,
                    iterations,
                    KEY_LENGTH
            );

            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hashBytes = skf.generateSecret(spec).getEncoded();
            return String.join(SEPARATOR, new String[]{
                    "", ALGORITHM, String.valueOf(iterations), toBase64(saltBytes), toBase64(hashBytes)
            });
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public static boolean verify(String password, String hash) {
        try {
            String[] tokenParts = hash.split("\\"+SEPARATOR);
            String algorithm = tokenParts[1];
            int iterations = Integer.parseInt(tokenParts[2]);
            byte[] saltBytes = fromBase64(tokenParts[3]);
            byte[] correctHash = fromBase64(tokenParts[4]);

            char[] passwordChars = password.toCharArray();

            PBEKeySpec spec = new PBEKeySpec(
                    passwordChars,
                    saltBytes,
                    iterations,
                    KEY_LENGTH
            );

            SecretKeyFactory skf = SecretKeyFactory.getInstance(algorithm);
            byte[] testHashBytes = skf.generateSecret(spec).getEncoded();

            return MessageDigest.isEqual(testHashBytes, correctHash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error verifying password", e);
        }
    }

    private static String toBase64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    public static byte[] fromBase64(String base64String) {
        return Base64.getDecoder().decode(base64String);
    }

    public static void main(String[] args) {
        String test1 = hash("admin");
        String test2 = hash("admin");
        System.out.println(test1 + "\n" + test2);
        System.out.println(verify("admin", test1) + " " + verify("admin", test2));
    }
}
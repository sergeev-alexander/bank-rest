package com.example.bankcards.config;

public class EncryptionConstants {

    public static final String ALGORITHM = "AES";
    public static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    public static final int KEY_LENGTH = 16;

    private EncryptionConstants() {
        // empty
    }
}
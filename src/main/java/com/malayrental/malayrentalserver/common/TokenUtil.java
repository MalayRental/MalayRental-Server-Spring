package com.malayrental.malayrentalserver.common;

import java.security.SecureRandom;
import org.apache.commons.codec.binary.Hex;

public class TokenUtil {
    public static String generateToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        return Hex.encodeHexString(bytes);
    }
} 
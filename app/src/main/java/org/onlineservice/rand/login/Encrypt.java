package org.onlineservice.rand.login;


import android.util.Base64;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encrypt {
    public String getEncryptedPassword(final String passwd){
        byte[] strByte = passwd.getBytes();
        final String encodedString = Base64.encodeToString(strByte,Base64.DEFAULT);

        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(encodedString.getBytes());
            final byte[] digest = md.digest();
            final String pwd_hash = String.format("%0" + (digest.length * 2) + "X", new BigInteger(1, digest));
            return pwd_hash;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

    }
}

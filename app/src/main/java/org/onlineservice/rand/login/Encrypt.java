package org.onlineservice.rand.login;


import android.util.Base64;

public class Encrypt {
    public String getEncryptedPassword(final String passwd){
        byte[] strByte = passwd.getBytes();
        final String encodedString = Base64.encodeToString(strByte,Base64.DEFAULT);
        return encodedString;
    }
}

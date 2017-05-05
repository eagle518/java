/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.genelet.framework;

import java.util.Base64;

/**
 *
 * @author Peter
 */
public class Scoder {
    
    public static byte[] scode(byte[] in, byte[] cryptext) {
        int len_cryptext = cryptext.length;
        int len_text = in.length;
        byte[] out = new byte[len_text];
        int k = len_cryptext/2;
        for (int i=0; i<len_text; i++) {
            out[i] = (byte) ( in[i] ^ cryptext[k] ^ (cryptext[0]*(255&k)) ) ;
            k = scode_crypt(cryptext, len_cryptext, k);
        }
        return out;
    }
    
    private static int scode_crypt(byte[] cryptext, int len_cryptext, int i) {
        if (i<(len_cryptext-1)) {
            cryptext[i] += cryptext[i+1];
        } else {
            cryptext[i] += cryptext[0];
        }
        if (cryptext[i]==0) {
            cryptext[i] += 1;
        }
        i++;
        if (i >= len_cryptext) {
            i = 0;
        }
        return i;
    }
    
    public static String encode_scoder(String text, String cryptext) {
        return Base64.getEncoder().encodeToString(scode(text.getBytes(), cryptext.getBytes()));
    }
    
    public static String decode_scoder(String text, String cryptext) {
        return new String(scode(Base64.getDecoder().decode(text), cryptext.getBytes()));
    }
}

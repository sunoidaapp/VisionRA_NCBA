package com.vision.util;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

@Component
public class AES_Encrypt_DecryptUtil {
	
	/* Should maintain the String length */
	private static final String initVector = "vision09SecretIV"; //encryptionIntVec
	
	
	/* Should maintain the String length */
    private static final String key = "visionEncryptKey"; //aesEncryptionKey
    
    
    
    /*public static void main(String[] args) {
    	try {
    		String plainTxt = "Good";
    		String eStr = encrypt(plainTxt);
    		//System.out.println(String.format("eStr: %s", eStr));
    		String dStr = decryptData(eStr);
    		//System.out.println(String.format("dStr: %s", dStr));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	
    public static String decrypt(String encryptedData) throws Exception {
		IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
		SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
		return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedData)));
	}
	
	public static String encrypt(String plainTxt) throws Exception {
		IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
		SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
		return Base64.getEncoder().encodeToString(cipher.doFinal(plainTxt.getBytes("UTF-8")));
	}
}

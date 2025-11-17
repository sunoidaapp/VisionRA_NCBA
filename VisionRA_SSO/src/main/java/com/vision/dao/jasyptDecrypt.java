package com.vision.dao;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class jasyptDecrypt {

	public static void main(String[] args) {
//		FTP
//	    		String password = "iVJaDliz7sl1wOpG/0efnNc0e9WDneOj";
		
		//DB
		String password = "3ZCNt552KPjXkmEJ+kpXjAFQtg7N6bdb";
		
	    		String jasyptSecreatKey = "v!$!0n";
	    		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
	        	encryptor.setPassword(jasyptSecreatKey);
	            encryptor.setAlgorithm("PBEWithSHA1AndDESede");
	            password = encryptor.decrypt(password);
	            System.out.println(password);
	    }
	}


package com.parth.urlshortener.util;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component
public class Base62Util {
	
	private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	
	private static final SecureRandom RANDOM = new SecureRandom();
	
	/**
     * Encode a numeric ID to Base62 string.
     * Example: 12345 -> "3d7"
     */
	
	public String encode(long num) {
		StringBuilder sb = new StringBuilder();
		while(num > 0) {
			sb.append(BASE62.charAt((int)(num%62)));
			num /= 62;
		}
		return sb.reverse().toString();
	}
	
	
	/**
     * Decode a Base62 string back to a numeric value.
     */
	
	public long decode(String str) {
		long num=0;
		for(char c: str.toCharArray()) {
			num = num * 62 + BASE62.indexOf(c);
		}
		return num;
	}
	
	/**
     * Generate a random Base62 short code of given length.
     * 7 chars = 62^7 = 3,521,614,606,208 possible combinations.
     */
	
	public String generateRandomCode(int length) {
		StringBuilder sb = new StringBuilder(length);
		for(int i=0; i< length; i++) {
			sb.append(BASE62.charAt(RANDOM.nextInt(62)));
		}
		return sb.toString();
	}
}

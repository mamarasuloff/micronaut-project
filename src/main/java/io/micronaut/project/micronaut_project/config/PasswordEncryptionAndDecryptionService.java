package io.micronaut.project.micronaut_project.config;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;

public class PasswordEncryptionAndDecryptionService {
	
	String SECRET_KEY     = "micronaut-project-secret-key";
	String SALT           = "micronaut-project-salt";
	String ARRAY_OF_BYTES = "micronautproject";
	
	public IvParameterSpec generateIv() throws DecoderException, UnsupportedEncodingException {
	    byte[] initializationVector = ARRAY_OF_BYTES.getBytes();
	    return new IvParameterSpec(initializationVector);
	}
	
	public String encrypt(String strToEncrypt) {
		try { 
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256"); 
	        KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256); 
	        SecretKey tmp = factory.generateSecret(spec); 
	        SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES"); 
	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); 
	        cipher.init(Cipher.ENCRYPT_MODE, secretKey, generateIv()); 
	        return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8))); 
	    } 
	    catch(Exception e) { 
	    	System.out.println("Error while encrypting: " + e.toString()); 
	    } 
		return null; 
	}
	
    public String decrypt(String strToDecrypt) { 
    	try { 
    		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256"); 
            KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256); 
            SecretKey tmp = factory.generateSecret(spec); 
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES"); 
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING"); 
            cipher.init(Cipher.DECRYPT_MODE, secretKey, generateIv());
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt))); 
        } 
        catch (Exception e) { 
            System.out.println("Error while decrypting: " + e.toString()); 
        } 
        return null; 
    }
}

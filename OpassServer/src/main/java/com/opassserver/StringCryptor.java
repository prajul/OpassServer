package com.opassserver;

import android.util.Base64;
import android.util.Log;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class StringCryptor 
{
	private static final String CIPHER_ALGORITHM = "AES";
	private static final String RANDOM_GENERATOR_ALGORITHM = "SHA1PRNG";
	private static final int RANDOM_KEY_SIZE = 128;

    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final byte[] keyValue = new byte[] { 'T', 'h', 'i', 's', 'I', 's', 'A', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y' };
	
	// Encrypts string and encode in Base64
	public static String encrypt( String password, String data ) throws Exception 
	{
		byte[] secretKey = generateKey( password.getBytes() );
	    byte[] clear = data.getBytes();
		
	    SecretKeySpec secretKeySpec = new SecretKeySpec( secretKey, CIPHER_ALGORITHM );
		Cipher cipher = Cipher.getInstance( CIPHER_ALGORITHM );
	    cipher.init( Cipher.ENCRYPT_MODE, secretKeySpec );
	    
	    byte[] encrypted = cipher.doFinal( clear );
	    String encryptedString = Base64.encodeToString( encrypted, Base64.DEFAULT );
	    
		return encryptedString;
	}
	
	// Decrypts string encoded in Base64
	public static String decrypt( String password, String encryptedData ) throws Exception 
	{
		byte[] secretKey = generateKey( password.getBytes() );
		
		SecretKeySpec secretKeySpec = new SecretKeySpec( secretKey, CIPHER_ALGORITHM );
		Cipher cipher = Cipher.getInstance( CIPHER_ALGORITHM );
	    cipher.init( Cipher.DECRYPT_MODE, secretKeySpec );
	    
	    byte[] encrypted = Base64.decode( encryptedData, Base64.DEFAULT );
	    byte[] decrypted = cipher.doFinal( encrypted );
	    
		return new String( decrypted );
	}
	
	public static byte[] generateKey( byte[] seed ) throws Exception
	{
		KeyGenerator keyGenerator = KeyGenerator.getInstance( CIPHER_ALGORITHM );
		SecureRandom secureRandom = SecureRandom.getInstance( RANDOM_GENERATOR_ALGORITHM );
		secureRandom.setSeed( seed );
	    keyGenerator.init( RANDOM_KEY_SIZE, secureRandom );
	    SecretKey secretKey = keyGenerator.generateKey();
	    return secretKey.getEncoded();
	}



    public static String encrypt(String valueToEnc) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encValue = c.doFinal(valueToEnc.getBytes());
        String encryptedValue = Base64.encodeToString(encValue,Base64.DEFAULT);
        return encryptedValue;
    }


    public static String decrypt(String encryptedValue) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = Base64.decode(encryptedValue,Base64.DEFAULT);
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    private static Key generateKey() throws Exception {
        Key key = new SecretKeySpec(keyValue, ALGORITHM);
        // SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        // key = keyFactory.generateSecret(new DESKeySpec(keyValue));
        return key;
    }

public static  void AESCBCEncryption() throws NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException {

        String message = "1234567812345678";
        System.out.println("Plaintext: " + message + "\n");

        // generate a key
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        keygen.init(128);  // To use 256 bit keys, you need the "unlimited strength" encryption policy files from Sun.
        byte[] key = keygen.generateKey().getEncoded();
        SecretKeySpec skeySpec = new SecretKeySpec(keyValue, "AES");//new SecretKeySpec(key, "AES");

        // build the initialization vector.  This example is all zeros, but it
        // could be any value or generated using a random number generator.
//        byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    String iv = "0000000000000000";

    IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());

        // initialize the cipher for encrypt mode
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivspec);

        // encrypt the message
        byte[] encrypted = cipher.doFinal(message.getBytes());
        Log.i("Ciphertext: " , Base64.encodeToString(encrypted, Base64.DEFAULT));

        // reinitialize the cipher for decryption
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivspec);

        // decrypt the message
        byte[] decrypted = cipher.doFinal(encrypted);
        Log.i("Plaintext: ", new String(decrypted) + "\n");
    }



    public static String encrypt() throws Exception {
        try {
            String data = "This string contains a secret message.";
            String key = "ThisIsASecretKey";
            String iv = "1234567812345678";

            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            int blockSize = cipher.getBlockSize();

            // We need to pad with zeros to a multiple of the cipher block size,
            // so first figure out what the size of the plaintext needs to be.
            byte[] dataBytes = data.getBytes();
            int plaintextLength = dataBytes.length;
            if (plaintextLength % blockSize != 0) {
                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
            }

            // In java, primitive arrays of integer types have all elements
            // initialized to zero, so no need to explicitly zero any part of
            // the array.
            byte[] plaintext = new byte[plaintextLength];

            // Copy our actual data into the beginning of the array.  The
            // rest of the array is implicitly zero-filled, as desired.
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);

            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());

            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            byte[] encrypted = cipher.doFinal(plaintext);

            return  Base64.encodeToString(encrypted,Base64.DEFAULT);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

public static String decrypt()throws Exception {
    String data = "SXiAEJ5ZOsqmgpCROXGRbWSTm36b9cJsq1ShL6yYndo9c0JJOizAXLV8dBviLupY";
    String key = "ThisIsASecretKey";
    String iv = "1234567812345678";
try {

byte[] encoded  = Base64.decode(data,Base64.DEFAULT);
    Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
    SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
    IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
    cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
    byte[] encrypted = cipher.doFinal(encoded);
    Log.i("Decrypted string ",new String(encrypted));
    return new String(encrypted);

} catch (Exception e) {
    e.printStackTrace();
    return null;
}


}
}

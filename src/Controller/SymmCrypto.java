package Controller;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class SymmCrypto {

    private static final String ALGORITHM = "AES";
    private KeyGenerator keygen;
    private Cipher cipher;

    //constructor
    public SymmCrypto(){
        try {
            //instantiate the Cipher object
            cipher = Cipher.getInstance(ALGORITHM);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String decrypt(String cipherText, String encodedKey ) {
        String data = null;
        Key key = originalKey(encodedKey);

        try {
            //init
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] dataBytes = cipher.doFinal( Base64.getDecoder().decode(cipherText));
            //convert byte[] to String
            data = new String(dataBytes);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;//confidential data recovered

    }


    public String encrypt(String input, String encodedKey ){
        String cipherText = null;
        Key key = originalKey(encodedKey);

        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] cipherBytes = cipher.doFinal(input.getBytes());
            //convert byte[] to string
            cipherText = Base64.getEncoder().encodeToString(cipherBytes);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return cipherText;
    }

    public String getKey() throws NoSuchAlgorithmException {
        //gen secret key
        keygen = KeyGenerator.getInstance(ALGORITHM);
        keygen.init(128, new SecureRandom());
        Key key = keygen.generateKey();
        return Base64.getEncoder().encodeToString(key.getEncoded());

    }


    public Key originalKey(String encodedKey){
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        return(new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM));
    }

}

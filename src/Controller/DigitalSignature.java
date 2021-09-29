package Controller;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

public class DigitalSignature {

    //Hashing algorithm
    private final String HASHING_ALGO = "SHA-256";

    //Crypto algorithm
    private final String CRYPTO_ALGO = "RSA";

    private Cipher cipher;
    private KeyPairGenerator keygen;
    private KeyPair keypair;
    private KeyFactory kf;

    public DigitalSignature (){
        try{
            cipher = Cipher.getInstance(CRYPTO_ALGO);
            keygen = KeyPairGenerator.getInstance(CRYPTO_ALGO);
            keygen.initialize(512);

            kf = KeyFactory.getInstance("RSA");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    //Hashing and return as byte
    public byte[] hash (String data){
        byte[] hashBytes = null;
        //API-messagedigest
        try{
            MessageDigest md = MessageDigest.getInstance( HASHING_ALGO );
            hashBytes = md.digest(data.getBytes());
            return hashBytes;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }


    //Generate digital signature with private key
    public String encrypt (byte[] hash, String privateKeyString){
        byte[] dsBytes = null;
        try {
            //init
            byte[] privateByte = Base64.getDecoder().decode(privateKeyString);
            PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(privateByte));

            cipher.init( Cipher.ENCRYPT_MODE, privateKey);
            dsBytes = cipher.doFinal(hash);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(dsBytes != null){
            return Base64.getEncoder().encodeToString(dsBytes);
        } else {
            return null;
        }
    }


    //Verify digital signature with public key
    public boolean verify(String data, String ds, String publicKeyString){
        byte[] dataHash = hash(data);
        byte[] dsBytes = null;
        try {
            byte[] publicByte = Base64.getDecoder().decode(publicKeyString);
            PublicKey publicKey = kf.generatePublic(new X509EncodedKeySpec(publicByte));

            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            dsBytes = cipher.doFinal(Base64.getDecoder().decode(ds));

        } catch (Exception e){
            e.printStackTrace();
        } return Arrays.equals(dataHash, dsBytes);
    }

    public String[] getKeyPair(){
        keypair = keygen.generateKeyPair();
        String privateKey = Base64.getEncoder().encodeToString(keypair.getPrivate().getEncoded());
        String publicKey = Base64.getEncoder().encodeToString(keypair.getPublic().getEncoded());
        String[] keyPairArray = {privateKey,publicKey};
        return keyPairArray;
    }




}

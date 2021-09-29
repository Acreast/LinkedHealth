package Controller;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class HashingUtils {

    //Hashing with salt
    public static String hash(String input, byte[] salt) {

        String hashedInput = null;
        byte[] inputBytes = input.getBytes();

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(inputBytes);


            messageDigest.update(salt);


            byte[] hashedInputBytes = messageDigest.digest();


            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < hashedInputBytes.length; i++) {
                sb.append(Integer.toHexString(0xFF & hashedInputBytes[i]));
            }

            hashedInput = sb.toString();
        }
        catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }

        return hashedInput;
    }


    //Salt generate for password hashing and set fixed size
    private static final int SIZE = 16;
    public static byte[] generateSalt() {
        SecureRandom sr = new SecureRandom();
        byte[] b = new byte[SIZE];
        sr.nextBytes(b);
        return b;
    }

    //Block hash
    public static String hashBlock(String input, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] bytes = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }




}

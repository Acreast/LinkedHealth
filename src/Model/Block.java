package Model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;


import Controller.HashingUtils;
import org.javatuples.Decade;
import org.javatuples.Ennead;

public class Block implements Serializable{

    private String currentHash;
    private String previoushash;
    private String owner;
    private String creator;
    private String digitalSignature;
    private List<Decade<String, String, String, String, String, String, String,String,String,String>> data; //block data
    private long timestamp;



    public Block(List<List<String>> data, String previoushash,String ownerID,String digitalSignature,long timestamp,String creator) {
        List<Decade<String, String, String, String, String, String,String,String,String,String>> DecadeList =
                data.stream().map( elem -> Decade.fromCollection(elem) ).collect(Collectors.toList());
        this.previoushash = previoushash;
        this.data = DecadeList;
        this.timestamp = timestamp;
        this.owner = ownerID;
        this.digitalSignature = digitalSignature;
        this.creator = creator;
        this.currentHash = this.blockHashCode(Block.genByteArr(this.data), this.previoushash, this.owner ,this.timestamp);
    }




    public List<Decade<String, String, String, String, String, String,String,String,String,String>> getData() {
        return data;
    }


    public String getCurrentHash() {
        return currentHash;

    }

    public String getPrevioushash() {
        return previoushash;
    }

    public void setCurrentHash(String currentHash) {
        this.currentHash = currentHash;
    }

    public void setPrevioushash(String previoushash) {
        this.previoushash = previoushash;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

//    ---- Unused method ----
//    public String blockHashCode() {
//        return HashingUtils.hash(Block.genByteArr(this), "SHA-256");
//    }


    public String getDigitalSignature() {
        return digitalSignature;
    }

    public void setDigitalSignature(String digitalSignature) {
        this.digitalSignature = digitalSignature;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String blockHashCode(byte[] data, String prehash, String owner, long tmestamp) {
        return HashingUtils.hashBlock(
                data + prehash + owner + tmestamp,
                "SHA-256");
    }

    //    private static byte[] genByteArr(Block b) {
    private static byte[] genByteArr(List<Decade<String, String, String, String, String, String,String,String,String,String>> b) {
        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        ObjectOutputStream out;
        if (b != null) {
            try {
                out = new ObjectOutputStream(boas);
                out.writeObject(b);
                out.flush();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                return null;
            }
            return boas.toByteArray();
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "Current Hash:" + this.currentHash + "\nPrevious Hash:" + this.previoushash + "\n";
    }
}

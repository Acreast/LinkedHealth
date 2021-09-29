package Controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import Model.Block;
import Model.Record;

public class BlockChain {

    private static final String CHAIN_OBJFILE = "master/chainobj.dat";

    public static void persist(LinkedList<Block> chain) {

        try (FileOutputStream fos = new FileOutputStream(CHAIN_OBJFILE);
             ObjectOutputStream out = new ObjectOutputStream(fos)) {
            out.writeObject(chain);
        } catch (Exception e) {
        }

    }

    public static LinkedList<Block> get() {

        try (FileInputStream fis = new FileInputStream(CHAIN_OBJFILE);
             ObjectInputStream in = new ObjectInputStream(fis)) {
            return (LinkedList<Block>) in.readObject();
        } catch (Exception e) {
            return null;
        }

    }

    public static void distribute( String temp ){
        try {
            Files.write(Paths.get("recordblockchain.txt"), temp.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException ex) {
            Logger.getLogger(BlockChain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static List<Record> obtainPatientBlock(String patientID){
        SymmCrypto crypto = new SymmCrypto();
        File input = new File("recordblockchain.txt");
        try {
            String owner = patientID;
            if (input.length() == 0) {
                return null;
            }
            JsonElement fileElement = JsonParser.parseReader(new FileReader(input));
            JsonArray fileArray = fileElement.getAsJsonArray();
            if (fileArray.size()==0) {
                return null;
            }
            JsonObject fileObject = fileArray.get(0).getAsJsonObject();
            JsonObject data = fileObject.get("data").getAsJsonArray().get(0).getAsJsonObject();


            List<Record> ownerList = new ArrayList<>();

            for(int i = 0; i< fileArray.size();i++){
                if (fileArray.get(i).getAsJsonObject().get("owner").getAsString().equals(owner)) {

                    JsonObject recordObject = fileArray.get(i).getAsJsonObject();
                    JsonObject dataObject = recordObject.get("data").getAsJsonArray().get(0).getAsJsonObject();

                    ownerList.add(
                            new Record(
                                    dataObject.get("val0").getAsString(),
                                    dataObject.get("val1").getAsString(),
                                    dataObject.get("val2").getAsString(),
                                    dataObject.get("val3").getAsString(),
                                    dataObject.get("val4").getAsString(),
                                    dataObject.get("val5").getAsString(),
                                    dataObject.get("val6").getAsString(),
                                    dataObject.get("val7").getAsString(),
                                    dataObject.get("val8").getAsString(),
                                    dataObject.get("val9").getAsString(),
                                    recordObject.get("digitalSignature").getAsString(),
                                    recordObject.get("timestamp").getAsString(),
                                    recordObject.get("owner").getAsString()
                            )

                    );

                }
            }

            return ownerList;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static List<Record> obtainPastRecordBlock(String doctorID){
        File input = new File("recordblockchain.txt");
        try {
            String creator = doctorID;
            if (input.length() == 0) {
                return null;
            }
            JsonElement fileElement = JsonParser.parseReader(new FileReader(input));
            JsonArray fileArray = fileElement.getAsJsonArray();
            if (fileArray.size()==0) {
                return null;
            }
            JsonObject fileObject = fileArray.get(0).getAsJsonObject();
            JsonObject data = fileObject.get("data").getAsJsonArray().get(0).getAsJsonObject();


            List<Record> ownerList = new ArrayList<>();

            for(int i = 0; i< fileArray.size();i++){
                if (fileArray.get(i).getAsJsonObject().get("creator").getAsString().equals(creator)) {
                    JsonObject recordObject = fileArray.get(i).getAsJsonObject();
                    JsonObject dataObject = recordObject.get("data").getAsJsonArray().get(0).getAsJsonObject();

                    ownerList.add(
                            new Record(
                                    dataObject.get("val0").getAsString(),
                                    dataObject.get("val1").getAsString(),
                                    dataObject.get("val2").getAsString(),
                                    dataObject.get("val3").getAsString(),
                                    dataObject.get("val4").getAsString(),
                                    dataObject.get("val5").getAsString(),
                                    dataObject.get("val6").getAsString(),
                                    dataObject.get("val7").getAsString(),
                                    dataObject.get("val8").getAsString(),
                                    dataObject.get("val9").getAsString(),
                                    recordObject.get("digitalSignature").getAsString(),
                                    recordObject.get("timestamp").getAsString(),
                                    recordObject.get("owner").getAsString()
                            )

                    );

                }
            }

            return ownerList;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static List<Record> obtainPastRecordBlock(){
        File input = new File("recordblockchain.txt");
        try {
            if (input.length() == 0) {
                return null;
            }
            JsonElement fileElement = JsonParser.parseReader(new FileReader(input));
            JsonArray fileArray = fileElement.getAsJsonArray();
            if (fileArray.size()==0) {
                return null;
            }
            JsonObject fileObject = fileArray.get(0).getAsJsonObject();
            JsonObject data = fileObject.get("data").getAsJsonArray().get(0).getAsJsonObject();


            List<Record> ownerList = new ArrayList<>();

            for(int i = 0; i< fileArray.size();i++){
                    JsonObject recordObject = fileArray.get(i).getAsJsonObject();
                    JsonObject dataObject = recordObject.get("data").getAsJsonArray().get(0).getAsJsonObject();

                    ownerList.add(
                            new Record(
                                    dataObject.get("val0").getAsString(),
                                    dataObject.get("val1").getAsString(),
                                    dataObject.get("val2").getAsString(),
                                    dataObject.get("val3").getAsString(),
                                    dataObject.get("val4").getAsString(),
                                    dataObject.get("val5").getAsString(),
                                    dataObject.get("val6").getAsString(),
                                    dataObject.get("val7").getAsString(),
                                    dataObject.get("val8").getAsString(),
                                    dataObject.get("val9").getAsString(),
                                    recordObject.get("digitalSignature").getAsString(),
                                    recordObject.get("timestamp").getAsString(),
                                    recordObject.get("owner").getAsString()
                            )

                    );


            }

            return ownerList;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }



//                new Record(
//            crypto.decrypt(dataObject.get("val0").getAsString().replaceAll("\u003d", "="), encodedKey),
//            crypto.decrypt(dataObject.get("val1").getAsString().replaceAll("\u003d", "="), encodedKey),
//            crypto.decrypt(dataObject.get("val2").getAsString().replaceAll("\u003d", "="), encodedKey),
//            crypto.decrypt(dataObject.get("val3").getAsString().replaceAll("\u003d", "="), encodedKey),
//            crypto.decrypt(dataObject.get("val4").getAsString().replaceAll("\u003d", "="), encodedKey),
//            crypto.decrypt(dataObject.get("val5").getAsString().replaceAll("\u003d", "="), encodedKey),
//            crypto.decrypt(dataObject.get("val6").getAsString().replaceAll("\u003d", "="), encodedKey),
//            crypto.decrypt(dataObject.get("val7").getAsString().replaceAll("\u003d", "="), encodedKey),
//            crypto.decrypt(dataObject.get("val8").getAsString().replaceAll("\u003d", "="), encodedKey)
//            )
}

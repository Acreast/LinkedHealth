package Controller;

import Model.*;
import com.google.gson.GsonBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.layout.Region;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;


public class SQLDatabaseConnection {

    static LinkedList<Block> bchain = new LinkedList();

    static HashingUtils hs;
    String connectionUrl;

    public SQLDatabaseConnection() {
        //If azure
        connectionUrl = "jdbc:sqlserver://fyptp046490.database.windows.net:1433;database=LinkedCare;" +
                "user=admintp046490@fyptp046490;password={admin@tp046490};" +
                "encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;";

        //Local host database during development
        //connectionUrl = "jdbc:sqlserver://DESKTOP-6FG8RPU;DatabaseName=LinkedCare;IntegratedSecurity=true";
        hs = new HashingUtils();
    }

    public UserLogin loginUser(String username, String password){

        try (Connection con = DriverManager.getConnection(connectionUrl); Statement stmt = con.createStatement()) {
            String SQL = "SELECT * FROM RegisteredUser WHERE UserName= '" +username+ "'";
            ResultSet rs = stmt.executeQuery(SQL);

            //If user exists
            if(rs.next()){
                String salt = rs.getString("Salt");
                String hashedPw = rs.getString("HashedPassword");
                if (hashedPw.equals(hs.hash(password,Base64.getDecoder().decode(salt)))){
                    return new UserLogin
                    (
                            rs.getString("UserID"),
                            rs.getString("UserRealName"),
                            rs.getString("UserAddress"),
                            rs.getString("UserContactNumber"),
                            rs.getString("UserEmail"),
                            rs.getString("UserIC"),
                            rs.getString("UserGender"),
                            rs.getString("UserDOB"),
                            rs.getString("UserRole")
                    );
                } else {
                    System.out.println("Incorrect credentials");
                    return null;
                }
            } else{
                System.out.println("No such user");
                return null;
            }


        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setContentText("Possible reasons: Not connected to the internet, \nAzure server is no longer alive or" +
                    " IP address issue\nPlease use a local database if the problem persists.");
            alert.setHeaderText(null);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();

            return null;
        }

    }

    public void registerUser(String userName, String password, String realName, String address, String role, String gender, String DOB, String IC,String userContact, String email){
        try (Connection con = DriverManager.getConnection(connectionUrl); Statement stmt = con.createStatement()) {

            //Generate userID
            String userIDQuery = "SELECT TOP 1 UserID FROM dbo.RegisteredUser ORDER BY UserID DESC";
            ResultSet result = stmt.executeQuery(userIDQuery);
            int userNumber;
            String userID = null;
            if(result.next()){
                String lastUserID[] = result.getString("UserID").split("U");
                userNumber = Integer.valueOf(lastUserID[1]) + 1;
                if(userNumber< 10){
                    userID = "U000";
                } else if(userNumber <100){
                    userID = "U00";
                } else if(userNumber<1000){
                    userID = "U0";
                } else if(userNumber<10000){
                    userID = "U";
                }
                userID += userNumber;
            } else {
                userID = "U0001";
            }



            //Get salt and hash password
            byte[] salt = hs.generateSalt();
            String saltString = Base64.getEncoder().encodeToString(salt);
            String hashedPassword = hs.hash(password,salt);

            //Query
            String SQL = "INSERT INTO [dbo].[RegisteredUser]" +
                    "           ([UserID],[UserName],[HashedPassword],[Salt],[UserRole],[UserRealName],[UserDOB],[UserAddress],[UserGender],[UserContactNumber],[UserEmail],[UserIC])"
                    + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";

            PreparedStatement pstm = con.prepareStatement(SQL);
            pstm.setString(1,userID);
            pstm.setString(2,userName);
            pstm.setString(3,hashedPassword);
            pstm.setString(4,saltString);
            pstm.setString(5,role);
            pstm.setString(6,realName);
            pstm.setString(7,DOB);
            pstm.setString(8,address);
            pstm.setString(9,gender);
            pstm.setString(10,userContact);
            pstm.setString(11,email);
            pstm.setString(12,IC);
            pstm.executeUpdate();
            pstm.close();

            //-TODO set different role registration
            if(role.equals("Patient")){
                String patientID = "";
                String patientIDQuery = "SELECT TOP 1 PatientID FROM dbo.PatientUser ORDER BY PatientID DESC";
                Statement searchPatientStatement = con.createStatement();
                ResultSet patientResult = searchPatientStatement.executeQuery(patientIDQuery);
                int patientNumber;
                if(patientResult.next()){
                    String lastPatientID[] = patientResult.getString("PatientID").split("P");

                    patientNumber = Integer.valueOf(lastPatientID[1]) + 1;
                    if(patientNumber< 10){
                        patientID = "P000";
                    } else if(patientNumber <100){
                        patientID = "P00";
                    } else if(patientNumber<1000){
                        patientID = "P0";
                    } else if(patientNumber<10000){
                        patientID = "P";
                    }
                    patientID += patientNumber;
                } else {
                    patientID = "P0001";
                }
                SymmCrypto sc = new SymmCrypto();
                String insertQuery = "INSERT INTO [dbo].[PatientUser]" +
                        "([PatientID], [UserID] ,[PatientKey],[PatientTransparency],[PatientHeight],[PatientWeight])" +
                        "VALUES(?,?,?,?,?,?)";
                PreparedStatement statement = con.prepareStatement(insertQuery);
                statement.setString(1,patientID);
                statement.setString(2,userID);
                statement.setString(3,sc.getKey());
                statement.setString(4,"false,false,true,true,true,false,false,true,false");
                statement.setFloat(5,0.0f);
                statement.setFloat(6,0.0f);
                statement.executeUpdate();
                statement.close();



            }else if (role.equals("Doctor")){
                String doctorID = "";
                Statement searchDoctorStatement = con.createStatement();
                String doctorIDQuery = "SELECT TOP 1 DoctorID FROM dbo.DoctorUser ORDER BY DoctorID DESC";
                ResultSet doctorResult = searchDoctorStatement.executeQuery(doctorIDQuery);
                int doctorNumber;
                if(doctorResult.next()){
                    String lastPatientID[] = doctorResult.getString("DoctorID").split("D");
                    doctorNumber = Integer.valueOf(lastPatientID[1]) + 1;
                    if(doctorNumber< 10){
                        doctorID = "D000";
                    } else if(doctorNumber <100){
                        doctorID = "D00";
                    } else if(doctorNumber<1000){
                        doctorID = "D0";
                    } else if(doctorNumber<10000){
                        doctorID = "D";
                    }
                    doctorID += doctorNumber;
                } else{
                    doctorID = "D0001";
                }

                DigitalSignature ds = new DigitalSignature();
                String[] keyPair = ds.getKeyPair();
                String insertQuery = "INSERT INTO [dbo].[DoctorUser]" +
                        "           ([DoctorID],[UserID],[PrivateKey],[PublicKey])" +
                        "     VALUES(?,?,?,?)";
                PreparedStatement statement = con.prepareStatement(insertQuery);
                statement.setString(1,doctorID);
                statement.setString(2,userID);
                statement.setString(3,keyPair[0]);
                statement.setString(4,keyPair[1]);
                statement.executeUpdate();
                statement.close();

            }



        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setContentText("Possible reasons: Not connected to the internet, \nAzure server is no longer alive or" +
                    " IP address issue\nPlease use a local database if the problem persists.");
            alert.setHeaderText(null);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();

            return;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    public void changePassword(String userID, String oldPassword,String newPassword){
        String SQL = "  SELECT HashedPassword,Salt from dbo.RegisteredUser WHERE UserID = '"+userID+"'";
        try (Connection con = DriverManager.getConnection(connectionUrl); Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(SQL);
            if (rs.next()){
                String salt = rs.getString("Salt");
                String hashedPw = rs.getString("HashedPassword");
                if(hashedPw.equals(hs.hash(oldPassword,Base64.getDecoder().decode(salt)))){
                    byte[] newSalt = hs.generateSalt();
                    String saltString = Base64.getEncoder().encodeToString(newSalt);
                    String hashedPassword = hs.hash(newPassword,newSalt);
                    String updatePassword = "UPDATE dbo.RegisteredUser SET HashedPassword = '"+hashedPassword+"', Salt = '"+saltString+"' WHERE UserID = ?";
                    PreparedStatement pstm = con.prepareStatement(updatePassword);
                    pstm.setString(1,userID);
                    pstm.executeUpdate();
                    pstm.close();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Password change");
                    alert.setHeaderText(null);
                    alert.setContentText("The password has been updated");
                    alert.showAndWait();

                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Password Incorrect");
                    alert.setHeaderText(null);
                    alert.setContentText("Old password incorrect");
                    alert.showAndWait();
                    return;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setContentText("Possible reasons: Not connected to the internet, \nAzure server is no longer alive or" +
                    " IP address issue\nPlease use a local database if the problem persists.");
            alert.setHeaderText(null);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();

            return;
        }

    }

    public boolean checkAvailability(String username){
        try (Connection con = DriverManager.getConnection(connectionUrl); Statement stmt = con.createStatement();) {
            String SQL = "SELECT UserName FROM RegisteredUser WHERE UserName= '" +username+ "'";
            ResultSet rs = stmt.executeQuery(SQL);
            if(rs.next()){
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setContentText("Possible reasons: Not connected to the internet, \nAzure server is no longer alive or" +
                    " IP address issue\nPlease use a local database if the problem persists.");
            alert.setHeaderText(null);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
            return false;

        }
        return true;
    }

    public UserLogin getUserByUserID(String userID){
        String SQL = "SELECT [UserRole],[UserRealName],[UserDOB],[UserAddress],[UserGender],[UserContactNumber]," +
                "[UserEmail],[UserIC] FROM [dbo].[RegisteredUser] WHERE UserID = '"+userID+"'";
        try (Connection con = DriverManager.getConnection(connectionUrl); Statement stmt = con.createStatement();) {
            ResultSet rs= stmt.executeQuery(SQL);

            if(rs.next()){
                return new UserLogin(
                        userID,
                        rs.getString("UserRealName"),
                        rs.getString("UserAddress"),
                        rs.getString("UserContactNumber"),
                        rs.getString("UserEmail"),
                        rs.getString("UserIC"),
                        rs.getString("UserGender"),
                        rs.getString("UserDOB"),
                        rs.getString("UserRole")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setContentText("Possible reasons: Not connected to the internet, \nAzure server is no longer alive or" +
                    " IP address issue\nPlease use a local database if the problem persists.");
            alert.setHeaderText(null);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();

        }
        return null;
    }

    public Patient getPatientByUserID(String userID){

        try (Connection con = DriverManager.getConnection(connectionUrl); Statement stmt = con.createStatement();) {
            String SQL = "SELECT * FROM PatientUser WHERE UserID= '" +userID+ "'";
            ResultSet rs = stmt.executeQuery(SQL);

            //If patient exists
            if(rs.next()){
                    return new Patient
                            (
                                    rs.getString("UserID"),
                                    rs.getString("PatientID"),
                                    rs.getString("PatientAllergy"),
                                    rs.getString("PatientRemarks"),
                                    rs.getString("PatientMartialStatus"),
                                    rs.getString("PatientKey"),
                                    rs.getString("PatientTransparency"),
                                    rs.getString("MainInsurance"),
                                    rs.getString("PatientBloodType"),
                                    rs.getString("PatientWeight"),
                                    rs.getString("PatientHeight")
                            );
            }


        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Patient getPatientByPatientID(String patientID){
        try (Connection con = DriverManager.getConnection(connectionUrl); Statement stmt = con.createStatement();) {
            String SQL = "SELECT * FROM PatientUser WHERE PatientID= '" +patientID+ "'";
            ResultSet rs = stmt.executeQuery(SQL);

            //If patient exists
            if(rs.next()){
                return new Patient
                        (
                                rs.getString("UserID"),
                                rs.getString("PatientID"),
                                rs.getString("PatientAllergy"),
                                rs.getString("PatientRemarks"),
                                rs.getString("PatientMartialStatus"),
                                rs.getString("PatientKey"),
                                rs.getString("PatientTransparency"),
                                rs.getString("MainInsurance"),
                                rs.getString("PatientBloodType"),
                                rs.getString("PatientWeight"),
                                rs.getString("PatientHeight")
                        );
            }


        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getDoctorID(String userID){
        try (Connection con = DriverManager.getConnection(connectionUrl); Statement stmt = con.createStatement();) {
            String SQL = "SELECT DoctorID FROM DoctorUser WHERE userID= '" +userID+ "'";
            ResultSet rs = stmt.executeQuery(SQL);

            if(rs.next()){
                return rs.getString("DoctorID");
            }


        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setContentText("Possible reasons: Not connected to the internet, \nAzure server is no longer alive or" +
                    " IP address issue\nPlease use a local database if the problem persists.");
            alert.setHeaderText(null);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();

        }
        return null;

    }

    public Doctor getDoctorByDoctorID(String doctorID){
        try (Connection con = DriverManager.getConnection(connectionUrl); Statement stmt = con.createStatement();) {
            String SQL = "SELECT * FROM DoctorUser WHERE doctorID= '" +doctorID+ "'";
            ResultSet rs = stmt.executeQuery(SQL);

            if(rs.next()){
                return new Doctor(
                        rs.getString("DoctorID"),
                        rs.getString("UserID"),
                        rs.getString("PrivateKey"),
                        rs.getString("PublicKey")
                );
            }


        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setContentText("Possible reasons: Not connected to the internet, \nAzure server is no longer alive or" +
                    " IP address issue\nPlease use a local database if the problem persists.");
            alert.setHeaderText(null);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();

        }
        return null;
    }

    public void patientUpdateProfile(String userID, String patientID, String name, String DOB, String martialStatus, String contactNumber, String email, String address){
        String patientTableQuery = "UPDATE [dbo].[PatientUser]\n" +
                "SET [PatientMartialStatus] = ?\n" +
                "WHERE PatientID = ?";
        String userTableQuery =  "UPDATE [dbo].[RegisteredUser]\n" +
                "SET [UserRealName] = ?,[UserDOB] = ?,[UserAddress] = ?,[UserContactNumber] = ?,[UserEmail] = ?\n" +
                "WHERE UserID = ?";

        try(Connection con = DriverManager.getConnection(connectionUrl);){
            //Update user table
            PreparedStatement firstUpdate = con.prepareStatement(userTableQuery);
            firstUpdate.setString(1,name);
            firstUpdate.setString(2,DOB);
            firstUpdate.setString(3,address);
            firstUpdate.setString(4,contactNumber);
            firstUpdate.setString(5,email);
            firstUpdate.setString(6,userID);
            firstUpdate.executeUpdate();
            firstUpdate.close();

            PreparedStatement secondUpdate = con.prepareStatement(patientTableQuery);
            secondUpdate.setString(1,martialStatus);
            secondUpdate.setString(2,patientID);
            secondUpdate.executeUpdate();
            secondUpdate.close();

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setContentText("Possible reasons: Not connected to the internet, \nAzure server is no longer alive or" +
                    " IP address issue\nPlease use a local database if the problem persists.");
            alert.setHeaderText(null);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();

            return;
        }


    }

    public void generalUpdateProfile(String userID, String name,String DOB,String contactNumber,String email,String address){
        String userTableQuery =  "UPDATE [dbo].[RegisteredUser]\n" +
                "SET [UserRealName] = ?,[UserDOB] = ?,[UserAddress] = ?,[UserContactNumber] = ?,[UserEmail] = ?\n" +
                "WHERE UserID = ?";
        try(Connection con = DriverManager.getConnection(connectionUrl);) {
            //Update user table
            PreparedStatement pstm = con.prepareStatement(userTableQuery);
            pstm.setString(1, name);
            pstm.setString(2, DOB);
            pstm.setString(3, address);
            pstm.setString(4, contactNumber);
            pstm.setString(5, email);
            pstm.setString(6, userID);
            pstm.executeUpdate();
            pstm.close();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setContentText("Possible reasons: Not connected to the internet, \nAzure server is no longer alive or" +
                    " IP address issue\nPlease use a local database if the problem persists.");
            alert.setHeaderText(null);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();

            return;
        }
    }

    public byte[] getProfileImage(String patientID){
        try(Connection con = DriverManager.getConnection(connectionUrl);){
            String patientIDQuery = "SELECT PatientImage FROM dbo.PatientUser WHERE PatientID='" + patientID + "'";
            Statement stmt = con.createStatement();
            ResultSet result = stmt.executeQuery(patientIDQuery);
            if(result.next()){
                return result.getBytes("PatientImage");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setContentText("Possible reasons: Not connected to the internet, \nAzure server is no longer alive or" +
                    " IP address issue\nPlease use a local database if the problem persists.");
            alert.setHeaderText(null);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();

        }
        return null;
    }

    public void patientUpdateProfileImage(String patientID,byte[] imageByte){
        String patientTableQuery = "UPDATE [dbo].[PatientUser]\n" +
                "SET [PatientImage] = ?\n" +
                "WHERE PatientID = ?";
        try(Connection con = DriverManager.getConnection(connectionUrl);){
            PreparedStatement secondUpdate = con.prepareStatement(patientTableQuery);
            secondUpdate.setBytes(1,imageByte);
            secondUpdate.setString(2,patientID);
            secondUpdate.executeUpdate();
            secondUpdate.close();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setContentText("Possible reasons: Not connected to the internet, \nAzure server is no longer alive or" +
                    " IP address issue\nPlease use a local database if the problem persists.");
            alert.setHeaderText(null);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();

            return;
        }

    }

    public void uploadAttachment(String patientID, String timestamp, byte[] fileByte,String fileName,String notes){
        String SQL = "INSERT INTO [dbo].[Attachment]\n" +
                "           ([PatientID],[TimeStampOfCreation],[AttachmentByte],[AttachmentFileName],[AttachmentNotes])\n" +
                "     VALUES(?,?,?,?,?)";
        try(Connection con = DriverManager.getConnection(connectionUrl);){
            PreparedStatement pstm = con.prepareStatement(SQL);
            pstm.setString(1,patientID);
            pstm.setString(2,timestamp);
            pstm.setBytes(3,fileByte);
            pstm.setString(4,fileName);
            pstm.setString(5,notes);
            pstm.executeUpdate();
            pstm.close();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setContentText("Possible reasons: Not connected to the internet, \nAzure server is no longer alive or" +
                    " IP address issue\nPlease use a local database if the problem persists.");
            alert.setHeaderText(null);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();

            return;
        }
    }

    public void retrieveAttachment(String patientID,String timestamp,String fileName){
        try(Connection con = DriverManager.getConnection(connectionUrl);){
            String patientIDQuery = "SELECT AttachmentByte FROM dbo.Attachment WHERE PatientID='" + patientID + "' " +
                    "AND TimeStampOfCreation = '"+timestamp+"' AND AttachmentFileName = '"+fileName+"'";
            Statement stmt = con.createStatement();
            ResultSet result = stmt.executeQuery(patientIDQuery);
            if(result.next()){
                byte[] attachmentByte = result.getBytes("AttachmentByte");
                try (FileOutputStream fos = new FileOutputStream("downloads/" + fileName)) {
                    fos.write(attachmentByte);
                } catch (IOException e) {
                    e.printStackTrace();
                    downloadAttachmentFailedAlert();
                    return;
                }
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Download complete");
                alert.setHeaderText(null);
                alert.setContentText("Attachment retrieved, please check your download folder in this system's download folder");
                alert.showAndWait();
            } else{
                downloadAttachmentFailedAlert();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            downloadAttachmentFailedAlert();
            return;
        }

    }

    public void downloadAttachmentFailedAlert(){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setTitle("An error has occured");
        alert.setContentText("Failed to download attachment, the file might have been removed from the database/ database connection error.");
        alert.showAndWait();
    }

    public boolean updateTransparency(String patientID, String settings){
        String patientTableQuery = "UPDATE [dbo].[PatientUser]\n" +
                "SET [PatientTransparency] = ?\n" +
                "WHERE PatientID = ?";
        try(Connection con = DriverManager.getConnection(connectionUrl);){
            PreparedStatement update = con.prepareStatement(patientTableQuery);
            update.setString(1,settings);
            update.setString(2,patientID);
            update.executeUpdate();
            update.close();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public ObservableList<PatientConnectionTableModel> obtainPatientConnectionList(String userID,String idType){

        String SQL = "";
        if(idType.equals("DoctorID")){
            SQL =
                    "SELECT P.PatientID, P.DoctorID, P.ApplicationDate, P.ApplicationStatus, U.UserRealName\n" +
                            "FROM PatientDoctorConnection P\n" +
                            "LEFT JOIN PatientUser D\n" +
                            "ON P.PatientID = D.PatientID\n" +
                            "LEFT JOIN RegisteredUser U\n" +
                            "ON D.UserID = U.UserID\n" +
                            "WHERE DoctorID = '"+ userID+"' ";

        } else if(idType.equals(("PatientID"))) {
            SQL =
                    "  SELECT P.PatientID, P.DoctorID, P.ApplicationDate, P.ApplicationStatus, U.UserRealName\n" +
                            "  FROM PatientDoctorConnection P \n" +
                            "  LEFT JOIN DoctorUser D\n" +
                            "  ON D.DoctorID = P.DoctorID\n" +
                            "  LEFT JOIN RegisteredUser U\n" +
                            "  ON D.UserID = U.UserID" +
                            "  WHERE PatientID = '"+userID+"'";
        }


        ObservableList<PatientConnectionTableModel> list = FXCollections.observableArrayList();
        try (Connection con = DriverManager.getConnection(connectionUrl); Statement stmt = con.createStatement();) {
            ResultSet rs = stmt.executeQuery(SQL);
            String obtainID = "";
            if (idType.equals("PatientID")){
                obtainID = "DoctorID";
            } else if(idType.equals("DoctorID")){
                obtainID = "PatientID";
            }
            while (rs.next()){
                list.add(new PatientConnectionTableModel(
                        rs.getString(obtainID),
                        rs.getString("UserRealName"),
                        rs.getString("ApplicationDate"),
                        rs.getString("ApplicationStatus")
                ));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setContentText("Possible reasons: Not connected to the internet, \nAzure server is no longer alive or" +
                    " IP address issue\nPlease use a local database if the problem persists.");
            alert.setHeaderText(null);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();

        }

        return list;
    }

    public List<OngoingMedicationTableModel> obtainOngoingMedication(String patientID){
        String SQL = "  SELECT * FROM MedicationRecord WHERE TakeUntil > GETDATE() AND PatientID = '"+patientID+"'";
        List<OngoingMedicationTableModel> ongoingMedicationList = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(connectionUrl); Statement stmt = con.createStatement();) {

            ResultSet rs = stmt.executeQuery(SQL);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                while (rs.next()){
                    ongoingMedicationList.add(new OngoingMedicationTableModel(
                            rs.getString("MedicationName"),
                            rs.getString("TakeUntil"),
                            rs.getString("Dose"),
                            sdf.format(new Date(Long.parseLong(rs.getString("TimeStampOfCreation")))),
                            rs.getString("DoctorID"),
                            rs.getString("MedicationNotes")
                    ));
                }

        } catch (SQLException throwables) {
            throwables.printStackTrace();

        }
        return ongoingMedicationList;
    }

    public boolean applyConnection(String userID, String doctorID){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String patientID = "";



        String searchPatientID = "Select PatientID from PatientUser WHERE UserID = '"+userID+"' ";

        try(Connection con = DriverManager.getConnection(connectionUrl);){
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(searchPatientID);
            if(rs.next()){
                patientID = rs.getString("PatientID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setContentText("Possible reasons: Not connected to the internet, \nAzure server is no longer alive or" +
                    " IP address issue\nPlease use a local database if the problem persists.");
            alert.setHeaderText(null);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();

        }


        String searchExistingConnection = "SELECT * from PatientDoctorConnection WHERE PatientID = '"+ patientID+"' AND DoctorID = '"+doctorID+"'";

        try(Connection con = DriverManager.getConnection(connectionUrl);){
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(searchExistingConnection);
            if(rs.next()){


                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setContentText("Possible reasons: Not connected to the internet, \nAzure server is no longer alive or" +
                    " IP address issue\nPlease use a local database if the problem persists.");
            alert.setHeaderText(null);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();

        }


        String SQL = "INSERT INTO [dbo].[PatientDoctorConnection]\n" +
                "           ([PatientID],[DoctorID],[ApplicationDate],[ApplicationStatus])\n" +
                "     VALUES(?,?,?,?)";
        try(Connection con = DriverManager.getConnection(connectionUrl);){
            PreparedStatement insertConnection = con.prepareStatement(SQL);
            insertConnection.setString(1,patientID);
            insertConnection.setString(2,doctorID);
            insertConnection.setString(3,sdf.format(date));
            insertConnection.setString(4,"Pending");
            insertConnection.executeUpdate();
            insertConnection.close();
            return true;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }

    }

    public boolean cancelConnectionRequest(String patientID, String doctorID){

        String SQL = "DELETE FROM dbo.PatientDoctorConnection WHERE PatientID = '"+patientID+"' AND DoctorID = '"+doctorID+"'" ;
        try(Connection con = DriverManager.getConnection(connectionUrl);){
            Statement stmt = con.createStatement();
            stmt.executeUpdate(SQL);
            stmt.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setContentText("Possible reasons: Not connected to the internet, \nAzure server is no longer alive or" +
                    " IP address issue\nPlease use a local database if the problem persists.");
            alert.setHeaderText(null);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();

        }

        return false;

    }

    public boolean updateConnection(String patientID, String doctorID, String action){
        String patientTableQuery = "UPDATE [dbo].[PatientDoctorConnection]\n" +
                "SET [ApplicationStatus] = '"+action+"'\n" +
                "WHERE PatientID = ? AND DoctorID = ?";
        try(Connection con = DriverManager.getConnection(connectionUrl);){
            PreparedStatement secondUpdate = con.prepareStatement(patientTableQuery);
            secondUpdate.setString(1,patientID);
            secondUpdate.setString(2,doctorID);
            secondUpdate.executeUpdate();
            secondUpdate.close();
            return true;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public boolean updatePatientPhysical(String patientID, String allergies,String otherRemarks,String bloodType,Float weight,
                                         Float height, String mainInsurance){
        String SQL = "UPDATE dbo.PatientUser SET PatientAllergy = '"+allergies+"',PatientRemarks = '"+otherRemarks+"',MainInsurance='"+mainInsurance+"',PatientBloodType='"+bloodType+"',PatientWeight="+weight+",PatientHeight="+height+"" +
                "WHERE PatientID = ?";
        try(Connection con = DriverManager.getConnection(connectionUrl);){
            PreparedStatement pstm = con.prepareStatement(SQL);
            pstm.setString(1,patientID);
            pstm.executeUpdate();
            pstm.close();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setContentText("Possible reasons: Not connected to the internet, \nAzure server is no longer alive or" +
                    " IP address issue\nPlease use a local database if the problem persists.");
            alert.setHeaderText(null);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();

            return false;
        }
        return true;
    }

    public UserLogin findPatientByIC(String patientIC){
        String SQL = "Select * from RegisteredUser WHERE UserIC = '"+patientIC+"' ";
        try (Connection con = DriverManager.getConnection(connectionUrl); Statement stmt = con.createStatement();) {
            ResultSet rs = stmt.executeQuery(SQL);
            if(rs.next()){
                return new UserLogin(
                        rs.getString("UserID"),
                        rs.getString("UserRealName"),
                        rs.getString("UserAddress"),
                        rs.getString("UserContactNumber"),
                        rs.getString("UserEmail"),
                        rs.getString("UserIC"),
                        rs.getString("UserGender"),
                        rs.getString("UserDOB"),
                        rs.getString("UserRole")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setContentText("Possible reasons: Not connected to the internet, \nAzure server is no longer alive or" +
                    " IP address issue\nPlease use a local database if the problem persists.");
            alert.setHeaderText(null);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();

        }
        return null;
    }

    public List<DiagnosisListTableModel> getDiagnosisList(){
        List<DiagnosisListTableModel> list = new ArrayList<>();
        String SQL = "SELECT * FROM Diagnosis";
        try (Connection con = DriverManager.getConnection(connectionUrl); Statement stmt = con.createStatement();) {
            ResultSet rs = stmt.executeQuery(SQL);
            if(rs.next()){
                while (rs.next()){
                    list.add(
                            new DiagnosisListTableModel(
                                    rs.getString("DiagnosisName"),rs.getString("DiagnosisDescription")
                            )
                    );
                }
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setContentText("Possible reasons: Not connected to the internet, \nAzure server is no longer alive or" +
                    " IP address issue\nPlease use a local database if the problem persists.");
            alert.setHeaderText(null);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();

        }
        return list;
    }

    public List<ExaminationDialogTableModel> getExaminationList(){
        List<ExaminationDialogTableModel> list = new ArrayList<>();
        String SQL = "SELECT * FROM Examination";
        try (Connection con = DriverManager.getConnection(connectionUrl); Statement stmt = con.createStatement();) {
            ResultSet rs = stmt.executeQuery(SQL);
            if(rs.next()){
                while (rs.next()){
                    list.add(
                            new ExaminationDialogTableModel(
                                    false,rs.getString("BodyComponent"),rs.getString("ExaminationDefault"),"-"
                            )
                    );
                }
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setContentText("Possible reasons: Not connected to the internet, \nAzure server is no longer alive or" +
                    " IP address issue\nPlease use a local database if the problem persists.");
            alert.setHeaderText(null);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();

        }
        return list;
    }

    public List<MedicationListTableModel> getMedicationList(){
        List<MedicationListTableModel> list = new ArrayList<>();
        String SQL = "SELECT * FROM Medication";
        try (Connection con = DriverManager.getConnection(connectionUrl); Statement stmt = con.createStatement();) {
            ResultSet rs = stmt.executeQuery(SQL);
            if(rs.next()){
                while (rs.next()){
                    list.add(
                            new MedicationListTableModel(
                                    rs.getString("MedicationName"),rs.getString("MedicationDescription")
                            )
                    );
                }
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setContentText("Possible reasons: Not connected to the internet, \nAzure server is no longer alive or" +
                    " IP address issue\nPlease use a local database if the problem persists.");
            alert.setHeaderText(null);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();

        }
        return list;
    }

    public boolean appendRecord(List<String> data,String patientID,String doctorID,ObservableList<MedicationTableModel> medicationOList,ObservableList<AttachmentTableModel> attachmentOList){


        SymmCrypto sc = new SymmCrypto();
        DigitalSignature ds = new DigitalSignature();
        Patient p = getPatientByPatientID(patientID);

        //Encryption of data
        String patientKey = p.getKey();
        String recordInformation = sc.encrypt(data.get(0),patientKey);
        String vitals = data.get(1);
        String allergies = data.get(2);
        String otherRemarks = sc.encrypt(data.get(3), patientKey);
        String reasonOfVisit = sc.encrypt(data.get(4), patientKey);
        String examination = data.get(5);
        String diagnosis = data.get(6);
        String medication = data.get(7);
        String additionalNotes = sc.encrypt(data.get(8),patientKey);
        String attachments = sc.encrypt(data.get(9),patientKey);
        Doctor d = getDoctorByDoctorID(doctorID);

        //To generate digital signature
        String digitalSignature;
        String combinedString = recordInformation + vitals + allergies + otherRemarks + reasonOfVisit + examination
                + diagnosis + medication + additionalNotes + attachments;

        String doctorPrivateKey = d.getPrivateKey();

        digitalSignature = ds.encrypt(ds.hash(combinedString),doctorPrivateKey);


        List<String> recordData = new ArrayList<>();
        recordData.add(recordInformation);
        recordData.add(vitals);
        recordData.add(allergies);
        recordData.add(otherRemarks);
        recordData.add(reasonOfVisit);
        recordData.add(examination);
        recordData.add(diagnosis);
        recordData.add(medication);
        recordData.add(additionalNotes);
        recordData.add(attachments);

        List<List<String>> encryptedBlockData = new ArrayList();
        encryptedBlockData.add(recordData);

        Long timestamp = Calendar.getInstance().getTimeInMillis();



        if(!addMedicationRecord(medicationOList,patientID,doctorID,timestamp.toString())){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("An error has occurred");
            alert.setHeaderText(null);
            alert.setContentText("Error with creating new medication record, medication record not added.");
            alert.showAndWait();
        }
        if(!uploadAttachments(attachmentOList,patientID,timestamp.toString())){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("An error has occurred");
            alert.setHeaderText(null);
            alert.setContentText("Error with uploading attachments.");
            alert.showAndWait();
        }





        if(new File("recordblockchain.txt").length() < 6){
            //genesis block
            Block b1 = new Block(encryptedBlockData, "0",patientID,digitalSignature,timestamp,doctorID);
            bchain.add(b1);
            BlockChain.persist(bchain);
            out(bchain);
            return true;
        } else {
            bchain = BlockChain.get();
            Block block = new Block(encryptedBlockData, bchain.getLast().getCurrentHash(),patientID, digitalSignature,timestamp,doctorID);
            bchain.add(block);
            BlockChain.persist(bchain);
            out(bchain);
            return true;
        }



    }

    public boolean addMedicationRecord(ObservableList<MedicationTableModel> medicationOList,String patientID,String doctorID,String timestamp){
        String SQL = "INSERT INTO [dbo].[MedicationRecord]\n" +
                "           ([PatientID],[MedicationName],[Dose],[TakeUntil],[MedicationNotes],[TimeStampOfCreation],[DoctorID])\n" +
                "     VALUES(?,?,?,?,?,?,?)";

        List<MedicationTableModel> medicationList = medicationOList.stream().collect(Collectors.toList());
        try(Connection con = DriverManager.getConnection(connectionUrl);){
            medicationList.forEach(e->{
                try {
                    PreparedStatement insertMedicationRecord = con.prepareStatement(SQL);
                    insertMedicationRecord.setString(1,patientID);
                    insertMedicationRecord.setString(2,e.medicationNameProperty().getValue());
                    insertMedicationRecord.setString(3,e.doseProperty().getValue());
                    insertMedicationRecord.setString(4,e.dateProperty().getValue());
                    insertMedicationRecord.setString(5,e.notesProperty().getValue());
                    insertMedicationRecord.setString(6,timestamp);
                    insertMedicationRecord.setString(7,doctorID);
                    insertMedicationRecord.executeUpdate();
                    insertMedicationRecord.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean uploadAttachments(ObservableList<AttachmentTableModel> attachmentOList,String patientID,String timeStamp){
        List<AttachmentTableModel> attachmentList = new ArrayList<>();
        attachmentList = attachmentOList.stream().collect(Collectors.toList());
        attachmentList.forEach(e->{

            File f = new File(e.filePathProperty().getValue());
            if(f!=null){
                byte[] fileByte = new byte[(int)f.length()];
                try {
                    FileInputStream fis = new FileInputStream(f);
                    fis.read(fileByte);
                    uploadAttachment(patientID,timeStamp,fileByte,f.getName(),e.notesProperty().getValue());

                } catch (FileNotFoundException ex) {

                    ex.printStackTrace();
                    uploadFailedAlert();

                } catch (IOException ex) {
                    ex.printStackTrace();
                    uploadFailedAlert();
                }
            }

        });
        return true;
    }

    public void uploadFailedAlert(){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("An error has occurred");
        alert.setHeaderText(null);
        alert.setContentText("Error with uploading attachments.");
        alert.showAndWait();
    }


    //supposedly the mining function for miners
    public static void out(LinkedList<Block> bchain){
        String temp = new GsonBuilder().setPrettyPrinting().create().toJson(bchain);
        BlockChain.distribute(temp);
    }







}

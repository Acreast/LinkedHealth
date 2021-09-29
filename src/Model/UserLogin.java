package Model;

public class UserLogin {



    private String userID, name, userAddress, userContactNumber, userEmail, userIC, gender, DOB, userRole;



    public UserLogin(String userID, String name, String userAddress, String userContactNumber, String userEmail, String userIC, String gender, String DOB, String userRole) {
        this.userID = userID;
        this.name = name;
        this.userAddress = userAddress;
        this.userContactNumber = userContactNumber;
        this.userEmail = userEmail;
        this.userIC = userIC;
        this.gender = gender;
        this.DOB = DOB;
        this.userRole = userRole;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserContactNumber() {
        return userContactNumber;
    }

    public void setUserContactNumber(String userContactNumber) {
        this.userContactNumber = userContactNumber;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserIC() {
        return userIC;
    }

    public void setUserIC(String userIC) {
        this.userIC = userIC;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    @Override
    public String toString() {
        return "userLogin{" +
                "name='" + name + '\'' +
                ", userAddress='" + userAddress + '\'' +
                ", userContactNumber='" + userContactNumber + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", userIC='" + userIC + '\'' +
                ", gender='" + gender + '\'' +
                ", DOB='" + DOB + '\'' +
                ", userRole='" + userRole + '\'' +
                '}';
    }
}

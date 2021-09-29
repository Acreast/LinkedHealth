package Model;

public class Patient {

    private String userID, patientID, allergy, remark, martialStatus, key, patientTransparency,mainInsurance,bloodType;
    private Float weight, height;

    public Patient(String userID, String patientID, String allergy, String remark, String martialStatus, String key, String patientTransparency, String mainInsurance, String bloodType, String weight, String height) {
        this.userID = userID;
        this.patientID = patientID;
        this.allergy = allergy;
        this.remark = remark;
        this.martialStatus = martialStatus;
        this.key = key;
        this.patientTransparency = patientTransparency;
        this.mainInsurance = mainInsurance;
        this.bloodType = bloodType;
        if (weight.equals("") || weight == null) {
            this.weight = 0.0f;
        } else{
            this.weight = Float.parseFloat(weight);
        }
        if (height.equals("") || height == null) {
            this.height = 0.0f;
        } else{
            this.height = Float.parseFloat(height);
        }

    }

    public String getUserID() {
        return userID;
    }

    public String getPatientID() {
        return patientID;
    }


    public String getAllergy() {
        return allergy;
    }

    public void setAllergy(String allergy) {
        this.allergy = allergy;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getMartialStatus() {
        return martialStatus;
    }

    public void setMartialStatus(String martialStatus) {
        this.martialStatus = martialStatus;
    }

    public String getKey() {
        return key;
    }


    public String getPatientTransparency() {
        return patientTransparency;
    }

    public void setPatientTransparency(String patientTransparency) {
        this.patientTransparency = patientTransparency;
    }

    public String getMainInsurance() {
        return mainInsurance;
    }

    public void setMainInsurance(String mainInsurance) {
        this.mainInsurance = mainInsurance;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "userID='" + userID + '\'' +
                ", patientID='" + patientID + '\'' +
                ", allergy='" + allergy + '\'' +
                ", remark='" + remark + '\'' +
                ", martialStatus='" + martialStatus + '\'' +
                ", key='" + key + '\'' +
                ", patientTransparency='" + patientTransparency + '\'' +
                ", mainInsurance='" + mainInsurance + '\'' +
                ", bloodType='" + bloodType + '\'' +
                ", weight=" + weight +
                ", height=" + height +
                '}';
    }
}

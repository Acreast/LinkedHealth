package Model;


import Controller.SQLDatabaseConnection;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Record {

    private String digitalSignature;
    private String publicKey;
    private String generalInformation;
    private String vitals,allergies,bloodType,otherRemarks;
    private String doctorID,patientID;
    private String timeStamp;
    private String reasonOfVisit,examination,diagnosis,medication,notes,attachments;
    private String date;

    public Record(String generalInformation, String vitals, String allergies, String otherRemarks, String reasonOfVisit
    ,String examination,String diagnosis,String medication,String additionalNotes,String attachments,String digitalSignature
    ,String timeStamp,String patientID){
        this.digitalSignature = digitalSignature;
        this.generalInformation = generalInformation;
        this.vitals = vitals;
        this.allergies = allergies;
        this.otherRemarks = otherRemarks;
        this.reasonOfVisit = reasonOfVisit;
        this.examination = examination;
        this.diagnosis = diagnosis;
        this.medication = medication;
        this.notes = additionalNotes;
        this.attachments = attachments;
        this.timeStamp = timeStamp;
        this.patientID = patientID;

        Date dateFromTimestamp = new Date(Long.parseLong(timeStamp));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        this.date = sdf.format(dateFromTimestamp);


    }

    public String getDigitalSignature() {
        return digitalSignature;
    }

    public void setDigitalSignature(String digitalSignature) {
        this.digitalSignature = digitalSignature;
    }

    public String getPublicKey(String doctorID) {
        SQLDatabaseConnection sc = new SQLDatabaseConnection();
        Doctor d = sc.getDoctorByDoctorID(doctorID);
        return d.getPublicKey();
    }


    public String getGeneralInformation() {
        return generalInformation;
    }

    public void setGeneralInformation(String generalInformation) {
        this.generalInformation = generalInformation;
    }

    public String getVitals() {
        return vitals;
    }

    public void setVitals(String vitals) {
        this.vitals = vitals;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getOtherRemarks() {
        return otherRemarks;
    }

    public void setOtherRemarks(String otherRemarks) {
        this.otherRemarks = otherRemarks;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getReasonOfVisit() {
        return reasonOfVisit;
    }

    public void setReasonOfVisit(String reasonOfVisit) {
        this.reasonOfVisit = reasonOfVisit;
    }

    public String getExamination() {
        return examination;
    }

    public void setExamination(String examination) {
        this.examination = examination;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getMedication() {
        return medication;
    }

    public void setMedication(String medication) {
        this.medication = medication;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getAttachments() {
        return attachments;
    }

    public void setAttachments(String attachments) {
        this.attachments = attachments;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

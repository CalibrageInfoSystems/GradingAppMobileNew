package com.oilpalm3f.gradingapp.dbmodels;

public class GatepassTokenListModel {

    private String GatePassTokenCode;
    private String VehicleNumber;
    private String GatePassSerialNumber;
    private String IsCollection;
    private String CreatedDate;

    public String getGatePassTokenCode() {
        return GatePassTokenCode;
    }

    public void setGatePassTokenCode(String gatePassTokenCode) {
        GatePassTokenCode = gatePassTokenCode;
    }

    public String getVehicleNumber() {
        return VehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        VehicleNumber = vehicleNumber;
    }

    public String getGatePassSerialNumber() {
        return GatePassSerialNumber;
    }

    public void setGatePassSerialNumber(String gatePassSerialNumber) {
        GatePassSerialNumber = gatePassSerialNumber;
    }

    public String getIsCollection() {
        return IsCollection;
    }

    public void setIsCollection(String isCollection) {
        IsCollection = isCollection;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }
}

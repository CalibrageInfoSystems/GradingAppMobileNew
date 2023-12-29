package com.oilpalm3f.gradingapp.dbmodels;

public class GatepassInListModel {


    private String GatePassCode;
    private String GatePassTokenCode;
    private String IsVehicleOut;
    private String CreatedDate;
    private String WBCode;
    private String VehicleType;
    private String WBID;
    private String VehicleCategory;

    private String IsCollection;

    private String VehicleNumber;

    private String VehicleTypeId;

    private String VehicleCategoryId;


    public String getVehicleTypeId() {
        return VehicleTypeId;
    }

    public void setVehicleTypeId(String vehicleTypeId) {
        VehicleTypeId = vehicleTypeId;
    }

    public String getVehicleCategoryId() {
        return VehicleCategoryId;
    }

    public void setVehicleCategoryId(String vehicleCategoryId) {
        VehicleCategoryId = vehicleCategoryId;
    }

    public String getVehicleNumber() {
        return VehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        VehicleNumber = vehicleNumber;
    }

    public String getIsCollection() {
        return IsCollection;
    }

    public void setIsCollection(String isCollection) {
        IsCollection = isCollection;
    }

    public String getGatePassCode() {
        return GatePassCode;
    }

    public void setGatePassCode(String gatePassCode) {
        GatePassCode = gatePassCode;
    }

    public String getGatePassTokenCode() {
        return GatePassTokenCode;
    }

    public void setGatePassTokenCode(String gatePassTokenCode) {
        GatePassTokenCode = gatePassTokenCode;
    }

    public String getIsVehicleOut() {
        return IsVehicleOut;
    }

    public void setIsVehicleOut(String isVehicleOut) {
        IsVehicleOut = isVehicleOut;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public String getWBCode() {
        return WBCode;
    }

    public void setWBCode(String WBCode) {
        this.WBCode = WBCode;
    }

    public String getVehicleType() {
        return VehicleType;
    }

    public void setVehicleType(String vehicleType) {
        VehicleType = vehicleType;
    }

    public String getWBID() {
        return WBID;
    }

    public void setWBID(String WBID) {
        this.WBID = WBID;
    }

    public String getVehicleCategory() {
        return VehicleCategory;
    }

    public void setVehicleCategory(String vehicleCategory) {
        VehicleCategory = vehicleCategory;
    }
}

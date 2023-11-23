package com.oilpalm3f.gradingapp.dbmodels;

public class GatePass {
    private int Id;
    private String GatePassCode;
    private String GatePassTokenCode;
    private int WeighbridgeId;
    private int VehicleTypeId;
    private int CreatedByUserId;
    private String CreatedDate;
    private int IsVehicleOut;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
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

    public int getWeighbridgeId() {
        return WeighbridgeId;
    }

    public void setWeighbridgeId(int weighbridgeId) {
        WeighbridgeId = weighbridgeId;
    }

    public int getVehicleTypeId() {
        return VehicleTypeId;
    }

    public void setVehicleTypeId(int vehicleTypeId) {
        VehicleTypeId = vehicleTypeId;
    }

    public int getCreatedByUserId() {
        return CreatedByUserId;
    }

    public void setCreatedByUserId(int createdByUserId) {
        CreatedByUserId = createdByUserId;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public int getIsVehicleOut() {
        return IsVehicleOut;
    }

    public void setIsVehicleOut(int isVehicleOut) {
        IsVehicleOut = isVehicleOut;
    }

    public int getUpdatedByUserId() {
        return UpdatedByUserId;
    }

    public void setUpdatedByUserId(int updatedByUserId) {
        UpdatedByUserId = updatedByUserId;
    }

    public String getUpdatedDate() {
        return UpdatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        UpdatedDate = updatedDate;
    }

    public int getServerUpdatedStatus() {
        return ServerUpdatedStatus;
    }

    public void setServerUpdatedStatus(int serverUpdatedStatus) {
        ServerUpdatedStatus = serverUpdatedStatus;
    }

    private int UpdatedByUserId;
    private String UpdatedDate;
    private int ServerUpdatedStatus;
}

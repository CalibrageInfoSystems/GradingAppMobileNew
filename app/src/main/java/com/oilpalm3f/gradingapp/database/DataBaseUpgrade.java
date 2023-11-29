package com.oilpalm3f.gradingapp.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.oilpalm3f.gradingapp.common.CommonConstants;

import static android.content.Context.MODE_PRIVATE;

public class DataBaseUpgrade {
    private static final String LOG_TAG = DataBaseUpgrade.class.getName();

    static void upgradeDataBase(final Context context, final SQLiteDatabase db) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("appprefs", MODE_PRIVATE);
        boolean result = true;
        try {
            boolean isFreshInstall = sharedPreferences.getBoolean(CommonConstants.IS_FRESH_INSTALL, true);
            if (isFreshInstall) {
                upgradeDb1(db);
                upgradeDb2(db);
//

            } else {
                boolean isDbUpgradeFinished = sharedPreferences.getBoolean(String.valueOf(Palm3FoilDatabase.DATA_VERSION), false);
                Log.v(LOG_TAG, "@@@@ database....." + isDbUpgradeFinished);
                if (!isDbUpgradeFinished) {
                    switch (Palm3FoilDatabase.DATA_VERSION) {
                        case 1:
                            upgradeDb1(db);
                            break;
                        case 2:
                            upgradeDb2(db);
                            break;

                    }
                } else {
                    Log.v(LOG_TAG, "@@@@ database is already upgraded " + Palm3FoilDatabase.DATA_VERSION);
                }
            }

        } catch (Exception e) {
            result = false;
        } finally {
            if (result) {
                Log.v(LOG_TAG, "@@@@ database is upgraded " + Palm3FoilDatabase.DATA_VERSION);
            } else {
                Log.e(LOG_TAG, "@@@@ database is upgrade failed or already upgraded");
            }
            sharedPreferences.edit().putBoolean(CommonConstants.IS_FRESH_INSTALL, false).apply();
            sharedPreferences.edit().putBoolean(String.valueOf(Palm3FoilDatabase.DATA_VERSION), true).apply();
        }
    }


    public static void upgradeDb1(final SQLiteDatabase db) {
        Log.d(LOG_TAG, "******* upgradeDataBase " + Palm3FoilDatabase.DATA_VERSION);

        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void upgradeDb2( final SQLiteDatabase db) {
        Log.d(LOG_TAG, "******* upgradeDataBase 2 ******" + Palm3FoilDatabase.DATA_VERSION);

        String column1 = "Alter Table FFBGrading Add VehicleNumber Varchar(50)";
        String fingerprintcolumn = "ALTER TABLE CollectionCenter Add IsFingerPrintReq BIT";


        String GatePassToken = "CREATE TABLE GatePassToken(    " +
                " Id                                INTEGER     PRIMARY KEY AUTOINCREMENT ,\n" +
                " GatePassTokenCode                         VARCHAR(100)             NOT NULL,\n" +
                " VehicleNumber            VARCHAR(50)             NOT NULL,\n" +
                " GatePassSerialNumber         INT               NOT NULL,\n" +
                " IsCollection    BOOLEAN       NOT NULL,\n" +
                " CreatedByUserId                     INT                 NOT NULL,\n" +
                " CreatedDate                      DATETIME               NOT NULL,\n" +
                "    ServerUpdatedStatus     BOOLEAN       NOT NULL\n" +
                " );";

        String GatePass = "CREATE TABLE GatePass(  " +
                " Id                                INTEGER     PRIMARY KEY AUTOINCREMENT ,\n" +
                " GatePassCode                         VARCHAR(100)             NOT NULL,\n" +
                " GatePassTokenCode             VARCHAR(100)             NOT NULL,\n" +
                " WeighbridgeId         INT                 NOT NULL,\n" +
                " VehicleTypeId     INT           NOT NULL,\n" +
                " CreatedByUserId                     INT                 NOT NULL,\n" +
                " CreatedDate                      DATETIME               NOT NULL,\n" +
                " UpdatedByUserId                    INT                  NOT NULL,\n" +
                " IsVehicleOut    BOOLEAN       NOT NULL,\n" +
                " UpdatedDate                      DATETIME               NOT NULL,\n" +
                "    ServerUpdatedStatus     BOOLEAN       NOT NULL\n" +
                " );";

        String ActivityRight = "CREATE TABLE ActivityRight(    " +
                " Id                                INTEGER    ,\n" +
                " Name            VARCHAR(100)             NOT NULL,\n" +
                " Desc                         VARCHAR(500)             NOT NULL,\n" +
                " IsActive    BOOLEAN       NOT NULL,\n" +
                " CreatedByUserId                     INT                 NOT NULL,\n" +
                " CreatedDate                      DATETIME               NOT NULL,\n" +
                " UpdatedByUserId                    INT                  NOT NULL,\n" +
                " UpdatedDate                      DATETIME               NOT NULL\n" +
                " );";

        String ClassType = "CREATE TABLE ClassType(    " +
                " ClassTypeId                              INTEGER    ,\n" +
                " Name            VARCHAR(255)             NOT NULL,\n" +
                " IsActive    BOOLEAN       NOT NULL,\n" +
                " CreatedByUserId                     INT                 NOT NULL,\n" +
                " CreatedDate                      DATETIME               NOT NULL,\n" +
                " UpdatedByUserId                    INT                  NOT NULL,\n" +
                " UpdatedDate                      DATETIME               NOT NULL\n" +
                " );";

        String TypeCdDmt = "CREATE TABLE TypeCdDmt(    " +
                " TypeCdId                                INTEGER    NOT NULL ,\n" +
                " ClassTypeId                              INTEGER    NOT NULL ,\n" +
                " Desc            VARCHAR(255)     NOT NULL,\n" +
                " TableName            VARCHAR(255)  ,\n" +
                " ColumnName            VARCHAR(255)  ,\n" +
                " SortOrder                                INTEGER    ,\n" +
                " IsActive    BOOLEAN       NOT NULL,\n" +
                " CreatedByUserId                     INT                 NOT NULL,\n" +
                " CreatedDate                      DATETIME               NOT NULL,\n" +
                " UpdatedByUserId                    INT                  NOT NULL,\n" +
                " UpdatedDate                      DATETIME               NOT NULL\n" +
                " );";



        String LookUp = "CREATE TABLE LookUp(    " +
                " Id                                INTEGER    ,\n" +
                " LookUpTypeId                              INTEGER    ,\n" +
                " Name                         VARCHAR(255)             NOT NULL,\n" +
                " Remarks            VARCHAR(10000)  ,\n" +
                " IsActive    BOOLEAN       NOT NULL,\n" +
                " CreatedByUserId                     INT                 NOT NULL,\n" +
                " CreatedDate                      DATETIME               NOT NULL,\n" +
                " UpdatedByUserId                    INT                  NOT NULL,\n" +
                " UpdatedDate                      DATETIME               NOT NULL\n" +
                " );";

        String MillWeighBridge = "CREATE TABLE MillWeighBridge(    " +
                " Id                                INTEGER     PRIMARY KEY AUTOINCREMENT ,\n" +
                " Code                         VARCHAR(25)             NOT NULL,\n" +
                " Name            VARCHAR(100)             NOT NULL,\n" +
                " IsActive    BOOLEAN       NOT NULL,\n" +
                " CreatedByUserId                     INT                 NOT NULL,\n" +
                " CreatedDate                      DATETIME               NOT NULL,\n" +

                " UpdatedByUserId                    INT                  NOT NULL,\n" +
                " UpdatedDate                      DATETIME               NOT NULL\n" +
                " );";


        String UserMillWeighBridgexref = "CREATE TABLE UserMillWeighBridgexref(    " +

                " UserId                     INT                         NOT NULL,\n" +
                " MillWeighBridgeId                     INT                 NOT NULL\n" +
                " );";

        try {
            db.execSQL(column1);
            db.execSQL(fingerprintcolumn);
            db.execSQL(GatePassToken);
            db.execSQL(GatePass);
            db.execSQL(ActivityRight);
            db.execSQL(ClassType);
            db.execSQL(TypeCdDmt);
            db.execSQL(LookUp);
            db.execSQL(MillWeighBridge);
            db.execSQL(UserMillWeighBridgexref);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

package aditij.assignment4.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by aditij on 2/22/2015.
 */
public class ClinicSQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_PATIENT = "Patient";
    public static final String PATIENT_ID = "PatientId";
    public static final String NAME = "Name";
    public static final String ADDRESS = "Address";

    public static final String TABLE_SPECIALIZATION = "Specialization";
    public static final String SPECIALIZATION_ID = "SpecializationId";
    public static final String SPECIALIZATION_NAME = "SpecializationName";

    public static final String TABLE_PHYSICIAN = "Physician";
    public static final String PHYSICIAN_ID = "PhysicianId";
    public static final String PHYSICIAN_SPECIALIZATION_ID = "PhysicianSpecializationId";

    public static final String TABLE_PHYSICIAN_PATIENT = "PatientPhysicianMapping";
    public static final String MAPPING_ID="MappingId";
    public static final String DIAGNOSIS="Diagnosis";
    public static final String VISITING_TIME = "VisitingTime";
    public static final String MAPPING_PATIENT_ID="MappingPatientId";
    public static final String MAPPING_PHYSICIAN_ID="MappingPhysicianId";

    private static String PATIENT_TABLE_CREATE ="CREATE TABLE "+TABLE_PATIENT + " ("
            +PATIENT_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
            +NAME+" TEXT, "
            +ADDRESS +" TEXT);";

    private static String SPECIALIZATION_TABLE_CREATE = "CREATE TABLE "+TABLE_SPECIALIZATION+ " ("
            +SPECIALIZATION_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
            +SPECIALIZATION_NAME+" TEXT); ";

    private static String PHYSICIAN_TABLE_CREATE ="CREATE TABLE "+TABLE_PHYSICIAN+ " ("
            +PHYSICIAN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
            +NAME+" TEXT, "
            +PHYSICIAN_SPECIALIZATION_ID +" INTEGER," +
            " FOREIGN KEY("+PHYSICIAN_SPECIALIZATION_ID +") REFERENCES "+TABLE_SPECIALIZATION+"("+SPECIALIZATION_ID+")"+");";

    private static String MAPPING_TABLE_CREATE ="CREATE TABLE "+TABLE_PHYSICIAN_PATIENT + " ("
            +MAPPING_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
            +DIAGNOSIS+" TEXT, "
            +VISITING_TIME+" DATETIME, "
            +MAPPING_PATIENT_ID +" INTEGER,"
            +MAPPING_PHYSICIAN_ID +" INTEGER, "+
            " FOREIGN KEY("+MAPPING_PATIENT_ID+") REFERENCES "+TABLE_PATIENT+"("+PATIENT_ID+"),"+
            " FOREIGN KEY("+MAPPING_PHYSICIAN_ID +") REFERENCES "+TABLE_PHYSICIAN+"("+PHYSICIAN_ID+")"
            +");";

    public static final String DATABASE_NAME = "Clinic.db";
    public static final int DATABASE_VERSION = 1;

    public ClinicSQLiteHelper(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHYSICIAN_PATIENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHYSICIAN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SPECIALIZATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PATIENT);

        db.execSQL(PATIENT_TABLE_CREATE);
        db.execSQL(SPECIALIZATION_TABLE_CREATE);
        db.execSQL(PHYSICIAN_TABLE_CREATE);
        db.execSQL(MAPPING_TABLE_CREATE);

        db.execSQL("INSERT INTO "+ TABLE_SPECIALIZATION +" ("+SPECIALIZATION_NAME+") VALUES ('Neurology')");
        db.execSQL("INSERT INTO "+ TABLE_SPECIALIZATION +" ("+SPECIALIZATION_NAME+") VALUES ('Cardiology')");
        db.execSQL("INSERT INTO "+ TABLE_SPECIALIZATION +" ("+SPECIALIZATION_NAME+") VALUES ('General')");
        db.execSQL("INSERT INTO "+ TABLE_SPECIALIZATION +" ("+SPECIALIZATION_NAME+") VALUES ('Ophthalmology')");
        db.execSQL("INSERT INTO "+ TABLE_SPECIALIZATION +" ("+SPECIALIZATION_NAME+") VALUES ('Pediatric')");

        db.execSQL("INSERT INTO "+ TABLE_PHYSICIAN +" ("+ NAME +","+PHYSICIAN_SPECIALIZATION_ID+") VALUES ('Phy_Neu1',1)");
        db.execSQL("INSERT INTO "+ TABLE_PHYSICIAN +" ("+ NAME +","+PHYSICIAN_SPECIALIZATION_ID+") VALUES ('Phy_Neu2',1)");
        db.execSQL("INSERT INTO "+ TABLE_PHYSICIAN +" ("+ NAME +","+PHYSICIAN_SPECIALIZATION_ID+") VALUES ('Phy_Card1',2)");
        db.execSQL("INSERT INTO "+ TABLE_PHYSICIAN +" ("+ NAME +","+PHYSICIAN_SPECIALIZATION_ID+") VALUES ('Phy_Card2',2)");
        db.execSQL("INSERT INTO "+ TABLE_PHYSICIAN +" ("+ NAME +","+PHYSICIAN_SPECIALIZATION_ID+") VALUES ('Phy_Gen1',3)");
        db.execSQL("INSERT INTO "+ TABLE_PHYSICIAN +" ("+ NAME +","+PHYSICIAN_SPECIALIZATION_ID+") VALUES ('Phy_Opt1',4)");
        db.execSQL("INSERT INTO "+ TABLE_PHYSICIAN +" ("+ NAME +","+PHYSICIAN_SPECIALIZATION_ID+") VALUES ('Phy_Opt2',4)");
        db.execSQL("INSERT INTO "+ TABLE_PHYSICIAN +" ("+ NAME +","+PHYSICIAN_SPECIALIZATION_ID+") VALUES ('Phy_Ped1',5)");
        db.execSQL("INSERT INTO "+ TABLE_PHYSICIAN +" ("+ NAME +","+PHYSICIAN_SPECIALIZATION_ID+") VALUES ('Phy_Ped2',5)");
        db.execSQL("INSERT INTO "+ TABLE_PHYSICIAN +" ("+ NAME +","+PHYSICIAN_SPECIALIZATION_ID+") VALUES ('Phy_Ped3',5)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(ClinicSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + MAPPING_TABLE_CREATE);
        db.execSQL("DROP TABLE IF EXISTS " + PHYSICIAN_TABLE_CREATE);
        db.execSQL("DROP TABLE IF EXISTS " + SPECIALIZATION_TABLE_CREATE);
        db.execSQL("DROP TABLE IF EXISTS " + PATIENT_TABLE_CREATE);
        onCreate(db);
    }
}

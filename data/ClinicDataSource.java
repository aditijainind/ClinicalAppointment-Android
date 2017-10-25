package aditij.assignment4.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aditij on 2/23/2015.
 */
public class ClinicDataSource {
    private SQLiteDatabase database;
    private ClinicSQLiteHelper dbHelper;

    private String[] patientAllColumns={ClinicSQLiteHelper.PATIENT_ID,ClinicSQLiteHelper.NAME,ClinicSQLiteHelper.ADDRESS};
    private String[] specializationAllColumns={ClinicSQLiteHelper.SPECIALIZATION_ID,ClinicSQLiteHelper.SPECIALIZATION_NAME};
    private String[] physicianAllColumns={ClinicSQLiteHelper.PHYSICIAN_ID,ClinicSQLiteHelper.NAME,ClinicSQLiteHelper.PHYSICIAN_SPECIALIZATION_ID};
    private String[] mappingAllColumns={ClinicSQLiteHelper.MAPPING_ID,ClinicSQLiteHelper.DIAGNOSIS,ClinicSQLiteHelper.VISITING_TIME,ClinicSQLiteHelper.MAPPING_PATIENT_ID,ClinicSQLiteHelper.MAPPING_PHYSICIAN_ID};

    public ClinicDataSource(Context context)
    {
        dbHelper = new ClinicSQLiteHelper(context);
    }

    public void open() throws SQLException
    {
        database = dbHelper.getWritableDatabase();
    }

    public void close()
    {
        dbHelper.close();
    }

    public List<Appointment> getAllAppointments()
    {
        List<Appointment> allAppointments = new ArrayList<Appointment>();
        String query = "SELECT * FROM "+ClinicSQLiteHelper.TABLE_PHYSICIAN_PATIENT;
        Cursor cursor = database.rawQuery(query,null);

        cursor.moveToFirst();
        while(!cursor.isAfterLast())
        {
            Appointment appointment = cursorToAppointment(cursor);
            appointment.setPatientName(getPatientById(appointment.getPatientId()).getName());
            Physician physician = getPhysicianById(appointment.getPhysicianId());
            appointment.setPhysicianName(physician.getName());
            appointment.setPhysicianDepartment(physician.getSpecialization());
            allAppointments.add(appointment);
            cursor.moveToNext();
        }
        cursor.close();
        return allAppointments;
    }

    private Appointment cursorToAppointment(Cursor cursor)
    {
        Appointment appointment = new Appointment();
        appointment.setMappingId(cursor.getInt(0));
        appointment.setPatientId(cursor.getInt(3));
        appointment.setPhysicianId(cursor.getInt(4));
        //appointment.setPatientName(cursor.getString(3));
        //appointment.setPhysicianName(cursor.getString(4));
        appointment.setDiagnosis(cursor.getString(1));
        appointment.setVisitTime(cursor.getString(2));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        return appointment;
    }
    public List<String> getAllDepartments()
    {
        List<String> allDepartments = new ArrayList<String>();
        String query = "SELECT "+ClinicSQLiteHelper.SPECIALIZATION_NAME+" FROM "+ClinicSQLiteHelper.TABLE_SPECIALIZATION;

        Cursor cursor = database.rawQuery(query,null);

        cursor.moveToFirst();
        while(!cursor.isAfterLast())
        {
            String department = cursor.getString(0);
            allDepartments.add(department);
            cursor.moveToNext();
        }
        cursor.close();
        return allDepartments;
    }

    public List<String> getPhysicianBySpecialization(String specialization)
    {
        List<String> allPhysicians = new ArrayList<String>();
        String query = "SELECT "+ClinicSQLiteHelper.NAME+" FROM "+ClinicSQLiteHelper.TABLE_PHYSICIAN + " p INNER JOIN "+ClinicSQLiteHelper.TABLE_SPECIALIZATION+" s ON p."+ClinicSQLiteHelper.PHYSICIAN_SPECIALIZATION_ID+"=s."+ClinicSQLiteHelper.SPECIALIZATION_ID+" AND s."+ClinicSQLiteHelper.SPECIALIZATION_NAME+"='"+specialization+"'";

        Cursor cursor = database.rawQuery(query,null);

        cursor.moveToFirst();
        while(!cursor.isAfterLast())
        {
            String department = cursor.getString(0);
            allPhysicians.add(department);
            cursor.moveToNext();
        }
        cursor.close();
        return allPhysicians;
    }

    public long savePatient(Patient patient)
    {
        ContentValues values = new ContentValues();
        values.put(ClinicSQLiteHelper.NAME,patient.getName());
        values.put(ClinicSQLiteHelper.ADDRESS, patient.getAddress());

        if(patient.getPatientId()==-1)
        {
            long insertId = database.insert(ClinicSQLiteHelper.TABLE_PATIENT, null, values);
            return insertId;
        }
        else
        {
            database.update(ClinicSQLiteHelper.TABLE_PATIENT,values,ClinicSQLiteHelper.PATIENT_ID+" = " + Long.toString(patient.getPatientId()),null);
            return patient.getPatientId();
        }
    }

    public long saveAppointment(Appointment appointment)
    {
        ContentValues values = new ContentValues();
        values.put(ClinicSQLiteHelper.DIAGNOSIS,appointment.getDiagnosis());
        values.put(ClinicSQLiteHelper.MAPPING_PHYSICIAN_ID,getPhysicianIdByNameDept(appointment.getPhysicianName(), appointment.getPhysicianDepartment()));
        values.put(ClinicSQLiteHelper.VISITING_TIME,appointment.getVisitTime());
        values.put(ClinicSQLiteHelper.MAPPING_PATIENT_ID,appointment.getPatientId());

        if(appointment.getMappingId()==-1)
        {
            long insertId = database.insert(ClinicSQLiteHelper.TABLE_PHYSICIAN_PATIENT, null, values);
            return insertId;
        }
        else
        {
            database.update(ClinicSQLiteHelper.TABLE_PHYSICIAN_PATIENT,values,ClinicSQLiteHelper.MAPPING_PATIENT_ID+" = " + Long.toString(appointment.getPatientId()),null);
            return appointment.getPatientId();
        }
    }

    public int getPhysicianIdByNameDept(String name, String dept)
    {
        int physicianId=-1;
        String query = "SELECT "+ClinicSQLiteHelper.PHYSICIAN_ID+" FROM "+ClinicSQLiteHelper.TABLE_PHYSICIAN + " p INNER JOIN "+ClinicSQLiteHelper.TABLE_SPECIALIZATION+" s ON p."+ClinicSQLiteHelper.PHYSICIAN_SPECIALIZATION_ID+"=s."+ClinicSQLiteHelper.SPECIALIZATION_ID+" AND s."+ClinicSQLiteHelper.SPECIALIZATION_NAME+"='"+dept+"' AND p."+ClinicSQLiteHelper.NAME+"='"+name+"'";

        Cursor cursor = database.rawQuery(query,null);

        cursor.moveToFirst();
        if(!cursor.isAfterLast())
        {
            physicianId= cursor.getInt(0);
        }
        cursor.close();
        return physicianId;
    }

    public Physician getPhysicianById(long physicianId)
    {
        Physician physician = new Physician();
        String query = "SELECT * FROM "+ClinicSQLiteHelper.TABLE_PHYSICIAN+" WHERE "+ClinicSQLiteHelper.PHYSICIAN_ID+"="+Long.toString(physicianId);
        Cursor cursor = database.rawQuery(query,null);

        cursor.moveToFirst();
        if(!cursor.isAfterLast()) {
            physician.setPhysicianId(Long.parseLong(cursor.getString(0),10));
            physician.setName(cursor.getString(1));

            String specQuery= "SELECT "+ClinicSQLiteHelper.SPECIALIZATION_NAME+" FROM "+ClinicSQLiteHelper.TABLE_SPECIALIZATION+" WHERE "+ClinicSQLiteHelper.SPECIALIZATION_ID+"="+cursor.getString(2);
            Cursor cursorSpec = database.rawQuery(specQuery,null);

            cursorSpec.moveToFirst();
            if(!cursorSpec.isAfterLast()) {
                physician.setSpecialization(cursorSpec.getString(0));
            }
        }
        cursor.close();
        return physician;
    }

    public Patient getPatientById(long patientId)
    {
        Patient patient = new Patient();
        String query = "SELECT * FROM "+ClinicSQLiteHelper.TABLE_PATIENT+" WHERE "+ClinicSQLiteHelper.PATIENT_ID+"="+Long.toString(patientId);
        Cursor cursor = database.rawQuery(query,null);

        cursor.moveToFirst();
        if(!cursor.isAfterLast()) {
            patient.setPatientId(Long.parseLong(cursor.getString(0),10));
            patient.setName(cursor.getString(1));
            patient.setAddress(cursor.getString(2));
        }
        cursor.close();
        return patient;
    }

    public Appointment getAppointmentById(long appointmentId)
    {
        Appointment appointment = new Appointment();
        String query = "SELECT * FROM "+ClinicSQLiteHelper.TABLE_PHYSICIAN_PATIENT+" WHERE "+ClinicSQLiteHelper.MAPPING_ID+"="+Long.toString(appointmentId);
        Cursor cursor = database.rawQuery(query,null);

        cursor.moveToFirst();
        if(!cursor.isAfterLast())
            appointment = cursorToAppointment(cursor);
        cursor.close();

        appointment.setPatientName(getPatientById(appointment.getPatientId()).getName());
        Physician physician = getPhysicianById(appointment.getPhysicianId());
        appointment.setPhysicianName(physician.getName());
        appointment.setPhysicianDepartment(physician.getSpecialization());

        return appointment;
    }

    public void deleteAppointmentById(long appointmentId)
    {
        Appointment appointment = new Appointment();
        String query = "DELETE FROM "+ClinicSQLiteHelper.TABLE_PHYSICIAN_PATIENT+" WHERE "+ClinicSQLiteHelper.MAPPING_ID+"="+Long.toString(appointmentId);
        database.execSQL(query);

        Cursor cursor = database.rawQuery(query,null);
    }

}

package aditij.assignment4;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import aditij.assignment4.Helpers.JSONParser;
import aditij.assignment4.data.Appointment;
import aditij.assignment4.data.ClinicDataSource;
import aditij.assignment4.data.Patient;
import aditij.assignment4.data.Physician;
import aditij.assignment4.services.AlarmService;


public class EditActivity extends Activity {

    JSONParser jsonParser = new JSONParser();
    private static String url_create_appointment ="http://people.cs.clemson.edu/~aditij/createAppointment.php";
    private static String url_get_appointment = "http://people.cs.clemson.edu/~aditij/getAppointmentById.php";
    private ClinicDataSource dataSource;
    private Patient patient;
    private Appointment appointment;
    private Physician physician;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        dataSource = new ClinicDataSource(this);
        try
        {
            dataSource.open();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        addDepartmentItems();
        Intent receivedIntent = getIntent();
        String mappingId = receivedIntent.getStringExtra("SelectedAppointmentId");
        appointment = new Appointment();
        patient = new Patient();
        physician = new Physician();
        if(!mappingId.equals("-1")) {
            appointment.setMappingId(Long.parseLong(mappingId));
        }
        else
        {
            patient.setPatientId(-1);
            appointment.setMappingId(-1);


        }
        new AppointmentGetterById().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.notification_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addDepartmentItems()
    {
        Spinner spinnerDepartments = (Spinner) findViewById(R.id.spinnerDepartment);
        List<String> listDepartments = dataSource.getAllDepartments();
        listDepartments.add(0,"Select a department");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, listDepartments);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepartments.setAdapter(dataAdapter);
    }


    public void saveAppointment(View view)
    {
        if(((EditText) findViewById(R.id.txtPatientName)).getText().toString().isEmpty() || ((EditText) findViewById(R.id.txtPatientName)).getText().toString().isEmpty()
                ||((EditText) findViewById(R.id.txtAppointmentDate)).getText().toString().isEmpty() || ((EditText) findViewById(R.id.txtAppointmentTime)).getText().toString().isEmpty()
                ||((Spinner) findViewById(R.id.spinnerDepartment)).getSelectedItem().toString().isEmpty() || ((Spinner) findViewById(R.id.spinnerPhysician)).getSelectedItem()==null || ((Spinner) findViewById(R.id.spinnerPhysician)).getSelectedItem().toString().isEmpty())
        {
            new AlertDialog.Builder(this)
                    .setTitle("Invalid data")
                    .setMessage("Invalid values for one or more items")
                    .setNeutralButton("OK",null).create().show();
        }
        else
        {
            patient.setName(((EditText) findViewById(R.id.txtPatientName)).getText().toString());
            patient.setAddress(((EditText) findViewById(R.id.txtPatientName)).getText().toString());

            appointment.setPhysicianDepartment(((Spinner) findViewById(R.id.spinnerDepartment)).getSelectedItem().toString());
            appointment.setPhysicianName(((Spinner) findViewById(R.id.spinnerPhysician)).getSelectedItem().toString());

            new NewAppointmentCreator().execute();
            new AlertDialog.Builder(this)
                    .setTitle("Save Appointment")
                    .setMessage("Appointment saved successfully")
                    .setNeutralButton("OK", null).create().show();


            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void getPhysicians(View view)
    {
        Spinner spinnerPhysicians = (Spinner) findViewById(R.id.spinnerPhysician);

        Spinner spinnerDepartments = (Spinner) findViewById(R.id.spinnerDepartment);

        List<String> listPhysicians = dataSource.getPhysicianBySpecialization(spinnerDepartments.getSelectedItem().toString());

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, listPhysicians);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPhysicians.setAdapter(dataAdapter);
    }
    public void getDate(View view)
    {
        final View parentView = (View)view.getParent();


        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(this,DatePickerDialog.THEME_HOLO_LIGHT,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        EditText txtAppointmentDate = (EditText)parentView.findViewById(R.id.txtAppointmentDate);
                        txtAppointmentDate.setText((new DateFormatSymbols().getMonths()[monthOfYear]) + " " +dayOfMonth + ", "
                                +  year);
                        String monthStr = Integer.toString(monthOfYear);
                        String dayStr = Integer.toString(dayOfMonth);
                        if(monthStr.length()==1)
                            monthStr = "0"+monthStr;
                        if(dayStr.length()==1)
                            dayStr = "0"+dayStr;
                        appointment.setVisitTime(Integer.toString(year) + "-" + monthStr + "-" + dayStr);
                    }
                }, mYear, mMonth, mDay);
        dpd.show();

    }

    public void getTime(View view)
    {
        int mHour=0, mMinute=0;
        final View parentView = (View)view.getParent();

        TimePickerDialog tpd = new TimePickerDialog(this,TimePickerDialog.THEME_HOLO_LIGHT,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        EditText txtAppointmentTime = (EditText)parentView.findViewById(R.id.txtAppointmentTime);
                        String AM_PM = "AM";
                        String hourString =Integer.toString(hourOfDay);
                        String minString = Integer.toString(minute);

                        if(minute<10)
                            minString = "0"+minString;

                        if(Integer.toString(hourOfDay).length()==1)
                        {
                            hourString="0"+Integer.toString(hourOfDay);
                        }

                        appointment.setVisitTime(appointment.getVisitTime()+" "+hourString+":"+minString+":00");

                        if(hourOfDay>12)
                        {
                            AM_PM="PM";
                            hourString=Integer.toString(hourOfDay-12);
                        }
                        txtAppointmentTime.setText(hourString + ":" + minString + " " +AM_PM);



                    }
                }, mHour, mMinute, false);

        tpd.show();



    }

    class NewAppointmentCreator extends AsyncTask<String,String,String>
    {
        protected void OnPreExecute()
        {
            super.onPreExecute();

        }

        protected String doInBackground(String... args)
        {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("MappingId",Long.toString(appointment.getMappingId())));
            params.add(new BasicNameValuePair("PatientId",Long.toString(appointment.getPatientId())));
            params.add(new BasicNameValuePair("PatientAddress",patient.getAddress()));
            params.add(new BasicNameValuePair("PatientName",patient.getName()));
            params.add(new BasicNameValuePair("Diagnosis",appointment.getDiagnosis()));
            params.add(new BasicNameValuePair("PhysicianName",appointment.getPhysicianName()));
            params.add(new BasicNameValuePair("MappingPhysicianID",Long.toString(appointment.getPhysicianId())));
            params.add(new BasicNameValuePair("AppointmentTime",appointment.getVisitTime()));
            JSONObject json = jsonParser.makeHttpRequest(url_create_appointment,"POST",params);

            SharedPreferences appSharedPreferences = PreferenceManager.getDefaultSharedPreferences(EditActivity.this);

            boolean reminderRequired = appSharedPreferences.getBoolean("reminderCheckbox", false);




            if(reminderRequired) {
                String reminderTime = appSharedPreferences.getString("reminderTime", "");
                Intent alarmIntent = new Intent(EditActivity.this, AlarmService.class);
                try {
                    alarmIntent.putExtra("alarmId", json.getString("mappingId"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                alarmIntent.putExtra("alarmLabel", "Appointment with Doctor in " +reminderTime +" minutes");
                alarmIntent.putExtra("alarmAction", "Create");
                StringTokenizer stAppointment = new StringTokenizer(appointment.getVisitTime(), " ");
                String appointmentDate = "";
                String appointmentTime = "";
                while (stAppointment.hasMoreTokens()) {
                    appointmentDate = stAppointment.nextToken();
                    appointmentTime = stAppointment.nextToken();
                }

                StringTokenizer stAppointmentTime = new StringTokenizer(appointmentTime, ":");

                alarmIntent.putExtra("alarmHH", stAppointmentTime.nextToken());
                alarmIntent.putExtra("alarm_mm", stAppointmentTime.nextToken());
                alarmIntent.putExtra("alarm_ss", stAppointmentTime.nextToken());

                StringTokenizer stAppointmentDate = new StringTokenizer(appointmentDate, "-");
                alarmIntent.putExtra("alarmYear", stAppointmentDate.nextToken());
                alarmIntent.putExtra("alarmMonth", stAppointmentDate.nextToken());
                alarmIntent.putExtra("alarmDate", stAppointmentDate.nextToken());
                alarmIntent.putExtra("reminderTime", reminderTime);
                EditActivity.this.startService(alarmIntent);
            }
            return null;
        }

        protected void onPostExecute(String str)
        {

        }
    }
    class AppointmentGetterById extends AsyncTask<String,String,String>
    {
        protected void OnPreExecute()
        {
            super.onPreExecute();

        }

        protected String doInBackground(String... args)
        {
            Intent receivedIntent = getIntent();
            final String mappingId = receivedIntent.getStringExtra("SelectedAppointmentId");
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("reqPatientId",mappingId));

            final JSONObject json = jsonParser.makeHttpRequest(url_get_appointment,"GET",params);

            runOnUiThread(new Runnable() {
                public void run() {
                    try {

                        JSONArray appointmentArray = json.getJSONArray("appointments");
                        if (appointmentArray.length()>0) {
                            JSONObject appointment = appointmentArray.getJSONObject(0);


                            if (!mappingId.equals("-1")) {
                                ((EditText) findViewById(R.id.txtPatientName)).setText(appointment.getString("patientName"));
                                ((EditText) findViewById(R.id.txtPatientAddress)).setText(appointment.getString("address"));

                                StringTokenizer tokenizer = new StringTokenizer(appointment.getString("VisitingTime"), " ");
                                String visitDate = "";
                                String visitTime = "";

                                int index = 0;
                                String month="";
                                while (tokenizer.hasMoreTokens() )
                                {
                                    if(index==0)
                                    {
                                        StringTokenizer dateTokenizer = new StringTokenizer(tokenizer.nextToken(),"-");
                                        int dateIndex=0;
                                        while(dateTokenizer.hasMoreTokens())
                                        {

                                            if(dateIndex==0)
                                                visitDate=", "+dateTokenizer.nextToken();
                                            if(dateIndex==1)
                                                month=new DateFormatSymbols().getMonths()[Integer.parseInt(dateTokenizer.nextToken())];
                                            if(dateIndex==2)
                                                visitDate=month+" " + dateTokenizer.nextToken()+visitDate;
                                            dateIndex++;
                                        }
                                    }
                                    else
                                    {
                                        StringTokenizer timeTokenizer = new StringTokenizer(tokenizer.nextToken(),":");
                                        int timeIndex=0;

                                        String AM_PM = "AM";
                                        String hourString ="";
                                        String minString = "";

                                        while(timeTokenizer.hasMoreTokens())
                                        {
                                            if(timeIndex==0) {
                                                hourString = timeTokenizer.nextToken();
                                                if(Integer.parseInt(hourString)>12) {
                                                    visitTime = Integer.toString(Integer.parseInt(hourString)-12);
                                                    AM_PM="PM";
                                                }
                                                else
                                                    visitTime = hourString;
                                            }
                                            else if(timeIndex==1)
                                            {
                                                minString=timeTokenizer.nextToken();
                                                if(Integer.parseInt(minString)<10)
                                                    minString = "0"+minString;
                                                visitTime = hourString+":"+minString+" " +AM_PM;
                                            }
                                            else
                                                timeTokenizer.nextToken();
                                            timeIndex++;
                                        }

                                    }
                                    index++;
                                }
                                ((EditText) findViewById(R.id.txtAppointmentDate)).setText(visitDate);
                                ((EditText) findViewById(R.id.txtAppointmentTime)).setText(visitTime);


                                JSONArray departmentArray = json.getJSONArray("departments");
                                List<String> listDepartments = new ArrayList<String>();

                                if (departmentArray != null && departmentArray.length() > 0) {
                                    for (int i = 0; i < departmentArray.length(); i++) {
                                        listDepartments.add(departmentArray.get(i).toString());
                                    }
                                }


                                Spinner spinnerDepartments = (Spinner) findViewById(R.id.spinnerDepartment);
                                listDepartments.add(0, "Select a department");
                                ArrayAdapter<String> dataAdapterDepartments = new ArrayAdapter<String>(EditActivity.this,
                                        android.R.layout.simple_spinner_item, listDepartments);
                                dataAdapterDepartments.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerDepartments.setAdapter(dataAdapterDepartments);


                                spinnerDepartments.setSelection(listDepartments.indexOf(appointment.getString("SpecializationName")));


                                JSONArray physiciansArray = json.getJSONArray("physicians");
                                List<String> listPhysicians = new ArrayList<String>();

                                if (physiciansArray != null && physiciansArray.length() > 0) {
                                    for (int i = 0; i < physiciansArray.length(); i++) {
                                        listPhysicians.add(physiciansArray.get(i).toString());
                                    }
                                }

                                listPhysicians.add(0, "Select a physician");

                                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(EditActivity.this,
                                        android.R.layout.simple_spinner_item, listPhysicians);
                                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                ((Spinner) EditActivity.this.findViewById(R.id.spinnerPhysician)).setAdapter(dataAdapter);
                                ((Spinner) EditActivity.this.findViewById(R.id.spinnerPhysician)).setSelection(listPhysicians.indexOf(appointment.getString("phy_Name")));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            return null;
        }

        protected void onPostExecute(String str)
        {

        }
    }


}

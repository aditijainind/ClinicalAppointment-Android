package aditij.assignment4;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import aditij.assignment4.Helpers.JSONParser;
import aditij.assignment4.data.Appointment;
import aditij.assignment4.data.ClinicDataSource;
import android.os.AsyncTask;

public class MainActivity extends Activity {
    private ClinicDataSource dataSource;
    private Appointment appointment;
    private List<Appointment> allAppointments;
    JSONParser jParser = new JSONParser();
    private static String url_all_appointments ="http://people.cs.clemson.edu/~aditij/getAllAppointments.php";
    private static String url_delete_appointment ="http://people.cs.clemson.edu/~aditij/deleteAppointment.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataSource = new ClinicDataSource(this);
        try
        {
            dataSource.open();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        //allAppointments = dataSource.getAllAppointments();
        allAppointments = new ArrayList<Appointment>();
        new LoadAllAppointments().execute();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Intent intent = new Intent(MainActivity.this,
                    SetPreferenceActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void editAppointment(View view)
    {
        View parentView =(View)view.getParent().getParent();
        String currentMappingId = ((TextView)parentView.findViewById(R.id.txtMappingId)).getText().toString();

        Intent intent = new Intent(this,EditActivity.class);
        intent.putExtra("SelectedAppointmentId", currentMappingId);
        startActivity(intent);
    }

    public void deleteAppointment(View view)
    {

        final View currentView=view;
        new AlertDialog.Builder(this)
                .setTitle("Delete Appointment")
                .setMessage("Are you sure you want to delete?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        doDelete(currentView);
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create().show();


    }

    public void downloadDiagnosis(View view)
    {
        View parentView =(View)view.getParent().getParent().getParent();
        String currentDiagnosis = ((TextView)parentView.findViewById(R.id.txtDiagnosis)).getText().toString();

        new FileDownloader().execute(currentDiagnosis);
    }

    public void uploadDiagnosis(View view)
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath()
                );
        intent.setDataAndType(uri, "application/*");
        startActivityForResult(Intent.createChooser(intent, "Open folder"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result)
    {
        String fileUri = result.getDataString();

        //new FileUploader().execute(fileUri.toString());
        new FileUploader().execute(fileUri);
    }


    public void doDelete(View currentView) {

        View parentView =(View)currentView.getParent().getParent();
        String currentMappingId = ((TextView)parentView.findViewById(R.id.txtMappingId)).getText().toString();

        TableLayout rootTableView =(TableLayout)parentView.getParent();
        rootTableView.removeView(parentView);

        new AppointmentDeleter().execute(currentMappingId);

        //dataSource.deleteAppointmentById(Long.parseLong(currentMappingId,10));

    }
    public void addAppointment(View view)
    {

        Intent intent = new Intent(this,EditActivity.class);
        intent.putExtra("SelectedAppointmentId", "-1");
        startActivity(intent);
    }

    public void displayInfoPage(View view)
    {
        Intent intent = new Intent(this,InfoActivity.class);
        startActivity(intent);
    }


    class LoadAllAppointments extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        //
        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            final JSONObject json = jParser.makeHttpRequest(url_all_appointments, "GET", params);

                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            int success = json.getInt("success");
                            if (success == 1) {
                                JSONArray appointmentArray = json.getJSONArray("appointments");
                                for (int i = 0; i < appointmentArray.length(); i++) {
                                    JSONObject c = appointmentArray.getJSONObject(i);
                                    Appointment appointment = new Appointment();
                                    appointment.setPatientName(c.getString("patientName"));
                                    appointment.setPhysicianName(c.getString("phy_Name"));
                                    appointment.setPhysicianDepartment(c.getString("SpecializationName"));
                                    appointment.setMappingId(Long.parseLong(c.getString("MappingId")));
                                    appointment.setVisitTime(c.getString("VisitingTime"));
                                    appointment.setDiagnosis(c.getString("Diagnosis"));
                                    allAppointments.add(appointment);

                                }
                            }

                            TableLayout tblAllAppointments = (TableLayout)MainActivity.this.findViewById(R.id.tblAppointment);

                            for(int currentIndex=0; currentIndex<allAppointments.size();currentIndex++) {

                                View newRow = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_item,null);
                                ((TextView)newRow.findViewById(R.id.txtPatient)).setText(allAppointments.get(currentIndex).getPatientName());
                                ((TextView)newRow.findViewById(R.id.txtPhysician)).setText(allAppointments.get(currentIndex).getPhysicianName());
                                ((TextView)newRow.findViewById(R.id.txtSpecialization)).setText(allAppointments.get(currentIndex).getPhysicianDepartment());

                                ((TextView)newRow.findViewById(R.id.txtDiagnosis)).setText(allAppointments.get(currentIndex).getDiagnosis());
                                if(allAppointments.get(currentIndex).getDiagnosis().trim().isEmpty())
                                {
                                    ((Button) newRow.findViewById(R.id.btnDownload)).setVisibility(View.GONE);
                                }

                                StringTokenizer tokenizer = new StringTokenizer(allAppointments.get(currentIndex).getVisitTime(), " ");
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



                                ((TextView)newRow.findViewById(R.id.txtVisitTime)).setText(visitDate + " " + visitTime);
                                ((TextView)newRow.findViewById(R.id.txtMappingId)).setText(Long.toString(allAppointments.get(currentIndex).getMappingId()));
                                tblAllAppointments.addView(newRow);

                                View v = new View(MainActivity.this);
                                v.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, 1));
                                v.setBackgroundColor(Color.rgb(51, 51, 51));

                                tblAllAppointments.addView(v);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }}
                    );


            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
        }


    }

    class AppointmentDeleter extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        //
        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("reqPatientId",args[0]));
            final JSONObject json = jParser.makeHttpRequest(url_delete_appointment,"POST", params);

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
        }


    }

    class FileDownloader extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        //
        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            try{
            URL url = new URL("http://localhost/ClinicApplication/documents/CACC_SoftwareArchitectureDocument.docx");

                String fileUrl ="http://people.cs.clemson.edu/~aditij/documents/"+ args[0];   // -> http://maven.apache.org/maven-1.x/maven.pdf
                String fileName = args[0];  // -> maven.pdf
                String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
                File folder = new File(extStorageDirectory+"/", "ClinicDocs");
                folder.mkdir();

                File pdfFile = new File(folder, fileName);

                try{
                    pdfFile.createNewFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                FileDownloaderExt.downloadFile(fileUrl, pdfFile); ;
                return null;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
        }


    }

    class FileUploader extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        //
        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            String url = "http:///ClinicApplication/documents/test.pdf";
            File file = new File("\\sdcard\\testthreepdf\\1749.pdf");
            try {
                HttpClient httpclient = new DefaultHttpClient();

                HttpPost httppost = new HttpPost(url);

                InputStreamEntity reqEntity = new InputStreamEntity(new FileInputStream(file), -1);
                reqEntity.setContentType("binary/octet-stream");
                reqEntity.setChunked(true); // Send in multiple parts if needed
                httppost.setEntity(reqEntity);
                HttpResponse response = httpclient.execute(httppost);
                //Do something with response...

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
        }


    }
}

package aditij.assignment4.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by aditij on 3/13/2015.
 */
public class AlarmService extends IntentService {

    public static final String CREATE = "CREATE";
    public static final String CANCEL = "CANCEL";

    private IntentFilter matcher;

    public AlarmService() {
        super(AlarmService.class.getSimpleName());
        matcher = new IntentFilter();
        matcher.addAction(CREATE);
        matcher.addAction(CANCEL);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        String alarmId = intent.getStringExtra("alarmId");
        String alarmLabel = intent.getStringExtra("alarmLabel");
        String alarmHH = intent.getStringExtra("alarmHH");
        String alarm_mm = intent.getStringExtra("alarm_mm");
        String alarmDate = intent.getStringExtra("alarmDate");
        String alarmAction = intent.getStringExtra("alarmAction");
        int reminderTime = Integer.valueOf(intent.getStringExtra("reminderTime"));
        //if (matcher.matchAction(action))
        {
            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            Intent i = new Intent(this, AlarmBroadcastReceiver.class);
            i.putExtra("alarmId", alarmId);
            i.putExtra("alarmLabel",alarmLabel);
            i.putExtra("alarmHH",alarmHH);
            i.putExtra("alarm_mm",alarm_mm);
            i.putExtra("alarmDate", alarmDate);
            i.putExtra("alarmYear",intent.getStringExtra("alarmYear"));
            i.putExtra("alarmMonth",intent.getStringExtra("alarmMonth"));
            i.putExtra("alarmDate", intent.getStringExtra("alarmDate"));
            PendingIntent pi = PendingIntent.getBroadcast(this, 0, i,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            switch(alarmAction.toLowerCase()) {
                case "create":
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());

                    calendar.set(Calendar.DATE,Integer.valueOf(intent.getStringExtra("alarmDate")));
                    calendar.set(Calendar.MONTH,Integer.valueOf(intent.getStringExtra("alarmMonth")));
                    calendar.set(Calendar.YEAR,Integer.valueOf(intent.getStringExtra("alarmYear")));
                    calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(intent.getStringExtra("alarmHH")));
                    calendar.set(Calendar.MINUTE, Integer.valueOf(intent.getStringExtra("alarm_mm")));
                    calendar.add(Calendar.MINUTE,reminderTime*-1);
                    manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                            pi);
                    break;
                case "delete":
                    manager.cancel(pi);
                    break;
                default:break;
            }
        }
    }

}

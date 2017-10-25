package aditij.assignment4;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by aditij on 3/13/2015.
 */
public class SetPreferenceActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new NotificationsSettingsActivity()).commit();
    }

}
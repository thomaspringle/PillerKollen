package se.tpr.pillerkollen;

import se.tpr.pillerkollen.alarm.AlarmActivity;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class SettingsFragment extends Fragment {

	// TODO:
	// reminders - remind to take medicine
	// reminders - remind to renew medicine
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
		
		Button alarmButton = (Button)rootView.findViewById(R.id.setAlarmButton);
		final Activity context = this.getActivity();
		final AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		
		alarmButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setAlarm(10, alarmManager, context);

			}
		});
        return rootView;
	}
	
	public static final void setAlarm(int seconds, AlarmManager alarmManager, Context context) {
		// create the pending intent
		Intent intent = new Intent(context, AlarmActivity.class);
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
		// get the alarm manager, and scedule an alarm that triggers my activity
		alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + seconds * 1000, pendingIntent);
		Toast.makeText(context, "Timer set to " + seconds + " seconds.", Toast.LENGTH_SHORT).show();	
	}
}

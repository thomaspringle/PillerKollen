package se.tpr.pillerkollen.alarm;

import java.util.List;

import se.tpr.pillerkollen.R;
import se.tpr.pillerkollen.SettingsFragment;
import se.tpr.pillerkollen.R.id;
import se.tpr.pillerkollen.R.layout;
import se.tpr.pillerkollen.schedule.ScheduleTime;
import se.tpr.pillerkollen.schedule.ScheduleViewDto;
import se.tpr.pillerkollen.schedule.ScheduleViewExpandableListAdapter;
import se.tpr.pillerkollen.schedule.SchedulesDataSource;
import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

public class AlarmActivity extends Activity {

	// TODO: 
	// Create notification with the option to cancel the snooze time
	// Play sound
	// Vibrate
	// Create service that schedules events on reboot and / or when a certain time has elapsed
	private SchedulesDataSource schedulesDatasource;
	private Activity context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_alarm);
		
		
		schedulesDatasource = new SchedulesDataSource(this);
		schedulesDatasource.open();
		context = this;
		
		List<ScheduleViewDto> schedules = schedulesDatasource.getAllScheduleViewsForTime(ScheduleTime.MORNING);
		ListView listView = (ListView)findViewById(R.id.alarm_medicine_list);
		AlertMedicinesListAdapter adapter = new AlertMedicinesListAdapter(context, schedules);
		listView.setAdapter(adapter);
		
		Button dismiss = (Button) findViewById(R.id.dismiss_button);
		Button snooze = (Button) findViewById(R.id.snooze_button);
		
		dismiss.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		snooze.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SettingsFragment.setAlarm(10, ((AlarmManager) getSystemService(ALARM_SERVICE)), context);
				finish();
			}
		});
	}

	@Override
	public void onResume() {
		schedulesDatasource.open();
		super.onResume();
	}

	@Override
	public void onPause() {
		schedulesDatasource.close();
		super.onPause();
	}
}

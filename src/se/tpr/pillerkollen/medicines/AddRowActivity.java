package se.tpr.pillerkollen.medicines;

import java.util.ArrayList;
import java.util.List;

import se.tpr.pillerkollen.R;
import se.tpr.pillerkollen.schedule.SchedulesDataSource;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class AddRowActivity extends Activity {

	private Context context;
	private MedicinesDataSource medicinesDatasource;
	private SchedulesDataSource schedulesDatasource;

	
	private String name = "";
	private String type = "";
	private String dosage = "";
	private String unit = "";
	private String description = "";
	private List<String> scheduledTimes;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_row);
		getActionBar().setDisplayShowTitleEnabled(true);
		getActionBar().setTitle(getString(R.string.add_row));
		
		
		context = this;
		
		medicinesDatasource = new MedicinesDataSource(context);
		medicinesDatasource.open();
		
		schedulesDatasource = new SchedulesDataSource(context);
		schedulesDatasource.open();
				
		setButtonListeners();

	}

	@Override
	public void finish() {
		super.finish();
	}
	  
	private void setButtonListeners() {
		Button addButton = (Button) findViewById(R.id.add_row_add_button);
		addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addRow();
//				finish();
			}
		});
		Button cancelButton = (Button) findViewById(R.id.add_row_cancel_button);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				setResult(RESULT_CANCELED, intent);
				finish();
			}
		});
	}

	protected void addRow() {

		name = nullCheck((EditText) findViewById(R.id.add_row_medicine_name_input));
		type = nullCheck((EditText) findViewById(R.id.add_row_medicine_type_input));
		description = nullCheck((EditText) findViewById(R.id.add_row_medicine_description_input));
		dosage = nullCheck((EditText) findViewById(R.id.add_row_medicine_dosage_input));
		unit = nullCheck((EditText) findViewById(R.id.add_row_medicine_unit_input));
		
		List<String> schedules = findScheduledTimes();
		
		this.scheduledTimes = schedules;
		String missingField = checkForMissingFields();
		
		if (missingField.isEmpty()) {
			new AddRowTask().execute();
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			// Add the button
			builder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   dialog.dismiss();
			           }
			       });
			builder.setMessage(getString(R.string.add_row_missing_value) + " " + missingField).setTitle(R.string.add_row_missing_title);

			builder.create().show();
		}
		
	}

	private List<String> findScheduledTimes() {
		List<String> schedules = new ArrayList<String>();
		
		CheckBox cb0800 = (CheckBox) findViewById(R.id.add_row_checkbox_1);
		CheckBox cb1200 = (CheckBox) findViewById(R.id.add_row_checkbox_2);
		CheckBox cb2000 = (CheckBox) findViewById(R.id.add_row_checkbox_3);
		CheckBox cb2400 = (CheckBox) findViewById(R.id.add_row_checkbox_4);
		
		if (cb0800.isChecked()) {
			schedules.add(cb0800.getText().toString());
		}
		if (cb1200.isChecked()) {
			schedules.add(cb1200.getText().toString());
		}
		if (cb2000.isChecked()) {
			schedules.add(cb2000.getText().toString());
		}
		if (cb2400.isChecked()) {
			schedules.add(cb2400.getText().toString());
		}
		return schedules;
	}

	private String checkForMissingFields() {
		String missingField = "";
		
		if (name.isEmpty() ) {
			missingField = getString(R.string.medicines_title_name);
		
		} else if (type.isEmpty()) {
			missingField = getString(R.string.medicines_title_type);
		
		} else if (dosage.isEmpty()) {
			missingField = getString(R.string.medicines_title_dosage);
		
		} else if (unit.isEmpty()) {
			missingField = getString(R.string.medicines_title_unit);
		
		} else if (description.isEmpty()) {
//			missingField = getString(R.string.medicines_title_description);
		}
		return missingField;
	}

	private String nullCheck(EditText editText) {
		String value = editText.getText().toString();
		return value == null ? "" : value;
	}

	public void showErrorDialog(Exception exception) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		Resources res = getResources();
		builder
	      .setTitle(res.getString(R.string.error_title))
	      .setMessage(exception.getMessage())
	      .setPositiveButton(res.getString(R.string.ok_button), null)
	      .show();	
	}

	@Override
	public void onResume() {
		medicinesDatasource.open();	
		schedulesDatasource.open();
		super.onResume();
	}

	@Override
	public void onPause() {
		medicinesDatasource.close();
		schedulesDatasource.close();
		super.onPause();
	}
	
	public class AddRowTask extends AsyncTask<Void, Void, Long> {

		private Exception exception;
		private ProgressDialog progress;

		@Override
		protected void onPreExecute() {
            progress = new ProgressDialog(context, 0);
            progress.setMessage(getResources().getString(R.string.progress_indicator));
            progress.show();
		}

		@Override
		protected Long doInBackground(Void... arg0) {
			try {
				Medicine createdMedicine = medicinesDatasource.createMedicine(name, type, description, dosage, unit);
				
				for (String time : scheduledTimes) {
					
				}
//				String lineNo = AltranHttpClient.addRow(selectedCustomer, selectedProject, selectedTask, comment);
				return createdMedicine.getId();

			} catch (Exception e) {
				e.printStackTrace();
				this.exception = e;
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(Long lineId) {
			if (progress != null) {
				progress.dismiss();
			}
			
			if (this.exception != null) {
				showErrorDialog(exception);
			} else {
				Intent intent = new Intent();
				setResult(RESULT_OK, intent);
				intent.putExtra("name_field", name);
				intent.putExtra("type_field", type);
				intent.putExtra("description_field", description);
				intent.putExtra("dosage_field", dosage);
				intent.putExtra("unit_field", unit);
				
				finish();
			}
			super.onPostExecute(lineId);
		}
	}
}
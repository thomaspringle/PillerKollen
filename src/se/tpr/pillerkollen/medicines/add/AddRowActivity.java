package se.tpr.pillerkollen.medicines.add;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import se.tpr.pillerkollen.R;
import se.tpr.pillerkollen.medicines.Medicine;
import se.tpr.pillerkollen.medicines.MedicinesDataSource;
import se.tpr.pillerkollen.schedule.SchedulesDataSource;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class AddRowActivity extends Activity implements OnItemSelectedListener {

	private AddRowActivity context;
	private ViewFlipper viewFlipper;
//	private ListView dosagesListView;
	DosagesArrayAdapter dosagesArrayAdapter;
	
	private MedicinesDataSource medicinesDatasource;
	private SchedulesDataSource schedulesDatasource;


	private String name = "";
	private String type = "";
	private String dosage = "";
	private List<Dosage> dosages;
	private String unit = "";
	private String description = "";
	private List<String> scheduledTimes;
	private Time scheduleEndTime = null;
	private Time scheduleStartTime = null;
	private Time scheduleTime = new Time(); 

	private Spinner freqSpinner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_row);
		getActionBar().setDisplayShowTitleEnabled(true);
		getActionBar().setTitle(getString(R.string.add_row));

		scheduleTime.setToNow();

		viewFlipper = (ViewFlipper) findViewById(R.id.add_row_medicines_view_flipper);

		context = this;

		medicinesDatasource = new MedicinesDataSource(context);
		medicinesDatasource.open();

		schedulesDatasource = new SchedulesDataSource(context);
		schedulesDatasource.open();

		setButtonListeners();
		setupDismissKeyboard(viewFlipper);


		freqSpinner = (Spinner) findViewById(R.id.add_row_spinner_freq);
		freqSpinner.setOnItemSelectedListener(this);
		ArrayAdapter<CharSequence> freqAdapter = ArrayAdapter.createFromResource(this, 
				R.array.recurrence_freq, 
				R.layout.recurrencepicker_freq_item);

		freqAdapter.setDropDownViewResource(R.layout.recurrencepicker_freq_item);
		freqSpinner.setAdapter(freqAdapter);
		
		ListView dosagesListView = (ListView) findViewById(R.id.add_row_page1_dosage_list);
		dosages = new ArrayList<Dosage>();
		dosages.add(new Dosage());
		dosagesArrayAdapter = new DosagesArrayAdapter(this, dosages);
		dosagesListView.setAdapter(dosagesArrayAdapter);
		
	}
	public void setupDismissKeyboard(View view) {
		//Set up touch listener for non-text box views to hide keyboard.
		if(!(view instanceof EditText)) {

			view.setOnTouchListener(new OnTouchListener() {
				@SuppressLint("ClickableViewAccessibility")
				public boolean onTouch(View v, MotionEvent event) {
//					v.performClick();
					hideSoftKeyBoard(v);
					return false;
				}
			});
		}
		//If a layout container, iterate over children and seed recursion.
		if (view instanceof ViewGroup) {

			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				View innerView = ((ViewGroup) view).getChildAt(i);
				setupDismissKeyboard(innerView);
			}
		}
	}
	private void hideSoftKeyBoard(View view) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}


	@Override
	public void finish() {
		super.finish();
	}

	private void updateSchedulePage() {
		collectValuesFromPage1();

		TextView unit1 = (TextView) findViewById(R.id.add_row_medicine_unit1);
		TextView unit2 = (TextView) findViewById(R.id.add_row_medicine_unit2);
		TextView unit3 = (TextView) findViewById(R.id.add_row_medicine_unit3);
		TextView unit4 = (TextView) findViewById(R.id.add_row_medicine_unit4);
		unit1.setText(this.unit);
		unit2.setText(this.unit);
		unit3.setText(this.unit);
		unit4.setText(this.unit);

		if (scheduleStartTime == null) {
			scheduleStartTime = new Time(scheduleTime);
			
			final String dateStr = DateUtils.formatDateTime(this, scheduleStartTime.toMillis(false), DateUtils.FORMAT_NUMERIC_DATE);
			TextView startDate = (TextView) findViewById(R.id.add_row_medicine_start_date);
			startDate.setText(dateStr);
		}

		if (scheduleEndTime == null) {
			scheduleEndTime = new Time(scheduleTime);
			scheduleEndTime.month += 3;
			scheduleEndTime.normalize(false);
			
			final String dateStr = DateUtils.formatDateTime(this, scheduleEndTime.toMillis(false), DateUtils.FORMAT_NUMERIC_DATE);
			TextView endDate = (TextView) findViewById(R.id.add_row_medicine_end_date);
			endDate.setText(dateStr);
		}
	}

	private void setButtonListeners() {
		View nextButton = (View) findViewById(R.id.add_row_page1_next_button_container);
		nextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hideSoftKeyBoard(v);
				if (viewFlipper.getDisplayedChild() == 0) {
					updateSchedulePage();
					viewFlipper.setInAnimation(context, R.anim.in_from_right);
					viewFlipper.setOutAnimation(context, R.anim.out_to_left);
					viewFlipper.showNext();
				}
			}
		});
		View previousButton = (View) findViewById(R.id.add_row_page2_previous_button_container);
		previousButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hideSoftKeyBoard(v);
				if (viewFlipper.getDisplayedChild() == 1) {

					viewFlipper.setInAnimation(context, R.anim.in_from_left);
					viewFlipper.setOutAnimation(context, R.anim.out_to_right);
					viewFlipper.showPrevious();
				}
			}
		});

		View addButton = (View) findViewById(R.id.add_row_page2_add_button_container);
		addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hideSoftKeyBoard(v);
				if (viewFlipper.getDisplayedChild() == 1) {
					addRow();
				}
			}
		});

		View cancelButtonPage1 = (View) findViewById(R.id.add_row_page1_cancel_button_container);
		cancelButtonPage1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hideSoftKeyBoard(v);
				Intent intent = new Intent();
				setResult(RESULT_CANCELED, intent);
				finish();
			}
		});
		View cancelButtonPage2 = (View) findViewById(R.id.add_row_page2_cancel_button_container);
		cancelButtonPage2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hideSoftKeyBoard(v);
				Intent intent = new Intent();
				setResult(RESULT_CANCELED, intent);
				finish();
			}
		});

		View addDosage = (View) findViewById(R.id.add_row_page1_dosages_container);
		addDosage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hideSoftKeyBoard(v);
				
				dosages.add(new Dosage());
				ListView dosagesListView = (ListView) findViewById(R.id.add_row_page1_dosage_list);
				
//				View childAt = dosagesListView.getChildAt(0);
//				int height = childAt.getMeasuredHeight();
//				Log.d(this.getClass().getName(), "Height of row: " + height);
				
				dosagesArrayAdapter = new DosagesArrayAdapter(context, dosages);
				dosagesListView.setAdapter(dosagesArrayAdapter);
				
				/*
				LayoutParams params = dosagesListView.getLayoutParams();
				int rowHeight = getResources().getDimensionPixelSize(R.dimen.add_dosage_row_height);
				int padding = getResources().getDimensionPixelSize(R.dimen.add_dosage_padding);
				params.height = rowHeight * dosages.size() + padding;
				Log.d(this.getClass().getName(), "Height of row: " + params.height);
//				dosagesListView.setLayoutParams(params);
				dosagesListView.setMinimumHeight((params.height) * dosages.size());*/
				updateListViewHeight();
			}
		});
		
	}
	private void updateListViewHeight() {
		ListView dosagesListView = (ListView) findViewById(R.id.add_row_page1_dosage_list);
		LayoutParams params = dosagesListView.getLayoutParams();
		int rowHeight = getResources().getDimensionPixelSize(R.dimen.add_dosage_row_height);
		int padding = getResources().getDimensionPixelSize(R.dimen.add_dosage_padding);
		params.height = rowHeight * dosages.size() + padding;
		Log.d(this.getClass().getName(), "Height of view: " + params.height);
//		dosagesListView.setLayoutParams(params);
		dosagesListView.setMinimumHeight((params.height) * dosages.size());

	}
	protected void removeDosage(Dosage dosageToRemove) {
		updateListViewHeight();
//		Iterator<Dosage> iter = dosages.iterator();
//		while (iter.hasNext()) {
//			Dosage dosage = iter.next();
//			if(dosage.hasId(dosageToRemove.getId())) {
//				iter.remove();
//				return;
//			}
//		}
	}
	protected void addRow() {

		collectValuesFromPage1();

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
	private void collectValuesFromPage1() {
		name = nullCheck((EditText) findViewById(R.id.add_row_medicine_name_input));
		type = nullCheck((EditText) findViewById(R.id.add_row_medicine_type_input));
		description = nullCheck((EditText) findViewById(R.id.add_row_medicine_description_input));
		dosage = nullCheck((EditText) findViewById(R.id.add_row_medicine_dosage_input));
		unit = nullCheck((EditText) findViewById(R.id.add_row_medicine_unit_input));
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
			missingField = getString(R.string.medicines_hint_name);

		} else if (type.isEmpty()) {
			missingField = getString(R.string.medicines_hint_type);

		} else if (dosage.isEmpty()) {
			missingField = getString(R.string.medicines_hint_dosage);

		} else if (unit.isEmpty()) {
			missingField = getString(R.string.medicines_hint_unit);

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

	// Implements OnItemSelectedListener interface
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		if (parent == freqSpinner) {
			//mModel.freq = position;
		}
	}

	// Implements OnItemSelectedListener interface
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
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

				// TODO: Add to Schedule:
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

package se.tpr.pillerkollen.medicines.add;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import se.tpr.pillerkollen.R;
import se.tpr.pillerkollen.medicines.Medicine;
import se.tpr.pillerkollen.medicines.MedicinesDataSource;
import se.tpr.pillerkollen.schedule.SchedulesDataSource;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class AddRowActivity extends Activity implements OnItemSelectedListener {

	private AddRowActivity context;
	private ViewFlipper viewFlipper;

	private AddDosagesController addDosagesController;

	private MedicinesDataSource medicinesDatasource;
	private SchedulesDataSource schedulesDatasource;


	private static final String UNIT_FIELD = "unit_field";
	private static final String DOSAGES_FIELD = "dosages_field";
	private static final String DESCRIPTION_FIELD = "description_field";
	private static final String TYPE_FIELD = "type_field";
	private static final String NAME_FIELD = "name_field";
	private static final String ID_FIELD = "id_field";

	private String name = "";
	private String type = "";

	//	private List<Dosage> dosages;
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

		// TODO: Hide and show action bar when scrolling?
		//		getActionBar().setDisplayShowTitleEnabled(true);
		//		getActionBar().setTitle(getString(R.string.add_row));
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		getActionBar().hide();
		setContentView(R.layout.activity_add_row);

		scheduleTime.setToNow();

		viewFlipper = (ViewFlipper) findViewById(R.id.add_row_medicines_view_flipper);

		context = this;

		medicinesDatasource = new MedicinesDataSource(context);
		medicinesDatasource.open();

		schedulesDatasource = new SchedulesDataSource(context);
		schedulesDatasource.open();

		
		setButtonListenersPage1();
		setupScheduleTimes();
		setButtonListenersPage2();
		setupDismissKeyboard(viewFlipper);


		freqSpinner = (Spinner) findViewById(R.id.add_row_spinner_freq);
		freqSpinner.setOnItemSelectedListener(this);
		ArrayAdapter<CharSequence> freqAdapter = ArrayAdapter.createFromResource(this, 
				R.array.recurrence_freq, 
				R.layout.recurrencepicker_freq_item);

		freqAdapter.setDropDownViewResource(R.layout.recurrencepicker_freq_item);
		freqSpinner.setAdapter(freqAdapter);


		addDosagesController = new AddDosagesController(this);
		restoreState(savedInstanceState);
		addDosagesController.reDrawTable();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Medicine medicine = collectValuesFromPage1();

		outState.putString(NAME_FIELD, medicine.getName());
		outState.putString(TYPE_FIELD, medicine.getType());
		outState.putString(DESCRIPTION_FIELD, medicine.getDescription());
		outState.putLong(ID_FIELD, medicine.getId());


		if (!addDosagesController.dosages.isEmpty()) {
			List<String> dosageStrings = getDosages();
			String dosageString = "";
			for (String dosage : dosageStrings) {
				dosageString +=dosage + ",";
			}
			//			String[] typ = new String[1];
			//			String[] dosagesArray = getDosages().toArray(typ);
			//			outState.putString(DOSAGES_FIELD, Arrays.toString(dosagesArray));
			outState.putString(DOSAGES_FIELD, dosageString);
			Log.d(this.getClass().getName(), "Dosages on save: " + dosageString);
			outState.putString(UNIT_FIELD, addDosagesController.dosages.get(0).getUnit());

		}
	}

	//	@Override
	//	protected void onRestoreInstanceState(Bundle inState) {
	//		super.onRestoreInstanceState(inState);
	//		
	//		restoreState(inState);
	//	}

	private void restoreState(Bundle inState) {
		List<Dosage> dosages = new ArrayList<Dosage>();
		dosages.add(new Dosage());


		if (inState != null) {
			if (inState.containsKey(NAME_FIELD)) {
				EditText nameInput = (EditText)findViewById(R.id.add_row_medicine_name_input);
				nameInput.setText(inState.getString(NAME_FIELD));
			}
			if (inState.containsKey(TYPE_FIELD)) {
				EditText typeInput = (EditText)findViewById(R.id.add_row_medicine_type_input);
				typeInput.setText(inState.getString(TYPE_FIELD));
			}
			if (inState.containsKey(DESCRIPTION_FIELD)) {
				EditText descInput = (EditText)findViewById(R.id.add_row_medicine_description_input);
				descInput.setText(inState.getString(DESCRIPTION_FIELD));
			}
			if (inState.containsKey(DOSAGES_FIELD)) {

				String dosagesString = inState.getString(DOSAGES_FIELD);
				Log.d(this.getClass().getName(), "Dosages on restore: " + dosagesString);
				dosagesString = dosagesString.substring(0, dosagesString.length());
				String unitString = inState.getString(UNIT_FIELD);

				String[] dosageArray = dosagesString.split(",");
				addDosagesController.reBuildDosages(dosageArray, unitString);
			}
		}
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
		View rootView = findViewById(R.id.add_row_medicines_view_flipper);
		rootView.requestFocus();
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

		
		TextView startDate = (TextView) findViewById(R.id.add_row_medicine_start_date);
		
		if (startDate.getText() == null || startDate.getText().toString().isEmpty()) {
			final String startDateStr = DateUtils.formatDateTime(this, scheduleStartTime.toMillis(false), DateUtils.FORMAT_NUMERIC_DATE);
			startDate.setText(startDateStr);	
		}
		
		TextView endDate = (TextView) findViewById(R.id.add_row_medicine_end_date);
		
		if (endDate.getText() == null || endDate.getText().toString().isEmpty()) {
			final String endDateStr = DateUtils.formatDateTime(this, scheduleEndTime.toMillis(false), DateUtils.FORMAT_NUMERIC_DATE);
			endDate.setText(endDateStr);	
		}
	}

	private void setupScheduleTimes() {
		if (scheduleStartTime == null) {
			scheduleStartTime = new Time(scheduleTime);
		}
		if (scheduleEndTime == null) {
			scheduleEndTime = new Time(scheduleTime);
			scheduleEndTime.month += 3;
			scheduleEndTime.normalize(false);
		}
	}

	private void setButtonListenersPage1() {
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


		View addDosage = (View) findViewById(R.id.add_row_page1_dosages_container);
		addDosage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hideSoftKeyBoard(v);

				addDosagesController.addDosage();
			}
		});

	}
	private void setButtonListenersPage2() {
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
					createMedicine();
				}
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

		View changeStartDate = findViewById(R.id.add_row_medicine_start_date);
		changeStartDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int selectedYear = scheduleStartTime.year; 
				int selectedMonth = scheduleStartTime.month;
				int selectedDayOfMonth = scheduleStartTime.monthDay;

				DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
						scheduleStartTime = new Time(scheduleTime);

						scheduleStartTime.year = year; 
						scheduleStartTime.month = monthOfYear;
						scheduleStartTime.monthDay = dayOfMonth;
						
						final String dateStr = DateUtils.formatDateTime(context, scheduleStartTime.toMillis(false), DateUtils.FORMAT_NUMERIC_DATE);
						TextView startDate = (TextView) findViewById(R.id.add_row_medicine_start_date);
						startDate.setText(dateStr);

					}
				}, selectedYear, selectedMonth, selectedDayOfMonth);
				datePickerDialog.show();
			}
		});
		
		View changeEndDate = findViewById(R.id.add_row_medicine_end_date);
		changeEndDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int selectedYear = scheduleEndTime.year; 
				int selectedMonth = scheduleEndTime.month;
				int selectedDayOfMonth = scheduleEndTime.monthDay;

				DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
						scheduleEndTime = new Time(scheduleTime);

						scheduleEndTime.year = year; 
						scheduleEndTime.month = monthOfYear;
						scheduleEndTime.monthDay = dayOfMonth;
						
						final String dateStr = DateUtils.formatDateTime(context, scheduleEndTime.toMillis(false), DateUtils.FORMAT_NUMERIC_DATE);
						TextView startDate = (TextView) findViewById(R.id.add_row_medicine_end_date);
						startDate.setText(dateStr);

					}
				}, selectedYear, selectedMonth, selectedDayOfMonth);
				datePickerDialog.show();
			}
		});
	}
	protected void createMedicine() {

		Medicine medicine = collectValuesFromPage1();

		List<String> schedules = findScheduledTimes();

		this.scheduledTimes = schedules;
		String missingField = checkForMissingFields();

		// TODO: check for incorrect page2 values?

				// Create a row for each dosage


				if (missingField.isEmpty()) {
					// Create a medicine for each dosage (medA, 10mg; medA, 5mg)
					List<Medicine> medicines = new ArrayList<Medicine>();
					for (Dosage dosage : addDosagesController.dosages) {
						String dosageValue = dosage.getDosage();
						if (dosageValue == null || dosageValue.trim().isEmpty()) {
							continue;
						}

						Medicine newMedicine = new Medicine(medicine);

						medicine.setDosage(dosageValue);
						medicine.setUnit(dosage.getUnit());
						medicines.add(newMedicine);
					}
					new AddRowsTask(medicines).execute();
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
	private Medicine collectValuesFromPage1() {

		Medicine medicine = new Medicine();

		medicine.setName(nullCheck((EditText) findViewById(R.id.add_row_medicine_name_input)));
		medicine.setType(nullCheck((EditText) findViewById(R.id.add_row_medicine_type_input)));
		medicine.setDescription(nullCheck((EditText) findViewById(R.id.add_row_medicine_description_input)));

		return medicine;
	}

	private List<String> getDosages() {
		List<String> result = new ArrayList<String>();
		for (Dosage dosage : addDosagesController.dosages) {
			result.add(dosage.getDosage());
		}
		return result;
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

		} else if (dosagesAreEmpty()) {
			missingField = getString(R.string.medicines_hint_dosage);

		} else if (unit.isEmpty()) {
			missingField = getString(R.string.medicines_hint_unit);

		} else if (description.isEmpty()) {
			// description is optional
		}

		return missingField;
	}

	private boolean dosagesAreEmpty() {

		if (addDosagesController.dosages.isEmpty()) {
			return true;
		}
		String dosage = addDosagesController.dosages.get(0).getDosage();
		String unit = addDosagesController.dosages.get(0).getUnit();
		if (dosage.isEmpty() || unit.isEmpty()) {
			return true;
		}
		return false;
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
		// TODO: All values already added must be saved and re-populated
		// TODO: Dosages as well.
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

	public class AddRowsTask extends AsyncTask<Void, Void, List<Long>> {

		private Exception exception;
		private ProgressDialog progress;
		private List<Medicine> medicines;

		public AddRowsTask(List<Medicine> medicines) {
			this.medicines = medicines;
		}

		@Override
		protected void onPreExecute() {
			progress = new ProgressDialog(context, 0);
			progress.setMessage(getResources().getString(R.string.progress_indicator));
			progress.show();
		}

		@Override
		protected List<Long> doInBackground(Void... arg0) {
			try {
				// TODO FIXME
				// Save as JSON in stead of rows?
				// Easier to serialize - deserialize
				/* {
					    "name": "namnet",
					    "type": "capsule",
					    "dosages": [
					        {
					            "dosage": "10",
					            "unit": "mg"
					        }
					    ],
					    "description": "immunesupressant"
					}
				 */
				List<Long> ids = new ArrayList<Long>();
				for (Medicine medicine : medicines) {
					Medicine createdMedicine = medicinesDatasource.createMedicine(medicine);
					ids.add(createdMedicine.getId());
				}

				// TODO: Add to Schedule:
				for (String time : scheduledTimes) {

				}
				return ids;

			} catch (Exception e) {
				e.printStackTrace();
				this.exception = e;
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<Long> param) {
			if (progress != null) {
				progress.dismiss();
			}

			if (this.exception != null) {
				showErrorDialog(exception);
			} else {
				Intent intent = new Intent();
				setResult(RESULT_OK, intent);
				intent.putExtra(ID_FIELD, param.toString());
				intent.putExtra(NAME_FIELD, name);
				intent.putExtra(TYPE_FIELD, type);
				intent.putExtra(DESCRIPTION_FIELD, description);
				String[] typ = new String[1];
				intent.putExtra(DOSAGES_FIELD, Arrays.toString(getDosages().toArray(typ))); // FIXME
				intent.putExtra(UNIT_FIELD, unit);

				finish();
			}
			super.onPostExecute(param);
		}
	}
}

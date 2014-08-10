package se.tpr.pillerkollen.medicines.add;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import se.tpr.pillerkollen.R;
import se.tpr.pillerkollen.medicines.Medicine;
import se.tpr.pillerkollen.medicines.MedicinesDataSource;
import se.tpr.pillerkollen.schedule.SchedulesDataSource;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ViewFlipper;

public class AddRowActivity extends Activity implements OnItemSelectedListener {

	private AddRowActivity context;
	private ViewFlipper viewFlipper;

	private AddDosagesController addDosagesController;
	private AddScheduleController addScheduleController;

	private MedicinesDataSource medicinesDatasource;
	private SchedulesDataSource schedulesDatasource;


	private static final String UNIT_FIELD = "unit_field";
	private static final String DOSAGES_FIELD = "dosages_field";
	private static final String DESCRIPTION_FIELD = "description_field";
	private static final String TYPE_FIELD = "type_field";
	private static final String NAME_FIELD = "name_field";
	private static final String ID_FIELD = "id_field";



	private List<String> scheduledTimes;


	private Spinner freqSpinner;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TODO: Hide and show action bar when scrolling?
		//		getActionBar().setDisplayShowTitleEnabled(true);
		//		getActionBar().setTitle(getString(R.string.add_row));
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		getActionBar().hide();
		setContentView(R.layout.activity_add_row);


		viewFlipper = (ViewFlipper) findViewById(R.id.add_row_medicines_view_flipper);

		context = this;

		medicinesDatasource = new MedicinesDataSource(context);
		medicinesDatasource.open();

		schedulesDatasource = new SchedulesDataSource(context);
		schedulesDatasource.open();


		setButtonListenersPage1();
		setButtonListenersPage2();
		setupDismissKeyboard(viewFlipper);


		freqSpinner = (Spinner) findViewById(R.id.add_row_spinner_freq);
		freqSpinner.setOnItemSelectedListener(this);
		ArrayAdapter<CharSequence> freqAdapter = ArrayAdapter.createFromResource(this, 
				R.array.recurrence_freq, 
				R.layout.recurrencepicker_freq_item);

		freqAdapter.setDropDownViewResource(R.layout.recurrencepicker_freq_item);
		freqSpinner.setAdapter(freqAdapter);

		// Delay drawing of table to avoid layout bug
		Handler handler = new Handler();

		handler.post(new Runnable() {
			@Override
			public void run() {
				addDosagesController = new AddDosagesController(context);
				restoreState(savedInstanceState);
				addDosagesController.reDrawTable();
			}
		});

		addScheduleController = new AddScheduleController(context);
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

			outState.putString(DOSAGES_FIELD, dosageString);
			Log.d(this.getClass().getName(), "Dosages on save: " + dosageString);
			outState.putString(UNIT_FIELD, addDosagesController.dosages.get(0).getUnit());

		}
	}

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
		//		collectValuesFromPage1();

		String unit = addDosagesController.getUnit(); 

		addScheduleController.updateUnit(unit);

		addScheduleController.initScheduleTimesUI();

		//		TextView startDate = (TextView) findViewById(R.id.add_row_medicine_start_date);

		//		if (startDate.getText() == null || startDate.getText().toString().isEmpty()) {
		//			final String startDateStr = DateUtils.formatDateTime(this, scheduleStartTime.toMillis(false), DateUtils.FORMAT_NUMERIC_DATE);
		//			startDate.setText(startDateStr);	
		//		}
		//		
		//		TextView endDate = (TextView) findViewById(R.id.add_row_medicine_end_date);
		//		
		//		if (endDate.getText() == null || endDate.getText().toString().isEmpty()) {
		//			final String endDateStr = DateUtils.formatDateTime(this, scheduleEndTime.toMillis(false), DateUtils.FORMAT_NUMERIC_DATE);
		//			endDate.setText(endDateStr);	
		//		}
	}



	private void setButtonListenersPage1() {
		View nextButton = (View) findViewById(R.id.add_row_page1_next_button_container);
		nextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hideSoftKeyBoard(v);
				if (viewFlipper.getDisplayedChild() == 0) {
					Medicine medicine = collectValuesFromPage1();
					String missingField = checkForMissingFieldsPage1(medicine);
					if (!missingField.isEmpty()) {
						alertMissingField(missingField);
					} else {
						updateSchedulePage();
						viewFlipper.setInAnimation(context, R.anim.in_from_right);
						viewFlipper.setOutAnimation(context, R.anim.out_to_left);
						viewFlipper.showNext();
					}
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


		View addDosageContainer = (View) findViewById(R.id.add_row_page1_dosages_container);
		View addDosageImage = (View) findViewById(R.id.add_row_page1_add_dosage_image);
		View addDosageText = (View) findViewById(R.id.add_row_page1_add_dosage_text);
		OnClickListener addDosageListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				//				hideSoftKeyBoard(v);

				addDosagesController.addDosage();
			}
		};
		//		addDosageContainer.setOnClickListener(addDosageListener);
		addDosageImage.setOnClickListener(addDosageListener);
		addDosageText.setOnClickListener(addDosageListener);

		addDosageContainer.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.ACTION_DOWN || keyCode == KeyEvent.ACTION_UP) {
					return false;
				}

				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					hideSoftKeyBoard(v);

					addDosagesController.addDosage();
					return true;
				}
				return false;
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
				addScheduleController.setStartDateClickListener();
			}
		});

		View changeEndDate = findViewById(R.id.add_row_medicine_end_date);
		changeEndDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				addScheduleController.setEndDateClickListener();


			}
		});
	}
	protected void createMedicine() {

		Medicine medicine = collectValuesFromPage1();

		List<String> schedules = findScheduledTimes();

		this.scheduledTimes = schedules;
		String missingField = checkForMissingFieldsPage1(medicine);

		// TODO: check for incorrect page2 values?


		if (missingField.isEmpty()) {
			// Create a medicine for each dosage (medA, 10mg; medA, 5mg)
			//				List<Medicine> medicines = new ArrayList<Medicine>();
			List<BigDecimal> dosages = new ArrayList<BigDecimal>();
			for (Dosage dosage : addDosagesController.dosages) {
				String dosageValue = dosage.getDosage();
				if (dosageValue == null || dosageValue.trim().isEmpty()) {
					continue;
				}
				dosages.add(new BigDecimal(dosageValue));

			}
			Medicine newMedicine = new Medicine(medicine);

			medicine.setDosages(dosages);
			medicine.setUnit(addDosagesController.getUnit());
			//				medicines.add(newMedicine);
			new AddRowsTask(medicine).execute();
		} else {
			alertMissingField(missingField);
		}

	}

	private void alertMissingField(String missingField) {
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

		CheckBox cb0800 = (CheckBox) findViewById(R.id.add_medicine_page2_checkbox_1);
		CheckBox cb1200 = (CheckBox) findViewById(R.id.add_medicine_page2_checkbox_2);
		CheckBox cb2000 = (CheckBox) findViewById(R.id.add_medicine_page2_checkbox_3);
		CheckBox cb2400 = (CheckBox) findViewById(R.id.add_medicine_page2_checkbox_4);

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

	private String checkForMissingFieldsPage1(Medicine medicine) {
		String missingField = "";

		if (medicine.getName().isEmpty() ) {
			missingField = getString(R.string.medicines_hint_name);

		} else if (medicine.getType().isEmpty()) {
			missingField = getString(R.string.medicines_hint_type);

		} else if (dosagesAreEmpty()) {
			missingField = getString(R.string.medicines_hint_dosage);

		} else if (addDosagesController.getUnit().isEmpty()) {
			missingField = getString(R.string.medicines_hint_unit);

		} else if (medicine.getDescription().isEmpty()) {
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

	public class AddRowsTask extends AsyncTask<Void, Void, Long> {

		private Exception exception;
		private ProgressDialog progress;
		private Medicine medicine;

		public AddRowsTask(Medicine medicine) {
			this.medicine = medicine;
		}

		@Override
		protected void onPreExecute() {
			progress = new ProgressDialog(context, 0);
			progress.setMessage(getResources().getString(R.string.progress_indicator));
			progress.show();
		}

		@Override
		protected Long doInBackground(Void... arg0) {
			try {
				// TODO
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

				Medicine createdMedicine = medicinesDatasource.createMedicine(medicine);

				// TODO: Add to Schedule:
				for (String time : scheduledTimes) {

				}
				return createdMedicine.getId();

			} catch (Exception e) {
				e.printStackTrace();
				this.exception = e;
				return null;
			}
		}

		@Override
		protected void onPostExecute(Long id) {
			if (progress != null) {
				progress.dismiss();
			}

			if (this.exception != null) {
				showErrorDialog(exception);
			} else {
				//				Medicine medicine = medicines.get(0);

				//				long[] ids = new long[param.size()];
				//				for (int i=0; i<param.size(); i++) {
				//					ids[i] = param.get(i);
				//				}

				Intent intent = new Intent();
				setResult(RESULT_OK, intent);
				intent.putExtra(ID_FIELD, id);
				//				intent.putExtra(NAME_FIELD, medicine.getName());
				//				intent.putExtra(TYPE_FIELD, medicine.getType());
				//				intent.putExtra(DESCRIPTION_FIELD, medicine.getDescription());
				//				String[] typ = new String[1];
				//				intent.putExtra(DOSAGES_FIELD, Arrays.toString(getDosages().toArray(typ))); // FIXME
				//				intent.putExtra(UNIT_FIELD, addDosagesController.getUnit());

				finish();
			}
			super.onPostExecute(id);
		}
	}

	public String createDescription(String value) throws NumberFormatException {

		BigDecimal totalDosage = new BigDecimal(value);
		Set<BigDecimal> parts = new TreeSet<BigDecimal>(java.util.Collections.reverseOrder());
		Map<BigDecimal, Integer> numberOfParts = new LinkedHashMap<BigDecimal, Integer>();
		for (Dosage dosage : addDosagesController.dosages) {

			String dosageValue = dosage.getDosage();
			if (dosageValue == null || dosageValue.isEmpty()) continue;

			parts.add(new BigDecimal(dosageValue));
		}

		calculateNumberOfParts(totalDosage, new LinkedList<BigDecimal>(parts), numberOfParts);

		String unit = addDosagesController.getUnit();

		StringBuilder sb = new StringBuilder();

		for (BigDecimal part : numberOfParts.keySet()) {
			Integer number = numberOfParts.get(part);
			if (number == 0) continue;

			// %s x %s%s -> 1 x 10mg
			String row = getResources().getString(R.string.medicines_schedule_units_description_template, number.toString(), part.toString(), unit);
			if (!parts.contains(part)) {
				row += "*";
			}
			if (sb.length() > 0) {
				sb.append("\n");
			}
			sb.append(row);
		}
		return sb.toString();
	}

	/**
	 * Calculates how to divide the total dosage into the given pills dosages.
	 * The result is stored in numberOfParts.
	 * 
	 * With the parts 10mg, 5mg, 1mg, and the totalDosage of 27mg, 
	 * the result is to take the pills 2x10mg, 1x5mg, 2x1mg
	 *  
	 * @param totalDosage The total dosage of the medicine to take
	 * @param parts The different dosages the medicine is available in
	 * @param numberOfParts The resulting map containing how many pills of a certain dosage to take. [(10mg, 2), (5mg, 1), (1mg, 2)]; 
	 */
	private void calculateNumberOfParts(BigDecimal totalDosage, List<BigDecimal> parts, Map<BigDecimal, Integer> numberOfParts) {
		if (parts.isEmpty() || totalDosage.signum() <= 0) {
			return;
		}

		BigDecimal part = parts.get(0);

		while (totalDosage.subtract(part).signum() >= 0) {
			Integer number = numberOfParts.get(part) == null ? 0 : numberOfParts.get(part);
			numberOfParts.put(part, number+1);
			totalDosage = totalDosage.subtract(part);
		}

		List<BigDecimal> subList = parts.subList(1, parts.size());

		// If 0.3mg is left and the lowest dosage is 0.5, 0.3 will be returned.
		if (hasNonDividableRest(totalDosage, subList)) {
			BigDecimal formatNumber = totalDosage.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros();
			numberOfParts.put(formatNumber, 1);
		}
		calculateNumberOfParts(totalDosage, subList, numberOfParts);
	}


	private boolean hasNonDividableRest(BigDecimal totalDosage, List<BigDecimal> subList) {
		return subList.isEmpty() && totalDosage.signum() == 1;
	}
}

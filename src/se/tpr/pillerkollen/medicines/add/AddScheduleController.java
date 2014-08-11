package se.tpr.pillerkollen.medicines.add;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import se.tpr.pillerkollen.R;
import se.tpr.pillerkollen.schedule.Schedule;
import se.tpr.pillerkollen.schedule.ScheduleTime;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class AddScheduleController {

	private AddRowActivity context;
	
	private Time scheduleEndTime = null;
	private Time scheduleStartTime = null;
	private Time scheduleTime = new Time(); 
	
	
	public AddScheduleController(AddRowActivity context) {
		this.context = context;
		scheduleTime.setToNow();
		setupScheduleTimes();
	}

	public void updateUnit(String unit) {
		TextView unit1 = (TextView) context.findViewById(R.id.add_row_medicine_unit1);
		TextView unit2 = (TextView) context.findViewById(R.id.add_row_medicine_unit2);
		TextView unit3 = (TextView) context.findViewById(R.id.add_row_medicine_unit3);
		TextView unit4 = (TextView) context.findViewById(R.id.add_row_medicine_unit4);

		unit1.setText(unit);
		unit2.setText(unit);
		unit3.setText(unit);
		unit4.setText(unit);
	}
	private void hideSoftKeyBoard(View view) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//		View rootView = context.findViewById(R.id.add_row_medicines_view_flipper);
//		rootView.requestFocus();
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
	public void initScheduleTimesUI() {
		TextView startDate = (TextView) context.findViewById(R.id.add_row_medicine_start_date);
		
		if (startDate.getText() == null || startDate.getText().toString().isEmpty()) {
			final String startDateStr = DateUtils.formatDateTime(context, scheduleStartTime.toMillis(false), DateUtils.FORMAT_NUMERIC_DATE);
			startDate.setText(startDateStr);	
		}
		
		TextView endDate = (TextView) context.findViewById(R.id.add_row_medicine_end_date);
		
		if (endDate.getText() == null || endDate.getText().toString().isEmpty()) {
			final String endDateStr = DateUtils.formatDateTime(context, scheduleEndTime.toMillis(false), DateUtils.FORMAT_NUMERIC_DATE);
			endDate.setText(endDateStr);	
		}
		
		
		
		EditText quantity1 = (EditText) context.findViewById(R.id.add_row_medicine_quantity1);
		EditText quantity2 = (EditText) context.findViewById(R.id.add_row_medicine_quantity2);
		EditText quantity3 = (EditText) context.findViewById(R.id.add_row_medicine_quantity3);
		EditText quantity4 = (EditText) context.findViewById(R.id.add_row_medicine_quantity4);
		
		TextView description1 = (TextView) context.findViewById(R.id.add_row_medicine_unit1_description);
		TextView description2 = (TextView) context.findViewById(R.id.add_row_medicine_unit2_description);
		TextView description3 = (TextView) context.findViewById(R.id.add_row_medicine_unit3_description);
		TextView description4 = (TextView) context.findViewById(R.id.add_row_medicine_unit4_description);
		
		initScheduleRow(quantity1, description1);
		initScheduleRow(quantity2, description2);
		initScheduleRow(quantity3, description3);
		initScheduleRow(quantity4, description4);
		
		CheckBox checkbox1 = (CheckBox) context.findViewById(R.id.add_medicine_page2_checkbox_1);
		CheckBox checkbox2 = (CheckBox) context.findViewById(R.id.add_medicine_page2_checkbox_2);
		CheckBox checkbox3 = (CheckBox) context.findViewById(R.id.add_medicine_page2_checkbox_3);
		CheckBox checkbox4 = (CheckBox) context.findViewById(R.id.add_medicine_page2_checkbox_4);
		
		TextView unit1 = (TextView) context.findViewById(R.id.add_row_medicine_unit1);
		TextView unit2 = (TextView) context.findViewById(R.id.add_row_medicine_unit2);
		TextView unit3 = (TextView) context.findViewById(R.id.add_row_medicine_unit3);
		TextView unit4 = (TextView) context.findViewById(R.id.add_row_medicine_unit4);
		
		setupCheckbox(quantity1, checkbox1, unit1);
		setupCheckbox(quantity2, checkbox2, unit2);
		setupCheckbox(quantity3, checkbox3, unit3);
		setupCheckbox(quantity4, checkbox4, unit4);
	}

	private void setupCheckbox(final EditText quantity, CheckBox checkbox, final TextView unit) {
		
		quantity.setEnabled(checkbox.isChecked());				
		unit.setEnabled(checkbox.isChecked());

		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				quantity.setEnabled(isChecked);				
				unit.setEnabled(isChecked);
			}
		});
	}

	// TODO: Check checkbox?
	private void initScheduleRow(final EditText quantity, final TextView description) {
		String quantityOriginalValue = quantity.getText().toString();
		if (quantityOriginalValue == null || quantityOriginalValue.isEmpty()) {
			drawMedicineQuantityRow(description, "0");
		} else {
			drawMedicineQuantityRow(description, quantityOriginalValue);
		}
		quantity.setOnFocusChangeListener(new EditQuantityFocusListener(quantityOriginalValue, description));
		
		quantity.setOnEditorActionListener(new OnEditorActionListener() {
		    @Override
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        if (actionId == EditorInfo.IME_ACTION_DONE) {
					String value = ((TextView)v).getText().toString();
					if (value == null || value.isEmpty()) {
						return true;
					}
					
					drawMedicineQuantityRow(description, value);
					hideSoftKeyBoard(v);
					return true;
		        }
		        return false;
		    }
		});
		
//		quantity.setOnKeyListener(new OnKeyListener() {
//			
//			@Override
//			public boolean onKey(View v, int keyCode, KeyEvent event) {
//				if (keyCode == KeyEvent.ACTION_DOWN || keyCode == KeyEvent.ACTION_UP) {
//					return false;
//				}
//				
//				if (keyCode == KeyEvent.KEYCODE_ENTER) {
//					String value = ((EditText)v).getText().toString();
//					if (value == null || value.isEmpty()) {
//						return true;
//					}
//					
//					drawMedicineQuantityRow(description, value);
//					return true;
//				}
//				return false;
//			}
//		});
	}

	class EditQuantityFocusListener implements View.OnFocusChangeListener {

		private String originalValue;
		private TextView descriptionField;

		public EditQuantityFocusListener(String originalValue, TextView descriptionField) {
			this.originalValue = originalValue;
			this.descriptionField = descriptionField;
		}

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (!hasFocus) {
				try {
					String value = ((EditText)v).getText().toString();
					if (value == null || value.equals(originalValue)) {
						return;
					}
					
					drawMedicineQuantityRow(descriptionField, value);
					originalValue = value;
				} catch (Exception e) {
					((EditText)v).setText(originalValue);
				}
			} 
		}

	}

	private void drawMedicineQuantityRow(TextView descriptionField, String value) {
		TableRow parentTableRow = (TableRow) descriptionField.getParent();
		String description = context.createDescription(value);
		if (description.isEmpty()) {
			parentTableRow.setVisibility(View.GONE);
		} else {
			parentTableRow.setVisibility(View.VISIBLE);
		}
		descriptionField.setText(description);
	}
	
	public void setStartDateClickListener() {
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
				TextView startDate = (TextView) context.findViewById(R.id.add_row_medicine_start_date);
				startDate.setText(dateStr);

			}
		}, selectedYear, selectedMonth, selectedDayOfMonth);
		datePickerDialog.show();
	}

	public void setEndDateClickListener() {
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
				TextView startDate = (TextView) context.findViewById(R.id.add_row_medicine_end_date);
				startDate.setText(dateStr);

			}
		}, selectedYear, selectedMonth, selectedDayOfMonth);
		datePickerDialog.show();
	}

	public List<Schedule> collectSchedules() {
		List<Schedule> schedules = new ArrayList<Schedule>();

		CheckBox checkbox1 = (CheckBox) context.findViewById(R.id.add_medicine_page2_checkbox_1);
		CheckBox checkbox2 = (CheckBox) context.findViewById(R.id.add_medicine_page2_checkbox_2);
		CheckBox checkbox3 = (CheckBox) context.findViewById(R.id.add_medicine_page2_checkbox_3);
		CheckBox checkbox4 = (CheckBox) context.findViewById(R.id.add_medicine_page2_checkbox_4);
		
		EditText dosage1 = (EditText) context.findViewById(R.id.add_row_medicine_quantity1);
		EditText dosage2 = (EditText) context.findViewById(R.id.add_row_medicine_quantity2);
		EditText dosage3 = (EditText) context.findViewById(R.id.add_row_medicine_quantity3);
		EditText dosage4 = (EditText) context.findViewById(R.id.add_row_medicine_quantity4);
		
		int morning = ScheduleTime.MORNING.ordinal();
		int noon = ScheduleTime.NOON.ordinal();
		int evening = ScheduleTime.EVENING.ordinal();
		int night = ScheduleTime.NIGHT.ordinal();
		
		addSchedule(schedules, checkbox1, dosage1, morning);
		addSchedule(schedules, checkbox2, dosage2, noon);
		addSchedule(schedules, checkbox3, dosage3, evening);
		addSchedule(schedules, checkbox4, dosage4, night);
		
		return schedules;
	}

	private void addSchedule(List<Schedule> schedules, CheckBox checkboxField, EditText dosageField, int time) {
		String dosage = dosageField.getText().toString();
		if (checkboxField.isChecked() && dosage!=null && !dosage.isEmpty()) {
			Schedule schedule = new Schedule(0, 0, time, new BigDecimal(dosage));
			schedules.add(schedule);
		}
	}
}

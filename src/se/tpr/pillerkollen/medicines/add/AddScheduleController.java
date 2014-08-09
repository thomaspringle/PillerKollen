package se.tpr.pillerkollen.medicines.add;

import se.tpr.pillerkollen.R;
import android.app.DatePickerDialog;
import android.text.Editable;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

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
		TextView description1 = (TextView) context.findViewById(R.id.add_row_medicine_unit1_description);
		
		String quantity1OriginalValue = quantity1.getText().toString();
		quantity1.setOnFocusChangeListener(new EditQuantityFocusListener(quantity1OriginalValue, description1));
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
					
					String description = context.createDescription(value);
					descriptionField.setText(description);
					originalValue = value;
				} catch (Exception e) {
					((EditText)v).setText(originalValue);
				}
			} 
		}

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

}

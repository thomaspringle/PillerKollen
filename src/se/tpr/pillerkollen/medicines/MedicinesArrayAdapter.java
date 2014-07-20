package se.tpr.pillerkollen.medicines;

import java.util.ArrayList;
import java.util.List;

import se.tpr.pillerkollen.R;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

public class MedicinesArrayAdapter extends ArrayAdapter<Medicine> implements View.OnTouchListener, View.OnFocusChangeListener { 

	private Activity context;
	private List<Medicine> medicines;
	
	public MedicinesArrayAdapter(Activity context, List<Medicine> medicines) {
		super(context, R.layout.medicines_row, medicines);
		this.context = context;
		this.medicines = medicines;

}
	
	static class ViewHolder {
		protected long id;
		protected TextView name;
		protected TextView type;
		protected TextView description;
		protected TextView dosage;
		protected TextView unit;
		
	}
	private void hideSoftKeyBoard() {

		InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
		if(imm.isAcceptingText()) {                         
			imm.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), 0);
		} else {
		}
	}
	
	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		if (view instanceof EditText) {
			EditText editText = (EditText) view;
			editText.setFocusable(true);
			editText.setFocusableInTouchMode(true);
		} else {
			/*ViewHolder holder = (ViewHolder) view.getTag();
			for (int i=0; i<7; i++) {
				holder.dayFields.get(i).setFocusable(false);
				holder.dayFields.get(i).setFocusableInTouchMode(false);
			}*/
			hideSoftKeyBoard();
		}
		return false;
	}
	
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if(hasFocus){
			hideSoftKeyBoard();
		} else {
			
		}
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View medicinesRowView = null;
		
		if (convertView == null) {
			LayoutInflater inflator = context.getLayoutInflater();
			medicinesRowView = inflator.inflate(R.layout.medicines_row, null);
			final ViewHolder viewHolder = new ViewHolder();
			
		    viewHolder.name = (TextView) medicinesRowView.findViewById(R.id.medicinesRowName);
		    viewHolder.type = (TextView) medicinesRowView.findViewById(R.id.medicinesRowType);
		    viewHolder.description = (TextView) medicinesRowView.findViewById(R.id.medicinesRowDescription);
		    viewHolder.dosage = (TextView) medicinesRowView.findViewById(R.id.medicinesRowDosage);
		    viewHolder.dosage = (TextView) medicinesRowView.findViewById(R.id.medicinesRowUnit);

		    
		    medicinesRowView.setTag(viewHolder);
		} else {
			medicinesRowView = convertView;
			((ViewHolder) medicinesRowView.getTag()).name.setTag(medicines.get(position));
		}

		Medicine medicine = medicines.get(position);
		ViewHolder holder = (ViewHolder) medicinesRowView.getTag();
		
		holder.id = medicine.getId();
		holder.name.setText(medicine.getName());
		holder.type.setText(medicine.getType());
		holder.description.setText(medicine.getDescription());
		holder.dosage.setText(medicine.getDescription());
		holder.unit.setText(medicine.getUnit());
		
		/*List<TimesheetTableDayBean> days = medicine.getDays();
		List<Boolean> activeDays = weekSelection.activeDays();
		List<String> datesInYYYYMMdd = TimeUtil.datesInYYYYMMdd(weekSelection);
		timesheetRowView.setOnTouchListener(this);
		
		for (int i=0; i<7; i++) {
			
			EditText editText = holder.dayFields.get(i);
			TimesheetTableDayBean dayBean = days.get(i);
			String reportedHours = dayBean.getHours().toString();
			editText.setText(reportedHours);
			*/
			//if (activeDays.get(i) && !signed) {
//			holder.name.setEnabled(true);
		/*	} else {
				editText.setFocusable(false);
				editText.setEnabled(false);
			}*/
		holder.name.setOnTouchListener(this);
		holder.name.setOnFocusChangeListener(new EditTextFocusListener(holder.id, medicine.getName(), context, MedicinesSQLiteHelper.COLUMN_NAME));
//		}
		medicinesRowView.setOnFocusChangeListener(this);
		return medicinesRowView;
	}
	
	class EditTextFocusListener implements View.OnFocusChangeListener {


		private String originalValue;
		private long id;
		private Activity activity;
		private String columnName;

		public EditTextFocusListener(long id, String originalValue, Activity activity, String columnName) {
			this.id = id;
			this.originalValue = originalValue;
			this.activity = activity;
			this.columnName = columnName;
		}

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (!hasFocus) {
				try {
					
					String value = ((EditText)v).getText().toString();
					/*if (Float.parseFloat(hours) > 24) {
						hours = "24.0";
					}*/
					if (value.equals(originalValue)) {
						return;
					}
					
					// The field might have been automatically cleared if it was '0' when focused
					/*boolean reportedHoursIsEmpty = "".equals(hours) && "0".equals(reportedHours); 
					if (reportedHoursIsEmpty) {
						((EditText)v).setText("0");
						return;
					}*/

					/*
					BigDecimal roundedHours = new BigDecimal(hours).setScale(1, BigDecimal.ROUND_HALF_UP); 
					context.reportTimeForDay(date, roundedHours, lineId);
					((EditText)v).setText(roundedHours.toString()); //String.format("%.1f", hours)); */
					
					context.reportTimeForDay(date, roundedHours, lineId);
					
				} catch (Exception e) {
					((EditText)v).setText(originalValue);
				}
				
			} /*else {
				// Clear the '0' so you don't have to erase it manually
				String hours = ((EditText)v).getText().toString();
				if ("0".equals(hours)) {
					((EditText)v).setText("");
				}
			}*/
		}
	}
}

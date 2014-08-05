package se.tpr.pillerkollen.medicines.add;

import java.util.Iterator;
import java.util.List;

import se.tpr.pillerkollen.R;
import se.tpr.pillerkollen.medicines.add.DosagesArrayAdapter.EditTextFocusListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class AddDosagesController {

	private AddRowActivity addRowActivity;
	private List<Dosage> dosages;
	
	public AddDosagesController(AddRowActivity addRowActivity, List<Dosage> dosages) {
		this.addRowActivity = addRowActivity;
		this.dosages = dosages;
	}

	protected void reDrawTable() {
		TableLayout table = (TableLayout) addRowActivity.findViewById(R.id.dosage_table);
		table.removeAllViews();
		
		for (int i = 0; i < dosages.size(); i++) {
			displayRow(i);
		}
		table.requestLayout();
	}
	
	protected void addDosage() {
		final Dosage dosage = new Dosage();
		dosages.add(dosage);
		
		TableLayout table = (TableLayout) addRowActivity.findViewById(R.id.dosage_table);
		
		final TableRow dosagesRowView = (TableRow)LayoutInflater.from(addRowActivity).inflate(R.layout.add_medicines_dosage_row, null);

		EditText dosageField = (EditText) dosagesRowView.findViewById(R.id.add_row_medicine_dosage_input);
		EditText unitField = (EditText) dosagesRowView.findViewById(R.id.add_row_medicine_unit_input);
		TextView unitTextField = (TextView) dosagesRowView.findViewById(R.id.add_row_medicine_unit_text);
		
		if (dosages.size() == 1) {
			unitField.setVisibility(View.VISIBLE);
			unitTextField.setVisibility(View.GONE);
			unitField.setOnFocusChangeListener(new EditUnitFocusListener(dosage.getUnit()));
		} else {
			unitField.setVisibility(View.GONE);
			unitTextField.setVisibility(View.VISIBLE);
			unitTextField.setText(dosages.get(0).getUnit());
		}
		dosageField.setOnFocusChangeListener(new EditDosageFocusListener(dosage));
		ImageView removeButton = (ImageView) dosagesRowView.findViewById(R.id.add_row_medicine_remove_dosage_button);
		
		removeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				removeRow(dosage, dosagesRowView);
				
//				addRowActivity.removeDosage(dosage);
			}
		});
	    table.addView(dosagesRowView);
	    table.requestLayout();
	}
	
	private void removeRow(Dosage dosage, View child) {
		TableLayout table = (TableLayout) addRowActivity.findViewById(R.id.dosage_table);
		table.removeView(child);
		dosages.remove(dosage);
		table.requestLayout();
//		Iterator<Dosage> iter = dosages.iterator();
//		while (iter.hasNext()) {
//			Dosage dosage = iter.next();
//			if(dosage.hasId(dosageToRemove)) {
//				iter.remove();
//				return;
//			}
//		}
	}
	
	private void updateUnits() {
		TableLayout table = (TableLayout) addRowActivity.findViewById(R.id.dosage_table);
		for (int i = 0; i < table.getChildCount(); i++) {
		    View child = table.getChildAt(i);

		    if (child instanceof TableRow) {
		        TableRow dosagesRowView = (TableRow) child;
				TextView unitTextField = (TextView) dosagesRowView.findViewById(R.id.add_row_medicine_unit_text);
				if (i != 0) {
					unitTextField.setText(dosages.get(0).getUnit());
				}				
		    }
		}
		table.requestLayout();
	}
	
	private void displayRow(int position) {
		TableLayout table = (TableLayout) addRowActivity.findViewById(R.id.dosage_table);
		View child = table.getChildAt(position);

		// Row exists in view but not in domain, remove it from view
		if (position > dosages.size() && child != null) {
			table.removeViewAt(position);
			return;
		}
		
		// Row does not exist in view but exists on domain, create the view
	    if (child == null) {
	    	TableRow dosagesRowView = (TableRow)LayoutInflater.from(addRowActivity).inflate(R.layout.add_medicines_dosage_row, null);
	    	table.addView(dosagesRowView);
	    	child = dosagesRowView;
	    }
	    
	    if (child instanceof TableRow) {
	        final TableRow dosagesRowView = (TableRow) child;
	        final Dosage dosage = dosages.get(position);
	        EditText dosageField = (EditText) dosagesRowView.findViewById(R.id.add_row_medicine_dosage_input);
			EditText unitField = (EditText) dosagesRowView.findViewById(R.id.add_row_medicine_unit_input);
			TextView unitTextField = (TextView) dosagesRowView.findViewById(R.id.add_row_medicine_unit_text);
			if (position == 0) {
				unitField.setVisibility(View.VISIBLE);
				unitTextField.setVisibility(View.GONE);
				unitField.setOnFocusChangeListener(new EditUnitFocusListener(dosage.getUnit()));
			} else {
				unitField.setVisibility(View.GONE);
				unitTextField.setVisibility(View.VISIBLE);
				unitTextField.setText(dosages.get(0).getUnit());
			}
			dosageField.setOnFocusChangeListener(new EditDosageFocusListener(dosage));
			
			ImageView removeButton = (ImageView) dosagesRowView.findViewById(R.id.add_row_medicine_remove_dosage_button);
			
			removeButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					removeRow(dosage, dosagesRowView);
//					addRowActivity.removeDosage(dosage);
				}
			});
	    }
		
	}
	class EditUnitFocusListener implements View.OnFocusChangeListener {

		private String originalValue;

		public EditUnitFocusListener(String originalValue) {
			this.originalValue = originalValue;
		}

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (!hasFocus) {
				try {
					String value = ((EditText)v).getText().toString();
					if (value.equals(originalValue)) {
						return;
					}

					dosages.get(0).setUnit(value);
					updateUnits();
				} catch (Exception e) {
					((EditText)v).setText(originalValue);
				}
			} 
		}
	}
	class EditDosageFocusListener implements View.OnFocusChangeListener {

		private String originalValue;
		private Dosage dosage;

		public EditDosageFocusListener(Dosage dosage) {
			this.dosage = dosage;
			this.originalValue = dosage.getDosage();
		}

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (!hasFocus) {
				try {
					String value = ((EditText)v).getText().toString();
					if (value.equals(originalValue)) {
						return;
					}
					
					dosage.setDosage(value);
				} catch (Exception e) {
					((EditText)v).setText(originalValue);
				}
			} 
		}
	}
}

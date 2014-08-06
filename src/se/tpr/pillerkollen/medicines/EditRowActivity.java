package se.tpr.pillerkollen.medicines;

import se.tpr.pillerkollen.R;
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
import android.widget.EditText;

public class EditRowActivity extends Activity {

	private Context context;
	private MedicinesDataSource datasource;

	private String name = "";
	private String type = "";
	private String dosage = "";
	private String unit = "";
	private String description = "";
	
	private Medicine medicine;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_row);
		getActionBar().setDisplayShowTitleEnabled(true);
		getActionBar().setTitle(getString(R.string.edit_row));
		
		long medicineId = getIntent().getLongExtra(MedicinesFragment.MEDICINE_ID_FIELD, -1);
		context = this;
		
		datasource = new MedicinesDataSource(context);
		datasource.open();
		
		medicine = datasource.getMedicine(medicineId);
		
		setButtonListeners();
		populateFields();

	}

	private void populateFields() {
		((EditText) findViewById(R.id.edit_row_medicine_name_input)).setText(medicine.getName());
		((EditText) findViewById(R.id.edit_row_medicine_type_input)).setText(medicine.getType());
		((EditText) findViewById(R.id.edit_row_medicine_description_input)).setText(medicine.getDescription());
		((EditText) findViewById(R.id.edit_row_medicine_dosage_input)).setText("");
		((EditText) findViewById(R.id.edit_row_medicine_unit_input)).setText(medicine.getUnit());
		
	}

	@Override
	public void finish() {
		super.finish();
	}
	  
	private void setButtonListeners() {
		Button saveButton = (Button) findViewById(R.id.edit_row_save_button);
		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveRow();
//				finish();
			}
		});
		Button cancelButton = (Button) findViewById(R.id.edit_row_cancel_button);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				setResult(RESULT_CANCELED, intent);
				finish();
			}
		});
	}

	protected void saveRow() {

		name = nullCheck((EditText) findViewById(R.id.edit_row_medicine_name_input));
		type = nullCheck((EditText) findViewById(R.id.edit_row_medicine_type_input));
		description = nullCheck((EditText) findViewById(R.id.edit_row_medicine_description_input));
		dosage = nullCheck((EditText) findViewById(R.id.edit_row_medicine_dosage_input));
		unit = nullCheck((EditText) findViewById(R.id.edit_row_medicine_unit_input));
		
		String missingField = checkForMissingFields();
		
		if (missingField.isEmpty()) {
			new SaveRowTask().execute();
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			// Add the button
			builder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   dialog.dismiss();
			           }
			       });
			builder.setMessage(R.string.add_row_missing_value + " " + missingField).setTitle(R.string.add_row_missing_title);

			builder.create().show();
		}
		
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
		datasource.open();	
		super.onResume();
	}

	@Override
	public void onPause() {
		datasource.close();
		super.onPause();
	}

	public class SaveRowTask extends AsyncTask<Void, Void, Long> {

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

				Medicine updatedMedicine = new Medicine(medicine.getId(), name, type, description, null, unit);
				datasource.updateMedicine(updatedMedicine);
				return medicine.getId();

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

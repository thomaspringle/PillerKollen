package se.tpr.pillerkollen.medicines;

import java.util.List;

import se.tpr.pillerkollen.R;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

public class MedicinesArrayAdapter extends ArrayAdapter<Medicine> implements View.OnTouchListener, View.OnFocusChangeListener { 

	private MedicinesFragment medicinesFragment;
	private Activity activity;
	private List<Medicine> medicines;

	public MedicinesArrayAdapter(MedicinesFragment context, List<Medicine> medicines) {
		super(context.getActivity(), R.layout.medicines_row, medicines);
		this.medicinesFragment = context;
		this.medicines = medicines;
		this.activity = context.getActivity();
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

		Log.i(this.getClass().getName(), "Hiding software keyboard");
		/*InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		if(imm.isAcceptingText()) {                         
			imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
		} else {
		}*/
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
			LayoutInflater inflator = activity.getLayoutInflater();
			medicinesRowView = inflator.inflate(R.layout.medicines_row, null);
			final ViewHolder viewHolder = new ViewHolder();

			viewHolder.name = (TextView) medicinesRowView.findViewById(R.id.medicinesRowName);
			viewHolder.type = (TextView) medicinesRowView.findViewById(R.id.medicinesRowType);
			viewHolder.description = (TextView) medicinesRowView.findViewById(R.id.medicinesRowDescription);
			viewHolder.dosage = (TextView) medicinesRowView.findViewById(R.id.medicinesRowDosage);
			viewHolder.unit = (TextView) medicinesRowView.findViewById(R.id.medicinesRowUnit);


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
		// TODO: disaply dosages better
		holder.dosage.setText(medicine.getDosagesString());
		holder.unit.setText(medicine.getUnit());

		return medicinesRowView;
	}



}

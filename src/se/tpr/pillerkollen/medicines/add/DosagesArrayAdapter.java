package se.tpr.pillerkollen.medicines.add;

import java.util.Iterator;
import java.util.List;

import se.tpr.pillerkollen.R;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class DosagesArrayAdapter extends ArrayAdapter<Dosage> { 

	private AddRowActivity addRowActivity;
	private Activity activity;
	private List<Dosage> dosages;

	public DosagesArrayAdapter(AddRowActivity context, List<Dosage> dosages) {
		super(context, R.layout.add_medicines_dosage_row, dosages);
		this.addRowActivity = context;
		this.dosages = dosages;
		this.activity = context;
	}

	static class ViewHolder {
		protected String id;
		protected EditText dosage;
		protected EditText unit;
		protected TextView unitText;

	}
//	private void hideSoftKeyBoard() {
//
//		Log.i(this.getClass().getName(), "Hiding software keyboard");
//		/*InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
//		if(imm.isAcceptingText()) {                         
//			imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
//		} else {
//		}*/
//	}

//	@Override
//	public boolean onTouch(View view, MotionEvent motionEvent) {
//		if (view instanceof EditText) {
//			EditText editText = (EditText) view;
//			editText.setFocusable(true);
//			editText.setFocusableInTouchMode(true);
//		} else {
//			/*ViewHolder holder = (ViewHolder) view.getTag();
//			for (int i=0; i<7; i++) {
//				holder.dayFields.get(i).setFocusable(false);
//				holder.dayFields.get(i).setFocusableInTouchMode(false);
//			}*/
//			hideSoftKeyBoard();
//		}
//		return false;
//	}
//
//	@Override
//	public void onFocusChange(View v, boolean hasFocus) {
//		if(hasFocus){
//			hideSoftKeyBoard();
//		} else {
//
//		}
//	}

	private void removeRow(String id) {
		Iterator<Dosage> iter = dosages.iterator();
		while (iter.hasNext()) {
			Dosage dosage = iter.next();
			if(dosage.hasId(id)) {
				iter.remove();
				notifyDataSetChanged();
				return;
			}
		}
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View dosagesRowView = null;

		if (convertView == null) {
			LayoutInflater inflator = activity.getLayoutInflater();
			dosagesRowView = inflator.inflate(R.layout.add_medicines_dosage_row, null);
			final ViewHolder viewHolder = new ViewHolder();

			viewHolder.dosage = (EditText) dosagesRowView.findViewById(R.id.add_row_medicine_dosage_input);
			viewHolder.unit = (EditText) dosagesRowView.findViewById(R.id.add_row_medicine_unit_input);
			viewHolder.unitText = (TextView) dosagesRowView.findViewById(R.id.add_row_medicine_unit_text);
			
			dosagesRowView.setTag(viewHolder);
		} else {
			dosagesRowView = convertView;
			ViewHolder viewHolder = (ViewHolder) dosagesRowView.getTag();
			viewHolder.dosage.setTag(dosages.get(position));
		}

		final Dosage dosage = dosages.get(position);
		ViewHolder holder = (ViewHolder) dosagesRowView.getTag();

		holder.id = dosage.getId();
		holder.dosage.setText(dosage.getDosage());
		holder.unit.setText(dosage.getUnit());
		holder.unitText.setText(dosage.getUnit());
		
		if (position == 0) {
			holder.unit.setVisibility(View.VISIBLE);
			holder.unitText.setVisibility(View.GONE);
			holder.unit.setOnFocusChangeListener(new EditTextFocusListener(dosage.getId(), dosage.getUnit()));
		} else {
			holder.unit.setVisibility(View.GONE);
			holder.unitText.setVisibility(View.VISIBLE);
			holder.unitText.setText(dosages.get(0).getUnit());
		}
		
		
		ImageView removeButton = (ImageView) dosagesRowView.findViewById(R.id.add_row_medicine_remove_dosage_button);
		
		removeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				removeRow(dosage.getId());
				addRowActivity.removeDosage(dosage);
			}
		});

		return dosagesRowView;
	}

	class EditTextFocusListener implements View.OnFocusChangeListener {

		private String originalValue;
		private String id;

		public EditTextFocusListener(String id, String originalValue) {
			this.id = id;
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
					notifyDataSetChanged();
				} catch (Exception e) {
					((EditText)v).setText(originalValue);
				}
			} 
		}
	}
}

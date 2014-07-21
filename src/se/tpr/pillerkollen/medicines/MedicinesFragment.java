package se.tpr.pillerkollen.medicines;

import java.util.List;

import se.tpr.pillerkollen.R;
import se.tpr.pillerkollen.R.id;
import se.tpr.pillerkollen.R.layout;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

public class MedicinesFragment extends ListFragment {

	private MedicinesDataSource datasource;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		datasource = new MedicinesDataSource(getActivity());
		datasource.open();

		List<Medicine> values = datasource.getAllMedicines();

		// use the SimpleCursorAdapter to show the
		// elements in a ListView
		// <Medicine>(getActivity(), R.layout.medicines_row, values);
		MedicinesArrayAdapter adapter = new MedicinesArrayAdapter(this, values);
		setListAdapter(adapter);


		View rootView = inflater.inflate(R.layout.fragment_medicines, container, false);

		listenToAddBtn(rootView);
		listentoDelBtn(rootView);

		return rootView;
	}

	private void listentoDelBtn(View rootView) {
		Button delBtn = (Button) rootView.findViewById(R.id.medicinesBtnDel);
		delBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ArrayAdapter<Medicine> adapter = (ArrayAdapter<Medicine>) getListAdapter();
				if (getListAdapter().getCount() > 0) {
					Medicine medicine = (Medicine) getListAdapter().getItem(0);
					datasource.deleteMedicine(medicine);
					adapter.remove(medicine);
				}
			}
		});
	}

	private void listenToAddBtn(View rootView) {
		Button addBtn = (Button) rootView.findViewById(R.id.medicinesBtnAdd);
		addBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ArrayAdapter<Medicine> adapter = (ArrayAdapter<Medicine>) getListAdapter();
				// save the new comment to the database
				String name = "MMF Sandoz";
				String type = "Tablett";
				String description = "Immundämpande";
				String dosage = "250";
				String unit = "mg";
				Medicine medicine = datasource.createMedicine(name, type, description, dosage, unit);
				adapter.add(medicine);
			}
		});
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

	public void updateMedicine(long id, String value, String columnName) {

		datasource.updateMedicine(id, value, columnName);
		
	}

}

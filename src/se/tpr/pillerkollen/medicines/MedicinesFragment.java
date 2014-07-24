package se.tpr.pillerkollen.medicines;

import java.util.List;

import se.tpr.pillerkollen.MainActivity;
import se.tpr.pillerkollen.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MedicinesFragment extends ListFragment {

	private static final int REQUEST_CODE_ADD_ROW = 10;
	private static final int REQUEST_CODE_EDIT_ROW = 20;

	public static final String MEDICINE_ID_FIELD = "medicine_id_field";
	
	private MedicinesDataSource datasource;
	
	MedicinesArrayAdapter adapter = null;
	private View selectedRow = null;
	private int selectedRowNo = -1;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		datasource = new MedicinesDataSource(getActivity());
		datasource.open();

		updateMedicines();

		View rootView = inflater.inflate(R.layout.fragment_medicines, container, false);

//		listenToAddBtn(rootView);
//		listentoDelBtn(rootView);
//		bindEditRowViewButtons();
		
		return rootView;
	}

	private void updateMedicines() {
		List<Medicine> values = datasource.getAllMedicines();

		adapter = new MedicinesArrayAdapter(this, values);
		setListAdapter(adapter);
	}
	
	private void bindEditRowViewButtons() {
		// TODO: Move selected row Up and Down
		// TODO: Copy button
		ImageButton infoButton = (ImageButton) getActivity().findViewById(R.id.medicines_row_info_button);
		ImageButton discardRowButton = (ImageButton) getActivity().findViewById(R.id.medicines_discard_row_button);
		ImageButton editRowButton = (ImageButton) getActivity().findViewById(R.id.medicines_edit_row_button);
		ImageButton addRowButton = (ImageButton) getActivity().findViewById(R.id.medicines_add_row_button);
		
		infoButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (selectedRowNo != -1 && selectedRow != null) {
					String description = ((TextView)selectedRow.findViewById(R.id.medicinesRowDescription)).getText().toString();
					if (null != description && !"".equals(description)) {
						Toast.makeText(getActivity(),	"Description: " + description, Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(getActivity(),	"Description: not set", Toast.LENGTH_LONG).show();
					}
				}
				
			}
		});
		
		addRowButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				addRow();
			}
		});
		
		editRowButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Medicine medicine = (Medicine) getListAdapter().getItem(selectedRowNo);
				editRow(medicine);
			}
		});
		
//		refreshRowButton.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				new FetchWeekTask().execute(weekSelection);
//			}
//		});
		
		discardRowButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Are you sure you want to delete the selected row?
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				// Add the buttons
				builder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				        	   new DiscardRowTask().execute();
				           }
				       });
				builder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				        	   dialog.dismiss();
				           }
				       });
				builder.setMessage(R.string.discard_row_message).setTitle(R.string.discard_row_title);

				builder.create().show();
			}
		});
	}
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
		datasource.open();
    	if (resultCode == MainActivity.RESULT_OK && requestCode == REQUEST_CODE_ADD_ROW) {
    		updateMedicines();
    	} else if (resultCode == MainActivity.RESULT_CANCELED && requestCode == REQUEST_CODE_ADD_ROW) {
    	
    	} else if (resultCode == MainActivity.RESULT_OK && requestCode == REQUEST_CODE_EDIT_ROW) {
    		updateMedicines();
    	} else if (resultCode == MainActivity.RESULT_CANCELED && requestCode == REQUEST_CODE_EDIT_ROW) {
    		
    	}
    }
	
	private void addRow() {
		Intent intent = new Intent(getActivity(), AddRowActivity.class);
		startActivityForResult(intent, REQUEST_CODE_ADD_ROW);
	}
	private void editRow(Medicine medicine) {
		Intent intent = new Intent(getActivity(), EditRowActivity.class);
		intent.putExtra(MEDICINE_ID_FIELD, medicine.getId());
		startActivityForResult(intent, REQUEST_CODE_EDIT_ROW);
	}
	private void hideSoftKeyBoard(View view) {
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	@Override
	public void onListItemClick(ListView l, View view, int position, long id) {
		super.onListItemClick(l, view, position, id);
		
		if (selectedRowNo == position) {
			selectedRow = null;
			view.setSelected(false);
			selectedRowNo = -1;
		} else {
			selectedRow = view;
			view.setSelected(true);
			selectedRowNo = position;
		}
		updateEditRowView();
		hideSoftKeyBoard(view);
	}
	private void updateEditRowView() {
		
		ImageButton infoButton = (ImageButton) getActivity().findViewById(R.id.medicines_row_info_button);
		ImageButton discardRowButton = (ImageButton) getActivity().findViewById(R.id.medicines_discard_row_button);
		ImageButton editRowButton = (ImageButton) getActivity().findViewById(R.id.medicines_edit_row_button);
		if (selectedRowNo == -1) {
			infoButton.setEnabled(false);
			discardRowButton.setEnabled(false);
			editRowButton.setEnabled(false);
		} else {
			infoButton.setEnabled(true);
			discardRowButton.setEnabled(true);
			editRowButton.setEnabled(true);
		}
	}

	@Override
	public void onResume() {
		datasource.open();
		
		updateEditRowView();
		bindEditRowViewButtons();
		
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
	private void showErrorDialog(Exception exception) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		Resources res = getResources();
		
		builder
	      .setTitle(res.getString(R.string.error_title))
	      .setMessage(exception.getMessage())
	      .setPositiveButton(res.getString(R.string.ok_button), null)
	      .show();
	}
	private void discardSelectedRowInDomain(Long lineId) {
		adapter.notifyDataSetChanged();
	}
	
	class DiscardRowTask extends AsyncTask<Void, Void, Long> {

		private Exception exception;
		private ProgressDialog progress;
		
		
		@Override
		protected void onPreExecute() {
            progress = new ProgressDialog(getActivity(), 0);
            progress.setMessage(getResources().getString(R.string.discard_row_progress));
            progress.show();
		}
		
		@Override
		protected Long doInBackground(Void... params) {
			try {
				Medicine medicine = (Medicine) getListAdapter().getItem(selectedRowNo);

				datasource.deleteMedicine(medicine);
				
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
				discardSelectedRowInDomain(lineId);
				
			}
			super.onPostExecute(lineId);
		}
	}
}

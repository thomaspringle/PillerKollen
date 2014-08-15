package se.tpr.pillerkollen.alarm;

import java.util.List;

import se.tpr.pillerkollen.R;
import se.tpr.pillerkollen.medicines.Medicine;
import se.tpr.pillerkollen.schedule.ScheduleViewDto;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AlertMedicinesListAdapter extends ArrayAdapter<ScheduleViewDto> {

	private Activity activity;
	private List<ScheduleViewDto> schedules;

	public AlertMedicinesListAdapter(Activity context, List<ScheduleViewDto> schedules) {
		super(context, R.layout.alert_medicines_row, schedules);
		
		this.activity = context;
		this.schedules = schedules;
	}
	
	static class ViewHolder {
		protected long id;
		protected TextView name;
//		protected TextView type;
		protected TextView dosage;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View alertMedicinesRowView = null;

		if (convertView == null) {
			LayoutInflater inflator = activity.getLayoutInflater();
			alertMedicinesRowView = inflator.inflate(R.layout.alert_medicines_row, null);
			final ViewHolder viewHolder = new ViewHolder();

			viewHolder.name = (TextView) alertMedicinesRowView.findViewById(R.id.medicinesRowName);
//			viewHolder.type = (TextView) alertMedicinesRowView.findViewById(R.id.medicinesRowType);
			viewHolder.dosage = (TextView) alertMedicinesRowView.findViewById(R.id.medicinesRowDosage);


			alertMedicinesRowView.setTag(viewHolder);
		} else {
			alertMedicinesRowView = convertView;
			((ViewHolder) alertMedicinesRowView.getTag()).name.setTag(schedules.get(position));
		}

		ScheduleViewDto scheduleView = schedules.get(position);
		ViewHolder holder = (ViewHolder) alertMedicinesRowView.getTag();
		Medicine medicine = scheduleView.getMedicine();
		
		// TODO: Show the quantity as number of pills/capsules in stead of number
		
		
		//  %s x %s%s => 1 x 10mg
		String dosageString = activity.getResources().getString(R.string.medicines_schedule_units_description_template, 
				scheduleView.getQuantity(), scheduleView.getDosage().toString(), scheduleView.getUnit());
		
		holder.id = medicine.getId();
		holder.name.setText(medicine.getName());
//		holder.type.setText(medicine.getType());

		holder.dosage.setText(dosageString);

		return alertMedicinesRowView;
	}
}

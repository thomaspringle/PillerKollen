package se.tpr.pillerkollen.schedule;

import java.util.Map;

import se.tpr.pillerkollen.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;
import android.widget.Toast;

public class ScheduleViewExpandableListAdapter extends BaseExpandableListAdapter {

	private final Map<ScheduleTime, ScheduleViewTimeGroup> groups;
//	public LayoutInflater inflater;
	private Fragment fragment;

	public ScheduleViewExpandableListAdapter(Fragment frag, Map<ScheduleTime, ScheduleViewTimeGroup> scheduleGroups) {
		fragment = frag;
		this.groups = scheduleGroups;
//		inflater = frag.getActivity().getLayoutInflater();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return groups.get(ScheduleTime.values()[groupPosition]).getChild(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		
		
		// If children are empty, show empty message
		if (groups.get(ScheduleTime.values()[groupPosition]).getNumberOfChildren() == 0) {
			if (convertView == null) {
				LayoutInflater inflater = fragment.getActivity().getLayoutInflater();
				convertView = inflater.inflate(R.layout.schedule_list_details, null);
			}
			TextView nameField = (TextView) convertView.findViewById(R.id.medicine_name_text);
			nameField.setText(fragment.getResources().getString(R.string.medicines_schedule_empty));
			
			TextView dosageField = (TextView) convertView.findViewById(R.id.medicine_dosage_text);
			dosageField.setVisibility(View.GONE);
			return convertView;
		}
				
				
		final ScheduleViewDto child = (ScheduleViewDto) getChild(groupPosition, childPosition);
		
		
		if (convertView == null) {
			LayoutInflater inflater = fragment.getActivity().getLayoutInflater();
			convertView = inflater.inflate(R.layout.schedule_list_details, null);
		}
		TextView nameField = (TextView) convertView.findViewById(R.id.medicine_name_text);
		nameField.setText(child.getMedicineName());
		
		TextView dosageField = (TextView) convertView.findViewById(R.id.medicine_dosage_text);
		dosageField.setVisibility(View.VISIBLE);
		
		//  %s x %s%s => 1 x 10mg
		String dosageString = fragment.getResources().getString(R.string.medicines_schedule_units_description_template, 
										  						child.getQuantity(), child.getDosage().toString(), child.getUnit());
		dosageField.setText(dosageString);
		
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(fragment.getActivity(), child.getMedicineName(), Toast.LENGTH_SHORT).show();
			}
		});
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// If children are empty, show empty message, so always return at least one child
		return Math.max(1, groups.get(ScheduleTime.values()[groupPosition]).getNumberOfChildren());
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groups.get(ScheduleTime.values()[groupPosition]);
	}

	@Override
	public int getGroupCount() {
		return groups.size();
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);
	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		super.onGroupExpanded(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		
		if (convertView == null) {
			LayoutInflater inflater = fragment.getActivity().getLayoutInflater();
			convertView = inflater.inflate(R.layout.schedule_list_group, null);
		}
		
		ScheduleViewTimeGroup group = (ScheduleViewTimeGroup) getGroup(groupPosition);
		((CheckedTextView) convertView).setText(group.getGroupName());
		((CheckedTextView) convertView).setChecked(isExpanded);
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}
} 
package se.tpr.pillerkollen;

import se.tpr.pillerkollen.schedule.Group;
import se.tpr.pillerkollen.schedule.MyExpandableListAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

public class ScheduleFragment extends Fragment {
	// more efficient than HashMap for mapping integers to objects
	SparseArray<Group> groups = new SparseArray<Group>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);

		createData();
		ExpandableListView listView = (ExpandableListView)rootView.findViewById(R.id.scheduleListView);
		MyExpandableListAdapter adapter = new MyExpandableListAdapter(this, groups, savedInstanceState);
		listView.setAdapter(adapter);

		return rootView;
	}

	public void createData() {
		for (int j = 0; j < 5; j++) {
			Group group = new Group("Test " + j);
			for (int i = 0; i < 5; i++) {
				group.children.add("Sub Item" + i);
			}
			groups.append(j, group);
		}
	}
}

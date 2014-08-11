package se.tpr.pillerkollen.schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import se.tpr.pillerkollen.R;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

public class ScheduleFragment extends Fragment {


	private SchedulesDataSource schedulesDatasource;
	
	// more efficient than HashMap for mapping integers to objects
//	SparseArray<Group> groups = new SparseArray<Group>();
	private ScheduleFragment scheduleFragment;
	
	public static boolean dirty = false;
	
	private ExpandableListView listView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		final View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);

		this.scheduleFragment = this;

		schedulesDatasource = new SchedulesDataSource(scheduleFragment.getActivity());
		schedulesDatasource.open();
		
		listView = (ExpandableListView)rootView.findViewById(R.id.scheduleListView);
		
		Handler handler = new Handler();

		handler.post(new Runnable() {
			@Override
			public void run() {
				
				updateUI();
			}
		});
		
		return rootView;
	}

	public static void setDirty() {
		dirty = true;
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
	    super.setUserVisibleHint(isVisibleToUser);
	    if (isVisibleToUser && dirty) { 
	    	updateUI();
	    }
	    else {  }
	}
	
	private void updateUI() {
		List<ScheduleViewDto> schedules = schedulesDatasource.getAllScheduleViews();
		Map<ScheduleTime, ScheduleViewTimeGroup> scheduleGroups = createGroups(schedules);

		ScheduleViewExpandableListAdapter adapter = new ScheduleViewExpandableListAdapter(scheduleFragment, scheduleGroups);
		listView.setAdapter(adapter);
		dirty = false;
	}
	
	private Map<ScheduleTime, ScheduleViewTimeGroup> createGroups(List<ScheduleViewDto> schedules) {
		Map<ScheduleTime, ScheduleViewTimeGroup> result = new TreeMap<ScheduleTime, ScheduleViewTimeGroup>();
		for (ScheduleTime time : ScheduleTime.values()) {
			List<ScheduleViewDto> schedulesForTime = findSchedulesForTime(time, schedules);
			ScheduleViewTimeGroup group = new ScheduleViewTimeGroup(time.name(), schedulesForTime);
			result.put(time, group);
		}
		return result;
	}

	private List<ScheduleViewDto> findSchedulesForTime(ScheduleTime time, List<ScheduleViewDto> schedules) {
		List<ScheduleViewDto> result = new ArrayList<ScheduleViewDto>();
		for (ScheduleViewDto schedule : schedules) {
			if (schedule.hasTime(time)) {
				result.add(schedule);
			}
		}
		return result;
	}

	
	@Override
	public void onResume() {
		schedulesDatasource.open();
		super.onResume();
	}

	@Override
	public void onPause() {
		schedulesDatasource.close();
		super.onPause();
	}
}

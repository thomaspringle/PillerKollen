package se.tpr.pillerkollen.schedule;

import java.util.List;

public class ScheduleViewTimeGroup {

	private String groupName;
	private List<ScheduleViewDto> children;

	public ScheduleViewTimeGroup(String groupName, List<ScheduleViewDto> children) {
		this.groupName = groupName;
		this.children = children;
	}

	public ScheduleViewDto getChild(int childPosition) {
		return children.get(childPosition);
	}

	public int getNumberOfChildren() {
		return children.size();
	}

	public String getGroupName() {
		return groupName;
	}
	
	
}

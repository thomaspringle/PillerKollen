package se.tpr.pillerkollen.schedule;

public class Schedule {

	private long id;
	private long medicine_id;
	private String time;
	private String quantity;
	
	// private String from
	// private String until
	
	public Schedule(long id, long medicine_id, String time, String quantity) {
		this.id = id;
		this.medicine_id = medicine_id;
		this.time = time;
		this.quantity = quantity;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getMedicine_id() {
		return medicine_id;
	}

	public void setMedicine_id(long medicine_id) {
		this.medicine_id = medicine_id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "Schedule [id=" + id + ", medicine_id=" + medicine_id
				+ ", time=" + time + ", quantity=" + quantity + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Schedule other = (Schedule) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
}

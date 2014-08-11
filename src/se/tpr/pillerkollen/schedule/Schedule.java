package se.tpr.pillerkollen.schedule;

import java.math.BigDecimal;

public class Schedule {

	private long id;
	private long medicine_id;
	private int time;
	private BigDecimal dosage;
	
	// private String from
	// private String until
	
	public Schedule(long id, long medicine_id, int time, BigDecimal dosage) {
		this.id = id;
		this.medicine_id = medicine_id;
		this.time = time;
		this.dosage = dosage;
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

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public BigDecimal getDosage() {
		return dosage;
	}

	public void setDosage(BigDecimal dosage) {
		this.dosage = dosage;
	}

	@Override
	public String toString() {
		return "Schedule [id=" + id + ", medicine_id=" + medicine_id
				+ ", time=" + time + ", dosage=" + dosage.toString() + "]";
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

	public Double getDosageDouble() {
		
		return dosage.doubleValue();
		
//		if (dosage.compareTo(new BigDecimal(1)) < 1 && dosage.signum() != 0) {
//			BigDecimal formatNumber = dosage.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros();
//			return formatNumber.doubleValue();
//		} else {
//			return dosage.doubleValue();	
//		}
	}
	
	
}

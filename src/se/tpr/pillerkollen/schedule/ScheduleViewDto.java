package se.tpr.pillerkollen.schedule;

import java.math.BigDecimal;

import se.tpr.pillerkollen.medicines.Medicine;

public class ScheduleViewDto {

	private Schedule schedule;
	private Medicine medicine;
	private Integer quantity;
	private BigDecimal dosage;

	public ScheduleViewDto(Schedule schedule, Medicine medicine, Integer quantity, BigDecimal dosage) {
		this.schedule = schedule;
		this.medicine = medicine;
		this.quantity = quantity;
		this.dosage = dosage;
	}

	public Medicine getMedicine() {
		return medicine;
	}
	
	public Schedule getSchedule() {
		return schedule;
	}

	public boolean hasTime(ScheduleTime time) {
		return this.schedule.getTime() == time.ordinal();
	}

	public CharSequence getMedicineName() {
		return medicine.getName();
	}

	public Integer getQuantity() {
		return quantity;
	}
	
	public BigDecimal getDosage() {
		return dosage;
	}

	public String getUnit() {
		return medicine.getUnit();
	}
}

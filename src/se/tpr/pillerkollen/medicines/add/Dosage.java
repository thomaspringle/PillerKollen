package se.tpr.pillerkollen.medicines.add;

import java.util.UUID;

public class Dosage {

	private String id;

	private String dosage;
	private String unit;
	
	public Dosage() {
		id = UUID.randomUUID().toString();
	}
	
	public Dosage(String id, String dosage, String unit) {
		this.id = id;
		
		if (id == null || id.isEmpty()) {
			this.id = UUID.randomUUID().toString();
		}
		this.dosage = dosage;
		this.unit = unit;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getDosage() {
		return dosage;
	}
	public void setDosage(String dosage) {
		this.dosage = dosage;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}

	@Override
	public String toString() {
		return "Dosage [id=" + id + ", dosage=" + dosage + ", unit=" + unit
				+ "]";
	}

	public boolean hasId(String otherId) {
		return this.id.equals(otherId);
	}
	
}

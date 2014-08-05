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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Dosage other = (Dosage) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
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

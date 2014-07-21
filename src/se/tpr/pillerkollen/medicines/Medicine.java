package se.tpr.pillerkollen.medicines;

public class Medicine {

	private long id;
	private String name;
	private String type;
	private String description;
	private String dosage;
	private String unit;
	
	public Medicine() {
		
	}
	
	public Medicine(long id, String name, String type, String description, String dosage, String unit) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.description = description;
		this.dosage = dosage;
		this.unit = unit;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
		return "Medicine name=" + name + ", type=" + type + ", description=" + description;
	}

	public boolean hasId(long otherId) {
		return this.id == otherId;
	}
	
}

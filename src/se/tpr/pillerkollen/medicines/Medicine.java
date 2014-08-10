package se.tpr.pillerkollen.medicines;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class Medicine {

	public static final String DOSAGE_SEPARATOR = ";";
	private long id;
	private String name;
	private String type;
	private String description;
	private List<BigDecimal> dosages;
	private String unit;
	
	public Medicine() {
		
	}
	
	public Medicine(long id, String name, String type, String description, List<BigDecimal> dosages, String unit) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.description = description;
		this.dosages = dosages;
		this.unit = unit;
	}
	public Medicine(Medicine medicine) {
		this.id = medicine.getId();
		this.name = medicine.getName();
		this.type = medicine.getType();
		this.description = medicine.getDescription();
		this.dosages = medicine.getDosages();
		this.unit = medicine.getUnit();
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
	public List<BigDecimal> getDosages() {
		return dosages;
	}
	public void setDosages(List<BigDecimal> dosages) {
		this.dosages = dosages;
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

	public String getDosagesString() {
		StringBuilder sb = new StringBuilder();
		for (BigDecimal dosage : dosages) {
			if (dosage.compareTo(new BigDecimal(1)) < 1 && dosage.signum() != 0) {
				BigDecimal formatNumber = dosage.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros();
				sb.append(formatNumber.toString());
			} else {
				sb.append(dosage.toString());	
			}
			sb.append(Medicine.DOSAGE_SEPARATOR);
		}
		return sb.toString();
	}

}

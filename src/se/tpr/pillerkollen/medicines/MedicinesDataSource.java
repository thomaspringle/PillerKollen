package se.tpr.pillerkollen.medicines;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import se.tpr.pillerkollen.common.PillerkollenSQLiteHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class MedicinesDataSource {

	private SQLiteDatabase database;
	private PillerkollenSQLiteHelper dbHelper;

	private String[] allColumns = { 
			MedicinesTableProperties.COLUMN_ID,
			MedicinesTableProperties.COLUMN_NAME,
			MedicinesTableProperties.COLUMN_TYPE,
			MedicinesTableProperties.COLUMN_DESCRIPTION, 
			MedicinesTableProperties.COLUMN_DOSAGES,
			MedicinesTableProperties.COLUMN_UNIT	  
	};

	public MedicinesDataSource(Context context) {
		dbHelper = new PillerkollenSQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Medicine createMedicine(Medicine newMedicine) {
		String name = newMedicine.getName();
		String type = newMedicine.getType();
		String description = newMedicine.getDescription();
		String dosages = newMedicine.getDosagesString();
		String unit = newMedicine.getUnit();
		
		return createMedicine(name, type, description, dosages, unit);
	}
	
	public Medicine createMedicine(String name, String type, String description, String dosages, String unit) {
		ContentValues values = new ContentValues();
		values.put(MedicinesTableProperties.COLUMN_NAME, name);
		values.put(MedicinesTableProperties.COLUMN_TYPE, type);
		values.put(MedicinesTableProperties.COLUMN_DESCRIPTION, description);
		values.put(MedicinesTableProperties.COLUMN_DOSAGES, dosages);
		values.put(MedicinesTableProperties.COLUMN_UNIT, unit);

		long insertId = database.insert(MedicinesTableProperties.TABLE_MEDICINES, null, values);
		Cursor cursor = database.query(MedicinesTableProperties.TABLE_MEDICINES,
				allColumns, MedicinesTableProperties.COLUMN_ID + " = " + insertId, 
				null, null, null, null);
		cursor.moveToFirst();
		Medicine newMedicine = cursorToMedicine(cursor);
		cursor.close();
		return newMedicine;
	}

	public void deleteMedicine(Medicine medicine) {
		long id = medicine.getId();
		System.out.println("Medicine deleted with id: " + id);
		database.delete(MedicinesTableProperties.TABLE_MEDICINES, 
				MedicinesTableProperties.COLUMN_ID	+ " = " + id, null);
	}

	public List<Medicine> getAllMedicines() {
		List<Medicine> medicines = new ArrayList<Medicine>();

		Cursor cursor = database.query(MedicinesTableProperties.TABLE_MEDICINES,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Medicine medicine = cursorToMedicine(cursor);
			medicines.add(medicine);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return medicines;
	}

	public Medicine getMedicine(long id) throws NoSuchElementException {
		List<Medicine> medicines = new ArrayList<Medicine>();

		Cursor cursor = database.query(MedicinesTableProperties.TABLE_MEDICINES,
				allColumns, MedicinesTableProperties.COLUMN_ID + " = " + id, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Medicine medicine = cursorToMedicine(cursor);
			medicines.add(medicine);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		if (medicines.isEmpty()) {
			throw new NoSuchElementException("Could not find medicine with id: " + id);
		}
		return medicines.get(0);
		
	}
	public Medicine updateMedicine(Medicine medicine) {
		ContentValues values = new ContentValues();
	    values.put(MedicinesTableProperties.COLUMN_NAME, medicine.getName());
	    values.put(MedicinesTableProperties.COLUMN_TYPE, medicine.getType());
	    values.put(MedicinesTableProperties.COLUMN_DESCRIPTION, medicine.getDescription());
	    values.put(MedicinesTableProperties.COLUMN_DOSAGES, medicine.getDosagesString());
	    values.put(MedicinesTableProperties.COLUMN_UNIT, medicine.getUnit());
		
		database.update(MedicinesTableProperties.TABLE_MEDICINES, values, MedicinesTableProperties.COLUMN_ID + "=" + medicine.getId(), null);
		
		return medicine;
	}
	public Medicine cursorToMedicine(Cursor cursor) {
		return cursorToMedicine(cursor, 0);
	}
	/**
	 * Create a medicine from a row in the Medicines Table
	 * @param cursor Order: id, name, type, description, dosages, unit
	 * @param offset Default is 0, if using query where other columns are present offset might be useful
	 * @return The Medicine representing the db-row
	 */
	public static Medicine cursorToMedicine(Cursor cursor, int offset) {
		
		long id = cursor.getLong(0+offset);

		String name = cursor.getString(1+offset);
		String type = cursor.getString(2+offset);
		String description = cursor.getString(3+offset);
		String dosagesString = cursor.getString(4+offset);
		String unit = cursor.getString(5+offset);

		List<BigDecimal> dosages = asList(dosagesString);
		Medicine medicine = new Medicine(id, name, type, description, dosages, unit);
		return medicine;
	}

	private static List<BigDecimal> asList(String dosagesString) {
		List<BigDecimal> result = new ArrayList<BigDecimal>();
		String[] dosagesArray = dosagesString.split(Medicine.DOSAGE_SEPARATOR);
		for (String dosage : dosagesArray) {
			result.add(new BigDecimal(dosage));
		}
		return result;
	}
//
//	public Medicine updateMedicine(long id, String value, String columnName) {
//		
//		ContentValues values = new ContentValues();
//	    values.put(columnName, value);
//	    
//		database.update(MedicinesSQLiteHelper.TABLE_MEDICINES, values, MedicinesSQLiteHelper.COLUMN_ID + "=" + id, null);
//		
//		return getMedicine(id);
//	}

}

package se.tpr.pillerkollen.medicines;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class MedicinesDataSource {

	private SQLiteDatabase database;
	private MedicinesSQLiteHelper dbHelper;

	private String[] allColumns = { 
			MedicinesSQLiteHelper.COLUMN_ID,
			MedicinesSQLiteHelper.COLUMN_NAME,
			MedicinesSQLiteHelper.COLUMN_TYPE,
			MedicinesSQLiteHelper.COLUMN_DESCRIPTION, 
			MedicinesSQLiteHelper.COLUMN_DOSAGE,
			MedicinesSQLiteHelper.COLUMN_UNIT	  
	};

	public MedicinesDataSource(Context context) {
		dbHelper = new MedicinesSQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Medicine createMedicine(String name, String type, String description, String dosage, String unit) {
		ContentValues values = new ContentValues();
		values.put(MedicinesSQLiteHelper.COLUMN_NAME, name);
		values.put(MedicinesSQLiteHelper.COLUMN_TYPE, type);
		values.put(MedicinesSQLiteHelper.COLUMN_DESCRIPTION, description);
		values.put(MedicinesSQLiteHelper.COLUMN_DOSAGE, dosage);
		values.put(MedicinesSQLiteHelper.COLUMN_UNIT, unit);

		long insertId = database.insert(MedicinesSQLiteHelper.TABLE_MEDICINES, null, values);
		Cursor cursor = database.query(MedicinesSQLiteHelper.TABLE_MEDICINES,
				allColumns, MedicinesSQLiteHelper.COLUMN_ID + " = " + insertId, 
				null, null, null, null);
		cursor.moveToFirst();
		Medicine newMedicine = cursorToMedicine(cursor);
		cursor.close();
		return newMedicine;
	}

	public void deleteMedicine(Medicine medicine) {
		long id = medicine.getId();
		System.out.println("Medicine deleted with id: " + id);
		database.delete(MedicinesSQLiteHelper.TABLE_MEDICINES, 
				MedicinesSQLiteHelper.COLUMN_ID	+ " = " + id, null);
	}

	public List<Medicine> getAllMedicines() {
		List<Medicine> medicines = new ArrayList<Medicine>();

		Cursor cursor = database.query(MedicinesSQLiteHelper.TABLE_MEDICINES,
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

		Cursor cursor = database.query(MedicinesSQLiteHelper.TABLE_MEDICINES,
				allColumns, MedicinesSQLiteHelper.COLUMN_ID + " = " + id, null, null, null, null);

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
	    values.put(MedicinesSQLiteHelper.COLUMN_NAME, medicine.getName());
	    values.put(MedicinesSQLiteHelper.COLUMN_TYPE, medicine.getType());
	    values.put(MedicinesSQLiteHelper.COLUMN_DESCRIPTION, medicine.getDescription());
	    values.put(MedicinesSQLiteHelper.COLUMN_DOSAGE, medicine.getDosage());
	    values.put(MedicinesSQLiteHelper.COLUMN_UNIT, medicine.getUnit());
		
		database.update(MedicinesSQLiteHelper.TABLE_MEDICINES, values, "id=" + medicine.getId(), null);
		
		return medicine;
	}
	private Medicine cursorToMedicine(Cursor cursor) {
		
		long id = cursor.getLong(0);

		String name = cursor.getString(1);
		String type = cursor.getString(2);
		String description = cursor.getString(3);
		String dosage = cursor.getString(4);
		String unit = cursor.getString(5);

		Medicine medicine = new Medicine(id, name, type, description, dosage, unit);
		return medicine;
	}

	public Medicine updateMedicine(long id, String value, String columnName) {
		
		ContentValues values = new ContentValues();
	    values.put(columnName, value);
	    
		database.update(MedicinesSQLiteHelper.TABLE_MEDICINES, values, "id=" + id, null);
		
		return getMedicine(id);
	}
}

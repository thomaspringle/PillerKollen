package se.tpr.pillerkollen.schedule;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

import se.tpr.pillerkollen.common.PillerkollenSQLiteHelper;
import se.tpr.pillerkollen.medicines.Medicine;
import se.tpr.pillerkollen.medicines.MedicinesDataSource;
import se.tpr.pillerkollen.medicines.MedicinesTableProperties;
import se.tpr.pillerkollen.medicines.add.DosageHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SchedulesDataSource {

	private SQLiteDatabase database;
	private PillerkollenSQLiteHelper dbHelper;

	private String[] allColumns = { 
			SchedulesTableProperties.COLUMN_ID,
			SchedulesTableProperties.COLUMN_MEDICINE_ID,
			SchedulesTableProperties.COLUMN_TIME,
			SchedulesTableProperties.COLUMN_DOSAGE 
	};

	public SchedulesDataSource(Context context) {
		dbHelper = new PillerkollenSQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}


	public Schedule createSchedule(Schedule schedule) {
		return createSchedule(schedule.getMedicine_id(), schedule.getTime(), schedule.getDosageDouble());
	}
	
	public Schedule createSchedule(Long medicineId, int time, Double dosage) {
		ContentValues values = new ContentValues();
		values.put(SchedulesTableProperties.COLUMN_MEDICINE_ID, medicineId);
		values.put(SchedulesTableProperties.COLUMN_TIME, time);
		values.put(SchedulesTableProperties.COLUMN_DOSAGE, dosage);


		long insertId = database.insert(SchedulesTableProperties.TABLE_SCHEDULES, null, values);
		Cursor cursor = database.query(SchedulesTableProperties.TABLE_SCHEDULES,
				allColumns, SchedulesTableProperties.COLUMN_ID + " = " + insertId, 
				null, null, null, null);
		cursor.moveToFirst();
		Schedule newSchedule = scheduleFromCursor(cursor);
		cursor.close();
		return newSchedule;
	}

	public void deleteSchedule(Schedule schedule) {
		long id = schedule.getId();
		System.out.println("Schedule deleted with id: " + id);
		database.delete(SchedulesTableProperties.TABLE_SCHEDULES, 
				SchedulesTableProperties.COLUMN_ID	+ " = " + id, null);
	}

	public List<Schedule> getAllSchedules() {
		List<Schedule> schedules = new ArrayList<Schedule>();

		Cursor cursor = database.query(SchedulesTableProperties.TABLE_SCHEDULES,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Schedule schedule = scheduleFromCursor(cursor);
			schedules.add(schedule);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return schedules;
	}

	public Schedule getSchedule(long id) throws NoSuchElementException {
		List<Schedule> schedules = new ArrayList<Schedule>();

		Cursor cursor = database.query(SchedulesTableProperties.TABLE_SCHEDULES,
				allColumns, SchedulesTableProperties.COLUMN_ID + " = " + id, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Schedule schedule = scheduleFromCursor(cursor);
			schedules.add(schedule);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		if (schedules.isEmpty()) {
			throw new NoSuchElementException("Could not find schedule with id: " + id);
		}
		return schedules.get(0);
		
	}
	public Schedule updateSchedule(Schedule schedule) {
		ContentValues values = new ContentValues();
	    values.put(SchedulesTableProperties.COLUMN_MEDICINE_ID, schedule.getMedicine_id());
	    values.put(SchedulesTableProperties.COLUMN_TIME, schedule.getTime());
	    values.put(SchedulesTableProperties.COLUMN_DOSAGE, schedule.getDosageDouble());
		
		String where = SchedulesTableProperties.COLUMN_ID + "=" + schedule.getId();
		database.update(SchedulesTableProperties.TABLE_SCHEDULES, values, where, null);
		
		return schedule;
	}


	public List<ScheduleViewDto> getAllScheduleViews() {
		/*
		List<ScheduleViewDto> result = new ArrayList<ScheduleViewDto>();
		// WHERE b.property_id=?
		final String find_all_schedules_for_medicines_query = 
								String.format(" SELECT sc.%1$s AS sc_id, sc.%2$s AS sc_med_id, sc.%3$s AS sc_time, sc.%4$s AS sc_dosage, " +
													 " md.%5$s AS md_id, md.%6$s AS md_name, md.%7$s AS md_type, md.%8$s AS md_desc, md.%9$s AS md_dosages, md.%10$s AS md_unit " +
											  " FROM %11$s AS md INNER JOIN %12$s AS sc ON md.%5$s=sc.%2$s ",
												SchedulesTableProperties.COLUMN_ID, 		// 1
												SchedulesTableProperties.COLUMN_MEDICINE_ID,
												SchedulesTableProperties.COLUMN_TIME,
												SchedulesTableProperties.COLUMN_DOSAGE, 	// 4
												
												MedicinesTableProperties.COLUMN_ID,			// 5
												MedicinesTableProperties.COLUMN_NAME,
												MedicinesTableProperties.COLUMN_TYPE, 		// 7
												MedicinesTableProperties.COLUMN_DESCRIPTION, 
												MedicinesTableProperties.COLUMN_DOSAGES,
												MedicinesTableProperties.COLUMN_UNIT,		// 10
												
												MedicinesTableProperties.TABLE_MEDICINES,	// 11
												SchedulesTableProperties.TABLE_SCHEDULES); 	// 12

		Log.d(this.getClass().getName(), find_all_schedules_for_medicines_query);
		
		Cursor cursor = database.rawQuery(find_all_schedules_for_medicines_query, null);
		cursor.moveToFirst();
		
		while (!cursor.isAfterLast()) {
			List<ScheduleViewDto> scheduleViews = cursorToScheduleViews(cursor);
			result.addAll(scheduleViews);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return result; */
		return getAllScheduleViewsForTime(null);
	}

	public List<ScheduleViewDto> getAllScheduleViewsForTime(ScheduleTime timeOfDay) {
		
		List<ScheduleViewDto> result = new ArrayList<ScheduleViewDto>();
		// WHERE b.property_id=?
		String find_all_schedules_for_medicines_query = 
								String.format(" SELECT sc.%1$s AS sc_id, sc.%2$s AS sc_med_id, sc.%3$s AS sc_time, sc.%4$s AS sc_dosage, " +
													 " md.%5$s AS md_id, md.%6$s AS md_name, md.%7$s AS md_type, md.%8$s AS md_desc, md.%9$s AS md_dosages, md.%10$s AS md_unit " +
											  " FROM %11$s AS md INNER JOIN %12$s AS sc ON md.%5$s=sc.%2$s ",
												SchedulesTableProperties.COLUMN_ID, 		// 1
												SchedulesTableProperties.COLUMN_MEDICINE_ID,
												SchedulesTableProperties.COLUMN_TIME,
												SchedulesTableProperties.COLUMN_DOSAGE, 	// 4
												
												MedicinesTableProperties.COLUMN_ID,			// 5
												MedicinesTableProperties.COLUMN_NAME,
												MedicinesTableProperties.COLUMN_TYPE, 		// 7
												MedicinesTableProperties.COLUMN_DESCRIPTION, 
												MedicinesTableProperties.COLUMN_DOSAGES,
												MedicinesTableProperties.COLUMN_UNIT,		// 10
												
												MedicinesTableProperties.TABLE_MEDICINES,	// 11
												SchedulesTableProperties.TABLE_SCHEDULES); 	// 12

//		List<String> selectionArguments = new LinkedList<String>(); new String[] {"2"}
		if (timeOfDay != null) {
			find_all_schedules_for_medicines_query += String.format(" WHERE sc.%1$s=%2$d ", SchedulesTableProperties.COLUMN_TIME, timeOfDay.ordinal());
		}
		
		find_all_schedules_for_medicines_query += String.format(" ORDER BY md.%1$s ", MedicinesTableProperties.COLUMN_NAME);
		
		Log.d(this.getClass().getName(), find_all_schedules_for_medicines_query);
		
		Cursor cursor = database.rawQuery(find_all_schedules_for_medicines_query, null);
		cursor.moveToFirst();
		
		while (!cursor.isAfterLast()) {
			List<ScheduleViewDto> scheduleViews = cursorToScheduleViews(cursor);
			result.addAll(scheduleViews);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return result;
	}
	
	private List<ScheduleViewDto> cursorToScheduleViews(Cursor cursor) {
		List<ScheduleViewDto> result = new ArrayList<ScheduleViewDto>();
		
		Schedule schedule = scheduleFromCursor(cursor);
		Medicine medicine = MedicinesDataSource.cursorToMedicine(cursor, 4);
		

		// Split medicines so that each dosage is displayed on it's own row.
		// MedicineA.dosages = 1;5;10, ScheduleA.dosage = 7mg 
		// ==> 
		// MedicineA 5mg x 1, 
		// MedicineA 1mg x 2 
		Map<BigDecimal, Integer> quantityOfDosages = calculateQuantities(schedule, medicine);
		
		for (BigDecimal dosage : quantityOfDosages.keySet()) {
			Integer quantity = quantityOfDosages.get(dosage);
			ScheduleViewDto scheduleViewDto = new ScheduleViewDto(schedule, medicine, quantity, dosage);
			result.add(scheduleViewDto);
		}
		return result;
	}

	private Map<BigDecimal, Integer> calculateQuantities(Schedule schedule, Medicine medicine) {
		BigDecimal totalDosage = schedule.getDosage();
		Set<BigDecimal> parts = new TreeSet<BigDecimal>(java.util.Collections.reverseOrder());
		parts.addAll(medicine.getDosages());
		Map<BigDecimal, Integer> numberOfParts = new LinkedHashMap<BigDecimal, Integer>();

		DosageHelper.calculateNumberOfParts(totalDosage, new LinkedList<BigDecimal>(parts), numberOfParts);
		return numberOfParts;
	}

	private Schedule scheduleFromCursor(Cursor cursor) {
		long id = cursor.getLong(0);

		long medicineId = cursor.getLong(1);
		int time = cursor.getInt(2);
		Double dosage = cursor.getDouble(3);
		
		Schedule schedule = new Schedule(id, medicineId, time, new BigDecimal(dosage));
		return schedule;
	}

}

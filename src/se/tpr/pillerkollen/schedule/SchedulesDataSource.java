package se.tpr.pillerkollen.schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class SchedulesDataSource {

	private SQLiteDatabase database;
	private SchedulesSQLiteHelper dbHelper;

	private String[] allColumns = { 
			SchedulesSQLiteHelper.COLUMN_ID,
			SchedulesSQLiteHelper.COLUMN_MEDICINE_ID,
			SchedulesSQLiteHelper.COLUMN_TIME,
			SchedulesSQLiteHelper.COLUMN_DOSAGE 
	};

	public SchedulesDataSource(Context context) {
		dbHelper = new SchedulesSQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Schedule createSchedule(Long scheduleId, String time, String quantity) {
		ContentValues values = new ContentValues();
		values.put(SchedulesSQLiteHelper.COLUMN_MEDICINE_ID, scheduleId);
		values.put(SchedulesSQLiteHelper.COLUMN_TIME, time);
		values.put(SchedulesSQLiteHelper.COLUMN_DOSAGE, quantity);


		long insertId = database.insert(SchedulesSQLiteHelper.TABLE_SCHEDULES, null, values);
		Cursor cursor = database.query(SchedulesSQLiteHelper.TABLE_SCHEDULES,
				allColumns, SchedulesSQLiteHelper.COLUMN_ID + " = " + insertId, 
				null, null, null, null);
		cursor.moveToFirst();
		Schedule newSchedule = cursorToSchedule(cursor);
		cursor.close();
		return newSchedule;
	}

	public void deleteSchedule(Schedule schedule) {
		long id = schedule.getId();
		System.out.println("Schedule deleted with id: " + id);
		database.delete(SchedulesSQLiteHelper.TABLE_SCHEDULES, 
				SchedulesSQLiteHelper.COLUMN_ID	+ " = " + id, null);
	}

	public List<Schedule> getAllSchedules() {
		List<Schedule> schedules = new ArrayList<Schedule>();

		Cursor cursor = database.query(SchedulesSQLiteHelper.TABLE_SCHEDULES,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Schedule schedule = cursorToSchedule(cursor);
			schedules.add(schedule);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return schedules;
	}

	public Schedule getSchedule(long id) throws NoSuchElementException {
		List<Schedule> schedules = new ArrayList<Schedule>();

		Cursor cursor = database.query(SchedulesSQLiteHelper.TABLE_SCHEDULES,
				allColumns, SchedulesSQLiteHelper.COLUMN_ID + " = " + id, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Schedule schedule = cursorToSchedule(cursor);
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
	    values.put(SchedulesSQLiteHelper.COLUMN_MEDICINE_ID, schedule.getMedicine_id());
	    values.put(SchedulesSQLiteHelper.COLUMN_TIME, schedule.getTime());
	    values.put(SchedulesSQLiteHelper.COLUMN_DOSAGE, schedule.getDosage());
		
		String where = SchedulesSQLiteHelper.COLUMN_ID + "=" + schedule.getId();
		database.update(SchedulesSQLiteHelper.TABLE_SCHEDULES, values, where, null);
		
		return schedule;
	}
	private Schedule cursorToSchedule(Cursor cursor) {
		
		long id = cursor.getLong(0);

		long medicineId = cursor.getLong(1);
		String time = cursor.getString(2);
		String dosage = cursor.getString(3);


		Schedule schedule = new Schedule(id, medicineId, time, dosage);
		return schedule;
	}

	public Schedule updateSchedule(long id, String value, String columnName) {
		
		ContentValues values = new ContentValues();
	    values.put(columnName, value);
	    
		database.update(SchedulesSQLiteHelper.TABLE_SCHEDULES, values, SchedulesSQLiteHelper.COLUMN_ID + "=" + id, null);
		
		return getSchedule(id);
	}
}

package se.tpr.pillerkollen.schedule;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SchedulesSQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_SCHEDULES = "schedules";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_MEDICINE_ID = "medicine_id";
	public static final String COLUMN_TIME = "time";
	public static final String COLUMN_QUANTITY= "quantity";
	
	private static final String DATABASE_NAME = "schedules.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_SCHEDULES + "(" + COLUMN_ID	+ " integer primary key autoincrement, "
			+ COLUMN_MEDICINE_ID + " integer, "
			+ COLUMN_TIME + " text, "
			+ COLUMN_QUANTITY + " text); ";
			//+ "FOREIGN KEY(" + COLUMN_MEDICINE_ID + ") REFERENCES medicines(_id));";

	public SchedulesSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(SchedulesSQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULES);
		onCreate(db);
	}

} 
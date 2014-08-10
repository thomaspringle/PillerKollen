package se.tpr.pillerkollen.medicines;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MedicinesSQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_MEDICINES = "medicines";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_DOSAGES = "dosages";
	public static final String COLUMN_UNIT = "unit";
	
	private static final String DATABASE_NAME = "medicines.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_MEDICINES + "(" + COLUMN_ID
			+ " integer primary key autoincrement, "
			+ COLUMN_NAME + " text not null, "
			+ COLUMN_TYPE + " text, "
			+ COLUMN_DESCRIPTION + " text, "
			+ COLUMN_DOSAGES + " text, "
			+ COLUMN_UNIT + " text);";

	public MedicinesSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MedicinesSQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICINES);
		onCreate(db);
	}

} 
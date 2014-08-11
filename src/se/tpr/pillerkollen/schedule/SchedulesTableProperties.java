package se.tpr.pillerkollen.schedule;


public class SchedulesTableProperties {

	public static final String TABLE_SCHEDULES = "schedules";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_MEDICINE_ID = "medicine_id";
	public static final String COLUMN_TIME = "time";
	public static final String COLUMN_DOSAGE= "dosage";
	
	// Database creation sql statement
	public static final String TABLE_CREATE_SCHEDULES = "CREATE TABLE "
			+ TABLE_SCHEDULES + "(" + COLUMN_ID	+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_MEDICINE_ID + " INTEGER, "
			+ COLUMN_TIME + " INTEGER, "
			+ COLUMN_DOSAGE + " REAL); ";
			//+ "FOREIGN KEY(" + COLUMN_MEDICINE_ID + ") REFERENCES medicines(_id));";

//	public SchedulesTableProperties(Context context) {
//		super(context, DATABASE_NAME, null, DATABASE_VERSION);
//	}
//
//	@Override
//	public void onCreate(SQLiteDatabase database) {
//		database.execSQL(DATABASE_CREATE);
//	}
//
//	@Override
//	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		Log.w(SchedulesTableProperties.class.getName(),
//				"Upgrading database from version " + oldVersion + " to "
//						+ newVersion + ", which will destroy all old data");
//		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULES);
//		onCreate(db);
//	}

} 
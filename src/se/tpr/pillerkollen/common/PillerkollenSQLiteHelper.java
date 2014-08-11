package se.tpr.pillerkollen.common;

import se.tpr.pillerkollen.medicines.MedicinesTableProperties;
import se.tpr.pillerkollen.schedule.SchedulesTableProperties;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PillerkollenSQLiteHelper extends SQLiteOpenHelper {
	
	public static final String DATABASE_NAME = "pillerkollen.db";
	private static final int DATABASE_VERSION = 1;

	
	public PillerkollenSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(MedicinesTableProperties.TABLE_CREATE_MEDICINES);
		database.execSQL(SchedulesTableProperties.TABLE_CREATE_SCHEDULES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(PillerkollenSQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + MedicinesTableProperties.TABLE_CREATE_MEDICINES);
		db.execSQL("DROP TABLE IF EXISTS " + SchedulesTableProperties.TABLE_CREATE_SCHEDULES);
		onCreate(db);
	}

} 
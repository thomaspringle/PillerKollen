package se.tpr.pillerkollen.medicines;


public class MedicinesTableProperties {

	public static final String TABLE_MEDICINES = "medicines";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_DOSAGES = "dosages";
	public static final String COLUMN_UNIT = "unit";
	
	// Database creation sql statement
	public static final String TABLE_CREATE_MEDICINES = "create table "
			+ TABLE_MEDICINES + "(" + COLUMN_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_NAME + " TEXT NOT NULL, "
			+ COLUMN_TYPE + " TEXT, "
			+ COLUMN_DESCRIPTION + " TEXT, "
			+ COLUMN_DOSAGES + " TEXT, "
			+ COLUMN_UNIT + " TEXT);";
	
} 
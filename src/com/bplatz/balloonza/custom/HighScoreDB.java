package com.bplatz.balloonza.custom;

import java.util.ArrayList;

import com.bplatz.balloonza.manager.ResourceManager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class HighScoreDB extends SQLiteOpenHelper
{
	public static final int DATABASE_VERSION = 11;
	
	public static final String DATABASE_NAME = "highscoredatabase";
	public static String SCORETABLE = "scores";
	
	public static String colValueId = "id";
	public static String colRank = "rank";
	public static String colName = "name";
	public static String colValue = "value";
	
	private ArrayList<ScoreInfo> scoreList = new ArrayList<ScoreInfo>();
	Context c;
	
	public HighScoreDB(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		c = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL("CREATE TABLE if not exists " + SCORETABLE + "(" + colValueId + " INTEGER PRIMARY KEY AUTOINCREMENT," + colRank + " INTEGER,"
				+ colName + " TEXT," + colValue + " FLOAT)");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		Log.d("com.b2.balloonza", "Dropping Databases, Upgrade from " + oldVersion + " to " + newVersion);
		db.execSQL("DROP TABLE IF EXISTS " + SCORETABLE);
		onCreate(db);
	}
	
	public int getRank(float value, String table)
	{
		ArrayList<ScoreInfo> infoList = this.getTable(table);
		int i;
		for(i = 0; i < infoList.size(); i++)
		{
			if(value > infoList.get(i).value)
				break;
		}
		
		return i+1;
	}
	
	private void updateRanks(SQLiteDatabase db, int rank, String table)
	{
		db.execSQL("UPDATE " + table + " SET " + colRank + " = " + colRank + " + 1 WHERE " + colRank + " >= " + rank);
		remove(db, table, 11);
	}
	
	public void addValue(ScoreInfo scoreInfo, String table)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		updateRanks(db, scoreInfo.rank, table);
		ContentValues contentValues = new ContentValues();
		contentValues.put(colRank, scoreInfo.rank);
		contentValues.put(colName, scoreInfo.name);
		contentValues.put(colValue, scoreInfo.value);
		db.insert(table, null, contentValues);
		
		db.close();
	}
	
	public void clearTables()
	{
		try
		{
			SQLiteDatabase db = this.getWritableDatabase();
			db.execSQL("DROP TABLE IF EXISTS " + SCORETABLE);
			onCreate(db);
			db.close();
			ResourceManager.getInstance().activity.toastOnUiThread("Local Leaderboard Reset");
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void remove(SQLiteDatabase db, String table, int rank)
	{
		try
		{
			String[] args = { "" + rank };
			db.delete(table, colRank + "=?", args);
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public ArrayList<ScoreInfo> getTable(String table)
	{
		open();
		return scoreList;
	}
	
	private void addToList(String table, ScoreInfo info)
	{
		scoreList.add(info);
	}
	
	
	public void open()
	{
		scoreList.clear();
		SQLiteDatabase db = this.getWritableDatabase();
		openDB(db, SCORETABLE);
		db.close();
	}
	
	
	private void openDB(SQLiteDatabase db, String table)
	{
		
		Cursor cursor = db.rawQuery("select * from " + table + " ORDER BY " + colValue + " DESC", null);
		if (cursor.getCount() != 0)
		{
			if (cursor.moveToFirst())
			{
				do
				{
					ScoreInfo info = new ScoreInfo();
					
					info.rank = cursor.getInt(cursor.getColumnIndex(colRank));
					
					info.name = cursor.getString(cursor.getColumnIndex(colName));
					
					info.value = cursor.getFloat(cursor.getColumnIndex(colValue));
					
					addToList(table, info);
					
				} while (cursor.moveToNext());
			}
		}
		cursor.close();
	}
}
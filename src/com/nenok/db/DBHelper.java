package com.nenok.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class DBHelper {
	private static final String DATABASE_NAME = "nenok.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "account";
    public SQLiteDatabase myDataBase; 
    private Context context;
    private SQLiteDatabase db;
    
    private SQLiteStatement insertStmt;
    private static final String INSERT = "insert into "
    	       + TABLE_NAME + "(email,token,user_id) values (?,?,?)";
    
    private SQLiteStatement insertStmt2;
    private static final String INSERT2 = "insert into "
 	       + "locations" + "(email,longitude,latitude,user_id) values (?,?,?,?)";
    
    public DBHelper(Context context) {
        this.context = context;
        OpenHelper openHelper = new OpenHelper(this.context);
        this.db = openHelper.getWritableDatabase();
        this.insertStmt = this.db.compileStatement(INSERT);
        this.insertStmt2 = this.db.compileStatement(INSERT2);
     }
    
    public long insert(String name) {
        this.insertStmt.bindString(1, name);
        return this.insertStmt.executeInsert();
     }
    
    public long insert2(String name) {
        this.insertStmt2.bindString(1, name);
        return this.insertStmt2.executeInsert();
     }

     public void deleteAll() {
        this.db.delete(TABLE_NAME, null, null);
     }
     
     public Cursor getScores()
     {
         Cursor localCursor =  
           this.myDataBase.rawQuery("SELECT * FROM account WHERE", null);
//            this.myDataBase.query(TBL_SCOREBOARD, new String[] { 
//         		KEY_ID,
//         		KEY_LESSON, 
//         		KEY_LEVEL,
//         		KEY_SCORE,
//         		KEY_DATETIMETAKEN}, 
//         		null, 
//         		null, null, null, null);
         
                                     
         if (localCursor != null)
           localCursor.moveToFirst();
         return localCursor;
     }
     
     public String[] getEmail(){
     	
 		Cursor localCursor =  
 				this.myDataBase.rawQuery("SELECT * FROM account", null);

	
 		String[] array = new String[localCursor.getCount()];
     	int i = 0;
     	Log.v("cursor ", localCursor.toString());
     	// Loop over rows
     	while (localCursor.moveToNext()) {
     	    	String uname = 
     	    			localCursor.getString(localCursor.getColumnIndex("email"));
	        	    array[i] = uname;
	        	    i++;
     	 }
			return array;
			
     }
     
//     public String getEmail() {
//         List<String> list = new ArrayList<String>();
//         Cursor cursor = this.db.query(TABLE_NAME, new String[] { "email" },
//           null, null, null, null, "user_id desc");
//         if (cursor.moveToFirst()) {
//            do {
//               list.add(cursor.getString(0));
//            } while (cursor.moveToNext());
//         }
//         if (cursor != null && !cursor.isClosed()) {
//            cursor.close();
//         }
//         return list;
//      }

     public List<String> selectAll() {
        List<String> list = new ArrayList<String>();
        Cursor cursor = this.db.query(TABLE_NAME, new String[] { "email" },
          null, null, null, null, "user_id desc");
        if (cursor.moveToFirst()) {
           do {
              list.add(cursor.getString(0));
           } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
           cursor.close();
        }
        return list;
     }
     
     public List<String> selectAllLocations() {
         List<String> list = new ArrayList<String>();
         Cursor cursor = this.db.query("locations", new String[] { "email" },
           null, null, null, null, "user_id desc");
         if (cursor.moveToFirst()) {
            do {
               list.add(cursor.getString(0));
            } while (cursor.moveToNext());
         }
         if (cursor != null && !cursor.isClosed()) {
            cursor.close();
         }
         return list;
      }

     private static class OpenHelper extends SQLiteOpenHelper {

        OpenHelper(Context context) {
           super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
           db.execSQL("CREATE TABLE " + TABLE_NAME + "(id INTEGER PRIMARY KEY, email TEXT, token TEXT, user_id TEXT)");
           db.execSQL("CREATE TABLE " + "locations" + "(id INTEGER PRIMARY KEY, email TEXT, latitude TEXT,longitude TEXT, user_id TEXT)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
           Log.w("Example", "Upgrading database, this will drop tables and recreate.");
           db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
           onCreate(db);
        }
     }
     
     
}

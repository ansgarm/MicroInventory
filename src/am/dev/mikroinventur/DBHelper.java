/*******************************************************************************
 * Copyright 2014 Ansgar Mertens
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package am.dev.mikroinventur;

import am.dev.mikroinventur.datamodel.InventoryItem;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper {

	private DatabaseHelper mDBHelper;
	private SQLiteDatabase mDB;

	public static final String DB_NAME = "inventory.db";
	public static final String TABLE_ITEMS = "articles";
	public static final String KEY_ID = "_id";
	public static final String KEY_CODE = "code";
	public static final String KEY_FORMAT = "format";
	public static final String KEY_NAME = "name";
	public static final String KEY_PRICE = "price";
	public static final String KEY_AMOUNT = "amount";
	public static final String KEY_DESCRIPTION = "description";

	private static final int DB_VERSION = 1;
	private final Context mCtx;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_ITEMS
					+ " (_id INTEGER primary key autoincrement, "
					+ " code VARCHAR(20), "
					+ " format VARCHAR(20), "
					+ " name VARCHAR(30), "
					+ " price VARCHAR(10), "
					+ " amount VARCHAR(10), "
					+ " description VARCHAR(200))"
			);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		}

	}

	public DBHelper(Context ctx) {
		this.mCtx = ctx;
	}

	public DBHelper open() throws SQLException {
		mDBHelper = new DatabaseHelper(mCtx);
		mDB = mDBHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDBHelper.close();
		mDBHelper = null;
	}

	/******* Methoden ******/

	// Cursor bekommen (zB für ListViews)
	public Cursor getCursor(String sql) {
		return mDB.rawQuery(sql, null);
	}

	public void deleteDB() {
		mDB.delete(TABLE_ITEMS, null, null);
	}
	
	public long addItem(InventoryItem ii){
		long newId = mDB.insert(TABLE_ITEMS, null, ii.getContentValues());
		ii.setId(newId);
		return newId;
	}
	
	public int updateItem(InventoryItem ii){
		return mDB.update(TABLE_ITEMS, ii.getContentValues(), KEY_ID+"="+ii.getId(), null);
	}
	
	public void deleteItem(long articleId){
		mDB.delete(TABLE_ITEMS, KEY_ID+"="+articleId, null);
	}
	
	public InventoryItem getItem(String sBarcode, String sBarcodeFormat){
		Cursor c = getCursor("SELECT * FROM "+TABLE_ITEMS+" WHERE "+KEY_CODE+
				"='"+sBarcode+"' AND "+KEY_FORMAT+"='"+sBarcodeFormat+"'");
		
		if(!c.moveToFirst()){
			c.close();
			return null;
		}
		
		InventoryItem ii = new InventoryItem(c);
		
		c.close();
		return ii;
	}
	
	public InventoryItem getItem(long id){
		
		Cursor c = getCursor("SELECT * FROM "+TABLE_ITEMS+" WHERE "+KEY_ID+"="+id);
		
		if(!c.moveToFirst()){
			c.close();
			return new InventoryItem();
		}
		
		InventoryItem ii = new InventoryItem(c);
		
		c.close();
		return ii;
	}
	
	public String getProduktCSV(){
		if(mDBHelper == null || mDB==null)
			open();
		
		Cursor c = getCursor("SELECT "
				+KEY_NAME+","
				+KEY_PRICE+","
				+KEY_DESCRIPTION+","
				+KEY_AMOUNT+","
				+KEY_CODE+","
				+KEY_FORMAT
				+" FROM "+TABLE_ITEMS+" ORDER BY "+KEY_NAME);
		
		
		return Tools.getCSVFromCursor(c);
	}
	
}

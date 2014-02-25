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
package am.dev.mikroinventur.data.util;

import java.util.List;

import am.dev.mikroinventur.data.util.DBUtils.DBField.DBKey;
import android.database.sqlite.SQLiteDatabase;

public class DBUtils {
	
	/**
	 * helper method for creating a new table in the given SQLiteDatabase
	 * @param db the database to create the table in
	 * @param tablename the name of the new table
	 * @param fields a list of DBFields which should be created in the database
	 * @param override will use an additionally "IF NOT EXISTS" if value is false
	 */
	public static void createTable(SQLiteDatabase db, String tablename, List<DBField> fields, boolean override){
		
		if(!db.isOpen()){
			throw new RuntimeException("Database isn't open!");
		}
		
		if(db.isReadOnly()){
			throw new RuntimeException("Database is readonly!");
		}
		
		db.execSQL(getCreateStatement(tablename, fields, override));
		
	}
	
	public static String getCreateStatement(String tablename, List<DBField> fields, boolean override){
		
		//no fields?
		if(fields == null || fields.isEmpty()){
			throw new RuntimeException("list of fields is empty!");
		}
		
		StringBuilder create_statement = new StringBuilder("CREATE TABLE ");
		
		//if not exists?
		if(!override)
			create_statement.append("IF NOT EXISTS ");
		
		create_statement.append(tablename).append(" (");
		
		for(DBField dbf : fields){
			create_statement.append(dbf.name)
			.append(" ")
			.append(dbf.type);
			
			if(dbf.key == DBKey.PRIMARY){
				create_statement.append(" PRIMARY KEY");
			}else if(dbf.key == DBKey.UNIQUE){
				create_statement.append(" UNIQUE");
			}
			create_statement.append(dbf.autoIncrement?" AUTOINCREMENT":"")
			.append(", ");
		}
		
		create_statement.delete(create_statement.length()-2, create_statement.length()); //last 2 chars >>", "<<
		create_statement.append(")");
		
		return create_statement.toString();
	}
	
	public static class DBField{
		
		public static enum DBKey{
			NONE,PRIMARY,UNIQUE
		}
		public static enum DBFieldType{
			INTEGER,FLOAT,DATE,DATETIME,BLOB,TEXT
		}
		
		private String name;
		private DBFieldType type;
		private DBKey key = DBKey.NONE;
		private boolean autoIncrement = false;
		
		/**
		 * represents a field of a table
		 * @param name name of the field
		 * @param type type of the field
		 * @param primaryKey if the field is a primary key
		 * @param autoIncrement if the field is autoincrement
		 */
		public DBField(String name, DBFieldType type, DBKey key, boolean autoIncrement) {
			this.name = name;
			this.type = type;
			this.key = key;
			this.autoIncrement = autoIncrement;
		}
		
		/**
		 * represents a field of a table
		 * @param name name of the field
		 * @param type type of the field
		 * @param primaryKey if the field is a primary key
		 */
		public DBField(String name, DBFieldType type, DBKey key) {
			this.name = name;
			this.type = type;
			this.key = key;
		}
		
		public DBField(String name, DBFieldType type) {
			this.name = name;
			this.type = type;
		}
		
	}
	
}

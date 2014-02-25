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

import android.database.Cursor;

public class Tools {
	
	public static String getCSVFromCursor(Cursor c){
		
		//Check if Cursor is null
		if(c == null)
			return "";
		
		final char itemDivider = ';';
		final String rowDivider = "\r\n";  
		
		StringBuilder sb = new StringBuilder();
		
		String[] columnNames = c.getColumnNames();
		
		//"Header" (names of columns) 
		for(String col : columnNames){
			sb.append(col).append(itemDivider);
		}
		sb.deleteCharAt(sb.length()-1).append(rowDivider); //delete last itemDivider and add rowDivider
		
		//Check if Cursor has 0 rows / is empty
		if(c.getCount()<1){
			
			sb.delete(sb.length()-rowDivider.length(), sb.length()); //delete last row divider
			
			return sb.toString();
		}
		
		//else
		//"Data" (data from cursor)
		c.moveToFirst();
		
		do{
			for (int index = 0; index < columnNames.length; index++) {
				sb.append(c.getString(index)).append(itemDivider);
			}
			sb.deleteCharAt(sb.length()-1).append(rowDivider); //delete last itemDivider and add rowDivider
			
		}while(c.moveToNext());
		
		sb.delete(sb.length()-rowDivider.length(), sb.length()); //delete last row divider
		
		return sb.toString();
	}	
	
	//TODO tool for choosing table rows and so on for csv?
	//("export what you want")
	
}

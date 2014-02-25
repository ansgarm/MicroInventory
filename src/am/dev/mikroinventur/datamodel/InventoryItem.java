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
package am.dev.mikroinventur.datamodel;

import am.dev.mikroinventur.DBHelper;
import android.content.ContentValues;
import android.database.Cursor;


public class InventoryItem {
	
	public static final long INVALID = -1;
	
	private String sBarcode;
	private String sBarcodeFormat;
	private String sName;
	private String sPrice;
	private String sAmount;
	private String sDescription;
	private long articleId = INVALID;
	
	public InventoryItem() {
		
	}
	
	public InventoryItem(Cursor c){
		try{
			articleId = c.getLong(c.getColumnIndexOrThrow(DBHelper.KEY_ID));
			sName = c.getString(c.getColumnIndexOrThrow(DBHelper.KEY_NAME));
			sPrice = c.getString(c.getColumnIndexOrThrow(DBHelper.KEY_PRICE));
			sAmount = c.getString(c.getColumnIndexOrThrow(DBHelper.KEY_AMOUNT));
			sDescription = c.getString(c.getColumnIndexOrThrow(DBHelper.KEY_DESCRIPTION));
			sBarcode = c.getString(c.getColumnIndexOrThrow(DBHelper.KEY_CODE));
			sBarcodeFormat = c.getString(c.getColumnIndexOrThrow(DBHelper.KEY_FORMAT));
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public ContentValues getContentValues(){
		ContentValues cv = new ContentValues(6);
		
		cv.put(DBHelper.KEY_CODE, sBarcode);
		cv.put(DBHelper.KEY_FORMAT, sBarcodeFormat);
		cv.put(DBHelper.KEY_NAME, sName);
		cv.put(DBHelper.KEY_PRICE, sPrice);
		cv.put(DBHelper.KEY_AMOUNT, sAmount);
		cv.put(DBHelper.KEY_DESCRIPTION, sDescription);
		
		return cv;
	}
	
	public long getId(){
		return articleId;
	}
	
	public void setId(long id){
		articleId = id;
	}
	
	public boolean hasId(){
		return articleId != INVALID;
	}

	public String getBarcode() {
		return sBarcode;
	}

	public void setBarcode(String sBarcode) {
		this.sBarcode = sBarcode;
	}

	public String getBarcodeFormat() {
		return sBarcodeFormat;
	}

	public void setBarcodeFormat(String sBarcodeFormat) {
		this.sBarcodeFormat = sBarcodeFormat;
	}

	public String getName() {
		return sName;
	}

	public void setName(String sName) {
		this.sName = sName;
	}

	public String getPrice() {
		return sPrice;
	}

	public void setPrice(String sPrice) {
		this.sPrice = sPrice;
	}

	public String getAmount() {
		return sAmount;
	}

	public void setAmount(String sAmount) {
		this.sAmount = sAmount;
	}

	public String getDescription() {
		return sDescription;
	}

	public void setDescription(String sDescription) {
		this.sDescription = sDescription;
	}
	
}

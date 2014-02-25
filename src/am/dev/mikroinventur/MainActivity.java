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

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.ClipboardManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import de.clabsandroid.mikroinventur.R;

public class MainActivity extends Activity 
			implements OnClickListener{
    
	/** Hauptmenu mit Buttons (mit je einem Icon, ähnlich BarcodeList)
	 *	
	 *	-Scannen ("Produkte hinzufügen")
	 *	-Liste ("Liste mit Produkten")
	 *	-Export per Clipboard ("Copy CSV to Clipboard")
	 *	-Export per Mail ("vordefinierte Mail")
	 *	-Einstellungen (deletedb)
	 *	-About ("link zu homepage/mail me bugs etc")
	 *	-Textview about barcode scanner integration "this app uses ..."+link
	 */
	
	DBHelper mDBHelper;
	Button bt_scan, bt_list, bt_export_mail, bt_export_clipboard, bt_settings, bt_about;
	boolean alreadyFoundOutThatBarcodeReaderIsInstalled = false;
	boolean canceledLastDialog = false;
	AlertDialog mBarcodeScannerNotInstalledDialog;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mDBHelper = new DBHelper(this);
        
        bt_scan = (Button) findViewById(R.id.bt_scan);
        bt_list = (Button) findViewById(R.id.bt_list);
        bt_export_mail = (Button) findViewById(R.id.bt_export_mail);
        bt_export_clipboard = (Button) findViewById(R.id.bt_export_clipboard);
        bt_settings = (Button) findViewById(R.id.bt_settings);
        bt_about = (Button) findViewById(R.id.bt_about);
        
        bt_scan.setOnClickListener(this);
        bt_list.setOnClickListener(this);
        bt_export_mail.setOnClickListener(this);
        bt_export_clipboard.setOnClickListener(this);
        bt_settings.setOnClickListener(this);
        bt_about.setOnClickListener(this);
        
        if(isBarcodeReaderInstalled()){
        	bt_scan.setEnabled(true);
        }else{
        	showBarcodeReaderNotInstalledDialog();
        }
        
    }

	@Override
	public void onClick(View v) {
		
		if(v==bt_scan){
			
			Intent intent = new Intent(this, ScanActivity.class);
			intent.putExtra(ScanActivity.EXTRA_NEW_ON_SAVE, getSharedPreferences("am.dev.mikroinventur_preferences", 0).getBoolean("quickscan", true));
			startActivity(intent);
			
		}else if(v==bt_list){
			startActivity(new Intent(this, ListeActivity.class));
		}else if(v==bt_export_mail){
			String csv = mDBHelper.getProduktCSV();
			
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){ //attach as file
				
				try {
					File file = new File(Environment.getExternalStorageDirectory(),System.currentTimeMillis()/1000+".csv");;
					if(!file.exists())
						file.createNewFile();
					
					FileWriter fw = new FileWriter(file);
					fw.write(mDBHelper.getProduktCSV());
					fw.flush();
					fw.close();
					
					final Intent emailIntent2 = new Intent(android.content.Intent.ACTION_SEND);
					emailIntent2.setType("plain/text");
					emailIntent2.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.csv_mail_header));
					emailIntent2.putExtra(android.content.Intent.EXTRA_STREAM, Uri.fromFile(file));
					startActivity(Intent.createChooser(emailIntent2, getString(R.string.send_mail)));
					
					return;
					
				} catch (/*IO*/Exception e) {
					//TODO add translation for error message
					Toast.makeText(MainActivity.this, "Fehler beim Speichern der Datei\nWird als Text angehängt", Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
				
			}else{//attach as text
				Toast.makeText(MainActivity.this, "Kein Zugriff auf den Speicher\nWird als Text angehängt", Toast.LENGTH_LONG).show();
			}
			
			final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.setType("plain/text");
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.csv_mail_header));
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, csv);
			startActivity(Intent.createChooser(emailIntent, getString(R.string.send_mail)));
			
		}else if(v==bt_export_clipboard){
			
			String csv = mDBHelper.getProduktCSV();
			ClipboardManager cbm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			cbm.setText(csv);
			Toast.makeText(this, getString(R.string.clipboard_success), Toast.LENGTH_LONG).show();
			
		}else if(v==bt_settings){
			startActivity(new Intent(this, SettingsActivity.class));
		}else if(v==bt_about){
			startActivity(new Intent(this, AboutActivity.class));
		}
		
	}

	@Override
	protected void onResume() {
		
		if(isBarcodeReaderInstalled()){
        	bt_scan.setEnabled(true);
        }else{
        	if(!canceledLastDialog){
        		showBarcodeReaderNotInstalledDialog();
        	}
        }
		
		super.onResume();
	}
	
	public boolean isBarcodeReaderInstalled(){
		
		if(alreadyFoundOutThatBarcodeReaderIsInstalled)
			return true;
		
		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.setPackage("com.google.zxing.client.android");
        intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
        
		List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent,PackageManager.MATCH_DEFAULT_ONLY);  
	    
		if(list==null)
			return false;
			
		if(list.size() > 0){
			alreadyFoundOutThatBarcodeReaderIsInstalled = true;
			return true;
		}else{
			return false;
		}
		
	}
	
	public void showBarcodeReaderNotInstalledDialog(){
		
		if(mBarcodeScannerNotInstalledDialog == null){ //Dialog erstellen, falls noch nicht vorhanden
			AlertDialog.Builder builder = new Builder(this);
			builder.setTitle(R.string.missing_barcode_scanner_dialog_title);
			builder.setMessage(R.string.missing_barcode_scanner_dialog_message);
			builder.setIcon(R.drawable.zxing_icon);
			builder.setPositiveButton(R.string.missing_barcode_scanner_dialog_market,
					new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse("market://details?id=com.google.zxing.client.android"));
					startActivity(intent);
					
				}
			});
			builder.setNeutralButton(R.string.missing_barcode_scanner_dialog_web,
					new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					Uri uri = Uri.parse("http://code.google.com/p/zxing/downloads/list");
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);
					
				}
			});
			builder.setNegativeButton(R.string.missing_barcode_scanner_dialog_later,
					new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					canceledLastDialog = true;
					dialog.dismiss();
					
				}
			});
			
			mBarcodeScannerNotInstalledDialog = builder.create();	
		}
		
		mBarcodeScannerNotInstalledDialog.show();
		
	}
	
}

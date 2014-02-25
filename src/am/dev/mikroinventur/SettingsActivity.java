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

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import de.clabsandroid.mikroinventur.R;

public class SettingsActivity extends PreferenceActivity {
	
	Preference pref_deletedb;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        
        pref_deletedb = findPreference("deletedb");
        
        pref_deletedb.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				
				AlertDialog.Builder adb = new Builder(SettingsActivity.this);
				adb.setTitle(R.string.deletedb_dialog_title);
				adb.setMessage(R.string.deletedb_dialog_message);
				adb.setIcon(R.drawable.ic_launcher);
				adb.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						DBHelper mDBHelper = new DBHelper(SettingsActivity.this);
						mDBHelper.open();
						mDBHelper.deleteDB();
						mDBHelper.close();
						dialog.dismiss();
					}
				});
				adb.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				adb.create().show();
				
				return false;
			}
		});
         
    }
}

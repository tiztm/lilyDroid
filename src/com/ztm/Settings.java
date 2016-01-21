package com.ztm;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;

public class Settings extends PreferenceActivity {  

   
    public void onCreate(Bundle savedInstanceState) {  
    	super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
	
    
}  
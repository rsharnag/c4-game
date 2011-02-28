package org.example.connectfour;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class prefs extends PreferenceActivity {
	private static final String OPT_LEVEL="level";
	private static final String OPT_LEVEL_DEF="1";
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}
	
	 /** Get the current value of the level option */
	public static String getLevel(Context context)
	{
		return PreferenceManager.getDefaultSharedPreferences(context)
		.getString(OPT_LEVEL, OPT_LEVEL_DEF);
	}
}

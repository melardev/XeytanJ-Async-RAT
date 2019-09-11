package com.melardev.xeytanj.support;

public class SharedPreferences {
	
	private static SharedPreferences prefs;
	
	private SharedPreferences(){
		
	}
	
	public static SharedPreferences getInstance(){
		if(prefs == null)
			prefs = new SharedPreferences();
		
		return prefs;
	}

}

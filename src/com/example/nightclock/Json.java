package com.example.nightclock;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

public class Json {
	
	public static JSONObject getJson(String urlString){
		
		InputStream is = null;
		String result = "";
		JSONObject jsonObject = null;
		
		// HTTP
		try {	    	
			URL url = new URL(urlString);
			   HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			   try {
			     is = new BufferedInputStream(urlConnection.getInputStream());
//			     readStream(in);
			   }finally {
			     urlConnection.disconnect();
			 }
		} catch(Exception e) {
			return null;
		}
	    
		// Read response to string
		try {	    	
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"),8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			result = sb.toString();	            
		} catch(Exception e) {
			return null;
		}

		// Convert string to object
		try {
			jsonObject = new JSONObject(result);            
		} catch(JSONException e) {
			return null;
		}
    
		return jsonObject;
	}
}
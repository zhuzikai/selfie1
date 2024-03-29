package com.selfie.catalog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Base64;
import android.util.Log;

public class HttpManager {

	public static String getData(String uri) {
		
		BufferedReader reader = null;
		Log.d("try", "2");
		try {
			URL url = new URL(uri);
			Log.d("try", "3");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			Log.d("try", "4");
			StringBuilder sb = new StringBuilder();
			reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			
			return sb.toString();
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}
		}
		
	}

	public static String getData(String uri, String userName, String password) {
		
		BufferedReader reader = null;
		HttpURLConnection con = null;
		
		byte[] loginBytes = (userName + ":" + password).getBytes();
		StringBuilder loginBuilder = new StringBuilder()
			.append("Basic ")
			.append(Base64.encodeToString(loginBytes, Base64.DEFAULT));
		
		try {
			URL url = new URL(uri);
			con = (HttpURLConnection) url.openConnection();
			
			con.addRequestProperty("Authorization", loginBuilder.toString());
			
			StringBuilder sb = new StringBuilder();
			reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			
			return sb.toString();
			
		} catch (Exception e) {
			e.printStackTrace();
			try {
				int status = con.getResponseCode();
				Log.d("HttpManager", "HTTP response code: " + status);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return null;
		} 
		finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}
		}
		
	}

	
}

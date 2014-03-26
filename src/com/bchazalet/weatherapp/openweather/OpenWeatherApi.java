package com.bchazalet.weatherapp.openweather;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.bchazalet.weatherapp.openweather.WeatherData.Condition;
import com.bchazalet.weatherapp.openweather.WeatherData.LatLng;
import com.google.common.io.CharStreams;

/**
 * Methods to communicate with the OpenWeather Api
 *
 */
public class OpenWeatherApi {
	
	private static final String TAG = "OpenWeatherApi";
	
	/**
	 * Pre-formatted url for weather information
	 */
	private static final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?q=%s";
	
	/**
	 * Pre-formatted url for weather condition icon
	 */
	private static final String ICON_BASE_URL = "http://openweathermap.org/img/w/%s.png";
	
	/**
	 * Static methods only
	 */
	private OpenWeatherApi(){}

	/**
	 * Fetch the weather information from the openweather api
	 * @param cityName
	 * 		The name of the city you are interested in
	 * @param country
	 * 		The city's country
	 * @return
	 * 		the weather information; null if an error occurred
	 */
	public static WeatherData getWeatherByCity(String cityName, String country){
		String urlString = String.format(WEATHER_URL, cityName + "," + country);
		
		HttpURLConnection urlConnection = null;
		WeatherData data = null;
		try {
			// Getting Json data
			URL url = new URL(urlString.toString());
			urlConnection = (HttpURLConnection) url.openConnection();
			String stringFromStream = CharStreams.toString(new InputStreamReader(new BufferedInputStream(urlConnection.getInputStream()), "UTF-8"));
			JSONObject json = new JSONObject(stringFromStream);
			Log.d(TAG, "Received " + json);
			
			// Parsing json
			JSONObject coord = json.getJSONObject("coord");
			JSONObject main = json.getJSONObject("main");
			JSONObject condition = json.getJSONArray("weather").getJSONObject(0);
			Condition cdt = new Condition(condition.getInt("id"), condition.getString("main"), condition.getString("description"), condition.getString("icon"));
			data = new WeatherData(cityName, new LatLng(coord.getDouble("lat"), coord.getDouble("lon")),
					main.getDouble("temp"), main.getDouble("temp_max"), main.getDouble("temp_min"),
					main.getDouble("pressure"), main.getDouble("humidity"), cdt);
			
		} catch (MalformedURLException e) {
			Log.e(TAG, "error while fetching weather info (this should not happen really!)", e);
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "error while fetching weather info", e);
		} catch (IOException e) {
			Log.e(TAG, "error while fetching weather info", e);
		} catch (JSONException e) {
			Log.e(TAG, "error while fetching weather info", e);
		} finally {
			if(urlConnection != null)
				urlConnection.disconnect();
		}
		
		return data;
	}
	
	/**
	 * Fetches the icon from open weather
	 * @param iconCode
	 * 		the icon code
	 * @return
	 * 		a drawable representing the icon; null if an error occurred
	 */
	public static Bitmap getIcon(String iconCode){
		String urlString = String.format(ICON_BASE_URL, iconCode);
		
		HttpURLConnection urlConnection = null;
		Bitmap img = null;
		try {
			URL url = new URL(urlString.toString());
			img = BitmapFactory.decodeStream(url.openStream());
		} catch (MalformedURLException e) {
			Log.e(TAG, "error while fetching icon (this should not happen really!)", e);
		} catch (IOException e) {
			Log.d(TAG, "error while fetching icon", e);
		} finally {
			if(urlConnection != null)
				urlConnection.disconnect();
		}
		
		return img;
	}
	
}

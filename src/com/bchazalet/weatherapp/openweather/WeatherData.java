package com.bchazalet.weatherapp.openweather;

import java.io.Serializable;

/**
 * Represents weather information for a particular location
 */
public class WeatherData implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Represents coordinates of a location
	 */
	public static class LatLng implements Serializable {
		
		private static final long serialVersionUID = 1L;
		public final double latitude;
		public final double longitude;
		
		public LatLng(double lat, double lng){
			this.latitude = lat;
			this.longitude = lng;
		}
	}
	
	/**
	 * Represents the weather condition (description, icon, etc)
	 */
	public static class Condition implements Serializable {
		
		private static final long serialVersionUID = 1L;

		private final int id;
		
		private final String main;

		private final String description;
		
		private final String icon;
		
		public Condition(int id, String main, String desc, String icon){
			this.id = id;
			this.main = main;
			this.description = desc;
			this.icon = icon;
		}
		
		public int getId() {
			return id;
		}
		
		public String getMain() {
			return main;
		}
		
		public String getDescription() {
			return description;
		}
		
		public String getIcon() {
			return icon;
		}
	}
	
	private final String name;
	
	private final LatLng location;
	
	// Temperatures are in Kelvin
	private final double temp;
	
	private final double tempMax;
	
	private final double tempMin;
	
	/**
	 * Atmospheric pressure in hPa
	 */
	private final double pressure;
	
	/**
	 * Humidity in %
	 */
	private final double humidity;
	
	private final Condition condition;
	
	public WeatherData(String name, LatLng location, double temp, double tempMax, double tempMin, double pressure, 
			double humidity, Condition condition){
		this.name = name;
		this.location = location;
		this.temp = temp;
		this.tempMax = tempMax;
		this.tempMin = tempMin;
		this.pressure = pressure;
		this.humidity = humidity;
		this.condition = condition;
	}

	public String getName() {
		return name;
	}

	public LatLng getLocation() {
		return location;
	}

	public double getTemp() {
		return Helpers.toCelsius(temp);
	}

	public double getTempMax() {
		return Helpers.toCelsius(tempMax);
	}

	public double getTempMin() {
		return Helpers.toCelsius(tempMin);
	}

	public double getPressure() {
		return pressure;
	}

	public double getHumidity() {
		return humidity;
	}
	
	public Condition getCondition(){
		return condition;
	}
	
}

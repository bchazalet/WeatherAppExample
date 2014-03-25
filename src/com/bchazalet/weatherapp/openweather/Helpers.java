package com.bchazalet.weatherapp.openweather;

public class Helpers {

	private static final double ZERO_KELVIN = 273.15;
	
	/**
	 * Converts a temperature in Kelvin to a temperature in Celsius
	 */
	public static double toCelsius(double tempInKelvin){
		return tempInKelvin - ZERO_KELVIN;
	}
}

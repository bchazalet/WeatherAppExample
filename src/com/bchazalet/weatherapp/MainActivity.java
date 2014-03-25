package com.bchazalet.weatherapp;

import java.text.DecimalFormat;

import com.bchazalet.weatherapp.openweather.OpenWeatherApi;
import com.bchazalet.weatherapp.openweather.WeatherData;
import com.example.weatherapp.R;

import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	private static final String COUNTRY_UK = "uk";

	private DecimalFormat NUMERIC_FORMAT = new DecimalFormat("#.0");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Setting up spinner
		final Spinner spinner = (Spinner) findViewById(R.id.cities_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.cities_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				if(pos > 0){ // 0 is "Select a city"
					if(isDataConnected()){
						String city = parent.getItemAtPosition(pos).toString();
						new GetWeatherDataTask().execute(city);
						spinner.setEnabled(false);
					} else {
						showErrorMsg(getString(R.string.error_connectivity_msg));
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// does nothing
			}
		});
		
	}
	
	/**
	 * Can we use a data connection?
	 */
	private boolean isDataConnected(){
		ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(manager.getActiveNetworkInfo() != null && manager.getActiveNetworkInfo().isConnected())
			return true;
		
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Updates the on-screen data
	 * @param data
	 * 		the weather information to show
	 */
	public void updateDisplayedData(WeatherData data){
		TextView name = (TextView) findViewById(R.id.name);
		TextView location = (TextView) findViewById(R.id.location);
		TextView temp = (TextView) findViewById(R.id.temp);
		TextView tempMin = (TextView) findViewById(R.id.temp_min);
		TextView tempMax = (TextView) findViewById(R.id.temp_max);
		TextView humidity = (TextView) findViewById(R.id.humidity);
		TextView pressure = (TextView) findViewById(R.id.pressure);
		TextView condition = (TextView) findViewById(R.id.conditions);
		
		name.setText(data.getName());
		location.setText(data.getLocation().latitude + ", " + data.getLocation().longitude);
		temp.setText(NUMERIC_FORMAT.format(data.getTemp()));
		tempMin.setText(NUMERIC_FORMAT.format(data.getTempMin()));
		tempMax.setText(NUMERIC_FORMAT.format(data.getTempMax()));
		humidity.setText(NUMERIC_FORMAT.format(data.getHumidity()));
		pressure.setText(NUMERIC_FORMAT.format(data.getPressure()));
		condition.setText(data.getCondition().getMain() + ": " + data.getCondition().getDescription());
	}
	
	/**
	 * Display an error message to the user
	 */
	public void showErrorMsg(String msg){
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
	
	/**
	 * AsyncTask to fetch the weather information from the openweather api
	 */
	private class GetWeatherDataTask extends AsyncTask<String, Integer, WeatherData> {

		@Override
		protected WeatherData doInBackground(String... params) {
			if(params.length != 1){
				throw new RuntimeException("Wrong number of arguments");
			}
			
			String city = params[0];
			return OpenWeatherApi.getWeatherByCity(city, COUNTRY_UK);
		}
		
		@Override
		protected void onPostExecute(WeatherData data) {
			if(data != null){
				updateDisplayedData(data);
				new GetIconAsyncTask().execute(data.getCondition().getIcon());
			} else {
				showErrorMsg(getString(R.string.error_request_msg));
			}
			// Whatever happens, make the spinner available again
			Spinner spinner = (Spinner) findViewById(R.id.cities_spinner);
			spinner.setEnabled(true);
		}
	}
	
	/**
	 * AsyncTask to fetch the weather icon from openweather api
	 * <p>Ideally the icon should be cached and re-used instead of fetched every time.
	 */
	private class GetIconAsyncTask extends AsyncTask<String, Integer, Drawable> {

		@Override
		protected Drawable doInBackground(String... params) {
			if(params.length != 1){
				throw new RuntimeException("Wrong number of arguments");
			}
			
			String iconCode = params[0];
			return OpenWeatherApi.getIcon(iconCode);
		}
		
		@Override
		protected void onPostExecute(Drawable img) { 
			ImageView imgView = (ImageView) findViewById(R.id.icon);
			if(img != null){
				imgView.setImageDrawable(img);
				imgView.setVisibility(View.VISIBLE);
			} else {
				// if we could not fetch the icon, still remove the last-showing one
				imgView.setVisibility(View.INVISIBLE);
				showErrorMsg(getString(R.string.error_icon_msg));
			}
		}
		
	}
	
}

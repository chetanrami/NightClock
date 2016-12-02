package com.example.nightclock;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceFragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		showSettingsBtn();

		callAsynchronousTask(TimeUnit.SECONDS.toMillis(300));

		TextView tv2 = (TextView) findViewById(R.id.row2col2);
		tv2.setText("This is textView2");

		final Button settingsBtn = (Button) findViewById(R.id.settings);
		settingsBtnParms(settingsBtn);
	}

	private void showSettingsBtn() {
		final Button settingsBtn = (Button) findViewById(R.id.settings);
		settingsBtn.setVisibility(View.GONE);

		View screenTouchShow = findViewById(R.id.textclock2);
		screenTouchShow.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				settingsBtn.setVisibility(View.VISIBLE);
			}
		});
		View row2Col2View = findViewById(R.id.row2col2);
		settingsBtnGone(settingsBtn, row2Col2View);

		View buttonView = findViewById(R.id.settings);
		settingsBtnGone(settingsBtn, buttonView);
	}

	private void settingsBtnGone(final Button settingsBtn, View screenTouchHide) {
		screenTouchHide.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				settingsBtn.setVisibility(View.GONE);
			}
		});
	}

	private void settingsBtnParms(Button settingsBtn) {

		settingsBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Display the fragment as the main content.
				FragmentManager mFragmentManager = getFragmentManager();
				FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
				PrefsFragment mPrefsFragment = new PrefsFragment();
				mFragmentTransaction.replace(android.R.id.content, mPrefsFragment);
				mFragmentTransaction.commit();

				findViewById(R.id.textclock).setVisibility(View.INVISIBLE);
				findViewById(R.id.textclock2).setVisibility(View.INVISIBLE);
				findViewById(R.id.settings).setVisibility(View.INVISIBLE);
				findViewById(R.id.row2col1).setVisibility(View.INVISIBLE);
				findViewById(R.id.row2col2).setVisibility(View.INVISIBLE);
			}
		});
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

	class GetURLResponse extends AsyncTask<String, Void, Map<String, String>> {

		protected Map<String, String> doInBackground(String... urls) {
			String url = urls[0];
			JSONObject jsonObj = Json.getJson(url);
			Map<String, String> mapping = new HashMap<>();
			String temperature_string1 = "";
			String temperature_string2 = "";
			try {
				temperature_string1 = jsonObj.getJSONObject("current_observation").getString("temp_f");
				temperature_string2 = jsonObj.getJSONObject("current_observation").getString("feelslike_f");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mapping.put(C.CURR_TEMP, temperature_string1);
			mapping.put(C.FEELS_LIKE_TEMP, temperature_string2);
			return mapping;
		}

		protected void onProgressUpdate(Integer... progress) {
			// setProgressPercent(progress[0]);
		}

		protected void onPostExecute(Map<String, String> result) {
			// get selected values from result;
			TextView tv1 = (TextView) MainActivity.this.findViewById(R.id.row2col1);
			TextView tv2 = (TextView) MainActivity.this.findViewById(R.id.row2col2);
			tv1.setText(result.get(C.CURR_TEMP));
			tv2.setText(result.get(C.FEELS_LIKE_TEMP));
		}
	}

	public void callAsynchronousTask(long milliseconds) {
		final Handler handler = new Handler();
		Timer timer = new Timer();
		TimerTask doAsynchronousTask = new TimerTask() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					public void run() {
						try {
							String url = "http://api.wunderground.com/api/13427c4e1fa71718/conditions/q/IL/Hanover_park.json";
							GetURLResponse performBackgroundTask = new GetURLResponse();
							performBackgroundTask.execute(url, null, "");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		};
		timer.schedule(doAsynchronousTask, 0, milliseconds);
	}

	public static class PrefsFragment extends PreferenceFragment {

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.preferences);
		}
	}
}
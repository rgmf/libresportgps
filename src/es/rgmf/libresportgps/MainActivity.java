/**
 * Copyright (C) 2014 Román Ginés Martínez Ferrández <rgmf@riseup.net>
 *
 * This program (LibreSportGPS) is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either
 * version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package es.rgmf.libresportgps;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import es.rgmf.libresportgps.common.SegmentUtil;
import es.rgmf.libresportgps.common.Session;
import es.rgmf.libresportgps.db.DBModel;
import es.rgmf.libresportgps.db.orm.Track;
import es.rgmf.libresportgps.db.orm.TrackPoint;
import es.rgmf.libresportgps.fragment.AbstractViewFragment;
import es.rgmf.libresportgps.fragment.DataViewFragment;
import es.rgmf.libresportgps.fragment.SettingsFragment;
import es.rgmf.libresportgps.fragment.TrackFragment;
import es.rgmf.libresportgps.fragment.TrackListFragment;
import es.rgmf.libresportgps.fragment.dialog.AddSegmentDialog;
import es.rgmf.libresportgps.gps.GpsLoggerService;
import es.rgmf.libresportgps.gps.GpsLoggerServiceConnection;
import es.rgmf.libresportgps.gps.IGpsLoggerServiceClient;



/**
 * This class represent the main activity of the application.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class MainActivity extends FragmentActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks,
		IGpsLoggerServiceClient, TrackListFragment.OnTrackSelectedListener,
		TrackListFragment.ProgressCallbacks,
		AddSegmentDialog.AddSegmentDialogListener {

	private ProgressDialog mProgressDialog = null;

	/**
	 * Needed attributes to creates and manages service.
	 */
	private static Intent serviceIntent;
	private GpsLoggerService gpsService;
	private GpsLoggerServiceConnection gpsServiceConnection = new GpsLoggerServiceConnection(
			this);

	/**
	 * Fragment manager.
	 */
	private FragmentManager mFragmentManager;

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	/**
	 * This method is called when this activity is created.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		/**************** To create a copy of the database ***************
		try {
			File
			dbInFile = getDatabasePath("libresportgps.db");
			File dbOutFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/copy_libresportgps.db");
			InputStream in = new FileInputStream(dbInFile);
			OutputStream out = new FileOutputStream(dbOutFile);
			 
			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) { out.write(buf, 0, len); }
			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		***************** End to create a copy of the database **************/

		super.onCreate(savedInstanceState);

		if (mFragmentManager == null) {
			mFragmentManager = getSupportFragmentManager();
		}

		setContentView(R.layout.activity_main);

		serviceIntent = new Intent(this, GpsLoggerService.class);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));

		// Check if external storage is available.
		checkExternalStorage();

		// Set LibreSportGPS to keepScreenOn.
		View libresportgpsView = findViewById(R.id.drawer_layout);
		libresportgpsView.setKeepScreenOn(true);

		// Check GPS status provider to show a message if GPS is disabled.
		this.checkGpsProvider();
	}

	/**
	 * This method is called each time the activity is executed. For example
	 * when you change from other application to it.
	 */
	@Override
	protected void onResume() {
		super.onResume();

		// Get preferences and populate data in Session.
		populateSessionWithPrefs();

		// Starts GPS Service and bind it to this Activity.
		Session.setBoundToService(true);
		startService(serviceIntent);
		bindService(serviceIntent, gpsServiceConnection,
				Context.BIND_AUTO_CREATE);
	}

	/**
	 * Get user preferences and populates them to Session.
	 */
	private void populateSessionWithPrefs() {
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		long timeBeforeLogging = Integer.valueOf(sharedPrefs.getString(
				SettingsFragment.KEY_PREF_TIME_BEFORE_LOGGING, "0")) * 1000; // To Milliseconds.

		Session.setTimeBeforeLogging(timeBeforeLogging);
	}

	/**
	 * This method is called when you chose an option in Navigation Drawer.
	 */
	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentTransaction transaction = mFragmentManager.beginTransaction(); 
		
		switch (position) {
		case 0: 
			transaction
					.replace(R.id.container, TrackListFragment.newInstance());
			break;
		case 1:
			transaction.replace(R.id.container, DataViewFragment.newInstance());
			break;
		case 2:
			//transaction.replace(R.id.container, SettingsFragment.newInstance());
		default:
			break;
		}
		mFragmentManager.popBackStack();
		transaction.commitAllowingStateLoss();
	}

	/**
	 * When a section is attached.
	 * 
	 * @param number
	 */
	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_start_section);
		case 2:
			mTitle = getString(R.string.title_data_section);
			break;
		case 3:
			mTitle = getString(R.string.action_settings);
		}
	}

	/**
	 * This method is called when the activity will be destroyed.
	 */
	@Override
	public void onDestroy() {
		if (Session.isTrackingStarted()) {
			Track track = new Track();
			track.setId(Session.getTrackId());
			track.setTitle(Session
					.getFileName());
			track.setDescription("");
			track.setDistance((float) Session
					.getDistance());
			track.setStartTime(Session
					.getStartTimeStamp());
			track.setActivityTime(Session
					.getActivityTimeStamp());
			track.setFinishTime(System
					.currentTimeMillis());
			track.setMaxSpeed(Session
					.getMaxSpeed());
			track.setMaxElevation((float) Session
					.getMaxAltitude());
			track.setMinElevation((float) Session
					.getMinAltitude());
			track.setElevationGain((float) Session
					.getAltitudeGain());
			track.setElevationLoss((float) Session
					.getAltitudeLoss());

			DBModel.endRecordingTrack(this,
					Session.getTrackId(), Track.OPEN_TRACK, track);
		}
		
		super.onDestroy();
		stopAndUnbindToService();
		/*
		 * if(mProgressDialog != null) { Log.v("MainActivity::onDestroy",
		 * "mProgressDialog.dismiss"); mProgressDialog.dismiss(); }
		 */
	}

	/**
	 * This method is called when the activity is paused.
	 */
	@Override
	public void onPause() {
		stopAndUnbindToService();
		super.onPause();
	}

	/**
	 * This method stop and unbind to service if required.
	 */
	private void stopAndUnbindToService() {
		if (Session.isBoundToService()) {
			unbindService(gpsServiceConnection);
			Session.setBoundToService(false);
		}

		if (!Session.isTrackingStarted()) {
			stopService(serviceIntent);
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		/*
		 * int id = item.getItemId(); if (id == R.id.action_settings) { return
		 * true; }
		 */
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Check if GPS is enabled. If GPS is not enabled then It shows an Dialog
	 * through the user can enabled it.
	 */
	private void checkGpsProvider() {
		LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
				&& !Session.isAlertDialogGPSShowed()) {
			Session.setAlertDialogGPSShowed(true);
			new AlertDialog.Builder(this)
					.setTitle(R.string.gps_disabled)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setMessage(
							getResources()
									.getString(R.string.gps_disabled_hint))
					.setCancelable(true)
					.setPositiveButton(android.R.string.yes,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									startActivity(new Intent(
											Settings.ACTION_LOCATION_SOURCE_SETTINGS));
								}
							})
					.setNegativeButton(android.R.string.no,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							}).create().show();
		}
	}

	/**
	 * Check if external storage is available. If SD is not available then it
	 * shows a message.
	 */
	private void checkExternalStorage() {
		String state = Environment.getExternalStorageState();
		if (!Environment.MEDIA_MOUNTED.equals(state)
				|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			Toast.makeText(this, R.string.external_storage_not_available,
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Set gpsService.
	 * 
	 * This method is called from GpsLoggerServiceConnection.
	 * 
	 * @param gpsService
	 *            the GpsLoggerService.
	 */
	public void setGpsService(GpsLoggerService gpsService) {
		this.gpsService = gpsService;
	}

	/**
	 * Return the gpsService.
	 * 
	 * @return the GpsLoggerService.
	 */
	public GpsLoggerService getGpsService() {
		return this.gpsService;
	}

	@Override
	public void onStatusMessage(String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFatalMessage(String message) {
		// TODO Auto-generated method stub

	}

	/**
	 * This method is called when location is updated.
	 * 
	 * All this method do is call the current fragment to update data you see in
	 * that fragment.
	 */
	@Override
	public void onLocationUpdate(Location loc) {
		Fragment currentFragment = mFragmentManager
				.findFragmentById(R.id.container);
		if (currentFragment instanceof AbstractViewFragment) {
			((AbstractViewFragment) currentFragment).onLocationUpdate(loc);
		}
	}

	/**
	 * This method is called when user click on track list item in the
	 * TrackListFragment fragment.
	 * 
	 * @param track
	 *            The track selected.
	 */
	@Override
	public void onTrackSelected(Track track) {
		TrackFragment fragment = TrackFragment.newInstance(track);
		FragmentManager fragmentManager = getSupportFragmentManager();
        // clear back stack
        //for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
        //    fragmentManager.popBackStack();
        //}
        FragmentTransaction t = fragmentManager.beginTransaction();
        t.replace(R.id.container, fragment);
        //fragmentManager.popBackStack();
        t.addToBackStack(null);
        t.commit();
		
		/*
		TrackDetailFragment tdf = TrackDetailFragment.newInstance();
		tdf.setTrack(track);

		FragmentTransaction transaction = mFragmentManager.beginTransaction();
		transaction.replace(R.id.container, tdf);
		transaction.addToBackStack(null); // Add this fragment to stack because
											// the user can
											// back to the latter fragment.
		transaction.commitAllowingStateLoss();
		*/
	}

	/**
	 * We need to control back button (or up button) to back to some fragments
	 * to another depend on fragments configurations.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mFragmentManager.getBackStackEntryCount() == 0) {
				this.finish();
				return false;
			} else {
				mFragmentManager.popBackStack();
			}
		}

		return true;
	}

	/**
	 * Indicates that status of the provider has changed. See documentation of
	 * this method in GpsLoggerService.
	 * 
	 * @param provider
	 *            the provider.
	 * @param status
	 *            the new status.
	 * @param extras
	 *            the extras.
	 */
	public void onStatusChanged(String provider, int status, Bundle extras) {
		/*
		 * if(status == LocationProvider.OUT_OF_SERVICE || status ==
		 * LocationProvider.TEMPORARILY_UNAVAILABLE) { Toast.makeText(this,
		 * R.string.out_of_service, Toast.LENGTH_SHORT).show(); } else {
		 * Toast.makeText(this, R.string.service_again_available,
		 * Toast.LENGTH_SHORT).show(); }
		 */
	}

	@Override
	public void onNmeaSentence(long timestamp, String nmeaSentence) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSatelliteCount(int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStartLogging() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopLogging() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSetAnnotation() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClearAnnotation() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFileName(String newFileName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onWaitingForLocation(boolean inProgress) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * When user create a new segment and click ok this method will be called.
	 */
	@Override
	public void onDialogPositiveClick(String segmentName,
    		Long trackId, TrackPoint begin, TrackPoint end) {
		if (SegmentUtil.addSegment(this, segmentName, trackId, begin, end)) {
			Toast.makeText(this, R.string.segment_created, Toast.LENGTH_LONG).show();
		}
		else {
			Toast.makeText(this, R.string.fail_segment_created, Toast.LENGTH_LONG).show();
		}
	}

	/***** Implement the interface TrackListFragment.ProgressCallbacks *****/
	/**
	 * These methods are called from fragments through callback in these
	 * fragments to show and dismiss loading dialog.
	 */
	@Override
	public void onPreExecute() {
		// Create the progress dialog.
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setMessage(getString(R.string.loading_file));
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		// Stop rotation screen.
		int current_orientation = getResources().getConfiguration().orientation;
		if (current_orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		// Show the progress dialog.
		mProgressDialog.show();
	}

	@Override
	public void onProgressUpdate(int percent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCancelled() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
	}

	@Override
	public void onPostExecute() {
		// Dismiss the dialog progress.
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}

		// Load the track list fragment to refresh and load the new track.
		FragmentTransaction transaction = mFragmentManager.beginTransaction();
		transaction.replace(R.id.container, TrackListFragment.newInstance());
		mFragmentManager.popBackStack();
		transaction.commitAllowingStateLoss();

		// Activate the rotation screen.
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

	}
	/*** End implement the interface TrackListFragment.ProgressCallbacks ***/
}
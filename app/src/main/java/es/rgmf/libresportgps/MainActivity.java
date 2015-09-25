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

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import es.rgmf.libresportgps.adapter.NavDrawerListAdapter;
import es.rgmf.libresportgps.common.Session;
import es.rgmf.libresportgps.data.NavDrawerItem;
import es.rgmf.libresportgps.db.DBModel;
import es.rgmf.libresportgps.db.orm.Segment;
import es.rgmf.libresportgps.db.orm.Track;
import es.rgmf.libresportgps.db.orm.TrackPoint;
import es.rgmf.libresportgps.fragment.AbstractViewFragment;
import es.rgmf.libresportgps.fragment.DataViewFragment;
import es.rgmf.libresportgps.fragment.SegmentListFragment;
import es.rgmf.libresportgps.fragment.SettingsFragment;
import es.rgmf.libresportgps.fragment.StatsFragment;
import es.rgmf.libresportgps.fragment.TrackFragment;
import es.rgmf.libresportgps.fragment.TrackListFragment;
import es.rgmf.libresportgps.fragment.dialog.AddSegmentDialog.AddSegmentDialogListener;
import es.rgmf.libresportgps.gps.GpsLoggerService;
import es.rgmf.libresportgps.gps.GpsLoggerServiceConnection;
import es.rgmf.libresportgps.gps.IGpsLoggerServiceClient;

/**
 * This class represent the main activity of the application.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class MainActivity extends FragmentActivity implements
		IGpsLoggerServiceClient, TrackListFragment.OnTrackListSelectedListener,
		TrackListFragment.ProgressCallbacks, SegmentListFragment.OnSegmentListSelectedListener {

	private ProgressDialog mProgressDialog = null;

	/**
	 * Needed attributes to creates and manages service.
	 */
	private static Intent serviceIntent;
	private GpsLoggerService gpsService;
	private GpsLoggerServiceConnection gpsServiceConnection = new GpsLoggerServiceConnection(
			this);

	/**
	 * Enum with all drawer menu items identifier.
	 */
	public enum DrawerMenu {
		TRACKS,
		DATAVIEW,
		SEGMENTS,
		SETTINGS;
	}

	/**
	 * Fragment manager.
	 */
	private FragmentManager mFragmentManager;
	
	/**
	 * The drawer layout.
	 */
	private DrawerLayout mDrawerLayout;
	private RelativeLayout mRelativeLayout;
	
	/**
	 * The drawer list view.
	 */
    private ListView mDrawerList;
    
    /**
     * The drawer toggle.
     */
    private ActionBarDrawerToggle mDrawerToggle;
    
    /**
     * Navigation drawer title.
     */
    private CharSequence mDrawerTitle;
 
    /**
     * Drawer list of items titles.
     */
    private String[] mNavMenuTitles;
    
    /**
     * The icons of the drawer list items.
     */
    private TypedArray mNavMenuIcons;
 
    /**
     * List of drawer items
     */
    private ArrayList<NavDrawerItem> mNavDrawerItems;
    
    /**
     * The drawer list adapter.
     */
    private NavDrawerListAdapter mDrawerListAdapter;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	private Context mContext;

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

		mContext = this;

		setContentView(R.layout.activity_main);

		serviceIntent = new Intent(this, GpsLoggerService.class);
		mTitle = getTitle();

		// Load slide menu items from resources.
        mNavMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
 
        // Load navigation drawer icons from resources.
        mNavMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);
 
        // Get drawer layout and list view.
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relative_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
 
        // Create and add navigation drawer items to array.
        mNavDrawerItems = new ArrayList<NavDrawerItem>();
        mNavDrawerItems.add(new NavDrawerItem(mNavMenuTitles[0], mNavMenuIcons.getResourceId(0, -1)));
        mNavDrawerItems.add(new NavDrawerItem(mNavMenuTitles[1], mNavMenuIcons.getResourceId(1, -1)));
        mNavDrawerItems.add(new NavDrawerItem(mNavMenuTitles[2], mNavMenuIcons.getResourceId(2, -1)));
		mNavDrawerItems.add(new NavDrawerItem(mNavMenuTitles[3], mNavMenuIcons.getResourceId(3, -1)));
        
        // Recycle the typed array.
        mNavMenuIcons.recycle();
        
        // Setting the navigation drawer list adapter.
        mDrawerListAdapter = new NavDrawerListAdapter(getApplicationContext(),
                mNavDrawerItems);
        mDrawerList.setAdapter(mDrawerListAdapter);
        
        // Enabling action bar app icon and behaving it as toggle button.
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ){
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }
 
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
 
        if (savedInstanceState == null) {
            // On first time display view for first navigation item.
        	onNavigationDrawerItemSelected(0);
        }
        
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
        
        // Settings on click.
        LinearLayout settings = (LinearLayout) findViewById(R.id.settings_layout);
        settings.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), SettingsActivity.class);
			    startActivity(intent);
			}
		});
        
		// Check if external storage is available.
		checkExternalStorage();

		// Set LibreSportGPS to keepScreenOn.
		//View libresportgpsView = findViewById(R.id.drawer_layout);
		//libresportgpsView.setKeepScreenOn(true);
		mDrawerLayout.setKeepScreenOn(true);

		checkRecoveryTracks();
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
	
	@Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
 
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            // display view for selected nav drawer item
        	onNavigationDrawerItemSelected(position);
        }
    }

	/**
	 * This method is called when you chose an option in Navigation Drawer.
	 */
	public void onNavigationDrawerItemSelected(int position) {

		DrawerMenu item = DrawerMenu.values()[position];
		/*
		if (item == DrawerMenu.SEGMENTS) {
			Intent intent = new Intent(mContext, OSMActivity.class);
			startActivity(intent);
		}
		else {*/
			// update the main content by replacing fragments
			FragmentTransaction transaction = mFragmentManager.beginTransaction();

			switch (item) {
				case TRACKS:
					transaction
							.replace(R.id.container, TrackListFragment.newInstance());
					break;
				case DATAVIEW:
					transaction.replace(R.id.container, DataViewFragment.newInstance());
					break;
				case SEGMENTS:
					transaction.replace(R.id.container, SegmentListFragment.newInstance());
					break;
				case SETTINGS:
					transaction.replace(R.id.container, StatsFragment.newInstance(StatsFragment.NONE, StatsFragment.NONE, StatsFragment.NONE));
					break;
				default:
					break;
			}
			mFragmentManager.popBackStack();
			transaction.commitAllowingStateLoss();
			mDrawerLayout.closeDrawer(mRelativeLayout);
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
		//}
	}

	/**
	 * This method is called when the activity will be destroyed.
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		stopAndUnbindToService();
		/*
		 * if(mProgressDialog != null) { Log.v("MainActivity::onDestroy",
		 * "mProgressDialog.dismiss"); mProgressDialog.dismiss(); }
		 */
	}

	@Override
	protected void onStop() {
		super.onStop();
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

			// End recording track with open track status.
			DBModel.endRecordingTrack(this,
					Session.getTrackId(), Track.OPEN_TRACK, track);
		}
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
		//if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			return true;
		//}
		//return super.onCreateOptionsMenu(menu);
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
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
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
	 * Check if there are tracks to recover because crash or something like that.
	 *
	 * Only the last track can be recovered.
	 */
	private void checkRecoveryTracks() {
		List<Track> trackList = DBModel.getOpenTracks(this);
		if (trackList.size() > 0) {
			final Track track = trackList.get(trackList.size() - 1);

			new AlertDialog.Builder(this)
					.setTitle(R.string.recovery_track)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setMessage(
							getResources()
									.getString(R.string.recovery_track_hint))
					.setCancelable(true)
					.setPositiveButton(android.R.string.yes,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
													int which) {
									Session.setTrackingStarted(true);
									Session.setTrackId(track.getId());
									Session.setTracking(true);
									Session.setTrackingStarted(true);
									Session.setDistance(track.getDistance());
									Session.setMaxSpeed(track.getMaxSpeed());
									Session.setStartTimeStamp(track.getStartTime());
									Session.setLastTimeStamp(track.getFinishTime());
									Session.setActivityTimeStamp(track.getActivityTime());
									Session.setFileName(track.getTitle());
									onNavigationDrawerItemSelected(DrawerMenu.DATAVIEW.ordinal());
								}
							})
					.setNegativeButton(android.R.string.no,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
													int which) {
									DBModel.deleteTrack(mContext, track.getId());
									dialog.cancel();
								}
							}).create().show();
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
	 * This method is called when user click on track list year item in the
	 * TrackListFragment fragment.
	 * 
	 * @param year
	 *            The number of the year according to Calendar object.
	 */
	@Override
	public void onYearSelected(int year) {
		StatsFragment fragment = StatsFragment.newInstance(year, StatsFragment.NONE, StatsFragment.NONE);
		FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction t = fragmentManager.beginTransaction();
        t.replace(R.id.container, fragment);
        //fragmentManager.popBackStack();
        t.addToBackStack(null);
        t.commit();
	}
	
	/**
	 * This method is called when user click on track list month item in the
	 * TrackListFragment fragment.
	 * 
	 * @param year
	 *            The number of the month according to Calendar object.
	 */
	@Override
	public void onMonthSelected(int year, int month) {
		StatsFragment fragment = StatsFragment.newInstance(year, month, StatsFragment.NONE);
		FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction t = fragmentManager.beginTransaction();
        t.replace(R.id.container, fragment);
        //fragmentManager.popBackStack();
        t.addToBackStack(null);
        t.commit();
	}

	/**
	 * This method is called when user click on segment list item in the
	 * SegmentListFragment fragment.
	 *
	 * @param segment
	 *            The segment selected.
	 */
	@Override
	public void onSegmentSelected(Segment segment) {
		/*
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
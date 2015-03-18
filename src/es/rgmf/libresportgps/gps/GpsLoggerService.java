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

package es.rgmf.libresportgps.gps;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import es.rgmf.libresportgps.common.Session;
import es.rgmf.libresportgps.common.Utilities;
import es.rgmf.libresportgps.db.DBModel;
import es.rgmf.libresportgps.file.FileFactory;
import es.rgmf.libresportgps.file.writer.IWriter;

/**
 * This class is the service of the GPS. Provide GPS service to the application
 * through the main activity.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class GpsLoggerService extends Service implements LocationListener {
	private LocationManager gpsLocationManager;
	private final IBinder binder = new GpsLoggerBinder();
	private static IGpsLoggerServiceClient serviceClient;

	@Override
	public void onCreate() {
		super.onCreate();

		gpsLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		gpsLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				0, 0, this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Session.setGpsEnabled(gpsLocationManager.isProviderEnabled("gps"));
		return Service.START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		stopSelf();

		// We don't want onRebind() to be called, so return false.
		return false;
	}

	/**
	 * This method do these tasks: - Show data in the view. - Save the
	 * information in the database. - Update Session needed data. - Write the
	 * file/s.
	 */
	@Override
	public void onLocationChanged(Location loc) {
		// We receive location so gps is ready.
		Session.setGpsReady(true);
		
		// Update Session data from Location.
		updateSessionDataFromLocation(loc);
		
		// Update the view to show location information to user.
		serviceClient.onLocationUpdate(loc);

		// First of all we check if time and distance before logging
		// is completed.
		if ((Session.getLastLoggingTime() + Session.getTimeBeforeLogging()) < System
				.currentTimeMillis() && Session.isTracking()) {
			
			// Update Session data from Location.
			//updateSessionDataFromLocation(loc);

			// Update the view to show location information to user.
			//serviceClient.onLocationUpdate(loc);

			// Save information in database.
			if (Session.getTrackId() != -1)
				DBModel.saveLocation(this, Session.getTrackId(), loc);

			// Save information in files.
			for (IWriter file : FileFactory.getFiles()) {
				file.writeTrack(loc);
			}
		}
	}

	/**
	 * Update needed Session data from loc (Location).
	 * 
	 * @param loc
	 *            The Location object.
	 */
	private void updateSessionDataFromLocation(Location loc) {
		// Activity time.
		Session.updateActivityTimeStamp();

		// Max speed.
		float speed = loc.getSpeed() * 3.6f; // m/s to km/h
		if (speed > Session.getMaxSpeed()) {
			Session.setMaxSpeed(speed);
		}

		// If there is last location.
		if (Session.getLastLocation() != null) {
			// Distance.
			double d = Utilities.CalculateDistance(Session.getLastLocation()
					.getLatitude(), Session.getLastLocation().getLongitude(),
					loc.getLatitude(), loc.getLongitude());
			Session.setDistance(Session.getDistance() + d);
			// Altitude gain.
			Session.setAltitudeGain(d, loc.getAltitude());
			// Max. altitude.
			if (loc.getAltitude() > Session.getMaxAltitude()) {
				Session.setMaxAltitude(loc.getAltitude());
			}
			// Min. altitude.
			if (loc.getAltitude() < Session.getMinAltitude())
				Session.setMinAltitude(loc.getAltitude());
		}
		// It is the first location so is the minimum altitude and the start
		// altitude.
		else {
			Session.setMinAltitude(loc.getAltitude());
			Session.setStartAltitude(loc.getAltitude());
		}

		// Set last location with current location to the next location update.
		Session.setLastLocation(loc);

		// Set last time logging.
		Session.setLastLoggingTime(loc.getTime());
	}

	@Override
	public void onProviderDisabled(String arg0) {
		Session.setGpsEnabled(false);
	}

	@Override
	public void onProviderEnabled(String arg0) {
		Session.setGpsEnabled(true);

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		GpsStatus gpsStatus = gpsLocationManager.getGpsStatus(null);
		if (gpsStatus != null) {
			Iterable<GpsSatellite> sats = gpsStatus.getSatellites();
			int satsInView = 0;
			int satsUsed = 0;
			for (GpsSatellite sat : sats) {
				satsInView++;
				if (sat.usedInFix()) {
					satsUsed++;
				}
			}
			Session.setSatellitesInView(satsInView);
			Session.setSatellitesInUsed(satsUsed);
		}
		/*
		 * if(status == LocationProvider.OUT_OF_SERVICE || status ==
		 * LocationProvider.TEMPORARILY_UNAVAILABLE) {
		 * Session.setTracking(false); Session.setOpenTrkseg(true); } else {
		 * Session.setTracking(true); } serviceClient.onStatusChanged(provider,
		 * status, extras);
		 */
	}

	/**
	 * Sets the activity form for this service. The activity form needs to
	 * implement IGpsLoggerServiceClient.
	 * 
	 * @param mainForm
	 *            The calling client
	 */
	public static void setServiceClient(IGpsLoggerServiceClient mainForm) {
		serviceClient = mainForm;
	}

	/**
	 * Bind interface for service interaction
	 */
	public class GpsLoggerBinder extends Binder {
		/**
		 * Called by the activity when binding. Returns itself.
		 * 
		 * @return the GPS Logger service
		 */
		public GpsLoggerService getService() {
			return GpsLoggerService.this;
		}
	}
}

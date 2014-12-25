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
		gpsLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0.0f, this);
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

	@Override
	public void onLocationChanged(Location loc) {
		if (!Session.isGpsReady())
			Session.setGpsReady(true);

		if (Session.isTracking()) {
			// Update the view to show location information to user.
			serviceClient.onLocationUpdate(loc);
			
			// Save information in database.
			if(Session.getTrackId() != -1)
				DBModel.saveLocation(this, Session.getTrackId(), loc);
			
			// Update last location in Session.
			// BE CAREFUL!!! This Session update must be after save location
			//               database.
			Session.setLastLocation(loc);
	
			// Save information in files.
			for(IWriter file: FileFactory.getFiles()) {
				file.writeTrack(loc);
			}
		}
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
		if(gpsStatus != null) {
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
		if(status == LocationProvider.OUT_OF_SERVICE || 
		   status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
			Session.setTracking(false);
			Session.setOpenTrkseg(true);
		}
		else {
			Session.setTracking(true);
		}
		serviceClient.onStatusChanged(provider, status, extras);
		*/
	}
    
    /**
     * Sets the activity form for this service. The activity form needs to
     * implement IGpsLoggerServiceClient.
     *
     * @param mainForm The calling client
     */
    public static void setServiceClient(IGpsLoggerServiceClient mainForm) {
        serviceClient = mainForm;
    }
    
    /**
	 * Bind interface for service interaction
	 */
	public class GpsLoggerBinder extends Binder {		
		/**
		 * Called by the activity when binding.
		 * Returns itself.
		 * @return the GPS Logger service
		 */
		public GpsLoggerService getService() {			
			return GpsLoggerService.this;
		}
	}
}

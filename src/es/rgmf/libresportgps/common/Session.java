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

package es.rgmf.libresportgps.common;

import android.app.Application;
import android.location.Location;
import android.os.Environment;
import android.preference.PreferenceManager;

/**
 * This class represent the current session. It has all information needed
 * about GPS data and all data needed by the application.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class Session extends Application {
	private static long trackId = -1;

	private static Location lastLocation = null;
	private static double distance = 0f; // In meters.
	private static float maxSpeed = 0f; // In km/h.
	
	private static long lastLoggingTime = 0L;
	private static int timeBeforeLogging = 0;
	
	private static int satellitesInView = 0;
	private static int satellitesInUsed = 0;
	private static boolean boundToService = false;
	private static boolean isAlertDialogGPSShowed = false;
	private static boolean isTracking = false; // The play button is showing when false
											   // and the pause button is showing when true.
	private static boolean trackingStarted = false;
	private static boolean isGpsReady = false;
	private static boolean isGpsEnabled = false;
	private static long startTimeStamp = 0; // Start time in milliseconds.
	private static long lastTimeStamp = 0; // Last time in milliseconds (last time the user paused the activity).
	private static long activityTimeStamp = 0; // Activity time in milliseconds.
	private static double startAltitude = 0.0d;
	private static double altitudeGain = 0.0d;
	private static double altitudeLoss = 0.0d;
	private static double minAltitude = 0.0d;
	private static double maxAltitude = 0.0d;
	private static String fileName = Utilities.timeStampFormatterForFilename(System.currentTimeMillis());
	private static boolean openTrkseg = false; // If it attribute is true then it will open a new trkseg tag in GPX file.
	private static String appFolder = Environment.getExternalStorageDirectory() + "/libresportgps";
	
	// Session data to calculate elevation gain.
	private static Double minElevationGain = 0.99d;
	private static Double distanceGain = 100d;
	private static Double startElevationGain = 0d;
	private static Double finishElevationGain = 0d;
	private static Double distanceAccGain = 0d;
	
	public static void reset() {
		trackId = -1;
		lastLocation = null;
		distance = 0f;
		maxSpeed = 0f;
		isTracking = false;
		trackingStarted = false;
		startTimeStamp = 0;
		lastTimeStamp = 0;
		activityTimeStamp = 0;
		openTrkseg = false;
		newFilename();
	}
	
	public static long getTrackId() {
		return trackId;
	}

	public static void setTrackId(long trackId) {
		Session.trackId = trackId;
	}
	
	public static long getStartTimeStamp() {
		return startTimeStamp;
	}

	public static void setStartTimeStamp(long startTimeStamp) {
		Session.startTimeStamp = startTimeStamp;
	}

	public static long getLastTimeStamp() {
		return lastTimeStamp;
	}

	public static void setLastTimeStamp(long lastTimeStamp) {
		Session.lastTimeStamp = lastTimeStamp;
	}

	public static long getActivityTimeStamp() {
		return activityTimeStamp;
	}

	public static void setActivityTimeStamp(long activityTimeStamp) {
		Session.activityTimeStamp = activityTimeStamp;
	}
	
	public static void updateActivityTimeStamp() {
		long currentTimeInMilliseconds = System.currentTimeMillis();
		if(Session.lastTimeStamp > 0)
			Session.activityTimeStamp += (currentTimeInMilliseconds - Session.lastTimeStamp);
		else
			Session.activityTimeStamp = 0;
		Session.lastTimeStamp = currentTimeInMilliseconds;
	}

	public static Location getLastLocation() {
		return lastLocation;
	}

	public static void setLastLocation(Location lastLocation) {
		Session.lastLocation = lastLocation;
	}

	public static double getDistance() {
		return distance;
	}

	public static void setDistance(double distance) {
		Session.distance = distance;
	}

	public static float getMaxSpeed() {
		return maxSpeed;
	}

	public static void setMaxSpeed(float maxSpeed) {
		Session.maxSpeed = maxSpeed;
	}

	public static boolean isTracking() {
		return isTracking;
	}

	public static void setTracking(boolean isTracking) {
		Session.isTracking = isTracking;
		if(isTracking) {
			if(Session.startTimeStamp == 0) {
				Session.startTimeStamp = System.currentTimeMillis();
				Session.lastTimeStamp = Session.startTimeStamp;
			}
			else {
				Session.lastTimeStamp = System.currentTimeMillis();
			}
		}
	}

	public static boolean isGpsReady() {
		return isGpsReady;
	}

	public static void setGpsReady(boolean isGpsReady) {
		Session.isGpsReady = isGpsReady;
	}

	public static boolean isGpsEnabled() {
		return isGpsEnabled;
	}

	public static void setGpsEnabled(boolean isGpsEnabled) {
		Session.isGpsEnabled = isGpsEnabled;
	}

	public static String getFileName() {
		return fileName;
	}

	public static void setFileName(String fileName) {
		Session.fileName = fileName;
	}
	
	public static void newFilename() {
		Session.fileName = Utilities.timeStampFormatterForFilename(System.currentTimeMillis());
	}

	public static boolean openTrkseg() {
		return openTrkseg;
	}

	public static void setOpenTrkseg(boolean openTrkseg) {
		Session.openTrkseg = openTrkseg;
	}

	public static boolean isTrackingStarted() {
		return trackingStarted;
	}

	public static void setTrackingStarted(boolean trackingStarted) {
		Session.trackingStarted = trackingStarted;
	}

	public static String getAppFolder() {
		return appFolder;
	}

	public static void setAppFolder(String appFolder) {
		Session.appFolder = appFolder;
	}

	public static boolean isAlertDialogGPSShowed() {
		return isAlertDialogGPSShowed;
	}

	public static void setAlertDialogGPSShowed(boolean isAlertDialogGPSShowed) {
		Session.isAlertDialogGPSShowed = isAlertDialogGPSShowed;
	}

	public static boolean isBoundToService() {
		return boundToService;
	}

	public static void setBoundToService(boolean boundToService) {
		Session.boundToService = boundToService;
	}

	public static int getSatellitesInView() {
		return satellitesInView;
	}

	public static void setSatellitesInView(int satellitesInView) {
		Session.satellitesInView = satellitesInView;
	}

	public static int getSatellitesInUsed() {
		return satellitesInUsed;
	}

	public static void setSatellitesInUsed(int satellitesInUsed) {
		Session.satellitesInUsed = satellitesInUsed;
	}

	public static double getMaxAltitude() {
		return maxAltitude;
	}

	public static void setMaxAltitude(double maxAltitude) {
		Session.maxAltitude = maxAltitude;
	}

	public static double getMinAltitude() {
		return minAltitude;
	}

	public static void setMinAltitude(double minAltitude) {
		Session.minAltitude = minAltitude;
	}

	public static double getStartAltitude() {
		return startAltitude;
	}

	public static void setStartAltitude(double startAltitude) {
		Session.startAltitude = startAltitude;
		Session.startElevationGain = startAltitude;
	}

	public static double getAltitudeGain() {
		return altitudeGain;
	}

	public static void setAltitudeGain(double altitudeGain) {
		Session.altitudeGain += altitudeGain;
	}
	
	public static void setAltitudeGain(double distance, double altitude) {
		distanceAccGain += distance;
		finishElevationGain = altitude;
		if (distanceAccGain >= distanceGain) {
			if ((finishElevationGain - startElevationGain) >= minElevationGain) {
				altitudeGain += (finishElevationGain - startElevationGain);
			}
			startElevationGain = finishElevationGain;
			distanceAccGain = 0d;
		}
	}

	public static double getAltitudeLoss() {
		return altitudeLoss;
	}

	public static void setAltitudeLoss(double altitudeLoss) {
		Session.altitudeLoss += altitudeLoss;
	}

	public static Double getMinElevationGain() {
		return minElevationGain;
	}

	public static void setMinElevationGain(Double minElevationGain) {
		Session.minElevationGain = minElevationGain;
	}

	public static Double getDistanceGain() {
		return distanceGain;
	}

	public static void setDistanceGain(Double distanceGain) {
		Session.distanceGain = distanceGain;
	}

	public static int getTimeBeforeLogging() {
		return timeBeforeLogging;
	}

	public static void setTimeBeforeLogging(int timeBeforeLogging) {
		Session.timeBeforeLogging = timeBeforeLogging;
	}

	public static long getLastLoggingTime() {
		return lastLoggingTime;
	}

	public static void setLastLoggingTime(long lastLogginigTime) {
		Session.lastLoggingTime = lastLogginigTime;
	}
}

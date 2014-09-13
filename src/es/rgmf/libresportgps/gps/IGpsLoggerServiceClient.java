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

import android.location.Location;
import android.os.Bundle;

/**
 * This is the Interface all Activities that need GPS Service (GpsLoggerService) have to
 * implement.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public interface IGpsLoggerServiceClient {
    /**
     * New message from the service to be displayed on the activity form.
     *
     * @param message
     */
    public void onStatusMessage(String message);

    /**
     * Indicates that a fatal error has occurred, logging will stop.
     *
     * @param message
     */
    public void onFatalMessage(String message);

    /**
     * A new location fix has been obtained.
     *
     * @param loc
     */
    public void onLocationUpdate(Location loc);


    /**
     * A new NMEA sentence was received
     *
     * @param timestamp
     * @param nmeaSentence
     */
    public void onNmeaSentence(long timestamp, String nmeaSentence);

    /**
     * New satellite count has been obtained.
     *
     * @param count
     */
    public void onSatelliteCount(int count);

    /**
     * Asking the calling activity form to clear itself.
     */
    public void onStartLogging();

    /**
     * Asking the calling activity form to indicate that logging has stopped
     */
    public void onStopLogging();

    /**
     * Asking the calling activity form to indicate that an annotation is pending
     */
    public void onSetAnnotation();

    /**
     * Asking the calling activity form to indicate that no annotation is pending
     */
    public void onClearAnnotation();


    /**
     * A new current file name is available.
     *
     * @param newFileName
     */
    public void onFileName(String newFileName);


    /**
     * Indicates that the location manager has started waiting for its next location
     */
    public void onWaitingForLocation(boolean inProgress);
    
    /**
     * Indicates that status of the provider has changed. See documentation of this method in
     * GpsLoggerService.
     * 
     * @param provider the provider.
     * @param status the new status.
     * @param extras the extras.
     */
    public void onStatusChanged(String provider, int status, Bundle extras);
}

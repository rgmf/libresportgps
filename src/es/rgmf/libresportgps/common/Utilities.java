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

import java.text.SimpleDateFormat;

/**
 * Several utilities that can be used in all the application.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class Utilities {
	/**
     * Uses the Haversine formula to calculate the distance between to latitude and longitude coordinates.
     * 
     * Haversine formula:
     * A = sin²(Δlat/2) + cos(lat1) . cos(lat2) . sin²(Δlong/2)
     * C = 2 . atan2(√a, √(1−a))
     * D = R . c
     * R = radius of earth, 6371 km
     * 
     * All angles are in radians.
     * 
     * @param latitude1  The first point's latitude.
     * @param longitude1 The first point's longitude.
     * @param latitude2  The second point's latitude.
     * @param longitude2 The second point's longitude.
     * @return The distance between the two points in meters.
     */
    public static double CalculateDistance(double latitude1, double longitude1, double latitude2, double longitude2) {
        double deltaLatitude = Math.toRadians(Math.abs(latitude1 - latitude2));
        double deltaLongitude = Math.toRadians(Math.abs(longitude1 - longitude2));
        double latitude1Rad = Math.toRadians(latitude1);
        double latitude2Rad = Math.toRadians(latitude2);

        double a = Math.pow(Math.sin(deltaLatitude / 2), 2) +
                (Math.cos(latitude1Rad) * Math.cos(latitude2Rad) * Math.pow(Math.sin(deltaLongitude / 2), 2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return 6371 * c * 1000;
    }
    
    /**
     * Convert the timeStamp in the String HH:mm:ss:SS
     * 
     * @param timeStamp
     * @return
     */
    public static String timeStampFormatter(long timeStamp) {
    	SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss:SS");
    	return formatter.format(timeStamp);
    }
    
    /**
     * Convert the timeStamp in the String dd/MM/yyyy HH:mm:ss:SS
     * 
     * @param timeStamp
     * @return
     */
    public static String timeStampCompleteFormatter(long timeStamp) {
    	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    	return formatter.format(timeStamp);
    }
    
    /**
     * Convert the timeStamp in the String HH_mm_ss_SS
     * 
     * @param timeStamp
     * @return
     */
    public static String timeStampFormatterForFilename(long timeStamp) {
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
    	return formatter.format(timeStamp);
    }
    
    /**
     * Convert milliseconds to hours and returns hours.
     * 
     * @param millis
     * @return
     */
    public static long millisecondsToHours(long millis) {
    	if(millis == 0)
    		return 0;
    	
    	return (millis / (1000 * 60 * 60)) % 24;
    }

    /**
     * Return a string representing a distance.
     * 
     * @param distance
     * @return
     */
	public static String distance(double distance) {
		return String.format("%.2f km", distance / 1000);
	}

	/**
     * Return a string representing an elevation.
     * 
     * @param elevation
     * @return
     */
	public static String elevation(double elevation) {
		return String.format("%.2f m", elevation);
	}

	/**
     * Return a string representing a speed.
     * 
     * @param speed
     * @return
     */
	public static String speed(double speed) {
		return String.format("%.2f km/h", speed);
	}
}

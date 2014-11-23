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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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
    	if(timeStamp > 0) {
    		long milliseconds = timeStamp % 1000;
    		long seconds = (timeStamp / 1000) % 60;
    		long minutes = (timeStamp / 1000 / 60) % 60;
    		long hours = (timeStamp / 1000 / 60 / 60) % 24;
    		return String.format("%02d:%02d:%02d.%04d", hours, minutes, seconds, milliseconds);
    	}
    	else
    		return "00:00:00.00";
    }
    
    /**
     * Convert the timeStamp in the String dd/MM/yyyy HH:mm:ss:SS
     * 
     * @param timeStamp
     * @return
     */
    public static String timeStampCompleteFormatter(long timeStamp) {
    	if(timeStamp > 0) {
	    	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	    	return formatter.format(timeStamp);
    	}
    	else
    		return "00/00/0000 00:00:00";
    }
    
    /**
     * Convert the timeStamp in the String HH_mm_ss_SS
     * 
     * @param timeStamp
     * @return
     */
    public static String timeStampFormatterForFilename(long timeStamp) {
    	if(timeStamp > 0) {
	    	SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
	    	return formatter.format(timeStamp);
    	}
    	else
    		return "00000000_000000";
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

	/**
	 * Return an average speed with activityTime in milliseconds and distance
	 * in meters.
	 * 
	 * @param activityTime The activity time in milliseconds.
	 * @param distance The distance in meters.
	 * @return km/h.
	 */
	public static String avgSpeed(long activityTime, float distance) {
		float km = distance / ((float)1000);
		float hour = ((float)activityTime) / ((float)1000) / ((float)60) / ((float)60);
		
		return String.format("%.2f km/h", km/hour);
	}
	
	/**
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth) {
	
	        final int halfHeight = height / 2;
	        final int halfWidth = width / 2;
	
	        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        // height and width larger than the requested height and width.
	        while ((halfHeight / inSampleSize) > reqHeight
	                && (halfWidth / inSampleSize) > reqWidth) {
	            inSampleSize *= 2;
	        }
	    }
	
	    return inSampleSize;
	}
	
	/**
	 * It loads an image and return the bitmap effciently (see {@link https://developer.android.com/training/displaying-bitmaps/load-bitmap.html}.
	 * 
	 * @param selectedImagePath
	 * @param i
	 * @param j
	 * @return
	 */
	public static Bitmap loadBitmapEfficiently(String imagePath, int reqWidth, int reqHeight) {
		BitmapFactory.Options options = new BitmapFactory.Options();
    	options.inJustDecodeBounds = true;
    	//BitmapFactory.decodeResource(getResources(), R.id.student_photo_input, options);
    	BitmapFactory.decodeFile(imagePath, options);
    	
    	// Calculate inSampleSize
        options.inSampleSize = Utilities.calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap photoBitmap = BitmapFactory.decodeFile(imagePath, options);
        
        return photoBitmap;
	}
}

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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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
     * ISO 8601 format.
     * 
     * Convert milliseconds to date in the String yyyy-MM-dd'T'hh:mm:ss.SSS'Z' format.
     * 
     * @param milliseconds
     * @return The string representation time or "0000-00-00'T'00:00:00.000'Z'" if error.
     */
    public static String millisecondsToDateForGPX(long milliseconds) {
    	DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTimeInMillis(milliseconds);
    	
    	return formatter.format(calendar.getTime());
    }
    
    /**
     * ISO 8601 format.
     * 
	 * Return a time in ISO8601 format in milliseconds.
	 * 
	 * Accept ISO8601 format with and without milliseconds:
	 * - yyyy-MM-dd'T'hh:mm:ss'Z'
	 * - yyyy-MM-dd'T'hh:mm:ss.SSS'Z'
	 * 
	 * @param strDate The string representing a time.
	 * @return The milliseconds or 0 if raise and exception or strDate is not ok.
	 */
	public static Long getMillisecondsFromStringGPXDate(String strDate) {
		DateFormat shortFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
		DateFormat longFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
		try {
			if(strDate.length() == 20) {
				return shortFormatter.parse(strDate).getTime();
			}
			else if(strDate.length() == 24) {
				return longFormatter.parse(strDate).getTime();
			}
			else {
				return 0L;
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return 0L;
		}
	}
	
	/**
     * Return a yyyy-MM-dd date format.
     * 
     * @param milliseconds
     * @return The string representation date or "0000-00-00" if error.
     */
    public static String millisecondsToDate(long milliseconds) {
    	DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTimeInMillis(milliseconds);
    	
    	return formatter.format(calendar.getTime());
    }
    
    /**
     * Convert the timeStamp in the String HH:mm:ss:SS
     * 
     * @param timeStamp
     * @return
     */
    public static String timeStampFormatter(long timeStamp) {
    	if(timeStamp > 0) {
    		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss.SS");
    		return formatter.format(timeStamp);
    	}
    	else
    		return "00:00:00.00";
    }
    
    /**
     * Convert the timeStamp in the String HH:mm:ss
     * 
     * @param timeStamp
     * @return
     */
    public static String timeStampSecondsFormatter(long timeStamp) {
    	if(timeStamp > 0) {
    		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
    		return formatter.format(timeStamp);
    	}
    	else
    		return "00:00:00";
    }
    
    /**
     * Convert the timeStamp to the String HH:mm:ss
     * 
     * This method calculate the total time so this can be more than 24 hours so is not 
     * a time but a number of hours, minutes and seconds.
     * 
     * @param timeStamp
     * @return
     */
    public static String totalTimeFormatter(long timeStamp) {
    	if(timeStamp > 0) {
    		long seconds, minutes, hours;
    		seconds = (timeStamp / 1000) % 60;
    		minutes = (timeStamp / 1000 / 60) % 60;
    		hours = (timeStamp / 1000 / 60 / 60);
    		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    	}
    	else
    		return "00:00:00";
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
		if (distance < 1000)
			return String.format("%d m", (int) distance);
		else
			return String.format("%.2f km", distance / 1000d);
	}
	
	/**
	 * Return a string representing a gain.
	 * 
	 * @param gain
	 * @return
	 */
	public static String gain(int gain) {
		return String.format("%d m", gain);
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
	 * Return a string representing the gradient.
	 * 
	 * @param elevation
	 * @param distance
	 * @return
	 */
	public static String gradient(double elevation, double distance) {
		double gradient = (100d * elevation) / distance;
		return String.format("%.2f %%", gradient);
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
	 * Return the tempo per km.
	 * 
	 * @param distance Distance in meters.
	 * @param activityTime Time in milliseconds.
	 * @return
	 */
	public static String tempoPerKm(Float distance, Long activityTime) {
		float km = distance / 1000;
		float tempoMillis = ((float) activityTime) / km;
		long seconds = (long) ((tempoMillis / 1000) % 60);
		long minutes = (long) ((tempoMillis / 1000 / 60) % 60);
		return String.format("%02d:%02d min/km", minutes, seconds);
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
	 * Return a complete name of the month String.
	 * 
	 * @param m number of the Calendar month.
	 * @return
	 */
	public static String getNameOfCalendarMonth(int m) {
		Calendar cal = Calendar.getInstance();
		// We need to subtract 1 because Calendar begin by 0 (January).
		cal.set(Calendar.MONTH, m - 1);
		return cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
	}
	
	/**
	 * It loads an image and return the bitmap efficiently (see {@link https://developer.android.com/training/displaying-bitmaps/load-bitmap.html}.
	 * 
	 * @param selectedImagePath
	 * @param i
	 * @param j
	 * @return
	 */
	/*
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
	*/
}
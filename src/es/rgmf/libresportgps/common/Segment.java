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

import java.util.List;

import android.content.Context;
import android.util.Log;
import es.rgmf.libresportgps.db.DBModel;
import es.rgmf.libresportgps.db.orm.TrackPoint;


/**
 * Several utilities to work with segments.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class Segment {

	public static boolean addSegment(Context context, String segmentName, Long trackId,
			TrackPoint begin, TrackPoint end) {
		Float distance;
		Float avgSpeed;
		Float maxSpeed;
		Long time;
		Float eleGain;
		
		// Distance in meters.
		distance = end.getDistance() - begin.getDistance();
		
		// Time in milliseconds.
		time = end.getTime() - begin.getTime();
		
		// Average speed in km/h.
		avgSpeed = (distance / 1000f) / (time / (1000f * 60f * 60f));
		
		Log.v("Distance:", distance + "");
		Log.v("Time:", time + "");
		Log.v("Avg. Speed:", avgSpeed + "");
		
		/*
		List<TrackPoint> trackPoints = DBModel.getTrackPointsFromTo(context, begin.getId(), end.getId());
		for (TrackPoint trackPoint : trackPoints) {
			
		}
		*/
		
		return false;
	}
	
}

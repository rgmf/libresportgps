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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import es.rgmf.libresportgps.db.DBModel;
import es.rgmf.libresportgps.db.orm.Segment;
import es.rgmf.libresportgps.db.orm.SegmentPoint;
import es.rgmf.libresportgps.db.orm.TrackPoint;


/**
 * Several utilities to work with segments.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class SegmentUtil {

	public static boolean addSegment(Context context, String segmentName, Long trackId,
			TrackPoint begin, TrackPoint end) {
		Float distance;
		Float beginDistance;
		Float avgSpeed;
		Float maxSpeed = Float.MIN_VALUE;
		Long time;
		Double eleGain = 0d;
		List<TrackPoint> trackPointList;
		List<SegmentPoint> segmentPointList;
		SegmentPoint segmentPoint;
		Segment segment;
		Float distanceAccGain = 0f;
		Double finishElevationGain;
		Float distanceGain = 0f;
		Double startElevationGain = begin.getElevation();
		
		// Distance in meters.
		distance = end.getDistance() - begin.getDistance();
		
		// Time in milliseconds.
		time = end.getTime() - begin.getTime();
		
		// Average speed in km/h.
		avgSpeed = (distance / 1000f) / (time / (1000f * 60f * 60f));
		
		// Begin distance.
		beginDistance = begin.getDistance();
		
		// Save in the list all segments points.
		trackPointList = DBModel.getTrackPointsFromTo(context, begin.getId(), end.getId());
		segmentPointList = new ArrayList<SegmentPoint>();
		for (TrackPoint trackPoint : trackPointList) {
			segmentPoint = new SegmentPoint();
			
			// Max speed.
			if (trackPoint.getSpeed() > maxSpeed)
				maxSpeed = trackPoint.getSpeed();
			
			// Set latitude and longitude.
			segmentPoint.setLat(trackPoint.getLat());
			segmentPoint.setLng(trackPoint.getLng());
			
			// Set distance to the fragment point
			segmentPoint.setDistance(trackPoint.getDistance() - beginDistance);
			
			// Set elevation.
			segmentPoint.setElevation(trackPoint.getElevation());
			
			// Add segment point to the list.
			segmentPointList.add(segmentPoint);
			
			// Recalculate elevation gain to add it to the segment later.
			distanceAccGain += trackPoint.getDistance();
			finishElevationGain = trackPoint.getElevation();
			if (distanceAccGain >= distanceGain) {
				if ((finishElevationGain - startElevationGain) >= Session.getMinElevationGain()) {
					eleGain += (finishElevationGain - startElevationGain);
				}
				startElevationGain = finishElevationGain;
				distanceAccGain = 0f;
			}
		}
		
		// Create the segment object.
		segment = new Segment();
		segment.setName(segmentName);
		segment.setDistance(end.getDistance() - begin.getDistance());
		segment.setElevationGain(eleGain);
		
		
		
		
		Log.v("Segment Name:", segment.getName());
		Log.v("Distance:", segment.getDistance() + "");
		Log.v("Time:", time + "");
		Log.v("Avg. Speed:", avgSpeed + "");
		Log.v("Ele. Gain:", (end.getElevation() - begin.getElevation()) + "");//eleGain + "");
		Log.v("Avg. %:", ((100f * (end.getElevation() - begin.getElevation())) / segment.getDistance()) + "");
		
		
		return false;
	}
	
}

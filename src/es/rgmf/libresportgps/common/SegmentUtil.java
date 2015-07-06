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
import es.rgmf.libresportgps.db.DBModel;
import es.rgmf.libresportgps.db.orm.Segment;
import es.rgmf.libresportgps.db.orm.SegmentPoint;
import es.rgmf.libresportgps.db.orm.SegmentTrack;
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
		Double eleGain;
		List<TrackPoint> trackPointList;
		List<SegmentPoint> segmentPointList;
		SegmentPoint segmentPoint;
		Segment segment;
		SegmentTrack segmentTrack;
		Long segmentId;
		
		// Distance in meters.
		distance = end.getDistance() - begin.getDistance();
		
		// Time in milliseconds.
		time = end.getTime() - begin.getTime();
		
		// Average speed in km/h.
		avgSpeed = (distance / 1000f) / (time / (1000f * 60f * 60f));
		
		// Elevation gain.
		eleGain = end.getElevation() - begin.getElevation();
		
		// Begin distance.
		beginDistance = begin.getDistance();
		
		// Create the segment object.
		segment = new Segment();
		segment.setName(segmentName);
		segment.setDistance(end.getDistance() - begin.getDistance());
		segment.setElevationGain(eleGain);
		
		// Create the segment track.
		segmentTrack = new SegmentTrack();
		segmentTrack.setTime(time);
		segmentTrack.setMaxSpeed(maxSpeed);
		segmentTrack.setAvgSpeed(avgSpeed);
		
		// Create and add all segment points.
		trackPointList = DBModel.getTrackPointsFromTo(context, begin.getId(), end.getId());
		segmentPointList = new ArrayList<SegmentPoint>();
		for (TrackPoint trackPoint : trackPointList) {
			segmentPoint = new SegmentPoint();
			
			// Max speed (the speed is in m/s so we need to convert to km/h).
			if ((trackPoint.getSpeed() * 3.6f) > maxSpeed)
				maxSpeed = trackPoint.getSpeed() * 3.6f;
			
			// Set latitude and longitude.
			segmentPoint.setLat(trackPoint.getLat());
			segmentPoint.setLng(trackPoint.getLng());
			
			// Set distance to the fragment point
			segmentPoint.setDistance(trackPoint.getDistance() - beginDistance);
			
			// Set elevation.
			segmentPoint.setElevation(trackPoint.getElevation());
			
			// Add segment point into the list.
			segmentPointList.add(segmentPoint);
		}
		/*
		Log.v("Segment Name:", segment.getName());
		Log.v("Distance:", segment.getDistance() + "");
		Log.v("Time:", time + "");
		Log.v("     Begin:", begin.getTime() + "");
		Log.v("     End:", end.getTime() + "");
		Log.v("Avg. Speed:", avgSpeed + "");
		Log.v("Max. Speed:", maxSpeed + "");
		Log.v("Ele. Gain:", (end.getElevation() - begin.getElevation()) + "");//eleGain + "");
		Log.v("Avg. %:", ((100f * segment.getElevationGain()) / segment.getDistance()) + "");
		*/
		if (DBModel.newSegment(context, trackId, segment, segmentTrack, segmentPointList))
			return true;
		else
			return false;
	}
	
}

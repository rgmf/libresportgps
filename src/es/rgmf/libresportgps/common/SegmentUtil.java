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
	public static double PRECISION_POINT = 2e-08; // (lat0 - lat1) * (lat0 - lat1) + (lng0 - lng1) * (lng0 - lng1) < PRECISION_POINT
	
	/**
	 * Add a new segment.
	 * 
	 * @param context The context.
	 * @param segmentName The segment name chosen by the user.
	 * @param trackId The track's id from the segment belongs.
	 * @param begin The begin point of the segment on the track.
	 * @param end The end point of the segment on the track.
	 * @return True if ok and false in other case.
	 */
	public static boolean addSegment(Context context, String segmentName, Long trackId,
			TrackPoint begin, TrackPoint end) {
		Float distance;
		Float avgSpeed;
		Float maxSpeed = Float.MIN_VALUE;
		Long time;
		Double eleGain;
		Segment segment;
		SegmentPoint segmentPoint;
		SegmentTrack segmentTrack;
		
		// Distance in meters.
		distance = end.getDistance() - begin.getDistance();
		
		// Time in milliseconds.
		time = end.getTime() - begin.getTime();
		
		// Average speed in km/h.
		avgSpeed = (distance / 1000f) / (time / (1000f * 60f * 60f));
		
		// Elevation gain.
		eleGain = end.getElevation() - begin.getElevation();
		
		// Create the segment object.
		segment = new Segment();
		segment.setName(segmentName);
		segment.setDistance(distance);
		segment.setElevationGain(eleGain);
		
		// Create the segment point.
		segmentPoint = new SegmentPoint();
		segmentPoint.setBeginLat(begin.getLat());
		segmentPoint.setEndLat(end.getLat());
		segmentPoint.setBeginLng(begin.getLng());
		segmentPoint.setEndLng(end.getLng());
		segmentPoint.setDistance(distance);
		segmentPoint.setSegment(segment);
		
		// Create the segment track.
		segmentTrack = new SegmentTrack();
		segmentTrack.setTime(time);
		segmentTrack.setMaxSpeed(maxSpeed);
		segmentTrack.setAvgSpeed(avgSpeed);
		segmentTrack.setSegmentPoint(segmentPoint);
		
		// Create a new segment associated to the track identiy by trackId.
		if (DBModel.newSegment(context, trackId, segmentTrack)) {
			findThisSemgnetInOtherTracks(context, trackId, segmentPoint);
			return true;
		}
		else
			return false;
	}
	
	/**
	 * This method find the segment in other tracks to add them to the 
	 * database.
	 */
	public static void findThisSemgnetInOtherTracks(Context context, Long trackId, SegmentPoint segmentPoint) {
		DBModel.findAndAddSegmentTracks(context, trackId, segmentPoint, PRECISION_POINT);
	}
}

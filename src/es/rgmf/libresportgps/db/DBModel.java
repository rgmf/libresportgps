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

package es.rgmf.libresportgps.db;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import es.rgmf.libresportgps.common.Session;
import es.rgmf.libresportgps.db.orm.Segment;
import es.rgmf.libresportgps.db.orm.SegmentPoint;
import es.rgmf.libresportgps.db.orm.SegmentTrack;
import es.rgmf.libresportgps.db.orm.Sport;
import es.rgmf.libresportgps.db.orm.Track;
import es.rgmf.libresportgps.db.orm.TrackPoint;

/**
 * The database model.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class DBModel {
	/**
	 * See the method in DBAdapter to more information.
	 * 
	 * @param context
	 * @param title
	 * @return
	 */
	public static long createTrack(Context context, String title) {
		long id = -1;
		DBAdapter dbAdapter = new DBAdapter(context);
		dbAdapter.open();
		id = dbAdapter.addTrack(title, 1);
		dbAdapter.close();
		return id;
	}
	
	/**
	 * See the method in DBAdapter to more information.
	 * 
	 * @param context
	 * @param track
	 * @return
	 */
	public static long createTrack(Context context, Track track) {
		long id = -1;
		DBAdapter dbAdapter = new DBAdapter(context);
		dbAdapter.open();
		id = dbAdapter.addTrack(track);
		dbAdapter.close();
		return id;
	}

	/**
	 * See the method in DBAdapter to more information.
	 * 
	 * @param context The Context.
	 * @param trackId The identify of the track.
	 * @param status The status of the track (finished or open track).
	 * @param track The track data.
	 */
	public static void endRecordingTrack(Context context, long trackId, int status, Track track) {
		DBAdapter dbAdapter = new DBAdapter(context);
		dbAdapter.open();
		dbAdapter.updateRecordingTrack(trackId, status, track);
		dbAdapter.close();
	}

	/**
	 * Get information from Location and save it to database.
	 * 
	 * @param context
	 * @param trackId
	 * @param loc
	 */
	public static void saveLocation(Context context, long trackId, Location loc) {
		DBAdapter dbAdapter = new DBAdapter(context);
		dbAdapter.open();
		TrackPoint trackPoint = new TrackPoint();
		trackPoint.setTrack(new Track());
		trackPoint.getTrack().setId(trackId);
		trackPoint.setLat(loc.getLatitude());
		trackPoint.setLng(loc.getLongitude());
		trackPoint.setTime(loc.getTime());
		trackPoint.setDistance((float) Session.getDistance());
		/*
		if(Session.getLastLocation() != null)
			trackPoint.setDistance((float) Utilities.CalculateDistance(
					Session.getLastLocation().getLatitude(), 
					Session.getLastLocation().getLongitude(),
					loc.getLatitude(),
					loc.getLongitude()));
		 */
		trackPoint.setAccuracy(loc.getAccuracy());
		trackPoint.setElevation(loc.getAltitude());
		trackPoint.setSpeed(loc.getSpeed());
		dbAdapter.addTrackPoint(trackPoint);
		dbAdapter.close();
	}
	
	/**
	 * Save all track points.
	 * 
	 * @param context The context.
	 * @param trackId The track identify.
	 * @param trackPoints All track points.
	 */
	public static void addTrackPoints(Context context, Long trackId,
			List<TrackPoint> trackPoints) {
		DBAdapter dbAdapter = new DBAdapter(context);
		dbAdapter.open();
		TrackPoint trkPoint;
		for(int i = 0; i < trackPoints.size(); i++) {
			trkPoint = trackPoints.get(i);
			trkPoint.setTrack(new Track());
			trkPoint.getTrack().setId(trackId);
			dbAdapter.addTrackPoint(trkPoint);
		}
		dbAdapter.close();
	}

    /**
     * Return the track identify by id.
     *
     * @param context The application context.
     * @param id The id of the track it want.
     * @return The track identify by id (if exists) or null (if not exists).
     */
    public static Track getTrack(Context context, Long id) {
        Track track;
        DBAdapter dbAdapter = new DBAdapter(context);
        dbAdapter.open();
        track = dbAdapter.getTrack(id);
        dbAdapter.close();
        return track;
    }

	/**
	 * Get all tracks.
	 * 
	 * @param context
	 * @return
	 */
	public static ArrayList<Track> getTracks(Context context) {
		ArrayList<Track> tracks;
		DBAdapter dbAdapter = new DBAdapter(context);
		dbAdapter.open();
		tracks = dbAdapter.getTracks();
		dbAdapter.close();
		return tracks;
	}

    /**
     * Get all sports.
     *
     * @param context
     * @return
     */
    public static ArrayList<Sport> getSports(Context context) {
        ArrayList<Sport> sports;
        DBAdapter dbAdapter = new DBAdapter(context);
        dbAdapter.open();
        sports = dbAdapter.getSports();
        dbAdapter.close();
        return sports;
    }

    /**
     * Update the track identify by id with data inside track.
     * @param id
     * @param track
     */
    public static void updateTrack(Context context, Long id, Track track) {
        DBAdapter dbAdapter = new DBAdapter(context);
        dbAdapter.open();
        dbAdapter.updateTrack(id, track);
        dbAdapter.close();
    }

	/**
	 * See method description in DBAdapter.
	 * 
	 * @param context
	 * @param id
	 * @param name
	 */
	public static void updateTrackName(Context context, long id, String name) {
		DBAdapter dbAdapter = new DBAdapter(context);
		dbAdapter.open();
		dbAdapter.updateTrackName(id, name);
		dbAdapter.close();
	}

	/**
	 * See method description in DBAdapter.
	 * 
	 * @param context
	 * @param id
	 * @param desc
	 */
	public static void updateTrackDescription(Context context, long id, String desc) {
		DBAdapter dbAdapter = new DBAdapter(context);
		dbAdapter.open();
		dbAdapter.updateTrackDescription(id, desc);
		dbAdapter.close();
	}

	/**
	 * Delete the track.
	 * 
	 * @param context
	 * @param trackId
	 * @return true if track was deleted.
	 */
	public static boolean deleteTrack(Context context, long trackId) {
		DBAdapter dbAdapter = new DBAdapter(context);
		dbAdapter.open();
		boolean res = dbAdapter.deleteTrack(trackId);
		dbAdapter.close();
		return res;
	}

	/**
	 * Get all distance / elevation from track points of the track id.
	 * 
	 * @param context The context.
	 * @param id The track identify.
	 * @return a TreeMap
	 */
	public static TreeMap<Integer, Float> getDistEleMap(Context context,
			Long trackId) {
		DBAdapter dbAdapter = new DBAdapter(context);
		dbAdapter.open();
		TreeMap<Integer, Float> map = dbAdapter.getDistEleMap(trackId);
		dbAdapter.close();
		return map;
	}

	/**
	 * Get all track points of the track identified by trackId.
	 * 
	 * @param context The context.
	 * @param trackId The track identify.
	 * @return a list of TrackPoint.
	 */
	public static List<TrackPoint> getTrackPoints(Context context, Long trackId) {
		DBAdapter dbAdapter = new DBAdapter(context);
		dbAdapter.open();
		List<TrackPoint> list = dbAdapter.getTrackPoints(trackId);
		dbAdapter.close();
		return list;
	}
	
	/**
	 * Get all track points from begin to end.
	 * 
	 * @param context The context.
	 * @param begin The beginning track point id.
	 * @param end The end track point id.
	 * @return A track point list.
	 */
	public static List<TrackPoint> getTrackPointsFromTo(Context context, Long begin, Long end) {
		DBAdapter dbAdapter = new DBAdapter(context);
		dbAdapter.open();
		List<TrackPoint> list = dbAdapter.getTrackPointsFromTo(begin, end);
		dbAdapter.close();
		return list;
	}
	
	/**
	 * Return all segment tracks or null.
	 * 
	 * @param context The context.
	 * @param trackId The id of the track.
	 * @return The list of segment tracks or null.
	 */
	public static List<SegmentTrack> getAllSegmentTrack(Context context, Long trackId) {
		DBAdapter dbAdapter = new DBAdapter(context);
		dbAdapter.open();
		List<SegmentTrack> list = dbAdapter.getAllSegmentTrack(trackId);
		dbAdapter.close();
		return list;
	}
	
	/**
	 * Return all segment tracks or null.
	 * 
	 * @param context The context.
	 * @param trackId The id of the track.
	 * @param segmentId the id of the segment.
	 * @return The list of segment tracks or null.
	 */
	public static List<SegmentTrack> getAllSegmentTrack(Context context, Long trackId, Long segmentId) {
		DBAdapter dbAdapter = new DBAdapter(context);
		dbAdapter.open();
		List<SegmentTrack> list = dbAdapter.getAllSegmentTrack(trackId, segmentId);
		dbAdapter.close();
		return list;
	}
	
	/**
	 * Return the first segment point from all segments from the track. 
	 * 
	 * @param context The context.
	 * @param trackId The track id.
	 * @return a list or null.
	 */
	public static List<SegmentPoint> getAllSegmentPointFromTrack(Context context, Long trackId) {
		DBAdapter dbAdapter = new DBAdapter(context);
		dbAdapter.open();
		List<SegmentPoint> list = dbAdapter.getAllSegmentPointFromTrack(trackId);
		dbAdapter.close();
		return list;
	}
	
	/**
	 * Create a new segment with transaction.
	 * 
	 * @param context
	 * @param trackId
	 * @param segment
	 * @param segmentTrack
	 * @param segmentPointList
	 * @return true if ok.
	 */
	public static boolean newSegment(Context context, Long trackId, SegmentTrack segmentTrack) {
		DBAdapter dbAdapter = new DBAdapter(context);
		dbAdapter.open();
		boolean result = dbAdapter.newSegment(trackId, segmentTrack);
		dbAdapter.close();
		return result;
	}

	/**
	 * Add a new segment.
	 * 
	 * @param context Context object.
	 * @param segment Segment object.
	 * @return The identifier of the segment inserted.
	 */
	public static Long addSegment(Context context, Segment segment) {
		DBAdapter dbAdapter = new DBAdapter(context);
		dbAdapter.open();
		Long id;
		try {
			id = dbAdapter.addSegment(segment);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		dbAdapter.close();
		return id;
	}

	/**
	 * Add a segment track.
	 * 
	 * @param context Context object.
	 * @param trackId Track identifier.
	 * @param segmentId Segment identifier.
	 * @param segmentTrack Segment track object.
	 * @return The identifier of the segment track inserted.
	 */
	public static Long addSegmentTrack(Context context, Long trackId,
			Long segmentId, SegmentTrack segmentTrack) {
		DBAdapter dbAdapter = new DBAdapter(context);
		dbAdapter.open();
		Long id;
		try {
			id = dbAdapter.addSegmentTrack(trackId, segmentId, segmentTrack);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		dbAdapter.close();
		return id;
	}

	/**
	 * Add a segment point.
	 * 
	 * @param context Context object.
	 * @param segmentId Segment id of the segment points.
	 * @param segmentPoint A segment point.
	 * @return The identifier of the segment point inserted.
	 */
	public static Long addSegmentPoint(Context context, Long segmentId,
			SegmentPoint segmentPoint) {
		DBAdapter dbAdapter = new DBAdapter(context);
		dbAdapter.open();
		Long id;
		try {
			id = dbAdapter.addSegmentPoint(segmentId, segmentPoint);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		dbAdapter.close();
		return id;
	}

	/**
	 * This method finds and creates segment tracks in other 
	 * tracks than track identify by trackId.
	 * 
	 * @param trackId
	 * @param segmentPoint
	 * @param precision
	 */
	public static void findAndAddSegmentTracks(Context context, Long trackId,
			SegmentPoint segmentPoint, Double precision) {
		DBAdapter dbAdapter = new DBAdapter(context);
		dbAdapter.open();
		List<TrackPoint> trackPointList = dbAdapter.findSegmentOtherTracks(trackId, segmentPoint.getBeginLat(), segmentPoint.getBeginLng(), precision);
		for (TrackPoint tp : trackPointList) {
			Log.v("Track name:", tp.getTrack().getTitle());
		}
		dbAdapter.close();
	}
}

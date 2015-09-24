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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import es.rgmf.libresportgps.MainActivity;
import es.rgmf.libresportgps.common.Session;
import es.rgmf.libresportgps.common.Utilities;
import es.rgmf.libresportgps.data.Stats;
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
	 * Return a list of open tracks (track that are recording).
	 *
	 * @param context The context.
	 * @return
	 */
	public static List<Track> getOpenTracks(Context context) {
		List<Track> tracks;
		DBAdapter dbAdapter = new DBAdapter(context);
		dbAdapter.open();
		tracks = dbAdapter.getTracks(Track.OPEN_TRACK);
		dbAdapter.close();
		return tracks;
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
	/*
	public static List<TrackPoint> getTrackPointsFromTo(Context context, Long begin, Long end) {
		DBAdapter dbAdapter = new DBAdapter(context);
		dbAdapter.open();
		List<TrackPoint> list = dbAdapter.getTrackPointsFromTo(begin, end);
		dbAdapter.close();
		return list;
	}
	*/
	
	/**
	 * Return all segment tracks or null.
	 * 
	 * @param context The context.
	 * @param trackId The id of the track.
	 * @return The list of segment tracks or null.
	 */
	/*
	public static List<SegmentTrack> getAllSegmentTrack(Context context, Long trackId) {
		DBAdapter dbAdapter = new DBAdapter(context);
		dbAdapter.open();
		List<SegmentTrack> list = dbAdapter.getAllSegmentTrack(trackId);
		dbAdapter.close();
		return list;
	}
	*/
	
	/**
	 * Return all segment tracks or null.
	 * 
	 * @param context The context.
	 * @param trackId The id of the track.
	 * @param segmentId the id of the segment.
	 * @return The list of segment tracks or null.
	 */
	/*
	public static List<SegmentTrack> getAllSegmentTrack(Context context, Long trackId, Long segmentId) {
		DBAdapter dbAdapter = new DBAdapter(context);
		dbAdapter.open();
		List<SegmentTrack> list = dbAdapter.getAllSegmentTrack(trackId, segmentId);
		dbAdapter.close();
		return list;
	}
	*/
	
	/**
	 * Return the first segment point from all segments from the track. 
	 * 
	 * @param context The context.
	 * @param trackId The track id.
	 * @return a list or null.
	 */
	/*
	public static List<SegmentPoint> getAllSegmentPointFromTrack(Context context, Long trackId) {
		DBAdapter dbAdapter = new DBAdapter(context);
		dbAdapter.open();
		List<SegmentPoint> list = dbAdapter.getAllSegmentPointFromTrack(trackId);
		dbAdapter.close();
		return list;
	}
	*/
	
	/**
	 * Get stats by sport filtering by paramethers. if they are not null.
	 * 
	 * @param conext The context.
	 * @param year Year.
	 * @param month Month.
	 * @param sport Sport id.
	 * @return
	 */
	public static Map<Long, Stats> getStats(Context context, Integer year, Integer month, Integer sport) {
		DBAdapter dbAdapter = new DBAdapter(context);
		dbAdapter.open();
		Map<Long, Stats> map = dbAdapter.getStats(year, month, sport);
		dbAdapter.close();
		return map;
	}
	
	/**
	 * Create a new segment with transaction.
	 * 
	 * @param context
	 * @param trackId
	 * @param segmentTrack
	 * @return true if ok.
	 */
	/*
	public static boolean newSegment(Context context, Long trackId, SegmentTrack segmentTrack) {
		DBAdapter dbAdapter = new DBAdapter(context);
		dbAdapter.open();
		boolean result = dbAdapter.newSegment(trackId, segmentTrack);
		dbAdapter.close();
		return result;
	}
	*/

	/**
	 * Add a new segment.
	 * 
	 * @param context Context object.
	 * @param segment The segment object.
	 * @param spList The list of the segment points.
	 * @return The identifier of the segment inserted.
	 */
	public static Long addSegment(Context context, Segment segment, List<SegmentPoint> spList) {
		DBAdapter dbAdapter = new DBAdapter(context);
		dbAdapter.open();
		Long id = null;
		try {
			if (spList.size() > 0) {
				id = dbAdapter.addSegment(segment);
				for (SegmentPoint sp : spList)
					dbAdapter.addSegmentPoint(id, sp);
			}
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
	/*
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
	*/

	/**
	 * Find the segment in all tracks from database.
	 *
	 * 1.- Get all tracks that have initial segment point.
	 * 2.- Get all tracks that have finish segment point.
	 * 3.- Get in the middle points from tracks that have the initial and finish points.
	 * 4.- Check and find the tracks that have all segment points from init to finish.
	 *
	 * @param context The context.
	 * @param segmentId The segment identifier.
	 * @return A map with all tracks with track points that belong to the segment.
	 */
	public static Map<Long, Track> findSegmentInTracks(Context context, Long segmentId) {
		Map<Long, Track> result = new HashMap<Long, Track>();
		DBAdapter dbAdapter = new DBAdapter(context);
		dbAdapter.open();
		List<SegmentPoint> spList = dbAdapter.getSegmentPoints(segmentId);
		if (spList != null && spList.size() > 0) {
			SegmentPoint sp = spList.get(0);

			// GET INITIAL POINTS.
			// Rectangle definition.
			// From: http://janmatuschek.de/LatitudeLongitudeBoundingCoordinates
			double distance = 5.0d; // In km
			double earthRadius = 6371.0d; // in meters
			double radius = distance / earthRadius; // radius from rectangle

			double lat1 = sp.getLat() - radius;
			double lat2 = sp.getLat() + radius;

			double latT = Math.asin(Math.sin(sp.getLat()) / Math.cos(radius));
			double Alon = Math.acos((Math.cos(radius) - Math.sin(latT) * Math.sin(sp.getLat())) / (Math.cos(latT) * Math.cos(sp.getLat())));

			double lon1 = sp.getLng() - Alon;
			double lon2 = sp.getLng() + Alon;

			Map<Long, Track> begin = dbAdapter.findPointInTracks(sp.getLat(), sp.getLng(), lat1, lon1, lat2, lon2);

			// GET FINISH ONE POINTS AND ADD THEM TO THE RESULT.
			if (begin != null && begin.size() > 0) {
				sp = spList.get(spList.size() - 1);

				// Rectangle definition.
				// From: http://janmatuschek.de/LatitudeLongitudeBoundingCoordinates
				lat1 = sp.getLat() - radius;
				lat2 = sp.getLat() + radius;

				latT = Math.asin(Math.sin(sp.getLat()) / Math.cos(radius));
				Alon = Math.acos((Math.cos(radius) - Math.sin(latT) * Math.sin(sp.getLat())) / (Math.cos(latT) * Math.cos(sp.getLat())));

				lon1 = sp.getLng() - Alon;
				lon2 = sp.getLng() + Alon;

				Map<Long, Track> finish = dbAdapter.findPointInTracks(sp.getLat(), sp.getLng(), lat1, lon1, lat2, lon2);
				List<Long> idsBegin = new ArrayList<Long>(begin.keySet());
				if (finish != null && finish.size() > 0) {
					for (Long id : idsBegin) {
						if (finish.containsKey(id)) {
							begin.get(id).getTrackPointList().addAll(finish.get(id).getTrackPointList());
						}
						else {
							begin.remove(id);
						}
					}
				}

				// COMPLETE THE INTERMEDIATE TRACK POINTS.
				// LATER, CHECK IF THESE TRACK POINTS ARE THE SEGMENT AND CREATE THE RESULT TO RETURN.
				// ONLY ADD TO RESULT IF ALL SEGMENT POINTS ARE INSIDE TRACK POINTS.
				for (Track track : begin.values()) {
					if (track.getTrackPointList() != null && track.getTrackPointList().size() == 2) {
						List<TrackPoint> aux = dbAdapter.getPointsInTrackFromBeginToEnd(
								track.getId(),
								track.getTrackPointList().get(0).getId(),
								track.getTrackPointList().get(track.getTrackPointList().size() - 1).getId());
						if (aux.size() > 1) {
							int count = 0, countPointsBetween = 0;
							double distanceBetween = Double.MAX_VALUE;
							for (SegmentPoint segmentPoint : spList) {
								//Log.v("SegmentPoint", segmentPoint.getLat() + " / " + segmentPoint.getLng());
								while (distanceBetween > 20 && count < aux.size()) {
									//Log.v("Count", count + "");
									//Log.v("Lat1/Lng1 - Lat2/Lng2", segmentPoint.getLat() + "/" + segmentPoint.getLng() + " - " +  aux.get(count).getLat() + "/" + aux.get(count).getLng());
									distanceBetween = Utilities.CalculateDistance(
											segmentPoint.getLat(),
											segmentPoint.getLng(),
											aux.get(count).getLat(),
											aux.get(count).getLng()
									);
									//Log.v("DistanceBetween", distanceBetween + "");
									count++;
								}
								if (distanceBetween <= 100) {
									countPointsBetween++;
								}
							}

							Log.v("Track:", track.getTitle());
							Log.v("Puntos coincidentes", countPointsBetween + "");
							if (countPointsBetween == spList.size()) {
								Log.v("Para adentro", "Para adentro");
								track.setTrackPointList(aux);
								result.put(track.getId(), track);
							}
						}
					}

					// Add segmnt track information.
					TrackPoint firstPoint = track.getTrackPointList().get(0);
					TrackPoint lastPoint = track.getTrackPointList().get(track.getTrackPointList().size() - 1);
					if (firstPoint.getTime() < lastPoint.getTime()) {
						long timeSegmentTrack = lastPoint.getTime() - firstPoint.getTime();
						float distanceSegmentTrack = lastPoint.getDistance() - firstPoint.getDistance();
						float avgSpeedSegmentTrack = (((float) timeSegmentTrack) / (float) 1000.0) / distanceSegmentTrack;

						SegmentTrack segmentTrack = new SegmentTrack();
						segmentTrack.setTime(timeSegmentTrack);
						segmentTrack.setAvgSpeed(avgSpeedSegmentTrack);
						try {
							dbAdapter.addSegmentTrack(track.getId(), firstPoint.getId(), lastPoint.getId(), segmentId, segmentTrack);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

				/*
				for (Track t : result.values()) {
					Log.v("Track:", t.getId() + " - " + t.getTitle());
					for (TrackPoint tp : t.getTrackPointList())
						Log.v("TrackPoint:", tp.getLat() + " - " + tp.getLng());
					Log.v("---------", "-------------");
				}
				*/
			}
		}
		dbAdapter.close();

		return result;
	}

	/**
	 * This method finds and creates segment tracks in other 
	 * tracks than track identify by trackId.
	 * 
	 * @param trackId
	 * @param segmentPoint
	 * @param pointPrecision
	 * @param distancePrecision
	 */
	/*
	public static void findAndAddSegmentTracks(Context context, Long trackId,
			SegmentPoint segmentPoint, Double pointPrecision, Double distancePrecision) {
		DBAdapter dbAdapter = new DBAdapter(context);
		dbAdapter.open();
		List<TrackPoint> trackBeginPointList = dbAdapter.findSegmentOtherTracks(trackId, segmentPoint.getBeginLat(), segmentPoint.getBeginLng(), pointPrecision);
		List<TrackPoint> trackEndPointList = dbAdapter.findSegmentOtherTracks(trackId, segmentPoint.getEndLat(), segmentPoint.getEndLng(), pointPrecision);
		
		Log.v("BEGIN TRACK POINTS", "BEGIN TRACK POINTS");
		for (TrackPoint tbp : trackBeginPointList) {
			Log.v("Track title - Distance", tbp.getTrack().getTitle() + " - " + tbp.getDistance());
		}
		

		Log.v("END TRACK POINTS", "END TRACK POINTS");
		for (TrackPoint tep : trackEndPointList) {
			Log.v("Track title - Distance", tep.getTrack().getTitle() + " - " + tep.getDistance());
		}
		
		
		for (TrackPoint tbp : trackBeginPointList) {
			for (TrackPoint tep : trackEndPointList) {
				if (tbp.getTrack().getId().equals(tep.getTrack().getId()) && tbp.getDistance() < tep.getDistance() &&
						Math.abs((tep.getDistance() - tbp.getDistance()) - segmentPoint.getSegment().getDistance()) < distancePrecision) {
					Log.v("Título del track", tep.getTrack().getTitle());
					Log.v("Distancia del segmento en el track (candidato)", (tep.getDistance() - tbp.getDistance()) + "");
					Log.v("Distancia del segmento: ", segmentPoint.getSegment().getDistance() + "");
				}
			}
		}
		dbAdapter.close();
	}
	*/

	
	
	
	
	
	
	/*
	public static void reloadSegments(Context context) {
		DBAdapter dbAdapter = new DBAdapter(context);
		dbAdapter.open();
		dbAdapter.reloadSegments();
		dbAdapter.close();
	}
	*/
}

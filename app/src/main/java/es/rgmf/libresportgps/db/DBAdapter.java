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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import es.rgmf.libresportgps.common.Utilities;
import es.rgmf.libresportgps.data.Stats;
import es.rgmf.libresportgps.db.orm.Segment;
import es.rgmf.libresportgps.db.orm.SegmentPoint;
import es.rgmf.libresportgps.db.orm.SegmentTrack;
import es.rgmf.libresportgps.db.orm.Sport;
import es.rgmf.libresportgps.db.orm.Track;
import es.rgmf.libresportgps.db.orm.TrackPoint;

public class DBAdapter {
	private static DBHelper dbHelperSingleton;
	private SQLiteDatabase db;
	private Context context;
	
	/**
	 * Constructor.
	 * 
	 * @param ctx The application context.
	 */
	public DBAdapter(Context ctx) {
		context = ctx;
		dbHelperSingleton = getDbHelperSingleton(context);
	}
	
	/**
	 * Return the DBHelper.
	 * 
	 * @param context The application context.
	 * @return The DBHelper object.
	 */
	private static DBHelper getDbHelperSingleton(Context context) {
        if (dbHelperSingleton == null) {
            dbHelperSingleton = new DBHelper(context);
        }
        return dbHelperSingleton;
    }
	
	/**
	 * Open the connection if necessary.
	 * 
	 * @return the DBAdapter.
	 */
	public DBAdapter open() {
        if (db == null || !db.isOpen() || db.isReadOnly()) {
            try {
                db = dbHelperSingleton.getWritableDatabase();
            } catch (SQLException ex) {
                ex.printStackTrace();
                db = dbHelperSingleton.getReadableDatabase();
            }
        }
        return this;
    }

	/**
	 * Close the connection.
	 */
    public void close() {
        db.close();
    }

    /**
     * Create a new track with minimum information and return the id generated.
     * 
     * @param title The title of the track.
     * @param recording Set if the track is already recording.
     * @return the id of the track inserted.
     */
	public long addTrack(String title, int recording) {
		ContentValues values = new ContentValues();
    	values.put(DBHelper.TITLE_FIELD_NAME, title);
    	values.put(DBHelper.RECORDING_FIELD_NAME, recording);
    	return db.insert(DBHelper.TRACK_TBL_NAME, null, values);
	}
	
	/**
     * Create a new track and return the id generated.
     * 
     * @param track the track object.
     * @return the id of the track inserted.
     */
	public long addTrack(Track track) {
		ContentValues values = new ContentValues();
		Long sportId = null;
		
		if(track.getSport() != null) {
			sportId = addSport(track.getSport());
		}
		
    	values.put(DBHelper.TITLE_FIELD_NAME, track.getTitle());
    	values.put(DBHelper.RECORDING_FIELD_NAME, track.getRecording());
    	values.put(DBHelper.DESC_FIELD_NAME, track.getDescription());
    	values.put(DBHelper.DISTANCE_FIELD_NAME, track.getDistance());
    	values.put(DBHelper.START_TIME_FIELD_NAME, track.getStartTime());
    	values.put(DBHelper.ACTIVITY_TIME_FIELD_NAME, track.getActivityTime());
    	values.put(DBHelper.FINISH_TIME_FIELD_NAME, track.getFinishTime());
    	values.put(DBHelper.MAX_SPEED_FIELD_NAME, track.getMaxSpeed());
    	values.put(DBHelper.MAX_ELEVATION_FIELD_NAME, track.getMaxElevation());
    	values.put(DBHelper.MIN_ELEVATION_FIELD_NAME, track.getMinElevation());
    	values.put(DBHelper.ELEVATION_GAIN_FIELD_NAME, track.getElevationGain());
    	values.put(DBHelper.ELEVATION_LOSS_FIELD_NAME, track.getElevationLoss());
    	if(sportId != null)
    		values.put(DBHelper.SPORT_FIELD_NAME, sportId);
    	
    	return db.insert(DBHelper.TRACK_TBL_NAME, null, values);
	}
	
	/**
     * Create a new sport and return the id generated.
     * 
     * @param sport the sport object.
     * @return the id of the sport inserted.
     */
	public long addSport(Sport sport) {
		ContentValues values = new ContentValues();
		
    	values.put(DBHelper.NAME_FIELD_NAME, sport.getName());
    	values.put(DBHelper.DESC_FIELD_NAME, sport.getDescription());
    	values.put(DBHelper.LOGO_FIELD_NAME, sport.getLogo());
    	
    	return db.insert(DBHelper.SPORT_TBL_NAME, null, values);
	}
	
	/**
	 * Add a track point associated to a track.
	 * 
	 * @param trackPoint The TrackPoint Object Relational Mapping.
	 */
	public long addTrackPoint(TrackPoint trackPoint) {
		ContentValues values = new ContentValues();
		values.put(DBHelper.LAT_FIELD_NAME, trackPoint.getLat());
		values.put(DBHelper.LONG_FIELD_NAME, trackPoint.getLng());
		values.put(DBHelper.TIME_FIELD_NAME, trackPoint.getTime());
		values.put(DBHelper.DISTANCE_FIELD_NAME, trackPoint.getDistance());
		values.put(DBHelper.ACCURACY_FIELD_NAME, trackPoint.getAccuracy());
		values.put(DBHelper.ELEVATION_FIELD_NAME, trackPoint.getElevation());
		values.put(DBHelper.SPEED_FIELD_NAME, trackPoint.getSpeed());
		values.put(DBHelper.TRACK_ID_FIELD_NAME,  trackPoint.getTrack().getId());
		return db.insert(DBHelper.TRACK_POINT_TBL_NAME, null, values);
	}
	
	/**
	 * Create a new segment with transaction.
	 * 
	 * The requirement are:
	 * - Add a segment.
	 * - Add a segment track to know what track the segment is.
	 * - Add all segment points.
	 * 
	 * @param trackId
	 * @param segment
	 * @param segmentTrack
	 * @param segmentPointList
	 * @return True if ok or false if not ok.
	 */
	/*
	public boolean newSegment(Long trackId,	SegmentTrack segmentTrack) {
		db.beginTransactionNonExclusive();
		try {
			Long segmentId = addSegment(segmentTrack.getSegmentPoint().getSegment());
			Long segmentPointId = addSegmentPoint(segmentId, segmentTrack.getSegmentPoint());
			addSegmentTrack(trackId, segmentPointId, segmentTrack);
			db.setTransactionSuccessful();
			db.endTransaction();
			return true;
		} catch (Exception e) {
			db.endTransaction();
		}
		
		return false;
	}
	*/
	
	/**
	 * Add a new segment.
	 * 
	 * @param segment Segment object.
	 * @return The identifier of the segment inserted.
	 */
	public long addSegment(Segment segment) throws Exception {
		ContentValues values = new ContentValues();
		Long id;
		
		values.put(DBHelper.NAME_FIELD_NAME, segment.getName());
		values.put(DBHelper.DISTANCE_FIELD_NAME, segment.getDistance());
		values.put(DBHelper.ELEVATION_GAIN_FIELD_NAME, segment.getElevationGain());
		try {
			id = db.insertOrThrow(DBHelper.SEGMENT_TBL_NAME, null, values);
			return id;
		} catch(SQLException e) {
			e.printStackTrace();
			throw new Exception(e);
		}
	}

	/**
	 * Add a segment track.
	 * 
	 * @param trackId Track identifier.
	 * @param segmentId Segment identifier.
	 * @param firstPointId the first track point of the segment.
	 * @param lastPointId the last track point of the segment.
	 * @param segmentTrack Segment track object.
	 * @return The identifier of the segment track inserted.
	 * @throws Exception 
	 */
	public long addSegmentTrack(Long trackId, Long firstPointId, Long lastPointId, Long segmentId, SegmentTrack segmentTrack) throws Exception {
		ContentValues values = new ContentValues();
		Long id;
		values.put(DBHelper.TIME_FIELD_NAME, segmentTrack.getTime());
		values.put(DBHelper.AVG_SPEED_FIELD_NAME, segmentTrack.getAvgSpeed());
		values.put(DBHelper.TRACK_FIELD_NAME, trackId);
		values.put(DBHelper.TRACK_FIRST_POINT_NAME, firstPointId);
		values.put(DBHelper.TRACK_LAST_POINT_NAME, lastPointId);
		values.put(DBHelper.SEGMENT_FIELD_NAME, segmentId);
		try {
			id = db.insertOrThrow(DBHelper.SEGMENT_TRACK_TBL_NAME, null, values);
			return id;
		} catch(SQLException e) {
			e.printStackTrace();
			throw new Exception(e);
		}
	}

	/**
	 * Add a segment point.
	 * 
	 * @param segmentId Segment id of the segment points.
	 * @param segmentPoint A segment point.
	 * @return The identifier of the segment point inserted.
	 */
	public long addSegmentPoint(Long segmentId, SegmentPoint segmentPoint) throws Exception {
		ContentValues values = new ContentValues();
		Long id;
		
		values.put(DBHelper.LAT_FIELD_NAME, segmentPoint.getLat());
		values.put(DBHelper.LONG_FIELD_NAME, segmentPoint.getLng());
		values.put(DBHelper.ELEVATION_FIELD_NAME, segmentPoint.getAltitude());
		values.put(DBHelper.SEGMENT_FIELD_NAME, segmentId);
		try {
			id = db.insertOrThrow(DBHelper.SEGMENT_POINT_TBL_NAME, null, values);
			return id;
		} catch(SQLException e) {
			e.printStackTrace();
			throw new Exception(e);
		}
	}

	/**
	 * Set the recording field with the value of status.
	 * 
	 * @param trackId The identifier of the track.
	 * @param status The new status to recording field.
	 * @param track The Object Relational Mapping with data to update.
	 */
	public void updateRecordingTrack(long trackId, int status, Track track) {
		ContentValues values = new ContentValues();
		values.put(DBHelper.TITLE_FIELD_NAME, track.getTitle());
    	values.put(DBHelper.RECORDING_FIELD_NAME, status);
    	values.put(DBHelper.DESC_FIELD_NAME, track.getDescription());
    	values.put(DBHelper.DISTANCE_FIELD_NAME, track.getDistance());
    	values.put(DBHelper.START_TIME_FIELD_NAME, track.getStartTime());
    	values.put(DBHelper.ACTIVITY_TIME_FIELD_NAME, track.getActivityTime());
    	values.put(DBHelper.FINISH_TIME_FIELD_NAME, track.getFinishTime());
    	values.put(DBHelper.MAX_SPEED_FIELD_NAME, track.getMaxSpeed());
    	values.put(DBHelper.MAX_ELEVATION_FIELD_NAME, track.getMaxElevation());
    	values.put(DBHelper.MIN_ELEVATION_FIELD_NAME, track.getMinElevation());
    	values.put(DBHelper.ELEVATION_GAIN_FIELD_NAME, track.getElevationGain());
    	values.put(DBHelper.ELEVATION_LOSS_FIELD_NAME, track.getElevationLoss());
    	db.update(DBHelper.TRACK_TBL_NAME, values, 
				DBHelper.ID_FIELD_NAME + "=" + trackId, null);
	}

    /**
     * Update the track identify by trackId with datas inside newTrack object.
     * @param trackId
     * @param newTrack
     */
    public void updateTrack(Long trackId, Track newTrack) {
        ContentValues values = new ContentValues();
        if(newTrack.getTitle() != null)
            values.put(DBHelper.TITLE_FIELD_NAME, newTrack.getTitle());
        if(newTrack.getRecording() != null)
            values.put(DBHelper.RECORDING_FIELD_NAME, newTrack.getRecording());
        if(newTrack.getDescription() != null)
            values.put(DBHelper.DESC_FIELD_NAME, newTrack.getDescription());
        if(newTrack.getDistance() != null)
            values.put(DBHelper.DISTANCE_FIELD_NAME, newTrack.getDistance());
        if(newTrack.getStartTime() != null)
            values.put(DBHelper.START_TIME_FIELD_NAME, newTrack.getStartTime());
        if(newTrack.getActivityTime() != null)
            values.put(DBHelper.ACTIVITY_TIME_FIELD_NAME, newTrack.getActivityTime());
        if(newTrack.getFinishTime() != null)
            values.put(DBHelper.FINISH_TIME_FIELD_NAME, newTrack.getFinishTime());
        if(newTrack.getMaxSpeed() != null)
            values.put(DBHelper.MAX_SPEED_FIELD_NAME, newTrack.getMaxSpeed());
        if(newTrack.getMaxElevation() != null)
            values.put(DBHelper.MAX_ELEVATION_FIELD_NAME, newTrack.getMaxElevation());
        if(newTrack.getMinElevation() != null)
            values.put(DBHelper.MIN_ELEVATION_FIELD_NAME, newTrack.getMinElevation());
        if(newTrack.getElevationGain() != null)
            values.put(DBHelper.ELEVATION_GAIN_FIELD_NAME, newTrack.getElevationGain());
        if(newTrack.getElevationLoss() != null)
            values.put(DBHelper.ELEVATION_LOSS_FIELD_NAME, newTrack.getElevationLoss());
        if(newTrack.getSport() != null)
            if(newTrack.getSport().getId() != null)
                values.put(DBHelper.SPORT_FIELD_NAME, newTrack.getSport().getId());
        db.update(DBHelper.TRACK_TBL_NAME, values,
                DBHelper.ID_FIELD_NAME + "=" + trackId, null);
    }
	
	/**
	 * Update the name of the Track identify by id.
	 * 
	 * @param trackId the identifier of the Track to update.
	 * @param name the new name of the Track.
	 */
	public void updateTrackName(long trackId, String name) {
		ContentValues values = new ContentValues();
		values.put(DBHelper.TITLE_FIELD_NAME, name);
    	db.update(DBHelper.TRACK_TBL_NAME, values, 
				DBHelper.ID_FIELD_NAME + "=" + trackId, null);
	}
	
	/**
	 * Update the description of the Track identify by id.
	 * 
	 * @param trackId the identifier of the Track to update.
	 * @param desc the new description of the Track.
	 */
	public void updateTrackDescription(long trackId, String desc) {
		ContentValues values = new ContentValues();
		values.put(DBHelper.DESC_FIELD_NAME, desc);
    	db.update(DBHelper.TRACK_TBL_NAME, values,
				DBHelper.ID_FIELD_NAME + "=" + trackId, null);
	}

    /**
     * Query and return the track identify by id.
     *
     * @param id
     * @return
     */
    public Track getTrack(long id) {
        String query = "SELECT " +
                DBHelper.TRACK_TBL_NAME + "." + DBHelper.ID_FIELD_NAME + ", " +				    // 0
                DBHelper.TRACK_TBL_NAME + "." + DBHelper.TITLE_FIELD_NAME + ", " +				// 1
                DBHelper.TRACK_TBL_NAME + "." + DBHelper.RECORDING_FIELD_NAME + ", " +          // 2
                DBHelper.TRACK_TBL_NAME + "." + DBHelper.DESC_FIELD_NAME + ", " +               // 3
                DBHelper.TRACK_TBL_NAME + "." + DBHelper.DISTANCE_FIELD_NAME + ", " +           // 4
                DBHelper.TRACK_TBL_NAME + "." + DBHelper.START_TIME_FIELD_NAME + ", " +         // 5
                DBHelper.TRACK_TBL_NAME + "." + DBHelper.ACTIVITY_TIME_FIELD_NAME + ", " +      // 6
                DBHelper.TRACK_TBL_NAME + "." + DBHelper.FINISH_TIME_FIELD_NAME + ", " +        // 7
                DBHelper.TRACK_TBL_NAME + "." + DBHelper.MAX_SPEED_FIELD_NAME + ", " +          // 8
                DBHelper.TRACK_TBL_NAME + "." + DBHelper.MAX_ELEVATION_FIELD_NAME + ", " +      // 9
                DBHelper.TRACK_TBL_NAME + "." + DBHelper.MIN_ELEVATION_FIELD_NAME + ", " +      // 10
                DBHelper.TRACK_TBL_NAME + "." + DBHelper.ELEVATION_GAIN_FIELD_NAME + ", " +     // 11
                DBHelper.TRACK_TBL_NAME + "." + DBHelper.ELEVATION_LOSS_FIELD_NAME + ", " +     // 12

                DBHelper.SPORT_TBL_NAME + "." + DBHelper.ID_FIELD_NAME + ", " +                 // 13
                DBHelper.SPORT_TBL_NAME + "." + DBHelper.NAME_FIELD_NAME + ", " +               // 14
                DBHelper.SPORT_TBL_NAME + "." + DBHelper.DESC_FIELD_NAME + ", " +               // 15
                DBHelper.SPORT_TBL_NAME + "." + DBHelper.LOGO_FIELD_NAME +                      // 16

                " FROM " +
                DBHelper.TRACK_TBL_NAME + " LEFT OUTER JOIN " + DBHelper.SPORT_TBL_NAME + " ON " +
                DBHelper.TRACK_TBL_NAME + "." + DBHelper.SPORT_FIELD_NAME + "=" +
                DBHelper.SPORT_TBL_NAME + "." + DBHelper.ID_FIELD_NAME +

                " WHERE " +
                DBHelper.TRACK_TBL_NAME + "." + DBHelper.ID_FIELD_NAME + "=" + id;

        Cursor cursor = db.rawQuery(query, null);
        Track track = null;

        if(cursor.moveToFirst()) {
            Sport sport = new Sport();
            sport.setId(cursor.getLong(13));
            sport.setName(cursor.getString(14));
            sport.setDescription(cursor.getString(15));
            sport.setLogo(cursor.getString(16));

            track = new Track();
            track.setId(cursor.getLong(0));
            track.setTitle(cursor.getString(1));
            track.setRecording(cursor.getInt(2));
            track.setDescription(cursor.getString(3));
            track.setDistance(cursor.getFloat(4));
            track.setStartTime(cursor.getLong(5));
            track.setActivityTime(cursor.getLong(6));
            track.setFinishTime(cursor.getLong(7));
            track.setMaxSpeed(cursor.getFloat(8));
            track.setMaxElevation(cursor.getFloat(9));
            track.setMinElevation(cursor.getFloat(10));
            track.setElevationGain(cursor.getFloat(11));
            track.setElevationLoss(cursor.getFloat(12));
            track.setSport(sport);
        }
        cursor.close();

        return track;
    }

	/**
	 * Query and return all tracks order by finish date time (desc).
	 * 
	 * @return an ArrayList with all tracks in database.
	 */
	public ArrayList<Track> getTracks(Integer recording) {
		String query = "SELECT " +
				DBHelper.TRACK_TBL_NAME + "." + DBHelper.ID_FIELD_NAME + ", " +				    // 0
				DBHelper.TRACK_TBL_NAME + "." + DBHelper.TITLE_FIELD_NAME + ", " +				// 1
				DBHelper.TRACK_TBL_NAME + "." + DBHelper.RECORDING_FIELD_NAME + ", " +          // 2
				DBHelper.TRACK_TBL_NAME + "." + DBHelper.DESC_FIELD_NAME + ", " +               // 3
				DBHelper.TRACK_TBL_NAME + "." + DBHelper.DISTANCE_FIELD_NAME + ", " +           // 4
				DBHelper.TRACK_TBL_NAME + "." + DBHelper.START_TIME_FIELD_NAME + ", " +         // 5
				DBHelper.TRACK_TBL_NAME + "." + DBHelper.ACTIVITY_TIME_FIELD_NAME + ", " +      // 6
				DBHelper.TRACK_TBL_NAME + "." + DBHelper.FINISH_TIME_FIELD_NAME + ", " +        // 7
				DBHelper.TRACK_TBL_NAME + "." + DBHelper.MAX_SPEED_FIELD_NAME + ", " +          // 8
				DBHelper.TRACK_TBL_NAME + "." + DBHelper.MAX_ELEVATION_FIELD_NAME + ", " +      // 9
				DBHelper.TRACK_TBL_NAME + "." + DBHelper.MIN_ELEVATION_FIELD_NAME + ", " +      // 10
				DBHelper.TRACK_TBL_NAME + "." + DBHelper.ELEVATION_GAIN_FIELD_NAME + ", " +     // 11
				DBHelper.TRACK_TBL_NAME + "." + DBHelper.ELEVATION_LOSS_FIELD_NAME + ", " +     // 12
				
				DBHelper.SPORT_TBL_NAME + "." + DBHelper.ID_FIELD_NAME + ", " +                 // 13
				DBHelper.SPORT_TBL_NAME + "." + DBHelper.NAME_FIELD_NAME + ", " +               // 14
				DBHelper.SPORT_TBL_NAME + "." + DBHelper.DESC_FIELD_NAME + ", " +               // 15
				DBHelper.SPORT_TBL_NAME + "." + DBHelper.LOGO_FIELD_NAME +                      // 16
				
				" FROM " +
				DBHelper.TRACK_TBL_NAME + " LEFT OUTER JOIN " + DBHelper.SPORT_TBL_NAME + " ON " +
				DBHelper.TRACK_TBL_NAME + "." + DBHelper.SPORT_FIELD_NAME + "=" + 
				DBHelper.SPORT_TBL_NAME + "." + DBHelper.ID_FIELD_NAME;

				if (recording != null) {
					query += " WHERE " + DBHelper.TRACK_TBL_NAME + "." + DBHelper.RECORDING_FIELD_NAME + "=" +
							recording;
				}
				
				query += " ORDER BY " +
				DBHelper.TRACK_TBL_NAME + "." + DBHelper.FINISH_TIME_FIELD_NAME + " DESC";
    	
		Cursor cursor = db.rawQuery(query, null);
    	
    	ArrayList<Track> tracks = new ArrayList<Track>(cursor.getCount());
    	
    	if(cursor.moveToFirst()) {
            do {
            	Sport sport = new Sport();
            	sport.setId(cursor.getLong(13));
            	sport.setName(cursor.getString(14));
            	sport.setDescription(cursor.getString(15));
            	sport.setLogo(cursor.getString(16));
            	
            	Track track = new Track();
				track.setId(cursor.getLong(0));
				track.setTitle(cursor.getString(1));
				track.setRecording(cursor.getInt(2));
				track.setDescription(cursor.getString(3));
				track.setDistance(cursor.getFloat(4));
				track.setStartTime(cursor.getLong(5));
				track.setActivityTime(cursor.getLong(6));
				track.setFinishTime(cursor.getLong(7));
				track.setMaxSpeed(cursor.getFloat(8));
				track.setMaxElevation(cursor.getFloat(9));
				track.setMinElevation(cursor.getFloat(10));
				track.setElevationGain(cursor.getFloat(11));
				track.setElevationLoss(cursor.getFloat(12));
				track.setSport(sport);
				
                tracks.add(track);
            } while (cursor.moveToNext());
        }
    	cursor.close();
    	
    	return tracks;
	}

	public ArrayList<Track> getTracks() {
		return getTracks(null);
	}

    /**
     * @return All sports.
     */
    public ArrayList<Sport> getSports() {
        String query = "SELECT " +
                DBHelper.SPORT_TBL_NAME + "." + DBHelper.ID_FIELD_NAME + ", " +                 // 0
                DBHelper.SPORT_TBL_NAME + "." + DBHelper.NAME_FIELD_NAME + ", " +               // 1
                DBHelper.SPORT_TBL_NAME + "." + DBHelper.DESC_FIELD_NAME + ", " +               // 2
                DBHelper.SPORT_TBL_NAME + "." + DBHelper.LOGO_FIELD_NAME +                      // 3

                " FROM " +
                DBHelper.SPORT_TBL_NAME;

        Cursor cursor = db.rawQuery(query, null);

        ArrayList<Sport> sports = new ArrayList<Sport>(cursor.getCount());

        if(cursor.moveToFirst()) {
            do {
                Sport sport = new Sport();
                sport.setId(cursor.getLong(0));
                sport.setName(cursor.getString(1));
                sport.setDescription(cursor.getString(2));
                sport.setLogo(cursor.getString(3));

                sports.add(sport);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return sports;
    }
    
    /**
     * Get all distance / elevation from track points of the track id.
     * 
     * @param trackId The track identify.
     * @return The TreeMap with all distance / elevation points.
     */
    public TreeMap<Integer, Float> getDistEleMap(Long trackId) {
    	TreeMap<Integer, Float> treeMap = new TreeMap<Integer, Float>();
    	String query = "SELECT " +
    	
                DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.DISTANCE_FIELD_NAME + ", " +  // 0
                DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.ELEVATION_FIELD_NAME +        // 1

                " FROM " +
                DBHelper.TRACK_POINT_TBL_NAME +
                
                " WHERE " +
                DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.TRACK_ID_FIELD_NAME + "=" + trackId +
                
                " ORDER BY " +
                DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.DISTANCE_FIELD_NAME;

        Cursor cursor = db.rawQuery(query, null);
        
        if(cursor.moveToFirst()) {
            do {
            	treeMap.put((int) cursor.getFloat(0), cursor.getFloat(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
    	
		return treeMap;
	}
    
    /**
     * Get all track points from track identified by trackId.
     * 
     * @param trackId The track identify.
     * @return The track points list.
     */
    public List<TrackPoint> getTrackPoints(Long trackId) {
    	List<TrackPoint> list = new ArrayList<TrackPoint>();
    	TrackPoint tp;
    	String query = "SELECT " +

                DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.ID_FIELD_NAME + ", " +    		// 0
                DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.LAT_FIELD_NAME + ", " +    		// 1
                DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.LONG_FIELD_NAME + ", " +    		// 2
                DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.TIME_FIELD_NAME + ", " +    		// 3
                DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.DISTANCE_FIELD_NAME + ", " +  	// 4
                DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.ACCURACY_FIELD_NAME + ", " +    	// 5
                DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.ELEVATION_FIELD_NAME + ", " +    // 6
                DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.SPEED_FIELD_NAME +		    	// 7

                " FROM " +
                DBHelper.TRACK_POINT_TBL_NAME +
                
                " WHERE " +                
                DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.TRACK_ID_FIELD_NAME + "=" + trackId +
                
                " ORDER BY " +
                DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.DISTANCE_FIELD_NAME;

        Cursor cursor = db.rawQuery(query, null);
        
        if(cursor.moveToFirst()) {
            do {
            	tp = new TrackPoint();
            	tp.setId(cursor.getLong(0));
            	tp.setLat(cursor.getDouble(1));
            	tp.setLng(cursor.getDouble(2));
            	tp.setTime(cursor.getLong(3));
            	tp.setDistance(cursor.getFloat(4));
            	tp.setAccuracy(cursor.getFloat(5));
            	tp.setElevation(cursor.getDouble(6));
            	tp.setSpeed(cursor.getFloat(7));
            	
            	list.add(tp);
            } while (cursor.moveToNext());
        }
        cursor.close();
    	
		return list;
	}
    
    /**
     * Get all track points from begin to end.
     * 
     * @param begin The beginning track point id.
     * @param end The end track point id.
     * @return The track points list.
     */
    public List<TrackPoint> getTrackPointsFromTo(Long begin, Long end) {
    	List<TrackPoint> list = new ArrayList<TrackPoint>();
    	TrackPoint tp;
    	String query = "SELECT " +

                DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.ID_FIELD_NAME + ", " +    		// 0
                DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.LAT_FIELD_NAME + ", " +    		// 1
                DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.LONG_FIELD_NAME + ", " +    		// 2
                DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.TIME_FIELD_NAME + ", " +    		// 3
                DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.DISTANCE_FIELD_NAME + ", " +  	// 4
                DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.ACCURACY_FIELD_NAME + ", " +    	// 5
                DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.ELEVATION_FIELD_NAME + ", " +    // 6
                DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.SPEED_FIELD_NAME +		    	// 7

                " FROM " +
                DBHelper.TRACK_POINT_TBL_NAME +
                
                " WHERE " +                
                DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.ID_FIELD_NAME + ">=" + begin + " AND " +
                DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.ID_FIELD_NAME + "<=" + end +
                
                " ORDER BY " +
                DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.DISTANCE_FIELD_NAME;

        Cursor cursor = db.rawQuery(query, null);
        
        if(cursor.moveToFirst()) {
            do {
            	tp = new TrackPoint();
            	tp.setId(cursor.getLong(0));
            	tp.setLat(cursor.getDouble(1));
            	tp.setLng(cursor.getDouble(2));
            	tp.setTime(Utilities.getMillisecondsFromStringGPXDate(cursor.getString(3)));
            	tp.setDistance(cursor.getFloat(4));
            	tp.setAccuracy(cursor.getFloat(5));
            	tp.setElevation(cursor.getDouble(6));
            	tp.setSpeed(cursor.getFloat(7));
            	
            	list.add(tp);
            } while (cursor.moveToNext());
        }
        cursor.close();
    	
		return list;
	}
    
    /**
     * Get and return all segments from the track identify by trackId.
     * @param trackId The track id.
     * @param segmentId the segment id (this can be null. In this case will not apply this where filter).
     * @return The list of all segments from the track.
     */
	/*
    public List<SegmentTrack> getAllSegmentTrack(Long trackId, Long segmentId) {
    	List<SegmentTrack> list = new ArrayList<SegmentTrack>();
    	SegmentTrack st;
    	SegmentPoint sp;
    	Track track;
    	Sport sport;
    	Segment segment;
    	
    	String query = "SELECT " +

                DBHelper.SEGMENT_TRACK_TBL_NAME + "." + DBHelper.ID_FIELD_NAME + ", " +    			// 0
                DBHelper.SEGMENT_TRACK_TBL_NAME + "." + DBHelper.TIME_FIELD_NAME + ", " +    		// 1
                DBHelper.SEGMENT_TRACK_TBL_NAME + "." + DBHelper.MAX_SPEED_FIELD_NAME + ", " +  	// 2
                DBHelper.SEGMENT_TRACK_TBL_NAME + "." + DBHelper.AVG_SPEED_FIELD_NAME + ", " +    	// 3
                
                DBHelper.TRACK_TBL_NAME + "." + DBHelper.ID_FIELD_NAME + ", " +						// 4
                DBHelper.TRACK_TBL_NAME + "." + DBHelper.TITLE_FIELD_NAME + ", " +					// 5
                DBHelper.TRACK_TBL_NAME + "." + DBHelper.RECORDING_FIELD_NAME + ", " +				// 6
                DBHelper.TRACK_TBL_NAME + "." + DBHelper.DESC_FIELD_NAME + ", " +					// 7
                DBHelper.TRACK_TBL_NAME + "." + DBHelper.DISTANCE_FIELD_NAME + ", " +				// 8
                DBHelper.TRACK_TBL_NAME + "." + DBHelper.START_TIME_FIELD_NAME + ", " +				// 9
                DBHelper.TRACK_TBL_NAME + "." + DBHelper.ACTIVITY_TIME_FIELD_NAME + ", " +			// 10
                DBHelper.TRACK_TBL_NAME + "." + DBHelper.FINISH_TIME_FIELD_NAME + ", " +			// 11
                DBHelper.TRACK_TBL_NAME + "." + DBHelper.MAX_SPEED_FIELD_NAME + ", " +				// 12
                DBHelper.TRACK_TBL_NAME + "." + DBHelper.MAX_ELEVATION_FIELD_NAME + ", " +			// 13
                DBHelper.TRACK_TBL_NAME + "." + DBHelper.MIN_ELEVATION_FIELD_NAME + ", " +			// 14
                DBHelper.TRACK_TBL_NAME + "." + DBHelper.ELEVATION_GAIN_FIELD_NAME + ", " +			// 15
                DBHelper.TRACK_TBL_NAME + "." + DBHelper.ELEVATION_LOSS_FIELD_NAME + ", " +			// 16
                
                DBHelper.SPORT_TBL_NAME + "." + DBHelper.ID_FIELD_NAME + ", " +						// 17
                DBHelper.SPORT_TBL_NAME + "." + DBHelper.NAME_FIELD_NAME + ", " +					// 18
                DBHelper.SPORT_TBL_NAME + "." + DBHelper.DESC_FIELD_NAME + ", " +					// 19
                DBHelper.SPORT_TBL_NAME + "." + DBHelper.LOGO_FIELD_NAME + ", " +					// 20
                
                DBHelper.SEGMENT_POINT_TBL_NAME + "." + DBHelper.ID_FIELD_NAME + ", " + 			// 21
                DBHelper.SEGMENT_POINT_TBL_NAME + "." + DBHelper.BEGIN_LAT_FIELD_NAME + ", " + 		// 22
                DBHelper.SEGMENT_POINT_TBL_NAME + "." + DBHelper.END_LAT_FIELD_NAME + ", " + 		// 23
                DBHelper.SEGMENT_POINT_TBL_NAME + "." + DBHelper.BEGIN_LONG_FIELD_NAME + ", " + 	// 24
                DBHelper.SEGMENT_POINT_TBL_NAME + "." + DBHelper.END_LONG_FIELD_NAME + ", " + 		// 25
                DBHelper.SEGMENT_POINT_TBL_NAME + "." + DBHelper.DISTANCE_FIELD_NAME + ", " + 		// 26

                DBHelper.SEGMENT_TBL_NAME + "." + DBHelper.ID_FIELD_NAME + ", " +					// 27
                DBHelper.SEGMENT_TBL_NAME + "." + DBHelper.NAME_FIELD_NAME + ", " +					// 28
                DBHelper.SEGMENT_TBL_NAME + "." + DBHelper.DISTANCE_FIELD_NAME + ", " +				// 29
                DBHelper.SEGMENT_TBL_NAME + "." + DBHelper.ELEVATION_GAIN_FIELD_NAME + 				// 30
                

                " FROM " +
                DBHelper.SEGMENT_TRACK_TBL_NAME + ", " + DBHelper.TRACK_TBL_NAME + ", " +
                DBHelper.SPORT_TBL_NAME + ", " + DBHelper.SEGMENT_POINT_TBL_NAME + ", " + 
                DBHelper.SEGMENT_TBL_NAME +
                
                " WHERE " + 
                DBHelper.SEGMENT_TRACK_TBL_NAME + "." + DBHelper.TRACK_FIELD_NAME + "=" +
                DBHelper.TRACK_TBL_NAME + "." + DBHelper.ID_FIELD_NAME + " AND " +
                
                DBHelper.TRACK_TBL_NAME + "." + DBHelper.SPORT_FIELD_NAME + "=" +
                DBHelper.SPORT_TBL_NAME + "." + DBHelper.ID_FIELD_NAME + " AND " +

                DBHelper.SEGMENT_TRACK_TBL_NAME + "." + DBHelper.SEGMENT_POINT_FIELD_NAME + "=" +
                DBHelper.SEGMENT_POINT_TBL_NAME + "." + DBHelper.ID_FIELD_NAME + " AND " +

                DBHelper.SEGMENT_POINT_TBL_NAME + "." + DBHelper.SEGMENT_FIELD_NAME + "=" +
                DBHelper.SEGMENT_TBL_NAME + "." + DBHelper.ID_FIELD_NAME + " AND " +
                
                DBHelper.SEGMENT_TRACK_TBL_NAME + "." + DBHelper.TRACK_FIELD_NAME + "=" + trackId;
    	
    	if (segmentId != null)
    		query += " AND " + DBHelper.SEGMENT_POINT_TBL_NAME + "." + DBHelper.SEGMENT_FIELD_NAME + "=" + segmentId;
                
        query += " ORDER BY " +
                 DBHelper.SEGMENT_TRACK_TBL_NAME + "." + DBHelper.ID_FIELD_NAME;

        Cursor cursor = db.rawQuery(query, null);
        
        if(cursor.moveToFirst()) {
            do {
            	sport = new Sport();
                sport.setId(cursor.getLong(17));
                sport.setName(cursor.getString(18));
                sport.setDescription(cursor.getString(19));
                sport.setLogo(cursor.getString(20));

                track = new Track();
                track.setId(cursor.getLong(4));
                track.setTitle(cursor.getString(5));
                track.setRecording(cursor.getInt(6));
                track.setDescription(cursor.getString(7));
                track.setDistance(cursor.getFloat(8));
                track.setStartTime(cursor.getLong(9));
                track.setActivityTime(cursor.getLong(10));
                track.setFinishTime(cursor.getLong(11));
                track.setMaxSpeed(cursor.getFloat(12));
                track.setMaxElevation(cursor.getFloat(13));
                track.setMinElevation(cursor.getFloat(14));
                track.setElevationGain(cursor.getFloat(15));
                track.setElevationLoss(cursor.getFloat(16));
                track.setSport(sport);
            	
                segment = new Segment();
                segment.setId(cursor.getLong(27));
                segment.setName(cursor.getString(28));
                segment.setDistance(cursor.getFloat(29));
                segment.setElevationGain(cursor.getDouble(30));
                
                sp = new SegmentPoint();
                sp.setId(cursor.getLong(21));
                sp.setBeginLat(cursor.getDouble(22));
                sp.setEndLat(cursor.getDouble(23));
                sp.setBeginLng(cursor.getDouble(24));
                sp.setEndLng(cursor.getDouble(25));
                sp.setDistance(cursor.getFloat(26));
                sp.setSegment(segment);
                
            	st = new SegmentTrack();
            	st.setId(cursor.getLong(0));
            	st.setTime(cursor.getLong(1));
            	st.setMaxSpeed(cursor.getFloat(2));
            	st.setAvgSpeed(cursor.getFloat(3));
            	st.setTrack(track);
            	st.setSegmentPoint(sp);
            	
            	list.add(st);
            } while (cursor.moveToNext());
        }
        cursor.close();
    	
		return list;
	}
	*/
    
    /**
     * Get and return all segments from the track identify by trackId.
     * @param trackId The track id.
     * @param segmentId the segment id.
     * @return The list of all segments from the track.
     */
	/*
    public List<SegmentTrack> getAllSegmentTrack(Long trackId) {
    	return getAllSegmentTrack(trackId, null);
    }
    */
    
    /**
     * Get and return all segment points from the segment identifier.
     * 
     * @param segmentId The segment identifier.
     * @return The list of segment point.
     */
    public List<SegmentPoint> getSegmentPoints(Long segmentId) {
    	List<SegmentPoint> list = new ArrayList<SegmentPoint>();
    	SegmentPoint sp;
    	Segment segment;
    	
    	String query = "SELECT " +

                DBHelper.SEGMENT_POINT_TBL_NAME + "." + DBHelper.ID_FIELD_NAME + ", " +    			// 0
                DBHelper.SEGMENT_POINT_TBL_NAME + "." + DBHelper.LAT_FIELD_NAME + ", " +    		// 1
                DBHelper.SEGMENT_POINT_TBL_NAME + "." + DBHelper.LONG_FIELD_NAME + ", " +    		// 2
                DBHelper.SEGMENT_POINT_TBL_NAME + "." + DBHelper.ELEVATION_FIELD_NAME +		    	// 3

                " FROM " +
                DBHelper.SEGMENT_POINT_TBL_NAME + ", " +
                DBHelper.SEGMENT_TBL_NAME +
                
                " WHERE " +                
                DBHelper.SEGMENT_POINT_TBL_NAME + "." + DBHelper.SEGMENT_FIELD_NAME + "=" +
                DBHelper.SEGMENT_TBL_NAME + "." + DBHelper.ID_FIELD_NAME + " AND " +
                
                DBHelper.SEGMENT_TBL_NAME + "." + DBHelper.ID_FIELD_NAME + "=" + segmentId +

				" ORDER BY " +
				DBHelper.SEGMENT_POINT_FIELD_NAME + "." + DBHelper.ID_FIELD_NAME;
    	
        Cursor cursor = db.rawQuery(query, null);
        
        if(cursor.moveToFirst()) {
            do {
            	sp = new SegmentPoint();
            	sp.setId(cursor.getLong(0));
            	sp.setLat(cursor.getDouble(1));
            	sp.setLng(cursor.getDouble(2));
            	sp.setAltitude(cursor.getDouble(3));
            	
            	list.add(sp);
            } while (cursor.moveToNext());
        }
        cursor.close();
    	
		return list;
	}
    
    /**
	 * Get stats by sport filtering by paramethers. if they are not null.
	 *
	 * @param year Year.
	 * @param month Month.
	 * @param sport Sport id.
	 * @return
	 */
	public Map<Long, Stats> getStats(Integer year, Integer month, Integer sportId) {
		Map<Long, Stats> map = new LinkedHashMap<Long, Stats>();
    	Stats stats;
    	Sport sport;
    	
    	String query = "SELECT " +
    			"COUNT(" + DBHelper.TRACK_TBL_NAME + "." + DBHelper.ID_FIELD_NAME + ") AS workouts, " +						// 0
    			"SUM(" + DBHelper.TRACK_TBL_NAME + "." + DBHelper.DISTANCE_FIELD_NAME + ") AS distance, " +					// 1
    			"MAX(" + DBHelper.TRACK_TBL_NAME + "." + DBHelper.DISTANCE_FIELD_NAME + ") AS max_distance, " +				// 2
    			"SUM(" + DBHelper.TRACK_TBL_NAME + "." + DBHelper.ELEVATION_GAIN_FIELD_NAME + ") AS elevation, " +			// 3
    			"MAX(" + DBHelper.TRACK_TBL_NAME + "." + DBHelper.ELEVATION_GAIN_FIELD_NAME + ") AS max_elevation, " +		// 4
    			"SUM(" + DBHelper.TRACK_TBL_NAME + "." + DBHelper.ACTIVITY_TIME_FIELD_NAME + ") AS time, " +				// 5
    			"MAX(" + DBHelper.TRACK_TBL_NAME + "." + DBHelper.ACTIVITY_TIME_FIELD_NAME + ") AS max_time, " +			// 6
    			DBHelper.SPORT_TBL_NAME + "." + DBHelper.ID_FIELD_NAME + ", " +												// 7
    			DBHelper.SPORT_TBL_NAME + "." + DBHelper.NAME_FIELD_NAME + ", " +											// 8
    			DBHelper.SPORT_TBL_NAME + "." + DBHelper.DESC_FIELD_NAME + ", " +											// 9
    			DBHelper.SPORT_TBL_NAME + "." + DBHelper.LOGO_FIELD_NAME +													// 10
    			
    			" FROM " + DBHelper.TRACK_TBL_NAME + ", " + DBHelper.SPORT_TBL_NAME +
    			
    			" WHERE " +
    			DBHelper.TRACK_TBL_NAME + "." + DBHelper.SPORT_FIELD_NAME + "=" +
    			DBHelper.SPORT_TBL_NAME + "." + DBHelper.ID_FIELD_NAME;
    	
    	if (year != null) {
    		query += " AND STRFTIME('%Y', " + DBHelper.TRACK_TBL_NAME + "." + DBHelper.START_TIME_FIELD_NAME + " / 1000, 'unixepoch') = '" + year + "'";
    	}
    	
    	if (month != null) {
    		// I need to subtract 1 because Calendar object begin months by 0 (January).
    		query += " AND STRFTIME('%m', " + DBHelper.TRACK_TBL_NAME + "." + DBHelper.START_TIME_FIELD_NAME + " / 1000, 'unixepoch') = '" + String.format("%02d", month) + "'";
    	}
    	
    	if (sportId != null) {
    		query += " AND " + DBHelper.SPORT_TBL_NAME + "." + DBHelper.ID_FIELD_NAME + "=" + sportId;
    	}
    	
    	query += " GROUP BY " + DBHelper.TRACK_TBL_NAME + "." + DBHelper.SPORT_FIELD_NAME +
    			" ORDER BY workouts DESC";
    	
        Cursor cursor = db.rawQuery(query, null);
        
        if(cursor.moveToFirst()) {
            do {
            	sport = new Sport();
            	sport.setId(cursor.getLong(7));
            	sport.setName(cursor.getString(8));
            	sport.setDescription(cursor.getString(9));
            	sport.setLogo(cursor.getString(10));
            	
            	stats = new Stats();
            	stats.setWorkouts(cursor.getInt(0));
            	stats.setDistance(cursor.getInt(1));
            	stats.setMaxDistance(cursor.getInt(2));
            	stats.setGain(cursor.getInt(3));
            	stats.setMaxGain(cursor.getInt(4));
            	stats.setTime(cursor.getLong(5));
            	stats.setMaxTime(cursor.getLong(6));
            	stats.setSport(sport);
            	
            	map.put(sport.getId(), stats);
            } while (cursor.moveToNext());
        }
        cursor.close();
    	
		return map;
	}

	/**
	 * Delete the track identifier by trackId.
	 * 
	 * @param trackId The identifier of the track to delete.
	 * @return true if track could be deleted.
	 */
	public boolean deleteTrack(long trackId) {
		return db.delete(DBHelper.TRACK_TBL_NAME, 
				DBHelper.ID_FIELD_NAME + "=" + trackId,
				null) > 0;
	}

	/**
	 * Find geopoint in all tracks with a precision.
	 *
	 * @param lat The latitude found.
	 * @param lon The longitude found.
	 * @param lat1 The initial latitude of the square.
	 * @param lon1 The initial longitude of the square.
	 * @param lat2 The finish latitude of the square.
	 * @param lon2 The finish longitude of the square.
	 * @return A list of track with all points from tracks whose lat and lng are inside the rectangle
	 * defined by lat1, lon1, lat2 and lon2.
	 */
	public Map<Long, Track> findPointInTracks(Double lat, Double lon, Double lat1, Double lon1, Double lat2, Double lon2) {
		Map<Long, Track> list = new HashMap<Long, Track>();
		Track track = null;
		TrackPoint tp;
		Sport sport;
		
		String query = "SELECT " +
				DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.ID_FIELD_NAME + ", " +			// 0
				DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.LAT_FIELD_NAME + ", " + 			// 1
				DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.LONG_FIELD_NAME + ", " + 		// 2
				DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.TIME_FIELD_NAME + ", " + 		// 3
				DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.DISTANCE_FIELD_NAME + ", " + 	// 4
				DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.ACCURACY_FIELD_NAME + ", " + 	// 5
				DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.ELEVATION_FIELD_NAME + ", " + 	// 6
				DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.SPEED_FIELD_NAME + ", " + 		// 7

				DBHelper.TRACK_TBL_NAME + "." + DBHelper.ID_FIELD_NAME + ", " + 				// 8
				DBHelper.TRACK_TBL_NAME + "." + DBHelper.TITLE_FIELD_NAME + ", " + 				// 9
				DBHelper.TRACK_TBL_NAME + "." + DBHelper.RECORDING_FIELD_NAME + ", " + 			// 10
				DBHelper.TRACK_TBL_NAME + "." + DBHelper.DESC_FIELD_NAME + ", " + 				// 11
				DBHelper.TRACK_TBL_NAME + "." + DBHelper.DISTANCE_FIELD_NAME + ", " + 			// 12
				DBHelper.TRACK_TBL_NAME + "." + DBHelper.START_TIME_FIELD_NAME + ", " + 		// 13
				DBHelper.TRACK_TBL_NAME + "." + DBHelper.ACTIVITY_TIME_FIELD_NAME + ", " + 		// 14
				DBHelper.TRACK_TBL_NAME + "." + DBHelper.FINISH_TIME_FIELD_NAME + ", " + 		// 15
				DBHelper.TRACK_TBL_NAME + "." + DBHelper.MAX_SPEED_FIELD_NAME + ", " + 			// 16
				DBHelper.TRACK_TBL_NAME + "." + DBHelper.MAX_ELEVATION_FIELD_NAME + ", " + 		// 17
				DBHelper.TRACK_TBL_NAME + "." + DBHelper.MIN_ELEVATION_FIELD_NAME + ", " + 		// 18
				DBHelper.TRACK_TBL_NAME + "." + DBHelper.ELEVATION_GAIN_FIELD_NAME + ", " + 	// 19
				DBHelper.TRACK_TBL_NAME + "." + DBHelper.ELEVATION_LOSS_FIELD_NAME + ", " + 	// 20
                
                DBHelper.SPORT_TBL_NAME + "." + DBHelper.ID_FIELD_NAME + ", " +					// 21
                DBHelper.SPORT_TBL_NAME + "." + DBHelper.NAME_FIELD_NAME + ", " +				// 22
                DBHelper.SPORT_TBL_NAME + "." + DBHelper.DESC_FIELD_NAME + ", " +				// 23
                DBHelper.SPORT_TBL_NAME + "." + DBHelper.LOGO_FIELD_NAME + ", " +				// 24

				"MIN(((" + DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.LAT_FIELD_NAME + " - " + lat + ") * (" + DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.LAT_FIELD_NAME + " - " + lat + ")) + ((" + DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.LONG_FIELD_NAME + " - " + lon + ") * (" + DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.LONG_FIELD_NAME + " - " + lon + "))) " +

                
                " FROM " +
                	DBHelper.TRACK_TBL_NAME + ", " + DBHelper.SPORT_TBL_NAME + ", " + DBHelper.TRACK_POINT_TBL_NAME + 
                
                " WHERE " + 
                	DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.TRACK_ID_FIELD_NAME + "=" + 
                	DBHelper.TRACK_TBL_NAME + "." + DBHelper.ID_FIELD_NAME + " AND " +
                	
                	DBHelper.TRACK_TBL_NAME + "." + DBHelper.SPORT_FIELD_NAME + "=" +
                	DBHelper.SPORT_TBL_NAME + "." + DBHelper.ID_FIELD_NAME + " AND " +

					DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.LAT_FIELD_NAME + " BETWEEN " + lat1 + " AND " + lat2 + " AND " +
					DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.LONG_FIELD_NAME + " BETWEEN " + lon1 + " AND " + lon2 +

				" GROUP BY " + DBHelper.TRACK_TBL_NAME + "." + DBHelper.ID_FIELD_NAME;

		Cursor cursor = db.rawQuery(query, null);
        
        if(cursor.moveToFirst()) {
            do {
				tp = new TrackPoint();
				tp.setId(cursor.getLong(0));
				tp.setLat(cursor.getDouble(1));
				tp.setLng(cursor.getDouble(2));
				tp.setTime(cursor.getLong(3));
				tp.setDistance(cursor.getFloat(4));
				tp.setAccuracy(cursor.getFloat(5));
				tp.setElevation(cursor.getDouble(6));
				tp.setSpeed(cursor.getFloat(7));

				if (!list.containsKey(cursor.getLong(8))) {
					sport = new Sport();
					sport.setId(cursor.getLong(21));
					sport.setName(cursor.getString(22));
					sport.setDescription(cursor.getString(23));
					sport.setLogo(cursor.getString(24));

					track = new Track();
					track.setId(cursor.getLong(8));
					track.setTitle(cursor.getString(9));
					track.setRecording(cursor.getInt(10));
					track.setDescription(cursor.getString(11));
					track.setDistance(cursor.getFloat(12));
					track.setStartTime(cursor.getLong(13));
					track.setActivityTime(cursor.getLong(14));
					track.setFinishTime(cursor.getLong(15));
					track.setMaxSpeed(cursor.getFloat(16));
					track.setMaxElevation(cursor.getFloat(17));
					track.setMinElevation(cursor.getFloat(18));
					track.setElevationGain(cursor.getFloat(19));
					track.setElevationLoss(cursor.getFloat(20));
					track.setSport(sport);
					track.getTrackPointList().add(tp);

					list.put(cursor.getLong(8), track);
				}
				else {
					track.getTrackPointList().add(tp);
				}
            } while (cursor.moveToNext());
        }
        cursor.close();
		
		return list;
	}

	/**
	 * Find geopoint in all tracks with a precision.
	 *
	 * @param trackId The track identifier.
	 * @param beginId The begin track point identifier.
	 * @param endId The end track ponit identifier.
	 * @return A list of track with all points from beginId to endId.
	 */
	public List<TrackPoint> getPointsInTrackFromBeginToEnd(Long trackId, Long beginId, Long endId) {
		List<TrackPoint> list = new ArrayList<TrackPoint>();
		TrackPoint tp;

		String query = "SELECT " +
				DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.ID_FIELD_NAME + ", " +			// 0
				DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.LAT_FIELD_NAME + ", " + 			// 1
				DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.LONG_FIELD_NAME + ", " + 		// 2
				DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.TIME_FIELD_NAME + ", " + 		// 3
				DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.DISTANCE_FIELD_NAME + ", " + 	// 4
				DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.ACCURACY_FIELD_NAME + ", " + 	// 5
				DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.ELEVATION_FIELD_NAME + ", " + 	// 6
				DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.SPEED_FIELD_NAME +		 		// 7


				" FROM " +
				DBHelper.TRACK_TBL_NAME + ", " + DBHelper.SPORT_TBL_NAME + ", " + DBHelper.TRACK_POINT_TBL_NAME +

				" WHERE " +
				DBHelper.TRACK_TBL_NAME + "." + DBHelper.ID_FIELD_NAME + "=" + trackId + " AND " +

				DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.TRACK_ID_FIELD_NAME + "=" +
				DBHelper.TRACK_TBL_NAME + "." + DBHelper.ID_FIELD_NAME + " AND " +

				DBHelper.TRACK_TBL_NAME + "." + DBHelper.SPORT_FIELD_NAME + "=" +
				DBHelper.SPORT_TBL_NAME + "." + DBHelper.ID_FIELD_NAME + " AND " +

				DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.ID_FIELD_NAME + ">=" + beginId + " AND " +
				DBHelper.TRACK_POINT_TBL_NAME + "." + DBHelper.ID_FIELD_NAME + "<=" + endId;

		Cursor cursor = db.rawQuery(query, null);

		if(cursor.moveToFirst()) {
			do {
				tp = new TrackPoint();
				tp.setId(cursor.getLong(0));
				tp.setLat(cursor.getDouble(1));
				tp.setLng(cursor.getDouble(2));
				tp.setTime(cursor.getLong(3));
				tp.setDistance(cursor.getFloat(4));
				tp.setAccuracy(cursor.getFloat(5));
				tp.setElevation(cursor.getDouble(6));
				tp.setSpeed(cursor.getFloat(7));

				list.add(tp);

			} while (cursor.moveToNext());
		}
		cursor.close();

		return list;
	}

	
	
	
	
	/*
	public void reloadSegments() {
        db.execSQL("DROP TABLE " + DBHelper.SEGMENT_TRACK_TBL_NAME);
    	db.execSQL("DROP TABLE " + DBHelper.SEGMENT_POINT_TBL_NAME);
    	db.execSQL("DROP TABLE " + DBHelper.SEGMENT_TBL_NAME);
    	
    	db.execSQL("create table if not exists " + DBHelper.SEGMENT_TBL_NAME + "( " +
    			DBHelper.ID_FIELD_NAME + " integer primary key autoincrement, " +
    			DBHelper.NAME_FIELD_NAME + " text not null, " +
    			DBHelper.DISTANCE_FIELD_NAME + " real not null, " +
    			DBHelper.ELEVATION_GAIN_FIELD_NAME + " real);");
        
        db.execSQL("create table if not exists " + DBHelper.SEGMENT_POINT_TBL_NAME + "( " +
        		DBHelper.ID_FIELD_NAME + " integer primary key autoincrement, " +
        		DBHelper.BEGIN_LAT_FIELD_NAME + " integer not null, " +
        		DBHelper.BEGIN_LONG_FIELD_NAME + " integer not null, " +
        		DBHelper.END_LAT_FIELD_NAME + " integer not null, " +
        		DBHelper.END_LONG_FIELD_NAME + " integer not null, " +
        		DBHelper.DISTANCE_FIELD_NAME + " real not null, " +
        		DBHelper.SEGMENT_FIELD_NAME + " integer not null references " + DBHelper.SEGMENT_TBL_NAME + " (" + DBHelper.ID_FIELD_NAME + ") on delete cascade on update cascade);");
        
        db.execSQL("create table if not exists " + DBHelper.SEGMENT_TRACK_TBL_NAME + "( " +
        		DBHelper.ID_FIELD_NAME + " integer primary key autoincrement, " +
        		DBHelper.TIME_FIELD_NAME + " integer not null, " +
        		DBHelper.MAX_SPEED_FIELD_NAME + " real, " +
        		DBHelper.AVG_SPEED_FIELD_NAME + " real, " +
        		DBHelper.TRACK_FIELD_NAME + " integer not null references " + DBHelper.TRACK_TBL_NAME + " (" + DBHelper.ID_FIELD_NAME + ") on delete cascade on update cascade, " +
        		DBHelper.SEGMENT_POINT_FIELD_NAME + " integer not null references " + DBHelper.SEGMENT_POINT_TBL_NAME + " (" + DBHelper.ID_FIELD_NAME + ") on delete cascade on update cascade);");
	}
	*/
}

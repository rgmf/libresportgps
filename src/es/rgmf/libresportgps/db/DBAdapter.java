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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
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
     * Create a new track and return the id generated.
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
	 * Query and return all tracks.
	 * 
	 * @return an ArrayList with all tracks in database.
	 */
	public ArrayList<Track> getTracks() {		
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
}

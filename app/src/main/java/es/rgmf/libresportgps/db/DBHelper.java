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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;

import es.rgmf.libresportgps.common.Session;

/**
 * Database helper.
 * 
 * This class is package-protected (by default) and is only visible by {@file DBAdapter.java}.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 *
 */
class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 17;
    private static final String DATABASE_NAME = "libresportgps.db";
    
    /*************************** Table names *********************************/
    public static final String EQUIPMENT_TBL_NAME = "equipment";
    public static final String SPORT_EQUIPMENT_TBL_NAME = "sport_equipment";
    public static final String SPORT_TBL_NAME = "sport";
    public static final String TRACK_TBL_NAME = "track";
    public static final String TRACK_POINT_TBL_NAME = "track_point";
    public static final String WAYPOINT_TBL_NAME = "waypoint";
    public static final String SEGMENT_TBL_NAME = "segment";
    public static final String SEGMENT_POINT_TBL_NAME = "segment_point";
    public static final String SEGMENT_TRACK_TBL_NAME = "segment_track";
    /*************************************************************************/
    
    /*************************** Fields names ********************************/
    public static final String ID_FIELD_NAME = "id";
    public static final String LAT_FIELD_NAME = "lat";
    public static final String LONG_FIELD_NAME = "long";
    public static final String TIME_FIELD_NAME = "time";
    public static final String DISTANCE_FIELD_NAME = "distance";
    public static final String ACCURACY_FIELD_NAME = "accuracy";
    public static final String ELEVATION_FIELD_NAME = "elevation";
    public static final String SPEED_FIELD_NAME = "speed";
    public static final String TRACK_ID_FIELD_NAME = "track_id";
    public static final String TITLE_FIELD_NAME = "title";
    public static final String RECORDING_FIELD_NAME = "recording";
    public static final String DESC_FIELD_NAME = "description";
    public static final String START_TIME_FIELD_NAME = "start_time";
    public static final String ACTIVITY_TIME_FIELD_NAME = "activity_time";
    public static final String FINISH_TIME_FIELD_NAME = "finish_time";
    public static final String MAX_SPEED_FIELD_NAME = "max_speed";
    public static final String MAX_ELEVATION_FIELD_NAME = "max_elevation";
    public static final String MIN_ELEVATION_FIELD_NAME = "min_elevation";
    public static final String ELEVATION_GAIN_FIELD_NAME = "elevation_gain";
    public static final String ELEVATION_LOSS_FIELD_NAME = "elevation_loss";
    public static final String NAME_FIELD_NAME = "name";
    public static final String EQUIPMENT_FIELD_NAME = "equipment";
    public static final String IMAGE_FIELD_NAME = "image";
    public static final String SPORT_FIELD_NAME = "sport";
    public static final String LOGO_FIELD_NAME = "logo";
    public static final String POINT_ORDER_FIELD_NAME = "point_order";
    public static final String SEGMENT_FIELD_NAME = "segment";
    public static final String AVG_SPEED_FIELD_NAME = "avg_speed";
    public static final String TRACK_FIELD_NAME = "track";
    public static final String BEGIN_LAT_FIELD_NAME = "begin_lat";
    public static final String END_LAT_FIELD_NAME = "end_lat";
    public static final String BEGIN_LONG_FIELD_NAME = "begin_long";
    public static final String END_LONG_FIELD_NAME = "end_long";
    public static final String SEGMENT_POINT_FIELD_NAME = "segment_point";
    /*************************************************************************/
    
    /************************* SQL create table if not exists ******************************/
    private static final String EQUIPMENT_TBL = "create table if not exists " + EQUIPMENT_TBL_NAME + " (" +
    		ID_FIELD_NAME + " integer primary key autoincrement, " +
    		NAME_FIELD_NAME + " text not null, " +
    		DESC_FIELD_NAME + " text, " +
    		IMAGE_FIELD_NAME + " text);";
    
    private static final String SPORT_TBL = "create table if not exists " + SPORT_TBL_NAME + " (" +
    		ID_FIELD_NAME + " integer primary key autoincrement, " +
    		NAME_FIELD_NAME + " text not null, " +
    		DESC_FIELD_NAME + " text, " +
    		LOGO_FIELD_NAME + " text not null);";
    
    private static final String SPORT_EQUIPMENT_TBL = "create table if not exists " + SPORT_EQUIPMENT_TBL_NAME + " (" +
    		ID_FIELD_NAME + " integer primary key autoincrement, " +
    		SPORT_FIELD_NAME + " text not null references " + SPORT_TBL_NAME + " (" + ID_FIELD_NAME + ") on delete cascade on update cascade, " +
    		EQUIPMENT_FIELD_NAME+ " text not null references " + EQUIPMENT_TBL_NAME + " (" + ID_FIELD_NAME + ") on delete cascade on update cascade, " +
    		"unique (" + SPORT_FIELD_NAME + ", " + EQUIPMENT_FIELD_NAME + "));";
    
    private static final String TRACK_TBL = "create table if not exists " + TRACK_TBL_NAME + " (" +
    		ID_FIELD_NAME + " integer primary key autoincrement, " +
    		TITLE_FIELD_NAME + " text not null, " +
    		RECORDING_FIELD_NAME + " integer not null, " + // 1 if the track is recording and 0 if not.
    		DESC_FIELD_NAME + " text, " +
    		DISTANCE_FIELD_NAME + " real, " +
    		START_TIME_FIELD_NAME + " integer, " +
    		ACTIVITY_TIME_FIELD_NAME + " integer, " +
    		FINISH_TIME_FIELD_NAME + " integer, " +
    		MAX_SPEED_FIELD_NAME + " real, " +
    		MAX_ELEVATION_FIELD_NAME + " real, " +
    		MIN_ELEVATION_FIELD_NAME + " real, " +
    		ELEVATION_GAIN_FIELD_NAME + " real, " +
    		ELEVATION_LOSS_FIELD_NAME + " real, " +
    		SPORT_FIELD_NAME + " integer references " + SPORT_TBL_NAME + " (" + ID_FIELD_NAME + ") on delete cascade on update cascade);";
    
    private static final String TRACK_POINT_TBL = "create table if not exists " + TRACK_POINT_TBL_NAME + " (" + 
    		ID_FIELD_NAME + " integer primary key autoincrement, " +
    		LAT_FIELD_NAME + " integer not null, " +
    		LONG_FIELD_NAME + " integer not null, " +
    		TIME_FIELD_NAME + " integer not null, " +
    		DISTANCE_FIELD_NAME + " real, " +
    		ACCURACY_FIELD_NAME + " real, " +
    		ELEVATION_FIELD_NAME + " real, " +
    		SPEED_FIELD_NAME + " real, " +
    		TRACK_ID_FIELD_NAME + " integer not null references " + TRACK_TBL_NAME + " (" + ID_FIELD_NAME + ") on delete cascade on update cascade);";
    
    private static final String WAYPOINT_TBL = "create table if not exists " + WAYPOINT_TBL_NAME + "( " +
    		ID_FIELD_NAME + " integer primary key autoincrement, " +
    		TITLE_FIELD_NAME + " text not null, " +
    		LAT_FIELD_NAME + " integer not null, " +
    		LONG_FIELD_NAME + " integer not null, " +
    		TIME_FIELD_NAME + " integer not null, " +
    		DESC_FIELD_NAME + " text, " +
    		ELEVATION_FIELD_NAME + " real, " +
    		ACCURACY_FIELD_NAME + " real, " +
    		TRACK_ID_FIELD_NAME + " integer not null references " + TRACK_TBL_NAME + " (" + ID_FIELD_NAME + ") on delete cascade on update cascade);";
    
    private static final String SEGMENT_TBL = "create table if not exists " + SEGMENT_TBL_NAME + "( " +
    		ID_FIELD_NAME + " integer primary key autoincrement, " +
    		NAME_FIELD_NAME + " text not null, " +
    		DISTANCE_FIELD_NAME + " real not null, " +
    		ELEVATION_GAIN_FIELD_NAME + " real);";
    
    private static final String SEGMENT_POINT_TBL = "create table if not exists " + SEGMENT_POINT_TBL_NAME + "( " +
    		ID_FIELD_NAME + " integer primary key autoincrement, " +
    		LAT_FIELD_NAME + " integer not null, " +
    		LONG_FIELD_NAME + " integer not null, " +
			ELEVATION_FIELD_NAME + " real not null, " +
    		SEGMENT_FIELD_NAME + " integer not null references " + SEGMENT_TBL_NAME + " (" + ID_FIELD_NAME + ") on delete cascade on update cascade);";
    
    private static final String SEGMENT_TRACK_TBL = "create table if not exists " + SEGMENT_TRACK_TBL_NAME + "( " +
    		ID_FIELD_NAME + " integer primary key autoincrement, " +
    		TIME_FIELD_NAME + " integer not null, " +
    		MAX_SPEED_FIELD_NAME + " real, " +
    		AVG_SPEED_FIELD_NAME + " real, " +
    		TRACK_FIELD_NAME + " integer not null references " + TRACK_TBL_NAME + " (" + ID_FIELD_NAME + ") on delete cascade on update cascade, " +
    		SEGMENT_FIELD_NAME + " integer not null references " + SEGMENT_TBL_NAME + " (" + ID_FIELD_NAME + ") on delete cascade on update cascade);";
    		
    /*************************************************************************/
    
    /**
     * Constructor.
     * 
     * @param context
     */
    public DBHelper(Context context) {
		super(context,
				Session.getDbFolder() + "/" +  DATABASE_NAME,
				null,
				DATABASE_VERSION);

	}

    /**
     * Method that is called the once time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
    	db.execSQL(EQUIPMENT_TBL);
		db.execSQL(SPORT_TBL);
		db.execSQL(SPORT_EQUIPMENT_TBL);
    	db.execSQL(TRACK_TBL);
    	db.execSQL(TRACK_POINT_TBL);
    	db.execSQL(WAYPOINT_TBL);
    	db.execSQL(SEGMENT_TBL);
    	
    	db.execSQL("INSERT INTO " + SPORT_TBL_NAME + " (" + NAME_FIELD_NAME + ", " + LOGO_FIELD_NAME + ") " +
		           "VALUES ('Canicross', '" + Environment.getExternalStorageDirectory() + "/libresportgps/canicross.png')");
		db.execSQL("INSERT INTO " + SPORT_TBL_NAME + " (" + NAME_FIELD_NAME + ", " + LOGO_FIELD_NAME + ") " +
		           "VALUES ('Cycling', '" + Environment.getExternalStorageDirectory() + "/libresportgps/cycling.png')");
		db.execSQL("INSERT INTO " + SPORT_TBL_NAME + " (" + NAME_FIELD_NAME + ", " + LOGO_FIELD_NAME + ") " +
		           "VALUES ('Mountain Bike', '" + Environment.getExternalStorageDirectory() + "/libresportgps/mountain_bike.png')");
		db.execSQL("INSERT INTO " + SPORT_TBL_NAME + " (" + NAME_FIELD_NAME + ", " + LOGO_FIELD_NAME + ") " +
		           "VALUES ('Running', '" + Environment.getExternalStorageDirectory() + "/libresportgps/running.png')");
		db.execSQL("INSERT INTO " + SPORT_TBL_NAME + " (" + NAME_FIELD_NAME + ", " + LOGO_FIELD_NAME + ") " +
		           "VALUES ('Trail Running', '" + Environment.getExternalStorageDirectory() + "/libresportgps/trail_running.png')");
		db.execSQL("INSERT INTO " + SPORT_TBL_NAME + " (" + NAME_FIELD_NAME + ", " + LOGO_FIELD_NAME + ") " +
		           "VALUES ('Trekking', '" + Environment.getExternalStorageDirectory() + "/libresportgps/trekking.png')");
    }

    /**
     * To update the database between versions.
     */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		switch(newVersion) {
        case 8:
			db.execSQL(EQUIPMENT_TBL);
			db.execSQL(SPORT_TBL);
			db.execSQL(SPORT_EQUIPMENT_TBL);
        	db.execSQL(TRACK_TBL);
        	db.execSQL(TRACK_POINT_TBL);
        	db.execSQL(WAYPOINT_TBL);
        	db.execSQL(SEGMENT_TBL);
        	
        	db.execSQL("INSERT INTO " + SPORT_TBL_NAME + " (" + NAME_FIELD_NAME + ", " + LOGO_FIELD_NAME + ") " +
			           "VALUES ('Canicross', '" + Environment.getExternalStorageDirectory() + "/libresportgps/canicross.png')");
			db.execSQL("INSERT INTO " + SPORT_TBL_NAME + " (" + NAME_FIELD_NAME + ", " + LOGO_FIELD_NAME + ") " +
			           "VALUES ('Cycling', '" + Environment.getExternalStorageDirectory() + "/libresportgps/cycling.png')");
			db.execSQL("INSERT INTO " + SPORT_TBL_NAME + " (" + NAME_FIELD_NAME + ", " + LOGO_FIELD_NAME + ") " +
			           "VALUES ('Mountain Bike', '" + Environment.getExternalStorageDirectory() + "/libresportgps/mountain_bike.png')");
			db.execSQL("INSERT INTO " + SPORT_TBL_NAME + " (" + NAME_FIELD_NAME + ", " + LOGO_FIELD_NAME + ") " +
			           "VALUES ('Running', '" + Environment.getExternalStorageDirectory() + "/libresportgps/running.png')");
			db.execSQL("INSERT INTO " + SPORT_TBL_NAME + " (" + NAME_FIELD_NAME + ", " + LOGO_FIELD_NAME + ") " +
			           "VALUES ('Trail Running', '" + Environment.getExternalStorageDirectory() + "/libresportgps/trail_running.png')");
			db.execSQL("INSERT INTO " + SPORT_TBL_NAME + " (" + NAME_FIELD_NAME + ", " + LOGO_FIELD_NAME + ") " +
			           "VALUES ('Trekking', '" + Environment.getExternalStorageDirectory() + "/libresportgps/trekking.png')");
        	break;
        case 9:
        	db.execSQL("UPDATE " + SPORT_TBL_NAME + " SET " +
                       LOGO_FIELD_NAME + "=" + "'canicross' " +
        			   "WHERE " + NAME_FIELD_NAME + "='Canicross'");
        	db.execSQL("UPDATE " + SPORT_TBL_NAME + " SET " +
                       LOGO_FIELD_NAME + "=" + "'cycling' " +
     			       "WHERE " + NAME_FIELD_NAME + "='Cycling'");
        	db.execSQL("UPDATE " + SPORT_TBL_NAME + " SET " +
                       LOGO_FIELD_NAME + "=" + "'mountain_bike' " +
  			           "WHERE " + NAME_FIELD_NAME + "='Mountain Bike'");
        	db.execSQL("UPDATE " + SPORT_TBL_NAME + " SET " +
                       LOGO_FIELD_NAME + "=" + "'running' " +
  			           "WHERE " + NAME_FIELD_NAME + "='Running'");
        	db.execSQL("UPDATE " + SPORT_TBL_NAME + " SET " +
                       LOGO_FIELD_NAME + "=" + "'trail_running' " +
  			           "WHERE " + NAME_FIELD_NAME + "='Trail Running'");
        	db.execSQL("UPDATE " + SPORT_TBL_NAME + " SET " +
                       LOGO_FIELD_NAME + "=" + "'trekking' " +
  			           "WHERE " + NAME_FIELD_NAME + "='Trekking'");
        	break;
        case 10:
        	// In this database update we need to add a track point order field and update all registers.
        	db.execSQL("ALTER TABLE " + TRACK_POINT_TBL_NAME +
        			   " ADD " + POINT_ORDER_FIELD_NAME + " integer not null default 0");
        	
        	String[] trackCols = {ID_FIELD_NAME};
        	Cursor trackCursor = db.query(true, TRACK_TBL_NAME, trackCols, null, null, null, null, null, null);
        	if (trackCursor != null && trackCursor.moveToFirst()) {
        		String[] trackPointCols = {ID_FIELD_NAME};
        		Cursor trackPointCursor;
        		ContentValues values;
        		int order;
        		// To all tracks we have to update their track points.
        		do {
        			order = 1;
        			trackPointCursor = db.query(true, TRACK_POINT_TBL_NAME, trackPointCols, TRACK_ID_FIELD_NAME + "=" + trackCursor.getInt(0), null, null, null, null, null);
        			if (trackPointCursor != null && trackPointCursor.moveToFirst()) {
        				// Update all track points of this track.
        				do {
        					values = new ContentValues();
        					values.put(DBHelper.POINT_ORDER_FIELD_NAME, order);
        					db.update(DBHelper.TRACK_POINT_TBL_NAME, values, ID_FIELD_NAME + "=" + trackPointCursor.getInt(0), null);
        					order++;
        				} while (trackPointCursor.moveToNext());
        			}
        		} while (trackCursor.moveToNext());
        	}
        	break;
        case 11:
        	//db.execSQL("ALTER TABLE " + TRACK_POINT_TBL_NAME + 
        	//		" DROP COLUMN " + POINT_ORDER_FIELD_NAME );
        	db.execSQL("DROP TABLE IF EXISTS " + SEGMENT_TBL_NAME);
        	db.execSQL(SEGMENT_TBL);
        	db.execSQL(SEGMENT_POINT_TBL);
        	db.execSQL(SEGMENT_TRACK_TBL);
        	break;
        case 12:
        	db.execSQL("ALTER TABLE " + SEGMENT_TRACK_TBL_NAME +
        			" ADD COLUMN date_ date not null default 0");
        	break;
        case 13:
        	//db.execSQL("ALTER TABLE " + SEGMENT_TRACK_TBL_NAME + 
        	//		" DROP date_");
        	break;
		case 14:
        case 15:
		case 16:
		case 17:
        	db.execSQL("DROP TABLE IF EXISTS " + SEGMENT_TRACK_TBL_NAME);
        	db.execSQL("DROP TABLE IF EXISTS " + SEGMENT_POINT_TBL_NAME);
        	db.execSQL("DROP TABLE IF EXISTS " + SEGMENT_TBL_NAME);
        	
        	db.execSQL(SEGMENT_TBL);
        	db.execSQL(SEGMENT_POINT_TBL);
        	db.execSQL(SEGMENT_TRACK_TBL);
        	
        	break;
		}
	}
}

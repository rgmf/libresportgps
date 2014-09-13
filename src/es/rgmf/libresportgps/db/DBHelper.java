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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database helper.
 * 
 * This class is package-protected (by default) and is only visible by {@file DBAdapter.java}.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 *
 */
class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "libresportgps.db";
    
    /*************************** Table names *********************************/
    public static final String TRACK_TBL_NAME = "track";
    public static final String TRACK_POINT_TBL_NAME = "track_point";
    public static final String WAYPOINT_TBL_NAME = "waypoint";
    public static final String SEGMENT_TBL_NAME = "segment";
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
    /*************************************************************************/
    
    /************************* SQL create table ******************************/
    private static final String TRACK_TBL = "create table " + TRACK_TBL_NAME + " (" +
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
    		ELEVATION_LOSS_FIELD_NAME + " real);";
    
    private static final String TRACK_POINT_TBL = "create table " + TRACK_POINT_TBL_NAME + " (" + 
    		ID_FIELD_NAME + " integer primary key autoincrement, " +
    		LAT_FIELD_NAME + " integer not null, " +
    		LONG_FIELD_NAME + " integer not null, " +
    		TIME_FIELD_NAME + " integer not null, " +
    		DISTANCE_FIELD_NAME + " real, " +
    		ACCURACY_FIELD_NAME + " real, " +
    		ELEVATION_FIELD_NAME + " real, " +
    		SPEED_FIELD_NAME + " real, " +
    		TRACK_ID_FIELD_NAME + " integer not null references " + TRACK_TBL_NAME + " (" + ID_FIELD_NAME + ") on delete cascade on update cascade);";
    
    private static final String WAYPOINT_TBL = "create table " + WAYPOINT_TBL_NAME + "( " +
    		ID_FIELD_NAME + " integer primary key autoincrement, " +
    		TITLE_FIELD_NAME + " text not null, " +
    		LAT_FIELD_NAME + " integer not null, " +
    		LONG_FIELD_NAME + " integer not null, " +
    		TIME_FIELD_NAME + " integer not null, " +
    		DESC_FIELD_NAME + " text, " +
    		ELEVATION_FIELD_NAME + " real, " +
    		ACCURACY_FIELD_NAME + " real, " +
    		TRACK_ID_FIELD_NAME + " integer not null references " + TRACK_TBL_NAME + " (" + ID_FIELD_NAME + ") on delete cascade on update cascade);";
    
    private static final String SEGMENT_TBL = "create table " + SEGMENT_TBL_NAME + "( " +
    		ID_FIELD_NAME + " integer primary key autoincrement, " +
    		DISTANCE_FIELD_NAME + " real, " +
    		START_TIME_FIELD_NAME + " integer, " +
    		ACTIVITY_TIME_FIELD_NAME + " integer, " +
    		FINISH_TIME_FIELD_NAME + " integer, " +
    		MAX_SPEED_FIELD_NAME + " real, " +
    		MAX_ELEVATION_FIELD_NAME + " real, " +
    		MIN_ELEVATION_FIELD_NAME + " real, " +
    		ELEVATION_GAIN_FIELD_NAME + " real, " +
    		ELEVATION_LOSS_FIELD_NAME + " real, " +
    		TRACK_ID_FIELD_NAME + " integer not null references " + TRACK_TBL_NAME + " (" + ID_FIELD_NAME + ") on delete cascade on update cascade);";
    /*************************************************************************/
    		
    /**
     * Constructor.
     * 
     * @param context
     */
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Method that is called the once time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
    	db.execSQL(TRACK_TBL);
    	db.execSQL(TRACK_POINT_TBL);
    	db.execSQL(WAYPOINT_TBL);
    	db.execSQL(SEGMENT_TBL);
    }

    /**
     * To update the database between versions.
     */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}

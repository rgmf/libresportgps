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

package es.rgmf.libresportgps.data;

import java.io.Serializable;

import es.rgmf.libresportgps.db.orm.Sport;

/**
 * This class represent the stats.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class Stats implements Serializable {
	Sport sport;		// sport object.
	int mWorkouts; 		// total training days.
	int mDistance; 		// total distance in meters.
	int mMaxDistance;	// maximum distance in an activity.
	int mGain;			// total altitude in meters.
	int mMaxGain;		// maximum altitude in an activity.
	long mTime;			// total time.
	long mMaxTime;		// maximum time in an activity.
	
	public Sport getSport() {
		return sport;
	}
	public void setSport(Sport sport) {
		this.sport = sport;
	}
	public int getWorkouts() {
		return mWorkouts;
	}
	public void setWorkouts(int mWorkouts) {
		this.mWorkouts = mWorkouts;
	}
	public int getDistance() {
		return mDistance;
	}
	public void setDistance(int mDistance) {
		this.mDistance = mDistance;
	}
	public int getMaxDistance() {
		return mMaxDistance;
	}
	public void setMaxDistance(int mMaxDistance) {
		this.mMaxDistance = mMaxDistance;
	}
	public int getGain() {
		return mGain;
	}
	public void setGain(int mGain) {
		this.mGain = mGain;
	}
	public int getMaxGain() {
		return mMaxGain;
	}
	public void setMaxGain(int mMaxGain) {
		this.mMaxGain = mMaxGain;
	}
	public long getTime() {
		return mTime;
	}
	public void setTime(long mTime) {
		this.mTime = mTime;
	}
	public long getMaxTime() {
		return mMaxTime;
	}
	public void setMaxTime(long mMaxTime) {
		this.mMaxTime = mMaxTime;
	}
}

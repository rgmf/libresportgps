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

package es.rgmf.libresportgps.db.orm;

/**
 * Segment Object Relational Mapping.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class Segment {
	private long id;
	private float distance = 0;
	private long startTime = 0;
	private long activityTime = 0;
	private long finishTime = 0;
	private float maxSpeed = 0;
	private float maxElevation = 0;
	private float minElevation = 0;
	private float elevationGain = 0;
	private float elevationLoss = 0;
	private Track track;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public float getDistance() {
		return distance;
	}
	public void setDistance(float distance) {
		this.distance = distance;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getActivityTime() {
		return activityTime;
	}
	public void setActivityTime(long activityTime) {
		this.activityTime = activityTime;
	}
	public long getFinishTime() {
		return finishTime;
	}
	public void setFinishTime(long finishTime) {
		this.finishTime = finishTime;
	}
	public float getMaxSpeed() {
		return maxSpeed;
	}
	public void setMaxSpeed(float maxSpeed) {
		this.maxSpeed = maxSpeed;
	}
	public float getMaxElevation() {
		return maxElevation;
	}
	public void setMaxElevation(float maxElevation) {
		this.maxElevation = maxElevation;
	}
	public float getMinElevation() {
		return minElevation;
	}
	public void setMinElevation(float minElevation) {
		this.minElevation = minElevation;
	}
	public float getElevationGain() {
		return elevationGain;
	}
	public void setElevationGain(float elevationGain) {
		this.elevationGain = elevationGain;
	}
	public float getElevationLoss() {
		return elevationLoss;
	}
	public void setElevationLoss(float elevationLoss) {
		this.elevationLoss = elevationLoss;
	}
	public Track getTrack() {
		return track;
	}
	public void setTrack(Track track) {
		this.track = track;
	}
}

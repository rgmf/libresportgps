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

import java.io.Serializable;

/**
 * Track Object Relational Mapping.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class Track implements Serializable {
	public static int ENDED_TRACK = 0;
	public static int OPEN_TRACK = 1;
	
	private Long id;
	private String title;
	private Integer recording = 0;
	private String description;
	private Float distance = 0f;
	private Long startTime = 0L;
	private Long activityTime = 0L;
	private Long finishTime = 0L;
	private Float maxSpeed = 0f;
	private Float maxElevation = 0f;
	private Float minElevation = 0f;
	private Float elevationGain = 0f;
	private Float elevationLoss = 0f;
	private Sport sport = null;

    public Track() {}

    public Track(Long id, String title, String description, Integer recording, Float distance,
                 Long startTime, Long activityTime, Long finishTime, Float maxSpeed,
                 Float maxElevation, Float minElevation, Float elevationGain, Float elevationLoss,
                 Sport sport) {
        this.id = id;
        this.title = title;
        this.recording = recording;
        this.description = description;
        this.distance = distance;
        this.startTime = startTime;
        this.activityTime = activityTime;
        this.finishTime = finishTime;
        this.maxSpeed = maxSpeed;
        this.maxElevation = maxElevation;
        this.minElevation = minElevation;
        this.elevationGain = elevationGain;
        this.elevationLoss = elevationLoss;
        this.sport = sport;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getRecording() {
        return recording;
    }

    public void setRecording(Integer recording) {
        this.recording = recording;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(Long activityTime) {
        this.activityTime = activityTime;
    }

    public Long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Long finishTime) {
        this.finishTime = finishTime;
    }

    public Float getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(Float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public Float getMaxElevation() {
        return maxElevation;
    }

    public void setMaxElevation(Float maxElevation) {
        this.maxElevation = maxElevation;
    }

    public Float getMinElevation() {
        return minElevation;
    }

    public void setMinElevation(Float minElevation) {
        this.minElevation = minElevation;
    }

    public Float getElevationGain() {
        return elevationGain;
    }

    public void setElevationGain(Float elevationGain) {
        this.elevationGain = elevationGain;
    }

    public Float getElevationLoss() {
        return elevationLoss;
    }

    public void setElevationLoss(Float elevationLoss) {
        this.elevationLoss = elevationLoss;
    }

    public Sport getSport() {
        return sport;
    }

    public void setSport(Sport sport) {
        this.sport = sport;
    }
}

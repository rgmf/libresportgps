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
 * SegmentTrack Object Relational Mapping.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class SegmentTrack {
	private Long id;
	private Long time;
	private Float maxSpeed;
	private Float avgSpeed;
	private Track track;
	private SegmentPoint segmentPoint;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}
	public Float getMaxSpeed() {
		return maxSpeed;
	}
	public void setMaxSpeed(Float maxSpeed) {
		this.maxSpeed = maxSpeed;
	}
	public Float getAvgSpeed() {
		return avgSpeed;
	}
	public void setAvgSpeed(Float avgSpeed) {
		this.avgSpeed = avgSpeed;
	}
	public Track getTrack() {
		return track;
	}
	public void setTrack(Track track) {
		this.track = track;
	}
	public SegmentPoint getSegmentPoint() {
		return segmentPoint;
	}
	public void setSegmentPoint(SegmentPoint segmentPoint) {
		this.segmentPoint = segmentPoint;
	}
}

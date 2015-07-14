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
 * SegmentPoint Object Relational Mapping.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class SegmentPoint {
	private Long id;
	private Double beginLat = 0d;
	private Double endLat = 0d;
	private Double beginLng = 0d;
	private Double endLng = 0d;
	private Float distance = 0f;
	private Segment segment;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Float getDistance() {
		return distance;
	}
	public void setDistance(Float distance) {
		this.distance = distance;
	}
	public Segment getSegment() {
		return segment;
	}
	public void setSegment(Segment segment) {
		this.segment = segment;
	}
	public Double getBeginLat() {
		return beginLat;
	}
	public void setBeginLat(Double beginLat) {
		this.beginLat = beginLat;
	}
	public Double getEndLat() {
		return endLat;
	}
	public void setEndLat(Double endLat) {
		this.endLat = endLat;
	}
	public Double getBeginLng() {
		return beginLng;
	}
	public void setBeginLng(Double beginLng) {
		this.beginLng = beginLng;
	}
	public Double getEndLng() {
		return endLng;
	}
	public void setEndLng(Double endLng) {
		this.endLng = endLng;
	}
}

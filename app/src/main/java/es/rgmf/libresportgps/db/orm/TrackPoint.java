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

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * TrackPoint Object Relational Mapping.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class TrackPoint implements Parcelable {
	private long id;
	private double lat = 0;
	private double lng = 0;
	private long time = 0;
	private float distance = 0;
	private float accuracy = 0;
	private double elevation = 0;
	private float speed = 0;
	private Track track;

	public TrackPoint() {
	}

	private TrackPoint(Parcel in) {
		id = in.readLong();
		lat = in.readDouble();
		lng = in.readDouble();
		time = in.readLong();
		distance = in.readFloat();
		accuracy = in.readFloat();
		elevation = in.readDouble();
		speed = in.readFloat();
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public float getDistance() {
		return distance;
	}
	public void setDistance(float distance) {
		this.distance = distance;
	}
	public float getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}
	public double getElevation() {
		return elevation;
	}
	public void setElevation(double elevation) {
		this.elevation = elevation;
	}
	public float getSpeed() {
		return speed;
	}
	public void setSpeed(float speed) {
		this.speed = speed;
	}
	public Track getTrack() {
		return track;
	}
	public void setTrack(Track track) {
		this.track = track;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeLong(id);
		parcel.writeDouble(lat);
		parcel.writeDouble(lng);
		parcel.writeLong(time);
		parcel.writeFloat(distance);
		parcel.writeFloat(accuracy);
		parcel.writeDouble(elevation);
		parcel.writeFloat(speed);
	}

	public static final Parcelable.Creator<TrackPoint> CREATOR = new Parcelable.Creator<TrackPoint>() {
		public TrackPoint createFromParcel(Parcel in) {
			return new TrackPoint(in);
		}

		public TrackPoint[] newArray(int size) {
			return new TrackPoint[size];
		}
	};
}

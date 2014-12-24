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

package es.rgmf.libresportgps.file.reader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import es.rgmf.libresportgps.common.Utilities;
import es.rgmf.libresportgps.db.orm.TrackPoint;

/**
 * GPX reader class.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class GpxReader extends DefaultHandler implements IReader {
	private double distance = 0d;
	private Speed speed = new Speed();
	private Elevation elevation = new Elevation();
	private Long activityTime = 0L;
	private Long startTime = null;
	private Long finishTime = null;
	private List<TrackPoint> mTrackPoint = new ArrayList<TrackPoint>();

	private class ElevationGain {
		private static final double MIN_ELEVATION = 0.99d; // in meters.
		private static final double DISTANCE = 100d; // in meters.
		private Double start = null;
		private Double finish = null;
		private Double distance = 0d;
	}

	public GpxReader() {
	}

	public void loadFile(String gpxFile) {
		XmlPullParserFactory factory;
		Double prevLat = null, prevLon = null, currentLat = null, currentLon = null;
		boolean setDistance = true, min = false, trkPointStarted = false;
		ElevationGain elevationGain = new ElevationGain();
		TrackPoint trkPoint = new TrackPoint();

		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser parser = factory.newPullParser();

			parser.setInput(new InputStreamReader(new FileInputStream(gpxFile)));
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					if (parser.getName().equals("trkseg")) {
						if (prevLat != null && prevLon != null) {
							setDistance = false;
						}
					} else if (parser.getName().equals("trkpt")) {
						// Add already track point information and create 
						// another one if track point started.
						if (trkPointStarted == false) {
							trkPointStarted = true;
						} else {
							mTrackPoint.add(trkPoint);
							trkPoint = new TrackPoint();
						}

						// Get latitude and longitude to calculate the distance.
						if (parser.getAttributeCount() > 0) {
							if (prevLat == null && prevLon == null) {
								prevLat = currentLat = Double.valueOf(parser
										.getAttributeValue("", "lat"));
								prevLon = currentLon = Double.valueOf(parser
										.getAttributeValue("", "lon"));
								// Save latitude and longitude information inside
								// track point.
								trkPoint.setLat(prevLat);
								trkPoint.setLng(prevLon);
							} else {
								currentLat = Double.valueOf(parser
										.getAttributeValue("", "lat"));
								currentLon = Double.valueOf(parser
										.getAttributeValue("", "lon"));
								// Save latitude, longitude and distance information
								// inside track point.
								trkPoint.setLat(currentLat);
								trkPoint.setLng(currentLon);
								trkPoint.setDistance((float) distance);
								if (setDistance)
									distance += Utilities.CalculateDistance(
											prevLat, prevLon, currentLat,
											currentLon);
								prevLat = currentLat;
								prevLon = currentLon;

								elevationGain.distance += distance;
								if (elevationGain.distance >= elevationGain.DISTANCE) {
									if ((elevationGain.finish - elevationGain.start) >= elevationGain.MIN_ELEVATION) {
										elevation.gain += (elevationGain.finish - elevationGain.start);
									}
									elevationGain.start = elevationGain.finish;
									elevationGain.distance = 0d;
								}
							}
						}
						setDistance = true;
					} else if (parser.getName().equals("time")) {
						parser.next();
						// Save time information inside track point.
						trkPoint.setTime(Utilities
								.getMillisecondsTimeFromStringTime(parser
										.getText()));
						if (startTime == null) {
							startTime = Utilities
									.getMillisecondsTimeFromStringTime(parser
											.getText());
							finishTime = startTime;
						} else {
							activityTime += (Utilities
									.getMillisecondsTimeFromStringTime(parser
											.getText()) - finishTime);
							finishTime = Utilities
									.getMillisecondsTimeFromStringTime(parser
											.getText());
						}
					} else if (parser.getName().equals("speed")) {
						parser.next();
						// Save speed information inside track point.
						trkPoint.setSpeed(Float.valueOf(parser.getText()));
						double speedAux = Double.valueOf(parser.getText());
						if (speed.max < (speedAux * 3.7)) {
							speed.max = speedAux * 3.7; // km/h
						}
					} else if (parser.getName().equals("ele")) {
						parser.next();
						double elevationAux = Double.valueOf(parser.getText());
						// Save elevation information inside track point.
						trkPoint.setElevation(elevationAux);
						if (elevation.max < elevationAux)
							elevation.max = elevationAux;
						if (min) {
							if (elevation.min > elevationAux) {
								elevation.min = elevationAux;
							}
						} else {
							elevation.min = elevationAux;
							min = true;
						}

						if (elevationGain.start == null) {
							elevationGain.start = elevationAux;
						}
						elevationGain.finish = elevationAux;
					}
				}
				eventType = parser.next();
			}
			// System.out.println("End document");
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public double getDistance() {
		// TODO Auto-generated method stub
		return distance;
	}

	@Override
	public Speed getSpeed() {
		// TODO Auto-generated method stub
		return speed;
	}

	@Override
	public Elevation getElevation() {
		// TODO Auto-generated method stub
		return elevation;
	}

	public Long getActivityTime() {
		return activityTime == null ? 0 : activityTime;
	}

	public Long getStartTime() {
		return startTime == null ? 0 : startTime;
	}

	public Long getFinishTime() {
		return finishTime == null ? 0 : finishTime;
	}

	public List<TrackPoint> getTrackPoints() {
		return mTrackPoint;
	}

	public void setTrackPoints(List<TrackPoint> mTrackPoint) {
		this.mTrackPoint = mTrackPoint;
	}
}

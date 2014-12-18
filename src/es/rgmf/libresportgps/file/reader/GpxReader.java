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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import es.rgmf.libresportgps.common.Utilities;

public class GpxReader extends DefaultHandler implements IReader {
	private double distance;
	private Speed speed = new Speed();
	private Elevation elevation = new Elevation();
	private Long activityTime = 0L;
	private Long startTime = null;
	private Long finishTime = null;
	
	public GpxReader() {}
	
	public void loadFile(String gpxFile) {
		XmlPullParserFactory factory;
		Double prevLat = null, prevLon = null, currentLat = null, currentLon = null;
		boolean setDistance = true, min = false;
		
		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
	        XmlPullParser parser = factory.newPullParser();

	        parser.setInput(new InputStreamReader(new FileInputStream(gpxFile)));
	        int eventType = parser.getEventType();
	        while (eventType != XmlPullParser.END_DOCUMENT) {
				 if(eventType == XmlPullParser.START_DOCUMENT) {
				     //System.out.println("Start document");
				 }
				 else if(eventType == XmlPullParser.START_TAG) {
					 if(parser.getName().equals("trkseg")) {
						 if(prevLat != null && prevLon != null) {
							 setDistance = false;
							 //System.out.println("trkseg");
						 }
					 }
					 else if(parser.getName().equals("trkpt")) {
						 if(parser.getAttributeCount() > 0) {
							 if(prevLat == null && prevLon == null) {
								 prevLat = currentLat = Double.valueOf(parser.getAttributeValue("", "lat"));
								 prevLon = currentLon = Double.valueOf(parser.getAttributeValue("", "lon"));
							 }
							 else {
								 currentLat = Double.valueOf(parser.getAttributeValue("", "lat"));
								 currentLon = Double.valueOf(parser.getAttributeValue("", "lon"));
								 if(setDistance)
									 distance += Utilities.CalculateDistance(prevLat, prevLon, currentLat, currentLon);
								 prevLat = currentLat;
								 prevLon = currentLon;
							 }
						 }
						 setDistance = true;
					 }
					 else if(parser.getName().equals("time")) {
						 parser.next();
						 if(startTime == null) {
							 startTime = Utilities.getMillisecondsTimeFromStringTime(parser.getText());
							 finishTime = startTime;
						 }
						 else {
							 activityTime += (Utilities.getMillisecondsTimeFromStringTime(parser.getText()) - finishTime);
							 finishTime = Utilities.getMillisecondsTimeFromStringTime(parser.getText());
						 }
					 }
					 else if(parser.getName().equals("speed")) {
						 parser.next();
						 if(speed.max < (Double.valueOf(parser.getText()) * 3.7));
						 	speed.max = Double.valueOf(parser.getText()) * 3.7; // km/h
					 }
					 else if(parser.getName().equals("ele")) {
						 parser.next();
						 if(elevation.max < Double.valueOf(parser.getText()))
						 	 elevation.max = Double.valueOf(parser.getText());
						 if(min) {
							 if(elevation.min > Double.valueOf(parser.getText())) {
								 elevation.min = Double.valueOf(parser.getText());
							 }
						 }
						 else {
							 elevation.min = Double.valueOf(parser.getText());
						 }
					 }
				 }
				 eventType = parser.next();
	        }
	        //System.out.println("End document");
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loadFile(File gpxFile) {
		distance = 0d;
		
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(gpxFile);
			doc.getDocumentElement().normalize();
			
			NodeList nodeList = doc.getElementsByTagName("trkpt");
			double currentEle = 0, prevEle = 0, currentSpeed = 0;
			double prevLat = 0, prevLon = 0, currentLat = 0, currentLon = 0;
			int numItems = nodeList.getLength(), j = 0;
			
			// First items.
			for(j = 0; j < numItems; j++) {
				if(nodeList.item(j).getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) nodeList.item(j);
					if(element.getElementsByTagName("ele").item(0) != null)
						prevEle = Double.parseDouble(
								element.getElementsByTagName("ele").item(0).getTextContent());
					if(element.getAttribute("lat") != null)
						prevLat = Double.parseDouble(element.getAttribute("lat"));
					if(element.getAttribute("lon") != null)
						prevLon = Double.parseDouble(element.getAttribute("lon"));
					if(element.getElementsByTagName("time").item(0) != null)
						startTime = Utilities.getMillisecondsTimeFromStringTime(element.getElementsByTagName("time").item(0).getTextContent());
					
					elevation.max = prevEle;
					elevation.min = prevEle;
					finishTime = startTime;
					
					break;
				}
			}
			
			// Next items.
			for(int i = j; i < numItems; i++) {
				Node node = nodeList.item(i);
				
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					// GET INFORMATION FROM NODE.
					Element element = (Element) node;
					if(element.getElementsByTagName("ele").item(0) != null)
						currentEle = Double.parseDouble(
								element.getElementsByTagName("ele").item(0).getTextContent());
					if(element.getElementsByTagName("speed").item(0) != null)
						currentSpeed = Double.parseDouble(
								element.getElementsByTagName("speed").item(0).getTextContent());
					if(element.getAttribute("lat") != null)
						currentLat = Double.parseDouble(element.getAttribute("lat"));
					if(element.getAttribute("lon") != null)
						currentLon = Double.parseDouble(element.getAttribute("lon"));
					if(element.getElementsByTagName("time").item(0) != null) {
						finishTime = Utilities.getMillisecondsTimeFromStringTime(element.getElementsByTagName("time").item(0).getTextContent());
						if(finishTime != null) {
							activityTime += finishTime;
							if(startTime == null)
								startTime = finishTime;
						}
					}
					
					// SET DISTANCE.
					distance += Utilities.CalculateDistance(prevLat, prevLon, currentLat, currentLon);
					prevLat = currentLat;
					prevLon = currentLon;
					
					// SET ELEVATION.
					if(prevEle < currentEle) {
						elevation.gain += (currentEle - prevEle);
					}
					else if(prevEle > currentEle) {
						elevation.loss += (prevEle - currentEle);
					}
					
					if(currentEle > elevation.max) {
						elevation.max = currentEle;
					}
					else if(currentEle < elevation.min) {
						elevation.min = currentEle;
					}
					
					prevEle = currentEle;
					
					// SET SPEED.
					if(speed.max < currentSpeed)
						speed.max = currentSpeed * 3.7; // km/h
					speed.avg += currentSpeed;
				} 
			}
			speed.avg = speed.avg / numItems * 3.7; // km/h
		} catch (ParserConfigurationException | SAXException | IOException e) {
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
}

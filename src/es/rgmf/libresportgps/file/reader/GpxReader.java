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
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import es.rgmf.libresportgps.common.Utilities;

public class GpxReader extends DefaultHandler implements IReader {
	private double distance;
	private Speed speed = new Speed();
	private Elevation elevation = new Elevation();
	
	public GpxReader(File gpxFile) {
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
					
					elevation.max = prevEle;
					elevation.min = prevEle; 
					
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
}

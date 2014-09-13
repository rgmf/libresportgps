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

package es.rgmf.libresportgps.file.writer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.location.Location;
import es.rgmf.libresportgps.common.Session;
import es.rgmf.libresportgps.common.Utilities;

/**
 * This class can be used to write GPX files.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class GpxWriter implements IWriter {
	/**
	 * This is the file to write gpx data.
	 */
	private File gpxFile;
	/**
	 * The offset in the file where we start to write waypoints information.
	 * 
	 * This offset points to the position of the file where we will write the
	 * next waypoint information:
	 * 
	 * <wpt lat="..." lon="...">
	 *     ...
	 * </wpt>
	 */
	private long offsetWaypoints = 0;
	/**
	 * The offset in the file where we start to write track information.
	 * 
	 * This offset points to the position of the file where we will write the
	 * next track information:
	 * 
	 * <trk>
	 *     ...
	 * </trk>
	 */
	private long offsetTrack = 0;
	/**
	 * The offset in the file where we will write the bounds information in
	 * GPX XML file.
	 * 
	 * The bounds only can be write at the end because we need end latitude and
	 * longitude. Example of bounds:
	 * 
	 * <bounds minlat="42.401051" minlon="-71.126602" maxlat="42.468655" maxlon="-71.102973"/>
	 */
	private long offsetBounds = 0;
	
	/**
	 * This construct write the header GPX file and prepare it to
	 * be written.
	 * 
	 * At the end of the construction method, we will have a gpx empty file and
	 * all offsets ready to append contents to the file.
	 * 
	 * @param file The file already created.
	 */
	public GpxWriter(File file) {
		try {
			this.gpxFile = file;
			String currentHour = Utilities.timeStampFormatter(System.currentTimeMillis());
			FileOutputStream fos = new FileOutputStream(gpxFile);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			StringBuilder sb = new StringBuilder();
			
			// The GPX XML file empty.
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
            sb.append("<gpx version=\"1.0\" creator=\"LibreSportGPS - http://www.rgmf.es/\" ");
            sb.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
            sb.append("xmlns=\"http://www.topografix.com/GPX/1/0\" ");
            sb.append("xsi:schemaLocation=\"http://www.topografix.com/GPX/1/0 ");
            sb.append("http://www.topografix.com/GPX/1/0/gpx.xsd\">");
            sb.append("<time>").append(currentHour).append("</time>");
            sb.append("<trk><trkseg></trkseg></trk></gpx>");
            
            // Write the StringBuilder in the file.
            bos.write(sb.toString().getBytes());
            bos.flush();
            bos.close();
            
            // Set the offset we will need.
            this.offsetWaypoints = this.gpxFile.length() - 34;
            this.offsetTrack = this.gpxFile.length() - 21;
            this.offsetBounds = this.gpxFile.length() - (47 + currentHour.length());
            
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method use the gpxFile and the offsetTrack to write a new
	 * track information.
	 */
	@Override
	public void writeTrack(Location loc) {
		try {
			String currentHour = Utilities.timeStampFormatter(System.currentTimeMillis());
			RandomAccessFile raf = new RandomAccessFile(this.gpxFile, "rw");
			StringBuilder sb = new StringBuilder();
			
			// There have been a pause of the signal?
			if(Session.openTrkseg()) {
				Session.setOpenTrkseg(false);
				sb.append("</trkseg>");
				sb.append("<trkseg>");
			}
			
			// Create the new track information.
			sb.append("<trkpt lat=\"")
			  .append(String.valueOf(loc.getLatitude()))
			  .append("\" lon=\"")
			  .append(String.valueOf(loc.getLongitude()))
			  .append("\">");
			
			// Altitude.
			if(loc.hasAltitude()) {
				sb.append("<ele>")
				  .append(String.valueOf(loc.getAltitude()))
				  .append("</ele>");
			}
			
			// Time of this track point.
			sb.append("<time>")
			  .append(currentHour)
			  .append("</time>");
			
			// Speed.
			if(loc.hasSpeed()) {
				sb.append("<speed>")
				  .append(String.valueOf(loc.getSpeed()))
				  .append("</speed>");
			}
			
			// Extras: dilution of precision (DOP).
			if(loc.getExtras() != null) {
				// Horizontal Dilution Of Precision.
				if(loc.getExtras().getString("HDOP") != null) {
					sb.append("<hdop>")
					  .append(loc.getExtras().getString("HDOP"))
					  .append("</hdop>");
				}
				// Vertical Dilution Of Precision.
				if(loc.getExtras().getString("VDOP") != null) {
					sb.append("<vdop>")
					  .append(loc.getExtras().getString("VDOP"))
					  .append("</vdop>");
				}
				// Positional (3D) Dilution Of Precision.
				if(loc.getExtras().getString("PDOP") != null) {
					sb.append("<pdop>")
					  .append(loc.getExtras().getString("PDOP"))
					  .append("</pdop>");
				}
				// Time Dilution Of Precision.
				if(loc.getExtras().getString("TDOP") != null) {
					sb.append("<tdop>")
					  .append(loc.getExtras().getString("TDOP"))
					  .append("</tdop>");
				}
			}
			
			// Close the trkpt tag.
			sb.append("</trkpt></trkseg></trk></gpx>");
            
            // Write the StringBuilder in the file.
			raf.seek(offsetTrack);
			raf.write(sb.toString().getBytes());
			raf.close();
            
            // Update offset of the track we will need.
            this.offsetTrack += sb.length() - 21;
            
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method use the gpxFile and the offsetWaypoint to write a new
	 * waypoint information.
	 */
	@Override
	public void writeWaypoint(Location loc) {
				
	}
}

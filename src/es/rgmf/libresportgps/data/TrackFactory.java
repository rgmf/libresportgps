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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This factory class is used to create all types of tracks
 * (like gpx or kml, for instance).
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class TrackFactory {
	private static ArrayList<Track> tracks = null;
	private static final List<String> EXTENSIONS = Collections.unmodifiableList(Arrays.asList(new String[] {".gpx"}));
	
	/**
	 * Return a list of tracks into folderPath.
	 * 
	 * @param folderPath The path where tracks are.
	 * @return The list of Track.
	 */
	public static ArrayList<Track> getTracks(String folderPath) {
		if(tracks == null || tracks.size() == 0) {
			tracks = new ArrayList<Track>();
			File folder = new File(folderPath);
			
			if(folder.isDirectory()) {
				for(File file : folder.listFiles()) {
					if(file.isFile()) {
						String fileName = file.getName();
						String extension = fileName.substring(fileName.length() - 4);
						if(EXTENSIONS.contains(extension)) {
							switch(extension) {
							case ".gpx":
								GpxTrack track = new GpxTrack();
								track.setName(fileName);
								track.setDate(file.lastModified());
								tracks.add(track);
								break;
							}
						}
					}
				}
			}
		}
		
		return tracks;
	}
	
	public static void reset() {
		tracks = null;
	}
}

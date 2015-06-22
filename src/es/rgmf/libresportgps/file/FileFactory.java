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

package es.rgmf.libresportgps.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import es.rgmf.libresportgps.common.Session;
import es.rgmf.libresportgps.file.writer.GpxWriter;
import es.rgmf.libresportgps.file.writer.IWriter;

/**
 * This class creates all files where tracks, waypoints and routes will be
 * save.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class FileFactory {
	private static List<IWriter> files = null;
	
	public static List<IWriter> getFiles(String trackId) {
		if(files == null) {
			String folderName = Session.getAppFolder() + "/" + trackId;
			
			// If folder does not exist then creates it.
			File folder = new File(folderName);
			if(!folder.exists())
				folder.mkdirs();
			
			// Creates the file.
			files = new ArrayList<IWriter>();
			File file = new File(folderName, Session.getFileName() + ".gpx");
			GpxWriter gpxFile = new GpxWriter(file);
		
			// Adds the file inside the list of files.
			files.add(gpxFile);
		}
		
		return files;
	}
	
	public static void reset() {
		files = null;
	}
}

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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import es.rgmf.libresportgps.common.Session;

/**
 * This class creates all files where tracks, waypoints and routes will be
 * save.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class FileFactory {
	/**
	 * Create a folder if not exists called trackId inside the app folder.
	 * 
	 * @param trackId
	 * @return
	 */
	public static String createFolderIfNotExists(String trackId) {
		String folderName = Session.getAppFolder() + "/" + trackId;
		
		// If folder does not exist then creates it.
		File folder = new File(folderName);
		if(!folder.exists())
			folder.mkdirs();
		
		return folderName;
	}
	
	/**
	 * Copy the file fd in the folder name folderName.
	 * 
	 * @param fd
	 * @param folderName
	 * @throws IOException
	 */
	public static void copyFile(File fd, String folderName) throws IOException {
		InputStream in = new FileInputStream(fd.getAbsolutePath());
		OutputStream out = new FileOutputStream(folderName + "/" + fd.getName());
		
		// Transfer bytes from in to out
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = in.read(buf)) > 0) {
	        out.write(buf, 0, len);
	    }
	    in.close();
	    out.close();
	}
}

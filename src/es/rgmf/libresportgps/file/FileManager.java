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

/**
 * This is the base class to manage files and folders like list
 * files in a folder, see attributes of files and so on.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class FileManager {
	/**
	 * This method move sourceFolder/sourceName file to 
	 * destinationFolder/destinationName file.
	 * 
	 * @param sourceFolder
	 * @param sourceName
	 * @param destinationFolder
	 * @param destinationName
	 */
	public static void rename(String sourceFolder, String sourceName,
			String destinationFolder, String destinationName) {
		File source = new File(sourceFolder + "/" + sourceName);
		File destination = new File(destinationFolder + "/" + destinationName);
		
		if(source.isFile()) {
			source.renameTo(destination);
		}
	}
	
	/**
	 * This method delete folder/name file.
	 * 
	 * @param folder
	 * @param name
	 */
	public static void delete(String folder, String name) {
		File f = new File(folder + "/" + name);
		
		if(f.isFile()) {
			f.delete();
		}
	}
	
	/**
	 * This method delete folder.
	 * 
	 * @param folder name of the folder.
	 */
	public static void delete(String folder) {
		File f = new File(folder);
		
		if (f.isDirectory()) {
			// Delete all files of the folder.
			for (String s: f.list()) {
				File currentFile = new File(f.getPath(), s);
				currentFile.delete();
			}
			
			// Now already we can delete the folder.
			f.delete();
		}
	}
	
	/**
	 * Return a list of file names into folder.
	 * 
	 * @param folderPath The path of the folder.
	 * @return The list of files.
	 */
	public static ArrayList<String> getListOfFiles(String folderPath) {
		ArrayList<String> list = new ArrayList<String>();
		File folder = new File(folderPath);
		
		if(folder.isDirectory()) {
			for(File file : folder.listFiles()) {
				if(file.isFile())
					list.add(file.getName());
			}
		}
		
		return list;
	}
}

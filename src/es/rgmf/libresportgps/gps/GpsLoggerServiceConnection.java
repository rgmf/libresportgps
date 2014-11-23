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

package es.rgmf.libresportgps.gps;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import es.rgmf.libresportgps.MainActivity;

/**
 * The GPS service connection.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class GpsLoggerServiceConnection implements ServiceConnection {

	/**
	 * Reference to LibreSportGps activity
	 */
	private MainActivity activity;
	
	public GpsLoggerServiceConnection(MainActivity mainActivity) {
		activity = mainActivity;
	}
	
	@Override
	public void onServiceDisconnected(ComponentName name) {
		activity.setGpsService(null);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		// Set the GPS service.
		activity.setGpsService(((GpsLoggerService.GpsLoggerBinder) service).getService());
		
		// The Activity (the Service Client) from GpsLoggerService.
		GpsLoggerService.setServiceClient(activity);
	}

}

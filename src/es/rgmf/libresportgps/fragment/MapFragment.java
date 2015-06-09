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

package es.rgmf.libresportgps.fragment;

import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import es.rgmf.libresportgps.R;
import es.rgmf.libresportgps.db.orm.Track;

/**
 * This View is created to show the detail information of a Track.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class MapFragment extends Fragment {
	private MapView mMapView;
	private MapController mMapController;
	private Track mTrack;
	
	public static MapFragment newInstance(Track track) {
		MapFragment f = new MapFragment();
		f.mTrack = track;
		return f;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_map, container,
				false);
		
		// Create scale bar.
		ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(
				getActivity());
		
		// Get and configure the map view.
		mMapView = (MapView) rootView.findViewById(R.id.openmapview);
		mMapView.setBuiltInZoomControls(true);
		mMapView.getOverlays().add(scaleBarOverlay);

		// Get and configure map controller.
		mMapController = (MapController) mMapView.getController();
		mMapController.setZoom(14);
		
		return rootView;
	}
}

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

package es.rgmf.libresportgps.view;

import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import android.content.Context;
import android.util.AttributeSet;

/**
 * The class extended from MapView from osmdroid library.
 * 
 * I needed extended this class to zoom to bounding box where layout
 * is completed loaded overriding onLayout method.
 * 
 * Also, I needed extend this class to center the map initially on
 * a GeoPoint.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class RouteMapView extends MapView {
	private BoundingBoxE6 mBoundingBox = null;
	private GeoPoint mCenterPoint = null;
	private boolean wasCentered = false;

	public RouteMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
	    super.onLayout(arg0, arg1, arg2, arg3, arg4);

	    // Now that we have laid out the map view,
	    // zoom to any bounding box and center to 
	    // GeoPoint.
	    // This action have only done one time.
	    if (!wasCentered) {
		    if (this.mCenterPoint != null) {
		    	this.getController().setCenter(this.mCenterPoint);
		    }
		    
		    if (this.mBoundingBox != null) {
		        this.zoomToBoundingBox(this.mBoundingBox);
		    }
		    wasCentered = true;
	    }
	}
	
	public void setBoundingBoxE6(BoundingBoxE6 bb) {
		mBoundingBox = bb;
	}
	
	public void setCenterPoint(GeoPoint cp) {
		mCenterPoint = cp;
	}
}

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
import org.osmdroid.util.TileSystem;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.util.constants.MapViewConstants;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
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
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);

		if (!wasCentered) {
			if (mCenterPoint != null) {
				this.getController().setCenter(mCenterPoint);
			}
			if (mBoundingBox != null) {
				this.zoomToBoundingBox(mBoundingBox);
				wasCentered = true;
			}
		}
	}

	public void setBoundingBoxE6(BoundingBoxE6 bb) {
		mBoundingBox = bb;
	}

	public void setCenterPoint(GeoPoint cp) {
		mCenterPoint = cp;
	}
}

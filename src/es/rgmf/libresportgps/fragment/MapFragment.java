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

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.PathOverlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import es.rgmf.libresportgps.R;
import es.rgmf.libresportgps.db.orm.TrackPoint;
import es.rgmf.libresportgps.view.RouteMapView;

/**
 * This View is created to show the detail information of a Track.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class MapFragment extends Fragment {
	/**
	 * The osmdroid map view.
	 */
	private RouteMapView mMapView;
	/**
	 * The osmdroid controller.
	 */
	private MapController mMapController;
	/**
	 * List of track points of the track we will show over the map.
	 */
	private List<TrackPoint> mListTrackPoint = new ArrayList<TrackPoint>();
	
	/**
	 * Method to create instances.
	 * @param list
	 * @return
	 */
	public static MapFragment newInstance(List<TrackPoint> list) {
		MapFragment f = new MapFragment();
		f.mListTrackPoint = list;
		return f;
	}
	
	/**
	 * On create view:
	 * - Create the osmdroid map.
	 * - Draw the track with all track points of it.
	 * - Center the map.
	 * - Apply default zoom.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_map, container,
				false);
		
		// Create scale bar.
		ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(
				getActivity());
		
		// Get and configure the map view.
		mMapView = (RouteMapView) rootView.findViewById(R.id.openmapview);
		mMapView.setBuiltInZoomControls(true);
		mMapView.setMultiTouchControls(true);
		mMapView.getOverlays().add(scaleBarOverlay);

		// Get and configure map controller.
		mMapController = (MapController) mMapView.getController();
		
		// Load the track over the map.
		List<OverlayItem> overlays = new ArrayList<OverlayItem>();
		List<GeoPoint> geopoints = new ArrayList<GeoPoint>();
		GeoPoint point = null;
		OverlayItem overlay;
		PathOverlay path = new PathOverlay(Color.RED, getActivity());
		double minLat = Double.MAX_VALUE;
		double minLng = Double.MAX_VALUE;
		double maxLat = -Double.MAX_VALUE;
		double maxLng = -Double.MAX_VALUE;
		for (TrackPoint tp : mListTrackPoint) {
			point = new GeoPoint(tp.getLat(), tp.getLng());
			geopoints.add(point);
			
			// Title of the overlay = identify of the track point.
			// Snippet of the overlay = identify of the track point.
			overlay = new OverlayItem(String.valueOf(tp.getId()), String.valueOf(tp.getId()), point);
			overlays.add(overlay);
			
			// Create the path.
			path.addPoint(point);
			
			// Refresh minimums and maximums.
			if (minLat > tp.getLat())
				minLat = tp.getLat();
			if (minLng > tp.getLng()) 
				minLng = tp.getLng();
			if (maxLat < tp.getLat())
				maxLat = tp.getLat();
			if (maxLng < tp.getLng())
				maxLng = tp.getLng();
		}
		
		// The point where we center the map is the last GeoPoint.
		mMapView.setCenterPoint(point);
		
		// The bounding box where the map view will zoom.
		mMapView.setBoundingBoxE6(new BoundingBoxE6(maxLat, minLng, minLat, maxLng));
		
		// Add the path create before.
		mMapView.getOverlays().add(path);
		
		// Create the marker to each track point.
		Drawable marker = getResources().getDrawable(R.drawable.geopoint);
        int markerWidth = marker.getIntrinsicWidth();
        int markerHeight = marker.getIntrinsicHeight();
        marker.setBounds(0, markerHeight, markerWidth, 0);
        
        ResourceProxy resourceProxy = new DefaultResourceProxyImpl(getActivity());
        
        MyItemizedIconOverlay anotherItemizedIconOverlay = new MyItemizedIconOverlay(overlays, myOnItemGestureListener, resourceProxy, marker);
		mMapView.getOverlays().add(anotherItemizedIconOverlay);
		
		/*if (point != null) {
			ViewTreeObserver vto = rootView.getViewTreeObserver();
			vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			    @Override
			    public void onGlobalLayout() {
			        mMapController.setCenter(mCenterPoint);
			    }
			});
		}*/
		
		return rootView;
	}
	
	/**
	 * To get "click" on the points.
	 */
	OnItemGestureListener<OverlayItem> myOnItemGestureListener = new OnItemGestureListener<OverlayItem>() {		 
	    @Override
	    public boolean onItemLongPress(int arg0, OverlayItem arg1) {
	        // TODO Auto-generated method stub
	        return false;
	    }
	 
	    @Override
	    public boolean onItemSingleTapUp(int index, OverlayItem item) {
	        Toast.makeText(
	            getActivity(),
	            item.getPoint().getLatitude() + " : " + item.getPoint().getLongitude(),
	            Toast.LENGTH_LONG).show();
	             
	        return true;
	    }
	};
	
	/**
	 * My own itemized icon overlay to draw points where track points are.
	 * 
	 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
	 */
	private class MyItemizedIconOverlay extends ItemizedIconOverlay<OverlayItem> {
		Drawable mMarker;
		 
	    public MyItemizedIconOverlay(
	            List<OverlayItem> pList,
	            org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener<OverlayItem> pOnItemGestureListener,
	            ResourceProxy pResourceProxy,
	            Drawable marker) {
	    	
	    	super(pList, pOnItemGestureListener, pResourceProxy);
	 
	    	mMarker = marker;
	    }
	 
	    @Override
	    public void draw(Canvas canvas, MapView mapview, boolean arg2) {
	        // TODO Auto-generated method stub
	        //super.draw(canvas, mapview, arg2);
	 
	        if (this.size() > 0) {
	 
	            for (int i = 0; i < this.size(); i++) {
	                GeoPoint in = getItem(i).getPoint();
	 
	                Point out = new Point();
	                mapview.getProjection().toPixels(in, out);
	 
	                Bitmap bm = ((BitmapDrawable) mMarker).getBitmap();
	                //Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
	                canvas.drawBitmap(bm, out.x - bm.getWidth() / 2, out.y - bm.getHeight() / 2, null);
	            }
	        }
	    }
	 
	    @Override
	    public boolean onSingleTapUp(MotionEvent event, MapView mapView) {
	        // TODO Auto-generated method stub
	        // return super.onSingleTapUp(event, mapView);
	        return true;
	    }
	}
}

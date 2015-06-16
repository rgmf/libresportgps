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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import es.rgmf.libresportgps.R;
import es.rgmf.libresportgps.db.orm.TrackPoint;
import es.rgmf.libresportgps.view.RouteMapView;

/**
 * This View is created to show the map information of a Track.
 * 
 * Through this view you can see the path and create segments
 * clicking two points (begin and end point of the segment).
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class MapFragment extends Fragment {
	private static final int SCALE_OVERLAY = 0;
	private static final int PATH_OVERLAY = 1;
	private static final int ICON_OVERLAY = 2;
	
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
	 * The index of the begin point of the segment.
	 */
	private Integer mIdxBeginPoint = null;
	/**
	 * The index of the end point of the segment.
	 */
	private Integer mIdxEndPoint = null;
	
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
		mMapView.getOverlays().add(SCALE_OVERLAY, scaleBarOverlay);

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
		mMapView.getOverlays().add(PATH_OVERLAY, path);
		
		// Create the marker to each track point.
		Drawable marker = getResources().getDrawable(R.drawable.geopoint);
        int markerWidth = marker.getIntrinsicWidth();
        int markerHeight = marker.getIntrinsicHeight();
        marker.setBounds(0, markerHeight, markerWidth, 0);
        
        // Add ItemizedIconOverlay overlay to the map view. This is a set of 
        // icons.
        ResourceProxy resourceProxy = new DefaultResourceProxyImpl(getActivity());
        MyItemizedIconOverlay anotherItemizedIconOverlay = new MyItemizedIconOverlay(overlays, myOnItemGestureListener, resourceProxy, marker);
		mMapView.getOverlays().add(ICON_OVERLAY, anotherItemizedIconOverlay);
		
		return rootView;
	}
	
	/**
	 * To get "click" on the points.
	 * 
	 * We need these events to create segments on map view GeoPoints.
	 */
	OnItemGestureListener<OverlayItem> myOnItemGestureListener = new OnItemGestureListener<OverlayItem>() {		 
	    @Override
	    public boolean onItemLongPress(int arg0, OverlayItem arg1) {
	        // TODO Auto-generated method stub
	        return false;
	    }
	 
	    @Override
	    public boolean onItemSingleTapUp(int index, OverlayItem item) {
	    	// Create the new marker.
    		Drawable marker = getResources().getDrawable(R.drawable.geopoint_selected);
            int markerWidth = marker.getIntrinsicWidth();
            int markerHeight = marker.getIntrinsicHeight();
            marker.setBounds(0, markerHeight, markerWidth, 0);
            
	    	if (mIdxBeginPoint == null) {
	    		mIdxBeginPoint = index;
	    		
	    		// Get MyItemizedIconOverlay overlay from map view.
		    	MyItemizedIconOverlay overlay = (MyItemizedIconOverlay) mMapView.getOverlays().get(ICON_OVERLAY);
		    	overlay.setMarker(index, marker);
		    	mMapView.invalidate();
	    	}
	    	else if (mIdxEndPoint == null) {
	    		mIdxEndPoint = index;
	    		
	    		Log.v("Begin", mIdxBeginPoint + "");
	    		Log.v("End", mIdxEndPoint + "");
	    		
	    		if (mIdxBeginPoint > mIdxEndPoint) {
	    			mIdxEndPoint = mIdxBeginPoint;
	    			mIdxBeginPoint = index;
	    		}
	    		
	    		Log.v("Begin", mIdxBeginPoint + "");
	    		Log.v("End", mIdxEndPoint + "");
	    		
	    		
	    		
	    		for (int i = mIdxBeginPoint + 1; i <= mIdxEndPoint; i++) {
	    			// Get MyItemizedIconOverlay overlay from map view.
	    	    	MyItemizedIconOverlay overlay = (MyItemizedIconOverlay) mMapView.getOverlays().get(ICON_OVERLAY);
	    	    	overlay.setMarker(i, marker);
	    	    	mMapView.invalidate();
	    		}
	    	}
	    	
	    	/*
	    	item.setMarker(marker);
	    	
	    	Toast.makeText(
	    			getActivity(),
	    			item.getPoint().getLatitude() + " : " + item.getPoint().getLongitude(),
	    			Toast.LENGTH_LONG).show();
	    	*/
	             
	        return true;
	    }
	};
	
	/**
	 * My own itemized icon overlay to draw points where track points are.
	 * 
	 * These icons can be default icons or custom icons.
	 * 
	 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
	 */
	private class MyItemizedIconOverlay extends ItemizedIconOverlay<OverlayItem> {
		/**
		 * The default marker to points that are not marker.
		 */
		private Drawable mDefaultMarker;
		/**
		 * Map of custom markers.
		 */
		private Map<Integer, Drawable> mCustomMarkers;
		
	    public MyItemizedIconOverlay(
	            List<OverlayItem> pList,
	            org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener<OverlayItem> pOnItemGestureListener,
	            ResourceProxy pResourceProxy,
	            Drawable marker) {
	    	
	    	super(pList, pOnItemGestureListener, pResourceProxy);
	 
	    	mDefaultMarker = marker;
	    	
	    	mCustomMarkers = new HashMap<Integer, Drawable>();
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
	 
	                Bitmap bm;
	                if (mCustomMarkers.containsKey(i))
	                	bm = ((BitmapDrawable) mCustomMarkers.get(i)).getBitmap();
	                else	
	                	bm = ((BitmapDrawable) mDefaultMarker).getBitmap();
	                
	                //Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
	                canvas.drawBitmap(bm, out.x - bm.getWidth() / 2, out.y - bm.getHeight() / 2, null);
	            }
	        }
	    }
	    
	    /**
	     * Set a custom marker to a point.
	     * 
	     * @param index The index of the point to customize.
	     * @param marker The custom marker.
	     */
	    public void setMarker(Integer index, Drawable marker) {
			this.mCustomMarkers.put(index, marker);
		}
	 
	    @Override
	    public boolean onSingleTapUp(MotionEvent event, MapView mapView) {
	        // TODO Auto-generated method stub
	        // return super.onSingleTapUp(event, mapView);
	        return true;
	    }
	}
}

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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import es.rgmf.libresportgps.R;
import es.rgmf.libresportgps.SegmentActivity;
import es.rgmf.libresportgps.common.SegmentUtil;
import es.rgmf.libresportgps.db.DBModel;
import es.rgmf.libresportgps.db.orm.SegmentPoint;
import es.rgmf.libresportgps.db.orm.TrackPoint;
import es.rgmf.libresportgps.fragment.dialog.AddSegmentDialog;
import es.rgmf.libresportgps.fragment.dialog.AddSegmentDialog.AddSegmentDialogListener;
import es.rgmf.libresportgps.view.RouteMapView;

/**
 * This View is created to show the map information of a Track.
 * 
 * Through this view you can see the path and create segments
 * clicking two points (begin and end point of the segment).
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class MapFragment extends Fragment implements AddSegmentDialogListener {
	private static final int SCALE_OVERLAY = 0;
	private static final int PATH_OVERLAY = 1;
	private static final int ICON_OVERLAY = 2;
	private static final int SEGMENT_OVERLAY = 3;
	
	/**
	 * The osmdroid map view.
	 */
	private RouteMapView mMapView;
	/**
	 * The osmdroid controller.
	 */
	private MapController mMapController;
	/**
	 * The track id.
	 */
	private Long mTrackId;
	/**
	 * List of track points of the track we will show over the map.
	 */
	private List<TrackPoint> mListTrackPoint = new ArrayList<TrackPoint>();
	/**
	 * The options menu.
	 */
	private Menu mMenu;
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
	public static MapFragment newInstance(Long trackId, List<TrackPoint> list) {
		MapFragment f = new MapFragment();
		f.mTrackId = trackId;
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
        
        setHasOptionsMenu(true);
		
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
		Double minLat = Double.MAX_VALUE;
		Double minLng = Double.MAX_VALUE;
		Double maxLat = -Double.MAX_VALUE;
		Double maxLng = -Double.MAX_VALUE;
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
		mMapView.setBoundingBoxE6(new BoundingBoxE6(maxLat, maxLng, minLat, minLng));
		
		// Add the path create before.
		mMapView.getOverlays().add(PATH_OVERLAY, path);
		
		// Create the marker to each track point.
		Drawable marker = getResources().getDrawable(R.drawable.geopoint);
        int markerWidth = marker.getIntrinsicWidth();
        int markerHeight = marker.getIntrinsicHeight();
        marker.setBounds(0, markerHeight, markerWidth, 0);
        
        // Add ItemIconOverlay overlay to the map view. This is a set of 
        // icons.
        ResourceProxy resourceProxy = new DefaultResourceProxyImpl(getActivity());
        ItemIconOverlay anotherItemizedIconOverlay = new ItemIconOverlay(overlays, myOnItemGestureListener, resourceProxy, marker);
		mMapView.getOverlays().add(ICON_OVERLAY, anotherItemizedIconOverlay);
		
		// Get segments of this track to draw icons over the map.
		List<OverlayItem> segmentOverlays = new ArrayList<OverlayItem>();
		GeoPoint segmentPoint;
		OverlayItem segmentOverlay;
		//List<SegmentTrack> segmentTrackList = DBModel.getAllSegmentTrack(getActivity(), mTrackId);
		List<SegmentPoint> segmentPoints = DBModel.getAllSegmentPointFromTrack(getActivity(), mTrackId);
		for (SegmentPoint sp : segmentPoints) {
			if (sp.getSegment() != null && sp.getSegment().getId() != null) {
				segmentPoint = new GeoPoint(sp.getBeginLat(), sp.getBeginLng());
				segmentOverlay = new OverlayItem(String.valueOf(sp.getSegment().getId()), String.valueOf(sp.getSegment().getId()), segmentPoint);
				segmentOverlays.add(segmentOverlay);
			}
		}
		
		marker = getResources().getDrawable(R.drawable.segment_overlay);
        markerWidth = marker.getIntrinsicWidth();
        markerHeight = marker.getIntrinsicHeight();
        marker.setBounds(0, markerHeight, markerWidth, 0);
        
        SegmentIconOverlay segmentIconOverlay = new SegmentIconOverlay(segmentOverlays, onSegmentItemGestureListener, resourceProxy, marker);
		mMapView.getOverlays().add(SEGMENT_OVERLAY, segmentIconOverlay);
		
		return rootView;
	}
	
	/**
	 * This method modifies the options in the bar menu adapting it to this
	 * fragment.
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		mMenu = menu;
	    menu.clear();
	    inflater.inflate(R.menu.segment, menu);
	}
	
	/**
	 * Handle the clicked options in this fragment.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			/* THE USER CLICKS ON ADD SEGMENT BUTTON ON BUTTON BAR */
			case R.id.segment_add:
				if (mIdxBeginPoint == null || mIdxEndPoint == null) {
					new AlertDialog.Builder(getActivity())
					.setTitle(R.string.add_segment)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setMessage(getResources().getString(R.string.add_segment_hint))
					.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					}).create().show();
				}
				else {
					AddSegmentDialog dialog = new AddSegmentDialog(
							mTrackId,
							mListTrackPoint.get(mIdxBeginPoint),
							mListTrackPoint.get(mIdxEndPoint));
					dialog.setTargetFragment(this, 1);
					dialog.show(getFragmentManager(), null);
				}

	            return true;
	        
			/* THE USER CLICK ON CANCEL SEGMENT BUTTON ON BUTTON BAR */
			case R.id.segment_cancel:
				Drawable marker = getResources().getDrawable(R.drawable.geopoint);
		        int markerWidth = marker.getIntrinsicWidth();
		        int markerHeight = marker.getIntrinsicHeight();
		        marker.setBounds(0, markerHeight, markerWidth, 0);
				
		        if (mIdxBeginPoint != null && mIdxEndPoint != null) {
					for (int i = mIdxBeginPoint; i <= mIdxEndPoint; i++) {
		    			// Get ItemIconOverlay overlay from map view.
		    	    	ItemIconOverlay overlay = (ItemIconOverlay) mMapView.getOverlays().get(ICON_OVERLAY);
		    	    	overlay.setMarker(i, marker);
		    	    	mMapView.invalidate();
		    		}
		        }
		        else if (mIdxBeginPoint != null) {
		        	// Get MyItemizedIconOverlay overlay from map view.
	    	    	ItemIconOverlay overlay = (ItemIconOverlay) mMapView.getOverlays().get(ICON_OVERLAY);
	    	    	overlay.setMarker(mIdxBeginPoint, marker);
	    	    	mMapView.invalidate();
		        }
				
				mIdxBeginPoint = null;
				mIdxEndPoint = null;
				
				item.setVisible(false);
				
				return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * To get "click" on the segment icon.
	 */
	OnItemGestureListener<OverlayItem> onSegmentItemGestureListener = new OnItemGestureListener<OverlayItem>() {
		@Override
		public boolean onItemLongPress(int arg0, OverlayItem arg1) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onItemSingleTapUp(int arg0, OverlayItem item) {
			Intent intent = new Intent(getActivity(), SegmentActivity.class);
			intent.putExtra("segmentId", Long.valueOf(item.getTitle()));
			intent.putExtra("trackId", mTrackId);
			startActivity(intent);
			
			return true;
		}
	};
	
	/**
	 * To get "click" on the points from the track.
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
            
            // The index of the overlays is the same that the list mListTrackPoint.
	    	if (mIdxBeginPoint == null) {
	    		mIdxBeginPoint = index;
	    		
	    		// Set visibility of the cancel button menu true.
	    		mMenu.findItem(R.id.segment_cancel).setVisible(true); 
	    		
	    		// Get MyItemizedIconOverlay overlay from map view.
		    	ItemIconOverlay overlay = (ItemIconOverlay) mMapView.getOverlays().get(ICON_OVERLAY);
		    	overlay.setMarker(index, marker);
		    	mMapView.invalidate();
	    	}
	    	else if (mIdxEndPoint == null) {
	    		mIdxEndPoint = index;
	    		
	    		if (mIdxBeginPoint > mIdxEndPoint) {
	    			mIdxEndPoint = mIdxBeginPoint;
	    			mIdxBeginPoint = index;
	    		}
	    		
	    		for (int i = mIdxBeginPoint + 1; i <= mIdxEndPoint; i++) {
	    			// Get MyItemizedIconOverlay overlay from map view.
	    	    	ItemIconOverlay overlay = (ItemIconOverlay) mMapView.getOverlays().get(ICON_OVERLAY);
	    	    	overlay.setMarker(i, marker);
	    	    	mMapView.invalidate();
	    		}
	    	}
	             
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
	private class ItemIconOverlay extends ItemizedIconOverlay<OverlayItem> {
		/**
		 * The default marker to points that are not marker.
		 */
		protected Drawable mDefaultMarker;
		/**
		 * Map of custom markers.
		 */
		protected Map<Integer, Drawable> mCustomMarkers;
		
	    public ItemIconOverlay(
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
	
	/**
     * Another itemized icon overlay with an special characteristic: this up the Icon (to add
     * segment icon overlay).
     * 
     * @author Román Ginés Martínez Ferrández.
     */
    private class SegmentIconOverlay extends ItemIconOverlay {
		public SegmentIconOverlay(
				List<OverlayItem> pList,
				org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener<OverlayItem> pOnItemGestureListener,
				ResourceProxy pResourceProxy, Drawable marker) {
			super(pList, pOnItemGestureListener, pResourceProxy, marker);
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
	                
	                canvas.drawBitmap(bm, out.x - bm.getWidth() / 2, out.y - bm.getHeight(), null);
	            }
	        }
	    }
    }

    /**
     * Implement the method of the AddSegmentDialog interface.
     * 
     * When the user click on "ok" button from the dialog call this 
     * callback.
     * 
     * This callback show to the user a message say her how segment 
     * creation was. Later put a segment icon overlay, then change
     * the icon points to red and change the option bar.
     */
	@Override
	public void onDialogPositiveClick(String segmentName, Long trackId,
			TrackPoint begin, TrackPoint end) {
		// 1.- Show the toast message to the user.
		if (SegmentUtil.addSegment(getActivity(), segmentName, trackId, begin, end)) {
			Toast.makeText(getActivity(), R.string.segment_created, Toast.LENGTH_LONG).show();
		}
		else {
			Toast.makeText(getActivity(), R.string.fail_segment_created, Toast.LENGTH_LONG).show();
		}
		
		// 2.- Add the new SegmentIconOverlay overlay to the map view.
		List<OverlayItem> segmentOverlays = new ArrayList<OverlayItem>();
		GeoPoint segmentPoint;
		OverlayItem segmentOverlay;
		
        ResourceProxy resourceProxy = new DefaultResourceProxyImpl(getActivity());
        
        segmentPoint = new GeoPoint(mListTrackPoint.get(mIdxBeginPoint).getLat(),
        		             mListTrackPoint.get(mIdxBeginPoint).getLng());
        segmentOverlay = new OverlayItem(String.valueOf(mListTrackPoint.get(mIdxBeginPoint).getId()), 
        		                  String.valueOf(mListTrackPoint.get(mIdxBeginPoint).getId()),
        		                  segmentPoint);
		segmentOverlays.add(segmentOverlay);
		
		Drawable segmentMarker = getResources().getDrawable(R.drawable.segment_overlay);
        int markerWidth = segmentMarker.getIntrinsicWidth();
        int markerHeight = segmentMarker.getIntrinsicHeight();
        segmentMarker.setBounds(0, markerHeight, markerWidth, 0);
        
        SegmentIconOverlay anotherItemizedIconOverlay = new SegmentIconOverlay(segmentOverlays, onSegmentItemGestureListener, resourceProxy, segmentMarker);
		mMapView.getOverlays().add(SEGMENT_OVERLAY, anotherItemizedIconOverlay);
		
		// 3.- Change blue points to red points.
		Drawable marker = getResources().getDrawable(R.drawable.geopoint);
        markerWidth = marker.getIntrinsicWidth();
        markerHeight = marker.getIntrinsicHeight();
        marker.setBounds(0, markerHeight, markerWidth, 0);
		for (int i = mIdxBeginPoint; i <= mIdxEndPoint; i++) {
			// Get MyItemizedIconOverlay overlay from map view.
	    	ItemIconOverlay overlay = (ItemIconOverlay) mMapView.getOverlays().get(ICON_OVERLAY);
	    	overlay.setMarker(i, marker);
	    	mMapView.invalidate();
		}
		
		// 4.- Hide the cancel segment option from option bar menu.
		mIdxBeginPoint = null;
		mIdxEndPoint = null;
		MenuItem item = mMenu.findItem(R.id.segment_cancel);
		if (item != null)
			item.setVisible(false);
	}
}

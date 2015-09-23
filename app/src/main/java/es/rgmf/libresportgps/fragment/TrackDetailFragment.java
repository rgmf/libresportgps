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

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.PathOverlay;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.rgmf.libresportgps.MapActivity;
import es.rgmf.libresportgps.R;
import es.rgmf.libresportgps.TrackEditActivity;
import es.rgmf.libresportgps.common.Utilities;
import es.rgmf.libresportgps.db.orm.Track;
import es.rgmf.libresportgps.db.orm.TrackPoint;
import es.rgmf.libresportgps.view.RouteMapView;

/**
 * This View is created to show the detail information of a Track.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class TrackDetailFragment extends Fragment {
	private static final int PATH_OVERLAY = 0;
	private static final String ARG_TRACK = "arg_track";
	private static final String ARG_TRACK_POINT_LIST = "arg_track_point_list";

	/**
	 * The View. It can be used to access xml elements of this View.
	 */
	private View mRootView;
	/**
	 * The context.
	 */
	private Context mContext;
	/**
	 * The Track to show.
	 */
	private Track mTrack = null;
	/**
	 * The name of the file that contain the track information.
	 */
	protected String mName;
	/**
	 * The list of track points.
	 */
	protected List<TrackPoint> mTrackPointList;
	protected RouteMapView mMapView;
	protected BoundingBoxE6 mBoundingBox;
	
	/**
	 * Create an instance of this class.
	 * 
	 * @return Return the class instance.
	 */
	public static TrackDetailFragment newInstance(Track track, List<TrackPoint> list) {
		TrackDetailFragment fragment = new TrackDetailFragment();
		fragment.mTrack = track;
		fragment.mTrackPointList = list;
		return fragment;
	}

	/**
	 * This method is called when this fragment view is created.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_track_detail, container, false);

		if(getActivity() != null) {
			this.mContext = getActivity().getApplicationContext();
		}

		if (savedInstanceState != null) {
			mTrack = (Track) savedInstanceState.getSerializable(ARG_TRACK);
			mTrackPointList = savedInstanceState.getParcelableArrayList(ARG_TRACK_POINT_LIST);
		}

		setDataView();

		setMapView();

		mRootView.findViewById(R.id.button_over_map).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(mContext, MapActivity.class);
				intent.putExtra("trackId", mTrack.getId());
				intent.putParcelableArrayListExtra("trackPointList", (ArrayList<? extends Parcelable>) mTrackPointList);
				startActivity(intent);
			}
		});
		
		return mRootView;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(ARG_TRACK, mTrack);
		outState.putParcelableArrayList(ARG_TRACK_POINT_LIST, (ArrayList<? extends Parcelable>) mTrackPointList);
	}

	/**
	 * Set data from view.
	 */
	private void setDataView() {
		if(mTrack != null) {
			TextView tvName = (TextView) mRootView.findViewById(R.id.track_edit_name);
			TextView tvDesc = (TextView) mRootView.findViewById(R.id.track_edit_description);
			TextView tvDate = (TextView) mRootView.findViewById(R.id.track_date);
			TextView tvDistance = (TextView) mRootView.findViewById(R.id.track_distance);
			TextView tvActivityTime = (TextView) mRootView.findViewById(R.id.track_activity_time);
			TextView tvMaxEle = (TextView) mRootView.findViewById(R.id.track_max_ele);
			TextView tvMinEle = (TextView) mRootView.findViewById(R.id.track_min_ele);
			TextView tvGainEle = (TextView) mRootView.findViewById(R.id.track_gain_ele);
			TextView tvLossEle = (TextView) mRootView.findViewById(R.id.track_loss_ele);
			TextView tvMaxSpeed = (TextView) mRootView.findViewById(R.id.track_max_speed);
			TextView tvAvgSpeed = (TextView) mRootView.findViewById(R.id.track_avg_speed);
	        ImageView ivLogo = (ImageView) mRootView.findViewById(R.id.track_edit_logo);
			
			this.mName = mTrack.getTitle();
			tvName.setText(mTrack.getTitle());
			tvDesc.setText(mTrack.getDescription());
			tvDate.setText(Utilities.timeStampCompleteFormatter(mTrack.getStartTime()));
			tvDistance.setText(Utilities.distance(mTrack.getDistance()));
			tvActivityTime.setText(Utilities.totalTimeFormatter(mTrack.getActivityTime()));
			tvMaxEle.setText(Utilities.elevation(mTrack.getMaxElevation()));
			tvMinEle.setText(Utilities.elevation(mTrack.getMinElevation()));
			tvGainEle.setText(Utilities.elevation(mTrack.getElevationGain()));
			tvLossEle.setText(Utilities.elevation(mTrack.getElevationLoss()));
			tvMaxSpeed.setText(Utilities.speed(mTrack.getMaxSpeed()));
			tvAvgSpeed.setText(Utilities.avgSpeed(mTrack.getActivityTime(), mTrack.getDistance()));
	        if(mTrack.getSport() != null && mTrack.getSport().getLogo() != null && !mTrack.getSport().getLogo().isEmpty()) {
            	ivLogo.setImageResource(mContext.getResources().getIdentifier(mTrack.getSport().getLogo(), "drawable", mContext.getPackageName()));
	        }
		}
	}

	private void setMapView() {
		// Get and configure the map view.
		mMapView = (RouteMapView) mRootView.findViewById(R.id.openmapview);

		// Get and configure map controller.
		MapController mMapController = (MapController) mMapView.getController();

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
		for (TrackPoint tp : mTrackPointList) {
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
		//mMapView.getController().setCenter(point);

		// The bounding box where the map view will zoom.
		final BoundingBoxE6 bb = new BoundingBoxE6(maxLat, maxLng, minLat, minLng);
		mMapView.setBoundingBoxE6(bb);

		// We neeed the layout of the map was contructed so we listen to it.
		if (mMapView.getScreenRect(null).height() > 0) {
			mMapView.zoomToBoundingBox(bb);
		} else {
			ViewTreeObserver vto1 = mMapView.getViewTreeObserver();
			vto1.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				@SuppressWarnings("deprecation")
				@Override
				public void onGlobalLayout() {
					mMapView.zoomToBoundingBox(bb);
					ViewTreeObserver vto2 = mMapView.getViewTreeObserver();
					if (Build.VERSION.SDK_INT < 16) {
						vto2.removeGlobalOnLayoutListener(this);
					} else {
						vto2.removeOnGlobalLayoutListener(this);
					}
				}
			});
		}

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
	}

	/**
	 * To get "click" on the points from the track.
	 *
	 * We need these events to create segments on map view GeoPoints.
	 */
	ItemizedIconOverlay.OnItemGestureListener<OverlayItem> myOnItemGestureListener = new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
		@Override
		public boolean onItemLongPress(int arg0, OverlayItem arg1) { return false; }

		@Override
		public boolean onItemSingleTapUp(int index, OverlayItem item) { return false; }
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
					GeoPoint in = (GeoPoint) getItem(i).getPoint();

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

	public Track getTrack() { 
		return mTrack; 
	}
	
	public void setTrack(Track t) { 
		mTrack = t;
		// If we change the track we need to change the viewing data.
		setDataView(); 
	}
}

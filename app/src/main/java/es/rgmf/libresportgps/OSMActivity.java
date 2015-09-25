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

package es.rgmf.libresportgps;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import org.osmdroid.bonuspack.overlays.Marker;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.rgmf.libresportgps.common.MsgDialog;
import es.rgmf.libresportgps.common.Services;
import es.rgmf.libresportgps.common.Utilities;
import es.rgmf.libresportgps.db.DBModel;
import es.rgmf.libresportgps.db.orm.Segment;
import es.rgmf.libresportgps.db.orm.SegmentPoint;
import es.rgmf.libresportgps.db.orm.Track;
import es.rgmf.libresportgps.fragment.dialog.AddSegmentDialog;
import es.rgmf.libresportgps.fragment.dialog.AddSegmentDialog.AddSegmentDialogListener;
import es.rgmf.libresportgps.view.RouteMapView;

/**
 * This View is created to show the map information of a Segment.
 * 
 * Through this view you can see the path and create segments
 * clicking two points (begin and end point of the segment).
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class OSMActivity extends Activity implements AddSegmentDialogListener {
	private static final int SCALE_OVERLAY = 0;
	private static final int POINT_OVERLAY = 1;
	private static final int POLYLINE_OVERLAY = 2;

	/**
	 * The osmdroid map view.
	 */
	private RouteMapView mMapView;
	/**
	 * The osmdroid controller.
	 */
	private MapController mMapController;
	/**
	 * The options menu.
	 */
	private Menu mMenu;
	/**
	 * List of polyline points.
	 */
	private List<GeoPoint> mPointList = null;
	/**
	 * The road manager.
	 */
	private RoadManager mRoadManager = null;
	/**
	 * The context
	 */
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_map);

		// The context.
		mContext = this;

		// Back button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Create scale bar.
		ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(this);

		// Get and configure the map view.
		mMapView = (RouteMapView) findViewById(R.id.openmapview);
		mMapView.setBuiltInZoomControls(true);
		mMapView.setMultiTouchControls(true);
		mMapView.getOverlays().add(SCALE_OVERLAY, scaleBarOverlay);

		// Get and configure map controller.
		mMapController = (MapController) mMapView.getController();

		// The road manager.
		mRoadManager = new OSRMRoadManager();

		// The point list.
		mPointList = new ArrayList<GeoPoint>();

		PointOverlay overlay = new PointOverlay(this);
		mMapView.getOverlays().add(POINT_OVERLAY, overlay);







		/******************************* PROBANDO *********************************/
		//new FindSegmentTask().execute("");
		//DBModel.findSegmentInTracks(mContext, 2L);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.segment, menu);
		mMenu = menu;
		return true;
	}

	/**
	 * Clear and reset the map view overlays.
	 */
	private void clearOverlays() {
		mPointList.clear();

		MenuItem menuItem = mMenu.findItem(R.id.segment_cancel);
		if (menuItem != null) {
			menuItem.setVisible(false);
		}

		mMapView.getOverlays().clear();

		ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(this);
		mMapView.getOverlays().add(SCALE_OVERLAY, scaleBarOverlay);

		PointOverlay overlay = new PointOverlay(this);
		mMapView.getOverlays().add(POINT_OVERLAY, overlay);

		mMapView.invalidate();
	}

	/**
	 * Handle the clicked options in this fragment.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				return true;
			/* THE USER CLICKS ON ADD SEGMENT BUTTON ON BUTTON BAR */
			case R.id.segment_add:
				if (mPointList != null && mPointList.size() > 1) {
					AddSegmentDialog dialog = new AddSegmentDialog();
					FragmentManager fm = getFragmentManager();
					dialog.show(fm, "fragment_new_segment_name");
				}
				else {
					MsgDialog.alert(this, R.string.add_segment, getString(R.string.add_segment_hint));
				}

	            return true;

			/* THE USER CLICK ON CANCEL SEGMENT BUTTON ON BUTTON BAR */
			case R.id.segment_cancel:
				clearOverlays();
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * This function is called by AddSegmentDialog when user create and type a segment name.
	 *
	 * It is a method interface implementation.
	 *
	 * @param segmentName The segment name.
	 */
	@Override
	public void onDialogPositiveClick(String segmentName) {
		if (segmentName == null)
			segmentName = Utilities.todayString();

		if (mPointList != null && mPointList.size() > 2) {
			new AddSegmentTask().execute(segmentName);
		}
	}

	/**
	 * Overlay to draw points where clicked user.
	 */
	private class PointOverlay extends Overlay {

		public PointOverlay(Context ctx) {
			super(ctx);
		}

		@Override
		protected void draw(Canvas canvas, MapView mapView, boolean b) {
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e, MapView mapView) {
			if (Services.checkNetworkConnection(mContext)) {
				// Get point from x,y coordinate.
				Projection proj = mapView.getProjection();
				IGeoPoint p = proj.fromPixels((int) e.getX(), (int) e.getY());

				// Add a marker overlay to the map view.
				Marker marker = new Marker(mMapView);
				marker.setIcon(getResources().getDrawable(R.drawable.geopoint));
				GeoPoint geoPoint = new GeoPoint(p.getLatitudeE6(), p.getLongitudeE6());
				marker.setPosition(geoPoint);
				marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

				mPointList.add(geoPoint);
				if (mPointList.size() > 1) {
					Road road = mRoadManager.getRoad((ArrayList<GeoPoint>) mPointList);
					Polyline roadOverlay = RoadManager.buildRoadOverlay(road, mContext);
					mMapView.getOverlays().add(POLYLINE_OVERLAY, roadOverlay);
				} else {
					MenuItem item = mMenu.findItem(R.id.segment_cancel);
					if (item != null)
						item.setVisible(true);
				}

				mMapView.getOverlays().add(marker);
				mMapView.invalidate();
			}
			else {
				MsgDialog.alert(mContext, R.string.add_segment, getString(R.string.add_segment_internet_needed_hint));
			}

			return super.onSingleTapUp(e, mapView);
		}
	}

	/**
	 * This AsyncTask get elevation from all geo points and then add the segmento to the
	 * database.
	 *
	 * We need an AsyncTask because http connection, so user need Internet connection.
	 */
	class AddSegmentTask extends AsyncTask<String, Integer, Boolean> {
		private ProgressDialog dialog = new ProgressDialog(OSMActivity.this);
		private List<SegmentPoint> mSegmentPointList = new ArrayList<SegmentPoint>();
		private String mSegmentName = "";
		private Double mDistance = 0d;
		private Double mStartElevation = 0d;
		private Double mEndElevation = 0d;

		/**
		 * On pre execute.
		 *
		 * Show the waiting dialog.
		 */
		@Override
		protected void onPreExecute() {
			this.dialog.setMessage(getString(R.string.adding_segment) + " " + getString(R.string.please_wait));
			this.dialog.show();
		}

		/**
		 * @param isOk true if was ok
		 */
		@Override
		protected void onPostExecute(Boolean isOk) {
			super.onPostExecute(isOk);

			// Hide the waiting dialog
			if (dialog.isShowing()) {
				dialog.dismiss();
			}

			// Create the segment if background task was ok
			if (!isOk) {
				MsgDialog.alert(mContext, R.string.add_segment, getString(R.string.add_segment_internet_needed_hint));
			}
			else {
				Segment segment = new Segment();
				segment.setName(mSegmentName);
				segment.setDistance(mDistance);
				segment.setElevationGain(mEndElevation - mStartElevation);
				Long segmentId = DBModel.addSegment(mContext, segment, mSegmentPointList);
				if (segmentId != null) {
					new FindSegmentTask().execute(segmentId);
					//MsgDialog.alert(mContext, R.string.add_segment, getString(R.string.add_segment_ok));
				}
				else {
					MsgDialog.alert(mContext, R.string.add_segment, getString(R.string.add_segment_fail));
				}
			}

			// Clear overlays to clean the map
			clearOverlays();
		}

		/**
		 * When user execute this task must pass the longitude and latitude.
		 *
		 * @param params Two params: latitude and longitude.
		 * @return The segment name or null if any error.
		 */
		@Override
		protected Boolean doInBackground(String... params) {
			double result = Double.NaN;
			double longitude, latitude;
			Double prevLng = null;
			Double prevLat = null;
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();

			if (params.length == 1 && params[0] instanceof String) {
				mSegmentName = params[0];

				for (GeoPoint p : mPointList) {
					latitude = p.getLatitude();
					longitude = p.getLongitude();

					String url = "http://maps.googleapis.com/maps/api/elevation/"
							+ "xml?locations=" + String.valueOf(latitude)
							+ "," + String.valueOf(longitude)
							+ "&sensor=true";
					HttpGet httpGet = new HttpGet(url);
					try {
						HttpResponse response = httpClient.execute(httpGet, localContext);
						HttpEntity entity = response.getEntity();
						if (entity != null) {
							InputStream instream = entity.getContent();
							int r = -1;
							StringBuffer respStr = new StringBuffer();
							while ((r = instream.read()) != -1)
								respStr.append((char) r);
							String tagOpen = "<elevation>";
							String tagClose = "</elevation>";
							if (respStr.indexOf(tagOpen) != -1) {
								int start = respStr.indexOf(tagOpen) + tagOpen.length();
								int end = respStr.indexOf(tagClose);
								String value = respStr.substring(start, end);
								result = (double) (Double.parseDouble(value));
							}
							instream.close();
						}
					} catch (ClientProtocolException e) {
						return false;
					} catch (IOException e) {
						return false;
					}

					// Calculate distance and elevation gain
					if (prevLng != null && prevLat != null) {
						mDistance += Utilities.CalculateDistance(prevLat, prevLng, latitude, longitude);
						mEndElevation = result;
					}
					else {
						mStartElevation = result;
					}

					// Prepare next iteration
					prevLng = longitude;
					prevLat = latitude;

					// Add the new segment point
					SegmentPoint sp = new SegmentPoint();
					sp.setLat(p.getLatitude());
					sp.setLng(p.getLongitude());
					sp.setAltitude(result);
					mSegmentPointList.add(sp);
				}
			}
			else {
				return false;
			}

			return true;
		}
	}

	/**
	 * This AsyncTask find tracks that have the segment indicated.
	 */
	class FindSegmentTask extends AsyncTask<Long, Integer, Boolean> {
		private ProgressDialog dialog = new ProgressDialog(OSMActivity.this);
		private List<SegmentPoint> mSegmentPointList = new ArrayList<SegmentPoint>();
		private String mSegmentName = "";
		private Double mDistance = 0d;
		private Double mStartElevation = 0d;
		private Double mEndElevation = 0d;

		/**
		 * On pre execute.
		 *
		 * Show the waiting dialog.
		 */
		@Override
		protected void onPreExecute() {
			this.dialog.setMessage(getString(R.string.finding_segment_track) + " " + getString(R.string.please_wait));
			this.dialog.show();
		}

		/**
		 * @param isOk true if was ok
		 */
		@Override
		protected void onPostExecute(Boolean isOk) {
			super.onPostExecute(isOk);

			// Hide the waiting dialog
			if (dialog.isShowing()) {
				dialog.dismiss();
			}

			if (isOk)
				MsgDialog.alert(mContext, R.string.add_segment, getString(R.string.add_segment_ok));
			else
				MsgDialog.alert(mContext, R.string.add_segment, getString(R.string.add_segment_fail));
		}

		/**
		 * When user execute this task must pass the longitude and latitude.
		 *
		 * @param params Two params: latitude and longitude.
		 * @return The segment name or null if any error.
		 */
		@Override
		protected Boolean doInBackground(Long... params) {
			if (params.length > 0) {
				Map<Long, Track> result = DBModel.findSegmentInTracks(mContext, params[0]);
				if (result != null && result.size() > 0)
					return true;
			}

			return false;
		}
	}
}

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

import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import es.rgmf.libresportgps.adapter.SegmentResultAdapter;
import es.rgmf.libresportgps.common.Utilities;
import es.rgmf.libresportgps.db.DBModel;
import es.rgmf.libresportgps.db.orm.Segment;
import es.rgmf.libresportgps.db.orm.SegmentPoint;
import es.rgmf.libresportgps.db.orm.SegmentTrack;

/**
 * Segment Activity.
 * 
 * Throgh this activity the user can see all segment details.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 *
 */
public class SegmentActivity extends Activity {
	private List<SegmentTrack> mSegmentTrackList = null;
	//private List<String> mTrainingTimeList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_segment);
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		// Get all data passes by fragment.
		Long segmentId = getIntent().getLongExtra("segmentId", 0L);
		Long trackId = getIntent().getLongExtra("trackId", 0L);
		
		// Get all information from database (in a track the user can do 
		// a segment several times.
		//mSegmentTrackList = DBModel.getAllSegmentTrack(this, trackId, segmentId);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.segment, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		/*if (id == R.id.action_settings) {
			return true;
		}*/
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_segment,
					container, false);
			
			// Get the same information to all segment tracks that are inside segment.
			if (mSegmentTrackList != null && mSegmentTrackList.size() > 0) {
				TextView titleText = (TextView) rootView.findViewById(R.id.title_text);
				TextView distanceText = (TextView) rootView.findViewById(R.id.distance_text);
				TextView eleGainText = (TextView) rootView.findViewById(R.id.ele_gain_text);
				TextView gradientText = (TextView) rootView.findViewById(R.id.gradient_text);
				//TextView timeText = (TextView) rootView.findViewById(R.id.time_text);
				//TextView dateText = (TextView) rootView.findViewById(R.id.date_text);

				/*
				SegmentTrack segmentTrack = mSegmentTrackList.get(0);
				SegmentPoint segmentPoint = segmentTrack.getSegmentPoint();
				if (segmentPoint != null) {
					Segment segment = segmentPoint.getSegment();
					if (segment != null) {
						titleText.setText(segment.getName());
						distanceText.setText(Utilities.distance(segment.getDistance()));
						eleGainText.setText(Utilities.elevation(segment.getElevationGain()));
						gradientText.setText(Utilities.gradient(segment.getElevationGain(), segment.getDistance()));
					}
				}
				*/
			}
			
			return rootView;
		}
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			
			// Generate list view with all times in this track.
			displayTimeListView();
		}
		
		private void displayTimeListView() {
			SegmentResultAdapter adapter = new SegmentResultAdapter(getActivity(), mSegmentTrackList);
			ListView listView = (ListView) getView().findViewById(R.id.result_list_view);
			listView.setAdapter(adapter);
			listView.setTextFilterEnabled(true);
			 
			listView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
					 int position, long id) {
				}
			});
			
			
			
			
			//Array list of countries
			/*List<String> urlList = new ArrayList<String>();
			urlList.add("http://www.google.com");
			urlList.add("http://mail.google.com");
			urlList.add("http://maps.google.com");*/
			   
			//create an ArrayAdaptar from the String Array
			/*ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
			    R.layout.list_segment_result, mTrainingTimeList);
			ListView listView = (ListView) getView().findViewById(R.id.time_list_view);*/
			// Assign adapter to ListView
			//listView.setAdapter(dataAdapter);
			   
			//enables filtering for the contents of the given ListView
			/*listView.setTextFilterEnabled(true);
			 
			listView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
					 int position, long id) {
				}
			});*/
		}
	}
}

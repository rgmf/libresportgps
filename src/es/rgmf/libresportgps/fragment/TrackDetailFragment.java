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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import es.rgmf.libresportgps.R;
import es.rgmf.libresportgps.common.Utilities;
import es.rgmf.libresportgps.db.orm.Track;

/**
 * This View is created to show the detail information of a Track.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class TrackDetailFragment extends Fragment {
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
	 * Create an instance of this class.
	 * 
	 * @return Return the class instance.
	 */
	public static TrackDetailFragment newInstance(Track track) {
		TrackDetailFragment fragment = new TrackDetailFragment();
		fragment.mTrack = track;
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
		
		setDataView();
		
		return mRootView;
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
	

	public Track getTrack() { 
		return mTrack; 
	}
	
	public void setTrack(Track t) { 
		mTrack = t;
		// If we change the track we need to change the viewing data.
		setDataView(); 
	}
}

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

import java.util.TreeMap;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import es.rgmf.libresportgps.AltimetryActivity;
import es.rgmf.libresportgps.R;
import es.rgmf.libresportgps.TrackEditActivity;
import es.rgmf.libresportgps.common.Session;
import es.rgmf.libresportgps.common.Utilities;
import es.rgmf.libresportgps.db.DBModel;
import es.rgmf.libresportgps.db.orm.Track;
import es.rgmf.libresportgps.file.FileManager;

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
	 * Key to know where come back from.
	 */
	private static final int TRACK_EDIT_ACTIVITY_BACK = 1;
	
	/**
	 * Create an instance of this class.
	 * 
	 * @return Return the class instance.
	 */
	public static final TrackDetailFragment newInstance() {
		TrackDetailFragment fragment = new TrackDetailFragment();
		/*
		Bundle bundle = new Bundle(1);
		bundle.putInt("a_number", 1);
		fragment.setArguments(bundle);
		*/
		return fragment;
	}
	
	/**
	 * This method is called when this fragment view is created.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_track_detail, container, false);
		
		setHasOptionsMenu(true);

		if(getActivity() != null) {
			this.mContext = getActivity().getApplicationContext();
		}
		
	    Button button = (Button) mRootView.findViewById(R.id.track_detail_button);
	    button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Get all track points of this point and create a map with 
				// distance and elevation information.
				TreeMap<Integer, Float> treeMap = DBModel.getDistEleMap(mContext, mTrack.getId());
				
				// Call the activity.
				Intent intent = new Intent(getActivity(), AltimetryActivity.class);
				intent.putExtra("map", treeMap);
				intent.putExtra("maxX", (float) treeMap.lastKey());
				intent.putExtra("maxY", mTrack.getMaxElevation());
	        	startActivity(intent);
			}
		});
		
		setDataView();
		
		return mRootView;
	}

    /**
	 * This method modifies the options in the bar menu adapting it to this
	 * fragment.
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	    menu.clear();
	    inflater.inflate(R.menu.track_detail, menu);
	}
	
	/**
	 * Handle the clicked options in this fragment.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			/* THE USER CLICKS ON DELETE BUTTON ON BUTTON BAR */
			case R.id.track_detail_delete:
				new AlertDialog.Builder(getActivity())
				.setTitle(R.string.delete_trackfile)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setMessage(getResources().getString(R.string.delete_trackfile_hint))
				.setCancelable(true).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Delete all files in the folder and folder included.
						FileManager.delete(Session.getAppFolder() + "/" + mTrack.getId());
						// Delete the register in the database.
						if(!DBModel.deleteTrack(getActivity(), mTrack.getId()))
							Toast.makeText(getActivity(), R.string.track_was_not_deleted,
									Toast.LENGTH_LONG).show();
						// Go back to tracks list fragment.
						getFragmentManager().popBackStack();
					}
				}).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}).create().show();
	            return true;
	            

			/* THE USER CLICK ON EDIT BUTTON ON BUTTON BAR */
			case R.id.track_detail_edit:
				Intent intent = new Intent(getActivity(), TrackEditActivity.class);
				intent.putExtra("id", mTrack.getId());
                intent.putExtra("title", mTrack.getTitle());
                intent.putExtra("description", mTrack.getDescription());
	        	startActivityForResult(intent, TRACK_EDIT_ACTIVITY_BACK);
				return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * This method is called when activity called (TrackEditActivity) finish and come back here.
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // See which child activity is calling us back.
	    switch (requestCode) {
	        case TRACK_EDIT_ACTIVITY_BACK:
	        	mTrack = DBModel.getTrack(mContext, mTrack.getId());
	        	setDataView();
	        default:
	            break;
	    }
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
			tvDate.setText(Utilities.timeStampCompleteFormatter(mTrack.getFinishTime()));
			tvDistance.setText(Utilities.distance(mTrack.getDistance()));
			tvActivityTime.setText(Utilities.timeStampFormatter(mTrack.getActivityTime()));
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

	/**
	 * @return the track
	 */
	public Track getTrack() {
		return mTrack;
	}

	/**
	 * @param track the track to set
	 */
	public void setTrack(Track track) {
		this.mTrack = track;
	}
}

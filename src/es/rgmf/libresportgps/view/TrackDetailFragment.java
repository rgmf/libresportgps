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

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import es.rgmf.libresportgps.R;
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
	private View rootView;
	/**
	 * The context.
	 */
	private Context context;
	/**
	 * The Track to show.
	 */
	private Track track = null;
	/**
	 * The name of the file that contain the track information.
	 */
	protected String name;
	
	/**
	 * Create an instance of this class.
	 * 
	 * @return Return the class instance.
	 */
	public static final TrackDetailFragment newInstance() {
		TrackDetailFragment fragment = new TrackDetailFragment();
		Bundle bundle = new Bundle(1);
		bundle.putInt("a_number", 1);
		fragment.setArguments(bundle);
		return fragment;
	}
	
	/**
	 * This method is called when this fragment view is created.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_track_detail, container, false);

		if(getActivity() != null) {
			this.context = getActivity().getApplicationContext();
		}
		
		if(track != null) {
			EditText etName = (EditText) rootView.findViewById(R.id.track_edit_name);
			EditText etDesc = (EditText) rootView.findViewById(R.id.track_edit_description);
			TextView tvDate = (TextView) rootView.findViewById(R.id.track_date);
			TextView tvDistance = (TextView) rootView.findViewById(R.id.track_distance);
			TextView tvActivityTime = (TextView) rootView.findViewById(R.id.track_activity_time);
			TextView tvMaxEle = (TextView) rootView.findViewById(R.id.track_max_ele);
			TextView tvMinEle = (TextView) rootView.findViewById(R.id.track_min_ele);
			TextView tvGainEle = (TextView) rootView.findViewById(R.id.track_gain_ele);
			TextView tvLossEle = (TextView) rootView.findViewById(R.id.track_loss_ele);
			TextView tvMaxSpeed = (TextView) rootView.findViewById(R.id.track_max_speed);
			TextView tvAvgSpeed = (TextView) rootView.findViewById(R.id.track_avg_speed);
			Button bDelete = (Button) rootView.findViewById(R.id.track_del_button);
			
			this.name = track.getTitle();
			etName.setText(track.getTitle());
			etDesc.setText(track.getDescription());
			tvDate.setText(Utilities.timeStampCompleteFormatter(track.getFinishTime()));
			tvDistance.setText(Utilities.distance(track.getDistance()));
			tvActivityTime.setText(Utilities.timeStampFormatter(track.getActivityTime()));
			tvMaxEle.setText(Utilities.elevation(track.getMaxElevation()));
			tvMinEle.setText(Utilities.elevation(track.getMinElevation()));
			tvGainEle.setText(Utilities.elevation(track.getElevationGain()));
			tvLossEle.setText(Utilities.elevation(track.getElevationLoss()));
			tvMaxSpeed.setText(Utilities.speed(track.getMaxSpeed()));
			tvAvgSpeed.setText(Utilities.avgSpeed(track.getActivityTime(), track.getDistance()));
			
			// This is called when user edits the name field.
			etName.setOnEditorActionListener(new OnEditorActionListener() {
			    @Override
			    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			        boolean handled = false;
			        if (actionId == EditorInfo.IME_ACTION_SEND) {
			        	// Change the file name.
			        	FileManager.rename(
			        			Session.getAppFolder() + "/" + track.getId(), 
			        			name + ".gpx", 
			        			Session.getAppFolder() + "/" + track.getId(), 
			        			v.getText().toString() + ".gpx"
			        			);
			        	// Update the database title field.
			        	DBModel.updateTrackName(getActivity(), track.getId(), v.getText().toString());
			            handled = true;
			        }
			        return handled;
			    }
			});
			
			// This is called when user edits the description field.
			etDesc.setOnEditorActionListener(new OnEditorActionListener() {
			    @Override
			    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			        boolean handled = false;
			        if (actionId == EditorInfo.IME_ACTION_SEND) {
			        	// Update the database title field.
			        	DBModel.updateTrackDescription(getActivity(), track.getId(), v.getText().toString());
			            handled = true;
			        }
			        return handled;
			    }
			});
			
			// This is called when user clicks on delete button.
			bDelete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					new AlertDialog.Builder(getActivity())
					.setTitle(R.string.delete_trackfile)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setMessage(getResources().getString(R.string.delete_trackfile_hint))
					.setCancelable(true).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Delete the file.
							FileManager.delete(
									Session.getAppFolder() + "/" + track.getId(),
									name + ".gpx"
									);
							// Delete the register in the database.
							if(!DBModel.deleteTrack(getActivity(), track.getId()))
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
				}
			});
		}
		
		return rootView;
	}

	/**
	 * @return the track
	 */
	public Track getTrack() {
		return track;
	}

	/**
	 * @param track the track to set
	 */
	public void setTrack(Track track) {
		this.track = track;
	}
}

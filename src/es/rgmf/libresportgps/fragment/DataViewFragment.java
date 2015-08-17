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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import es.rgmf.libresportgps.R;
import es.rgmf.libresportgps.common.Session;
import es.rgmf.libresportgps.common.Utilities;
import es.rgmf.libresportgps.data.TrackFactory;
import es.rgmf.libresportgps.db.DBModel;
import es.rgmf.libresportgps.db.orm.Track;

/**
 * This View is created to show data from GPS (the basic information from GPS:
 * distance, speed, average speed...).
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class DataViewFragment extends AbstractViewFragment {
	/**
	 * The View. It can be used to access xml elements of this View.
	 */
	private View rootView;
	/**
	 * The Context.
	 */
	private Context context;

	/**
	 * Create an instance of this class.
	 * 
	 * @return Return the class instance.
	 */
	public static final DataViewFragment newInstance() {
		DataViewFragment fragment = new DataViewFragment();
		return fragment;
	}

	/**
	 * This method is called when View is created.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_data_view, container,
				false);

		setHasOptionsMenu(true);

		if (getActivity() != null) {
			this.context = getActivity().getApplicationContext();
		}

		// If there is Session then it loads the data.
		if (Session.isTrackingStarted()) {
			((TextView) rootView.findViewById(R.id.current_speed)).setText("0");
			((TextView) rootView.findViewById(R.id.average_speed))
					.setText(String.format("%.2f",
							((Session.getDistance() / 1000) / (Session
									.getActivityTimeStamp() / 1000 / 60 / 60))));
			((TextView) rootView.findViewById(R.id.max_speed)).setText(String
					.format("%.1f", Session.getMaxSpeed()));
			((TextView) rootView.findViewById(R.id.distance)).setText(String
					.format("%.2f", Session.getDistance() / 1000));
			((TextView) rootView.findViewById(R.id.start_time))
					.setText(Utilities.timeStampSecondsFormatter(Session
							.getStartTimeStamp()));
			((TextView) rootView.findViewById(R.id.activity_time))
					.setText(Utilities.timeStampSecondsFormatter(Session
							.getActivityTimeStamp()));
		}

		// Set on play/pause click button.
		ImageView icPlayPause = (ImageView) rootView
				.findViewById(R.id.ic_play_pause);
		if (Session.isTracking())
			icPlayPause.setImageResource(R.drawable.ic_pause);
		icPlayPause.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ImageView icPlayPause = (ImageView) rootView
						.findViewById(R.id.ic_play_pause);

				// If it is tracking then it sets fields in Session object and
				// it sets
				// the button play.
				if (Session.isTracking()) {
					Session.setTracking(false);
					Session.setOpenTrkseg(true);
					Session.setLastLocation(null); // Begin to compute from the user will be when play is click.
					icPlayPause.setImageResource(R.drawable.ic_play);
					// Otherwise it sets the button if the GPS is ready.
				} else {
					if (Session.isGpsReady()) {
						// If the activity have not started then it creates the
						// register in database
						// before the tracking was started. Also, it saves the
						// track identifier in
						// the Session so it has the track that it will be
						// update in database while
						// activity is started.
						if (!Session.isTrackingStarted()) {
							long idTrack = DBModel.createTrack(context,
									Session.getFileName());
							if (idTrack != -1) {
								Session.setTrackId(idTrack);
							} else {
								Toast.makeText(context,
										R.string.db_error_to_create_track,
										Toast.LENGTH_LONG).show();
								return;
							}
						}
						Session.setTracking(true);
						Session.setTrackingStarted(true);
						icPlayPause.setImageResource(R.drawable.ic_pause);
					} else if (Session.isGpsEnabled()) {
						Toast.makeText(context, R.string.waiting_gps,
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(context, R.string.gps_disabled,
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		});

		// Set on stop click button.
		ImageView icStop = (ImageView) rootView.findViewById(R.id.ic_stop);
		icStop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (Session.isTrackingStarted()) {
					new AlertDialog.Builder(rootView.getContext())
							.setTitle(R.string.stop_activity)
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setMessage(
									getResources().getString(
											R.string.stop_activity_question))
							.setCancelable(true)
							.setPositiveButton(android.R.string.yes,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// Before reset Session we have to
											// update Track table to set
											// recording to 0 (false)
											// and update all information to
											// close recording and tracking.
											Track track = new Track();
											track.setId(Session.getTrackId());
											track.setTitle(Session
													.getFileName());
											track.setDescription("");
											track.setDistance((float) Session
													.getDistance());
											track.setStartTime(Session
													.getStartTimeStamp());
											track.setActivityTime(Session
													.getActivityTimeStamp());
											track.setFinishTime(System
													.currentTimeMillis());
											track.setMaxSpeed(Session
													.getMaxSpeed());
											track.setMaxElevation((float) Session
													.getMaxAltitude());
											track.setMinElevation((float) Session
													.getMinAltitude());
											track.setElevationGain((float) Session
													.getAltitudeGain());
											track.setElevationLoss((float) Session
													.getAltitudeLoss());

											DBModel.endRecordingTrack(context,
													Session.getTrackId(), Track.ENDED_TRACK, track);

											// If the tracking is stopped then
											// we need to reset the Session, the
											// FileFactory and
											// the TrackFactory to reload files
											// from folder.
											Session.reset();
											TrackFactory.reset();
											((TextView) rootView
													.findViewById(R.id.current_speed))
													.setText("-");
											((TextView) rootView
													.findViewById(R.id.average_speed))
													.setText("-");
											((TextView) rootView
													.findViewById(R.id.max_speed))
													.setText("-");
											((TextView) rootView
													.findViewById(R.id.distance))
													.setText("-");
											((TextView) rootView
													.findViewById(R.id.start_time))
													.setText("--:--:--");
											((TextView) rootView
													.findViewById(R.id.activity_time))
													.setText("--:--:--");
											((ImageView) rootView
													.findViewById(R.id.ic_play_pause))
													.setImageResource(R.drawable.ic_play);
											Toast.makeText(context,
													R.string.activity_finished,
													Toast.LENGTH_SHORT).show();
										}
									})
							.setNegativeButton(android.R.string.no,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.cancel();
										}
									}).create().show();
				} else {
					Toast.makeText(context, R.string.activity_not_started,
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		return rootView;
	}

	/**
	 * This method is called when a Location is changed and updates the
	 * information of this view fragment using Session and Location data.
	 */
	@Override
	public void onLocationUpdate(Location loc) {
		// Current Speed.
		TextView currentSpeed = (TextView) rootView
				.findViewById(R.id.current_speed);
		currentSpeed.setText(String.format("%.0f", 
				loc.getSpeed() * 3.6f)); // From m/s to km/h

		// Max. Speed.
		TextView maxSpeed = (TextView) rootView.findViewById(R.id.max_speed);
		maxSpeed.setText(String.format("%.1f", Session.getMaxSpeed()));

		// Distance.
		TextView distance = (TextView) rootView.findViewById(R.id.distance);
		distance.setText(String.format("%.2f",
				Session.getDistance() / 1000.0f)); // From m to km.

		// Average Speed.
		TextView avgSpeed = (TextView) rootView
				.findViewById(R.id.average_speed);
		double averageSpeed = 0;
		long totalTime = Session.getActivityTimeStamp();
		if (totalTime != 0)
			averageSpeed = Session.getDistance() / (totalTime / 1000d);
		avgSpeed.setText(String.format("%.1f", averageSpeed * 3.6f));

		// Start time.
		TextView startTime = (TextView) rootView.findViewById(R.id.start_time);
		startTime.setText(Utilities.timeStampSecondsFormatter(Session
				.getStartTimeStamp()));

		// Activity time.
		TextView activityTime = (TextView) rootView
				.findViewById(R.id.activity_time);
		activityTime.setText(Utilities.timeStampSecondsFormatter(Session
				.getActivityTimeStamp()));

		// GPS Status.
		TextView accuracy = (TextView) rootView.findViewById(R.id.gps_accuracy);
		TextView status = (TextView) rootView.findViewById(R.id.gps_status);
		accuracy.setText(String.format("%.0f", loc.getAccuracy()));
		status.setText(String.format("%d / %d", Session.getSatellitesInUsed(),
				Session.getSatellitesInView()));
	}
	
	/**
	 * This fragment have not menu items so we have to clear the menu.
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    menu.clear();
		super.onCreateOptionsMenu(menu, inflater);
	}
}

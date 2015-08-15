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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ListFragment;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ListView;
import android.widget.Toast;
import es.rgmf.libresportgps.R;
import es.rgmf.libresportgps.adapter.TrackListAdapter;
import es.rgmf.libresportgps.common.Session;
import es.rgmf.libresportgps.data.TrackListHead;
import es.rgmf.libresportgps.db.DBModel;
import es.rgmf.libresportgps.db.orm.Track;
import es.rgmf.libresportgps.file.FileFactory;
import es.rgmf.libresportgps.file.FileManager;
import es.rgmf.libresportgps.file.reader.GpxReader;
import es.rgmf.libresportgps.fragment.dialog.FileDialog;

/**
 * This View is created to show the list of tracks.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class TrackListFragment extends ListFragment {
	private static final String POSITION_SELECTED = "position_selected";
	private static final int NONE_SELECTED = -5;
	
	/**
	 * This interface must be implemented by the activity that contain this
	 * fragment.
	 * 
	 * It is used to communication between this fragment and its activity
	 * container when user click on list element.
	 * 
	 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
	 */
	public interface OnTrackListSelectedListener {
		public void onTrackSelected(Track track);
		public void onYearSelected(int year);
		public void onMonthSelected(int year, int month);
	}

	/**
	 * This fragment can deliver messages to the activity by calling
	 * onTrackSelected (see interface definition) using mCallback instance of
	 * OnTrackSelectedListener interface (see interface definition).
	 */
	OnTrackListSelectedListener mSelectedCallback;

	/**
	 * Callback interface through which the fragment will report the task's
	 * progress and results back to the Activity.
	 */
	public static interface ProgressCallbacks {
		void onPreExecute();

		void onProgressUpdate(int percent);

		void onCancelled();

		void onPostExecute();
	}

	private ProgressCallbacks mProgressCallback;
	private ProgressBarTask mProgressBarTask;

	private ArrayList<Track> mTracks;

	private Context mContext;
	
	private TrackListAdapter mAdapter;
	
	private int mPosition = NONE_SELECTED;

	/**
	 * Create an instance of this class.
	 * 
	 * @return Return the class instance.
	 */
	public static final TrackListFragment newInstance() {
		TrackListFragment fragment = new TrackListFragment();
		return fragment;
	}

	/**
	 * See fragment lifecycle to understand when this method is called.
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mSelectedCallback = (OnTrackListSelectedListener) activity;
			mProgressCallback = (ProgressCallbacks) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnTrackSelectedListener");
		}
	}

	/**
	 * Called when the fragment's activity has been created and this fragment's
	 * view hierarchy instantiated.
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Retain this fragment across configuration changes.
		//setRetainInstance(true);
	}

	/**
	 * This method is called when this fragment view is created.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setHasOptionsMenu(true);

		// Get all tracks information.
		mTracks = DBModel.getTracks(getActivity());

		// Create values to add to the adapter (headers and values).
		mContext = inflater.getContext();
		mAdapter = new TrackListAdapter(mContext);
		int year = -1; // To force the first time cal.get(Calendar.YEAR != year
		int month = -1; // To force the first time cal.get(Calendar.MONTH != moth
		for (Track track : mTracks) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(track.getStartTime());
			if (cal.get(Calendar.YEAR) != year) {
				year = cal.get(Calendar.YEAR);
				TrackListHead head = new TrackListHead(TrackListHead.TYPE_YEAR, cal);
				mAdapter.add(head);
			}
			if (cal.get(Calendar.MONTH) != month) {
				month = cal.get(Calendar.MONTH);
				TrackListHead head = new TrackListHead(TrackListHead.TYPE_MONTH, cal);
				mAdapter.add(head);
			}
			mAdapter.add(track);
		}
		setListAdapter(mAdapter);
		
		//return super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.fragment_track_list, container, false);
	}
	
	/**
	 * When view is created.
	 */
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		// Enabling batch contextual actions in the ListView.
		final ListView listView = getListView();
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		
		// AbsListView.MultiChoiceModeListener interface.
		listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
			
		    @Override
		    public void onItemCheckedStateChanged(ActionMode mode, int position,
		                                          long id, boolean checked) {
		        // Here you can do something when items are selected/de-selected,
		        // such as update the title in the CAB.
		    	if (mAdapter.getItem(position) instanceof Track) {
			    	mAdapter.toggleSelection(position);
			    	mode.setTitle(mAdapter.getSelectedIds().size() + " Selected");
		    	}
		    	else if (mAdapter.getSelectedIds().size() == 0) {
		    		mode.finish();
		    	}
		    }

		    @Override
		    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		        // Respond to clicks on the actions in the CAB
		        switch (item.getItemId()) {
		            case R.id.tracklist_delete:
		            	/*new AlertDialog.Builder(getActivity())
						.setTitle(R.string.delete_trackfile)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setMessage(getResources().getString(R.string.delete_tracksfile_selected_hint))
						.setCancelable(true).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {*/
								// Calls getSelectedIds method from ListViewAdapter Class
								SparseBooleanArray selected = mAdapter.getSelectedIds();
								// Captures all selected ids with a loop
								for (int i = (selected.size() - 1); i >= 0; i--) {
									if (selected.valueAt(i)) {
										Object selectedItem = mAdapter.getItem(selected.keyAt(i));
										if (selectedItem instanceof Track) {
											FileManager.delete(Session.getAppFolder() + "/" + ((Track) selectedItem).getId());
											if (!DBModel.deleteTrack(getActivity(), ((Track) selectedItem).getId())) {
												Toast.makeText(getActivity(), R.string.track_was_not_deleted + "(" + ((Track) selectedItem).getTitle() +")",
														Toast.LENGTH_LONG).show();
											}
											else {
												mAdapter.remove(selectedItem);
											}
										}
									}
								}
							/*}
						}).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
							}
						}).create().show();*/

		                mode.finish(); // Action picked, so close the CAB
		                return true;
		            default:
		                return false;
		        }
		    }

		    @Override
		    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		        // Inflate the menu for the CAB
		        MenuInflater inflater = mode.getMenuInflater();
		        inflater.inflate(R.menu.track_list_context_menu, menu);
		        return true;
		    }

		    @Override
		    public void onDestroyActionMode(ActionMode mode) {
		        // Here you can make any necessary updates to the activity when
		        // the CAB is removed. By default, selected items are deselected/unchecked.
		    	mAdapter.removeSelection();
		    }

		    @Override
		    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		        // Here you can perform updates to the CAB due to
		        // an invalidate() request
		        return false;
		    }
		});
	}

	/**
	 * From Android Develop Documentation:
	 * 
	 * This method will be called when an item in the list is selected.
	 * Subclasses should override. Subclasses can call
	 * getListView().getItemAtPosition(position) if they need to access the data
	 * associated with the selected item.
	 * 
	 * Parameters l The ListView where the click happened v The view that was
	 * clicked within the ListView position The position of the view in the list
	 * id The row id of the item that was clicked
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// Calls super.
		super.onListItemClick(l, v, position, id);

		if (getListView().getItemAtPosition(position) instanceof Track) {
			mPosition = position;
			Track track = (Track) (getListView().getItemAtPosition(position));
			mSelectedCallback.onTrackSelected(track);
		}
		else if (getListView().getItemAtPosition(position) instanceof TrackListHead) {
			TrackListHead head = (TrackListHead) (getListView().getItemAtPosition(position));
			switch (head.getType()) {
			case TrackListHead.TYPE_YEAR:
			    mSelectedCallback.onYearSelected(head.getYearNumber());
			    break;
			case TrackListHead.TYPE_MONTH:
			    mSelectedCallback.onMonthSelected(head.getYearNumber(), head.getMonthNumber());
			    break;
			}
		}
	}

	/**
	 * This method modifies the options in the bar menu adapting it to this
	 * fragment.
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		menu.clear();
		inflater.inflate(R.menu.add, menu);
	}

	/**
	 * Handle the clicked options in this fragment.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_add:
			File mPath = new File(Environment.getExternalStorageDirectory()
					+ "//DIR//");
			FileDialog fileDialog = new FileDialog(getActivity(), mPath);
			fileDialog.setFileEndsWith(".gpx");
			fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
				public void fileSelected(final File file) {
					mProgressBarTask = new ProgressBarTask(file);
					mProgressBarTask.execute();
				}
			});
			// fileDialog.addDirectoryListener(new
			// FileDialog.DirectorySelectedListener() {
			// public void directorySelected(File directory) {
			// Log.d(getClass().getName(), "selected dir " +
			// directory.toString());
			// }
			// });
			// fileDialog.setSelectDirectoryOption(false);
			fileDialog.showDialog();
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * A progress bar task to perform the loading of gpx files in background
	 * work and proxies progress updates and results back to the Activity.
	 * 
	 * Note that we need to check if the callbacks are null in each method in
	 * case they are invoked after the Activity's and Fragment's onDestroy()
	 * method have been called.
	 */
	private class ProgressBarTask extends AsyncTask<Void, Integer, Void> {
		private File mFile;

		public ProgressBarTask(File file) {
			mFile = file;
		}

		@Override
		protected void onPreExecute() {
			if (mProgressCallback != null) {
				mProgressCallback.onPreExecute();
			}
		}

		/**
		 * Note that we do NOT call the callback object's methods directly from
		 * the background thread, as this could result in a race condition.
		 */
		@Override
		protected Void doInBackground(Void... ignore) {
			GpxReader gpxReader = new GpxReader();
			Track track = new Track();

			gpxReader.loadFile(mFile.toString());

			track.setTitle(mFile.getName());
			track.setDistance(new Float(gpxReader.getDistance()));
			track.setMaxSpeed(new Float(gpxReader.getSpeed().getMax()));
			track.setMaxElevation(new Float(gpxReader.getElevation().getMax()));
			track.setMinElevation(new Float(gpxReader.getElevation().getMin()));
			track.setElevationGain(new Float(gpxReader.getElevation().getGain()));
			track.setElevationLoss(new Float(gpxReader.getElevation().getLoss()));
			track.setActivityTime(gpxReader.getActivityTime());
			track.setStartTime(gpxReader.getStartTime());
			track.setFinishTime(gpxReader.getFinishTime());
			/*
			track.setStartTime(mFile.lastModified()
					- gpxReader.getActivityTime());
			track.setFinishTime(mFile.lastModified);
			*/

			long trackId = DBModel.createTrack(getActivity(), track);
			DBModel.addTrackPoints(getActivity(), trackId,
					gpxReader.getTrackPoints());
			
			// Save gpx file.
			String folderName = FileFactory.createFolderIfNotExists(String.valueOf(trackId));
			try {
				FileFactory.copyFile(mFile, folderName);
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... percent) {
			if (mProgressCallback != null) {
				mProgressCallback.onProgressUpdate(percent[0]);
			}
		}

		@Override
		protected void onCancelled() {
			if (mProgressCallback != null) {
				mProgressCallback.onCancelled();
			}
		}

		@Override
		protected void onPostExecute(Void ignore) {
			if (mProgressCallback != null) {
				mProgressCallback.onPostExecute();
			}
		}
	}
}

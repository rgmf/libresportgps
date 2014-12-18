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

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import es.rgmf.libresportgps.R;
import es.rgmf.libresportgps.adapter.TrackListAdapter;
import es.rgmf.libresportgps.db.DBModel;
import es.rgmf.libresportgps.db.orm.Track;
import es.rgmf.libresportgps.file.reader.GpxReader;
import es.rgmf.libresportgps.view.dialog.FileDialog;

/**
 * This View is created to show the list of tracks the application
 * have in his external storage folder.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class TrackListFragment extends ListFragment {
	/**
	 * This interface must be implemented by the activity that contain
	 * this fragment.
	 * 
	 * It is used to communication between this fragment and its activity
	 * container when user click on list element.
	 * 
	 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
	 */
    public interface OnTrackSelectedListener {
        public void onTrackSelected(Track track);
    }
    
    /**
	 * This fragment can deliver messages to the activity by calling
	 * onTrackSelected (see interface definition) using mCallback instance
	 * of OnTrackSelectedListener interface (see interface definition).
	 */
	OnTrackSelectedListener mSelectedCallback;
	
	/**
	 * Callback interface through which the fragment will report the
	 * task's progress and results back to the Activity.
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
	
	private ProgressDialog mProgressDialog;
	
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
	 * See fragment lifecycle to understand when this method is
	 * called.
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		// This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mSelectedCallback = (OnTrackSelectedListener) activity;
            mProgressCallback = (ProgressCallbacks) activity;
        } catch(ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTrackSelectedListener");
        }
	}
	
	/**
	 * Called when the fragment's activity has been created and this 
	 * fragment's view hierarchy instantiated.
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// Retain this fragment across configuration changes.
	    setRetainInstance(true);
	}
	
	/**
	 * This method is called when this fragment view is created.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		
		// Get all tracks information.
		mTracks = DBModel.getTracks(getActivity());
		
		TrackListAdapter adapter = new TrackListAdapter(inflater.getContext(), mTracks);
		setListAdapter(adapter);
		
		if(mProgressDialog != null) {
			mProgressDialog.show();
		}
		
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	
	/**
	 * From Android Develop Documentation:
	 * 
	 * This method will be called when an item in the list is selected. Subclasses should override.
	 * Subclasses can call getListView().getItemAtPosition(position) if they need to access the data 
	 * associated with the selected item.
	 * 
	 * Parameters
	 * l 			The ListView where the click happened
	 * v 			The view that was clicked within the ListView
	 * position 	The position of the view in the list
	 * id 			The row id of the item that was clicked 
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// Calls super.
		super.onListItemClick(l, v, position, id);
		
		Track track = (Track) (getListView().getItemAtPosition(position));
		mSelectedCallback.onTrackSelected(track);
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
		switch(item.getItemId()) {
		case R.id.menu_add:
			File mPath = new File(Environment.getExternalStorageDirectory() + "//DIR//");
	        FileDialog fileDialog = new FileDialog(getActivity(), mPath);
	        fileDialog.setFileEndsWith(".gpx");
	        fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
	            public void fileSelected(final File file) {
	            	mProgressBarTask = new ProgressBarTask(file);
	            	mProgressBarTask.execute();
	            }
	        });
	        //fileDialog.addDirectoryListener(new FileDialog.DirectorySelectedListener() {
	        //  public void directorySelected(File directory) {
	        //      Log.d(getClass().getName(), "selected dir " + directory.toString());
	        //  }
	        //});
	        //fileDialog.setSelectDirectoryOption(false);
	        fileDialog.showDialog();
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * A progress bar task to perform the loading of gpx files in
	 * background work and proxies progress updates and results back 
	 * to the Activity.
	 *
	 * Note that we need to check if the callbacks are null in each
	 * method in case they are invoked after the Activity's and
	 * Fragment's onDestroy() method have been called.
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
	     * Note that we do NOT call the callback object's methods
	     * directly from the background thread, as this could result 
	     * in a race condition.
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
			track.setActivityTime(gpxReader.getActivityTime());
			track.setStartTime(mFile.lastModified() - gpxReader.getActivityTime());
			track.setFinishTime(mFile.lastModified());
			
			DBModel.createTrack(getActivity(), track);
			
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

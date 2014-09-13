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

import java.util.ArrayList;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import es.rgmf.libresportgps.adapter.TrackListAdapter;
import es.rgmf.libresportgps.db.DBModel;
import es.rgmf.libresportgps.db.orm.Track;

/**
 * This View is created to show the list of tracks the application
 * have in his external storage folder.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class TrackListFragment extends ListFragment {
	/**
	 * This fragment can deliver messages to the activity by calling
	 * onTrackSelected (see interface definition) using mCallback instance
	 * of OnTrackSelectedListener interface (see interface definition).
	 */
	OnTrackSelectedListener mCallback;
	
	private ArrayList<Track> tracks;
	
	/**
	 * Create an instance of this class.
	 * 
	 * @return Return the class instance.
	 */
	public static final TrackListFragment newInstance() {
		TrackListFragment fragment = new TrackListFragment();
		Bundle bundle = new Bundle(1);
		bundle.putInt("a_number", 1);
		fragment.setArguments(bundle);
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
            mCallback = (OnTrackSelectedListener) activity;
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
	}
	
	/**
	 * This method is called when this fragment view is created.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Get all tracks information.
		tracks = DBModel.getTracks(getActivity());
		
		TrackListAdapter adapter = new TrackListAdapter(inflater.getContext(), tracks);
		setListAdapter(adapter);
		
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
		mCallback.onTrackSelected(track);
	}
	
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

}

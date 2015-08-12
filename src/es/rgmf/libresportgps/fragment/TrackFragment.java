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

import java.util.List;
import java.util.Vector;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import es.rgmf.libresportgps.R;
import es.rgmf.libresportgps.TrackEditActivity;
import es.rgmf.libresportgps.common.Session;
import es.rgmf.libresportgps.db.DBModel;
import es.rgmf.libresportgps.db.orm.Track;
import es.rgmf.libresportgps.db.orm.TrackPoint;
import es.rgmf.libresportgps.file.FileManager;

/**
 * This View is created to show the detail information of a Track.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class TrackFragment extends Fragment {
    public static final String ARG_SELECTED_TAB = "selected_tab";
    public static final String ARG_TRACK_ID = "track_id";
    
    /**
	 * Key to know where come back from.
	 */
	private static final int TRACK_EDIT_ACTIVITY_BACK = 1;
    
	/**
	 * The "tabs" identifiers.
	 */
    public static final int DETAIL = 0;
    public static final int ALTIMETRY = 1;
    public static final int MAP = 2;
    
    /**
     * The track.
     */
    private Track mTrack;

    /**
     * Where "tabs" will be loaded.
     */
    private ViewPager mPager;
    
    /**
	 * Create an instance of this class.
	 * 
	 * @return Return the class instance.
	 */
	public static TrackFragment newInstance(Track track) {
		TrackFragment fragment = new TrackFragment();
		fragment.mTrack = track;
		return fragment;
	}
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_pager, container, false);
        mPager = (ViewPager) root.findViewById(R.id.pager);
        
        setHasOptionsMenu(true);
        
        if (savedInstanceState != null) {
        	long trackId = savedInstanceState.getLong(ARG_TRACK_ID);
        	mTrack = DBModel.getTrack(getActivity(), trackId);
        }
        
        // Get list of track points needed to the profile and the map.
        Log.v("Init", "Init");
        List<TrackPoint> listTrackPoints = DBModel.getTrackPoints(getActivity(), mTrack.getId());
        Log.v("Finish", "Finish");
        
        // Create list of fragments and add them to the list.
        List<Fragment> fragments = new Vector<Fragment>();
        
        // 0.- TrackDetailFragment.
        fragments.add(TrackDetailFragment.newInstance(mTrack));
        
        // 1.- AltimetryFragment.
        if (listTrackPoints.size() > 0)
        	fragments.add(AltimetryFragment.newInstance(listTrackPoints,
        			listTrackPoints.get(listTrackPoints.size() - 1).getDistance(),
        			mTrack.getMinElevation(),
        			mTrack.getMaxElevation()));
        else
        	fragments.add(AltimetryFragment.newInstance(listTrackPoints, 0f, 0f, 0f));
        
        // 2.- MapFragment.
        fragments.add(MapFragment.newInstance(mTrack.getId(), listTrackPoints));
        
        TrackPagerAdapter pagerAdapter = new TrackPagerAdapter(getFragmentManager(), getResources(), fragments);
        mPager.setAdapter(pagerAdapter);
        
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            int tab = getArguments().getInt(ARG_SELECTED_TAB);
            mPager.setCurrentItem(tab, false);
        }
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putLong(ARG_TRACK_ID, mTrack.getId());
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
                if (mTrack.getSport() != null && mTrack.getSport().getLogo() != null && 
                		!mTrack.getSport().getLogo().isEmpty()) {
                	intent.putExtra("logo", mTrack.getSport().getLogo());
                }
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
	        	mTrack = DBModel.getTrack(getActivity(), mTrack.getId());
	        	TrackDetailFragment fragment = (TrackDetailFragment) ((TrackPagerAdapter) mPager.getAdapter()).getItem(DETAIL);
	        	fragment.setTrack(mTrack);
	        	mPager.getAdapter().notifyDataSetChanged();
	        default:
	            break;
	    }
	}

    /**
     * FragmentStatePagerAdapter.
     * 
     * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
     */
    public class TrackPagerAdapter extends FragmentStatePagerAdapter  {
        Resources mResources;
        List<Fragment> mFragments;
        FragmentManager mFragmentManager;
        
        public TrackPagerAdapter(FragmentManager fm, Resources resources, List<Fragment> fragments) {
        	super(fm);
        	this.mFragmentManager = fm;
        	this.mFragments = fragments;
        	this.mResources = resources;
        }

        @Override
        public Fragment getItem(int position) {
        	return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case DETAIL:
                    return mResources.getString(R.string.detail);
                case ALTIMETRY:
                    return mResources.getString(R.string.altimetry);
                case MAP:
                    return mResources.getString(R.string.track_on_map);
                default:
                    return super.getPageTitle(position);
            }
        }
        
        /**
         * Tricky code to force to call getItem method in this class
         * so we force to refresh data.
         */
        @Override
        public int getItemPosition(Object object) {
        	return POSITION_NONE;
        }
    }
}

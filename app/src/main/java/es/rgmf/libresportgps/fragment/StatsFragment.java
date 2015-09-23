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
import java.util.Map;
import java.util.Vector;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import es.rgmf.libresportgps.R;
import es.rgmf.libresportgps.common.Utilities;
import es.rgmf.libresportgps.data.Stats;
import es.rgmf.libresportgps.db.DBModel;

/**
 * This View is created to show the stats information of the
 * activities. They can be showed/filtered by year/month and/or sport.
 * 
 * The month only works with year.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class StatsFragment extends Fragment {
	private static final String ARG_YEAR = "arg_year";
	private static final String ARG_MONTH = "arg_month";
	private static final String ARG_SPORT = "arg_sport";
	
	private static final String ARG_SELECTED_TAB = "arg_tab";
	
	public static final int NONE = -1;

	/**
	 * The "tabs" identifiers.
	 */
    public static final int SUMMARY = 0;
    
    /**
     * The number of the year according to Calendar.
     */
    public int mYear = NONE;
    
    /**
     * The number of the month according to Calendar.
     */
    public int mMonth = NONE;
    
    /**
     * The id of the sport.
     */
    public int mSport = NONE;

    /**
     * Where "tabs" will be loaded.
     */
    private ViewPager mPager;
    
    /**
	 * Create an instance of this class.
	 * 
	 * @param year The year for the stats or NONE for all years.
	 * @param month The month of the year or NONE for all months of the year.
	 * @param sport The sport for the stats or NONE for all sports.
	 * @return Return the class instance.
	 */
	public static StatsFragment newInstance(int year, int month, int sport) {
		StatsFragment fragment = new StatsFragment();
		fragment.mYear = year;
		fragment.mMonth = month;
		fragment.mSport = sport;
		return fragment;
	}
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_pager, container, false);
        mPager = (ViewPager) root.findViewById(R.id.pager);
        
        setHasOptionsMenu(true);
        
        if (savedInstanceState != null) {
        	mYear = savedInstanceState.getInt(ARG_YEAR);
        	mMonth = savedInstanceState.getInt(ARG_MONTH);
        	mSport = savedInstanceState.getInt(ARG_SPORT);
        }
        
        Integer y = null;
        Integer m = null;
        Integer s = null;
        // Check mYear and mMonth.
        if (mYear != NONE) {
        	y = mYear;
        	if (mMonth != NONE)
        		m = mMonth;
        }
        // Check the sport.
        if (mSport != NONE)
        	m = mSport;
        
        // Get stats by year, month and/or sport.
        Map<Long, Stats> statsBySport = DBModel.getStats(getActivity(), y, m, s);
        
        // Create list of fragments and add them to the list.
        List<Fragment> fragments = new Vector<Fragment>();
        
        // 0.- TrackDetailFragment.
        fragments.add(StatsSummaryFragment.newInstance(statsBySport));
        
        StatsPagerAdapter pagerAdapter = new StatsPagerAdapter(getFragmentManager(), getResources(), fragments);
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
    	outState.putInt(ARG_YEAR, mYear);
    	outState.putInt(ARG_MONTH, mMonth);
    	outState.putInt(ARG_SPORT, mSport);
    }
    
    /**
	 * This method modifies the options in the bar menu adapting it to this
	 * fragment.
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	    menu.clear();
	    //inflater.inflate(R.menu.track_detail, menu);
	}

    /**
     * StatsPagerAdapter.
     * 
     * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
     */
    public class StatsPagerAdapter extends FragmentStatePagerAdapter  {
        Resources mResources;
        List<Fragment> mFragments;
        FragmentManager mFragmentManager;
        
        public StatsPagerAdapter(FragmentManager fm, Resources resources, List<Fragment> fragments) {
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
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case SUMMARY:
                	String headStr = mResources.getString(R.string.stats_summary);
                	if (mYear != NONE) {
                		headStr += " (";
                		if (mMonth != NONE) 
                			headStr += Utilities.getNameOfCalendarMonth(mMonth) + " - ";
                		headStr+= mYear + ")";
                	}
                    return headStr;
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

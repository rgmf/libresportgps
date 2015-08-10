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

import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import es.rgmf.libresportgps.R;
import es.rgmf.libresportgps.adapter.StatsListAdapter;
import es.rgmf.libresportgps.data.Stats;

/**
 * This View is created to show the detail information of a Track.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class StatsSummaryFragment extends ListFragment {
	/**
	 * The context.
	 */
	private Context mContext;
	/**
	 * The Track to show.
	 */
	private Map<Long, Stats> mStats = null;
	/**
	 * The name of the file that contain the track information.
	 */
	protected String mName;
	
	/**
	 * Create an instance of this class.
	 * 
	 * @return Return the class instance.
	 */
	public static StatsSummaryFragment newInstance(Map<Long, Stats> stats) {
		StatsSummaryFragment fragment = new StatsSummaryFragment();
		fragment.mStats = stats;
		return fragment;
	}
	
	/**
	 * This method is called when this fragment view is created.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(getActivity() != null) {
			this.mContext = getActivity().getApplicationContext();
		}
		
		StatsListAdapter adapter = new StatsListAdapter(mContext);
		if (savedInstanceState == null) {
			for (Map.Entry<Long, Stats> s : mStats.entrySet()) {
				adapter.add(s.getValue());
			}
		}
		setListAdapter(adapter);
		
		return inflater.inflate(R.layout.fragment_stats_list, container, false);
	}
}

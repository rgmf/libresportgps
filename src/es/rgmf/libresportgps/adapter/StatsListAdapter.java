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

package es.rgmf.libresportgps.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import es.rgmf.libresportgps.R;
import es.rgmf.libresportgps.common.Utilities;
import es.rgmf.libresportgps.data.Stats;
import es.rgmf.libresportgps.db.orm.Sport;

/**
 * This adapter is used to load the stats summary information of
 * all sports.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class StatsListAdapter extends ArrayAdapter<Stats> {
	private ArrayList<Stats> mValues;
	private Context mContext;
	private ViewHolder mHolder;
	
	/**
	 * The ViewHolder.
	 * 
	 * RecyclerView.Adapter implementations should subclass ViewHolder and add 
	 * fields for caching potentially expensive findViewById(int) results.
	 * 
	 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
	 */
	static class ViewHolder {
	    ImageView ivLogo;
        TextView tvName;
        TextView tvWorkouts;
        TextView tvDistance;
        TextView tvMaxDistance;
        TextView tvGain;
        TextView tvMaxGain;
        TextView tvTime;
        TextView tvMaxTime; 
	}

	/**
	 * Constructor with items.
	 * 
	 * @param context The context.
	 * @param items The values (activities and headers).
	 */
	public StatsListAdapter(Context context, ArrayList<Stats> items) {
		super(context, R.layout.fragment_row_stats_list, items);
		this.mContext = context;
		this.mValues = items;
	}
	
	/**
	 * Default constructor without data.
	 * 
	 * @param context The context.
	 */
	public StatsListAdapter(Context context) {
		super(context, R.layout.fragment_row_stats_list);
		this.mContext = context;
		this.mValues = new ArrayList<Stats>();
	}
	
	/**
	 * This method add a value to the adapter.
	 */
	@Override
	public void add(Stats object) {
		super.add(object);
		mValues.add(object);
	}

	/**
	 * getView override.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
		    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    convertView = inflater.inflate(R.layout.fragment_row_stats_list, parent, false);
		    
	        mHolder = new ViewHolder();
		    mHolder.ivLogo = (ImageView) convertView.findViewById(R.id.sport_logo);
		    mHolder.tvName = (TextView) convertView.findViewById(R.id.sport_name);
		    mHolder.tvWorkouts = (TextView) convertView.findViewById(R.id.workouts_value);
		    mHolder.tvDistance = (TextView) convertView.findViewById(R.id.distance_value);
		    mHolder.tvMaxDistance = (TextView) convertView.findViewById(R.id.max_distance_value);
		    mHolder.tvGain = (TextView) convertView.findViewById(R.id.gain_value);
		    mHolder.tvMaxGain = (TextView) convertView.findViewById(R.id.max_gain_value);
		    mHolder.tvTime = (TextView) convertView.findViewById(R.id.time_value);
		    mHolder.tvMaxTime = (TextView) convertView.findViewById(R.id.max_time_value);
		    
		    convertView.setTag(mHolder);
		}
		else {
			mHolder = (ViewHolder) convertView.getTag();
		}
	        
	    Stats stats = (Stats) mValues.get(position);
	    Sport sport = stats.getSport();
	    if (sport != null && sport.getLogo() != null && !sport.getLogo().isEmpty())
	    	mHolder.ivLogo.setImageResource(mContext.getResources().getIdentifier(sport.getLogo(), "drawable", mContext.getPackageName()));
	    else
	    	mHolder.ivLogo.setImageResource(R.drawable.unknown);
	        
	    if (sport != null)
	    	mHolder.tvName.setText(sport.getName());
	    else
	    	mHolder.tvName.setText("Unknown");
	    
	    mHolder.tvWorkouts.setText(String.valueOf(stats.getWorkouts()));
	        
	    mHolder.tvDistance.setText(Utilities.distance(stats.getDistance()));
	    mHolder.tvMaxDistance.setText(Utilities.distance(stats.getMaxDistance()));
        
	    mHolder.tvGain.setText(Utilities.gain(stats.getGain()));
	    mHolder.tvMaxGain.setText(Utilities.gain(stats.getMaxGain()));
        
	    mHolder.tvTime.setText(Utilities.timeStampSecondsFormatter(stats.getTime()));
	    mHolder.tvMaxTime.setText(Utilities.timeStampSecondsFormatter(stats.getMaxTime()));
	     
	    return convertView;
	}
}

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
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import es.rgmf.libresportgps.R;
import es.rgmf.libresportgps.common.Utilities;
import es.rgmf.libresportgps.data.TrackListHead;
import es.rgmf.libresportgps.db.orm.Sport;
import es.rgmf.libresportgps.db.orm.Track;

/**
 * This adapter is used to load the list of user tracks (activities).
 * 
 * This adapter show activities and two types of headers: years and months. 
 * In this way, the activities are order by years/month.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class TrackListAdapter extends ArrayAdapter<Object> {
	private ArrayList<Object> values;
	private Context mContext;
	private SparseBooleanArray mSelectedItemsIds;

	/**
	 * Constructor with items.
	 * 
	 * @param context The context.
	 * @param items The values (activities and headers).
	 */
	public TrackListAdapter(Context context, ArrayList<Object> items) {
		super(context, R.layout.fragment_row_track_list, items);
		this.mContext = context;
		this.values = items;
		this.mSelectedItemsIds = new SparseBooleanArray();
	}
	
	/**
	 * Default constructor without data.
	 * 
	 * @param context The context.
	 */
	public TrackListAdapter(Context context) {
		super(context, R.layout.fragment_row_track_list);
		this.mContext = context;
		this.values = new ArrayList<Object>();
		this.mSelectedItemsIds = new SparseBooleanArray();
	}
	
	/**
	 * This method add a value or header in the adapter.
	 */
	@Override
	public void add(Object object) {
		super.add(object);
		values.add(object);
	}

	/**
	 * getView override.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// It is a track (activity).
		if (values.get(position) instanceof Track) {
		    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    convertView = inflater.inflate(R.layout.fragment_row_track_list, parent, false);
		    
	        // Now we can fill the layout with the right values
		    ImageView ivLogo = (ImageView) convertView.findViewById(R.id.list_logo);
	        TextView tvName = (TextView) convertView.findViewById(R.id.list_name);
	        TextView tvDate = (TextView) convertView.findViewById(R.id.list_date);
	        
	        Track track = (Track) values.get(position);
	        Sport sport = track.getSport();
	        if(sport != null && sport.getLogo() != null && !sport.getLogo().isEmpty())
	        	ivLogo.setImageResource(mContext.getResources().getIdentifier(sport.getLogo(), "drawable", mContext.getPackageName()));
	        else
	        	ivLogo.setImageResource(R.drawable.unknown);
	        
	        tvName.setText(track.getTitle());
	        tvDate.setText(Utilities.timeStampCompleteFormatter(track.getFinishTime()));
		}
		// It is a header (year or month header).
		else if (values.get(position) instanceof TrackListHead) {
			TrackListHead head = (TrackListHead) values.get(position);
			switch (head.getType()) {
			case TrackListHead.TYPE_YEAR:
			    LayoutInflater inflaterYear = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			    convertView = inflaterYear.inflate(R.layout.fragment_year_head_track_list, parent, false);
			    
			    TextView tvYearHead = (TextView) convertView.findViewById(R.id.year_head_text);
			    tvYearHead.setText(head.getValue());
			    break;
			case TrackListHead.TYPE_MONTH:
			    LayoutInflater inflaterMonth = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			    convertView = inflaterMonth.inflate(R.layout.fragment_month_head_track_list, parent, false);
			    
			    TextView tvMonthHead = (TextView) convertView.findViewById(R.id.month_head_text);
			    tvMonthHead.setText(head.getValue());
			    break;
			}
		}
	     
	    return convertView;
	}
	
	public void toggleSelection(int position) {
		selectView(position, !mSelectedItemsIds.get(position));
	}
	
	public void selectView(int position, boolean value) {
		if (value)
			mSelectedItemsIds.put(position, value);
		else
			mSelectedItemsIds.delete(position);
		notifyDataSetChanged();
	}
	
	public SparseBooleanArray getSelectedIds() {
		return mSelectedItemsIds;
	}
	
	@Override
	public void remove(Object object) {
		values.remove(object);
		notifyDataSetChanged();
	}
}

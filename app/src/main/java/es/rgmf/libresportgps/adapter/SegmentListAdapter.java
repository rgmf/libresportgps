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

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.slf4j.helpers.Util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import es.rgmf.libresportgps.R;
import es.rgmf.libresportgps.common.Utilities;
import es.rgmf.libresportgps.data.TrackListHead;
import es.rgmf.libresportgps.db.orm.Segment;
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
public class SegmentListAdapter extends ArrayAdapter<Segment> {
	/**
	 * Values of the adapter.
	 */
	private List<Segment> mValues = new ArrayList<>();
	/**
	 * The context.
	 */
	private Context mContext;
	/**
	 * Array with information on values selected.
	 */
	private SparseBooleanArray mSelectedItemsIds;

	/**
	 * Private view holder class.
	 *
	 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
	 */
	private class ViewHolder {
		TextView tvName;
		TextView tvDistance;
		TextView tvElevationGain;
		TextView tvGradient;
	}

	/**
	 * Constructor with items.
	 *
	 * @param context The context.
	 * @param items The values (activities and headers).
	 */
	public SegmentListAdapter(Context context, List<Segment> items) {
		super(context, R.layout.fragment_row_segment_list);
		this.mContext = context;
		this.mValues = items;
		this.mSelectedItemsIds = new SparseBooleanArray();
	}

	/**
	 * getView override.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		// It is a track (activity).
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// First let's verify the convertView is not null
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.fragment_row_segment_list, parent, false);
			holder = new ViewHolder();
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}

		// Create the holder.
		holder.tvName = (TextView) convertView.findViewById(R.id.segment_name);
		holder.tvDistance = (TextView) convertView.findViewById(R.id.segment_distance);
		holder.tvElevationGain = (TextView) convertView.findViewById(R.id.segment_elevation_gain);
		holder.tvGradient = (TextView) convertView.findViewById(R.id.segment_gradient);

		// Now we can fill the layout with the right values.
		Segment segment = mValues.get(position);
		holder.tvName.setText(segment.getName());
		holder.tvDistance.setText(Utilities.distance(segment.getDistance()));
		holder.tvElevationGain.setText(Utilities.elevation(segment.getElevationGain()));
		holder.tvGradient.setText(Utilities.gradient(segment.getElevationGain(), segment.getDistance()));
	     
	    return convertView;
	}

	@Override
	public int getCount() {
        /*
        int count = mValues.size();
        return count > 0 ? count - 1 : count;
        */
		return mValues.size();
	}

	@Override
	public Segment getItem(int i) {
		return mValues.get(i);
	}

	@Override
	public long getItemId(int i) {
		return mValues.indexOf(getItem(i));
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
	
	public void removeSelection() {
		mSelectedItemsIds = new SparseBooleanArray();
		notifyDataSetChanged();
	}
	
	@Override
	public void remove(Segment object) {
		super.remove(object);
		mValues.remove(object);
		this.notifyDataSetChanged();
	}
}

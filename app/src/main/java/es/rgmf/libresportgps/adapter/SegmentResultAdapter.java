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

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import es.rgmf.libresportgps.R;
import es.rgmf.libresportgps.common.Utilities;
import es.rgmf.libresportgps.db.orm.SegmentTrack;

/**
 * This adapter is used to load the list of results from a
 * segment.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class SegmentResultAdapter extends ArrayAdapter<SegmentTrack> {
	private List<SegmentTrack> mValues;
	private Context mContext;
	
	public SegmentResultAdapter(Context context, List<SegmentTrack> items) {
		super(context, R.layout.list_segment_result, items);
		this.mContext = context;
		this.mValues = items;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// First let's verify the convertView is not null
	    if (convertView == null) {
	        // This a new view we inflate the new layout
	        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        convertView = inflater.inflate(R.layout.list_segment_result, parent, false);
	    }
        // Now we can fill the layout with the right values
	    TextView tvTime = (TextView) convertView.findViewById(R.id.time_text);
        TextView tvAvgSpeed = (TextView) convertView.findViewById(R.id.avg_speed_text);
        TextView tvDate = (TextView) convertView.findViewById(R.id.date_text);

		/*
        SegmentTrack segmentTrack = mValues.get(position);
        if (segmentTrack != null) {
        	tvTime.setText(Utilities.timeStampSecondsFormatter(segmentTrack.getTime()));
        	if (segmentTrack.getSegmentPoint() != null && segmentTrack.getSegmentPoint().getSegment() != null)
        		tvAvgSpeed.setText(Utilities.avgSpeed(segmentTrack.getTime(), segmentTrack.getSegmentPoint().getSegment().getDistance()));
        	if (segmentTrack.getTrack() != null)
        		tvDate.setText(Utilities.timeStampCompleteFormatter(segmentTrack.getTrack().getStartTime()));
        }
        */
	     
	    return convertView;
	}
}

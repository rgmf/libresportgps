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
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import es.rgmf.libresportgps.R;
import es.rgmf.libresportgps.common.Utilities;
import es.rgmf.libresportgps.db.orm.Sport;
import es.rgmf.libresportgps.db.orm.Track;

/**
 * This adapter is used to load the list of tracks the user have
 * in application folder.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class TrackListAdapter extends ArrayAdapter<Track> {
	private ArrayList<Track> values;
	private Context context;
	
	public TrackListAdapter(Context context, ArrayList<Track> items) {
		super(context, R.layout.fragment_row_track_list, items);
		this.context = context;
		this.values = items;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// First let's verify the convertView is not null
	    if (convertView == null) {
	        // This a new view we inflate the new layout
	        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        convertView = inflater.inflate(R.layout.fragment_row_track_list, parent, false);
	    }
        // Now we can fill the layout with the right values
	    ImageView ivLogo = (ImageView) convertView.findViewById(R.id.list_logo);
        TextView tvName = (TextView) convertView.findViewById(R.id.list_name);
        TextView tvDate = (TextView) convertView.findViewById(R.id.list_date);
        
        Track track = values.get(position);
        Sport sport = track.getSport();
        if(sport != null && sport.getLogo() != null && !sport.getLogo().isEmpty()) {
			Bitmap logoBitmap = Utilities.loadBitmapEfficiently(sport.getLogo(), 
	    			(int) context.getResources().getDimension(R.dimen.icon_size_small),
	    			(int) context.getResources().getDimension(R.dimen.icon_size_small));
	        ivLogo.setImageBitmap(logoBitmap);
        }
        else
        	ivLogo.setImageResource(R.drawable.unknown);
        tvName.setText(track.getTitle());
        tvDate.setText(Utilities.timeStampCompleteFormatter(track.getFinishTime()));
	     
	    return convertView;
	}
}

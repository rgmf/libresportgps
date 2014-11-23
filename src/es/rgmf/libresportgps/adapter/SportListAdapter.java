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
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import es.rgmf.libresportgps.R;
import es.rgmf.libresportgps.common.Utilities;
import es.rgmf.libresportgps.db.orm.Sport;
import es.rgmf.libresportgps.db.orm.Track;

/**
 * This adapter is used to load the list of sports.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class SportListAdapter extends BaseAdapter {
	private ArrayList<Sport> mValues;
	private Context mContext;

    /**
	 * Private view holder class.
	 *
	 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
	 */
    private class ViewHolder {
    	TextView tvId;
    	ImageView ivLogo;
        TextView tvName;
    }

	public SportListAdapter(Context context, ArrayList<Sport> items) {
		this.mContext = context;
		this.mValues = items;
	}

    @Override
	public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// First let's verify the convertView is not null
	    if (convertView == null) {
	        convertView = inflater.inflate(R.layout.spinner_sport, parent, false);
	    }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Create the holder.
        holder = new ViewHolder();
        holder.tvId = (TextView) convertView.findViewById(R.id.spinner_sport_id);
        holder.ivLogo = (ImageView) convertView.findViewById(R.id.spinner_sport_image);
        holder.tvName = (TextView) convertView.findViewById(R.id.spinner_sport_name);

        // Now we can fill the layout with the right values
        Sport sport = mValues.get(position);
        holder.tvId.setText(String.valueOf(sport.getId()));
        if(sport != null) {
        	if(sport.getLogo() != null) {
        		if(!sport.getLogo().isEmpty()) {
	        		Bitmap logoBitmap = Utilities.loadBitmapEfficiently(sport.getLogo(), 
	    	    			(int) mContext.getResources().getDimension(R.dimen.icon_size_small),
	    	    			(int) mContext.getResources().getDimension(R.dimen.icon_size_small));
	    	        holder.ivLogo.setImageBitmap(logoBitmap);
        		}
        	}
        }
        holder.tvName.setText(sport.getName());
	     
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
    public Object getItem(int i) {
        return mValues.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mValues.indexOf(getItem(i));
    }
}

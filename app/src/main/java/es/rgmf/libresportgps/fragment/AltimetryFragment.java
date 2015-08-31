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

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import es.rgmf.libresportgps.R;
import es.rgmf.libresportgps.db.orm.TrackPoint;
import es.rgmf.libresportgps.view.GraphicView;

/**
 * This Activity is created to show the altimetry (profile) of a track.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class AltimetryFragment extends Fragment {
	private static final int SMALL_GAIN = 300;
	private static final int MEDIUM_GAIN = 600;
	private static final int LARGE_GAIN = 1000;

	private static final String ARG_TRACK_POINT_LIST = "track_point_list";
	private static final String ARG_MAX_X = "max_x";
	private static final String ARG_MAX_Y = "max_y";
	private static final String ARG_MIN_Y = "min_y";
	private static final String ARG_SPLIT_X = "split_x";
	private static final String ARG_SPLIT_Y = "split_y";
	private static final String ARG_BEGIN_Y = "begin_y";
	private static final String ARG_X_UNIT = "x_unit";

	/**
	 * Root view.
	 */
	private View mRootView;
	/**
	 * The draw view.
	 */
	private GraphicView mGraphicView;
	/**
	 * Map of distance associated with the altitude.
	 */
	private List<TrackPoint> mList = new ArrayList<TrackPoint>();
	/**
	 * Maximum's distance.
	 */
	private float mMaxX = 0f;
	/**
	 * Maximum's altitude approximate above to split y.
	 */
	private float mMaxY = 0;
	/**
	 * Minimum's altitude.
	 */
	private float mMinY = 0f;
	/**
	 * Split distance (x axe) in meters.
	 */
	private int mSplitX;
	/**
	 * Split altitude (y axe) in meters.
	 */
	private int mSplitY;
	/**
	 * If minimum altitude is great then we begin more up.
	 */
	private int mBeginY;
	/**
	 * The x unit.
	 */
	private GraphicView.Unit mXUnit;
	
	/**
	 * Create a new instance of this class.
	 * 
	 * @param list The list of track points.
	 * @param maxX The maximum distance.
	 * @param minY The minimum elevation.
	 * @param maxY THe maximum elevation.
	 * @return
	 */
	public static AltimetryFragment newInstance(List<TrackPoint> list, Float maxX, Float minY, Float maxY) {
		AltimetryFragment f = new AltimetryFragment();
		f.mList = list;
		f.mMaxX = maxX;
		
		// Calculate the distance split to the graphic.
		if (f.mMaxX < 1000) {
			f.mSplitX = (int) f.mMaxX;
			f.mXUnit = GraphicView.Unit.METER;
		}
		else if (f.mMaxX < 10000) {
			f.mSplitX = (Math.round(f.mMaxX) - (Math.round(f.mMaxX) % 1000)) / 10;
			f.mXUnit = GraphicView.Unit.KM;
		}
		else if (f.mMaxX <= 40000) {
			f.mSplitX = (Math.round(f.mMaxX) - (Math.round(f.mMaxX) % 10000)) / 10;
			f.mXUnit = GraphicView.Unit.KM;
		}
		else {
			f.mSplitX = f.splitNumber(Math.round(f.mMaxX));
			f.mXUnit = GraphicView.Unit.KM;
		}
		
		// Calculate the altitude split to the graphic.
		int diff = Math.round(maxY - minY);
		if (diff <= 300) {
			f.mSplitY = 50;
		}
		else if (diff <= 500) {
			f.mSplitY = 100;
		}
		else if (diff <= 800) {
			f.mSplitY = 150;
		}
		else if (diff <= 1500) {
			f.mSplitY = 200;
		}
		else if (diff <= 2000) {
			f.mSplitY = 250;
		}
		else if (diff <= 2500) {
			f.mSplitY = 400;
		}
		else if (diff <= 4000) {
			f.mSplitY = 500;
		}
		else {
			f.mSplitY = 1000;
		}
		
		// Corregimos la altitud máxima que será:
		// El múltiplo más cercano por arriba a maxY
		f.mMaxY = Float.valueOf((Math.round(Math.round(maxY) / f.mSplitY) + 1) * f.mSplitY);
		
		// Además, si hay poca altura se añaden splits para que el gráfico no sea exagerado.
		if (maxY - minY < SMALL_GAIN) {
			f.mMaxY += (f.mSplitY * 5);
		}
		else if (maxY - minY < MEDIUM_GAIN) {
			f.mMaxY += (f.mSplitY * 3);
		}
		else if (maxY - minY < LARGE_GAIN) {
			f.mMaxY += (f.mSplitY);
		}
		
		// Si sobra mucho por arriba corregimos para que se aprecie la altitud hecha.
		int round = Math.round(minY / f.mSplitY);
		if (round > 0)
			f.mBeginY = f.mSplitY * (Math.round(minY / f.mSplitY) - 1);
		else
			f.mBeginY = 0;
		f.mMaxY -= f.mBeginY;
		
		return f;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_altimetry, container, false);

		if (savedInstanceState != null) {
			mList = savedInstanceState.getParcelableArrayList(ARG_TRACK_POINT_LIST);
			mMaxX = savedInstanceState.getFloat(ARG_MAX_X);
			mMaxY = savedInstanceState.getFloat(ARG_MAX_Y);
			mMinY = savedInstanceState.getFloat(ARG_MIN_Y);
			mSplitX = savedInstanceState.getInt(ARG_SPLIT_X);
			mSplitY = savedInstanceState.getInt(ARG_SPLIT_Y);
			mBeginY = savedInstanceState.getInt(ARG_BEGIN_Y);
			mXUnit = (GraphicView.Unit) savedInstanceState.getSerializable(ARG_X_UNIT);
		}

        // Draw with ordered map.
	    mGraphicView = new GraphicView(getActivity(), mList,
	    		mMaxX, mMaxY, mSplitX, mSplitY, mBeginY,
				mXUnit, GraphicView.Unit.METER);
	    mGraphicView.setBackgroundColor(Color.WHITE);
	    
	    RelativeLayout rl = (RelativeLayout) mRootView.findViewById(R.id.rl_drawing_parent);
	    rl.addView(mGraphicView);
		
		return mRootView;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putParcelableArrayList(ARG_TRACK_POINT_LIST, (ArrayList<? extends Parcelable>) mList);
		outState.putFloat(ARG_MAX_X, mMaxX);
		outState.putFloat(ARG_MAX_Y, mMaxY);
		outState.putFloat(ARG_MIN_Y, mMinY);
		outState.putInt(ARG_SPLIT_X, mSplitX);
		outState.putInt(ARG_SPLIT_Y, mSplitY);
		outState.putInt(ARG_BEGIN_Y, mBeginY);
		outState.putSerializable(ARG_X_UNIT, mXUnit);
	}

	/**
	 * Algorithm:
	 * a = number / 10
	 * b = a / 5
	 * return b * 5
	 * 
	 * The number must be greater or equal than 40000 meters or the
	 * result will be zero. So only works with distances greater or
	 * equal than 40000 meters.
	 * 
	 * @param number Distance in meters.
	 * @return the split number of meters.
	 */
	private int splitNumber(int number) {
		int a, b;
		
		number = number / 1000; // to km
		
		a = Math.round((float) number / 10f);
		b = Math.round((float) a / 5f);
		
		return b * 5 * 1000; // in meters
	}
}

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

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import es.rgmf.libresportgps.R;

/**
 * This Activity is created to show the altimetry of a track.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class AltimetryFragment extends Fragment {
	/**
	 * The draw view.
	 */
	private DrawView mDrawView;
	/**
	 * Map of distance associated with the altitude.
	 */
	private Map<Integer, Float> mMap = new TreeMap<Integer, Float>();
	/**
	 * Maximum's distance.
	 */
	private Float mMaxX = 0f;
	/**
	 * Maximum's altitude.
	 */
	private Float mMaxY = 0f;
	
	/**
	 * Create a new instance of this class.
	 * 
	 * @param map
	 * @param maxX
	 * @param maxY
	 * @return
	 */
	public static AltimetryFragment newInstance(Map<Integer, Float> map, Float maxX, Float maxY) {
		AltimetryFragment f = new AltimetryFragment();
		f.mMap = map;
		f.mMaxX = maxX;
		f.mMaxY = maxY;
		return f;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_altimetry, container, false);
		
		// Wee need to re-order the map because after de-serialize the map can be 
        // unordered.
        TreeMap<Integer, Float> treeMap = new TreeMap<Integer, Float>();
        treeMap.putAll(mMap);

        // Draw with ordered map.
	    mDrawView = new DrawView(getActivity(), treeMap, 
	    		mMaxX, mMaxY,
	    		getString(R.string.elevation), 
	    		getString(R.string.distance));
	    mDrawView.setBackgroundColor(Color.WHITE);
	    
	    RelativeLayout rl = (RelativeLayout) rootView.findViewById(R.id.rl_drawing_parent);
	    rl.addView(mDrawView);
		
		return rootView;
	}
	
	/**
	 * This class draw pars of values in a coordinate.
	 * 
	 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
	 */
	class DrawView extends View {
		private static final int PADDING_LEFT = 50;
		private static final int PADDING_RIGHT = 50;
		private static final int PADDING_TOP = 50;
		private static final int PADDING_BOTTOM = 50;
		
		private Map<Integer, Float> mMap;
		
		private Float mMaxX;
		private Float mMaxY;
		
		private String mXCoordinateText;
		private String mYCoordinateText;
	
        private Paint axesPaint = new Paint();
        private Paint linePaint = new Paint();

        public DrawView(Context context, Map<Integer, Float> map,
        		Float maxX, Float maxY,
        		String xCoordinateText, String yCoordinateText) {
            super(context);
            axesPaint.setColor(Color.BLUE);
            linePaint.setColor(Color.GREEN);
            this.mMap = map;
            this.mMaxX = maxX;
            this.mMaxY = maxY;
            this.mXCoordinateText = xCoordinateText;
            this.mYCoordinateText = yCoordinateText;
        }
        
        @Override
        public void onDraw(Canvas canvas) {
        	super.onDraw(canvas);
        	
        	int height = canvas.getHeight();
        	int width = canvas.getWidth();
        	
        	Iterator it;
        	
        	Integer prevX = null, x, key;
        	Float prevY = null, y;
        	
        	// Draw texts coordinates.
        	canvas.drawText(mXCoordinateText, PADDING_LEFT, PADDING_TOP - 5, axesPaint);
        	canvas.drawText(mYCoordinateText, width - (PADDING_RIGHT * 2), height - (PADDING_BOTTOM - (PADDING_BOTTOM / 2)), axesPaint);
        	
        	// Draw coordinate axes.
            canvas.drawLine(PADDING_LEFT, height - PADDING_BOTTOM,
            		PADDING_LEFT, PADDING_TOP,
            		axesPaint);
            canvas.drawLine(PADDING_LEFT, height - PADDING_BOTTOM,
            		width - PADDING_RIGHT, height - PADDING_BOTTOM,
            		axesPaint);
            
            // Draw references altitudes.
            canvas.drawText("0", PADDING_LEFT / 2, height - PADDING_BOTTOM, axesPaint);
            canvas.drawText(Float.toString(mMaxY), PADDING_LEFT / (Float.toString(mMaxY).length()), PADDING_TOP, axesPaint);
            
            // Draw lines from tree map.
            it = mMap.keySet().iterator();
            while(it.hasNext()) {
            	key = (Integer) it.next();
            	x = (int) ((key * (width - PADDING_LEFT - PADDING_RIGHT)) / mMaxX);
            	y = (float) ((mMap.get(key) * ((float) (height - PADDING_TOP - PADDING_BOTTOM))) / mMaxY);
            	if(prevX != null && prevY != null) {
            		canvas.drawLine(
            				(float) (prevX + PADDING_LEFT),
            				(float) (height - PADDING_TOP - prevY), // (height - PADDING_TOP - PADDING_BOTTOM) - prevY + PADDING_BOTTOM ==
            				                                        // height - PADDING_TOP - prevY
            				(float) (x + PADDING_LEFT),
            				(float) (height - PADDING_TOP - y),     // (height - PADDING_TOP - PADDING_BOTTOM) - y + PADDING_BOTTOM ==
            														// height - PADDING_TOP - y
            				linePaint);
            	}
            	prevX = x;
        		prevY = y;
            }
        }
	}
}

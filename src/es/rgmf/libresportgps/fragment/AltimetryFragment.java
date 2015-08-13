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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import es.rgmf.libresportgps.R;
import es.rgmf.libresportgps.db.orm.TrackPoint;

/**
 * This Activity is created to show the altimetry (profile) of a track.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class AltimetryFragment extends Fragment {
	private static final int SMALL_GAIN = 300;
	private static final int MEDIUM_GAIN = 600;
	private static final int LARGE_GAIN = 1000;
	
	/**
	 * The draw view.
	 */
	private DrawView mDrawView;
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
		if (f.mMaxX < 50000) {
			f.mSplitX = Math.round(f.mMaxX / 10f);
		}
		else {
			f.mSplitX = f.splitNumber(Math.round(f.mMaxX));
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
		f.mBeginY = f.mSplitY * (Math.round(minY / f.mSplitY) - 1);
		f.mMaxY -= f.mBeginY;
		
		return f;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_altimetry, container, false);

        // Draw with ordered map.
	    mDrawView = new DrawView(getActivity(), mList,
	    		mMaxX, mMaxY,
	    		getString(R.string.elevation), 
	    		getString(R.string.distance));
	    mDrawView.setBackgroundColor(Color.WHITE);
	    
	    RelativeLayout rl = (RelativeLayout) rootView.findViewById(R.id.rl_drawing_parent);
	    rl.addView(mDrawView);
		
		return rootView;
	}
	
	/**
	 * Algorithm:
	 * a = number / 10
	 * b = a / 5
	 * return b * 5
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
	
	/**
	 * This class draw pars of values in a coordinate.
	 * 
	 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
	 */
	class DrawView extends View {
		private static final int PADDING_LEFT = 50;
		private static final int PADDING_RIGHT = 10;
		private static final int PADDING_TOP = 50;
		private static final int PADDING_BOTTOM = 50;
		
		private List<TrackPoint> mList;
		
		private Float mMaxX;
		private Float mMaxY;
		
		private String mXCoordinateText;
		private String mYCoordinateText;
	
        private Paint axesPaint = new Paint();
        private Paint linePaint = new Paint();
        private Paint splitPaint = new Paint();
        private Paint xTextPaint = new Paint();
        private Paint yTextPaint = new Paint();

        public DrawView(Context context, List<TrackPoint> list,
        		Float maxX, Float maxY,
        		String yCoordinateText, String xCoordinateText) {
            super(context);
            axesPaint.setColor(Color.BLUE);
            linePaint.setColor(Color.GREEN);
            splitPaint.setColor(Color.LTGRAY);
            
            xTextPaint.setColor(Color.DKGRAY);
            yTextPaint.setColor(Color.DKGRAY);
            xTextPaint.setTextAlign(Align.CENTER);
            yTextPaint.setTextAlign(Align.RIGHT);
            
            this.mList = list;
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
        	
        	Integer prevX = null, x, distance;
        	Float prevY = null, y;
        	
        	// Draw texts coordinates.
        	canvas.drawText(mYCoordinateText, PADDING_LEFT, PADDING_TOP - 5, axesPaint);
        	axesPaint.setTextAlign(Align.RIGHT);
        	canvas.drawText(mXCoordinateText, width - PADDING_RIGHT, height - (PADDING_BOTTOM / 3), axesPaint);
        	
        	// Draw coordinate axes.
            canvas.drawLine(PADDING_LEFT, height - PADDING_BOTTOM,
            		PADDING_LEFT, PADDING_TOP,
            		axesPaint);
            canvas.drawLine(PADDING_LEFT, height - PADDING_BOTTOM,
            		width - PADDING_RIGHT, height - PADDING_BOTTOM,
            		axesPaint);
            
            // Draw references altitudes.
            axesPaint.setTextAlign(Align.RIGHT);
            canvas.drawText(String.valueOf(0 + mBeginY), PADDING_LEFT - 5, height - PADDING_BOTTOM, yTextPaint);
            
            // Draw horizontal lines (altitudes split).
            int maxDistance = (int) ((mMaxX * (width - PADDING_LEFT - PADDING_RIGHT)) / mMaxX);
            for (int j = mSplitY; j <= mMaxY; j += mSplitY) {
            	int splitY = (int) ((j * (height - PADDING_TOP - PADDING_BOTTOM) / mMaxY));
            	canvas.drawLine(PADDING_LEFT, 
            			height - PADDING_BOTTOM - splitY, 
            			PADDING_LEFT + maxDistance, 
            			height - PADDING_BOTTOM - splitY,
            			splitPaint);
            	
            	canvas.drawText(String.valueOf(j + mBeginY), PADDING_LEFT - 5, height - splitY - PADDING_BOTTOM, yTextPaint);
            }
            
            // Draw lines from tree map.
            int i = 0;
            for (TrackPoint tp : mList) {
            	// Get distance.
            	distance = (int) tp.getDistance();
            	
            	// x and y relative to the width and height of the graphic.
            	x = (int) ((distance * (width - PADDING_LEFT - PADDING_RIGHT)) / mMaxX);
            	y = (float) ((tp.getElevation() * ((float) (height - PADDING_TOP - PADDING_BOTTOM))) / mMaxY) - 
            			((mBeginY * ((float) (height - PADDING_TOP - PADDING_BOTTOM))) / mMaxY);
            	
            	// If there are previous points.
            	if(prevX != null && prevY != null) {
            		// Draw split lines each mSplitX meters.
            		if (i == 0 || i * mSplitX <= distance) {
            			int splitX = (int) (((i * mSplitX) * (width - PADDING_LEFT - PADDING_RIGHT)) / mMaxX);
            			if (i > 0) {
	            			canvas.drawLine(PADDING_LEFT + splitX, height - PADDING_BOTTOM,
	                        		PADDING_LEFT + splitX, PADDING_TOP,
	                        		splitPaint);
            			}
            			
            			// Draw km reference.
                    	canvas.drawText(String.valueOf((mSplitX / 1000) * i), 
                    			PADDING_LEFT + splitX, 
                    			height - (PADDING_BOTTOM - 15), 
                    			xTextPaint);

                    	i++;
            		}
            		
	            	// Draw a line joining track points (the graphic).
	            	canvas.drawLine(
	            			(float) (prevX + PADDING_LEFT),
	            			(float) (height - PADDING_TOP - prevY), // (height - PADDING_TOP - PADDING_BOTTOM) - prevY + PADDING_BOTTOM ==
	            			                                        // height - PADDING_TOP - prevY
	            			(float) (x + PADDING_LEFT),
	            			(float) (height - PADDING_TOP - y),     // (height - PADDING_TOP - PADDING_BOTTOM) - y + PADDING_BOTTOM ==
	            													// height - PADDING_TOP - y
	            			linePaint);
	            	
	            	// Coloring the graphic area with a rectangle so when gps was lost the graphic is coloring any way.
	            	canvas.drawRect(
	            			prevX + PADDING_LEFT, 
	            			height - PADDING_BOTTOM - y, 
	            			x  + PADDING_LEFT, 
	            			height - PADDING_BOTTOM,
	            			linePaint);
	            	
	                prevX = x;
	            	prevY = y;
            	}
            	else {
            		prevX = x;
            		prevY = y;
            	}
            }
        }
	}
}

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

package es.rgmf.libresportgps.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.List;

import es.rgmf.libresportgps.R;
import es.rgmf.libresportgps.db.orm.TrackPoint;

/**
 * This class draw pars of values in a coordinate gracphic.
 *
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class GraphicView extends View {
    private static final int PADDING_LEFT = 50;
    private static final int PADDING_RIGHT = 10;
    private static final int PADDING_TOP = 50;
    private static final int PADDING_BOTTOM = 50;

    public enum Unit {
        METER ("m"),
        KM ("km");

        private String mUnitStr;

        Unit (String us) {
            this.mUnitStr = us;
        }

        public String getUnitStr() {
            return mUnitStr;
        }
    }

    private Unit mXUnit;
    private Unit mYUnit;

    private List<TrackPoint> mList;

    private Float mMaxX;
    private Float mMaxY;
    private Integer mSplitX;
    private Integer mSplitY;
    private Integer mBeginY;

    private Paint axesPaint = new Paint();
    private Paint linePaint = new Paint();
    private Paint pointsPaint = new Paint();
    private Paint splitPaint = new Paint();
    private Paint xTextPaint = new Paint();
    private Paint yTextPaint = new Paint();

    /**
     * This constructor need a lot of data to draw the graphic.
     *
     * @param context The context.
     * @param list A list of points.
     * @param maxX The max value in the x coordinate.
     * @param maxY The max value in the y coordinate.
     * @param splitX How many distance the graphic draw an horizontal line.
     * @param splitY How many distance the graphic draw a vertical line.
     * @param beginY Where the y coordinate begin.
     * @param xUnit The unit of the x coordinate.
     * @param yUnit The unit of the y coordinate.
     */
    public GraphicView(Context context, List<TrackPoint> list,
                       Float maxX, Float maxY,
                       Integer splitX, Integer splitY,
                       Integer beginY, Unit xUnit, Unit yUnit) {
        super(context);
        axesPaint.setColor(context.getResources().getColor(R.color.actionbar_background));
        linePaint.setColor(context.getResources().getColor(R.color.selected_item));
        pointsPaint.setColor(context.getResources().getColor(R.color.accent_color));
        splitPaint.setColor(Color.LTGRAY);

        xTextPaint.setColor(Color.DKGRAY);
        yTextPaint.setColor(Color.DKGRAY);
        xTextPaint.setTextAlign(Paint.Align.CENTER);
        yTextPaint.setTextAlign(Paint.Align.RIGHT);

        this.mList = list;
        this.mMaxX = maxX;
        this.mMaxY = maxY;
        this.mSplitX = splitX;
        this.mSplitY = splitY;
        this.mBeginY = beginY;
        this.mXUnit = xUnit;
        this.mYUnit = yUnit;
    }

    /**
     * Draw the 2D graphic.
     *
     * @param canvas
     */
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int height = canvas.getHeight();
        int width = canvas.getWidth();

        Integer prevX = null, x, distance;
        Float prevY = null, y;

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

                    // Draw unit reference.
                    switch (mXUnit) {
                        case KM:
                            canvas.drawText(String.valueOf((mSplitX / 1000) * i),
                                PADDING_LEFT + splitX,
                                height - (PADDING_BOTTOM - 15),
                                xTextPaint);
                            break;
                        case METER:
                            canvas.drawText(String.valueOf((mSplitX) * i),
                                    PADDING_LEFT + splitX,
                                    height - (PADDING_BOTTOM - 15),
                                    xTextPaint);
                            break;
                    }

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
                        pointsPaint);

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

        // Draw texts coordinates.
        canvas.drawText(mYUnit.getUnitStr(), PADDING_LEFT, PADDING_TOP - 5, axesPaint);
        axesPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(mXUnit.getUnitStr(), width - PADDING_RIGHT, height - (PADDING_BOTTOM / 3), axesPaint);

        // Draw coordinate axes.
        canvas.drawLine(PADDING_LEFT, height - PADDING_BOTTOM,
                PADDING_LEFT, PADDING_TOP,
                axesPaint);
        canvas.drawLine(PADDING_LEFT, height - PADDING_BOTTOM,
                width - PADDING_RIGHT, height - PADDING_BOTTOM,
                axesPaint);

        // Draw references altitudes.
        axesPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(String.valueOf(0 + mBeginY), PADDING_LEFT - 5, height - PADDING_BOTTOM, yTextPaint);
    }
}
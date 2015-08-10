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

package es.rgmf.libresportgps.data;

import java.util.Calendar;
import java.util.Locale;

/**
 * This class represent a type of track list header.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class TrackListHead {
	public static final int TYPE_YEAR = 0;
	public static final int TYPE_MONTH = 1;
	
	private Calendar mCalendar;

	private int mType;			// Type: TYPE_YEAR or TYPE_MONTH.
	
	public TrackListHead (int type, Calendar cal) {
		mType = type;
		mCalendar = cal;
	}
	
	public int getType() {
		return mType;
	}

	public void setType(int mType) {
		this.mType = mType;
	}
	
	public int getYearNumber() {
		return mCalendar.get(Calendar.YEAR);
	}
	
	public int getMonthNumber() {
		// Calendar: 0 (January) - 11 (December). Because of that, 
		// I add 1.
		return mCalendar.get(Calendar.MONTH) + 1;
	}

	public String getValue() {
		if (mType == TYPE_YEAR)
			return String.valueOf(mCalendar.get(Calendar.YEAR));
		else
			return mCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
		//return mValue;
	}
}

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

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.Menu;
import android.view.MenuInflater;
import es.rgmf.libresportgps.R;

/**
 * Settings fragment.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class SettingsFragment extends PreferenceFragment {
	public static final String KEY_PREF_TIME_BEFORE_LOGGING = "time_before_logging";
	public static final String KEY_PREF_DISTANCE_BEFORE_LOGGING = "distance_before_logging";

	/**
	 * Create and instance of this class.
	 * 
	 * @return The instance.
	 */
	public static SettingsFragment newInstance() {
		SettingsFragment sf = new SettingsFragment();
		return sf;
	}
	
	/**
	 * Method called when the fragment is created.
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set options menu in bar menu.
		setHasOptionsMenu(true);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
	
	/**
	 * This method modifies the options in the bar menu adapting it to this
	 * fragment.
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		menu.clear();
	}
}

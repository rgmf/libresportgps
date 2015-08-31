/**
 * Copyright (C) 2014 Román Ginés Martínez Ferrández <rgmf@riseup.net>
 * <p/>
 * This program (LibreSportGPS) is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either
 * version 3 of the License, or any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package es.rgmf.libresportgps.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import es.rgmf.libresportgps.R;
import es.rgmf.libresportgps.common.Session;

/**
 * All View that need GPS data must extends from it.
 *
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public abstract class AbstractViewFragment extends Fragment {
    public abstract View onCreateView(LayoutInflater inflater, ViewGroup container,
                                      Bundle savedInstanceState);

    public abstract void onLocationUpdate(Location loc);

    /**
     * Check if GPS is enabled. If GPS is not enabled then It shows an Dialog
     * through the user can enabled it.
     */
    protected void checkGpsProvider() {
        LocationManager lm = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
                && !Session.isAlertDialogGPSShowed()) {
            Session.setAlertDialogGPSShowed(true);
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.gps_disabled)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(
                            getResources()
                                    .getString(R.string.gps_disabled_hint))
                    .setCancelable(true)
                    .setPositiveButton(android.R.string.yes,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    startActivity(new Intent(
                                            Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            })
                    .setNegativeButton(android.R.string.no,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.cancel();
                                }
                            }).create().show();
        }
    }
}

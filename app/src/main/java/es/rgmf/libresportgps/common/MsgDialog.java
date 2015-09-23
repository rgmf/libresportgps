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

package es.rgmf.libresportgps.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import es.rgmf.libresportgps.R;

/**
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class MsgDialog {

    public static void alert(Context context, Integer titleResource, String msg) {
        new AlertDialog.Builder(context)
                .setTitle(titleResource)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(msg)
                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create().show();
    }
}

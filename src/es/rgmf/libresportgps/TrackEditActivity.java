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

package es.rgmf.libresportgps;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Spinner;
import android.widget.TextView;
import es.rgmf.libresportgps.adapter.SportListAdapter;
import es.rgmf.libresportgps.db.DBModel;
import es.rgmf.libresportgps.db.orm.Sport;
import es.rgmf.libresportgps.db.orm.Track;
import es.rgmf.libresportgps.fragment.TrackListFragment;

/**
 * This class represent the main activity of the application.
 *
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class TrackEditActivity extends Activity {
    private Long mId;
    private SportListAdapter mSportListAdapter;
    /**
	 * This fragment can deliver messages to the activity by calling
	 * onTrackSelected (see interface definition) using mCallback instance
	 * of OnTrackSelectedListener interface (see interface definition).
	 */
	TrackListFragment.OnTrackSelectedListener mCallback;
    /**
     * This method is called when this activity is created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_track_edit);

        // Get all data passes by fragment.
        mId = getIntent().getLongExtra("id", 0L);
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String logo = getIntent().getStringExtra("logo");

        // Get all sports to the spinner.
        ArrayList<Sport> sportList = DBModel.getSports(this);
        sportList.add(new Sport(
                -1L,
                getString(R.string.unknown_sport),
                getString(R.string.unknown_sport),
                "unknown" //Environment.getExternalStorageDirectory() + "/libresportgps/unknown_sport.png"
        ));
        mSportListAdapter = new SportListAdapter(this, sportList);

        // Set data.
        Spinner spinner = (Spinner) findViewById(R.id.track_edit_spinner);
        spinner.setAdapter(mSportListAdapter);
        spinner.setSelection(mSportListAdapter.getCount() - 1);
        if (logo != null) {
        	int idx = 0;
        	for (Sport sport : sportList) {
            	if (sport.getLogo().equals(logo)) {
            		spinner.setSelection(idx);
            		break;
            	}
            	idx++;
        	}
        }
        TextView tvTitle = (TextView) findViewById(R.id.track_edit_title);
        tvTitle.setText(title);
        TextView tvDesc = (TextView) findViewById(R.id.track_edit_description);
        tvDesc.setText(description);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.cancel_accept, menu);
            return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_cancel:
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_accept:
                Spinner spinnerSport = (Spinner) findViewById(R.id.track_edit_spinner);
                Sport sport = (Sport) mSportListAdapter.getItem(spinnerSport.getSelectedItemPosition());
                Track track = new Track(
                        mId,
                        ((TextView) findViewById(R.id.track_edit_title)).getText().toString(),
                        ((TextView) findViewById(R.id.track_edit_description)).getText().toString(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        sport
                );
                DBModel.updateTrack(this, mId, track);
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
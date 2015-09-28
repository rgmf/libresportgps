package es.rgmf.libresportgps;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import es.rgmf.libresportgps.adapter.SegmentListAdapter;
import es.rgmf.libresportgps.adapter.SegmentTrackListAdapter;
import es.rgmf.libresportgps.common.Utilities;
import es.rgmf.libresportgps.db.DBModel;
import es.rgmf.libresportgps.db.orm.Segment;
import es.rgmf.libresportgps.db.orm.SegmentTrack;
import es.rgmf.libresportgps.fragment.TrackListFragment;

/**
 * The segment track activity where user can see all activities by segment the segment.
 *
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class SegmentTrackActivity extends Activity {
    public static final String EXTRA_SEGMENT_ID = "segmentId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segment_track);

        Long segmentId = getIntent().getLongExtra(EXTRA_SEGMENT_ID, 0L);

        if (savedInstanceState == null) {
            // Load the track list fragment to refresh and load the new track.
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment(segmentId)).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_segment_track, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment (see SegmentTrackActivity) containing a simple view.
     *
     * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
     */
    public class PlaceholderFragment extends ListFragment {

        private List<SegmentTrack> mTrackSegmentList;
        private Long mSegmentId;
        private Context mContext;
        private SegmentTrackListAdapter mAdapter;

        public PlaceholderFragment(Long segmentId) {
            mSegmentId = segmentId;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_list_segment_track, container, false);

            mContext = inflater.getContext();
            mTrackSegmentList = DBModel.getTrackSegments(getActivity(), mSegmentId);
            mAdapter = new SegmentTrackListAdapter(mContext, mTrackSegmentList);
            setListAdapter(mAdapter);

            if (mTrackSegmentList != null && mTrackSegmentList.size() > 0) {
                TextView tvName = (TextView) rootView.findViewById(R.id.segment_name);
                TextView tvDistance = (TextView) rootView.findViewById(R.id.segment_distance);
                TextView tvElevationGain = (TextView) rootView.findViewById(R.id.segment_elevation_gain);
                TextView tvGradient = (TextView) rootView.findViewById(R.id.segment_gradient);


                Segment segment = mTrackSegmentList.get(0).getSegment();
                tvName.setText(segment.getName());
                tvDistance.setText(Utilities.distance(segment.getDistance()));
                tvElevationGain.setText(Utilities.elevation(segment.getElevationGain()));
                tvGradient.setText(Utilities.gradient(segment.getElevationGain(), segment.getDistance()));
            }

            return rootView;
        }
    }

}

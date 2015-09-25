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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import es.rgmf.libresportgps.OSMActivity;
import es.rgmf.libresportgps.R;
import es.rgmf.libresportgps.adapter.SegmentListAdapter;
import es.rgmf.libresportgps.adapter.TrackListAdapter;
import es.rgmf.libresportgps.common.Session;
import es.rgmf.libresportgps.common.Utilities;
import es.rgmf.libresportgps.data.TrackListHead;
import es.rgmf.libresportgps.db.DBModel;
import es.rgmf.libresportgps.db.orm.Segment;
import es.rgmf.libresportgps.db.orm.Track;
import es.rgmf.libresportgps.db.orm.TrackPoint;
import es.rgmf.libresportgps.file.FileManager;
import es.rgmf.libresportgps.file.reader.GpxReader;
import es.rgmf.libresportgps.file.writer.GpxWriter;
import es.rgmf.libresportgps.fragment.dialog.FileDialog;

/**
 * This View is created to show the list of segments.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class SegmentListFragment extends ListFragment {
	private static final String POSITION_SELECTED = "position_selected";
	private static final int NONE_SELECTED = -5;
	
	/**
	 * This interface must be implemented by the activity that contain this
	 * fragment.
	 * 
	 * It is used to communication between this fragment and its activity
	 * container when user click on list element.
	 * 
	 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
	 */
	public interface OnSegmentListSelectedListener {
		public void onSegmentSelected(Segment segment);
	}

	/**
	 * This fragment can deliver messages to the activity by calling
	 * onTrackSelected (see interface definition) using mCallback instance of
	 * OnTrackSelectedListener interface (see interface definition).
	 */
	OnSegmentListSelectedListener mSelectedCallback;

	private List<Segment> mSegments = new ArrayList<Segment>();

	private Context mContext;
	
	private SegmentListAdapter mAdapter;
	
	private int mPosition = NONE_SELECTED;

	/**
	 * Create an instance of this class.
	 * 
	 * @return Return the class instance.
	 */
	public static final SegmentListFragment newInstance() {
		SegmentListFragment fragment = new SegmentListFragment();
		return fragment;
	}

	/**
	 * See fragment lifecycle to understand when this method is called.
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mSelectedCallback = (OnSegmentListSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnTrackSelectedListener");
		}
	}

	/**
	 * Called when the fragment's activity has been created and this fragment's
	 * view hierarchy instantiated.
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	/**
	 * This method is called when this fragment view is created.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setHasOptionsMenu(true);

		// Get all tracks information.
		mSegments = DBModel.getSegments(getActivity());
		if (mSegments != null && mSegments.size() > 0) {
			for (Segment s : mSegments) {
				Log.v("Segmento:", s.getName());
			}
		}

		// Create values to add to the adapter (headers and values).
		mContext = inflater.getContext();
		mAdapter = new SegmentListAdapter(mContext, mSegments);
		setListAdapter(mAdapter);

		return inflater.inflate(R.layout.fragment_segment_list, container, false);
	}
	
	/**
	 * When view is created.
	 */
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		// Enabling batch contextual actions in the ListView.
		final ListView listView = getListView();
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		
		// AbsListView.MultiChoiceModeListener interface.
		listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {

			@Override
			public void onItemCheckedStateChanged(ActionMode mode, int position,
												  long id, boolean checked) {
				// Here you can do something when items are selected/de-selected,
				// such as update the title in the CAB.
				if (mAdapter.getItem(position) instanceof Segment) {
					mAdapter.toggleSelection(position);
					mode.setTitle(mAdapter.getSelectedIds().size() + " Selected");
				} else if (mAdapter.getSelectedIds().size() == 0) {
					mode.finish();
				}
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				// Respond to clicks on the actions in the CAB
				switch (item.getItemId()) {
					case R.id.tracklist_delete:
						new AlertDialog.Builder(getActivity())
								.setTitle(R.string.delete_trackfile)
								.setIcon(android.R.drawable.ic_dialog_alert)
								.setMessage(getResources().getString(R.string.delete_tracksfile_selected_hint))
								.setCancelable(true).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// Calls getSelectedIds method from ListViewAdapter Class
								SparseBooleanArray selected = mAdapter.getSelectedIds();
								// Captures all selected ids with a loop
								for (int i = (selected.size() - 1); i >= 0; i--) {
									if (selected.valueAt(i)) {
										Segment selectedItem = mAdapter.getItem(selected.keyAt(i));
										if (!DBModel.deleteSegment(getActivity(), ((Segment) selectedItem).getId())) {
											Toast.makeText(getActivity(), getString(R.string.segment_was_not_deleted) + " (" + ((Segment) selectedItem).getName() + ")",
													Toast.LENGTH_LONG).show();
										} else {
											mAdapter.remove(selectedItem);
										}
									}
								}
								mAdapter.removeSelection();
							}
						}).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								mAdapter.removeSelection();
								dialog.cancel();
							}
						}).create().show();

						mode.finish(); // Action picked, so close the CAB
						return true;
					default:
						return false;
				}
			}

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				// Inflate the menu for the CAB
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.segment_list_context_menu, menu);
				return true;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				// Here you can make any necessary updates to the activity when
				// the CAB is removed. By default, selected items are deselected/unchecked.
				//mAdapter.removeSelection();
			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				// Here you can perform updates to the CAB due to
				// an invalidate() request
				return false;
			}
		});
	}

	/**
	 * From Android Develop Documentation:
	 * 
	 * This method will be called when an item in the list is selected.
	 * Subclasses should override. Subclasses can call
	 * getListView().getItemAtPosition(position) if they need to access the data
	 * associated with the selected item.
	 * 
	 * Parameters l The ListView where the click happened v The view that was
	 * clicked within the ListView position The position of the view in the list
	 * id The row id of the item that was clicked
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// Calls super.
		super.onListItemClick(l, v, position, id);

		mPosition = position;
		Segment segment = (Segment) (getListView().getItemAtPosition(position));
		mSelectedCallback.onSegmentSelected(segment);
	}

	/**
	 * This method modifies the options in the bar menu adapting it to this
	 * fragment.
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		menu.clear();
		inflater.inflate(R.menu.add, menu);
	}

	/**
	 * Handle the clicked options in this fragment.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_add:
			Intent intent = new Intent(mContext, OSMActivity.class);
			startActivity(intent);
			break;
		}

		return super.onOptionsItemSelected(item);
	}
}

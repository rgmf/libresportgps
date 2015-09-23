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

package es.rgmf.libresportgps.fragment.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import es.rgmf.libresportgps.R;
import es.rgmf.libresportgps.db.orm.TrackPoint;


/**
 * This class represent an add segment dialog.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class AddSegmentDialog extends DialogFragment {
	/**
	 * The value the user have to insert in this dialog.
	 */
	private String mSegmentName;

	/* The activity/fragment that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface AddSegmentDialogListener {
        public void onDialogPositiveClick(String segmentName);
    }
    
    // Use this instance of the interface to deliver action events
    AddSegmentDialogListener mListener;

	// Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (AddSegmentDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement AddSegmentDialogListener");
        }
    }
	
    /**
     * Here it creates the dialog and defer getting button to
     * onStart to control dismiss action.
     */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();
	    
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
    		.setPositiveButton(android.R.string.ok, null)
    		.setNegativeButton(android.R.string.cancel, null);
	    
	    builder.setView(inflater.inflate(R.layout.dialog_add_fragment, null));
		
	    return builder.create();
	}
	
	/**
	 * Getting buttons and control on click listener.
	 */
	@Override
	public void onStart() {
	    super.onStart();

	    AlertDialog dialog = (AlertDialog)getDialog();
	    dialog.getButton(AlertDialog.BUTTON_POSITIVE)
	      .setOnClickListener(new View.OnClickListener() {
	          @Override
	          public void onClick(View v) {
	        	  EditText segmentName = (EditText) getDialog().findViewById (R.id.segment_name);
           	   if (segmentName.getText().toString().length() == 0)
           	       segmentName.setError(getString(R.string.required_value));
           	   else {
           		   mSegmentName = segmentName.getText().toString();
           		   
           		   // With this code line we call to the callback in the Fragment that called this
           		   // dialog (getTargetFragment).
				   /*
           		   AddSegmentDialogListener callback = (AddSegmentDialogListener) getTargetFragment();
           		   callback.onDialogPositiveClick(mSegmentName);
           		   getDialog().dismiss();
           		   */
           		   

           		   AddSegmentDialogListener activity = (AddSegmentDialogListener) getActivity();
           		   activity.onDialogPositiveClick(mSegmentName);
           		   getDialog().dismiss();

           	   }
	        }
	    });
	}
	
	public String getName() {
		return mSegmentName;
	}
}

package com.jacsstuff.joesfilmfinder.preferences;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.jacsstuff.joesfilmfinder.R;

/**
 * This class displays the 'about' dialog from the options drop-down menu.
 */
public class AboutAppDialogue {

    public AboutAppDialogue(Context context){

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                alertBuilder.setMessage(context.getResources().getString(R.string.about_message));
                alertBuilder.setNeutralButton(context.getResources().getString(R.string.about_dialogue_ok_button),

                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // If we don't set a button, it won't appear. However, the default action
                            // closes the dialog (which we want), so no need to place any code within this Listener.
                          // Log.i("About dialogue", "closing about dialogue.");
                        }
                    }
                );

                alertBuilder.create();
                alertBuilder.show();

    }
}

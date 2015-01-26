package dk.simwir.pingpong.dialogs;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import dk.simwir.pingpong.R;

public class WallLoseDialogFragment extends DialogFragment{
    int mScore;
    int mHighscore;
    private static final String SCORE = "score";
    private static final String HIGHSCORE = "highscore";

    /**
     * Creates a new instance of the WallLoseDialog, Passing the information needed to show the dialog.
     * @param score The players score on the current attempt
     * @param highscore The players highscore on all attempts
     * @return Returns the fragment needed for creating a new dialog
     */
    public static WallLoseDialogFragment newInstance(int score, int highscore){
        WallLoseDialogFragment f = new WallLoseDialogFragment();

        //Saves the arguments passed in, in a fragment so that it can be referenced when showing the dialog.
        Bundle args = new Bundle();
        args.putInt(SCORE, score);
        args.putInt(HIGHSCORE, highscore);
        f.setArguments(args);

        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        String message;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //Gets the score and the Highscore from the fragment saved later
        mScore = getArguments().getInt(SCORE);
        mHighscore = getArguments().getInt(HIGHSCORE);

        //True if the score from this game is a new highscore
        if(mHighscore==mScore){
            //Sets the message to be shown in the dialog
            message = getString(R.string.dialog_wall_new_highscore) + "\n" + getString(R.string.dialog_wall_your_highscore) + " " + mHighscore;
        }else{
            //Sets the message to be shown in the dialog
            message = getString(R.string.dialog_wall_game_over) + " " + mScore + "\n" + getString(R.string.dialog_wall_your_highscore)+ " " + mHighscore;
        }
        //Sets the message to the message specefied above and sets the positive button to "Restart"
        //and negative button to "Menu"
        builder.setMessage(message).setPositiveButton(R.string.restart, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                mListener.onDialogPositiveClick(WallLoseDialogFragment.this);
            }
        }).setNegativeButton(R.string.menu, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                mListener.onDialogNegativeClick(WallLoseDialogFragment.this);
            }
        });
        return builder.create();
    }

    LoseDialogListener mListener;

    /**
     * An interface that makes the activity handle the button press
     */
    public interface LoseDialogListener{
        public void onDialogPositiveClick(DialogFragment dialog);

        public void onDialogNegativeClick(DialogFragment dialog);
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        try{
            mListener = (LoseDialogListener) activity;
        }catch(ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement WinnerDialogListener");
        }
    }
}

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

        //Saves the arguments passed in a fragment so that it can be references when showing the dialog.
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

        mScore = getArguments().getInt(SCORE);
        mHighscore = getArguments().getInt(HIGHSCORE);
        if(mHighscore==mScore){
            message = getString(R.string.dialog_wall_new_highscore) + "\n" + getString(R.string.dialog_wall_your_highscore) + " " + mHighscore;
        }else{
            message = getString(R.string.dialog_wall_game_over) + " " + mScore + "\n" + getString(R.string.dialog_wall_your_highscore)+ " " + mHighscore;
        }
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

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
    private static final String SCORE = "score";

    public static WallLoseDialogFragment newInstance(int score){
        WallLoseDialogFragment f = new WallLoseDialogFragment();

        Bundle args = new Bundle();
        args.putInt(SCORE, score);
        f.setArguments(args);

        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        mScore = getArguments().getInt(SCORE);
        builder.setMessage(getString(R.string.dialog_wall_game_over) + " " + mScore).setPositiveButton(R.string.restart, new DialogInterface.OnClickListener(){
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

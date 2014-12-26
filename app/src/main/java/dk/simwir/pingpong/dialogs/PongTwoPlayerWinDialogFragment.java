package dk.simwir.pingpong.dialogs;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import dk.simwir.pingpong.R;

public class PongTwoPlayerWinDialogFragment extends DialogFragment{
    int mWinner;
    private static final String WINNER = "winner";

    public static PongTwoPlayerWinDialogFragment newInstance(int winner){
        PongTwoPlayerWinDialogFragment f = new PongTwoPlayerWinDialogFragment();

        Bundle args = new Bundle();
        args.putInt(WINNER, winner);
        f.setArguments(args);

        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        mWinner = getArguments().getInt(WINNER);
        builder.setMessage(getString(R.string.dialog_pong2player_win) + " " + mWinner + " " + getString(R.string.won)).setPositiveButton(R.string.restart, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                mListener.onDialogPositiveClick(PongTwoPlayerWinDialogFragment.this);
            }
        }).setNegativeButton(R.string.menu, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                mListener.onDialogNegativeClick(PongTwoPlayerWinDialogFragment.this);
            }
        });
        return builder.create();
    }

    WinnerDialogListener mListener;

    public interface WinnerDialogListener{
        public void onDialogPositiveClick(DialogFragment dialog);

        public void onDialogNegativeClick(DialogFragment dialog);
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        try{
            mListener = (WinnerDialogListener) activity;
        }catch(ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement WinnerDialogListener");
        }
    }
}

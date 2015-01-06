package dk.simwir.pingpong.dialogs;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import dk.simwir.pingpong.R;

public class PongSinglePlayerWinDialogFragment extends DialogFragment{
    boolean mPlayerWin;
    private static final String WINNER = "winner";

    public static PongSinglePlayerWinDialogFragment newInstance(boolean playerWin){
        PongSinglePlayerWinDialogFragment f = new PongSinglePlayerWinDialogFragment();

        Bundle args = new Bundle();
        args.putBoolean(WINNER, playerWin);
        f.setArguments(args);

        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        mPlayerWin = getArguments().getBoolean(WINNER);
        if(mPlayerWin){
            builder.setMessage(R.string.dialog_pong1player_player_win).setPositiveButton(R.string.restart, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mListener.onDialogPositiveClick(PongSinglePlayerWinDialogFragment.this);
                }
            }).setNegativeButton(R.string.menu, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mListener.onDialogNegativeClick(PongSinglePlayerWinDialogFragment.this);
                }
            });
        }else{
            builder.setMessage(R.string.dialog_pong1player_ai_win).setPositiveButton(R.string.restart, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mListener.onDialogPositiveClick(PongSinglePlayerWinDialogFragment.this);
                }
            }).setNegativeButton(R.string.menu, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mListener.onDialogNegativeClick(PongSinglePlayerWinDialogFragment.this);
                }
            });
        }

        return builder.create();
    }

    WinnerDialogListener mListener;

    public interface WinnerDialogListener{
        public void onDialogPositiveClick(DialogFragment dialog);

        public void onDialogNegativeClick(DialogFragment dialog);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mListener = (WinnerDialogListener) activity;
        }catch(ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement WinnerDialogListener");
        }
    }
}

package dk.simwir.pingpong.dialogs;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import dk.simwir.pingpong.R;
/*
    Copyright © 2015  Simon Virenfeldt

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, version 3 of the License

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 */
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

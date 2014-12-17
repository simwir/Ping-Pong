package dk.simwir.pingpong.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import dk.simwir.pingpong.R;


public class PongTwoPlayerDialogFragment extends DialogFragment{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_pong2player_open_settings).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                mListener.onDialogPositiveClick(PongTwoPlayerDialogFragment.this);
            }
        }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                mListener.onDialogNegativeClick(PongTwoPlayerDialogFragment.this);
            }
        });
        return builder.create();
    }

    public interface NoticeDialogListener{
        public void onDialogPositiveClick(DialogFragment dialog);

        public void onDialogNegativeClick(DialogFragment dialog);
    }

    NoticeDialogListener mListener;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        try{
            mListener = (NoticeDialogListener) activity;
        }catch(ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
        }
    }
}

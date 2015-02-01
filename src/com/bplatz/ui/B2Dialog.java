package com.bplatz.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.bplatz.balloonza.R;

public class B2Dialog extends DialogFragment
{
	public interface B2DialogListener
	{
		public void onPositiveClick();
	}
	
	private String message;
	
	private B2DialogListener listener;
	
	public B2Dialog()
	{
		
	}
	
	public void setListener(B2DialogListener pListener)
	{
		listener = pListener;
	}
	
	public void setMessage(String pMessage)
	{
		message = pMessage;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		
		LayoutInflater factory = LayoutInflater.from(getActivity());
		final View messageView = factory.inflate(R.layout.b2_dialog, null);
		((TextView)messageView.findViewById(R.id.textView1)).setText(message);
		
		AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(messageView)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int whichButton)
					{
						listener.onPositiveClick();
					}
				}).setNegativeButton("No", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int whichButton)
					{
					}
				}).create();
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dialog.setTitle(getActivity().getClass().getSimpleName());

		return dialog;
	}
}

package com.bplatz.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.bplatz.balloonza.R;
import com.bplatz.balloonza.Balloonza;
import com.bplatz.balloonza.manager.SceneManager;
import com.bplatz.balloonza.manager.SceneManager.SceneType;

public class B2UsernameDialog extends DialogFragment implements OnEditorActionListener
{
	public interface B2UsernameDialogListener
	{
		void onFinishEditDialog(String inputText, boolean inOptions, boolean guest);
	}
	
	private EditText mEditText;
	private boolean done = false;
	
	public B2UsernameDialog()
	{
		
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		
		LayoutInflater factory = LayoutInflater.from(getActivity());
		final View textEntryView = factory.inflate(R.layout.b2_username_dialog, null);
		
		AlertDialog dialog = new AlertDialog.Builder(getActivity()).setTitle("Enter Username").setView(textEntryView)
				.setPositiveButton("OK", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int whichButton)
					{
						
					}
				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int whichButton)
					{
						if(!B2UsernameDialog.this.getArguments().getBoolean("in_option_menu", true))
							SceneManager.getInstance().setScene(SceneType.SCENE_MENU);
					}
				}).create();
		mEditText = (EditText) textEntryView.findViewById(R.id.txt_your_name);
		mEditText.requestFocus();
		dialog.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		dialog.setCanceledOnTouchOutside(false);
		mEditText.setOnEditorActionListener(this);
		dialog.setTitle(getActivity().getClass().getSimpleName());
		
		return dialog;
	}
	
	@Override
	public void onStart()
	{
		super.onStart(); // super.onStart() is where dialog.show() is actually
							// called on the underlying dialog, so we have to do
							// it after this point
		AlertDialog d = (AlertDialog) getDialog();
		if (d != null)
		{
			Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
			positiveButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if(submit())
						dismiss();
				}
			});
		}
	}
	
	private boolean submit()
	{
		final String username = mEditText.getText().toString();
		B2UsernameDialogListener activity = (B2UsernameDialogListener) getActivity();
		if (username.length() < 3 || username.length() > 8)
		{
			((Balloonza) activity).toastOnUiThread("Username must be between 3-8 characters", Toast.LENGTH_LONG);
			return false;
		}
		activity.onFinishEditDialog(username, getArguments().getBoolean("in_option_menu", true), getArguments().getBoolean("guest", false));
		done = true;
		return true;
	}
	
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
	{
		if (EditorInfo.IME_ACTION_DONE == actionId)
		{
			if (!submit())
				return false;
			this.dismiss();
			return true;
		}
		return false;
	}
	
	@Override
	public void onDismiss(DialogInterface dialog)
	{
		super.onDismiss(dialog);
		if (!done)
		{
			if(!B2UsernameDialog.this.getArguments().getBoolean("in_option_menu", true))
				SceneManager.getInstance().setScene(SceneType.SCENE_MENU);
		}
	}
}

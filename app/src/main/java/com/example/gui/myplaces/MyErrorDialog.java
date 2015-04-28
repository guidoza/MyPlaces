package com.example.gui.myplaces;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class MyErrorDialog extends DialogFragment {
	
	Dialog dialog;
	
	public void setDialog(Dialog dialog) {
		this.dialog = dialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (dialog != null) {
			return this.dialog;
		}
		return super.onCreateDialog(savedInstanceState);
	}

	
}

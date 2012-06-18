/*
 * (c) Copyright 2012 EVRYTHNG Ltd London / Zurich
 * www.evrythng.com
 * 
 */
package org.webofthings.freezeme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

/**
 * This is the class in charge of reading NFC tags.
 * 
 * @author Dominique Guinard (domguinard)
 **/

public class NfcTriggeredReader extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Intent intent = getIntent();
		String uri = intent.getData().toString();

		Toast.makeText(this, uri, 100).show();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

}

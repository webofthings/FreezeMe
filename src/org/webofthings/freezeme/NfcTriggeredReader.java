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
 * This is the class in charge of reading NFC tags, it is triggered
 * automatically by the Android system whenever a tag corresponding to what's
 * specified in the
 * AndroidManifest.xml is seen (e.g., tags with evrythng.com in our case).
 * 
 * @author Dominique Guinard (domguinard)
 **/
public class NfcTriggeredReader extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// The data that was read from the tag (i.e., a url) is put in the
		// intent by Android
		Intent intent = getIntent();
		String uri = intent.getData().toString();

		Toast.makeText(this, uri, 100).show();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

}

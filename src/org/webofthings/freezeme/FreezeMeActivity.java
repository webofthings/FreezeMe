/*
 * (c) Copyright 2012 EVRYTHNG Ltd London / Zurich
 * www.evrythng.com
 * 
 */
package org.webofthings.freezeme;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * 
 * This class offers the QR-code scanning feature using the zxing application.
 * 
 * @author Dominique Guinard (domguinard)
 * 
 */
public class FreezeMeActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button scanQrBtn = (Button) findViewById(R.id.scanButton);
		scanQrBtn.setOnClickListener(mScanQr);

		Button scanNfcBtn = (Button) findViewById(R.id.scanNfcButton);
		scanNfcBtn.setOnClickListener(mReadNfc);

	}

	/**
	 * Button to start scanning a QR code.
	 */
	protected Button.OnClickListener mScanQr = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			// Easiest: launch the scanning app is as easy as that... (using the zxing application)
			Intent intent = new Intent("com.google.zxing.client.android.SCAN");
			intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
			startActivityForResult(intent, 0);

			//			// Cleanest (because it will launch the market in case the zxing isn't installed):
			//			IntentIntegrator integrator = new IntentIntegrator(FreezeMeActivity.this);
			//			integrator.initiateScan();

		}
	};

	/**
	 * Button to start scanning a QR code.
	 */
	protected Button.OnClickListener mReadNfc = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			startActivity(new Intent(v.getContext(), NfcExplicitReader.class));
		}
	};

	/**
	 * Triggered when the scanning has finished!
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// Easiest:
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				String contents = intent.getStringExtra("SCAN_RESULT");
				Intent foodVizuIntent = new Intent(this, FoodVisualizer.class);
				foodVizuIntent.setData(Uri.parse(contents));
				startActivity(foodVizuIntent);
			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
			}
		}

		// Cleanest:
		//		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		//		if (scanResult != null) {
		//			// handle scan result
		//			Intent foodVizuIntent = new Intent(this, FoodVisualizer.class);
		//			foodVizuIntent.setData(Uri.parse(scanResult.getContents()));
		//		}
	}

}
package org.webofthings.freezeme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FreezeMeActivity extends Activity {
	private static String user;
	private static String password;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button scanQrBtn = (Button) findViewById(R.id.scanButton);
		scanQrBtn.setOnClickListener(mScanQr);

		Button readNfcBtn = (Button) findViewById(R.id.readNfcButton);
		readNfcBtn.setOnClickListener(mReadNfc);

	}

	protected Button.OnClickListener mScanQr = new Button.OnClickListener() {
		public void onClick(View v) {
			// launch the scanning app...
			Intent intent = new Intent("com.google.zxing.client.android.SCAN");
			intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
			startActivityForResult(intent, 0);
		}
	};

	protected Button.OnClickListener mReadNfc = new Button.OnClickListener() {
		public void onClick(View v) {
			// launch the NFC reader activity
			Bundle params = new Bundle();
			params.putString("userName", getUserName());
			params.putString("password", getPassword());
			Intent intent = new Intent(v.getContext(), NfcExplicitReader.class);
			intent.putExtras(params);
			startActivity(intent);
		}
	};

	/**
	 * Triggered when the scanning has finished!
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				String contents = intent.getStringExtra("SCAN_RESULT");
				startActivity(checkAndGo(this, getUserName(), getPassword(), contents, true));
			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
			}
		}
	}

	public static Intent checkAndGo(Context ctx, String username, String password, String uriString, boolean showMessages) {
		// Add key to url:
		if (username.equals("joe") && password.equals("123")) {
			uriString = uriString + "?key=Za6pZk";
			if (showMessages) {
				Toast.makeText(ctx, "Welcome " + username + "! Redirecting to admin view!", 0).show();
			}

		} else if (showMessages) {
			Toast.makeText(ctx, "Wrong credentials provided, redirecting to customer view!" + uriString, 0).show();
		}

		return new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));
	}

	private String getUserName() {
		EditText mEdit = (EditText) findViewById(R.id.usernameField);
		return mEdit.getText().toString();
	}

	private String getPassword() {
		EditText mPw = (EditText) findViewById(R.id.passwordField);
		return mPw.getText().toString();
	}

}
package org.webofthings.freezeme;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.webofthings.freezeme.dao.AbstractFoodDAO;
import org.webofthings.freezeme.dao.FakeFoodDAO;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Visualizer extends Activity {
	String urlToAccessMoreInfo = "http://";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.visualizer);
		
		//final Bundle receiveBundle = this.getIntent().getExtras();
		final String uri = this.getIntent().getData().toString();
		
		Log.d(AppData.log, uri);
		
		final AbstractFoodDAO food = new FakeFoodDAO();
		food.load();
        
        //final Bundle receiveBundle = this.getIntent().getExtras();
        this.urlToAccessMoreInfo = food.getInfoUrl();
        
        final TextView lblName = (TextView) findViewById(R.id.textView1);
        lblName.setText(food.getName());
        
        final TextView lblDaysLeft = (TextView) findViewById(R.id.textView2);
        lblDaysLeft.setText(food.getExpiration());
        
        try {
    	  ImageView i = (ImageView)findViewById(R.id.imageView1);
    	  Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(food.getImageUrl()).getContent());
    	  i.setImageBitmap(bitmap); 
    	} catch (MalformedURLException e) {
    		Log.d(AppData.log, e.getMessage());
    	} catch (IOException e) {
    		Log.d(AppData.log, e.getMessage());
    	}
    }
    
    public void goToURL(View v) {
    	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(this.urlToAccessMoreInfo));
    	startActivity(browserIntent);
    }
    
	@Override
	public void onResume() {
		super.onResume();
	}
}

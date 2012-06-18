package org.webofthings.freezeme;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.webofthings.freezeme.dao.AbstractFoodDAO;
import org.webofthings.freezeme.dao.EvrythngFoodDAO;

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
	final ExecutorService ex = Executors.newSingleThreadExecutor();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.visualizer);
		
		//final Bundle receiveBundle = this.getIntent().getExtras();
		final String shortUri = this.getIntent().getData().toString();
		
		Log.d(AppData.log, shortUri);
		
		final Future<AbstractFoodDAO> fut = this.ex.submit(new DataLoader(new EvrythngFoodDAO(shortUri)));//new FakeFoodDAO())); // Ignore the Future<>
		try {
			final AbstractFoodDAO data = fut.get();
			
			this.urlToAccessMoreInfo = data.getInfoUrl();
	        
	        final TextView lblName = (TextView) findViewById(R.id.textView1);
	        lblName.setText(data.getName());
	        
	        final TextView lblDaysLeft = (TextView) findViewById(R.id.textView2);
	        lblDaysLeft.setText(data.getExpiration());
	        
        	final Future<InputStream> imgFut = this.ex.submit(new ImageLoader(data.getImageUrl()));//new FakeFoodDAO())); // Ignore the Future<>
        	Bitmap bitmap = BitmapFactory.decodeStream(imgFut.get());
        	ImageView i = (ImageView)findViewById(R.id.imageView1);
        	i.setImageBitmap(bitmap);
		} catch (InterruptedException e) {
			Log.d(AppData.log, e.getMessage());
		} catch (ExecutionException e) {
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
	
	class DataLoader implements Callable<AbstractFoodDAO> {
		AbstractFoodDAO dao;
		
		public DataLoader(AbstractFoodDAO dao) {
			this.dao = dao;
		}

		@Override
		public AbstractFoodDAO call() throws Exception {
			this.dao.load();			
	    	return this.dao;
		}
	}
	
	class ImageLoader implements Callable<InputStream> {
		String url;
		
		public ImageLoader(String url) {
			this.url = url;
		}

		@Override
		public InputStream call() throws Exception {
	        try {
	    	  return (InputStream)new URL(url).getContent();
	    	} catch (MalformedURLException e) {
	    		Log.d(AppData.log, e.getMessage());
	    	} catch (IOException e) {
	    		Log.d(AppData.log, "Something went wrong loading the image");
	    		Log.d(AppData.log, e.getMessage());
	    	}
	    	return null;
		}
	}
}

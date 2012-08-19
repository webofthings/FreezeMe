/*
 * (c) Copyright 2012 EVRYTHNG Ltd London / Zurich
 * www.evrythng.com
 */
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * This class is the activity displaying information about the scanned food
 * item.
 * It also allows the user to add a food item to our fridge hosted on EVRYTHNG
 * services.
 * 
 * @author Aitor Gómez Goiri
 * @author Dominique Guinard (domguinard)
 * 
 */
public class FoodVisualizer extends Activity {
	String urlToAccessMoreInfo = "http://";
	String uriInTag;
	final ExecutorService ex = Executors.newSingleThreadExecutor();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.visualizer);

		//Load URL that was just read
		uriInTag = this.getIntent().getData().toString();

		Button removeBtn = (Button) findViewById(R.id.button2);
		removeBtn.setOnClickListener(removeButtonListener);

		Button addBtn = (Button) findViewById(R.id.button1);
		addBtn.setOnClickListener(addButtonListener);

		// Load food data
		Log.d(AppData.log, uriInTag);
		final Future<AbstractFoodDAO> fut = this.ex.submit(new DataLoader(new EvrythngFoodDAO(uriInTag)));//new FakeFoodDAO())); // Ignore the Future<>
		try {
			final AbstractFoodDAO data = fut.get();

			this.urlToAccessMoreInfo = data.getInfoUrl();

			final TextView lblName = (TextView) findViewById(R.id.textView1);
			lblName.setText(data.getName());

			final TextView lblDaysLeft = (TextView) findViewById(R.id.textView2);
			lblDaysLeft.setText("Expires on:" + " " + data.getExpiration());

			final Future<Bitmap> imgFut = this.ex.submit(new ImageLoader(data.getImageUrl()));//new FakeFoodDAO())); // Ignore the Future<>
			Bitmap bitmap = imgFut.get();
			ImageView i = (ImageView) findViewById(R.id.imageView1);
			i.setImageBitmap(bitmap);
		} catch (InterruptedException e) {
			Log.d(AppData.log, e.getMessage());
		} catch (ExecutionException e) {
			Log.d(AppData.log, e.getMessage());
		}

	}

	protected Button.OnClickListener addButtonListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			final Future<AbstractFoodDAO> fut = ex.submit(new AddFood(new EvrythngFoodDAO(uriInTag)));
			try {
				final AbstractFoodDAO data = fut.get();
				Toast.makeText(FoodVisualizer.this, "Added to Freezer on EVRYTHNG!", 0).show();

				final TextView lblDaysLeft = (TextView) findViewById(R.id.textView2);
				lblDaysLeft.setText("Expires on:" + " " + data.getExpiration());

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	protected Button.OnClickListener removeButtonListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			final Future<AbstractFoodDAO> fut = ex.submit(new RemoveFood(new EvrythngFoodDAO(uriInTag)));
			try {
				final AbstractFoodDAO data = fut.get();
				Toast.makeText(FoodVisualizer.this, "Removed from Freezer on EVRYTHNG!", 0).show();

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

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

	class AddFood implements Callable<AbstractFoodDAO> {
		AbstractFoodDAO dao;

		public AddFood(AbstractFoodDAO dao) {
			this.dao = dao;
		}

		@Override
		public AbstractFoodDAO call() throws Exception {
			this.dao.addToFreezer();
			return this.dao;
		}
	}

	class RemoveFood implements Callable<AbstractFoodDAO> {
		AbstractFoodDAO dao;

		public RemoveFood(AbstractFoodDAO dao) {
			this.dao = dao;
		}

		@Override
		public AbstractFoodDAO call() throws Exception {
			this.dao.removeFromFreeezer();
			return this.dao;
		}
	}

	class ImageLoader implements Callable<Bitmap> {
		String url;

		public ImageLoader(String url) {
			this.url = url;
		}

		@Override
		public Bitmap call() throws Exception {
			try {
				return BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
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

package org.webofthings.freezeme.dao;

import org.webofthings.freezeme.AppData;

import android.util.Log;

import com.evrythng.wrapper.android.EvrythngSimpleWrapper;
import com.evrythng.wrapper.android.model.v2.Property;
import com.evrythng.wrapper.android.model.v2.Thng;

public class EvrythngFoodDAO extends AbstractFoodDAO {
    private static String EVRYTHNG_ROOT_URL_V2 = "https://evrythng.net/";
    private static String API_KEY = "6eb0ed2e630334640c28c8b0ac5e7de1daf22704";
	final EvrythngSimpleWrapper wrapper = new EvrythngSimpleWrapper(EVRYTHNG_ROOT_URL_V2, API_KEY);
	
	public void load() {
		final String id = "4fdf251c0b1cdc017400009d";
		//final Thng getThng = wrapper.get(String.format("thngs/%s", longUrl), Thng.class);
		
		final Property<String> nam = wrapper.get(String.format("thngs/%s/properties/%s", id, "ProductName"), Property.class);
		final Property<String> exp = wrapper.get(String.format("thngs/%s/properties/%s", id, "ExpiryDate"), Property.class);
	    final Property<String> imgUrl = wrapper.get(String.format("thngs/%s/properties/%s", id, "ImageURL"), Property.class);
	    final Property<String> infoUrl = wrapper.get(String.format("thngs/%s/properties/%s", id, "ProductURL"), Property.class);
	    
	    this.name = nam.getValue();
	    this.expiration = exp.getValue();
	    this.imageUrl = imgUrl.getValue();
	    this.infoUrl = infoUrl.getValue();
	    
	    //System.out.println("Got thng: " + getThng.getDescription());
	}
}
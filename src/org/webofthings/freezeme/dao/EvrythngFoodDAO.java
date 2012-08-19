/*
 * (c) Copyright 2012 EVRYTHNG Ltd London / Zurich
 * www.evrythng.com
 * 
 */
package org.webofthings.freezeme.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.evrythng.android.wrapper.evrythng.EvrythngV2Wrapper;
import com.evrythng.android.wrapper.evrythng.EvrythngV3Wrapper;
import com.evrythng.android.wrapper.evrythng.model.v2.Property;
import com.evrythng.android.wrapper.evrythng.model.v3.UrlBinding;

/**
 * 
 * Loads the food data from the EVRYTHNG services, using the ThngDroid wrapper.
 * 
 * @author Aitor Gómez Goiri
 * @author Dominique Guinard (domguinard)
 * 
 */
public class EvrythngFoodDAO extends AbstractFoodDAO {
	private static final String EVRYTHNG_ROOT_URL_V2 = "https://evrythng.net/";
	private static final String API_KEY = "<GET-YOUR-API-KEY>";
	private static final String freezerCollectionId = "<CREATE-YOUR-COLLECTION>";
	private static final String EVRYTHNG_ROOT_URL_THNGLI = "<THNG-LI-URI>";

	final EvrythngV2Wrapper wrapper = new EvrythngV2Wrapper(EVRYTHNG_ROOT_URL_V2, API_KEY);
	private String thngUrl;
	private String thngId;

	public EvrythngFoodDAO(String thngUrl) {
		this.thngUrl = thngUrl;
		thngId = getId(thngUrl);
	}

	@Override
	public void load() {
		//final Thng getThng = wrapper.get(String.format("thngs/%s", longUrl), Thng.class);
		final Property<String> nam = wrapper.get(String.format("thngs/%s/properties/%s", thngId, "ProductName"), Property.class);
		final Property<String> exp = wrapper.get(String.format("thngs/%s/properties/%s", thngId, "ExpiryDate"), Property.class);
		final Property<String> imgUrl = wrapper.get(String.format("thngs/%s/properties/%s", thngId, "ImageURL"), Property.class);
		final Property<String> infoUrl = wrapper.get(String.format("thngs/%s/properties/%s", thngId, "ProductURL"), Property.class);

		this.name = nam.getValue();
		this.expiration = exp.getValue();
		this.imageUrl = imgUrl.getValue();
		this.infoUrl = infoUrl.getValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.webofthings.freezeme.dao.AbstractFoodDAO#addToFreezer()
	 */
	@Override
	public void addToFreezer() {
		// Add thng to collection
		ArrayList<String> thngs = new ArrayList<String>();
		thngs.add(thngId);
		ArrayList<String> collectUpdated = wrapper.post(String.format("collections/%s/thngs", freezerCollectionId), thngs, ArrayList.class);
		System.out.println(collectUpdated);

		// Update expire date, here we fake it at now + 1 minute	
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date now = Calendar.getInstance().getTime();
		int mins = now.getMinutes() + 1;
		now.setMinutes(mins);
		String date = formatter.format(now);
		Property<String> updatedTime = new Property<String>("ExpiryDate", date);
		Property<String> propertyUpdated = wrapper.put(String.format("thngs/%s/properties/%s", thngId, "ExpiryDate"), updatedTime, Property.class);
		this.expiration = date;

		// Update notification sent
		Property<String> updatedSent = new Property<String>("NotificationSent", "false");
		Property<String> propertyUpdatedSent = wrapper.put(String.format("thngs/%s/properties/%s", thngId, "NotificationSent"), updatedSent, Property.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.webofthings.freezeme.dao.AbstractFoodDAO#removeFromFreeezer()
	 */
	@Override
	public void removeFromFreeezer() {
		String thngId = getId(thngUrl);

		// Removes from collection
		ArrayList<String> thngs = new ArrayList<String>();
		thngs.add(thngId);
		wrapper.delete(String.format("collections/%s/thngs/%s", freezerCollectionId, thngId));
	}

	private String getId(String thngUrl) {
		final String[] s = this.thngUrl.split("/");
		return s[s.length - 1];
	}

	private String getLongIdFromShort(String thngUrl) {
		EvrythngV3Wrapper newWrapper = new EvrythngV3Wrapper(EVRYTHNG_ROOT_URL_THNGLI);
		String id = getId(this.thngUrl);
		UrlBinding binding = newWrapper.get(id, UrlBinding.class);
		return binding.getAdId();
	}

}
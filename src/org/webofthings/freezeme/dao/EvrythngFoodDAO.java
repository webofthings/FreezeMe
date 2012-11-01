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

import com.evrythng.android.wrapper.evrythng.EvrythngApiWrapper;
import com.evrythng.android.wrapper.evrythng.model.v3.UrlBinding;
import com.evrythng.thng.resource.model.store.Property;
import com.evrythng.thng.resource.model.store.Thng;

/**
 * 
 * Loads the food data from the EVRYTHNG services, using the ThngDroid wrapper.
 * 
 * @author Aitor Gómez Goiri
 * @author Dominique Guinard (domguinard)
 * 
 */
public class EvrythngFoodDAO extends AbstractFoodDAO {
	//	private static final String EVRYTHNG_ROOT_URL_V2 = "https://evrythng.net/";
	//	private static final String API_KEY = "<GET-YOUR-API-KEY>";
	//	private static final String freezerCollectionId = "<CREATE-YOUR-COLLECTION>";
	//	private static final String EVRYTHNG_ROOT_URL_THNGLI = "http://t.tn.gg";

	private static final String EVRYTHNG_ROOT_URL = "http://api.staging.evrythng.net/";
	private static final String API_KEY = "MaEDwlYA2Xxp2oSzt9uxbenTTyiYdZgnnVQlGnmnDCiLgfUO2pKzpbhzAeotwpc0KV9J8M2QssIdxOOt";
	private static final String freezerCollectionId = "504f3e5de4b009ea2f927552";
	private static final String EVRYTHNG_ROOT_URL_THNGLI = "http://d.tn.gg";

	final EvrythngApiWrapper wrapper = new EvrythngApiWrapper(EVRYTHNG_ROOT_URL, API_KEY);
	private String thngUrl;
	private String thngId;

	public EvrythngFoodDAO(String thngUrl) {
		this.thngUrl = thngUrl;
		thngId = getId(thngUrl);
	}

	@Override
	public void load() {
		Thng thng = wrapper.get(String.format("thngs/%s", thngId), Thng.class);

		this.name = thng.getName();
		this.expiration = thng.getProperties().get("ExpiryDate");
		this.imageUrl = thng.getProperties().get("ImageURL");
		this.infoUrl = thng.getProperties().get("ProductURL");
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
		ArrayList<String> collectUpdated = wrapper.put(String.format("collections/%s/thngs", freezerCollectionId), thngs, ArrayList.class);
		System.out.println(collectUpdated);

		// Update expire date, here we fake it at now + 1 minute	
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date now = Calendar.getInstance().getTime();
		int mins = now.getMinutes() + 1;
		now.setMinutes(mins);
		String date = formatter.format(now);
		Property updatedTime = new Property();
		updatedTime.setKey("ExpiryDate");
		updatedTime.setValue(date);
		Property propertyUpdated = wrapper.put(String.format("thngs/%s/properties/%s", thngId, "ExpiryDate"), updatedTime, Property.class);
		this.expiration = date;

		// Update notification sent
		Property updatedSent = new Property();
		updatedTime.setKey("NotificationSent");
		updatedTime.setValue("false");
		Property propertyUpdatedSent = wrapper.put(String.format("thngs/%s/properties/%s", thngId, "NotificationSent"), updatedSent, Property.class);
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
		EvrythngApiWrapper newWrapper = new EvrythngApiWrapper(EVRYTHNG_ROOT_URL_THNGLI, API_KEY);
		String id = getId(this.thngUrl);
		UrlBinding binding = newWrapper.get(id, UrlBinding.class);
		return binding.getAdId();
	}

}
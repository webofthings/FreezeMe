/*
 * (c) Copyright 2012 EVRYTHNG Ltd London / Zurich
 * www.evrythng.com
 * 
 */
package org.webofthings.freezeme.dao;

import android.util.Log;
import com.evrythng.java.wrapper.ApiManager;
import com.evrythng.java.wrapper.exception.EvrythngClientException;
import com.evrythng.java.wrapper.exception.EvrythngException;
import com.evrythng.java.wrapper.service.ThngService;
import com.evrythng.thng.commons.config.ApiConfiguration;
import com.evrythng.thng.resource.model.store.Thng;

/**
 * 
 * Loads the food data from the EVRYTHNG services, using the EVRYTHNG
 * Java/Android wrapper.
 * 
 * @author Dominique Guinard (domguinard)
 * @author Aitor Gómez Goiri
 * 
 */
public class EvrythngFoodDAO extends AbstractFoodDAO {

	private ApiManager apiManager;
	private String thngId;
	private String freezerCollectionId;

	public EvrythngFoodDAO(String scannedUrl, String apiKey, String freezerCollectionId) {
		apiManager = new ApiManager(new ApiConfiguration(apiKey));
		thngId = getId(scannedUrl);
		this.freezerCollectionId = freezerCollectionId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load() {
		ThngService thngService = apiManager.thngService();
		try {
            Thng item = thngService.thngReader(thngId).execute();
            setExpiration(item.getProperties().get("ExpiryDate"));
            setImageUrl(item.getProperties().get("ImageURL"));
            setInfoUrl(item.getProperties().get("ProductURL"));
            setName(item.getName());
		} catch (EvrythngException e) {
            Log.e("FREEZEME", "An error occurred while loading the thng: " + e);
		}
	}

	/**
	 * This adds an item to the freezer. I.e., to the EVRYTHNG Collection
	 * corresponding to the freezer.
	 * 
	 * @see org.webofthings.freezeme.dao.AbstractFoodDAO#addToFreezer()
	 */
	@Override
	public void addToFreezer() {
		try {
			// Add the thng to our freezer collection
			apiManager.collectionService().thngAdder(freezerCollectionId, thngId).execute();

			// And reset the notifications
			apiManager.thngService().propertyUpdater(thngId, "NotificationSent", "false").execute();

		} catch (EvrythngClientException e) {
            Log.e("FREEZEME", "An error occurred while adding to freezer: " + e);
        } catch (EvrythngException e) {
            Log.e("FREEZEME", "An error occurred while adding to freezer: " + e);
		}
	}

	/**
	 * This removes an item from the freezer. I.e., from the EVRYTHNG Collection
	 * corresponding to the freezer.
	 * 
	 * @see org.webofthings.freezeme.dao.AbstractFoodDAO#removeFromFreeezer()
	 */
	@Override
	public void removeFromFreeezer() {
		try {
			// Add the thng to our freezer collection
			apiManager.collectionService().thngRemover(freezerCollectionId, thngId).execute();

		} catch (EvrythngClientException e) {
            Log.e("FREEZEME", "An error occurred while removing from freezer: " + e);
		} catch (EvrythngException e) {
            Log.e("FREEZEME", "An error occurred while removing from freezer: " + e);
		}
	}

	/**
	 * Extract the thngId parameter from the URL read in the QR code or on the
	 * NFC tag.
	 * 
	 * @param scannedUrl
	 * @return thngId
	 */
	private String getId(String scannedUrl) {
		final String[] s = scannedUrl.split("/");
		return s[s.length - 1];
	}

}
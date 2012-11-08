/*
 * (c) Copyright 2012 EVRYTHNG Ltd London / Zurich
 * www.evrythng.com
 * 
 */
package org.webofthings.freezeme.dao;

import java.util.List;

import com.evrythng.java.wrapper.ApiConfiguration;
import com.evrythng.java.wrapper.ApiManager;
import com.evrythng.java.wrapper.core.EvrythngApiBuilder.Builder;
import com.evrythng.java.wrapper.exception.EvrythngClientException;
import com.evrythng.java.wrapper.exception.EvrythngException;
import com.evrythng.java.wrapper.service.ThngService;
import com.evrythng.thng.resource.model.store.Property;

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
			Builder<List<Property>> thngPropertiesReader = thngService.propertiesReader(thngId);
			List<Property> results = thngPropertiesReader.execute();

			for (Property currentProp : results) {
				if (currentProp.getKey().equalsIgnoreCase("ExpiryDate")) {
					setExpiration(currentProp.getValue());
				} else if (currentProp.getKey().equalsIgnoreCase("ImageURL")) {
					setImageUrl(currentProp.getValue());
				} else if (currentProp.getKey().equalsIgnoreCase("ProductURL")) {
					setInfoUrl(currentProp.getValue());
				}
			}
		} catch (EvrythngException e) {
			e.printStackTrace();
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
			e.printStackTrace();
		} catch (EvrythngException e) {
			e.printStackTrace();
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
			e.printStackTrace();
		} catch (EvrythngException e) {
			e.printStackTrace();
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
/*
 * (c) Copyright 2012 EVRYTHNG Ltd London / Zurich
 * www.evrythng.com
 * 
 */
package org.webofthings.freezeme.dao;

/**
 * 
 * Represents the food data.
 * 
 * @author Dominique Guinard (domguinard)
 * @author Aitor Gómez Goiri
 * 
 */
public abstract class AbstractFoodDAO {
	private String name;
	private String expiration;
	private String imageUrl;
	private String infoUrl;

	/**
	 * This loads the data related to a food item.
	 */
	public abstract void load();

	/**
	 * This adds a food item to the freezer (i.e., schedules the alert)
	 */
	public abstract void addToFreezer();

	/**
	 * This removes a food item from the freezer (i.e., removes all scheduled
	 * alerts)
	 */
	public abstract void removeFromFreeezer();

	public String getName() {
		return name;
	}

	public String getExpiration() {
		return expiration;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public String getInfoUrl() {
		return infoUrl;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setExpiration(String expiration) {
		this.expiration = expiration;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public void setInfoUrl(String infoUrl) {
		this.infoUrl = infoUrl;
	}
}

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
 * @author Aitor Gómez Goiri
 * @author Dominique Guinard (domguinard)
 * 
 */
public abstract class AbstractFoodDAO {
	String name = "";
	String expiration = "";
	String imageUrl = "";
	String infoUrl = "";

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
}

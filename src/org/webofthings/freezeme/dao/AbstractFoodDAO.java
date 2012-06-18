package org.webofthings.freezeme.dao;


public abstract class AbstractFoodDAO {
	String name = "";
	String expiration = "";
	String imageUrl = "";
	String infoUrl = "";
	
	public abstract void load();

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

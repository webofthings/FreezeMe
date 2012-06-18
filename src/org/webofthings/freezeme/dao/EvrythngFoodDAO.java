package org.webofthings.freezeme.dao;

import com.evrythng.wrapper.android.EvrythngSimpleWrapper;
import com.evrythng.wrapper.android.model.UrlBinding;
import com.evrythng.wrapper.android.model.v2.Property;

public class EvrythngFoodDAO extends AbstractFoodDAO {
    private static String EVRYTHNG_ROOT_URL_V2 = "https://evrythng.net/";
    private static String API_KEY = "6eb0ed2e630334640c28c8b0ac5e7de1daf22704";
	final EvrythngSimpleWrapper wrapper = new EvrythngSimpleWrapper(EVRYTHNG_ROOT_URL_V2, API_KEY);
	private String shortUrl;
	
	public EvrythngFoodDAO(String shortUrl) {
		this.shortUrl = shortUrl;
	}
	
	public void load() {
        EvrythngSimpleWrapper newWrapper = new EvrythngSimpleWrapper("http://t.tn.gg/");
        String id = getId();
        System.out.println(id);
        UrlBinding binding = newWrapper.get(id, UrlBinding.class);
        String longId = binding.getAdId();
        
		//final Thng getThng = wrapper.get(String.format("thngs/%s", longUrl), Thng.class);
		
		final Property<String> nam = wrapper.getV2(String.format("thngs/%s/properties/%s", longId, "ProductName"), Property.class);
		final Property<String> exp = wrapper.getV2(String.format("thngs/%s/properties/%s", longId, "ExpiryDate"), Property.class);
	    final Property<String> imgUrl = wrapper.getV2(String.format("thngs/%s/properties/%s", longId, "ImageURL"), Property.class);
	    final Property<String> infoUrl = wrapper.getV2(String.format("thngs/%s/properties/%s", longId, "ProductURL"), Property.class);
	    
	    this.name = nam.getValue();
	    this.expiration = exp.getValue();
	    this.imageUrl = imgUrl.getValue();
	    this.infoUrl = infoUrl.getValue();
	    
	    //System.out.println("Got thng: " + getThng.getDescription());
	}

	private String getId() {
		final String[] s = this.shortUrl.split("/");
		return s[s.length-1];
	}
}
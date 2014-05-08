package com.br.cb2.data;

import java.util.List;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;


@ParseClassName("Recipe")
public class RecipeParse extends ParseObject {

	public RecipeParse() {
		// A default constructor is required.
	}

	public String getName() {
		return getString("Name");
	}

	public void setName(String title) {
		put("Name", title);
	}
	
	public String getDescription() {
		return getString("Description");
	}

	public void setDescription(String description) {
		put("Description", description);
	}
	
	public ParseUser getAuthor() {
		return getParseUser("author");
	}

	public void setAuthor(ParseUser user) {
		put("author", user);
	}

	public String getRating() {
		return getString("Rating");
	}

	public void setRating(String rating) {
		put("Rating", rating);
	}
	
	public String getCookTime() {
		return getString("Cooktime");
	}

	public void setCookTime(String rating) {
		put("Cooktime", rating);
	}
	
	public String getPrepTime() {
		return getString("Preptime");
	}

	public void setPrepTime(String preptime) {
		put("Preptime", preptime);
	}
	
	public String getServings() {
		return getString("Servings");
	}

	public void setServings(String servings) {
		put("Servings", servings);
	}
	
	public ParseFile getMainImage() {
		return getParseFile("Mainimage");
	}

	public void setMainImage(ParseFile file) {
		put("Mainimage", file);
	}
	

}

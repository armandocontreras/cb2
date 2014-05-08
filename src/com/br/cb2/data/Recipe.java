/**
 * 
 */
package com.br.cb2.data;

import com.parse.ParseFile;
import com.parse.ParseUser;

/**
 * @author armando.contreras
 *
 */
public class Recipe {
	private String name;
	private String description;
	private String author;
	/**
	 * "Parse" a name from some sort of feed.  In the real world, this would
	 * take in something like a JSON String
	 * 
	 * @param name
	 * @return
	 */
	public static Recipe parse(RecipeParse recipeParse) {
		
		Recipe recipeObject = new Recipe();
		recipeObject.setAuthor(recipeParse.getAuthor().toString());
		recipeObject.setDescription(recipeParse.getDescription());
		recipeObject.setName(recipeParse.getName());
		return recipeObject;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String title) {
		this.name = title;
	}
	
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getAuthor() {
		return this.author;
	}

	public void setAuthor(String user) {
		this.author = user;
	}

	
	public boolean isAwesome() {
		return name.contains("j") || name.contains("J");
	}
}

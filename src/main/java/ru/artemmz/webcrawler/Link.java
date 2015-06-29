package ru.artemmz.webcrawler;

import java.util.*;
import java.net.URL;
import java.net.MalformedURLException;

public class Link{
	public List<Link> subLinks = new ArrayList<Link>();
	private String text;
	private URL pageURL;
	
	public Link(URL pageURL){
		this.pageURL = pageURL;
	}
	
	public Link(String sUrl){
		try{
			this.pageURL = new URL(sUrl);
		}catch(MalformedURLException e){
			e.printStackTrace();
		}
	}
	
	public URL getURL(){
		return pageURL;
	}
	
	public void setText(String text){
		this.text = text;
	}
	
	public String getText(){
		return this.text;
	}
	
	@Override
	public String toString(){
		return this.getURL().toString();
	}
	
	@Override
	public boolean equals(Object o){
		/*Comparing by URL link only */
		return this.getURL().equals(((Link)o).getURL());
	}
}

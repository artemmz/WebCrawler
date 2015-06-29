package ru.artemmz.webcrawler;

import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;
import java.net.URL;
import java.net.MalformedURLException;

public class TestLink {
    
    @Test
    public void testEqualLinks() throws MalformedURLException{
    	Link link1 = new Link("https://www.google.ru");
    	link1.subLinks.add(new Link("http://habrahabr.ru/"));
    	link1.subLinks.add(new Link("http://marionettejs.com/"));
    	
    	Link link2 = new Link("https://www.google.ru");
    	link2.subLinks.add(new Link("http://backbonejs.org/"));
    	link2.subLinks.add(new Link("http://e-maxx.ru/algo/"));
    	link2.subLinks.add(new Link("http://stackoverflow.com/"));
	    assertEquals(link1, link2);
    }
    
    @Test
    public void testNotEqualLinks() throws MalformedURLException{
    	Link link1 = new Link("http://tutorials.jenkov.com/java/enums.html");
    	Link link2 = new Link("http://tutorials.jenkov.com/java/index.html");
    	assertNotEquals(link1,link2);
    }
    
    @Test
    public void testStringContainsLink(){
    	Link link1 = new Link("http://tutorials.jenkov.com");
    	String sURL = "http://tutorials.jenkov.com/java/enums.html";
    	assertTrue(sURL.contains(link1.toString()));
    }
    
    @Test
    public void testNotContainsLink(){
    	List<Link> linksList = new ArrayList<Link>();
    	linksList.add(new Link("http://habrahabr.ru/"));
    	linksList.add(new Link("http://www.google.ru"));
    	linksList.add(new Link("http://tutorials.jenkov.com"));
    	linksList.add(new Link("http://tutorials.jenkov.com/java/enums/foobar.html"));
    	Link link1 = new Link("http://tutorials.jenkov.com/java/enums.html");
    	assertFalse(linksList.contains(link1));
    }
}

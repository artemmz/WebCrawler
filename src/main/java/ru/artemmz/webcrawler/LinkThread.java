package ru.artemmz.webcrawler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class LinkThread implements Runnable{
	private Link workLink;
	private WebCrawler crawler;
	
	public LinkThread(Link workLink, WebCrawler crawler){
		this.workLink = workLink;
		this.crawler = crawler;
	}
	
	@Override
	public void run(){
		try{
			inspectLink();
			addSubLinks();
		}catch(IOException e){	
			e.printStackTrace();
		}
	}
	
	private void inspectLink() throws IOException{
		try{
			Document doc = Jsoup.connect(this.workLink.toString()).get();
			this.workLink.setText(doc.body().text());
			addLinkToDB();
			
			//extract all sublinks
			Elements subLinks = doc.select("a[href]");
			for(Element link: subLinks){
				String sURL = link.attr("href");
				if(sURL.contains(this.crawler.startLink.toString())){
					Link curLink = new Link(sURL);
					if(!this.crawler.visitedLinks.contains(curLink)){
						this.workLink.subLinks.add(curLink);
					}
				}
			}
		}catch(org.jsoup.HttpStatusException e){
			/* cannot fetch page, sorry*/
		}catch(java.net.SocketTimeoutException ex){
			// same story, cannot fetch page
		}catch(org.jsoup.UnsupportedMimeTypeException exx){
			//yet another fetch failure
		}
	}
	
	private void addLinkToDB(){
		DBManager dbManager = DBManager.getInstance();
		dbManager.addLink(this.workLink);
	}
	
	private void addSubLinks(){
		this.crawler.writeNextLevelLinks(this.workLink.subLinks);
	}
}

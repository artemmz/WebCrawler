package ru.artemmz.webcrawler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.*;
import java.net.URL;
import java.net.MalformedURLException;

import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

public class WebCrawler{
	public Set<Link> visitedLinks = new HashSet<Link>();
	public Link startLink;
	private Set<Link> curLevelLinks = new HashSet<Link>();
	/*
	 * Temporary storage for the subLinks of the next level
	 * Cannot write to curLevelLinks directly, because need 
	 * to process links of the same level at a time
	*/
	private volatile List<Link> tempCurLevelLinks = Collections
			.synchronizedList(new ArrayList<Link>());
	private ExecutorService executor;
	private int threadsNumber; 
	private int crawlDepth;
	
	public WebCrawler(Link startLink, int crawlDepth, int threadsNumber){
		this.startLink = startLink;
		this.crawlDepth = crawlDepth;
		this.threadsNumber = threadsNumber;
		this.executor = Executors.newFixedThreadPool(threadsNumber);
		this.curLevelLinks.add(startLink);
	}
	
	public static void main(String[] args){
		if (args.length != 2){
			System.out.println("WebCrawler usage: <link> <crawlDepth (-1 for maximum)>");
		}else{
			String sURL = args[0];
			Link startLink = new Link(sURL);
			int crawlDepth = Integer.parseInt(args[1]);
			Properties props = loadProps();
			int threadsNumber = Integer.parseInt(props.getProperty("threadsNumber")); 
			WebCrawler crawler = new WebCrawler(	startLink,
													crawlDepth,
													threadsNumber	);
			crawler.crawl(); 
		}
	}
	
	public static Properties loadProps(){
		Properties prop = new Properties();
		String propDir = "webcrawler.properties";
		try{
			InputStream input = WebCrawler.class.getClassLoader().getResourceAsStream(propDir);
			if (input != null) {
				prop.load(input);
			} else {
				throw new FileNotFoundException("property file '" + propDir + "' not found in the classpath");
			}
		}catch (IOException e){
			e.printStackTrace();
		}finally{
			return prop;
		}
	}
	
	/**
	* processes links in breads-first way, from level to level
	*/
	public void crawl(){
		int level = 0;
		while (level < this.crawlDepth || this.crawlDepth == -1){
			if (this.curLevelLinks.isEmpty()) break;
			System.out.println("Processing level: " + level);
			for (Link curLink: this.curLevelLinks){
				if(!this.visitedLinks.contains(curLink)){
					System.out.println("=========CRAWLING THROUGH: "
								+ curLink.getURL() + " =========");
					this.executor.execute(new LinkThread(curLink,this)); //TBD
				}
			}
			
			this.executor.shutdown();
			while (!this.executor.isTerminated()){} //wait for threads to finish their jobs
			
			this.visitedLinks.addAll(this.curLevelLinks);
			this.curLevelLinks.clear();
			this.curLevelLinks.addAll(this.tempCurLevelLinks);
			this.tempCurLevelLinks.clear();
			
			// all level is processed, need to restart threadpool
			if (this.executor.isTerminated()) {
				restartExecutor();
			}
			level++;
		}
	}
	
	public void restartExecutor(){
		this.executor = Executors.newFixedThreadPool(this.threadsNumber);
	}
	
	public synchronized void writeNextLevelLinks(List<Link> nextLinks){
		this.tempCurLevelLinks.addAll(nextLinks);
	}
}

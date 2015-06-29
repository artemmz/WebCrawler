package ru.artemmz.webcrawler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import org.h2.tools.DeleteDbFiles;

public class DBManager{
    private static volatile DBManager instance;
	private Connection conn;
	private static final String DB_NAME="./LinkStorage";

    private DBManager() {
    	try{
	    	DeleteDbFiles.execute(".", DB_NAME, true);
	    	Class.forName("org.h2.Driver");
	        conn = DriverManager.
	            getConnection("jdbc:h2:"+DB_NAME, "sa", "");
	        Statement stat = conn.createStatement();
	        stat.execute("create table links "+
	        	"(id int primary key auto_increment, link text, content text)");
	        stat.close();
    	}catch(SQLException e){
    		e.printStackTrace();
    	}catch(ClassNotFoundException ee){
    		ee.printStackTrace();
    	}
    }
    
    public static DBManager getInstance() {
		DBManager localInstance = instance;
		if (localInstance == null) {
		    synchronized (DBManager.class) {
		        localInstance = instance;
		        if (localInstance == null) {
		            instance = localInstance = new DBManager();
		        }
		    }
		}
		return localInstance;
	}
	
	public void addLink(Link link){
		try{
			PreparedStatement stat = null;
			String query = "insert into links(link,content) values(?,?)";
			stat = conn.prepareStatement(query);
			stat.setString(1,link.toString());
			stat.setString(2,link.getText());
			stat.execute();
			stat.close();
		}catch(SQLException e){
    		e.printStackTrace();
    	}
	}
	
	@Override
	protected void finalize(){
		try{
			conn.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
}

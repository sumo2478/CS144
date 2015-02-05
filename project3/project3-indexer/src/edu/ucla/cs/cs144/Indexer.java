package edu.ucla.cs.cs144;

import java.io.IOException;
import java.io.StringReader;
import java.io.File;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Indexer {

    private IndexWriter indexWriter = null;
    
    /** Creates a new instance of Indexer */
    public Indexer() {    	
    }

    public IndexWriter getIndexWriter(boolean create) throws IOException {
        if (indexWriter == null) {
            Directory indexDir = FSDirectory.open(new File("/var/lib/lucene/index1/"));
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_2, new StandardAnalyzer());
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            indexWriter = new IndexWriter(indexDir, config);
        }
        return indexWriter;
    }

    public void closeIndexWriter() throws IOException {
        if (indexWriter != null) {
            indexWriter.close();
        }
    }

    public void rebuildIndexes() throws IOException {

        Connection conn = null;

        // create a connection to the database to retrieve Items from MySQL
	try {
	    conn = DbManager.getConnection(true);
	} catch (SQLException ex) {
	    System.out.println(ex);
	}


	/*
	 * Add your code here to retrieve Items using the connection
	 * and add corresponding entries to your Lucene inverted indexes.
         *
         * You will have to use JDBC API to retrieve MySQL data from Java.
         * Read our tutorial on JDBC if you do not know how to use JDBC.
         *
         * You will also have to use Lucene IndexWriter and Document
         * classes to create an index and populate it with Items data.
         * Read our tutorial on Lucene as well if you don't know how.
         *
         * As part of this development, you may want to add 
         * new methods and create additional Java classes. 
         * If you create new classes, make sure that
         * the classes become part of "edu.ucla.cs.cs144" package
         * and place your class source files at src/edu/ucla/cs/cs144/.
	 * 
	 */
    // Open the index writer
    getIndexWriter(true);

    try {
        // Retrieve all items
    	ResultSet itemSet = retrieveItemsFromDatabase(conn);
    	
        // Index all items
        indexItems(conn, itemSet);
        
    }catch (SQLException ex) {
    	System.out.println(ex);
    	System.exit(3);
    }    

    // Close the index writer
    closeIndexWriter();
    
        // close the database connection
	try {
	    conn.close();
	} catch (SQLException ex) {
	    System.out.println(ex);
	}
    }    

    public ResultSet retrieveItemsFromDatabase(Connection conn) throws SQLException {
        String queryString = "SELECT ItemId, Name, Description FROM Item";
        return executeDatabaseQuery(conn, queryString);
    }
    
    public ResultSet retrieveCategoryForItemFromDatabase(Connection conn, String ItemId) throws SQLException {
    	String queryString = "SELECT GROUP_CONCAT(CategoryName SEPARATOR ' ') as Categories FROM Category WHERE ItemId = " + ItemId;
    	return executeDatabaseQuery(conn, queryString);
    }

    public ResultSet executeDatabaseQuery(Connection conn, String queryString) throws SQLException {
        Statement stmt = conn.createStatement();

        return stmt.executeQuery(queryString);
    }

    public void indexItems(Connection conn, ResultSet itemSet) throws IOException, SQLException {
        IndexWriter writer = getIndexWriter(false);

        while (itemSet.next()) {
            String itemId = itemSet.getString("ItemId");
            String itemName = itemSet.getString("Name");
            String itemDescription = itemSet.getString("Description");
            String itemCategory = "";
            
            // Retrieve Categories
            ResultSet categorySet = retrieveCategoryForItemFromDatabase(conn, itemId);
            if (categorySet.next()) {
            	itemCategory = categorySet.getString("Categories");
            }
            
            String fullSearchableText = itemName + " " + itemDescription + " " + itemCategory;

            Document doc = new Document();
            doc.add(new StringField("ItemId", itemId, Field.Store.YES));
            doc.add(new StringField("Name", itemName, Field.Store.YES));
            doc.add(new TextField("content", fullSearchableText, Field.Store.NO));

            writer.addDocument(doc);
        }
    }

    public static void main(String args[]) {
    	try {
    		Indexer idx = new Indexer();
            idx.rebuildIndexes();	
    	}
    	catch (IOException ex) {
    		System.out.println(ex);
    	}        
    }   
}

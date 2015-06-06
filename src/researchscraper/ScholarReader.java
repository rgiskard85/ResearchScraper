/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package researchscraper;

import database.PostgresDBClient;
import database.Publication;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 *
 * @author Dionysios-Charalampos Vythoulkas <dcvythoulkas@gmail.com>
 */
public class ScholarReader {
    
    
    // Local variables supplied by user
    int researcher_id= -1;
    String[] searchTerms;
    PostgresDBClient pDBC;
    
    // Google is a bitch. Set a web-browser like user-agent otherwise Jsoup gets a 403 Forbidden error
    String browserUserAgent = "Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0";
    // This the class name for the results
    String className = "gs_r";    
    // Google Scholar also gives a link to the pdf version of the publication, if available
    String pdfLink = "";
    // Create document for further process
    Document doc; // create document to store scholar's results
    
    public ScholarReader(String researcher_id,String yearHigh, String yearLow) {
        this.researcher_id = Integer.parseInt(researcher_id);
        this.pDBC = new PostgresDBClient();
        this.searchTerms = Arrays.copyOf(pDBC.selResForScrape(this.researcher_id),4);
        this.searchTerms[2] = yearLow;
        this.searchTerms[3] = yearHigh;
        
        // Create search url for Google Scholar
        String gScholar = "http://scholar.google.gr/scholar?as_q=&as_epq=&as_oq=&as_eq=&as_occt=any&as_sauthors="+searchTerms[0]+"+"+searchTerms[1]+"&as_publication=&as_ylo="+searchTerms[2]+"&as_yhi="+searchTerms[3]+"&btnG=&hl=en&as_sdt=0%2C5";
        
        try {
            doc = Jsoup.connect(gScholar).userAgent(browserUserAgent).get(); // actually retrieve scholar's results
        } catch (IOException ex) {
            System.out.println("Oops!!! Something went terribly wrong! Good luck debugging ;)");
            if (ex.toString().substring(0, 66).equals("org.jsoup.HttpStatusException: HTTP error fetching URL. Status=503")) {
                System.out.println("We overdid it!!! Now Google has blocked our IP :( ");
                JOptionPane.showMessageDialog(null, "Αποκλεισμός IP διεύθυνσης από το Google Scholar",
                        "Μήνυμα", JOptionPane.INFORMATION_MESSAGE);
            }
            
        }
    }
    
    // Are there any results for this researcher?
    public boolean resultsExist() {
        Elements noResults = doc.getElementsByClass("gs_med");
        return noResults.isEmpty();
    }
    
    // How many pages are the results??
    public int numOfResultPages() {
        int numOfPages =-2;
        Element navigation = doc.getElementById("gs_n");
        return numOfPages + navigation.childNode(0).childNode(0).childNode(0).childNode(0).childNodeSize();
    }
    
    // Get the results
    public void getResults(int page) {
        
        // Create search url for Google Scholar to read results, page by page
        String gScholar = "http://scholar.google.gr/scholar?start=" + page + "&q=author:" + searchTerms[0] + "+author:" + searchTerms[1] + "&hl=en&as_sdt=0,5&as_ylo=" + searchTerms[2] + "&as_yhi=" + searchTerms[3];
        
        try {
            doc = Jsoup.connect(gScholar).userAgent(browserUserAgent).get(); // retrieve scholar's page_th results
        
            // Get all elements that hold results
            Elements results = doc.getElementsByClass(className);
            String title = "";
            int citations = 0;
            for (Element result:results) {
                // Get the two parts of every result.
                Elements resultParts = result.children();
                for (Element part:resultParts) {                    
                    // If the researcher in question, has a Google Scholar profile,
                    // then the first result is a link to his profile and the first part
                    // has a class name equall to "gs_rt".
                    if (part.className().equalsIgnoreCase("gs_rt")) // If the researcher has Google Scholar profile
                        System.out.println("The researcher has a Google Scholar profile"); // notify
                    else if (part.className().equalsIgnoreCase("gs_ri")) { // if we made it to the main result
                        // To get the publication's title
                        // if the doesn't have a link
                        //This if helps us get over the linkless titles
                        if (part.child(0).select("a[href]").isEmpty()) {
                            // initiate maimoudies to go around it
                            title = part.child(0).text().substring(14);
                            //System.out.println("This is the title: " + title);
                        } else {
                            // else select the link tag directly from the first child and get the text
                            title = part.child(0).select("a[href]").text();
                            //System.out.println("This is the title: " + title);
                        }
                        // get the number of citations
                        // from the last node, which has a variying index
                        if (part.child(part.childNodeSize()-1).child(0).ownText().length()>5 && part.child(part.childNodeSize()-1).child(0).ownText().substring(0,5).equalsIgnoreCase("Cited")) {
                            citations = Integer.parseInt(part.child(part.childNodeSize()-1).child(0).ownText().substring(9));
                            //System.out.println("This is the number of citations: " + citations);
                        }
                    }
                }
                if (!title.equals("")) {
                    System.out.println("Insert researcher_id = " + researcher_id + ", title = " + title + ", citations = " + citations);
                    String origin = "scholar";
                    Publication publication = new Publication(origin, researcher_id, title, citations);
                    publication.send2db();
                }
            }
        } catch (IOException ex) {
            System.out.println("Oops!!! Something went terribly wrong! Good luck debugging ;)");
        }
            
    }

    public void updateResearcher(int researcher_id) {
        pDBC.updLastUpdate(researcher_id);
    }
    
}

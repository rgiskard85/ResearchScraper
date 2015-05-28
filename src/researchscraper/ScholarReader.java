/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package researchscraper;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author giskard
 */
public class ScholarReader {
    
    
    // Local variables supplied by user
    String name ="",surName = "", yearHigh = "", yearLow = "";
    
    // TODO - THIS MUST ALSO GO IN A SEPARATE CLASS THAT WILL GET ALL RESULTS FROM GOOGLE
    // Google is a bitch. Set a web-browser like user-agent otherwise Jsoup gets a 403 Forbidden error
    String browserUserAgent = "Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0";
    // This the class name for the results
    String className = "gs_r";    
    // Google Scholar also gives a link to the pdf version of the publication, if available
    String pdfLink = "";
    // Create document for further process
    Document doc; // create document to store scholar's results
    
    public ScholarReader(String name, String surName, String yearHigh, String yearLow) {
        this.name = name;
        this.surName = surName;
        this.yearHigh = yearHigh;
        this.yearLow = yearLow;
        
        // Create search url for Google Scholar
        String gScholar = "http://scholar.google.gr/scholar?as_q=&as_epq=&as_oq=&as_eq=&as_occt=any&as_sauthors="+name+"+"+surName+"&as_publication=&as_ylo="+yearLow+"&as_yhi="+yearHigh+"&btnG=&hl=en&as_sdt=0%2C5";
        
        try {
            doc = Jsoup.connect(gScholar).userAgent(browserUserAgent).get(); // actually retrieve scholar's results
        } catch (IOException ex) {
            System.out.println("Oops!!! Something went terribly wrong! Good luck debugging ;)");
            Logger.getLogger(ResearchScraper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean resultsExist() {
        Elements noResults = doc.getElementsByClass("gs_med");
        //System.out.println(noResults);
        return noResults.isEmpty();
    }
    
    public int numOfResultPages() {
        int numOfPages =-2;
        Element navigation = doc.getElementById("gs_n");
        return numOfPages + navigation.childNode(0).childNode(0).childNode(0).childNode(0).childNodeSize();
    }
    
    public void getResults(int page) {
        
        // Create search url for Google Scholar to read results, page by page
        String gScholar = "http://scholar.google.gr/scholar?start=" + page + "&q=author:" + name + "+author:" + surName + "&hl=en&as_sdt=0,5&as_ylo=" + yearLow + "&as_yhi=" + yearHigh;
        
        try {
            doc = Jsoup.connect(gScholar).userAgent(browserUserAgent).get(); // retrieve scholar's page_th results
        
            // Get all elements that hold results
            Elements results = doc.getElementsByClass(className);
            for (Element result:results) {
                // Get the two parts of every result.
                Elements resultParts = result.children();
                for (Element part:resultParts) {                    
                    // If the researcher in question, has a Google Scholar profile,
                    // then the first result is a link to his profile and the first part
                    // has a class name equall to "gs_rt".
                    if (part.className().equalsIgnoreCase("gs_rt")) // If the researcher has Google Scholar profile
                        System.out.println("The researcher has a Google Scholar profile"); // show it
                    else if (part.className().equalsIgnoreCase("gs_ggs gs_fl")) { // if this element has pdfLink
                        //System.out.println(part.getElementsByAttribute("href"));
                        //System.out.println(part.select("a[href]"));
                        Elements anchors = part.select("a[href]");  // select all anchors with href attribute
                        for (Element anchor:anchors) {  // for every anchor
                            System.out.println("This is the pdflink: " + anchor.attr("href")); // get the url link
                        }
                    }
                    else if (part.className().equalsIgnoreCase("gs_ri")) { // if we made it to the main result
                        //System.out.println(part.childNodeSize());  // no need, just to check the number of children
                        
                        // To get the publication's title
                        // if the doesn't have a link
                        //This if helps us get over the linkless titles
                        if (part.child(0).select("a[href]").isEmpty()) {
                            // initiate maimoudies to go around it
                            System.out.println("This is the title: " + part.child(0).text().substring(14));
                        } else {
                            // else select the link tag directly from the first child and get the text
                            System.out.println("This is the title: " + part.child(0).select("a[href]").text());
                        }
                        
                        
                        //System.out.println(part.child(part.childNodeSize()-1).child(0).ownText().substring(0));
                        // get the number of citations
                        // from the last node, which has a variying index
                        if (part.child(part.childNodeSize()-1).child(0).ownText().length()>5 && part.child(part.childNodeSize()-1).child(0).ownText().substring(0,5).equalsIgnoreCase("Cited"))
                            
                            System.out.println("This is the number of citations: " + part.child(part.childNodeSize()-1).child(0).ownText().substring(9));
                    }
                }
                //System.out.println("#######################");
            }
        } catch (IOException ex) {
            System.out.println("Oops!!! Something went terribly wrong! Good luck debugging ;)");
            Logger.getLogger(ResearchScraper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        
    }
    
}

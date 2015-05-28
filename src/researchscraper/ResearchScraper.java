/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package researchscraper;

/**
 *
 * @author giskard
 */
public class ResearchScraper {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String researcherName = "athanasios";
        String researcherSurName = "kanatas";
        String yearHigh = "";
        String yearLow = "";
        
        
        ScholarReader sr = new ScholarReader(researcherName, researcherSurName, yearHigh, yearLow);
        int pages = 0;
        
        if (sr.resultsExist()) System.out.println("We have results!");
        else System.out.println("Your search came up with nothing");
        
        if (sr.resultsExist()) {
            pages = sr.numOfResultPages();
            System.out.println("There are a total of " + pages + " result pages.");
            
            for (int i = 0;i<(pages*10);i = i + 10) {
                System.out.println("You are on page: " + (i/10+1));
                sr.getResults(i);
                System.out.printf("\n\n\n\n\n");
            }
        }
    }
    
}

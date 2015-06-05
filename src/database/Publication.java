/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

/**
 *
 * @author Dionysios-Charalampos Vythoulkas <dcvythoulkas@gmail.com>
 */
public class Publication {
    private String origin;
    private int publication_id;
    private String title;
    private int citations;
    private int researcher_id;
    private PostgresDBClient pDBC;

    
    // constructor
    public Publication(String origin, int researcher_id, String title, int citations) {
        this.origin = origin;
        this.researcher_id = researcher_id;
        this.title = title;
        this.citations = citations;
        
        this.pDBC = new PostgresDBClient();
        this.publication_id = pDBC.selPubIdByTitle(title);        
    }
    
    // Insert or update records based on scrape results
    public void send2db() {
            
            // title exists in publication???
            if (publication_id > -1) {
                // ... yes
                // publication is linked to researcher?
                if (!pDBC.selAllPubRes(researcher_id, publication_id)) {
                    //...no
                    // link researcher with publication
                    pDBC.insPubRes(publication_id, researcher_id);
                }
                // citations are up to date?
                if (pDBC.selCitation(origin, publication_id) != citations){
                    // ... no
                    // update citations
                    pDBC.updCitations(citations, origin, publication_id);
                }
            }
            else {
                // ... no, insert title in publication and get generated key
                publication_id = pDBC.insTitle(title);
                // ... then link the researcher with the publication
                pDBC.insPubRes(publication_id, researcher_id);
                // ... then link the publication with the citations number
                pDBC.insCitations(origin,publication_id, citations);
            }
    }
}

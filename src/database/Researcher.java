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
public class Researcher {
    private int researcher_id;
    private String nameGr;
    private String surNameGr;
    public Researcher(int researcher_id, String nameGr, String surNameGr) {
        this.researcher_id = researcher_id;
        this.nameGr = nameGr;
        this.surNameGr = surNameGr;
    }
    
    public int getResearcherID() {
        return this.researcher_id;
    }
    
    public String getNameGr() {
        return this.nameGr;
    }
    
    public String getSurNameGr() {
        return this.surNameGr;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigerreader;

import java.util.ArrayList;

/**
 *
 * @author baojie
 */
public class Node {
    public long ID;
    byte NameLength;
    String Name;
    int x;
    int y;
    public double Latitude;
    public double Longitude;
    ArrayList<Long> AttachedEdges;
    
    Node(long ID, double x, double y){
        this.ID=ID;
        //this.NameLength=NameLength;
        this.Name=Name;
        this.Latitude=x;
        this.Longitude=y;
        this.AttachedEdges= new ArrayList<Long>();
       
    }

    
    
}

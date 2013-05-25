/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roadconverter;

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
   // ArrayList<Long> AttachedEdges;
    
   

    Node(long nid, double latitude, double longitude) {
        this.ID = nid;
        this.Latitude=latitude;
        this.Longitude=longitude;
        this.Name="";
        this.NameLength=0;
        
    }

public String ToString(){
    String result;
    result= this.ID+"\t"+this.Latitude+"\t"+this.Longitude+"\t"+this.x+"\t"+this.y;
    
    return result;
}

    
    
}

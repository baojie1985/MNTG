/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package resultconvert;

import java.util.ArrayList;

/**
 *
 * @author baojie
 */
class Node {
     public long ID;
    byte NameLength;
    String Name;
    int x;
    int y;
    public double Latitude;
    public double Longitude;
    ArrayList<Long> AttachedEdges;
    
    Node(long ID, byte NameLength, String Name, int x, int y){
        this.ID=ID;
        this.NameLength=NameLength;
        this.Name=Name;
        this.x=x;
        this.y=y;
        this.AttachedEdges= new ArrayList<Long>();
       
    }

}

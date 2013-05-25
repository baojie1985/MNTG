/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigerreader;

/**
 *
 * @author baojie
 */
public class Edge {

    long Node1;
    long Node2;
    byte NameLength;
    String Name;
    long ID;
    int EdgeClass;
    double distance;
    ROADTYPE roadtype;
    
    public enum ROADTYPE { C, I, M, O, S, U, UNKNOWN };


    Edge(long ID, long Node1, long Node2, String _name, ROADTYPE _type) {
        this.ID = ID;
        //this.NameLength = NameLength;
        //this.Name = Name;
        this.Node1 = Node1;
        this.Node2 = Node2;
        this.Name = _name;
        this.roadtype = _type;
        //this.EdgeClass = EdgeClass;
        //this. distance = 
    }
}

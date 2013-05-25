/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roadconverter;

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

    Edge(long ID, long Node1, long Node2) {
        this.ID = ID;
        this.NameLength = 0;
        this.Name ="";
        this.Node1 = Node1;
        this.Node2 = Node2;
        this.EdgeClass = 3;
        //this. distance = 
    }
}

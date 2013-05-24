/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roadtrans;

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

    Edge(long ID, byte NameLength, String Name, long Node1, long Node2, int EdgeClass) {
        this.ID = ID;
        this.NameLength = NameLength;
        this.Name = Name;
        this.Node1 = Node1;
        this.Node2 = Node2;
        this.EdgeClass = EdgeClass;
        //this. distance = 
    }
}

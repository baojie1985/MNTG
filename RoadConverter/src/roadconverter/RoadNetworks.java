/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roadconverter;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author baojie
 */
public class RoadNetworks {

    HashMap<Long, Node> Nodes;
    HashMap<Long, Edge> Edges;
    double uplat, uplng, lwlat, lwlng;
    double xinterval, yinterval;

    RoadNetworks(String Nodepath, String Edgepath) {
        Edges = new HashMap<Long, Edge>();
        Nodes = new HashMap<Long, Node>();
        uplat = -Double.MAX_VALUE;
        uplng = -Double.MAX_VALUE;
        lwlat = Double.MAX_VALUE;
        lwlng = Double.MAX_VALUE;

        ReadEdgeFile(Edgepath);
        ReadNodeFile(Nodepath);
        
        double tmp=uplng;
        uplng=lwlng;
        lwlng=tmp;
        GetInterval();
        UpdateXY();
        System.out.println("done");
    }

    private void UpdateXY() {
        for (Node n : Nodes.values()) {
            n.x = (int)((n.Latitude - lwlat) / xinterval);
            n.y = (int)((n.Longitude - lwlng) / yinterval);
         //   System.out.println(n.ToString());
        }
    }

    private void GetInterval() {
        xinterval = (uplat - lwlat) / 30000;
        yinterval = (uplng - lwlng) / 30000;
    }

    private void ReadEdgeFile(String filename) {
        try {
            FileReader fis = null;
            fis = new FileReader(filename);
            BufferedReader dis = new BufferedReader(fis);
            long Node1;
            long Node2;
            byte NameLength;
            String Name;
            Long ID;
            int EdgeClass;
            String line;
            while ((line = dis.readLine()) != null) {
                String[] SA = line.split(",");
                long Eid = Long.valueOf(SA[0]);
                long node1 = Long.valueOf(SA[1]);
                long node2 = Long.valueOf(SA[2]);
                Edge e = new Edge(Eid, node1, node2);
                Edges.put(Eid, e);

            }


        } catch (Exception ex) {
            Logger.getLogger(RoadConverter.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void ReadNodeFile(String Nodepath) {

        try {
            FileReader fis = null;
            fis = new FileReader(Nodepath);
            BufferedReader dis = new BufferedReader(fis);

            String line;
            while ((line = dis.readLine()) != null) {
                String[] SA = line.split(",");
                long nid = Long.valueOf(SA[0]);
                double latitude = Double.valueOf(SA[1]);
                double longitude = Double.valueOf(SA[2]);
                System.out.println(nid+"\t"+latitude+"\t"+longitude);
                if (latitude > uplat) {
                    uplat = latitude;
                }
                if (latitude < lwlat) {
                    lwlat = latitude;
                }
                if (longitude > uplng) {
                    uplng = longitude;
                }
                 System.out.println(longitude+"\t"+uplng);
                if (longitude < lwlng) {
                    System.out.println(longitude+"\t"+lwlng);
                    lwlng = longitude;
                }

                Node n = new Node(nid, latitude, longitude);
                Nodes.put(nid, n);

            }


        } catch (Exception ex) {
            Logger.getLogger(RoadConverter.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public boolean writeNode(java.io.DataOutputStream out, String name,
            long id, int x, int y) {
        try {
            byte l = (byte) name.length();
            out.writeByte(l);
            if (l > 0) {
                out.write(name.getBytes());
            }
            out.writeLong(id);
            out.writeInt(x);
            out.writeInt(y);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    public boolean writeEdge(java.io.DataOutputStream out, long nodeId1,
            long nodeId2, String name, long id, int edgeClass) {
        try {
            out.writeLong(nodeId1);
            out.writeLong(nodeId2);
            byte l = (byte) name.length();
            out.writeByte(l);
            if (l > 0) {
                out.write(name.getBytes());
            }
            out.writeLong(id);
            out.writeInt(edgeClass);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
    
    
    public void writeNodes(String path){
        try {
            DataOutputStream myout= new DataOutputStream( new FileOutputStream(path+"output.node"));
            
            for(Node n : Nodes.values()){
                writeNode(myout, "",  n.ID, n.x, n.y);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RoadNetworks.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
    
        public void writeEdges(String path){
        try {
            DataOutputStream myout= new DataOutputStream( new FileOutputStream(path+"output.edge"));
            
            for(Edge e : Edges.values()){
                writeEdge(myout, e.Node1, e.Node2, "", e.ID, 3);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RoadNetworks.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
    
    public void output(String path){
         writeNodes(path);
         writeEdges(path);
    }
    public void outputjavascript(){
        /*var path41 = [
                new google.maps.LatLng( 37.781569,-122.423937),
                new google.maps.LatLng(37.77967,-122.423573),
            ];*/
        System.out.println("var mypaths =[]");
        for(Edge e : this.Edges.values()){
            Node n1 = this.Nodes.get(e.Node1);
            Node n2 = this.Nodes.get(e.Node2);
            String s = "var path"+e.ID +"= [new google.maps.LatLng("+n1.Latitude +","+n1.Longitude+"),new google.maps.LatLng("+n2.Latitude+","+n2.Longitude+")];\n";
            System.out.println(s);
            System.out.println("mypaths.push(path"+e.ID+");\n");
        }
    }
}

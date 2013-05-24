/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roadtrans;


import java.io.*;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author baojie
 */
public class RoadTrans {

    static TrafficRequest TR;
    static int RESOLUTION = 30000;
    static HashMap<Long, Node> Nodes;
    static HashMap<Long, Edge> Edges;

    /**
     * @param args the command line arguments
     */
    public static double DistanceInKM(Node c1, Node c2) {
        double _eQuatorialEarthRadius = 6378.1370D;
        double SecCoDiff = 30.887;
        double _d2r = (Math.PI / 180D);

        double lat1 = c1.Latitude;
        double long1 = c1.Longitude;
        double lat2 = c2.Latitude;
        double long2 = c2.Longitude;

        double dlong = (long2 - long1) * _d2r;
        double dlat = (lat2 - lat1) * _d2r;

        double a = Math.pow(Math.sin(dlat / 2D), 2D) + Math.cos(lat1 * _d2r) * Math.cos(lat2 * _d2r) * Math.pow(Math.sin(dlong / 2D), 2D);
        double c = 2D * Math.atan2(Math.sqrt(a), Math.sqrt(1D - a));

        double d = _eQuatorialEarthRadius * c;

        return d;
    }

    public static double setLatByPoint(TrafficRequest trafficRequest, double point) {
        double difference = trafficRequest.uplat
                - trafficRequest.lwlat;
        double proportion = point / RESOLUTION;
        double offset = proportion * difference;
        return (trafficRequest.uplat - offset);
    }

    public static double setLngByPoint(TrafficRequest trafficRequest, double point) {
        double difference = trafficRequest.uplng
                - trafficRequest.lwlng;
        double proportion = point / RESOLUTION;
        double offset = proportion * difference;
        return trafficRequest.uplng - offset;
    }

    public static void ReadNodeFile(String filename) {

        try {
            FileInputStream fis = null;
            fis = new FileInputStream(filename);
            DataInputStream dis = new DataInputStream(fis);
            byte NameLength;
            String Name;
            long ID;
            int x;
            int y;
            while (dis.available() != 0) {
                NameLength = dis.readByte();
                if (NameLength != 0) {
                    byte[] tmp = new byte[NameLength];
                    for (int i = 0; i < NameLength; i++) {
                        tmp[i] = dis.readByte();
                    }
                    Name = new String(tmp);
                    //= dis.readChar();

                } else {
                    Name = "";
                }
                ID = dis.readLong();
                x = dis.readInt();
                y = dis.readInt();

                Node n = new Node(ID, NameLength, Name, x, y);
                n.Latitude = setLatByPoint(TR, x);
                n.Longitude = setLngByPoint(TR, y);
                Nodes.put(ID, n);
                System.out.println(ID + "\t" + x + "\t" + y + "\t" + n.Latitude + "\t" + n.Longitude+"\t"+n.Name+"\t"+n.NameLength);
            }

        } catch (Exception ex) {
            Logger.getLogger(RoadTrans.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static boolean IsInTheEdges(long Node1, long Node2) {
        boolean result = false;

        if (Node1 == Node2) {
            result = true;
        }

        Node n1 = Nodes.get(Node1);
        for (int i = 0; i < n1.AttachedEdges.size(); i++) {
            Edge e1 = Edges.get(n1.AttachedEdges.get(i));
            if (e1.Node1 == Node2 || e1.Node2 == Node2) {
                result = true;
            }
        }

        Node n2 = Nodes.get(Node2);
        for (int i = 0; i < n2.AttachedEdges.size(); i++) {
            Edge e2 = Edges.get(n2.AttachedEdges.get(i));
            if (e2.Node1 == Node1 || e2.Node2 == Node1) {
                result = true;
            }
        }

        return result;
    }

    public static void ReadEdgeFile(String filename) {
        try {
            FileInputStream fis = null;
            fis = new FileInputStream(filename);
            DataInputStream dis = new DataInputStream(fis);
            long Node1;
            long Node2;
            byte NameLength;
            String Name;
            Long ID;
            int EdgeClass;
            while (dis.available() != 0) {
                Node1 = dis.readLong();
                Node2 = dis.readLong();
                NameLength = dis.readByte();
                if (NameLength != 0) {
                    byte[] tmp = new byte[NameLength];
                    for (int i = 0; i < NameLength; i++) {
                        tmp[i] = dis.readByte();
                    }
                    Name = new String(tmp);
                    //= dis.readChar();

                } else {
                    Name = "";
                }
                ID = dis.readLong();
                EdgeClass = dis.readInt();
                //Node n1= Nodes.get(Node1);

                Edge e = new Edge(ID, NameLength, Name, Node1, Node2, EdgeClass);
                e.distance = DistanceInKM(Nodes.get(Node1), Nodes.get(Node2));
                System.out.println(ID + "\t" + NameLength + "\t" + Name + "\t" + Node1 + "\t" + Node2 + "\t" + EdgeClass + "\t" + e.distance);

                if (IsInTheEdges(Node1, Node2) == false) {
                    Edges.put(ID, e);
                    Node n1 = Nodes.get(Node1);
                    n1.AttachedEdges.add(ID);
                    Node n2 = Nodes.get(Node2);
                    n2.AttachedEdges.add(ID);
                } else {
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(RoadTrans.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void ReadParameters(String filename) {

        try {
            FileInputStream fis = null;
            fis = new FileInputStream(filename);
            DataInputStream dis = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(dis));

            String line = br.readLine();
            String[] sa = line.split("\t");
            int ID = Integer.valueOf(sa[1]);

            line = br.readLine();
            sa = line.split("\t");
            String User = sa[1];

            line = br.readLine();
            sa = line.split("\t");
            String Model = sa[1];

            line = br.readLine();
            sa = line.split("\t");
            String[] box = sa[1].split(",");
            double lwlat = Double.valueOf(box[0]);
            double uplng = Double.valueOf(box[1]);
            double uplat = Double.valueOf(box[2]);
            double lwlng = Double.valueOf(box[3]);


            TR.initial(ID, User, Model, uplat, uplng, lwlat, lwlng);

        } catch (Exception ex) {
            Logger.getLogger(RoadTrans.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void OutputEdge(String filename) {
        FileWriter fw = null;
        try {
            File outFile = new File(filename);
            fw = new FileWriter(outFile);
            PrintWriter pw = new PrintWriter(fw);
            for (Long key : Edges.keySet()) {
                Edge e = Edges.get(key);
                pw.println(e.ID + "\t" + e.Node1 + "\t" + e.Node2 + "\t" + e.EdgeClass + "\t" + e.distance);
            }
            pw.close();
            fw.close();


        } catch (IOException ex) {
            Logger.getLogger(RoadTrans.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(RoadTrans.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void OutputNode(String filename) {
        FileWriter fw = null;
        try {
            File outFile = new File(filename);
            fw = new FileWriter(outFile);
            PrintWriter pw = new PrintWriter(fw);
            for (Long key : Nodes.keySet()) {
                Node n = Nodes.get(key);
                pw.println(n.ID + "\t" + n.Latitude + "\t" + n.Longitude);
            }
            pw.close();
            fw.close();


        } catch (IOException ex) {
            Logger.getLogger(RoadTrans.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(RoadTrans.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void Output(String path) {
        OutputEdge(path + "/" + TR.ID + "-Edges.txt");
        OutputNode(path + "/" + TR.ID + "-Nodes.txt");


    }

    public static void main(String[] args) {
       // String path = args[0];
        //String ID = args[1];
        TR = new TrafficRequest();
        Nodes = new HashMap<Long, Node>();
        Edges = new HashMap<Long, Edge>();

        //ReadParameters(path + "/parameter" + ID + ".txt");
        //ReadNodeFile(path + "/output.node");
        //ReadEdgeFile(path + "/output.edge");
        //Output(path);

         String folder = "F:\\Dropbox\\MyProgram\\RoadConverter";

        ReadParameters(folder + "\\parameter828.txt");
        ReadNodeFile(folder + "\\output.node");
        ReadEdgeFile(folder + "\\output.edge");
        Output(folder);


    }
}

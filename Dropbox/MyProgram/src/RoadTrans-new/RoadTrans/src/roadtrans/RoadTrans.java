/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roadtrans;

import java.io.*;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import util.ProcessLauncher;
//import traffic.TrafficRequest;
import connection.provider.ConnectionProvider;



/**
 *
 * @author baojie, Ethan Waytas
 */
public class RoadTrans {

    static TrafficRequest TR;
    static int RESOLUTION = 30000;
    static HashMap<Long, Node> Nodes;
    static HashMap<Long, Edge> Edges;

    public static final String BASE_PATH = "/home/yackel";
    public static final String BASE_SHAPE_FILE_PATH = BASE_PATH
            + "/TrafficGenerator/shapefiles";
    
    public static final String SHAPE_NETWORK_FILE_MANAGER_CLASSPATH = "/home/yackel/TrafficGenerator/ShapeNetworkFileManager/generator21.jar"
            + File.pathSeparator
            + "/home/yackel/TrafficGenerator/ShapeNetworkFileManager/geotool2.jar"
            + File.pathSeparator
            + "/home/yackel/TrafficGenerator/ShapeNetworkFileManager/JTS13.jar"
            + File.pathSeparator
            + "/home/yackel/TrafficGenerator/ShapeNetworkFileManager";

    public static File GENERATED_FILE_NAME;
    public static File GENERATED_SHAPE_FILES;
    public static String GENERATED_FOLDER;
    
    public static final String DOWNLOAD_PATH = "/home/yackel/public_html/app/webroot/downloads/";
    
    /**
     * Create the appropriate directories, if needed. Does nothing if the directories already exist.
     * @param id - id of the traffic request
     */
    public static void createDirectories(int id) {
        
        /* Temporary file to see if the directory already exists. If not, we will
         * create it. There are some differences between the different traffic generators, so this will
         * hopefully simplify things.
         */
        File check = new File("/home/yackel/TrafficGenerator/" + id + "/");
        
        if (check.exists() || check.isDirectory()) {
            return;
        }
        
        new File("/home/yackel/TrafficGenerator/" + id + "/").mkdir();
        GENERATED_FOLDER = "/home/yackel/TrafficGenerator/" + id + "/";
        GENERATED_SHAPE_FILES = new File("/home/yackel/TrafficGenerator/" + id + "/");
        GENERATED_FILE_NAME = new File("/home/yackel/TrafficGenerator/" + id + "/output.txt");

    }
    
    /**
     * 
     * @param trafficRequest 
     */
    public static void generateShapeFiles(TrafficRequest trafficRequest) {
        
        StringBuffer sb = new StringBuffer();
        
        sb.append(trafficRequest.uplat).append(" ").append(trafficRequest.uplng).append(" ").append(trafficRequest.lwlat).append(" ").append(trafficRequest.lwlng).append(" ").append(trafficRequest.ID).append(" ");
        
        for (File countyFile : getCounties(trafficRequest)) {
            sb.append(countyFile.getAbsolutePath()).append(" ");
        }

        // generates output.node and output.edge
        ProcessLauncher.exec("java -classpath " + SHAPE_NETWORK_FILE_MANAGER_CLASSPATH
                + " conversion.ShapeNetworkFileManager "
                + sb.toString(), false);
        /*
        System.out.println("java -classpath " + SHAPE_NETWORK_FILE_MANAGER_CLASSPATH
                + " conversion.ShapeNetworkFileManager "
                + sb.toString());
        */ 
    }
    
    /**
     * Gets a list of all the counties in the selected area.
     */
    public static List<File> getCounties(TrafficRequest trafficRequest) {
        List<File> counties = new ArrayList<File>();
        Connection conn = ConnectionProvider.getConnection();
        PreparedStatement pstatement = null;

        try {
            pstatement = conn.prepareStatement("select filename from counties C where C.upperlong < ? and C.lowerlong > ? and C.upperlat > ? and C.lowerlat < ?");
            pstatement.setDouble(1, trafficRequest.lwlng);
            pstatement.setDouble(2, trafficRequest.uplng);
            pstatement.setDouble(3, trafficRequest.lwlat);
            pstatement.setDouble(4, trafficRequest.uplat);

            ResultSet rs = pstatement.executeQuery();

            while (rs.next()) {
                counties.add(new File(BASE_SHAPE_FILE_PATH, rs.getString(1) + File.separator + rs.getString(1)));
            }
        } catch (SQLException e) {            
            throw new RuntimeException(e);
        } finally {
            ConnectionProvider.safeClose(pstatement);
        }
        
        return counties;
    }
    
    /**
     * Copies over the edge and node files that RoadTrans generates
     * @param fromPath - location of files
     * @param toPath  - destination of files
     * @param id - identifier of the traffic request
     */
    public static void copyFiles(String fromPath, String toPath, int id) {
        
        ProcessLauncher.exec("cp " + fromPath + "/" + id + "-Nodes.txt " + toPath + id + "-Nodes.txt", false);
        
        ProcessLauncher.exec("cp " + fromPath +"/" + id + "-Edges.txt " + toPath + id + "-Edges.txt", false);
        
    }
    
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
        //System.out.println("c1 lat = " + lat1 + " c1 lng = " + long1 + " c2 lat = " + lat2 + " long2 = " + long2 );
        double dlong = (long2 - long1) * _d2r;
        double dlat = (lat2 - lat1) * _d2r;

        double a = Math.pow(Math.sin(dlat / 2D), 2D) + Math.cos(lat1 * _d2r) * Math.cos(lat2 * _d2r) * Math.pow(Math.sin(dlong / 2D), 2D);
        double c = 2D * Math.atan2(Math.sqrt(a), Math.sqrt(1D - a));

        double d = _eQuatorialEarthRadius * c;
        //System.out.println("_d2r = " + _d2r + " dlong = " + dlong + " dlat = " + dlat + " a = " + a + " c = " + c + " d = " + d + " _eQuatorialEarthRadius = " + _eQuatorialEarthRadius);
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
                //System.out.println(ID + "\t" + x + "\t" + y + "\t" + n.Latitude + "\t" + n.Longitude);
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
            pw.println("EdgeID\tNode1\tNode2\tEdgeClass\tDistance(in KM)");
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
            
            pw.println("NodeID\tLatitude\tLongitude");
            
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
        String path = args[0];
        int ID = Integer.parseInt(args[1]);
        
        double uplat = Double.valueOf(args[2]);
        double uplng = Double.valueOf(args[3]);
        double lwlat = Double.valueOf(args[4]);
        double lwlng = Double.valueOf(args[5]);
                
        
        TR = new TrafficRequest();
        
        Nodes = new HashMap<Long, Node>();
        Edges = new HashMap<Long, Edge>();

        //ReadParameters(path + "/parameter" + ID + ".txt");
        
        TR.initial(ID, "holder", "holder", uplat, uplng, lwlat, lwlng);
        
        createDirectories(ID);
        generateShapeFiles(TR);
        
        ReadNodeFile(path + "/output.node");
        ReadEdgeFile(path + "/output.edge");
        Output(path);
        
        copyFiles(path, DOWNLOAD_PATH, ID);

    }
}

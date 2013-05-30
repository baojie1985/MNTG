/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package randomgenerator;

//import connection.provider.ConnectionProvider;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.ProcessLauncher;


import traffic.TrafficRequest;

/**
 *
 * @author Ethan Waytas
 */
public class RandomGenerator {

    static TrafficRequest TR;
    static int RESOLUTION = 30000;
    static HashMap<Long, Node> Nodes;
    static HashMap<Long, Edge> Edges;
    
    static ArrayList<MovingObject> objects;
    
    static Random rand;
    
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

    public static final String TIGERREADER = "/home/yackel/TrafficGenerator/Test_Ethan/TigerReader.jar ";
    
    public static File GENERATED_FILE_NAME;
    public static File GENERATED_SHAPE_FILES;
    public static String GENERATED_FOLDER;
    
    public static final String DOWNLOAD_PATH = "/home/yackel/public_html/app/webroot/downloads/";
    
    //public static Connection conn = ConnectionProvider.getConnection();
    public static PreparedStatement pStatement = null;
    
    //public static final String DOWNLOAD_PATH = "/Users/nagell2008/Downloads/";
    
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
    /*
    public static void generateShapeFiles(TrafficRequest trafficRequest, String path) {
        
        StringBuffer sb = new StringBuffer();
        
        sb.append(path).append("/").append(" ");
        sb.append(trafficRequest.getRequestId()).append(" ");
        sb.append(trafficRequest.getUpperlat()).append(" ");
        sb.append(trafficRequest.getUpperlong()).append(" ");
        sb.append(trafficRequest.getLowerlat()).append(" ");
        sb.append(trafficRequest.getLowerlong()).append(" ");
        
        
        for (File countyFile : getCounties(trafficRequest)) {
            sb.append(countyFile.getAbsolutePath()).append(" ");
        }

        // generates output.node and output.edge
        ProcessLauncher.exec("java -classpath " + TIGERREADER
                + sb.toString(), false);
        
        System.out.println("java -classpath " + TIGERREADER
                + sb.toString());
         
    }
    */
    /**
     * Gets a list of all the counties in the selected area.
     */
    
    /*
    public static List<File> getCounties(TrafficRequest trafficRequest) {
        List<File> counties = new ArrayList<File>();
        Connection conn = ConnectionProvider.getConnection();
        PreparedStatement pstatement = null;

        try {
            pstatement = conn.prepareStatement("select filename from counties C where C.upperlong < ? and C.lowerlong > ? and C.upperlat > ? and C.lowerlat < ?");
            pstatement.setDouble(1, trafficRequest.getLowerlong()); //lwlng
            pstatement.setDouble(2, trafficRequest.getUpperlong()); //uplng
            pstatement.setDouble(3, trafficRequest.getLowerlat()); //lwlat
            pstatement.setDouble(4, trafficRequest.getUpperlat()); //uplat

            ResultSet rs = pstatement.executeQuery();

            while (rs.next()) {
                counties.add(new File(BASE_SHAPE_FILE_PATH, rs.getString(1) + File.separator + rs.getString(1)));
            }
        } catch (SQLException e) {            
            throw new RuntimeException(e);
        } finally {
            ConnectionProvider.safeClose(pstatement);
        }
        System.out.println(counties);
        return counties;
    }
    */
    /**
     * Copies over the edge and node files that RoadTrans generates
     * @param fromPath - location of files
     * @param toPath  - destination of files
     * @param id - identifier of the traffic request
     */
    public static void copyFiles(String fromPath, String toPath, int id) {
        
        ProcessLauncher.exec("cp " + fromPath + "/" + id + "output.txt " + toPath + id + "output.txt", false);
        
        //ProcessLauncher.exec("cp " + fromPath +"/" + id + "-Edges.txt " + toPath + id + "-Edges.txt", false);
        
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

                Edge e = new Edge(ID, Node1, Node2);
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
            //Logger.getLogger(RoadTrans.class.getName()).log(Level.SEVERE, null, ex);
        }

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

                Node n = new Node(ID, Name, x, y);
                n.Latitude = setLatByPoint(TR, x);
                n.Longitude = setLngByPoint(TR, y);
                Nodes.put(ID, n);
                //System.out.println(ID + "\t" + x + "\t" + y + "\t" + n.Latitude + "\t" + n.Longitude);
            }

        } catch (Exception ex) {
            //Logger.getLogger(RoadTrans.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        double difference = trafficRequest.getUpperlat()
                - trafficRequest.getLowerlat();
        double proportion = point / RESOLUTION;
        double offset = proportion * difference;
        return (trafficRequest.getUpperlat() - offset);
    }

    public static double setLngByPoint(TrafficRequest trafficRequest, double point) {
        double difference = trafficRequest.getUpperlong()
                - trafficRequest.getLowerlong();
        double proportion = point / RESOLUTION;
        double offset = proportion * difference;
        return trafficRequest.getUpperlong() - offset;
    }
    
    public static void InitializeObjects(TrafficRequest tr, PrintWriter pw)
    {
        int numObjs = tr.getNumObjs();
        int randomNum = -1;
        
        objects = new ArrayList<MovingObject>();
        
        randomNum = rand.nextInt(Nodes.size() - 0 + 1);
        
        List<Long> nodeIds = new ArrayList<Long>(Nodes.keySet());
        
        
        pw.println("Object_ID\tTimeStamp\tType\tLat\tLng");
        
        for (int i = 0; i < numObjs; i++)
        {
            MovingObject temp = new MovingObject(i, 0, 0, 0);
            
            temp.setCurrentPos( Nodes.get( nodeIds.get(randomNum) ) );
            temp.setLastPos(temp.getCurrentPos());
            
            temp.setObjecttype(MovingObject.ObjectType.newpoint);
            
            objects.add(temp);
            
            randomNum = rand.nextInt(Nodes.size() - 0 + 1);
            
            System.out.println("Object ID: " + temp.getId()
                                    + " TimeStamp: 0"  
                                    + " Type: " + temp.getObjecttype() 
                                    + " Lat: " + temp.getCurrentPos().Latitude 
                                    + " Lng: " + temp.getCurrentPos().Longitude);
        
            pw.println(temp.getId() + "\t"
                        + "0"  + "\t"
                        + temp.getObjecttype()
                        + "\t" + temp.getCurrentPos().Latitude
                        + "\t" + temp.getCurrentPos().Longitude);
            
            //insertIntoDB(tr.getRequestId(), temp.getObjecttype(), temp.getId(), 0, temp.getCurrentPos().Latitude, temp.getCurrentPos().Longitude);
            
            temp.setObjecttype(MovingObject.ObjectType.point);
        
        }
        
        return;
    }
    
    public static void CreateTraffic(TrafficRequest tr, PrintWriter pw)
    {
        int time = tr.getTime();
        
        MovingObject currentObject = null;
        Node currentNode = null;
        ArrayList<Long> currentEdges = null;
        
        Random localNum = new Random();
        int randomNum;
        Long edgeId;
        /*
         * FOR THE OUTPUT FILE, ASSUME MADE IN INITIALIZE, JUST APPEND TO IT
         * OR JUST PASS AROUND THE OUTPUT STREAM
         */
        
        // start at one because when nodes where intialized that was time zero
        for (int i = 1; i < time; i++)
        {
            for (int j = 0; j < objects.size(); j++)
            {
                
                
                //currentObject.setObjecttype(MovingObject.ObjectType.point);
                currentObject = objects.get(j);
                
                if (currentObject.getObjecttype() == MovingObject.ObjectType.disappearpoint)
                {
                    continue;
                }
                
                
                currentNode = currentObject.getCurrentPos();
                
                currentEdges = currentNode.AttachedEdges;
                
                randomNum = rand.nextInt(currentEdges.size());
                
                edgeId = currentEdges.get(randomNum);
                
                //System.out.println("randomNum: " + randomNum + " Edge Size: " + currentEdges.size() + " edgeId: " + edgeId);
                //System.out.println("in Nodes: " + Nodes.containsKey(edgeId));
                //System.out.println("Is node null: " + Nodes.get(edgeId));
                
                //System.out.println(currentEdges);
                /*
                System.out.println("Current ID: " + currentObject.getCurrentPos().ID);
                System.out.println("Node1 ID: " + Nodes.get(Edges.get(edgeId).Node1).ID);
                System.out.println("Node2 ID: " + Nodes.get(Edges.get(edgeId).Node2).ID);
                */
                //System.out.println(edgeId);
                //System.out.println(Edges.containsKey(edgeId));
                /*
                while ( Nodes.get(Edges.get(edgeId).Node1).ID != currentObject.getLastPos().ID && Nodes.get(Edges.get(edgeId).Node2).ID != currentObject.getLastPos().ID )
                {
                    randomNum = rand.nextInt(currentEdges.size());
                
                    edgeId = currentEdges.get(randomNum);
                }
                */
                
                
                while ( Nodes.get(edgeId).ID == currentObject.getLastPos().ID && currentEdges.size() > 1 )
                {
                    randomNum = rand.nextInt(currentEdges.size());
                
                    edgeId = currentEdges.get(randomNum);
                }
                
                // now edgeId is a random edge
                
                currentObject.setLastPos(currentObject.getCurrentPos());
                
                /*
                if (currentObject.getCurrentPos().ID == Edges.get(edgeId).Node1)
                {
                    currentObject.setCurrentPos(Nodes.get(Edges.get(edgeId).Node2));
                }
                else
                {
                    currentObject.setCurrentPos(Nodes.get(Edges.get(edgeId).Node1));
                }
                
                if (currentObject.getCurrentPos().AttachedEdges.size() == 1)
                {
                    currentObject.setObjecttype(MovingObject.ObjectType.disappearpoint);
                }
                */
                
               
                currentObject.setCurrentPos(Nodes.get(edgeId));
                
                if (currentObject.getCurrentPos().AttachedEdges.size() == 1)
                {
                    currentObject.setObjecttype(MovingObject.ObjectType.disappearpoint);
                }
                
                // This will also probably be the output of the file write
                System.out.println("Object ID: " + currentObject.getId()
                                    + " TimeStamp: " + i 
                                    + " Type: " + currentObject.getObjecttype() 
                                    + " Lat: " + currentObject.getCurrentPos().Latitude 
                                    + " Lng: " + currentObject.getCurrentPos().Longitude);
                
                pw.println(currentObject.getId() + "\t"
                        + i  + "\t"
                        + currentObject.getObjecttype()
                        + "\t" + currentObject.getCurrentPos().Latitude
                        + "\t" + currentObject.getCurrentPos().Longitude);
                
                //insertIntoDB(tr.getRequestId(), currentObject.getObjecttype(), currentObject.getId(), i, currentObject.getCurrentPos().Latitude, currentObject.getCurrentPos().Longitude);
                
                
            }
        }
        
        return; 
    }
    
    
    public static void tigerReaderOutput(String nodes, String edges)
    {
        
        BufferedReader br = null;
        BufferedReader brEdges = null;
        try {
           br = new BufferedReader(new FileReader(nodes));
           brEdges = new BufferedReader(new FileReader(edges));
        }
        catch (FileNotFoundException e) {
            System.out.println(e.toString());
        }
        
        boolean first_line = true;
        
        String line = "";
        
        long nodeId;
        double lat;
        double lng;
        long edgeId;

        try 
        {
            while ((line = br.readLine()) != null)
            {
                if (first_line)
                {
                    first_line = false;
                    continue;
                }
                
                
                String[] data = line.split(",");
                
                nodeId = Long.parseLong(data[0]);
                lat = Double.parseDouble(data[1]);
                lng = Double.parseDouble(data[2]);
                //edgeId = Long.parseLong(data[3]);
                
                
                if (Nodes.containsKey(nodeId))
                {
                    continue;
                }
                else
                {
                    Node newNode = new Node(nodeId, "", lat, lng);
                    Nodes.put(nodeId, newNode);
                }
                /*
                if (Nodes.containsKey(nodeId))
                {
                    if (!Nodes.get(nodeId).AttachedEdges.contains(edgeId))
                    {
                        Nodes.get(nodeId).AttachedEdges.add(edgeId);
                    }
                }
                else
                {
                    Nodes.put(nodeId, newNode);
                    newNode.AttachedEdges.add(edgeId);
                }
                */
                
            }
        }
        catch (IOException e)
        {
            System.out.println(e.toString());
        }
        
        
        long node1;
        long node2;
        
        first_line = true;
        try 
        {
            while ((line = brEdges.readLine()) != null)
            {
                if (first_line)
                {
                    first_line = false;
                    continue;
                }
                
                String[] data = line.split(",");
                
                edgeId = Long.parseLong(data[0]);
                node1 = Long.parseLong(data[1]);
                node2 = Long.parseLong(data[2]);
                
                //Edge newEdge = new Edge(edgeId, node1, node2);
                
                //Edges.put(edgeId, newEdge);
                
                if (Nodes.containsKey(node1))
                {
                    Nodes.get(node1).AttachedEdges.add(node2);
                }
                
                if (Nodes.containsKey(node2))
                {
                    Nodes.get(node2).AttachedEdges.add(node1);
                }
                
                
            }
        }
        catch (IOException e)
        {
            System.out.println(e.toString());
        }
        
        
        try
        {
            br.close();
            brEdges.close();
        }
        catch (IOException e)
        {
            System.out.println(e.toString());
        }
        
        
        
        return;
    }
    
        
    /*
    public static void insertIntoDB(int request_id, MovingObject.ObjectType t, int object_id, int timestamp, double lat, double lng)
    {
        
        if (pStatement == null)
        {
            try {
                pStatement = conn.prepareStatement("insert into traffic_results (traffic_request_id, type, object_id, timestamp, lat, lng) values (?,?,?,?,?,?)");
            }
            catch (SQLException e) {
                throw new IllegalStateException("Failed to insert traffic results.");
            } finally {
                //ConnectionProvider.safeClose(pStatement);
                //pStatement = null;
            }
        }
        
        try {
            pStatement.setInt(1, request_id);
            pStatement.setString(2, t.toString());
            pStatement.setInt(3, object_id);
            pStatement.setInt(4, timestamp);
            pStatement.setDouble(5, lat);
            pStatement.setDouble(6, lng);
            pStatement.addBatch();
        }
        catch (SQLException e)
        {
            throw new IllegalStateException("Failed to insert traffic results.");
        }
        
        return;
    }
    *
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String path = args[0];
        int ID = Integer.parseInt(args[1]);
        int numObjs = Integer.parseInt(args[2]);
        int time = Integer.parseInt(args[3]);
        double uplat = Double.valueOf(args[4]);
        double uplng = Double.valueOf(args[5]);
        double lwlat = Double.valueOf(args[6]);
        double lwlng = Double.valueOf(args[7]);
        
        
        TR = new TrafficRequest(traffic.TrafficRequest.TrafficType.Random, ID, numObjs, time, path, uplat, uplng, lwlat, lwlng);
        
        Nodes = new HashMap<Long, Node>();
        Edges = new HashMap<Long, Edge>();
        
        rand = new Random();
        
//for now        createDirectories(ID);
//for now        generateShapeFiles(TR, path);
        
//for now        ReadNodeFile(path + "/output.node");
//for now        ReadEdgeFile(path + "/output.edge");
        //Output(path);
        
        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            File outFile = new File(path + ID + "/output.txt");
            
            fw = new FileWriter(outFile);
            pw = new PrintWriter(fw);
            /*
            for (Long key : Nodes.keySet()) {
                Node n = Nodes.get(key);
                pw.println(n.ID + "\t" + n.Latitude + "\t" + n.Longitude);
            }
            pw.close();
            fw.close();
            */

        } catch (IOException ex) {
            //Logger.getLogger(RoadTrans.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            /*
            try {
                //fw.close();
            } catch (IOException ex) {
                //Logger.getLogger(RoadTrans.class.getName()).log(Level.SEVERE, null, ex);
            }
            */
        }
        
        tigerReaderOutput(path + ID + "/node.txt", path + ID + "/edge.txt");
        InitializeObjects(TR, pw);
        CreateTraffic(TR, pw);
        pw.close();

        try {
            fw.close();
        } catch (IOException ex) {
            
        }

        /*
        try
        {
            //pStatement.executeBatch();
            //pStatement.clearBatch();
        }
        catch (SQLException e)
        {
            throw new IllegalStateException("Failed to insert traffic results.");
        } finally {
                //ConnectionProvider.safeClose(pStatement);
                pStatement = null;
        }
        */
        //copyFiles(path, DOWNLOAD_PATH, ID);
        
    }
}

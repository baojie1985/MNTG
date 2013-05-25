/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigerreader;

import java.io.BufferedReader;
import java.io.File;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import sun.tools.tree.ThisExpression;


import tigerreader.Edge;
import tigerreader.Node;

/**
 *
 * @author Ethan Waytas - wayt0012@umn.edu
 */
public class TigerReader {

    
    static HashMap<Long, ArrayList<Node>> Nodes;
    static HashMap<Long, Edge> Edges;
    static long nodeID = 0;
    static long edgeID = 0;
    
    static long HASHKEY = 1000003;
    
    static double uplat;
    static double uplng;
    static double lwlat;
    static double lwlng;
    
    /**
     * Calculate the hash value for a node
     * @param lng - longitude to calculate
     * @return - the hash value
     */
    public static long calcHash(double lng)
    {
        long hash_value = Math.abs((long)((lng * 1000000) + 0.5) % HASHKEY);
        
        return hash_value;
        
    }
    
    /**
     * Inserts a node only if it does not already exist.
     * @param n - node to insert
     * @return - returns the id of the node
     */
    public static long insertNode(Node n)
    {
        
        long hash_value = calcHash(n.Longitude);
        
        ArrayList<Node> temp = Nodes.get(hash_value);
        
        if (temp == null)
        {
            temp = new ArrayList<Node>();
            
            if (n.AttachedEdges == null)
            {
                n.AttachedEdges = new ArrayList<Long>();
            }
            
            temp.add(n);
            
            Nodes.put(hash_value, temp);
            //nodeID++;
            return n.ID;
        }
        
        for(int i = 0; i < temp.size(); i++)
        {
            Node test = temp.get(i);
            
            if (test.Latitude == n.Latitude && test.Longitude == n.Longitude)
            {
                return test.ID;
            }
            
        }
        
        if (n.AttachedEdges == null)
        {
            n.AttachedEdges = new ArrayList<Long>();
        }
        
        temp.add(n);
        //nodeID++;
        return n.ID;
    }
    
    /**
     * Inserts each node into the other's edge list.
     * @param node1
     * @param node2 
     */
    public static void addToEdges(Node node1, Node node2)
    {
        long hash_value_n1 = calcHash(node1.Longitude);
        long hash_value_n2 = calcHash(node2.Longitude);
        
        ArrayList<Node> temp1 = Nodes.get(hash_value_n1);
        ArrayList<Node> temp2 = Nodes.get(hash_value_n2);
        
        Node temp_n1 = null;
        Node temp_n2 = null;
        
        
        
        for (Node n : temp1)
        {
            if (n.Latitude == node1.Latitude && n.Longitude == node1.Longitude)
            {
                temp_n1 = n;
            }
        }
        
        for (Node n : temp2)
        {
            if (n.Latitude == node2.Latitude && n.Longitude == node2.Longitude)
            {
                temp_n2 = n;
            }
        }
        

        if (!temp_n1.AttachedEdges.contains(temp_n2.ID))
        {
            temp_n1.AttachedEdges.add(temp_n2.ID);
        }
        
        if (!temp_n2.AttachedEdges.contains(temp_n1.ID))
        {
            temp_n2.AttachedEdges.add(temp_n1.ID);
        }
        
        return;
    }
    
    /**
     * Checks to see if two locations are within the specified boundary in the
     * Traffic Request. We only need one of the nodes to be in the boundary to
     * accept
     * @param lat1 - latitude of node 1
     * @param lng1 - longitude of node 1
     * @param lat2 - latitude of node 2
     * @param lng2 - longitude of node 2
     * @return 
     */
    public static boolean checkBounds(double lat1, double lng1, double lat2, double lng2)
    {
        
        if (lat1 <= uplat && lng1 >= uplng && lat1 >= lwlat && lng1 <= lwlng)
        {
            return true;
        }
        
        if (lat2 <= uplat && lng2 >= uplng && lat2 >= lwlat && lng2 <= lwlng)
        {
            return true;
        }
        
        return false;
    }
    
    
    public static Edge.ROADTYPE getRoadType(String[] a, int i)
    {
        

        if (a[i].equals("\"C\""))
        {
            return Edge.ROADTYPE.C;
        }
        
        if (a[i].equals("\"I\""))
        {
            return Edge.ROADTYPE.I;
        }

        if (a[i].equals("\"M\""))
        {
            return Edge.ROADTYPE.M;
        }
        
        if (a[i].equals("\"O\""))
        {
            return Edge.ROADTYPE.O;
        }

        if (a[i].equals("\"S\""))
        {
            return Edge.ROADTYPE.S;
        }

        if (a[i].equals("\"U\""))
        {
            return Edge.ROADTYPE.U;
        }

        return Edge.ROADTYPE.UNKNOWN;
        
    }
    
    /**
     * Reads through a tiger file (<filename>.data)
     * @param file - path to the file
     */
    public static void readTigerFile(String file)
    {
        
        BufferedReader br = null;
        try {
           br = new BufferedReader(new FileReader(file));
        }
        catch (FileNotFoundException e) {
            System.out.println(e.toString());
        }
        
        String line = "";
        
        double lng1 = 0;
        double lat1 = 0;
        double lng2 = 0;
        double lat2 = 0;
        
        String road_name = "Unknown";
        Edge.ROADTYPE roadtype = Edge.ROADTYPE.UNKNOWN;
        
        try {
            while ((line = br.readLine()) != null)
            {
                line = line.trim();
                line = line.replaceAll("\\(", "");
                line = line.replaceAll("\\)", "");
                String[] temp = line.split("  ");
                
                if (temp.length != 4)
                {
                    if (line.contains("<text>"))
                    {
                        
                        int start = line.indexOf(">");
                        int end = line.lastIndexOf("<");
                        
                        road_name = line.substring(start + 1, end);   
                        
                        if (road_name.equals(""))
                        {
                            road_name = "No Name Available";
                        }
                        
                        temp = line.split("  ");
                    
                        roadtype = getRoadType(temp, 4);

                    }

                    continue;
                }
                
                lng1 = Double.parseDouble(temp[0]);
                lat1 = Double.parseDouble(temp[1]);
                lng2 = Double.parseDouble(temp[2]);
                lat2 = Double.parseDouble(temp[3]);
                
                if (!checkBounds(lat1, lng1, lat2, lng2))
                {
                    continue;
                }
                
                Node node1 = new Node(nodeID, lat1, lng1);
                nodeID++;
                        
                Node node2 = new Node(nodeID, lat2, lng2);
                nodeID++;
                        
                long a = insertNode(node1);
                
                if (a != node1.ID)
                {
                    nodeID--;
                    node2.ID = nodeID;
                }
                
                long b = insertNode(node2);
                
                if (b != node2.ID)
                {
                    nodeID--;
                }
                
                Edge newEdge = new Edge(edgeID, a, b, road_name, roadtype);
                
                Edges.put(edgeID, newEdge);
                
                edgeID++;
                
                addToEdges(node1, node2);
            }
        }
        catch(IOException e)
        {
            System.out.println(e.toString());
        }
        
        try
        {
            br.close();
        }
        catch (IOException e)
        {
            System.out.println(e.toString());
        }
        return;
    }
    
    /**
     * Write the road network out to the node and edge files (txt format)
     * @param id - Traffic request id
     * @param pwNode - node file
     * @param pwEdge - edge file
     */
    public static void outputNetwork(int id, PrintWriter pwNode, PrintWriter pwEdge)
    {
        for (Map.Entry<Long, ArrayList<Node>> entry : Nodes.entrySet())
        {
            ArrayList<Node> temp = entry.getValue();
            
            for(int i = 0; i < temp.size(); i++)
            {
                
                Node n = temp.get(i);
                
                ArrayList<Long> edges = n.AttachedEdges;
                
                for (int j = 0; j < edges.size(); j++)
                {
                    pwNode.println(n.ID + "\t" + n.Latitude + "\t" + n.Longitude + "\t" + edges.get(j));
                }
            } 
        }
        
        for (Map.Entry<Long, Edge> entry : Edges.entrySet())
        {
            pwEdge.println(entry.getKey() + "\t" + entry.getValue().Node1 + "\t" + entry.getValue().Node2 + "\t" + entry.getValue().Name + "\t" + stringRoadType(entry.getValue().roadtype));
        }
    }
    
    
    public static String stringRoadType(Edge.ROADTYPE t)
    {
        
        switch(t)
        {
            case C:
                return "County";
            case I:
                return "Interstate";
            case M:
                return "Common Name";
            case O:
                return "Other";
            case S:
                return "State";
            case U:
                return "U.S.";
            default:
                return "Unknown";
        }
        
    }
            
    
    /**
     * 
     * @param path - desired path to store output
     */
    public static void createDirectory(String path)
    {
        
        File check = new File(path);
        
        if (!check.exists() || !check.isDirectory()) {
            new File(path).mkdir();
        }
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        //The path should have the trailing '/'
        // e.g. /path/to/location/
        String path = args[0];
        //id of the traffic request
        int id = Integer.valueOf(args[1]);
        
        //boundary box
        uplat = Double.valueOf(args[2]);
        uplng = Double.valueOf(args[3]);
        lwlat = Double.valueOf(args[4]);
        lwlng = Double.valueOf(args[5]);
        
        //creates the directory if it does not exist
        createDirectory(path + id + "/");
        
        //new files for the output of the road network
        File outFileNode = new File(path + "tiger_processed_" + id + "_nodes.txt");
        File outFileEdge = new File(path + "tiger_processed_" + id + "_edge.txt");
        
        FileWriter fwNode = null;
        FileWriter fwEdge = null;
        
        try
        {
            fwNode = new FileWriter(outFileNode);
            fwEdge = new FileWriter(outFileEdge);
        }
        catch (IOException e)
        {
            System.out.println(e.toString());
        }
        
        PrintWriter pwNode = new PrintWriter(fwNode);
        PrintWriter pwEdge = new PrintWriter(fwEdge);
        
        //headers at the top of the file for easy reading
        pwNode.println("Node_ID\tLatitude\tLongitude\tConnected_Edge");
        pwEdge.println("Edge_ID\tNode1\tNode2\tRoad Name\tRoad Type");
        
        
        String countyFile = "";
        
        Nodes = new HashMap<Long, ArrayList<Node>>();
        Edges = new HashMap<Long, Edge>();
        
        
        /*
         * The remaining arguements after the first 6 will be the county files
         */
        
        for (int i = 6; i < args.length; i++)
        {
            
            countyFile = args[i];
            
            try
            {
                readTigerFile(countyFile + ".data");
            }
            catch (Exception e)
            {
                System.out.println(e.toString());
                e.printStackTrace();
            }
            //writes the road network to the file
            outputNetwork(id, pwNode, pwEdge);
            
            //reset everything for the next county file
            Nodes.clear();
            Edges.clear();
        
        }
        
        pwNode.close();
        pwEdge.close();
        try
        {
            fwNode.close();
            fwEdge.close();
        }
        catch (IOException e)
        {
            System.out.println(e.toString());
        }
        
    }
}

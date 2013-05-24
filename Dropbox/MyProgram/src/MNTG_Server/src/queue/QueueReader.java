package queue;

import generator.TrafficGenerator;
import generator.TrafficGeneratorFactory;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.mail.MessagingException;

import traffic.TrafficRequest;
import traffic.TrafficResult;
import util.Emailer;
import connection.ConnectionProvider;
import java.io.*;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.ProcessLauncher;

public class QueueReader {

    private final ExecutorService pool = Executors.newFixedThreadPool(5);
    private List<TrafficRequest> trafficRequestQueueCache = new ArrayList<TrafficRequest>();

    public boolean isRequestPending() {
        // System.out.println("Selecting the pending tasks from the database server.");
        Connection conn = ConnectionProvider.getConnection();

        Statement statement = null;
        try {
            statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("select count(*) from traffic_requests where ready = 1 && finished=0");
            if (rs.next()) {
                //System.out.println("There are " + rs.getInt(1) + " tasks in the queue.");
                return rs.getInt(1) > 0;
            } else {
                System.out.println("Error");
                throw new IllegalStateException(
                        "Could not get the row result from the count query.");
            }
            //System.out.println("Total number of pending tasks: "+rs.);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionProvider.safeClose(statement);
        }
    }

    private void fillQueueCache() {
        Connection conn = ConnectionProvider.getConnection();
        Statement statement = null;

        try {
            statement = conn.createStatement();

            ResultSet rs = statement.executeQuery("select id, name, email, upperlat, upperlong, lowerlat, lowerlong, objBegin, extObjBegin, objPerTime, extObjPerTime, numObjClasses, numExtObjClasses, maxTime, reportProb, msd, scaleFactor from traffic_requests TR where TR.ready = 1 and TR.finished = 0 order by TR.created");
            while (rs.next()) {
                trafficRequestQueueCache.add(new TrafficRequest(rs.getInt(1),
                        rs.getString(2), rs.getString(3), rs.getDouble(4), rs.getDouble(5), rs.getDouble(6),
                        rs.getDouble(7), rs.getInt(8), rs.getInt(9), rs.getInt(10), rs.getInt(11), rs.getInt(12), rs.getInt(13), rs.getInt(14), rs.getInt(15), rs.getInt(16), rs.getDouble(17)));
                //String sql = "UPDATE traffic_requests TR SET Ready=2 WHERE id="+rs.getInt(1);
                //System.out.println(sql+"\t and "+trafficRequestQueueCache.size());
                //statement.execute(sql);

            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionProvider.safeClose(statement);
        }
    }

    public void proccessQueue_sequential() {
        fillQueueCache();

        Connection conn = ConnectionProvider.getConnection();
        PreparedStatement pStatement = null;
        try {
            pStatement = conn.prepareStatement("insert into traffic_results (traffic_request_id, type, object_id, timestamp, lat, lng) values (?,?,?,?,?,?)");

            int result = 1;
            Iterator<TrafficRequest> trafficRequestIterator = trafficRequestQueueCache.iterator();
            while (trafficRequestIterator.hasNext()) {
                TrafficRequest trafficRequest = null;
                try {
                    trafficRequest = trafficRequestIterator.next();
                    TrafficGenerator generator = TrafficGeneratorFactory.getGenerator(trafficRequest);
                    generator.startTraffic(trafficRequest);
                    while (generator.hasTraffic()) {
                        List<TrafficResult> trafficResults = generator.getTraffic(trafficRequest);
                        for (TrafficResult trafficResult : trafficResults) {
                            pStatement.setInt(1, trafficRequest.getRequestId());
                            pStatement.setString(2, trafficResult.getType());
                            pStatement.setInt(3, trafficResult.getId());
                            pStatement.setInt(4, trafficResult.getTime());
                            pStatement.setDouble(5, trafficResult.getLat());
                            pStatement.setDouble(6, trafficResult.getLng());
                            pStatement.addBatch();
                        }
                        pStatement.executeBatch();
                        pStatement.clearBatch();
                    }
                    generator.endTraffic();

                } catch (Throwable t) {
                    result = -1;
                    throw new RuntimeException(t);
                } finally {
                    trafficRequestIterator.remove();
                    if (trafficRequest != null) {
                        markTrafficRequestCompleted(trafficRequest, result);
                        try {
                            Emailer.sendEmail(trafficRequest, result);
                        } catch (MessagingException e) {
                            throw new RuntimeException(e);
                        } catch (UnsupportedEncodingException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to insert traffic results.");
        } finally {
            ConnectionProvider.safeClose(pStatement);
        }
        trafficRequestQueueCache.clear();
    }

    public static void proccessTrafficRequest(TrafficRequest trafficRequest) {

        System.out.println("Processing traffic request #" + trafficRequest.getRequestId() + " from " + trafficRequest.getEmail());

        Connection conn = ConnectionProvider.getConnection();
        PreparedStatement pStatement = null;
        try {
            pStatement = conn.prepareStatement("insert into traffic_results (traffic_request_id, type, object_id, timestamp, lat, lng) values (?,?,?,?,?,?)");

            int result = 1;
            File RESULT_FILES = new File("/home/yackel/public_html/app/webroot/downloads/" + trafficRequest.getRequestId() + ".txt");
            FileWriter outFile;
            PrintWriter outFilePrint = null;

            outFile = new FileWriter(RESULT_FILES);
            outFilePrint = new PrintWriter(outFile);
            outFilePrint.println("Object_Id" + " " + " Timestamp" + " " + "Type" + " " + "Lat" + " " + "Lng \n");
            try {
                TrafficGenerator generator = TrafficGeneratorFactory.getGenerator(trafficRequest);
                generator.startTraffic(trafficRequest);
                while (generator.hasTraffic()) {
                    List<TrafficResult> trafficResults = generator.getTraffic(trafficRequest);
                    for (TrafficResult trafficResult : trafficResults) {

                        pStatement.setInt(1, trafficRequest.getRequestId());
                        pStatement.setString(2, trafficResult.getType());
                        pStatement.setInt(3, trafficResult.getId());
                        pStatement.setInt(4, trafficResult.getTime());
                        pStatement.setDouble(5, trafficResult.getLat());
                        pStatement.setDouble(6, trafficResult.getLng());

                        pStatement.addBatch();
                    }
                    pStatement.executeBatch();
                    pStatement.clearBatch();
                }
                generator.endTraffic();
                String query = "SELECT * from traffic_results where traffic_request_id =" + trafficRequest.getRequestId();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(query);
                while (rs.next()) {
                    int id = rs.getInt("object_id");
                    String type = rs.getString("type");
                    if (type == "") {
                        type = "untyped";
                    }
                    int time = rs.getInt("timestamp");
                    double lat = rs.getDouble("lat");
                    double lng = rs.getDouble("lng");
                    outFilePrint.println(id + " " + time + " " + type + " " + lat + " " + lng);
                    outFilePrint.flush();
                }

                outFilePrint.close();
            } catch (Throwable t) {
                result = -1;
                throw new RuntimeException(t);
            } finally {
                if (trafficRequest != null) {
                    markTrafficRequestCompleted(trafficRequest, result);
                    try {
                        Emailer.sendEmail(trafficRequest, result);
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(QueueReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to insert traffic results.");
        } finally {
            ConnectionProvider.safeClose(pStatement);
        }
        System.out.println("Processing traffic request #" + trafficRequest.getRequestId() + " from " + trafficRequest.getEmail() + " is done.");
    }
    public static SharedInteger runningThreadsCount;

    public int hasThread(HashMap<Integer, TrafficRequestThread> threads) {
        for (int i = 0; i < threads.size(); i++) {
            TrafficRequestThread tr = threads.get(i);
            if (tr.flag == true) {
                return i;
            }
        }
        return -1;
    }

    public void proccessQueue() {
        fillQueueCache();
        System.out.println("traffic queue filled");
        boolean waitedLongTime = false;

        int maxThreadsRunning = 25;
        runningThreadsCount = new SharedInteger();
        runningThreadsCount.set(0);

        Iterator<TrafficRequest> trafficRequestIterator = trafficRequestQueueCache.iterator();
        /*
         * HashMap<Integer,TrafficRequestThread> threads = new HashMap<Integer,
         * TrafficRequestThread>();
         *
         * for (int i = 0; i < 5; i++) { TrafficRequestThread tr = new
         * TrafficRequestThread(); threads.put(i, tr); }
         */
        while (trafficRequestIterator.hasNext()) {
            TrafficRequest trafficRequest = null;
            try {

                trafficRequest = trafficRequestIterator.next();

               
                File folder = new File("/home/yackel/TrafficGenerator/" + trafficRequest.getRequestId() + "/");  //make target directory
                folder.mkdir();
                folder.setWritable(true);
                String command = " java -jar /media/Louai/MNTGExtractor.jar /media/Louai/osm/road_edges_partitioned/ /home/yackel/TrafficGenerator/" + " " + trafficRequest.getRequestId() + " " + trafficRequest.getInorderCoordinates() + " >> \\home\\yackel\\TrafficGenerator\\RoadLog.txt";// 44.891 -93.280 44.983 -93.086";

                System.out.println(command);

                ProcessLauncher.exec("java -jar /media/Louai/MNTGExtractor.jar /media/Louai/osm/road_edges_partitioned/ /home/yackel/TrafficGenerator/" + " " + trafficRequest.getRequestId() + " " + trafficRequest.getInorderCoordinates() , false);


                proccessTrafficRequest(trafficRequest);
 if (trafficRequest.getMaprequestStatus() == true) {
                    //ProcessLauncher.exec(" java -jar /media/Louai/MNTGExtractor.jar /media/Louai/osm/road_edges_partitioned/ /home/yackel/TrafficGenerator/900/ 900 44.891 -93.280 44.983 -93.086", false); //output the text file of the road network to the destination directiory
                    ProcessLauncher.exec( "cp /home/yackel/TrafficGenerator/"+trafficRequest.getRequestId()+"/node.txt" +"/home/yackel/public_html/app/webroot/downloads/"+trafficRequest.getRequestId()+"-Nodes.txt" , false);
                    ProcessLauncher.exec( "cp /home/yackel/TrafficGenerator/"+trafficRequest.getRequestId()+"/edge.txt" +"/home/yackel/public_html/app/webroot/downloads/"+trafficRequest.getRequestId()+"-Edges.txt" , false);
                }



                //pararell computing
                // TrafficRequestThread trt = new TrafficRequestThread(trafficRequest); 
                //pool.execute(trt);

                //trt.run();;

                //proccessTrafficRequest(trafficRequest);

                /*
                 * long start = System.currentTimeMillis(); while(true) {
                 * if(runningThreadsCount.get() < maxThreadsRunning ||
                 * waitedLongTime) { TrafficRequestThread trt = new
                 * TrafficRequestThread(trafficRequest); trt.start();
                 * threads.add(trt); runningThreadsCount.increment(); break; }
                 * long end = System.currentTimeMillis(); if((end-start) >
                 * 12*60*60*1000)//12 hours allowance { waitedLongTime = true;
                 * break; } }
                 */
            } catch (Throwable t) {
                throw new RuntimeException(t);
            } finally {
                trafficRequestIterator.remove();
            }
        }

        /*
         * Iterator<TrafficRequestThread> trafficRequestThreadIterator =
         * threads.iterator(); while (trafficRequestThreadIterator.hasNext()) {
         * TrafficRequestThread thread = null; try{ thread =
         * trafficRequestThreadIterator.next(); thread.join(3*60*60*1000);//3
         * hours allowance } catch (Throwable t) { throw new
         * RuntimeException(t); }
         *
         * }
         */

        trafficRequestQueueCache.clear();
    }

    private static void markTrafficRequestCompleted(TrafficRequest trafficRequest,
            int result) {
        Connection conn = ConnectionProvider.getConnection();
        Statement statement = null;
        try {
            statement = conn.createStatement();
            statement.execute("update traffic_requests TR set TR.finished = "
                    + result + " where TR.id = "
                    + trafficRequest.getRequestId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionProvider.safeClose(statement);
        }
    }
}

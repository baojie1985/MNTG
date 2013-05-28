package generator;

import java.awt.geom.Point2D;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import traffic.TrafficRequest;
import traffic.TrafficResult;
import util.ProcessLauncher;

public class BerlinModGenerator extends AbstractTrafficGenerator {

    public static String SECONDO_BIN_DIRECTORY = "/home/yackel/secondo/bin";
    public static String REQUEST_INPUT_FOLDER = "/home/yacket/TrafficGenerator";
    public static String DATA_FILE_HEADER = "(OBJECT streets () (rel (tuple ((Vmax real)(geoData line))))(\n";
    public static String STREET_HEADER = "(50.0(";
    public static String STREET_FOOTER = "\n";
    public static String DATA_FILE_FOOTER = "))\n";
    private static int PAGE_SIZE = 1000;
    private Scanner trafficReader;
    private int timestamp = 0;
    private int previousTripId = 0;
    private int motorId = 1;

    public void startTraffic(TrafficRequest trafficRequest) {
        mergeParameterFile(trafficRequest);
        generateTraffic(trafficRequest);
        File trafficData = new File(SECONDO_BIN_DIRECTORY, "trips" + trafficRequest.getRequestId() + ".csv");

        if (trafficRequest.getScaleFactor() < 1) {
            trafficRequest.setScaleFactor(1);
        }
        if (trafficRequest.getScaleFactor() > 2000) {
            trafficRequest.setScaleFactor(2000);
        }

        try {
            trafficReader = new Scanner(trafficData);
            trafficReader.nextLine(); // ignore the column headers
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void endTraffic() {
        trafficReader.close();
    }

    public boolean hasTraffic() {
        return trafficReader.hasNextLine();
    }

    private void mergeParameterFile(TrafficRequest trafficRequest) {
        FileWriter output = null;
        Scanner preScanner = null;
        Scanner postScanner = null;
        try {
            output = new FileWriter(new File(SECONDO_BIN_DIRECTORY,
                    "BerlinMOD_DataGenerator_" + trafficRequest.getRequestId() + ".SEC"));
            preScanner = new Scanner(new File(SECONDO_BIN_DIRECTORY,
                    "BerlinMOD_DataGenerator_Jie.SEC_pre"));
            postScanner = new Scanner(new File(SECONDO_BIN_DIRECTORY,
                    "BerlinMOD_DataGenerator_Jie.SEC_post"));

            while (preScanner.hasNextLine()) {
                String line = preScanner.nextLine().replace("#requestid#", String.valueOf(trafficRequest.getRequestId()));
                output.write(line
                        + System.getProperty("line.separator"));
            }
            output.write("let SCALEFACTOR = " + (0.005)
                    + ";"
                    + System.getProperty("line.separator"));
            int num_cars = (int) trafficRequest.getScaleFactor();
            output.write("let P_NUMCARS = "
                    + num_cars + ";"
                    + System.getProperty("line.separator"));

            while (postScanner.hasNextLine()) {
                String line = postScanner.nextLine().replace("#requestid#", String.valueOf(trafficRequest.getRequestId()));
                output.write(line
                        + System.getProperty("line.separator"));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            preScanner.close();
            postScanner.close();
            try {
                output.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void generateTraffic(TrafficRequest trafficRequest) {
        List<File> counties = getCounties(trafficRequest);

        File streetsData = new File(SECONDO_BIN_DIRECTORY, "streets" + trafficRequest.getRequestId() + ".data");
        File trafficData = new File(SECONDO_BIN_DIRECTORY, "trips" + trafficRequest.getRequestId() + ".csv");
        trafficData.setReadable(true);
        streetsData.delete();
        trafficData.delete();


        FileWriter streetsDataWriter = null;
        try {
            streetsDataWriter = new FileWriter(streetsData);

            streetsDataWriter.write(DATA_FILE_HEADER);

            // OLD: Convert data from TIGER shape format to SECONDO format
//            for (File county : counties) {
//                proccessCounty(county, streetsDataWriter, trafficRequest);
//            }
            // Convert data from OSM file format to SECONDO format
            File requestPath = new File(REQUEST_INPUT_FOLDER, String.valueOf(trafficRequest.getRequestId()));
            File nodeFile = new File(requestPath, "node.txt");
            File edgeFile = new File(requestPath, "edge.txt");
            processOSMFile(nodeFile, edgeFile, streetsDataWriter, trafficRequest);
            
            streetsDataWriter.write(DATA_FILE_FOOTER);
            streetsDataWriter.close();

            ProcessLauncher.exec(new String[]{
                        "/home/yackel" + File.separator + "secondo"
                        + File.separator + "bin" + File.separator
                        + "SecondoTTYNT", "-i",
                        "BerlinMOD_DataGenerator_" + trafficRequest.getRequestId() + ".SEC"}, new File(
                    SECONDO_BIN_DIRECTORY), true);
            ProcessLauncher.exec(
                    "tar cvzf /home/yackel/public_html/app/webroot/downloads/" + trafficRequest.getRequestId() + ".tar.gz -C /home/yackel/secondo/bin/ "
                    + "trips" + trafficRequest.getRequestId() + ".csv "
                    + "streets" + trafficRequest.getRequestId() + ".csv "
                    + "journey" + trafficRequest.getRequestId() + ".csv "
                    + "datamcar" + trafficRequest.getRequestId() + ".csv "
                    + "queryregions" + trafficRequest.getRequestId() + ".csv "
                    + "querypoints" + trafficRequest.getRequestId() + ".csv "
                    + "queryperiods" + trafficRequest.getRequestId() + ".csv "
                    + "queryinstants" + trafficRequest.getRequestId() + ".csv "
                    + "streets" + trafficRequest.getRequestId() + ".data", true);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<TrafficResult> getTraffic(TrafficRequest trafficRequest) {
        List<TrafficResult> trafficResults = new ArrayList<TrafficResult>();
        TrafficResult trafficResult = null;
        File RESULT_FILES = new File("/home/yackel/public_html/app/webroot/downloads/" + trafficRequest.getRequestId() + ".txt");
        FileWriter outFile;
        PrintWriter outFilePrint = null;
        try {
            outFile = new FileWriter(RESULT_FILES);
            outFilePrint = new PrintWriter(outFile);
        } catch (IOException ex) {
            Logger.getLogger(BerlinModGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        RESULT_FILES.setReadable(true);
        RESULT_FILES.setExecutable(true);

       // outFilePrint.append("Object_Id" + " " + " Timestamp" + " " + "Type" + " " + "Lat" + " " + "Lng \n");
        String line = null;

        // data format of each line is
        // Moid,Tripid,Tstart,Tend,Xstart,Ystart,Xend,Yend
        while (trafficReader.hasNextLine()) {
            line = trafficReader.nextLine();
            String[] splitResults = line.split(",");

            int tripId = Integer.parseInt(splitResults[1]);
            trafficResult = new TrafficResult();

            boolean allowBreak = false;

            if (previousTripId != tripId) {
                if (timestamp == 1) { // found a trip of size 1. delete the
                    // previous result to ignore it
                    trafficResults.remove(trafficResults.size() - 1);
                }
                timestamp = 0;
                motorId++;
                trafficResult.setLat(Double.parseDouble(splitResults[5]));
                trafficResult.setLng(Double.parseDouble(splitResults[4]));
                allowBreak = false;
            } else {
                trafficResult.setLat(Double.parseDouble(splitResults[7]));
                trafficResult.setLng(Double.parseDouble(splitResults[6]));
                allowBreak = true;
            }
            trafficResult.setType("");
            trafficResult.setId(motorId);
            trafficResult.setTime(timestamp++);
            trafficResults.add(trafficResult);
            previousTripId = tripId;
            
            //outFilePrint.append(trafficResult.getId() + " " + trafficResult.getTime() + " " + trafficResult.getType() + " " + trafficResult.getLat() + " " + trafficResult.getLng() + "\n");
           
            if (allowBreak && trafficResults.size() >= PAGE_SIZE) {
                break;
            }
        }
        outFilePrint.close();
        return trafficResults;
    }
    
    
    Pattern decimalPattern = Pattern.compile("-?[0-9]+(\\.[0-9]+)?");

    
    private void proccessCounty(File county, FileWriter writer,
            TrafficRequest trafficRequest) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(county.getAbsolutePath()
                    + ".data"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            List<String> streets = new ArrayList<String>();
            String line = null;
            boolean validStreet = false;

            while ((line = reader.readLine()) != null) {
                if (line.contains("\"")) { // reset the current street data
                    if (!streets.isEmpty()) {
                        if (validStreet) {
                            writeStreets(streets, writer);
                        }
                        streets.clear();
                        reader.readLine(); // eat the extra paren
                        validStreet = false;
                    }
                } else if (line.contains("-")) { // contains a negative number
                    // (i.e. we are in the U.S.
                    // latitude
                    validStreet |= isValidStreetLine(line, trafficRequest);
                    streets.add(line);
                }
            }

            fixEndOfFileClosingParens(streets); // the final line of
            // each file
            // will have extra parens
            if (!streets.isEmpty()) { // write the final street
                writeStreets(streets, writer);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void processOSMFile(File nodeFile, File edgeFile, FileWriter writer,
        TrafficRequest trafficRequest) {
      try {
        // Read all nodes
        Map<Long, Point2D.Double> nodes = new HashMap<Long, Point2D.Double>();
        BufferedReader reader = new BufferedReader(new FileReader(nodeFile.getAbsolutePath()));
        String line;
        while ((line = reader.readLine()) != null) {
          // Format of the input file is node_id, lat, lon
          String[] parts = line.split(",");
          Long nodeId = Long.parseLong(parts[0]);
          Point2D.Double coords = new Point2D.Double(
              Double.parseDouble(parts[2]), Double.parseDouble(parts[1]));
          nodes.put(nodeId, coords);
        }
        reader.close();
        
        // Read all edges and write directly to output files
        reader = new BufferedReader(new FileReader(edgeFile.getAbsolutePath()));
        
        writer.write("(50.0(\n");
        while ((line = reader.readLine()) != null) {
          String[] parts = line.split(",");
          //long edgeId = Long.parseLong(parts[0]);
          Point2D.Double node1 = nodes.get(Long.parseLong(parts[1]));
          Point2D.Double node2 = nodes.get(Long.parseLong(parts[2]));
          // Format of street file is (lon1 lat1 lon2 lat2) for each segment
          writer.write(String.format("(%g %g %g %g)\n", node1.x, node1.y, node2.x, node2.y));
        }
        writer.write("))\n");
      
        reader.close();
      } catch (FileNotFoundException e) {
        throw new RuntimeException(e);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    private void writeStreets(List<String> streets, FileWriter writer) {
        try {
            writer.write(STREET_HEADER);
            for (String street : streets) {
                writer.write(street + "\n");
            }
            writer.write(STREET_FOOTER);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void fixEndOfFileClosingParens(List<String> streets) {
        for (int i = 0; i < streets.size(); i++) {
            streets.set(i, streets.get(i).replace(")))))", ")))"));
        }
    }

    private boolean isValidStreetLine(String streetLine,
            TrafficRequest trafficRequest) {
        Scanner lineScanner = new Scanner(streetLine);
        double lng = Double.parseDouble(lineScanner.findInLine(decimalPattern));
        double lat = Double.parseDouble(lineScanner.findInLine(decimalPattern));
        if (trafficRequest.contains(lat, lng)) {
            return true;
        }

        lng = Double.parseDouble(lineScanner.findInLine(decimalPattern));
        lat = Double.parseDouble(lineScanner.findInLine(decimalPattern));
        if (trafficRequest.contains(lat, lng)) {
            return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return "BerlinMOD";
    }
}

package generator;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import traffic.TrafficRequest;
import traffic.TrafficResult;
import util.ProcessLauncher;

public class BrinkhoffGenerator extends AbstractTrafficGenerator {

    public static File GENERATED_FILE_NAME;     //outputfiles
    public static File GENERATED_SHAPE_FILES;
    public static File RESULT_FILES;
    public static String GENERATED_FOLDER;
    public static final File TRAFFIC_GENERATOR_JAR = new File(
            "/home/yackel/TrafficGenerator/CompleteSource21/newGenerator.jar");
    public static final String SHAPE_NETWORK_FILE_MANAGER_CLASSPATH = "/home/yackel/TrafficGenerator/ShapeNetworkFileManager/generator21.jar"
            + File.pathSeparator
            + "/home/yackel/TrafficGenerator/ShapeNetworkFileManager/geotool2.jar"
            + File.pathSeparator
            + "/home/yackel/TrafficGenerator/ShapeNetworkFileManager/JTS13.jar"
            + File.pathSeparator
            + "/home/yackel/TrafficGenerator/ShapeNetworkFileManager";

    private void CreateNewPropertyFile() {
        ProcessLauncher.exec("cp /home/yackel/TrafficGenerator/common.txt /home/yackel/TrafficGenerator/" + this.requestid + "/tmp.txt", false);


        try {

            BufferedReader in = new BufferedReader(new FileReader("/home/yackel/TrafficGenerator/" + this.requestid + "/tmp.txt")); //commmonly read file change the parameters init.
            FileWriter outFile = new FileWriter(GENERATED_FOLDER + "properties.txt");
            
            
            PrintWriter outFilePrint = new PrintWriter(outFile);
            String line;
            while ((line = in.readLine()) != null) {
                if (line.contains("####") == false) {
                    outFilePrint.println(line);
                } else {
                    String id = String.valueOf(requestid);
                    line = line.replace("####", id);
                    outFilePrint.println(line);
                }
            }

            in.close();
            outFilePrint.close();
        } catch (IOException ex) {
            Logger.getLogger(BrinkhoffGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public BrinkhoffGenerator(TrafficRequest trafficRequest) {

        this.requestid = trafficRequest.getRequestId();
       
        GENERATED_FOLDER = "/home/yackel/TrafficGenerator/" + this.requestid + "/";
        GENERATED_SHAPE_FILES = new File("/home/yackel/TrafficGenerator/" + this.requestid + "/");
        GENERATED_FILE_NAME = new File("/home/yackel/TrafficGenerator/" + this.requestid + "/output.txt");
        RESULT_FILES = new File("/home/yackel/TrafficGenerator/" + this.requestid + "/result.txt");
        try {
            File parameterfile= new File(GENERATED_FOLDER + "parameter"+this.requestid+".txt");
            FileWriter parameterOut = new FileWriter(parameterfile);
            parameterOut.write(trafficRequest.toFileString());
            parameterOut.flush();
            parameterOut.close();
        } catch (IOException ex) {
            Logger.getLogger(BrinkhoffGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        CreateNewPropertyFile();
    }

    @Override
    public List<TrafficResult> getTraffic(TrafficRequest trafficRequest) {
        System.out.println(GENERATED_FILE_NAME.toString());
        FileWriter outFile;
        PrintWriter outFilePrint = null;
        try {
            outFile = new FileWriter(RESULT_FILES);
            RESULT_FILES.setReadable(true);
            RESULT_FILES.setExecutable(true);
            outFilePrint = new PrintWriter(outFile);
        } catch (IOException ex) {
            Logger.getLogger(BrinkhoffGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }

        StringBuffer sb = new StringBuffer();
        sb.append(trafficRequest.getUpperlat()).append(" ").append(trafficRequest.getUpperlong()).append(" ").append(trafficRequest.getLowerlat()).append(" ").append(trafficRequest.getLowerlong()).append(" ").append(this.requestid).append(" ");
        for (File countyFile : getCounties(trafficRequest)) {
            sb.append(countyFile.getAbsolutePath()).append(" ");
        }

        //ProcessLauncher.exec("java -classpath " + SHAPE_NETWORK_FILE_MANAGER_CLASSPATH                + " conversion.ShapeNetworkFileManager "                + sb.toString(), false);
        ProcessLauncher.exec("java -jar /home/yackel/TrafficGenerator/Test_Jie/RoadConverter.jar " +GENERATED_FOLDER, false);
        System.out.println("java -jar /home/yackel/TrafficGenerator/Test_Jie/RoadConverter.jar " +GENERATED_FOLDER);
        
        //ProcessLauncher.exec("cp /home/yackel/TrafficGenerator/output.* /home.yackel/TrafficGenerator/"+this.requestid+"/", false);
        //System.out.println("cp /home/yackel/TrafficGenerator/output.* /home/yackel/TrafficGenerator/"+this.requestid+"/");
        ArrayList<TrafficResult> trafficResults = new ArrayList<TrafficResult>();

        //GENERATED_FILE_NAME.delete();

        sb = new StringBuffer();
        sb.append(new File(GENERATED_SHAPE_FILES, "output").getAbsolutePath()).append(" ");
        sb.append(trafficRequest.getObjBegin()).append(" ");
        sb.append(trafficRequest.getExtObjBegin()).append(" ");
        sb.append(trafficRequest.getObjPerTime()).append(" ");
        sb.append(trafficRequest.getExtObjPerTime()).append(" ");
        sb.append(trafficRequest.getNumObjClasses()).append(" ");
        sb.append(trafficRequest.getNumExtObjClasses()).append(" ");
        sb.append(trafficRequest.getMaxTime()).append(" ");
        sb.append(trafficRequest.getReportProb()).append(" ");
        sb.append(trafficRequest.getMsd());

        System.out.println("2" + sb.toString());

        ProcessLauncher.exec(
                "java -classpath " + TRAFFIC_GENERATOR_JAR
                + " generator2.DefaultDataGenerator " + this.requestid + " "
                + sb.toString(), false);

        System.out.println("java -classpath " + TRAFFIC_GENERATOR_JAR + " generator2.DefaultDataGenerator " + this.requestid + " " + sb.toString());

         //ProcessLauncher.exec( "java -jar /home/yackel/TrafficGenerator/RoadTrans.jar /home/yackel/TrafficGenerator/"+this.requestid +"/ " + this.requestid , false);

     //   System.out.println("java -classpath " + TRAFFIC_GENERATOR_JAR + " generator2.DefaultDataGenerator " + this.requestid + " " + sb.toString());
        
        
        if (GENERATED_FILE_NAME.exists()) {
            try {
                BufferedReader bfr = new BufferedReader(new FileReader(
                        GENERATED_FILE_NAME));
                String line = null;
                
                //outFilePrint.append("Object_Id"+" "+" Timestamp" +" "+"Type"+" "+ "Lat"+" "+ "Lng \n");
                
                while ((line = bfr.readLine()) != null) {
                    Scanner scanner = new Scanner(line);
                    TrafficResult trafficResult = new TrafficResult();
                    trafficResult.setType(scanner.next());
                    trafficResult.setId(scanner.nextInt());
                    scanner.nextInt(); // sequence number
                    scanner.nextInt(); // object class
                    trafficResult.setTime(scanner.nextInt());
                    trafficResult.setLatByPoint(trafficRequest,
                            scanner.nextDouble());
                    trafficResult.setLngByPoint(trafficRequest,
                        scanner.nextDouble());
                    trafficResults.add(trafficResult);
                 //   outFilePrint.append(trafficResult.getId() + " " + trafficResult.getTime() + " " + trafficResult.getType() + " " + trafficResult.getLat() + " " + trafficResult.getLng() + "\n");
                }
                
                bfr.close();
                
                //outFilePrint.close();
                
                //ProcessLauncher.exec("cp /home/yackel/TrafficGenerator/"+this.requestid+"/result.txt /home/yackel/public_html/app/webroot/downloads/"+this.requestid+".txt", false);
                ProcessLauncher.exec("cp /home/yackel/TrafficGenerator/"+this.requestid+"/output.edge /home/yackel/public_html/app/webroot/downloads/"+this.requestid+".edge", false);
                ProcessLauncher.exec("cp /home/yackel/TrafficGenerator/"+this.requestid+"/output.node /home/yackel/public_html/app/webroot/downloads/"+this.requestid+".node", false);
                ProcessLauncher.exec("cp /home/yackel/TrafficGenerator/"+this.requestid+"/parameter"+this.requestid+".txt /home/yackel/public_html/app/webroot/downloads/parameter"+this.requestid+".txt", false);
            } catch (FileNotFoundException e) {
                throw new IllegalStateException("Failed to generate traffic.");
            } catch (IOException e) {
                throw new IllegalStateException("Failed to read traffic file.");
            }
        } else {
            throw new IllegalStateException("Failed to generate traffic.");
        }

        hasTraffic = false;
        return trafficResults;
    }

    @Override
    public String getName() {
        return "Thomas Brinkhoff";
    }
    private boolean hasTraffic;

    @Override
    public void startTraffic(TrafficRequest trafficRequest) {
        hasTraffic = true;
    }

    @Override
    public void endTraffic() {
    }

    @Override
    public boolean hasTraffic() {
        return hasTraffic;
    }
}

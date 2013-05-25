/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import traffic.TrafficRequest;
import traffic.TrafficResult;
import util.ProcessLauncher;

/**
 *
 * @author nagell2008
 */
public class RandomGenerator extends AbstractTrafficGenerator{
    
        private boolean hasTraffic;
        
        
        private static final String RANDOMGENERATORJAR = "/path/to/generator/RandomGenerator.jar";
    	
        private static final String WEBSERVERPATH = "/path/to/webserver/";
        
        @Override
        public List<TrafficResult> getTraffic(TrafficRequest trafficRequest)
        {
            File output = new File(WEBSERVERPATH + trafficRequest.getRequestId() + "/" + "output.txt");
            
            this.endTraffic();
            
            if (!output.exists())
            {
                return null;
            }
            
            BufferedReader br = null;
            
            boolean first_line = true;
            
            String line = "";
            
            int ID;
            int timeStamp;
            String objType;
            double lat;
            double lng;
            
            List<TrafficResult> traffic = new ArrayList<TrafficResult>();
            
            
            try 
            {
                while ((line = br.readLine()) != null)
                {
                    if (first_line)
                    {
                        first_line = false;
                        continue;
                    }
                    
                    String[] data = line.split("\t");
                    
                    ID = Integer.parseInt(data[0]);
                    timeStamp = Integer.parseInt(data[1]);
                    objType = data[2];
                    lat = Double.parseDouble(data[3]);
                    lng = Double.parseDouble(data[4]);
                    
                    TrafficResult result = new TrafficResult();
                    
                    result.setId(ID);
                    result.setTime(timeStamp);
                    result.setType(objType);
                    result.setLat(lat);
                    result.setLng(lng);
                    
                    traffic.add(result);
                    
                }
            }
            catch (IOException e)
            {
                System.out.println(e.toString());
            }
            
            return traffic;
            
        }

        @Override
	public String getName()
        {
            return "RandomGenerator";
        }

        @Override
	public void startTraffic(TrafficRequest trafficRequest)
        {
            hasTraffic = true;
            
            /*
             * 
             * Call is:
             * path - /blah/blah/
             * id - #
             * numObjs - ##
             * time - #
             * uplat - ##
             * uplng - ##
             * lwlat - ##
             * lwlng - ##
             * 
             */
            ProcessLauncher.exec("java -jar " + RANDOMGENERATORJAR + " "
                    + WEBSERVERPATH + " " + trafficRequest.getRequestId() + " "
                    + trafficRequest.getObjBegin() + " "
                    + trafficRequest.getMaxTime() + " "
                    + trafficRequest.getUpperlat() + " "
                    + trafficRequest.getUpperlong() + " "
                    + trafficRequest.getLowerlat() + " "
                    + trafficRequest.getLowerlong(), false);
            
            System.out.println("java -jar " + RANDOMGENERATORJAR + " "
                    + WEBSERVERPATH + " " + trafficRequest.getRequestId() + " "
                    + trafficRequest.getObjBegin() + " "
                    + trafficRequest.getMaxTime() + " "
                    + trafficRequest.getUpperlat() + " "
                    + trafficRequest.getUpperlong() + " "
                    + trafficRequest.getLowerlat() + " "
                    + trafficRequest.getLowerlong());

            File output = new File(WEBSERVERPATH + trafficRequest.getRequestId() + "/" + "output.txt");
            
            
            if (!output.exists())
            {
                hasTraffic = false;
            }

            
        }

        @Override
	public void endTraffic()
        {
            hasTraffic = false;
        }

        @Override
	public boolean hasTraffic()
        {
            return hasTraffic;
        }

    
    
}

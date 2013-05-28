package traffic;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import com.google.gson.*;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TrafficRequest {

    public enum TrafficType {

        Brinkhoff, BerlinMod,Random
    }
    private int requestId;
    private String name;
    private double upperlat;
    private double upperlong;
    private double lowerlat;
    private double lowerlong;
    private int objBegin;
    private int extObjBegin;
    private int objPerTime;
    private int extObjPerTime;
    private int numObjClasses;
    private int numExtObjClasses;
    private int maxTime;
    private int reportProb;
    private int msd;
    private String email;
    private double scaleFactor;
    private boolean maprequest;

    /**
     * @return the upperlat
     */
    public double getUpperlat() {
        return upperlat;
    }

    /**
     * @param upperlat the upperlat to set
     */
    public void setUpperlat(double upperlat) {
        this.upperlat = upperlat;
    }

    /**
     * @return the upperlong
     */
    public double getUpperlong() {
        return upperlong;
    }

    public String getCoordinates() { // <uplat> <uplng> <lwlat> <lwlng>
        String result = null;
        result = this.upperlat + " " + this.upperlong + " " + this.lowerlat + " " + this.lowerlong;
        return result;

    }

    public boolean getMaprequestStatus() {
        return maprequest;
    }

    /**
     * @param upperlong the upperlong to set
     */
    public void setUpperlong(double upperlong) {
        this.upperlong = upperlong;
    }

    /**
     * @return the lowerlat
     */
    public double getLowerlat() {
        return lowerlat;
    }

    /**
     * @param lowerlat the lowerlat to set
     */
    public void setLowerlat(double lowerlat) {
        this.lowerlat = lowerlat;
    }

    /**
     * @return the lowerlong
     */
    public double getLowerlong() {
        return lowerlong;
    }

    /**
     * @param lowerlong the lowerlong to set
     */
    public void setLowerlong(double lowerlong) {
        this.lowerlong = lowerlong;
    }

    public TrafficRequest(int requestId, String name, String email,
            double upperlat, double upperlong, double lowerlat,
            double lowerlong, int objBegin, int extObjBegin, int objPerTime,
            int extObjPerTime, int numObjClasses, int numExtObjClasses,
            int maxTime, int reportProb, int msd, double scaleFactor) {

        if (name.contains("@@")) {
            this.maprequest = true;
            this.name = name.replaceAll("@@", "");
        } else {
            this.maprequest = false;
            this.name = name;
        }

        this.email = email;
        this.requestId = requestId;
        this.upperlat = upperlat;
        this.upperlong = upperlong;
        this.lowerlat = lowerlat;
        this.lowerlong = lowerlong;
        this.objBegin = objBegin;
        this.extObjBegin = extObjBegin;
        this.objPerTime = objPerTime;
        this.extObjPerTime = extObjPerTime;
        this.numObjClasses = numObjClasses;
        this.numExtObjClasses = numExtObjClasses;
        this.maxTime = maxTime;
        this.reportProb = reportProb;
        this.msd = msd;
        this.scaleFactor = scaleFactor;
    }

    public void setRequest_id(int requestId) {
        this.requestId = requestId;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * @return the objBegin
     */
    public int getObjBegin() {
        return objBegin;
    }

    /**
     * @param objBegin the objBegin to set
     */
    public void setObjBegin(int objBegin) {
        this.objBegin = objBegin;
    }

    /**
     * @return the extObjBegin
     */
    public int getExtObjBegin() {
        return extObjBegin;
    }

    /**
     * @param extObjBegin the extObjBegin to set
     */
    public void setExtObjBegin(int extObjBegin) {
        this.extObjBegin = extObjBegin;
    }

    /**
     * @return the objPerTime
     */
    public int getObjPerTime() {
        return objPerTime;
    }

    /**
     * @param objPerTime the objPerTime to set
     */
    public void setObjPerTime(int objPerTime) {
        this.objPerTime = objPerTime;
    }

    /**
     * @return the extObjPerTime
     */
    public int getExtObjPerTime() {
        return extObjPerTime;
    }

    /**
     * @param extObjPerTime the extObjPerTime to set
     */
    public void setExtObjPerTime(int extObjPerTime) {
        this.extObjPerTime = extObjPerTime;
    }

    /**
     * @return the numObjClasses
     */
    public int getNumObjClasses() {
        return numObjClasses;
    }

    /**
     * @param numObjClasses the numObjClasses to set
     */
    public void setNumObjClasses(int numObjClasses) {
        this.numObjClasses = numObjClasses;
    }

    /**
     * @return the numExtObjClasses
     */
    public int getNumExtObjClasses() {
        return numExtObjClasses;
    }

    /**
     * @param numExtObjClasses the numExtObjClasses to set
     */
    public void setNumExtObjClasses(int numExtObjClasses) {
        this.numExtObjClasses = numExtObjClasses;
    }

    /**
     * @return the maxTime
     */
    public int getMaxTime() {
        return maxTime;
    }

    /**
     * @param maxTime the maxTime to set
     */
    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }

    /**
     * @return the reportProb
     */
    public int getReportProb() {
        return reportProb;
    }

    /**
     * @param reportProb the reportProb to set
     */
    public void setReportProb(int reportProb) {
        this.reportProb = reportProb;
    }

    /**
     * @return the msd
     */
    public int getMsd() {
        return msd;
    }

    /**
     * @param msd the msd to set
     */
    public void setMsd(int msd) {
        this.msd = msd;
    }

    /**
     * @param requestId the requestId to set
     */
    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    /**
     * @return the trafficType
     */
    public TrafficType getTrafficType() {
        
        if (getNumObjClasses() == -17)
        {
            return TrafficType.Random;
        }
        
        if (getNumObjClasses() > 0) {
            return TrafficType.Brinkhoff;
        } else {
            return TrafficType.BerlinMod;
        }
    }

    /**
     * @param scaleFactor the scaleFactor to set
     */
    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    /**
     * @return the scaleFactor
     */
    public double getScaleFactor() {
        return scaleFactor;
    }

    public String getLocationName() {
        String semanticlocation = null;

        double lat = (this.getLowerlat() + this.getUpperlat()) / 2;
        double lng = (this.getLowerlong() + this.getUpperlong()) / 2;
        double n = this.getUpperlat();
        double w = this.getUpperlong();
        double s = this.getLowerlat();
        double e = this.getLowerlong();


        URL geourl;
        URLConnection geourlConn;
        DataInputStream geodis;
        //String[] ss = location.split("%20");
        //loc.split(" ");
        String googlemapapi = "http://maps.googleapis.com/maps/api/geocode/json?address=";
        googlemapapi = googlemapapi + lat + "," + lng + "&sensor=false&bounds=" + s + "," + w + "|" + n + "," + e;

        ////}
        JsonParser jp = new JsonParser();
        //googlemapapi += "&sensor=false";
        // System.out.println(googlemapapi);
        System.out.println(googlemapapi);
        try {
            geourl = new URL(googlemapapi);

            geourlConn = geourl.openConnection();
            String line;
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(geourlConn.getInputStream()));
            line = reader.readLine();

            while ((line = reader.readLine()) != null) {

                builder.append(line);
            }
            System.out.println(builder.toString());
            // System.out.println("{"+builder.toString()+"}");
            JsonElement geojson = jp.parse("{" + builder.toString() + "}");
            System.out.println(builder.toString());
            //JsonObject geoJO=geojson.getAsJsonObject();
            // JSONObject geoJO= new JSONObject(builder.toString());
            JsonObject geojo = geojson.getAsJsonObject();
            JsonElement mylocation = geojo.get("results").getAsJsonArray().get(0).getAsJsonObject().get("formatted_address");
            semanticlocation = mylocation.toString();
            System.out.println(mylocation.toString());

        } catch (Exception ex) {
            Logger.getLogger(TrafficRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return semanticlocation;
    }

    @Override
    public String toString() {

        String semanticarea = getLocationName();


        String result = null;
        String myname = this.getTrafficType().name();
        result = "Traffic Request ID " + this.getRequestId() + "\n";
        result += "User: " + this.getEmail() + "\n";
        result += "\n Traffic Model: " + myname + "\n";
        result += "Area: {(" + this.getLowerlat() + "," + this.getLowerlong() + "); (" + this.getUpperlat() + "," + this.getUpperlong() + ")}\n";
        result += "With the center location around " + semanticarea + ".\n";


        if ("BerlinMod".equals(myname)) {
            result += "Number of Vehichles: " + this.getScaleFactor() + "\n";
            /*
             * result+="Auxiliary data files can be downloaded from
             * http://siwa-umh.cs.umn.edu" + "/downloads/" + this.getRequestId()
             * + ".tar.gz. \n";
             */

        } else if ("Brinkhoff".equals(myname)) {
            result += "Starting Vehicles: " + this.getObjBegin() + "\n";
            result += "Simulating Time: " + this.getMaxTime() + "\n";
            result += "Additional Vehicles per time unit: " + this.getObjPerTime() + "\n\n";
            /*
             * result += "Edge File: http://siwa-umh.cs.umn.edu" + "/downloads/"
             * + this.getRequestId() + ".edge. \n" + "Nodes File:
             * http://siwa-umh.cs.umn.edu" + "/downloads/" + this.getRequestId()
             * + ".node. \n" + "Format Descriptions:
             * http://iapg.jade-hs.de/personen/brinkhoff/generator/FormatNetworkFiles.pdf";
             *
             */
        }


        return result;
    }

    public String toFileString() {

        String semanticarea = getLocationName();


        String result = null;
        String myname = this.getTrafficType().name();
        result = "ID\t" + this.getRequestId() + "\n";
        result += "User\t" + this.getEmail() + "\n";
        result += "Model\t" + myname + "\n";
        result += "Box\t" + this.getLowerlat() + "," + this.getLowerlong() + "," + this.getUpperlat() + "," + this.getUpperlong() + "\n";
        result += "Area\t" + semanticarea + ".\n";


        if ("BerlinMod".equals(myname)) {
            result += "Vehichles\t" + this.getScaleFactor() + "\n";
            /*
             * result+="Auxiliary data files can be downloaded from
             * http://siwa-umh.cs.umn.edu" + "/downloads/" + this.getRequestId()
             * + ".tar.gz. \n";
             */

        } else if ("Brinkhoff".equals(myname)) {
            result += "Starting\t" + this.getObjBegin() + "\n";
            result += "Time\t" + this.getMaxTime() + "\n";
            result += "Additional\t" + this.getObjPerTime() + "\n";
            /*
             * result += "Edge File: http://siwa-umh.cs.umn.edu" + "/downloads/"
             * + this.getRequestId() + ".edge. \n" + "Nodes File:
             * http://siwa-umh.cs.umn.edu" + "/downloads/" + this.getRequestId()
             * + ".node. \n" + "Format Descriptions:
             * http://iapg.jade-hs.de/personen/brinkhoff/generator/FormatNetworkFiles.pdf";
             *
             */
        }


        return result;
    }

    /**
     * Check if a given traffic result should be contained in this traffic
     * request
     *
     * @return
     */
      public String getInorderCoordinates() {
       String result =null;
       result=this.lowerlat+" "+this.upperlong+" "+this.upperlat+" "+this.lowerlong;
       return result;
               
    }
      
      
    public boolean contains(double lat, double lng) {
        return !(lat > getUpperlat() || lat < getLowerlat()
                || lng < getUpperlong() || lng > getLowerlong());
    }
}

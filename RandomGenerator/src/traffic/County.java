package traffic;

public class County {

    private int id;
    private int requestId;
    private String fileName;
    private double upperlat;
    private double upperlong;
    private double lowerlat;
    private double lowerlong;

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

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

    public County(String fileName, int id, int requestId, double upperlat,
            double upperlong, double lowerlat, double lowerlong) {
        this.fileName = fileName;
        this.id = id;
        this.requestId = requestId;
        this.upperlat = upperlat;
        this.upperlong = upperlong;
        this.lowerlat = lowerlat;
        this.lowerlong = lowerlong;
    }

    public void setRequest_id(int requestId) {
        this.requestId = requestId;
    }

    public int getRequestId() {
        return requestId;
    }
}

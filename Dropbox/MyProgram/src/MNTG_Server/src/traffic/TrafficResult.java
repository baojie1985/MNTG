package traffic;

public class TrafficResult {

    private int id;
    private String type;
    private double lat;
    private double lng;
    private int time;
    private final int RESOLUTION = 1000000;

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the lat
     */
    public double getLat() {
        return lat;
    }

    /**
     * @param lat the lat to set
     */
    public void setLat(double lat) {
        this.lat = lat;
    }

    /**
     * @return the lng
     */
    public double getLng() {
        return lng;
    }

    /**
     * @param lng the lng to set
     */
    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setLatByPoint(TrafficRequest trafficRequest, double point) {
//        double difference = trafficRequest.getUpperlat()
//                - trafficRequest.getLowerlat();
//        double proportion = point / RESOLUTION;
//        double offset = proportion * difference;
//        setLat(trafficRequest.getUpperlat() - offset);
      setLat(point / RESOLUTION);
    }

    public void setLngByPoint(TrafficRequest trafficRequest, double point) {
//        double difference = trafficRequest.getUpperlong()
//                - trafficRequest.getLowerlong();
//        double proportion = point / RESOLUTION;
//        double offset = proportion * difference;
//        setLng(trafficRequest.getUpperlong() - offset);
      setLng(point / RESOLUTION);
    }

    /**
     * @return the time
     */
    public int getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(int time) {
        this.time = time;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}

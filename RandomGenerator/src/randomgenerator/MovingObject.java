/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package randomgenerator;

/**
 *
 * @author nagell2008
 */
public class MovingObject {

    public enum ObjectType {
        newpoint, point, disappearpoint
    }
    
    private int id;
    private int timestamp;
    private double lat;
    private double lng;
    
    private ObjectType objecttype;
    
    private Node currentPos;
    private Node lastPos;
    
    public MovingObject(int _id, int _timestamp, double _lat, double _lng)
    {
        
        this.setId(_id);
        this.setTimestamp(_timestamp);
        this.setLat(_lat);
        this.setLng(_lng);
        
        this.setObjecttype(ObjectType.newpoint);
        
        this.currentPos = null;
        this.lastPos = null;
    }
    
    public void setCurrentPos(Node n)
    {
        this.currentPos = n;
    }
    
    public Node getCurrentPos()
    {
        return this.currentPos;
    }
    
    public void setLastPos(Node n)
    {
        this.lastPos = n;
    }
    
    public Node getLastPos()
    {
        return this.lastPos;
    }
    
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
     * @return the timestamp
     */
    public int getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
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

    /**
     * @return the objecttype
     */
    public ObjectType getObjecttype() {
        return objecttype;
    }

    /**
     * @param objecttype the objecttype to set
     */
    public void setObjecttype(ObjectType objecttype) {
        this.objecttype = objecttype;
    }
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package resultconvert;

/**
 *
 * @author baojie
 */
public class TrafficRequest {

    int ID;
    String User;
    String Model;
    double uplat;
    double uplng;
    double lwlat;
    double lwlng;

    public void initial(int ID, String user, String model, double uplat, double uplng, double lwlat, double lwlng) {
        this.ID = ID;
        this.User = user;
        this.Model = model;
        this.lwlat = lwlat;
        this.lwlng = lwlng;

        this.uplat = uplat;
        this.uplng = uplng;

    }

}

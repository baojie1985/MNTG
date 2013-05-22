package generator;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import main.Main;
import traffic.TrafficRequest;
import connection.ConnectionProvider;

public abstract class AbstractTrafficGenerator implements TrafficGenerator {

    int requestid;
    
    public List<File> getCounties(TrafficRequest trafficRequest) {
        List<File> counties = new ArrayList<File>();
        Connection conn = ConnectionProvider.getConnection();
        PreparedStatement pstatement = null;

        try {
            pstatement = conn.prepareStatement("select filename from counties C where C.upperlong < ? and C.lowerlong > ? and C.upperlat > ? and C.lowerlat < ?");
            pstatement.setDouble(1, trafficRequest.getLowerlong());
            pstatement.setDouble(2, trafficRequest.getUpperlong());
            pstatement.setDouble(3, trafficRequest.getLowerlat());
            pstatement.setDouble(4, trafficRequest.getUpperlat());

            ResultSet rs = pstatement.executeQuery();
            while (rs.next()) {
                counties.add(new File(Main.BASE_SHAPE_FILE_PATH, rs.getString(1) + File.separator + rs.getString(1)));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionProvider.safeClose(pstatement);
        }
        return counties;
    }
}

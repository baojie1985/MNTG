package generator;

import java.io.File;
import java.util.List;

import traffic.TrafficRequest;
import traffic.TrafficResult;

public interface TrafficGenerator {

	//public static final File GENERATED_SHAPE_FILES = new File("/home/yackel/TrafficGenerator/");

	public List<TrafficResult> getTraffic(TrafficRequest trafficRequest);

	public String getName();

	public void startTraffic(TrafficRequest trafficRequest);

	public void endTraffic();

	public boolean hasTraffic();
}

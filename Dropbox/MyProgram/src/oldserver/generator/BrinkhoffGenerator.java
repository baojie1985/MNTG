package generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import traffic.TrafficRequest;
import traffic.TrafficResult;
import util.ProcessLauncher;

public class BrinkhoffGenerator extends AbstractTrafficGenerator {

	public static final File GENERATED_FILE_NAME = new File(
			"/home/yackel/TrafficGenerator/output.txt");

	public static final File TRAFFIC_GENERATOR_JAR = new File(
			"/home/yackel/TrafficGenerator/CompleteSource21/newGenerator.jar");

	public static final String SHAPE_NETWORK_FILE_MANAGER_CLASSPATH = "/home/yackel/TrafficGenerator/ShapeNetworkFileManager/generator21.jar"
			+ File.pathSeparator
			+ "/home/yackel/TrafficGenerator/ShapeNetworkFileManager/geotool2.jar"
			+ File.pathSeparator
			+ "/home/yackel/TrafficGenerator/ShapeNetworkFileManager/JTS13.jar"
			+ File.pathSeparator
			+ "/home/yackel/TrafficGenerator/ShapeNetworkFileManager";

	public List<TrafficResult> getTraffic(TrafficRequest trafficRequest) {
		StringBuffer sb = new StringBuffer();
		sb.append(trafficRequest.getUpperlat()).append(" ")
				.append(trafficRequest.getUpperlong()).append(" ")
				.append(trafficRequest.getLowerlat()).append(" ")
				.append(trafficRequest.getLowerlong()).append(" ");
		for (File countyFile : getCounties(trafficRequest)) {
			sb.append(countyFile.getAbsolutePath()).append(" ");
		}

		ProcessLauncher
				.exec("java -classpath " + SHAPE_NETWORK_FILE_MANAGER_CLASSPATH
						+ " conversion.ShapeNetworkFileManager "
						+ sb.toString(), false);

		ArrayList<TrafficResult> trafficResults = new ArrayList<TrafficResult>();

		GENERATED_FILE_NAME.delete();

		sb = new StringBuffer();
		sb.append(new File(GENERATED_SHAPE_FILES, "output").getAbsolutePath())
				.append(" ");
		sb.append(trafficRequest.getObjBegin()).append(" ");
		sb.append(trafficRequest.getExtObjBegin()).append(" ");
		sb.append(trafficRequest.getObjPerTime()).append(" ");
		sb.append(trafficRequest.getExtObjPerTime()).append(" ");
		sb.append(trafficRequest.getNumObjClasses()).append(" ");
		sb.append(trafficRequest.getNumExtObjClasses()).append(" ");
		sb.append(trafficRequest.getMaxTime()).append(" ");
		sb.append(trafficRequest.getReportProb()).append(" ");
		sb.append(trafficRequest.getMsd());

		ProcessLauncher.exec(
				"java -classpath " + TRAFFIC_GENERATOR_JAR
						+ " generator2.DefaultDataGenerator properties.txt "
						+ sb.toString(), false);

		if (GENERATED_FILE_NAME.exists()) {
			try {
				BufferedReader bfr = new BufferedReader(new FileReader(
						GENERATED_FILE_NAME));
				String line = null;
				while ((line = bfr.readLine()) != null) {
					Scanner scanner = new Scanner(line);
					TrafficResult trafficResult = new TrafficResult();
					trafficResult.setType(scanner.next());
					trafficResult.setId(scanner.nextInt());
					scanner.nextInt(); // sequence number
					scanner.nextInt(); // object class
					trafficResult.setTime(scanner.nextInt());
					trafficResult.setLngByPoint(trafficRequest,
							scanner.nextDouble());
					trafficResult.setLatByPoint(trafficRequest,
							scanner.nextDouble());
					trafficResults.add(trafficResult);
				}
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

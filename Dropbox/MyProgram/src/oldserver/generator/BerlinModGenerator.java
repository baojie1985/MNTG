package generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import main.Controller;
import traffic.TrafficRequest;
import traffic.TrafficResult;
import util.ProcessLauncher;

public class BerlinModGenerator extends AbstractTrafficGenerator {

	public static String SECONDO_BIN_DIRECTORY = "/home/yackel/secondo/bin";

	public static String DATA_FILE_HEADER = "(OBJECT streets () (rel (tuple ((Vmax real)(geoData line))))(\n";

	public static String STREET_HEADER = "(50.0(";

	public static String STREET_FOOTER = "\n";

	public static String DATA_FILE_FOOTER = "))\n";

	private static int PAGE_SIZE = 1000;

	private Scanner trafficReader;

	private int timestamp = 0;

	private int previousTripId = 0;

	private int motorId = 1;

	public void startTraffic(TrafficRequest trafficRequest) {
		mergeParameterFile(trafficRequest);
		generateTraffic(trafficRequest);
		File trafficData = new File(SECONDO_BIN_DIRECTORY, "trips.csv");

		if (trafficRequest.getScaleFactor() < 1) {
			trafficRequest.setScaleFactor(1);
		}
		if (trafficRequest.getScaleFactor() > 2000) {
			trafficRequest.setScaleFactor(2000);
		}

		try {
			trafficReader = new Scanner(trafficData);
			trafficReader.nextLine(); // ignore the column headers
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public void endTraffic() {
		trafficReader.close();
	}

	public boolean hasTraffic() {
		return trafficReader.hasNextLine();
	}

	private void mergeParameterFile(TrafficRequest trafficRequest) {
		FileWriter output = null;
		Scanner preScanner = null;
		Scanner postScanner = null;
		try {
			output = new FileWriter(new File(SECONDO_BIN_DIRECTORY,
					"BerlinMOD_DataGenerator2.SEC"));
			preScanner = new Scanner(new File(SECONDO_BIN_DIRECTORY,
					"BerlinMOD_DataGenerator2.SEC_pre"));
			postScanner = new Scanner(new File(SECONDO_BIN_DIRECTORY,
					"BerlinMOD_DataGenerator2.SEC_post"));

			while (preScanner.hasNextLine()) {
				output.write(preScanner.nextLine()
						+ System.getProperty("line.separator"));
			}
			output.write("let SCALEFACTOR = " + (0.005)
					+ ";"
					+ System.getProperty("line.separator"));
			int num_cars = (int)trafficRequest.getScaleFactor();
			output.write("let P_NUMCARS = " +
					num_cars + ";"
					+ System.getProperty("line.separator"));

			while (postScanner.hasNextLine()) {
				output.write(postScanner.nextLine()
						+ System.getProperty("line.separator"));
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			preScanner.close();
			postScanner.close();
			try {
				output.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void generateTraffic(TrafficRequest trafficRequest) {
		List<File> counties = getCounties(trafficRequest);

		File streetsData = new File(SECONDO_BIN_DIRECTORY, "streets.data");
		File trafficData = new File(SECONDO_BIN_DIRECTORY, "trips.csv");

		streetsData.delete();
		trafficData.delete();

		FileWriter streetsDataWriter = null;
		try {
			streetsDataWriter = new FileWriter(streetsData);

			streetsDataWriter.write(DATA_FILE_HEADER);

			for (File county : counties) {
				proccessCounty(county, streetsDataWriter, trafficRequest);
			}
			streetsDataWriter.write(DATA_FILE_FOOTER);
			streetsDataWriter.close();

			ProcessLauncher.exec(new String[] {
					Controller.BASE_PATH + File.separator + "secondo"
							+ File.separator + "bin" + File.separator
							+ "SecondoTTYNT", "-i",
					"BerlinMOD_DataGenerator2.SEC" }, new File(
					SECONDO_BIN_DIRECTORY), true);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public List<TrafficResult> getTraffic(TrafficRequest trafficRequest) {
		List<TrafficResult> trafficResults = new ArrayList<TrafficResult>();
		TrafficResult trafficResult = null;

		String line = null;

		// data format of each line is
		// Moid,Tripid,Tstart,Tend,Xstart,Ystart,Xend,Yend
		while (trafficReader.hasNextLine()) {
			line = trafficReader.nextLine();
			String[] splitResults = line.split(",");

			int tripId = Integer.parseInt(splitResults[1]);
			trafficResult = new TrafficResult();

			boolean allowBreak = false;

			if (previousTripId != tripId) {
				if (timestamp == 1) { // found a trip of size 1. delete the
										// previous result to ignore it
					trafficResults.remove(trafficResults.size() - 1);
				}
				timestamp = 0;
				motorId++;
				trafficResult.setLat(Double.parseDouble(splitResults[5]));
				trafficResult.setLng(Double.parseDouble(splitResults[4]));
				allowBreak = false;
			} else {
				trafficResult.setLat(Double.parseDouble(splitResults[7]));
				trafficResult.setLng(Double.parseDouble(splitResults[6]));
				allowBreak = true;
			}
			trafficResult.setType("");
			trafficResult.setId(motorId);
			trafficResult.setTime(timestamp++);
			trafficResults.add(trafficResult);
			previousTripId = tripId;

			if (allowBreak && trafficResults.size() >= PAGE_SIZE) {
				break;
			}
		}

		return trafficResults;
	}

	Pattern decimalPattern = Pattern.compile("-?[0-9]+(\\.[0-9]+)?");

	private void proccessCounty(File county, FileWriter writer,
			TrafficRequest trafficRequest) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(county.getAbsolutePath()
					+ ".data"));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		try {
			List<String> streets = new ArrayList<String>();
			String line = null;
			boolean validStreet = false;

			while ((line = reader.readLine()) != null) {
				if (line.contains("\"")) { // reset the current street data
					if (!streets.isEmpty()) {
						if (validStreet) {
							writeStreets(streets, writer);
						}
						streets.clear();
						reader.readLine(); // eat the extra paren
						validStreet = false;
					}
				} else if (line.contains("-")) { // contains a negative number
													// (i.e. we are in the U.S.
													// latitude
					validStreet |= isValidStreetLine(line, trafficRequest);
					streets.add(line);
				}
			}

			fixEndOfFileClosingParens(streets); // the final line of
												// each file
			// will have extra parens
			if (!streets.isEmpty()) { // write the final street
				writeStreets(streets, writer);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void writeStreets(List<String> streets, FileWriter writer) {
		try {
			writer.write(STREET_HEADER);
			for (String street : streets) {
				writer.write(street + "\n");
			}
			writer.write(STREET_FOOTER);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void fixEndOfFileClosingParens(List<String> streets) {
		for (int i = 0; i < streets.size(); i++) {
			streets.set(i, streets.get(i).replace(")))))", ")))"));
		}
	}

	private boolean isValidStreetLine(String streetLine,
			TrafficRequest trafficRequest) {
		Scanner lineScanner = new Scanner(streetLine);
		double lng = Double.parseDouble(lineScanner.findInLine(decimalPattern));
		double lat = Double.parseDouble(lineScanner.findInLine(decimalPattern));
		if (trafficRequest.contains(lat, lng)) {
			return true;
		}

		lng = Double.parseDouble(lineScanner.findInLine(decimalPattern));
		lat = Double.parseDouble(lineScanner.findInLine(decimalPattern));
		if (trafficRequest.contains(lat, lng)) {
			return true;
		}
		return false;
	}

	@Override
	public String getName() {
		return "BerlinMOD";
	}
}

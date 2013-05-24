package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;

import queue.QueueReader;

public class Controller {

	public static final String BASE_PATH = "/home/yackel";

	public static final String BASE_SHAPE_FILE_PATH = BASE_PATH
			+ "/TrafficGenerator/shapefiles";

	public static final File ERROR_PATH = new File(
			"/home/yackel/TrafficGenerator/error.txt");

	public static PrintStream ERROR_WRITER = null;

	public static void main(String[] args) throws IOException {
		
		BufferedWriter br = new BufferedWriter(new FileWriter(new File("my_mntg_log.txt")));
		br.write("I started logging here\n");
		br.flush();
		br.close();

		safeSleep(10); // give time for the mysql service to start running

		ERROR_PATH.delete();
		ERROR_WRITER = new PrintStream(ERROR_PATH);
		QueueReader queue = new QueueReader();

		try {
			while (true) {
				try {
					if (queue.isRequestPending()) {
						queue.proccessQueue();
					}
				} catch (Throwable t) {
					t.printStackTrace(ERROR_WRITER);
					ERROR_WRITER.flush();
				}
				safeSleep(5);

			}
		} finally {
			ERROR_WRITER.close();
		}
	}

	private static void safeSleep(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			// safe to ignore
		}
	}
}

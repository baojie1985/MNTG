package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import main.Main;

public class ProcessLauncher {

	private static boolean showOutput = false;

	public static void exec(String[] process, File directory, boolean showOutput) {
		if (showOutput) {
			System.out.print("Launching Process: ");
			for (String processPart : process) {
				System.out.print(processPart);
			}
			System.out.println();
		}
		ProcessLauncher.showOutput = showOutput;

		ProcessBuilder processBuilder = new ProcessBuilder(process);
		if (directory != null) {
			processBuilder.directory(directory);
		}
		Process proc = null;

		try {
			proc = processBuilder.start();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		StreamReader errorGobbler = new StreamReader(proc.getErrorStream());

		StreamReader outputGobbler = new StreamReader(proc.getInputStream());

		// kick them off
		errorGobbler.start();
		outputGobbler.start();
		try {
			proc.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void exec(String process, boolean showOutput) {
		exec(process.split(" "), null, showOutput);
	}

	private static class StreamReader extends Thread {
		InputStream is;

		StreamReader(InputStream is) {
			this.is = is;
		}

		public void run() {
			try {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);

				String line = null;
				while ((line = br.readLine()) != null) {
					if (showOutput) {
						System.out.println(line);
						Main.ERROR_WRITER.append(line + "\n");
						Main.ERROR_WRITER.flush();
					}
				}

			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
}

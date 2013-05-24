package queue;

import traffic.TrafficRequest;

public class TrafficRequestThread extends Thread {
	public TrafficRequest trafficRequest = null;
	
	public TrafficRequestThread(TrafficRequest tr)
	{
		trafficRequest = tr;
	}

	public void run() {
        QueueReader.proccessTrafficRequest(trafficRequest);
        QueueReader.runningThreadsCount.decrement();
    }
}

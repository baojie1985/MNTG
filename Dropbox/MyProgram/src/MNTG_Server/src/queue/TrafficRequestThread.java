package queue;

import traffic.TrafficRequest;

public class TrafficRequestThread extends Thread {
    public boolean flag;    // if it is ocuppied
    public int requestid;

    public TrafficRequest trafficRequest = null;

    public TrafficRequestThread(TrafficRequest trt) {
        flag=true;
        trafficRequest = trt;
        requestid=trt.getRequestId();
    }

    public void run() {
        this.flag=false;
        
        //this.requestid=trafficRequest.getRequestId();
        
        QueueReader.proccessTrafficRequest(trafficRequest);
        
        this.flag=true;
        //QueueReader.runningThreadsCount.decrement();
    }
    
    
}

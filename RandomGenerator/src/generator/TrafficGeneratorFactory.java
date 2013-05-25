package generator;

import traffic.TrafficRequest;

public class TrafficGeneratorFactory {

    public static TrafficGenerator getGenerator(TrafficRequest trafficRequest) {
        switch (trafficRequest.getTrafficType()) {
            case Random:
                //return new BrinkhoffGenerator(trafficRequest);
                return null;
            default:
                throw new IllegalArgumentException("Not a recognized generator.");
        }
    }
}

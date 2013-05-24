package generator;

import traffic.TrafficRequest;

public class TrafficGeneratorFactory {

	public static TrafficGenerator getGenerator(TrafficRequest trafficRequest) {
		switch (trafficRequest.getTrafficType()) {
		case Brinkhoff:
			return new BrinkhoffGenerator();
		case BerlinMod:
			return new BerlinModGenerator();
		default:
			throw new IllegalArgumentException("Not a recognized generator.");
		}
	}
}

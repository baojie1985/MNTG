package queue;

import generator.TrafficGenerator;
import generator.TrafficGeneratorFactory;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.mail.MessagingException;

import traffic.TrafficRequest;
import traffic.TrafficResult;
import util.Emailer;
import connection.ConnectionProvider;

public class QueueReader {

	private List<TrafficRequest> trafficRequestQueueCache = new ArrayList<TrafficRequest>();

	public boolean isRequestPending() {
		Connection conn = ConnectionProvider.getConnection();

		Statement statement = null;
		try {
			statement = conn.createStatement();

			ResultSet rs = statement
					.executeQuery("select count(*) from traffic_requests where ready = 1");
			if (rs.next()) {
				return rs.getInt(1) > 0;
			} else {
				throw new IllegalStateException(
						"Could not get the row result from the count query.");
			}

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			ConnectionProvider.safeClose(statement);
		}
	}

	private void fillQueueCache() {
		Connection conn = ConnectionProvider.getConnection();
		Statement statement = null;

		try {
			statement = conn.createStatement();

			ResultSet rs = statement
					.executeQuery("select id, name, email, upperlat, upperlong, lowerlat, lowerlong, objBegin, extObjBegin, objPerTime, extObjPerTime, numObjClasses, numExtObjClasses, maxTime, reportProb, msd, scaleFactor from traffic_requests TR where TR.ready = 1 and TR.finished = 0 order by TR.created");
			while (rs.next()) {
				trafficRequestQueueCache.add(new TrafficRequest(rs.getInt(1),
						rs.getString(2), rs.getString(3), rs.getDouble(4), rs
								.getDouble(5), rs.getDouble(6),
						rs.getDouble(7), rs.getInt(8), rs.getInt(9), rs
								.getInt(10), rs.getInt(11), rs.getInt(12), rs
								.getInt(13), rs.getInt(14), rs.getInt(15), rs
								.getInt(16), rs.getDouble(17)));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			ConnectionProvider.safeClose(statement);
		}
	}

	public void proccessQueue_sequential() {
		fillQueueCache();

		Connection conn = ConnectionProvider.getConnection();
		PreparedStatement pStatement = null;
		try {
			pStatement = conn
					.prepareStatement("insert into traffic_results (traffic_request_id, type, object_id, timestamp, lat, lng) values (?,?,?,?,?,?)");

			int result = 1;
			Iterator<TrafficRequest> trafficRequestIterator = trafficRequestQueueCache
					.iterator();
			while (trafficRequestIterator.hasNext()) {
				TrafficRequest trafficRequest = null;
				try {
					trafficRequest = trafficRequestIterator.next();
					TrafficGenerator generator = TrafficGeneratorFactory
							.getGenerator(trafficRequest);
					generator.startTraffic(trafficRequest);
					while (generator.hasTraffic()) {
						List<TrafficResult> trafficResults = generator
								.getTraffic(trafficRequest);
						for (TrafficResult trafficResult : trafficResults) {
							pStatement.setInt(1, trafficRequest.getRequestId());
							pStatement.setString(2, trafficResult.getType());
							pStatement.setInt(3, trafficResult.getId());
							pStatement.setInt(4, trafficResult.getTime());
							pStatement.setDouble(5, trafficResult.getLat());
							pStatement.setDouble(6, trafficResult.getLng());
							pStatement.addBatch();
						}
						pStatement.executeBatch();
						pStatement.clearBatch();
					}
					generator.endTraffic();

				} catch (Throwable t) {
					result = -1;
					throw new RuntimeException(t);
				} finally {
					trafficRequestIterator.remove();
					if (trafficRequest != null) {
						markTrafficRequestCompleted(trafficRequest, result);
						try {
							Emailer.sendEmail(trafficRequest, result);
						} catch (MessagingException e) {
							throw new RuntimeException(e);
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							throw new RuntimeException(e);
						}
					}
				}
			}
		} catch (SQLException e) {
			throw new IllegalStateException("Failed to insert traffic results.");
		} finally {
			ConnectionProvider.safeClose(pStatement);
		}
		trafficRequestQueueCache.clear();
	}

	public static void proccessTrafficRequest(TrafficRequest trafficRequest) {
		Connection conn = ConnectionProvider.getConnection();
		PreparedStatement pStatement = null;
		try {
			pStatement = conn
					.prepareStatement("insert into traffic_results (traffic_request_id, type, object_id, timestamp, lat, lng) values (?,?,?,?,?,?)");

			int result = 1;
			
				try {
					TrafficGenerator generator = TrafficGeneratorFactory
							.getGenerator(trafficRequest);
					generator.startTraffic(trafficRequest);
					while (generator.hasTraffic()) {
						List<TrafficResult> trafficResults = generator
								.getTraffic(trafficRequest);
						for (TrafficResult trafficResult : trafficResults) {
							pStatement.setInt(1, trafficRequest.getRequestId());
							pStatement.setString(2, trafficResult.getType());
							pStatement.setInt(3, trafficResult.getId());
							pStatement.setInt(4, trafficResult.getTime());
							pStatement.setDouble(5, trafficResult.getLat());
							pStatement.setDouble(6, trafficResult.getLng());
							pStatement.addBatch();
						}
						pStatement.executeBatch();
						pStatement.clearBatch();
					}
					generator.endTraffic();

				} catch (Throwable t) {
					result = -1;
					throw new RuntimeException(t);
				} finally {
					if (trafficRequest != null) {
						markTrafficRequestCompleted(trafficRequest, result);
						try {
							Emailer.sendEmail(trafficRequest, result);
						} catch (MessagingException e) {
							throw new RuntimeException(e);
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							throw new RuntimeException(e);
						}
					}
				}
		} catch (SQLException e) {
			throw new IllegalStateException("Failed to insert traffic results.");
		} finally {
			ConnectionProvider.safeClose(pStatement);
		}
	}
	public static SharedInteger runningThreadsCount;
	public void proccessQueue() {
		fillQueueCache();

		boolean waitedLongTime = false;
		
		int maxThreadsRunning = 25;
		runningThreadsCount = new SharedInteger();
		runningThreadsCount.set(0);
		
		Iterator<TrafficRequest> trafficRequestIterator = trafficRequestQueueCache.iterator();

		ArrayList<TrafficRequestThread> threads = new ArrayList<TrafficRequestThread>();
		while (trafficRequestIterator.hasNext()) {
			TrafficRequest trafficRequest = null;
			try {
				trafficRequest = trafficRequestIterator.next();
				proccessTrafficRequest(trafficRequest);
				
				/*
				long start = System.currentTimeMillis();
				while(true)
				{
					if(runningThreadsCount.get() < maxThreadsRunning || waitedLongTime)
					{
						TrafficRequestThread trt = new TrafficRequestThread(trafficRequest);
						trt.start();
						threads.add(trt);
						runningThreadsCount.increment();
						break;
					}
					long end = System.currentTimeMillis();
					if((end-start) > 12*60*60*1000)//12 hours allowance
					{
						waitedLongTime = true;
						break;
					}
				}*/
			} catch (Throwable t) {
				throw new RuntimeException(t);
			} finally {
				trafficRequestIterator.remove();
			}
		}

		/*
		Iterator<TrafficRequestThread> trafficRequestThreadIterator = threads.iterator();
		while (trafficRequestThreadIterator.hasNext()) {
			TrafficRequestThread thread = null;
			try{
				thread = trafficRequestThreadIterator.next();
				thread.join(3*60*60*1000);//3 hours allowance
			} catch (Throwable t) {
				throw new RuntimeException(t);
			}
			
		}*/
		
		trafficRequestQueueCache.clear();
	}
	
	private static void markTrafficRequestCompleted(TrafficRequest trafficRequest,
			int result) {
		Connection conn = ConnectionProvider.getConnection();
		Statement statement = null;
		try {
			statement = conn.createStatement();
			statement.execute("update traffic_requests TR set TR.finished = "
					+ result + " where TR.id = "
					+ trafficRequest.getRequestId());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			ConnectionProvider.safeClose(statement);
		}
	}
}

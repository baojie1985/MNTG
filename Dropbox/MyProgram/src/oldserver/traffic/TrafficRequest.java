package traffic;

public class TrafficRequest {

	public enum TrafficType {
		Brinkhoff, BerlinMod
	}

	private int requestId;

	private String name;

	private double upperlat;

	private double upperlong;

	private double lowerlat;

	private double lowerlong;

	private int objBegin;

	private int extObjBegin;

	private int objPerTime;

	private int extObjPerTime;

	private int numObjClasses;

	private int numExtObjClasses;

	private int maxTime;

	private int reportProb;

	private int msd;

	private String email;

	private double scaleFactor;

	/**
	 * @return the upperlat
	 */
	public double getUpperlat() {
		return upperlat;
	}

	/**
	 * @param upperlat
	 *            the upperlat to set
	 */
	public void setUpperlat(double upperlat) {
		this.upperlat = upperlat;
	}

	/**
	 * @return the upperlong
	 */
	public double getUpperlong() {
		return upperlong;
	}

	/**
	 * @param upperlong
	 *            the upperlong to set
	 */
	public void setUpperlong(double upperlong) {
		this.upperlong = upperlong;
	}

	/**
	 * @return the lowerlat
	 */
	public double getLowerlat() {
		return lowerlat;
	}

	/**
	 * @param lowerlat
	 *            the lowerlat to set
	 */
	public void setLowerlat(double lowerlat) {
		this.lowerlat = lowerlat;
	}

	/**
	 * @return the lowerlong
	 */
	public double getLowerlong() {
		return lowerlong;
	}

	/**
	 * @param lowerlong
	 *            the lowerlong to set
	 */
	public void setLowerlong(double lowerlong) {
		this.lowerlong = lowerlong;
	}

	public TrafficRequest(int requestId, String name, String email,
			double upperlat, double upperlong, double lowerlat,
			double lowerlong, int objBegin, int extObjBegin, int objPerTime,
			int extObjPerTime, int numObjClasses, int numExtObjClasses,
			int maxTime, int reportProb, int msd, double scaleFactor) {
		this.name = name;
		this.email = email;
		this.requestId = requestId;
		this.upperlat = upperlat;
		this.upperlong = upperlong;
		this.lowerlat = lowerlat;
		this.lowerlong = lowerlong;
		this.objBegin = objBegin;
		this.extObjBegin = extObjBegin;
		this.objPerTime = objPerTime;
		this.extObjPerTime = extObjPerTime;
		this.numObjClasses = numObjClasses;
		this.numExtObjClasses = numExtObjClasses;
		this.maxTime = maxTime;
		this.reportProb = reportProb;
		this.msd = msd;
		this.scaleFactor = scaleFactor;
	}

	public void setRequest_id(int requestId) {
		this.requestId = requestId;
	}

	public int getRequestId() {
		return requestId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	/**
	 * @return the objBegin
	 */
	public int getObjBegin() {
		return objBegin;
	}

	/**
	 * @param objBegin
	 *            the objBegin to set
	 */
	public void setObjBegin(int objBegin) {
		this.objBegin = objBegin;
	}

	/**
	 * @return the extObjBegin
	 */
	public int getExtObjBegin() {
		return extObjBegin;
	}

	/**
	 * @param extObjBegin
	 *            the extObjBegin to set
	 */
	public void setExtObjBegin(int extObjBegin) {
		this.extObjBegin = extObjBegin;
	}

	/**
	 * @return the objPerTime
	 */
	public int getObjPerTime() {
		return objPerTime;
	}

	/**
	 * @param objPerTime
	 *            the objPerTime to set
	 */
	public void setObjPerTime(int objPerTime) {
		this.objPerTime = objPerTime;
	}

	/**
	 * @return the extObjPerTime
	 */
	public int getExtObjPerTime() {
		return extObjPerTime;
	}

	/**
	 * @param extObjPerTime
	 *            the extObjPerTime to set
	 */
	public void setExtObjPerTime(int extObjPerTime) {
		this.extObjPerTime = extObjPerTime;
	}

	/**
	 * @return the numObjClasses
	 */
	public int getNumObjClasses() {
		return numObjClasses;
	}

	/**
	 * @param numObjClasses
	 *            the numObjClasses to set
	 */
	public void setNumObjClasses(int numObjClasses) {
		this.numObjClasses = numObjClasses;
	}

	/**
	 * @return the numExtObjClasses
	 */
	public int getNumExtObjClasses() {
		return numExtObjClasses;
	}

	/**
	 * @param numExtObjClasses
	 *            the numExtObjClasses to set
	 */
	public void setNumExtObjClasses(int numExtObjClasses) {
		this.numExtObjClasses = numExtObjClasses;
	}

	/**
	 * @return the maxTime
	 */
	public int getMaxTime() {
		return maxTime;
	}

	/**
	 * @param maxTime
	 *            the maxTime to set
	 */
	public void setMaxTime(int maxTime) {
		this.maxTime = maxTime;
	}

	/**
	 * @return the reportProb
	 */
	public int getReportProb() {
		return reportProb;
	}

	/**
	 * @param reportProb
	 *            the reportProb to set
	 */
	public void setReportProb(int reportProb) {
		this.reportProb = reportProb;
	}

	/**
	 * @return the msd
	 */
	public int getMsd() {
		return msd;
	}

	/**
	 * @param msd
	 *            the msd to set
	 */
	public void setMsd(int msd) {
		this.msd = msd;
	}

	/**
	 * @param requestId
	 *            the requestId to set
	 */
	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	/**
	 * @return the trafficType
	 */
	public TrafficType getTrafficType() {
		if (getNumObjClasses() > 0) {
			return TrafficType.Brinkhoff;
		} else {
			return TrafficType.BerlinMod;
		}
	}

	/**
	 * @param scaleFactor
	 *            the scaleFactor to set
	 */
	public void setScaleFactor(double scaleFactor) {
		this.scaleFactor = scaleFactor;
	}

	/**
	 * @return the scaleFactor
	 */
	public double getScaleFactor() {
		return scaleFactor;
	}

	/**
	 * Check if a given traffic result should be contained in this traffic
	 * request
	 * 
	 * @return
	 */
	public boolean contains(double lat, double lng) {
		return !(lat > getUpperlat() || lat < getLowerlat()
				|| lng < getUpperlong() || lng > getLowerlong());
	}
}

package showmap;

import java.net.URL;

/**
 * Class for reading drawable objects from a file in a separate thread.
 * 
 * @version 2.30 15.10.00 constructors public
 * @version 2.20 30.05.00 separation of databas class, support of D021 files
 * @version 2.10 07.02.00 constructor with URL[]
 * @version 2.00 19.08.99 support of databases
 * @version 1.00 21.02.99 first version
 * @author Thomas Brinkhoff
 */

public class LoadDrawables extends Thread {

	/**
	 * ShowMap-Applet, von dem das Objekt angelegt wurde.
	 */
	private ShowMap applet;
	/**
	 * Anzahl der URLs, von denen geladen werden soll.
	 */
	private int urlNum = 0;
	/**
	 * die URLs, von denen geladen werden soll.
	 */
	private URL url[] = null;
	/**
	 * Anzahl der geladenen Objekte.
	 */
	private int objNum = 0;

	/**
	 * Constructor.
	 * 
	 * @param applet
	 *            the ShowMap applet
	 * @param url
	 *            one or more URLs to read
	 * @param objNum
	 *            number of objects read before
	 */
	public LoadDrawables(ShowMap applet, URL[] url, int objNum) {
		this.applet = applet;
		urlNum = url.length;
		this.url = url;
		this.objNum = objNum;
	}

	/**
	 * Constructor.
	 * 
	 * @param applet
	 *            the ShowMap applet
	 * @param url
	 *            the URL to read
	 * @param objNum
	 *            number of objects read before
	 */
	public LoadDrawables(ShowMap applet, URL url, int objNum) {
		this.applet = applet;
		urlNum = 1;
		this.url = new URL[urlNum];
		this.url[0] = url;
		this.objNum = objNum;
	}

	/**
	 * Reads the drawable objects using ShowMap.readDrawables. After the
	 * reading, the state of the applet is set to ShowMap.COMPLETE and the map
	 * is drawn.
	 */
	public void run() {
		for (int i = 0; i < urlNum; i++)
			if (url[i] != null)
				if (applet != null)
					objNum = applet.readDrawables(objNum, url[i], i);
	}
}

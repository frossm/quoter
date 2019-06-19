package org.fross.quote;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class URLOps {

	/**
	 * ReadURL: Retrieve data from a website
	 * 
	 * @param urlString
	 * @return
	 * @throws Exception
	 */
	public static String ReadURL(String urlString) throws Exception {
		BufferedReader Reader = null;

		try {
			URL url = new URL(urlString);
			Reader = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuilder buffer = new StringBuilder();
			int read;
			char[] chars = new char[1024];
			while ((read = Reader.read(chars)) != -1) {
				buffer.append(chars, 0, read);
			}

			return buffer.toString();

		} finally {
			if (Reader != null) {
				Reader.close();
			}
		}
	}
}

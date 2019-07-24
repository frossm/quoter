/**************************************************************************************************************
 * Quote.jar
 * Copyright 2019 Michael Fross, all rights reserved
 * 
 * Quote is a command line program that display stock quotes and index data.
 * 
 * License:  
 *   GNU General Public License v3
 *   http://www.gnu.org/licenses/gpl-3.0.html
 *   
 ***************************************************************************************************************/

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

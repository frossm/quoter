/**************************************************************************************************************
 * Quoter.jar
 * 
 * Quoter is a command line program that display stock quotes and index data.
 * 
 *  Copyright (c) 2019 Michael Fross
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *   
 ***************************************************************************************************************/
package org.fross.quoter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	/**
	 * updateCheck(): Query GitHub's tag API and determine the latest version of the application
	 * 
	 * @param app
	 * @return
	 */
	public static String updateCheck(String app) {
		String GITHUB_URL = "https://api.github.com/repos/frossm/" + app + "/tags";
		String returnString = null;

		try {
			// Read the tags from the GitHub Tags API
			String githubPage = URLOps.ReadURL(GITHUB_URL);

			// Pull out the latest version
			Pattern pattern = Pattern.compile("name.: *\"(.*?)\"", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(githubPage);

			if (matcher.find()) {
				returnString = matcher.group(1);
			} else {
				throw new Exception();
			}
		} catch (Exception ex) {
			returnString = "Unable to determine latest release";
		}

		return returnString;
	}
}

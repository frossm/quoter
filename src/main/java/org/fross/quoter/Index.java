/**************************************************************************************************************
 * Quoter.jar
 * 
 * Quoter is a command line program that display stock quotes and index data.
 * 
 * *  Copyright (c) 2019-2022 Michael Fross
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

import java.util.List;

import org.fross.library.Debug;
import org.fross.library.Output;
import org.fusesource.jansi.Ansi;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import us.codecraft.xsoup.Xsoup;

public class Index {
	/**
	 * GetIndex: Returns an array of Strings that contains the Dow, Nasdaq, and S&P data. Unfortunately I have to scrape a
	 * web page for this information as IEX Cloud does not contain index data.
	 * 
	 * The XPaths were determined by using the dev tools in Chrome, selecting the element, and using copy XPath
	 * 
	 * @param idx
	 * @return
	 */
	protected static String[] getIndex(String idx) {
		String[] retArray = new String[7];
		String URLTEMPLATE = "https://www.marketwatch.com/investing/index/SYMBOLHERE";
		String URL = "ERROR";
		Document htmlPage = null;

		// Ensure a valid value was passed
		switch (idx.toUpperCase()) {
		case "DOW":
			URL = URLTEMPLATE.replaceAll("SYMBOLHERE", "djia");
			break;
		case "NASDAQ":
			URL = URLTEMPLATE.replaceAll("SYMBOLHERE", "comp");
			break;
		case "S&P":
			URL = URLTEMPLATE.replaceAll("SYMBOLHERE", "spx");
			break;
		default:
			Output.fatalError("Call to getIndex() must be 'DOW', 'NASDAQ', or 'S&P'", 4);
			break;
		}
		
		Output.debugPrint("Index URL rewritten to: " + URL);

		try {
			// Download and parse the the webpage with xsoup
			try {
				htmlPage = Jsoup.connect(URL).userAgent("Mozilla").get();
			} catch (HttpStatusException ex) {
				Output.fatalError("Unable to connect to: " + URL + "\n" + ex.getMessage(), 4);
			}

			// Set the first element of the return array to the index name
			retArray[0] = idx;

			// Current Price
			String xPath = "//*[@id=\"maincontent\"]/div[2]/div[3]/div/div[2]/h2/span";
			List<Element> elements = Xsoup.compile(xPath).evaluate(htmlPage).getElements();
			retArray[1] = elements.get(0).text();

			// Change
			xPath = "//*[@id=\"maincontent\"]/div[2]/div[3]/div/div[2]/bg-quote/span[1]";
			elements = Xsoup.compile(xPath).evaluate(htmlPage).getElements();
			retArray[2] = elements.get(0).text();

			// Change Percent
			xPath = "//*[@id=\"maincontent\"]/div[2]/div[3]/div/div[2]/bg-quote/span[2]";
			elements = Xsoup.compile(xPath).evaluate(htmlPage).getElements();
			retArray[3] = elements.get(0).text();

			// 52 Week High & Low
			xPath = "//*[@id=\"maincontent\"]/div[7]/div[1]/div[1]/div/ul/li[3]/span[1]";
			elements = Xsoup.compile(xPath).evaluate(htmlPage).getElements();
			retArray[4] = elements.get(0).text().split(" - ")[0];
			retArray[5] = elements.get(0).text().split(" - ")[1];

			// Year to Date
			xPath = "//*[@id=\"maincontent\"]/div[7]/div[1]/div[2]/div/table/tbody/tr[3]/td[2]/ul/li[1]";
			elements = Xsoup.compile(xPath).evaluate(htmlPage).getElements();
			retArray[6] = elements.get(0).text();

			// Remove any commas & percent signs in the data and trim any whitespace
			for (int i = 0; i < retArray.length; i++) {
				retArray[i] = retArray[i].replaceAll("[,%]", "").trim();
			}

			// If we are in debug mode, display the values we are returning
			if (Debug.query() == true) {
				Output.debugPrint("Index Data Results:");
				for (int i = 0; i < retArray.length; i++) {
					Output.debugPrint("   " + i + ": " + retArray[i]);
				}
			}

		} catch (Exception ex) {
			Output.printColorln(Ansi.Color.RED, "Unable to get Index data for " + idx + "\n" + ex.getMessage());
		}

		return retArray;
	}

}
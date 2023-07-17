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

import java.util.ArrayList;
import java.util.HashMap;
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
	HashMap<String, String> indexData = new HashMap<>();
	static boolean marketOpen;
	XPathLookup xPathLookup = new XPathLookup();

	/**
	 * Symbol Constructor(): Initialize class with a symbol to process
	 * 
	 * @param symb
	 */
	public Index(String idx) {
		getIndex(idx);
	}

	/**
	 * queryPageItem():Find the specific value in the provided doc with the xPath given
	 * 
	 * @param doc
	 * @param xPath
	 * @return
	 */
	protected static String queryPageItem(Document doc, String xPath) {
		List<Element> elements = Xsoup.compile(xPath).evaluate(doc).getElements();
		return elements.get(0).text();
	}

	/**
	 * get(): Returns security detail based on passed field
	 * 
	 * @param field
	 * @return
	 */
	protected String get(String field) {
		try {
			return this.indexData.get(field);
		} catch (Exception ex) {
			Output.printColorln(Ansi.Color.RED, "Could not query '" + field + "' field in security data");
			throw new IllegalArgumentException();
		}
	}

	/**
	 * put(): Update a value in the objects symbolData HashMap
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	protected boolean put(String field, String value) {
		try {
			indexData.put(field, value);
		} catch (Exception ex) {
			return false;
		}
		return true;
	}

	/**
	 * getAllFieldNames(): Return an array of all of the keys in the HashMap
	 * 
	 * @return
	 */
	protected List<String> getAllFieldNames() {
		List<String> returnList = new ArrayList<String>();

		for (String i : this.indexData.keySet()) {
			returnList.add(i);
		}

		return returnList;
	}

	/**
	 * GetIndex: Returns an array of Strings that contains the Dow, Nasdaq, and S&P data. Unfortunately I have to scrape a web
	 * page for this information as IEX Cloud does not contain index data.
	 * 
	 * The XPaths were determined by using the dev tools in Chrome, selecting the element, and using copy XPath
	 * 
	 * @param idx
	 * @return
	 */
	private void getIndex(String idx) {
		String URL = "https://www.marketwatch.com/investing/index/SYMBOLHERE";
		Document htmlPage = null;

		// Ensure a valid value was passed
		switch (idx.toUpperCase()) {
		case "DOW":
			URL = URL.replaceAll("SYMBOLHERE", "djia");
			break;
		case "NASDAQ":
			URL = URL.replaceAll("SYMBOLHERE", "comp");
			break;
		case "S&P":
			URL = URL.replaceAll("SYMBOLHERE", "spx");
			break;
		default:
			Output.fatalError("Call to getIndex() must be 'DOW', 'NASDAQ', or 'S&P'", 4);
			break;
		}

		Output.debugPrintln("Index URL rewritten to: " + URL);
		// Add index name to hash
		indexData.put("index", idx);

		try {
			// Download and parse the the webpage with xsoup
			try {
				htmlPage = Jsoup.connect(URL).userAgent("Mozilla").get();
			} catch (HttpStatusException ex) {
				this.indexData.put("status", "error");
				return;
			}

			// Add the name to the hash
			this.indexData.put("index", idx);
			this.indexData.put("status", "ok");

			// Determine if the market is open or closed
			if (Symbol.queryPageItem(htmlPage, xPathLookup.lookupIndexOpen("marketStatus")).toLowerCase().contains("closed") == true) {
				marketOpen = false;
			} else {
				marketOpen = true;
			}

			// MarketWatch has different XPaths depending if the market is open or closed
			if (marketOpen == false) {
				// Market is CLOSED
				Output.debugPrintln("Market is currently CLOSED");

				// Current Price
				String key = "latestPrice";
				String result = queryPageItem(htmlPage, xPathLookup.lookupIndexClosed(key));
				indexData.put(key, result.replaceAll("[,%]", "").trim());

				// Change
				key = "change";
				result = queryPageItem(htmlPage, xPathLookup.lookupIndexClosed(key));
				indexData.put(key, result.replaceAll("[,%]", "").trim());

				// Change Percent
				key = "changePercent";
				result = queryPageItem(htmlPage, xPathLookup.lookupIndexClosed(key));
				indexData.put(key, result.replaceAll("[,%]", "").trim());

				// 52 Week Range
				key = "52weekRange";
				result = queryPageItem(htmlPage, xPathLookup.lookupIndexClosed(key));

				String w52Low = result.split(" - ")[0];
				String w52High = result.split(" - ")[1];

				indexData.put("week52Low", w52Low.replaceAll("[,%]", "").trim());
				indexData.put("week52High", w52High.replaceAll("[,%]", "").trim());

				// Year to Date Change Percent
				key = "ytdChangePercent";
				result = queryPageItem(htmlPage, xPathLookup.lookupIndexClosed(key));
				indexData.put(key, result.replaceAll("[,%]", "").trim());

				// One Year Change Percent
				key = "oneYearChangePercent";
				result = queryPageItem(htmlPage, xPathLookup.lookupIndexClosed(key));
				indexData.put(key, result.replaceAll("[,%]", "").trim());

				// TimeStamp
				key = "timeStamp";
				result = queryPageItem(htmlPage, xPathLookup.lookupIndexClosed(key));
				indexData.put(key, result.replaceAll("[,%]", "").trim());

			} else {
				// Market is OPEN
				Output.debugPrintln("Market is currently OPEN");

				// Current Price
				String key = "latestPrice";
				String result = queryPageItem(htmlPage, xPathLookup.lookupIndexOpen(key));
				indexData.put(key, result.replaceAll("[,%]", "").trim());

				// Change
				key = "change";
				result = queryPageItem(htmlPage, xPathLookup.lookupIndexOpen(key));
				indexData.put("change", result.replaceAll("[,%]", "").trim());

				// Change Percent
				key = "changePercent";
				result = queryPageItem(htmlPage, xPathLookup.lookupIndexOpen(key));
				indexData.put(key, result.replaceAll("[,%]", "").trim());

				// 52 Week Range
				key = "52weekRange";
				result = queryPageItem(htmlPage, xPathLookup.lookupIndexOpen(key));

				String w52Low = result.split(" - ")[0];
				String w52High = result.split(" - ")[1];

				indexData.put("week52Low", w52Low.replaceAll("[,%]", "").trim());
				indexData.put("week52High", w52High.replaceAll("[,%]", "").trim());

				// Year to Date
				key = "ytdChangePercent";
				result = queryPageItem(htmlPage, xPathLookup.lookupIndexOpen(key));
				indexData.put(key, result.replaceAll("[,%]", "").trim());

				// One Year Change Percent
				key = "oneYearChangePercent";
				result = queryPageItem(htmlPage, xPathLookup.lookupIndexOpen(key));
				indexData.put(key, result.replaceAll("[,%]", "").trim());

				// TimeStamp
				key = "timeStamp";
				result = queryPageItem(htmlPage, xPathLookup.lookupIndexOpen(key));
				indexData.put(key, result.replaceAll("[,%]", "").trim());

			}

			// If we are in debug mode, display the values we are returning
			if (Debug.query() == true) {
				Output.debugPrintln("Index Data Results:");
				for (String i : indexData.keySet()) {
					Output.debugPrintln("  - " + i + ": " + this.get(i));
				}
			}

		} catch (Exception ex) {
			indexData.put("status", "error");
		}

	}

}
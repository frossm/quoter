/**************************************************************************************************************
 * Quoter.jar
 * 
 * Quoter is a command line program that display stock quotes and index data.
 * 
 * *  Copyright (c) 2019-2024 Michael Fross
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

public class Index {
	HashMap<String, String> indexData = new HashMap<>();
	Config xPathLookup = new Config();

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
		return doc.selectXpath(xPath).text();
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
		// String URL = "https://www.marketwatch.com/investing/index/SYMBOLHERE";
		String URL = "https://finance.yahoo.com/quote/SYMBOLHERE";
		Document htmlPage = null;

		// Ensure a valid value was passed
		switch (idx.toUpperCase()) {
		case "DOW":
			URL = URL.replaceAll("SYMBOLHERE", "%5EDJI");
			break;
		case "NASDAQ":
			URL = URL.replaceAll("SYMBOLHERE", "%5EIXIC");
			break;
		case "S&P":
			URL = URL.replaceAll("SYMBOLHERE", "%5EGSPC");
			break;
		default:
			Output.fatalError("Call to getIndex() must be 'DOW', 'NASDAQ', or 'S&P'", 4);
			break;
		}

		Output.debugPrintln("Index URL rewritten to: " + URL);

		// Add index name to hash
		indexData.put("index", idx);

		try {
			// Download and parse the the webpage with xSoup
			try {
				htmlPage = Jsoup.connect(URL).timeout(Config.queryURLTimeout()).userAgent(Config.queryUserAgent()).get();

			} catch (HttpStatusException ex) {
				this.indexData.put("status", "error");
				return;
			}

			// Add the name to the hash
			this.indexData.put("index", idx);
			this.indexData.put("status", "ok");

			// MarketWatch has different XPaths depending if the market is open or closed
			if (MarketState.queryMarketOpen() == false) {
				// Market is CLOSED
				Output.debugPrintln("Market is currently CLOSED");

				// Current Price
				String key = "latestPrice";
				String result = queryPageItem(htmlPage, this.xPathLookup.lookupIndexClosed(key));
				indexData.put(key, result.replaceAll("[,%]", "").trim());

				// Change
				key = "change";
				result = queryPageItem(htmlPage, this.xPathLookup.lookupIndexClosed(key));
				indexData.put(key, result.replaceAll("[,%]", "").trim());

				// Change Percent
				key = "changePercent";
				result = queryPageItem(htmlPage, this.xPathLookup.lookupIndexClosed(key));
				result = result.replaceAll("[\\(\\)]", "");
				indexData.put(key, result.replaceAll("[,%)(]", "").trim());

				// 52 Week Range
				key = "52weekRange";
				result = queryPageItem(htmlPage, this.xPathLookup.lookupIndexClosed(key));

				String w52Low = result.split(" - ")[0];
				String w52High = result.split(" - ")[1];

				indexData.put("52weekLow", w52Low.replaceAll("[,%]", "").trim());
				indexData.put("52weekHigh", w52High.replaceAll("[,%]", "").trim());

				// Day Week Range
				key = "dayRange";
				result = queryPageItem(htmlPage, this.xPathLookup.lookupIndexClosed(key));

				String dayLow = result.split(" - ")[0];
				String dayHigh = result.split(" - ")[1];

				indexData.put("dayLow", dayLow.replaceAll("[,%]", "").trim());
				indexData.put("dayHigh", dayHigh.replaceAll("[,%]", "").trim());

				// TimeStamp
				key = "timeStamp";
				result = queryPageItem(htmlPage, this.xPathLookup.lookupIndexClosed(key));
				indexData.put(key, result.replaceAll("[Aa]t [Cc]lose: ", "").trim());

			} else {
				// Market is OPEN
				Output.debugPrintln("Market is currently OPEN");

				// Current Price
				String key = "latestPrice";
				String result = queryPageItem(htmlPage, this.xPathLookup.lookupIndexOpen(key));
				indexData.put(key, result.replaceAll("[,%]", "").trim());

				// Change
				key = "change";
				result = queryPageItem(htmlPage, this.xPathLookup.lookupIndexOpen(key));
				indexData.put("change", result.replaceAll("[,%]", "").trim());

				// Change Percent
				key = "changePercent";
				result = queryPageItem(htmlPage, this.xPathLookup.lookupIndexOpen(key));
				indexData.put(key, result.replaceAll("[,%)(]", "").trim());

				// 52 Week Range
				key = "52weekRange";
				result = queryPageItem(htmlPage, this.xPathLookup.lookupIndexOpen(key));

				String w52Low = result.split(" - ")[0];
				String w52High = result.split(" - ")[1];

				indexData.put("52weekLow", w52Low.replaceAll("[,%]", "").trim());
				indexData.put("52weekHigh", w52High.replaceAll("[,%]", "").trim());

				// Day Week Range
				key = "dayRange";
				result = queryPageItem(htmlPage, this.xPathLookup.lookupIndexOpen(key));

				String dayLow = result.split(" - ")[0];
				String dayHigh = result.split(" - ")[1];

				indexData.put("dayLow", dayLow.replaceAll("[,%]", "").trim());
				indexData.put("dayHigh", dayHigh.replaceAll("[,%]", "").trim());

				// TimeStamp
				key = "timeStamp";
				result = queryPageItem(htmlPage, this.xPathLookup.lookupIndexOpen(key));
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
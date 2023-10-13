/**************************************************************************************************************
 * Quoter.jar
 * 
 * Quoter is a command line program that display stock quotes and index data.
 * 
 *  Copyright (c) 2019-2023 Michael Fross
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

public class Symbol {
	HashMap<String, String> symbolData = new HashMap<>();
	XPathLookup xPathLookup = new XPathLookup();

	/**
	 * Symbol Constructor(): Initialize class with a symbol to process
	 * 
	 * @param symb
	 */
	public Symbol(String symb) {
		getSymbolData(symb);
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
			return this.symbolData.get(field);
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
			this.symbolData.put(field, value);
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

		for (String i : this.symbolData.keySet()) {
			returnList.add(i);
		}

		return returnList;
	}

	private void getSymbolData(String symb) {
		String URL = "https://www.marketwatch.com/investing/stock/SYMBOLHERE";
		Document htmlPage = null;
		boolean marketOpen = false;

		// Add the provided symbol to the URL template
		URL = URL.replaceAll("SYMBOLHERE", symb);
		Output.debugPrintln("Symbol URL rewritten to: " + URL);

		try {
			// Download and parse the the webpage with xsoup
			try {
				htmlPage = Jsoup.connect(URL).userAgent("Mozilla").get();
			} catch (HttpStatusException ex) {
				this.symbolData.put("status", "error");
				return;
			}

			// Provide a status & name field
			this.symbolData.put("symbol", symb.toUpperCase());
			this.symbolData.put("status", "ok");

			// Determine if the market is open or closed
			if (Symbol.queryPageItem(htmlPage, this.xPathLookup.lookupIndexOpen("marketStatus")).toLowerCase().contains("open") == true) {
				marketOpen = true;
			} else {
				marketOpen = false;
			}

			// MarketWatch has different XPaths depending if the market is open or closed
			if (marketOpen == false) {
				// Market is CLOSED
				Output.debugPrintln("Market is currently CLOSED");

				// Current Price
				String key = "latestPrice";
				String result = queryPageItem(htmlPage, this.xPathLookup.lookupSymbolClosed(key));
				this.symbolData.put(key, result.replaceAll("[$,%]", "").trim());

				// Change
				key = "change";
				result = queryPageItem(htmlPage, this.xPathLookup.lookupSymbolClosed(key));
				this.symbolData.put(key, result.replaceAll("[$,%]", "").trim());

				// Change Percent
				key = "changePercent";
				result = queryPageItem(htmlPage, this.xPathLookup.lookupSymbolClosed(key));
				this.symbolData.put(key, result.replaceAll("[,%]", "").trim());

				// 52 Week High / Low - Get range and split into high/low
				key = "52weekRange";
				result = queryPageItem(htmlPage, this.xPathLookup.lookupSymbolClosed(key));

				String low52 = "";
				String high52 = "";
				try {
					low52 = result.split(" - ")[0];
					high52 = result.split(" - ")[1];
				} catch (Exception ex) {
					low52 = high52 = "-";
				}

				this.symbolData.put("week52High", high52.replaceAll("[,%]", "").trim());
				this.symbolData.put("week52Low", low52.replaceAll("[,%]", "").trim());

				// Day Range - Get range and split into high/low
				key = "dayRange";
				result = queryPageItem(htmlPage, this.xPathLookup.lookupSymbolClosed(key));

				String lowD = "";
				String highD = "";
				try {
					lowD = result.split(" - ")[0];
					highD = result.split(" - ")[1];
				} catch (Exception ex) {
					lowD = highD = "-";
				}

				this.symbolData.put("dayHigh", highD.replaceAll("[,%]", "").trim());
				this.symbolData.put("dayLow", lowD.replaceAll("[,%]", "").trim());

				// Year to Date Change
				key = "ytdChangePercent";
				this.setOptionalField(htmlPage, key, MarketStatus.Closed);

				// One Year Change Percent
				key = "oneYearChangePercent";
				this.setOptionalField(htmlPage, key, MarketStatus.Closed);

				// TimeStamp
				key = "timeStamp";
				result = queryPageItem(htmlPage, this.xPathLookup.lookupSymbolClosed(key));
				this.symbolData.put(key, result.replaceAll("[,%]", "").trim());

				// Full Name of Company
				key = "fullname";
				result = queryPageItem(htmlPage, this.xPathLookup.lookupSymbolClosed(key));
				this.symbolData.put(key, result.trim());

			} else {
				// Market is OPEN
				Output.debugPrintln("Market is currently OPEN");

				// Current Price
				String key = "latestPrice";
				String result = queryPageItem(htmlPage, this.xPathLookup.lookupSymbolOpen(key));
				this.symbolData.put(key, result.replaceAll("[,%]", "").trim());

				// Change
				key = "change";
				result = queryPageItem(htmlPage, this.xPathLookup.lookupSymbolOpen(key));
				this.symbolData.put(key, result.replaceAll("[,%]", "").trim());

				// Change Percent
				key = "changePercent";
				result = queryPageItem(htmlPage, this.xPathLookup.lookupSymbolOpen(key));
				this.symbolData.put(key, result.replaceAll("[,%]", "").trim());

				// 52 Week High / Low - Get range and split into high/low
				key = "52weekRange";
				result = queryPageItem(htmlPage, this.xPathLookup.lookupSymbolOpen(key));

				String low52 = "";
				String high52 = "";
				try {
					low52 = result.split(" - ")[0];
					high52 = result.split(" - ")[1];
				} catch (Exception ex) {
					low52 = high52 = "-";
				}

				this.symbolData.put("week52High", high52.replaceAll("[,%]", "").trim());
				this.symbolData.put("week52Low", low52.replaceAll("[,%]", "").trim());

				// Day Range - Get range and split into high/low
				key = "dayRange";
				result = queryPageItem(htmlPage, this.xPathLookup.lookupSymbolOpen(key));

				String lowD = "";
				String highD = "";
				try {
					lowD = result.split(" - ")[0];
					highD = result.split(" - ")[1];
				} catch (Exception ex) {
					lowD = highD = "-";
				}

				this.symbolData.put("dayHigh", highD.replaceAll("[,%]", "").trim());
				this.symbolData.put("dayLow", lowD.replaceAll("[,%]", "").trim());

				// Year to Date Change
				key = "ytdChangePercent";
				this.setOptionalField(htmlPage, key, MarketStatus.Open);

				// One Year Change Percent
				key = "oneYearChangePercent";
				this.setOptionalField(htmlPage, key, MarketStatus.Open);

				// TimeStamp
				key = "timeStamp";
				result = queryPageItem(htmlPage, this.xPathLookup.lookupSymbolOpen(key));
				this.symbolData.put(key, result.replaceAll("[,%]", "").trim());

				// Full Name of Company
				key = "fullname";
				result = queryPageItem(htmlPage, this.xPathLookup.lookupSymbolOpen(key));
				this.symbolData.put(key, result.trim());
			}

			// If we are in debug mode, display the values of the symbol
			if (Debug.query() == true) {
				Output.debugPrintln("Symbol Data Results:");
				for (String i : this.symbolData.keySet()) {
					Output.debugPrintln("  - " + i + ": " + this.get(i));
				}
			}

		} catch (Exception ex) {
			// Most likely an invalid symbol
			this.symbolData.put("status", "error");
		}

	}

	/**
	 * setOptionalField(): Handles error conditions for those fields that do not have a valid value from the website
	 * 
	 * @param htmlPage
	 * @param key
	 * @param marketStatus
	 */
	private void setOptionalField(final Document htmlPage, final String key, final MarketStatus marketStatus) {
		try {
			final String result = queryPageItem(htmlPage,
					marketStatus == MarketStatus.Closed ? this.xPathLookup.lookupSymbolClosed(key) : this.xPathLookup.lookupSymbolOpen(key));
			symbolData.put(key, result.replaceAll("[,%]", "").trim());

		} catch (Exception e) {
			Output.debugPrintln("Failed to fetch key: " + key + " from page. Setting value as '---'");
			symbolData.put(key, "---");
		}
	}

	private enum MarketStatus {
		Open, Closed
	}

}

/**************************************************************************************************************
 * Quoter.jar
 * 
 * Quoter is a command line program that display stock quotes and index data.
 * 
 *  Copyright (c) 2019-2024 Michael Fross
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.fross.library.Debug;
import org.fross.library.Output;
import org.fusesource.jansi.Ansi;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Symbol {
	HashMap<String, String> symbolData = new HashMap<>();
	Config xPathLookup = new Config();

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
		String URL = "https://finance.yahoo.com/quote/SYMBOLHERE/key-statistics";
		Document htmlPage = null;

		// Add the provided symbol to the URL template
		URL = URL.replaceAll("SYMBOLHERE", symb);
		Output.debugPrintln("Symbol URL rewritten to: " + URL);

		try {
			// Download and parse the the webpage with xSoup
			try {
				htmlPage = Jsoup.connect(URL).timeout(Config.queryURLTimeout()).userAgent(Config.queryUserAgent()).get();

			} catch (IOException ex) {
				this.symbolData.put("status", "error");
				return;
			}

			// Provide a status & name field
			this.symbolData.put("symbol", symb.toUpperCase());
			this.symbolData.put("status", "ok");

			// MarketWatch has different XPaths depending if the market is open or closed
			if (MarketState.queryMarketOpen() == false) {
				// Market is CLOSED
				Output.debugPrintln("Market is currently CLOSED");

				// Latest Price
				String key = "latestPrice";
				String result = queryPageItem(htmlPage, this.xPathLookup.lookupSymbolClosed(key));
				this.symbolData.put(key, result.replaceAll("[$,%]", "").trim());

				// Change
				key = "change";
				result = queryPageItem(htmlPage, this.xPathLookup.lookupSymbolClosed(key));
				this.symbolData.put(key, result.replaceAll("[$,%]", "").trim());

				// Change Percentage
				key = "changePercent";
				result = queryPageItem(htmlPage, this.xPathLookup.lookupSymbolClosed(key));
				this.symbolData.put(key, result.replaceAll("[,%)(]", "").trim());

				// 52 Week High
				key = "52weekHigh";
				result = queryPageItem(htmlPage, this.xPathLookup.lookupSymbolClosed(key));
				this.setOptionalField(htmlPage, key, MarketStatus.Closed);

				// 52 Week Low
				key = "52weekLow";
				result = queryPageItem(htmlPage, this.xPathLookup.lookupSymbolClosed(key));
				this.setOptionalField(htmlPage, key, MarketStatus.Closed);

				// 52 Week Change Percentage
				try {
					key = "52weekChange";
					float high = Float.valueOf(queryPageItem(htmlPage, this.xPathLookup.lookupSymbolOpen("52weekHigh")));
					float low = Float.valueOf(queryPageItem(htmlPage, this.xPathLookup.lookupSymbolOpen("52weekLow")));
					String chng = String.valueOf((low / high) * 100).trim();
					this.symbolData.put(key, chng);
				} catch (Exception ex) {
					symbolData.put(key, "---");
				}

				// 50 Day Moving Average
				key = "50dayAvg";
				result = queryPageItem(htmlPage, this.xPathLookup.lookupSymbolClosed(key));
				this.setOptionalField(htmlPage, key, MarketStatus.Closed);

				// 200 Day Moving Average
				key = "200dayAvg";
				result = queryPageItem(htmlPage, this.xPathLookup.lookupSymbolClosed(key));
				this.setOptionalField(htmlPage, key, MarketStatus.Closed);

				// TimeStamp
				key = "timeStamp";
				result = queryPageItem(htmlPage, this.xPathLookup.lookupSymbolClosed(key));
				this.symbolData.put(key, result.replaceAll("^.*: ", "").trim());

				// Full Name of Company
				key = "fullname";
				result = queryPageItem(htmlPage, this.xPathLookup.lookupSymbolClosed(key));
				this.symbolData.put(key, result.trim());

			} else {
				// Market is OPEN
				Output.debugPrintln("Market is currently OPEN");

				// Current Price
				String key = "latestPrice";
				String result = htmlPage.selectXpath(this.xPathLookup.lookupSymbolOpen(key)).text();
				this.symbolData.put(key, result.replaceAll("[,%]", "").trim());

				// Change
				key = "change";
				result = htmlPage.selectXpath(this.xPathLookup.lookupSymbolOpen(key)).text();
				this.symbolData.put(key, result.replaceAll("[,%]", "").trim());

				// Change Percent
				key = "changePercent";
				result = htmlPage.selectXpath(this.xPathLookup.lookupSymbolOpen(key)).text();
				this.symbolData.put(key, result.replaceAll("[,%)(]", "").trim());

				// 52 Week High
				key = "52weekHigh";
				result = htmlPage.selectXpath(this.xPathLookup.lookupSymbolOpen(key)).text();
				this.setOptionalField(htmlPage, key, MarketStatus.Open);

				// 52 Week Low
				key = "52weekLow";
				result = htmlPage.selectXpath(this.xPathLookup.lookupSymbolOpen(key)).text();
				this.setOptionalField(htmlPage, key, MarketStatus.Open);

				// 52 Week Change Percentage
				try {
					key = "52weekChange";
					float high = Float.valueOf(queryPageItem(htmlPage, this.xPathLookup.lookupSymbolOpen("52weekHigh")));
					float low = Float.valueOf(queryPageItem(htmlPage, this.xPathLookup.lookupSymbolOpen("52weekLow")));
					String chng = String.valueOf((low / high) * 100).trim();
					this.symbolData.put(key, chng);
				} catch (Exception ex) {
					symbolData.put(key, "---");
				}

				// 50 Day Moving Average
				key = "50dayAvg";
				result = htmlPage.selectXpath(this.xPathLookup.lookupSymbolOpen(key)).text();
				this.setOptionalField(htmlPage, key, MarketStatus.Open);

				// 200 Day Moving Average
				key = "200dayAvg";
				result = htmlPage.selectXpath(this.xPathLookup.lookupSymbolOpen(key)).text();
				this.setOptionalField(htmlPage, key, MarketStatus.Open);

				// TimeStamp
				key = "timeStamp";
				result = htmlPage.selectXpath(this.xPathLookup.lookupSymbolOpen(key)).text();
				this.symbolData.put(key, result.replaceAll("[,%]", "").trim());

				// Full Name of Company
				key = "fullname";
				result = htmlPage.selectXpath(this.xPathLookup.lookupSymbolOpen(key)).text();
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
			Output.debugPrintln("Failed to fetch key: '" + key + "' from page. Setting value as '---'");
			symbolData.put(key, "---");
		}
	}

	private enum MarketStatus {
		Open, Closed
	}

}

/**************************************************************************************************************
 * Quoter.jar
 * 
 * Quoter is a command line program that display stock quotes and index data.
 * 
 *  Copyright (c) 2019-2022 Michael Fross
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
			symbolData.put(field, value);
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
		Output.debugPrint("Symbol URL rewritten to: " + URL);

		try {
			// Download and parse the the webpage with xsoup
			try {
				htmlPage = Jsoup.connect(URL).userAgent("Mozilla").get();
			} catch (HttpStatusException ex) {
				this.symbolData.put("status", "error");
			}

			// Provide a status & name field
			this.symbolData.put("symbol", symb.toUpperCase());
			this.symbolData.put("status", "ok");
			
			// Determine if the market is open or closed
			String marketOpenXPath = "/html/body/div[3]/div[2]/div[3]/div/small/div";
			String openResult = Symbol.queryPageItem(htmlPage, marketOpenXPath).toLowerCase();
			if (Symbol.queryPageItem(htmlPage, marketOpenXPath).toLowerCase().contains("open") == true) {
				marketOpen = true;
			} else {
				marketOpen = false;
			}

			// MarketWatch has different XPaths depending if the market is open or closed
			if (marketOpen == false) {
				// Market is CLOSED
				Output.debugPrint("Market is currently CLOSED");

				// Current Price
				String xPath = "/html/body/div[3]/div[2]/div[3]/div/div[2]/h2/bg-quote";
				String result = queryPageItem(htmlPage, xPath);
				symbolData.put("latestPrice", result.replaceAll("[,%]", "").trim());

				// Change
				xPath = "/html/body/div[3]/div[2]/div[3]/div/div[4]/table/tbody/tr/td[2]";
				result = queryPageItem(htmlPage, xPath);
				symbolData.put("change", result.replaceAll("[,%]", "").trim());

				// Change Percent
				xPath = "/html/body/div[3]/div[2]/div[3]/div/div[4]/table/tbody/tr/td[3]";
				result = queryPageItem(htmlPage, xPath);
				symbolData.put("changePercent", result.replaceAll("[,%]", "").trim());

				// 52 Week High / Low - Get range and split into high/low
				xPath = "/html/body/div[3]/div[6]/div[1]/div[1]/div/ul/li[3]/span[1]";
				result = queryPageItem(htmlPage, xPath);

				String low52 = "";
				String high52 = "";
				try {
					low52 = result.split(" - ")[0];
					high52 = result.split(" - ")[1];
				} catch (Exception ex) {
					low52 = high52 = "-";
				}

				symbolData.put("week52High", high52.replaceAll("[,%]", "").trim());
				symbolData.put("week52Low", low52.replaceAll("[,%]", "").trim());

				// Day Range - Get range and split into high/low
				xPath = "/html/body/div[3]/div[6]/div[1]/div[1]/div/ul/li[2]/span[1]";
				result = queryPageItem(htmlPage, xPath);

				String lowD = "";
				String highD = "";
				try {
					lowD = result.split(" - ")[0];
					highD = result.split(" - ")[1];
				} catch (Exception ex) {
					lowD = highD = "-";
				}

				symbolData.put("dayHigh", highD.replaceAll("[,%]", "").trim());
				symbolData.put("dayLow", lowD.replaceAll("[,%]", "").trim());

				// Year to Date Change
				xPath = "/html/body/div[3]/div[6]/div[1]/div[2]/div[1]/table/tbody/tr[4]/td[2]/ul/li[1]";
				result = queryPageItem(htmlPage, xPath);
				symbolData.put("ytdChange", result.replaceAll("[,%]", "").trim());

				// TimeStamp
				xPath = "/html/body/div[3]/div[2]/div[3]/div/div[1]/span/bg-quote";
				result = queryPageItem(htmlPage, xPath);
				symbolData.put("timeStamp", result.replaceAll("[,%]", "").trim());
				
			} else {
				// Market is OPEN
				Output.debugPrint("Market is currently OPEN");

				// Current Price
				String xPath = "/html/body/div[3]/div[2]/div[3]/div/div[2]/h2/bg-quote";
				String result = queryPageItem(htmlPage, xPath);
				symbolData.put("latestPrice", result.replaceAll("[,%]", "").trim());

				// Change
				xPath = "/html/body/div[3]/div[2]/div[3]/div/div[2]/bg-quote/span[1]/bg-quote";
				result = queryPageItem(htmlPage, xPath);
				symbolData.put("change", result.replaceAll("[,%]", "").trim());

				// Change Percent
				xPath = "/html/body/div[3]/div[2]/div[3]/div/div[2]/bg-quote/span[2]/bg-quote";
				result = queryPageItem(htmlPage, xPath);
				symbolData.put("changePercent", result.replaceAll("[,%]", "").trim());

				// 52 Week High / Low - Get range and split into high/low
				xPath = "/html/body/div[3]/div[6]/div[1]/div[1]/div/ul/li[3]/span[1]";
				result = queryPageItem(htmlPage, xPath);

				String low52 = "";
				String high52 = "";
				try {
					low52 = result.split(" - ")[0];
					high52 = result.split(" - ")[1];
				} catch (Exception ex) {
					low52 = high52 = "-";
				}

				symbolData.put("week52High", high52.replaceAll("[,%]", "").trim());
				symbolData.put("week52Low", low52.replaceAll("[,%]", "").trim());

				// Day Range - Get range and split into high/low
				xPath = "/html/body/div[3]/div[6]/div[1]/div[1]/div/ul/li[2]/span[1]";
				result = queryPageItem(htmlPage, xPath);

				String lowD = "";
				String highD = "";
				try {
					lowD = result.split(" - ")[0];
					highD = result.split(" - ")[1];
				} catch (Exception ex) {
					lowD = highD = "-";
				}

				symbolData.put("dayHigh", highD.replaceAll("[,%]", "").trim());
				symbolData.put("dayLow", lowD.replaceAll("[,%]", "").trim());

				// Year to Date Change
				xPath = "/html/body/div[3]/div[6]/div[1]/div[2]/div[1]/table/tbody/tr[4]/td[2]/ul/li[1]";
				result = queryPageItem(htmlPage, xPath);
				symbolData.put("ytdChange", result.replaceAll("[,%]", "").trim());

				// TimeStamp
				xPath = "/html/body/div[3]/div[2]/div[3]/div/div[1]/span/bg-quote";
				result = queryPageItem(htmlPage, xPath);
				symbolData.put("timeStamp", result.replaceAll("[,%]", "").trim());
			}

			// If we are in debug mode, display the values of the symbol
			if (Debug.query() == true) {
				Output.debugPrint("Symbol Data Results:");
				for (String i : symbolData.keySet()) {
					Output.debugPrint("  - " + i + ": " + this.get(i));
				}
			}

		} catch (Exception ex) {
			// Most likely an invalid symbol
			this.symbolData.put("status", "error");
		}

	}

}
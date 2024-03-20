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

import org.fross.library.Output;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class MarketState {
	/**
	 * queryMarketOpen(): Returns if the US index market is currently open
	 * 
	 * @return
	 */
	protected static boolean queryMarketOpen() {
		final String URL = "https://finance.yahoo.com/quote/F/key-statistics";
		String marketOpenResult = "";

		// Download and parse the the webpage and parse it with xSoup
		try {
			Document doc = Jsoup.connect(URL).timeout(Config.queryURLTimeout()).userAgent(Config.queryUserAgent()).get();
			marketOpenResult = doc.selectXpath(Config.marketStatusXPath).text().toLowerCase();

		} catch (Exception ex) {
			Output.fatalError("Could not determine if the market is open or closed", 7);
		}

		Output.debugPrintln("MarketOpen result: '" + marketOpenResult + "'");

		if (marketOpenResult.contains("open") == true)
			return true;
		else if (marketOpenResult.contains("close") == true)
			return false;
		else
			Output.fatalError("FATAL ERROR: Could not determine if the market is opened or closed", 7);

		return false;

	}
}
/**************************************************************************************************************
 * Quoter.jar
 * 
 * Quoter is a command line program that display stock quotes and index data.
 * 
 * Copyright (c) 2019-2024 Michael Fross
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *   
 ***************************************************************************************************************/
package org.fross.quoter;

import java.util.HashMap;

public class Config {
	HashMap<String, String> symbolOpen = new HashMap<>();
	HashMap<String, String> symbolClosed = new HashMap<>();
	HashMap<String, String> indexOpen = new HashMap<>();
	HashMap<String, String> indexClosed = new HashMap<>();
	static String marketStatusXPath;

	/**
	 * Constructor: Populates the hash maps with the xPath strings
	 */
	public Config() {
		// ---------------------------------------------------------------------------------
		// xPaths: Symbols with market CLOSED
		// ---------------------------------------------------------------------------------
		this.symbolClosed.put("latestPrice",   "//*[@id=\"quote-header-info\"]/div[3]/div[1]/div[1]/fin-streamer[1]");
		this.symbolClosed.put("change", "//*[@id=\"quote-header-info\"]/div[3]/div[1]/div[1]/fin-streamer[2]/span");
		this.symbolClosed.put("changePercent", "//*[@id=\"quote-header-info\"]/div[3]/div[1]/div[1]/fin-streamer[3]/span");
		
		this.symbolClosed.put("52weekHigh", "//*[@id=\"Col1-0-KeyStatistics-Proxy\"]/section/div[3]/div[2]/div/div[1]/div/div/table/tbody/tr[4]/td[2]");
		this.symbolClosed.put("52weekLow", "//*[@id=\"Col1-0-KeyStatistics-Proxy\"]/section/div[3]/div[2]/div/div[1]/div/div/table/tbody/tr[5]/td[2]");
		
		this.symbolClosed.put("50dayAvg", "//*[@id=\"Col1-0-KeyStatistics-Proxy\"]/section/div[3]/div[2]/div/div[1]/div/div/table/tbody/tr[6]/td[2]");
		this.symbolClosed.put("200dayAvg", "//*[@id=\"Col1-0-KeyStatistics-Proxy\"]/section/div[3]/div[2]/div/div[1]/div/div/table/tbody/tr[7]/td[2]");
				
		this.symbolClosed.put("timeStamp", "//*[@id=\"quote-market-notice\"]/span");
		this.symbolClosed.put("fullname", "//*[@id=\"quote-header-info\"]/div[2]/div[1]/div[1]/h1");

		// ---------------------------------------------------------------------------------
		// xPaths: Symbols with market OPEN
		// ---------------------------------------------------------------------------------
		this.symbolOpen.put("latestPrice", "//*[@id=\"quote-header-info\"]/div[3]/div[1]/div/fin-streamer[1]");
		this.symbolOpen.put("change", "//*[@id=\"quote-header-info\"]/div[3]/div[1]/div/fin-streamer[2]/span");
		this.symbolOpen.put("changePercent", "//*[@id=\"quote-header-info\"]/div[3]/div[1]/div/fin-streamer[3]/span");
		
		this.symbolOpen.put("52weekHigh", "//*[@id=\"Col1-0-KeyStatistics-Proxy\"]/section/div[3]/div[2]/div/div[1]/div/div/table/tbody/tr[4]/td[2]");
		this.symbolOpen.put("52weekLow", "//*[@id=\"Col1-0-KeyStatistics-Proxy\"]/section/div[3]/div[2]/div/div[1]/div/div/table/tbody/tr[5]/td[2]");
		
		this.symbolOpen.put("50dayAvg", "//*[@id=\"Col1-0-KeyStatistics-Proxy\"]/section/div[3]/div[2]/div/div[1]/div/div/table/tbody/tr[6]/td[2]");
		this.symbolOpen.put("200dayAvg", "//*[@id=\"Col1-0-KeyStatistics-Proxy\"]/section/div[3]/div[2]/div/div[1]/div/div/table/tbody/tr[7]/td[2]");
				
		this.symbolOpen.put("timeStamp", "//*[@id=\"quote-market-notice\"]/span");
		this.symbolOpen.put("fullname", "//*[@id=\"quote-header-info\"]/div[2]/div[1]/div[1]/h1");

		// ---------------------------------------------------------------------------------
		// xPaths: Indexes with market CLOSED
		// ---------------------------------------------------------------------------------
		this.indexClosed.put("latestPrice", "//*[@id=\"quote-header-info\"]/div[3]/div[1]/div/fin-streamer[1]");
		this.indexClosed.put("change", "//*[@id=\"quote-header-info\"]/div[3]/div[1]/div/fin-streamer[2]/span");
		this.indexClosed.put("changePercent", "//*[@id=\"quote-header-info\"]/div[3]/div[1]/div/fin-streamer[3]/span");
		this.indexClosed.put("52weekRange", "//*[@id=\"quote-summary\"]/div[2]/table/tbody/tr[2]/td[2]");
		this.indexClosed.put("dayRange", "//*[@id=\"quote-summary\"]/div[2]/table/tbody/tr[1]/td[2]");
		this.indexClosed.put("timeStamp", "//*[@id=\"quote-market-notice\"]/span");

		// ---------------------------------------------------------------------------------
		// xPaths: Indexes with market OPEN
		// ---------------------------------------------------------------------------------
		this.indexOpen.put("latestPrice", "//*[@id=\"quote-header-info\"]/div[3]/div[1]/div/fin-streamer[1]");
		this.indexOpen.put("change", "//*[@id=\"quote-header-info\"]/div[3]/div[1]/div/fin-streamer[2]/span");
		this.indexOpen.put("changePercent", "//*[@id=\"quote-header-info\"]/div[3]/div[1]/div/fin-streamer[3]/span");
		this.indexOpen.put("52weekRange", "//*[@id=\"quote-summary\"]/div[2]/table/tbody/tr[2]/td[2]");
		this.indexOpen.put("dayRange", "//*[@id=\"quote-summary\"]/div[2]/table/tbody/tr[1]/td[2]");
		this.indexOpen.put("timeStamp", "//*[@id=\"quote-market-notice\"]/span");

		// ---------------------------------------------------------------------------------
		// xPaths: Market Status from an index page
		// ---------------------------------------------------------------------------------
		marketStatusXPath = "/html/body/div[1]/div/div/div[1]/div/div[2]/div/div/div[6]/div/div/div/div[3]/div[1]/div/div/span";
	}

	/**
	 * lookupSymbolOpen(): Returns the xPath string as mapped to the provided key
	 * 
	 * @param key
	 * @return
	 */
	public String lookupSymbolOpen(String key) {
		return this.symbolOpen.get(key);
	}

	/**
	 * lookupSymbolClosed(): Returns the xPath string as mapped to the provided key
	 * 
	 * @param key
	 * @return
	 */
	public String lookupSymbolClosed(String key) {
		return this.symbolClosed.get(key);
	}

	/**
	 * lookupIndexOpen(): Returns the xPath string as mapped to the provided key
	 * 
	 * @param key
	 * @return
	 */
	public String lookupIndexOpen(String key) {
		return this.indexOpen.get(key);
	}

	/**
	 * lookupIndexClosed(): Returns the xPath string as mapped to the provided key
	 * 
	 * @param key
	 * @return
	 */
	public String lookupIndexClosed(String key) {
		return this.indexClosed.get(key);
	}

	/**
	 * queryUserAgent(): Return the user agent used for the Jsoup connect calls
	 * 
	 * @return
	 */
	public static String queryUserAgent() {
		final String UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36";
		return UA;
	}

	/**
	 * queryURLTimeout(): Return the timeout for getting data from webpages
	 * 
	 * @return
	 */
	public static int queryURLTimeout() {
		final int TIMEOUT = 20000;
		return TIMEOUT;

	}
}

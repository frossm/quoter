/**************************************************************************************************************
 * Quoter.jar
 * 
 * Quoter is a command line program that display stock quotes and index data.
 * 
 * Copyright (c) 2019-2023 Michael Fross
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

public class XPathLookup {
	HashMap<String, String> symbolOpen = new HashMap<>();
	HashMap<String, String> symbolClosed = new HashMap<>();
	HashMap<String, String> indexOpen = new HashMap<>();
	HashMap<String, String> indexClosed = new HashMap<>();

	/**
	 * Constructor: Populates the hash maps with the xPath strings
	 */
	public XPathLookup() {
		// ---------------------------------------------------------------------------------
		// xPaths: Symbols with market CLOSED
		// ---------------------------------------------------------------------------------
		this.symbolClosed.put("latestPrice", "/html/body/div[3]/div[2]/div[3]/div/div[4]/table/tbody/tr/td[1]");
		this.symbolClosed.put("change", "/html/body/div[3]/div[2]/div[3]/div/div[4]/table/tbody/tr/td[2]");
		this.symbolClosed.put("changePercent", "/html/body/div[3]/div[2]/div[3]/div/div[4]/table/tbody/tr/td[3]");
		this.symbolClosed.put("52weekRange", "/html/body/div[3]/div[6]/div[1]/div[1]/div/ul/li[3]/span[1]");
		this.symbolClosed.put("dayRange", "/html/body/div[3]/div[6]/div[1]/div[1]/div/ul/li[2]/span[1]");
		this.symbolClosed.put("ytdChangePercent", "/html/body/div[3]/div[6]/div[1]/div[2]/div[1]/table/tbody/tr[4]/td[2]/ul/li[1]");
		this.symbolClosed.put("oneYearChangePercent", "/html/body/div[3]/div[6]/div[1]/div[2]/div[1]/table/tbody/tr[5]/td[2]/ul/li[1]");
		this.symbolClosed.put("timeStamp", "/html/body/div[3]/div[2]/div[3]/div/div[1]/span/bg-quote");
		this.symbolClosed.put("fullname", "/html/body/div[3]/div[2]/div[2]/div/div[2]/h1");

		// ---------------------------------------------------------------------------------
		// xPaths: Symbols with market OPEN
		// ---------------------------------------------------------------------------------
		this.symbolOpen.put("latestPrice", "/html/body/div[3]/div[2]/div[3]/div/div[2]/h2/bg-quote");
		this.symbolOpen.put("change", "/html/body/div[3]/div[2]/div[3]/div/div[2]/bg-quote/span[1]/bg-quote");
		this.symbolOpen.put("changePercent", "/html/body/div[3]/div[2]/div[3]/div/div[2]/bg-quote/span[2]/bg-quote");
		this.symbolOpen.put("52weekRange", "/html/body/div[3]/div[6]/div[1]/div[1]/div/ul/li[3]/span[1]");
		this.symbolOpen.put("dayRange", "/html/body/div[3]/div[6]/div[1]/div[1]/div/ul/li[2]/span[1]");
		this.symbolOpen.put("ytdChangePercent", "/html/body/div[3]/div[6]/div[1]/div[2]/div[1]/table/tbody/tr[4]/td[2]/ul/li[1]");
		this.symbolOpen.put("oneYearChangePercent", "/html/body/div[3]/div[6]/div[1]/div[2]/div[1]/table/tbody/tr[5]/td[2]/ul/li[1]");
		this.symbolOpen.put("timeStamp", "/html/body/div[3]/div[2]/div[3]/div/div[1]/span/bg-quote");
		this.symbolOpen.put("fullname", "/html/body/div[3]/div[2]/div[2]/div/div[2]/h1");

		// ---------------------------------------------------------------------------------
		// xPaths: Indexes with market CLOSED
		// ---------------------------------------------------------------------------------
		this.indexClosed.put("latestPrice", "/html/body/div[3]/div[2]/div[3]/div/div[2]/h2/span");
		this.indexClosed.put("change", "/html/body/div[3]/div[2]/div[3]/div/div[2]/bg-quote/span[1]");
		this.indexClosed.put("changePercent", "/html/body/div[3]/div[2]/div[3]/div/div[2]/bg-quote/span[2]");
		this.indexClosed.put("52weekRange", "/html/body/div[3]/div[6]/div[1]/div[1]/div/ul/li[3]/span[1]");
		this.indexClosed.put("ytdChangePercent", "/html/body/div[3]/div[6]/div[1]/div[2]/div[1]/table/tbody/tr[5]/td[2]/ul/li[1]");
		this.indexClosed.put("oneYearChangePercent", "/html/body/div[3]/div[6]/div[1]/div[2]/div[1]/table/tbody/tr[5]/td[2]/ul/li[1]");
		this.indexClosed.put("timeStamp", "/html/body/div[3]/div[2]/div[3]/div/div[1]/span/bg-quote");

		// ---------------------------------------------------------------------------------
		// xPaths: Indexes with market OPEN
		// ---------------------------------------------------------------------------------
		this.indexOpen.put("latestPrice", "/html/body/div[3]/div[2]/div[3]/div/div[2]/h2/bg-quote");
		this.indexOpen.put("change", "/html/body/div[3]/div[2]/div[3]/div/div[2]/bg-quote/span[1]/bg-quote");
		this.indexOpen.put("changePercent", "/html/body/div[3]/div[2]/div[3]/div/div[2]/bg-quote/span[2]/bg-quote");
		this.indexOpen.put("52weekRange", "/html/body/div[3]/div[6]/div[1]/div[1]/div/ul/li[3]/span[1]");
		this.indexOpen.put("ytdChangePercent", "/html/body/div[3]/div[6]/div[1]/div[2]/div/table/tbody/tr[4]/td[2]/ul/li[1]");
		this.indexOpen.put("oneYearChangePercent", "/html/body/div[3]/div[6]/div[1]/div[2]/div[1]/table/tbody/tr[5]/td[2]/ul/li[1]");
		this.indexOpen.put("timeStamp", "/html/body/div[3]/div[2]/div[3]/div/div[1]/span/bg-quote");

		this.indexOpen.put("marketStatus", "/html/body/div[3]/div[2]/div[3]/div/small/div");

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
}

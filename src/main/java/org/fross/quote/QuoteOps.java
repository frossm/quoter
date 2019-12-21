/**************************************************************************************************************
 * Quote.jar
 * Copyright 2019 Michael Fross, all rights reserved
 * 
 * Quote is a command line program that display stock quotes and index data.
 * 
 * License:  
 *  MIT License / https://opensource.org/licenses/MIT
 *  Please see included LICENSE.txt file for additional details
 *   
 ***************************************************************************************************************/

package org.fross.quote;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import org.fross.library.Output;
import org.fusesource.jansi.Ansi;
import org.fross.library.Debug;

public class QuoteOps {

	/**
	 * GetQuote: Get a stock quote from IEXCloud.io and return an array of key data:
	 * 0 = symbol; 1 = latest realtime price; 2 = change; 3 = changePercent; 4 =
	 * dayhigh; 5 = daylow; 6 = year to date change percentage;
	 * 
	 * @param symb
	 * @param Token
	 * @return
	 */
	public static String[] GetQuote(String symb, String token) {
		String QUOTEURLTEMPLATE = "https://cloud.iexapis.com/stable/stock/SYMBOLHERE/quote?token=TOKENHERE";
		String quoteURL = "";
		String quoteDetail = "";
		String[] retArray = new String[9];

		// Get the quote data in JSON format
		Output.debugPrint("Processing Symbol: '" + symb + "'");
		quoteURL = QUOTEURLTEMPLATE.replaceAll("SYMBOLHERE", symb);
		quoteURL = quoteURL.replaceAll("TOKENHERE", token);
		Output.debugPrint("Rewritten URL: " + quoteURL);

		try {
			quoteDetail = URLOps.ReadURL(quoteURL);
		} catch (Exception ex) {
			String[] errorReturn = { symb, "Error", "Retrieving", "Quote", "", "", "", "", "" };
			return errorReturn;
		}

		// Display the returned JSON data
		Output.debugPrint("\nRaw Data from REST API call:\n" + quoteDetail + "\n");

		// Decode the JSON and extract the desired data
		try {
			JSONParser jp = new JSONParser();
			Object obj = jp.parse(quoteDetail);
			JSONObject jo = (JSONObject) obj;

			// Symbol
			try {
				retArray[0] = jo.get("symbol").toString();
			} catch (NullPointerException Ex) {
				retArray[0] = "-";
			}

			// LatestPrice
			try {
				retArray[1] = jo.get("latestPrice").toString();
			} catch (NullPointerException Ex) {
				retArray[1] = "-";
			}

			// Change
			try {
				retArray[2] = jo.get("change").toString();
			} catch (NullPointerException Ex) {
				retArray[2] = "-";
			}

			// ChangePercent
			try {
				retArray[3] = jo.get("changePercent").toString();
			} catch (NullPointerException Ex) {
				retArray[3] = "-";
			}

			// High
			try {
				retArray[4] = jo.get("high").toString();
			} catch (NullPointerException Ex) {
				retArray[4] = "-";
			}

			// Low
			try {
				retArray[5] = jo.get("low").toString();
			} catch (NullPointerException Ex) {
				retArray[5] = "-";
			}

			// 52 Week High
			try {
				retArray[6] = jo.get("week52High").toString();
			} catch (NullPointerException Ex) {
				retArray[6] = "-";
			}

			// 52 Week Low
			try {
				retArray[7] = jo.get("week52Low").toString();
			} catch (NullPointerException Ex) {
				retArray[7] = "-";
			}

			// YTD Change
			try {
				retArray[8] = jo.get("ytdChange").toString();
			} catch (NullPointerException Ex) {
				retArray[8] = "-";
			}

			// If we are in debug mode, display the values we are returning
			if (Debug.query() == true) {
				Output.debugPrint("Data Returned from Web:");
				for (int i = 0; i < retArray.length; i++) {
					Output.debugPrint("    " + i + ": " + retArray[i]);
				}
			}

		} catch (Exception ex) {
			Output.printColorln(Ansi.Color.RED, "Error parsing JSON from IEX Cloud:\n" + ex.getMessage());
		}

		return retArray;
	}

	/**
	 * GetIndex: Returns an array of Strings that contains the Dow, Nasdaq, and S&P
	 * data. Unfortunately I have to scrape a web page for this information as IEX
	 * Cloud does not contain index data.
	 * 
	 * @param idx
	 * @return
	 */
	public static String[] GetIndex(String idx) {
		String[] retArray = new String[4];
		String idxPage;
		String URLTEMPLATE = "https://www.cnbc.com/quotes/?symbol=SYMBOLHERE";
		String URL = "ERROR";

		// Ensure a valid value was passed
		if (idx.toUpperCase() == "DOW") {
			URL = URLTEMPLATE.replaceAll("SYMBOLHERE", ".dji");
		} else if (idx.toUpperCase() == "NASDAQ") {
			URL = URLTEMPLATE.replaceAll("SYMBOLHERE", ".ixic");
		} else if (idx.toUpperCase() == "S&P") {
			URL = URLTEMPLATE.replaceAll("SYMBOLHERE", ".inx");
		} else {
			Output.fatalError("Call to GetIndex must be 'DOW', 'NASDAQ', or 'S&P'", 4);
		}

		Output.debugPrint("Index URL rewritten to: " + URL);

		try {
			// Download the web page with
			idxPage = URLOps.ReadURL(URL);

			retArray[0] = idx;
			retArray[1] = StringUtils.substringBetween(idxPage, "\"last\":\"", "\"");
			retArray[2] = StringUtils.substringBetween(idxPage, "\"change\":\"", "\"");
			retArray[3] = StringUtils.substringBetween(idxPage, "\"change_pct\":\"", "\"");

			// If we are in debug mode, display the values we are returning
			if (Debug.query() == true) {
				Output.debugPrint("Index Data Returned from Web:");
				for (int i = 0; i < retArray.length; i++) {
					Output.debugPrint("    " + i + ": " + retArray[i]);
				}
			}

		} catch (Exception ex) {
			Output.printColorln(Ansi.Color.RED, "Unable to get Index data for " + idx + "\n" + ex.getMessage());
		}

		return retArray;
	}

}

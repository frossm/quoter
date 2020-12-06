/**************************************************************************************************************
 * Quoter.jar
 * 
 * Quoter is a command line program that display stock quotes and index data.
 * 
 *  Copyright (c) 2019 Michael Fross
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fross.library.Debug;
import org.fross.library.Output;
import org.fusesource.jansi.Ansi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Symbol {
	HashMap<String, String> symbolData = new HashMap<String, String>();

	/**
	 * Symbol Constructor(): Initialize class with a symbol to process
	 * 
	 * @param symb
	 */
	public Symbol(String symb, String token) {
		this.symbolData = getQuote(symb, token);
	}

	/**
	 * query(): Returns security
	 * 
	 * @param field
	 * @return
	 */
	protected String query(String field) {
		try {
			return this.symbolData.get(field);
		} catch (Exception ex) {
			Output.fatalError("Could not query '" + field + "' field in security data", 2);
			return "error";
		}

	}

	/**
	 * queryAllFieldNames(): Return an array of all of the keys in the HashMap
	 * 
	 * @return
	 */
	protected List<String> queryAllFieldNames() {
		List<String> returnList = new ArrayList<String>();

		for (String i : this.symbolData.keySet()) {
			returnList.add(i);
		}

		return returnList;
	}

	/**
	 * EpochTime2String(): Take a Long number as a time epoch and return a human readable string
	 * 
	 * @param epochTime
	 * @return
	 */
	protected static String epochTime2String(Long epochTime) {
		String returnString;

		// Convert Epoch to Simple Date String
		try {
			Date d = new Date(epochTime);
			DateFormat dFormat = new SimpleDateFormat("EEEEE MMMMM dd yyyy hh:mma z");
			returnString = dFormat.format(d);
		} catch (NullPointerException Ex) {
			throw new NullPointerException();
		}

		return (returnString);
	}

	/**
	 * getQuote: Get a stock quote from IEXCloud.io and return an array of key data
	 * 
	 * @param symb
	 * @param Token
	 * @return
	 */
	private static HashMap<String, String> getQuote(String symb, String token) {
		String QUOTEURLTEMPLATE = "https://cloud.iexapis.com/stable/stock/SYMBOLHERE/quote?token=TOKENHERE";
		String quoteURL = "";
		String quoteDetail = "";
		HashMap<String, String> returnData = new HashMap<String, String>();

		// Set the default status value to 'ok'. Will update to 'error' if there are issues
		returnData.put("status", "ok");

		// Rewrite the template URL with the provided values
		Output.debugPrint("Processing Symbol: '" + symb + "'");
		quoteURL = QUOTEURLTEMPLATE.replaceAll("SYMBOLHERE", symb);
		quoteURL = quoteURL.replaceAll("TOKENHERE", token);
		Output.debugPrint("Rewritten URL: " + quoteURL);

		// Query IEXCloud's REST API and get the security information in JSON format
		try {
			quoteDetail = URLOps.ReadURL(quoteURL);
		} catch (Exception ex) {
			returnData.put("symbol", symb);
			returnData.put("status", "Error");
			return returnData;
		}
		Output.debugPrint("\nRaw Data from REST API call:\n" + quoteDetail + "\n");

		// Decode the JSON and extract the desired data
		try {
			GsonBuilder builder = new GsonBuilder();
			Gson gson = builder.create();

			// In Gson, convert the JSON into a map
			@SuppressWarnings("unchecked")
			Map<String, Object> gsonMap = gson.fromJson(quoteDetail, Map.class);

			// Loop through the <String,Object> map and convert it to a <String, String> hashmap
			for (Map.Entry<String, Object> i : gsonMap.entrySet()) {
				String key = i.getKey();
				try {
					returnData.put(key, gsonMap.get(key).toString());
				} catch (NullPointerException ex) {
					returnData.put(key, "-");
				}
			}

			// Convert latest date to a readable string and replace the value in the map
			try {
				String dateString = String.format("%.0f", Double.parseDouble(returnData.get("latestUpdate")));
				dateString = epochTime2String(Long.parseLong(dateString));
				returnData.put("latestUpdate", dateString);

			} catch (NullPointerException Ex) {
				returnData.put("latestUpdate", "-");
			}

			// If we are in debug mode, display the values we are returning
			if (Debug.query() == true) {
				Output.debugPrint("Data Returned from Web:");
				for (Map.Entry<String, String> i : returnData.entrySet()) {
					String key = i.getKey();
					Output.debugPrint("    " + key + ": " + returnData.get(key));
				}
			}

		} catch (Exception ex) {
			Output.printColorln(Ansi.Color.RED, "Error parsing JSON from IEX Cloud:\n" + ex.getMessage());
			returnData.put("status", "Error");
		}

		return returnData;
	}
}
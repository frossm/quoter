/**************************************************************************************************************
 * Quoter.jar
 * 
 * Quoter is a command line program that display stock quotes and index data.
 * 
 *  Copyright (c) 2019-2021 Michael Fross
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
import org.fross.library.Format;
import org.fross.library.Output;
import org.fross.library.URLOperations;
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
	 * get(): Returns security detail based on passed field
	 * 
	 * @param field
	 * @return
	 */
	protected String get(String field) {
		try {
			return this.symbolData.get(field);
		} catch (Exception ex) {
			Output.fatalError("Could not query '" + field + "' field in security data", 2);
			return "error";
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
	 * epochTime2String(): Take a string and return a human readable date string
	 * 
	 * @param epochTime
	 * @return
	 */
	protected static String epochTime2String(String epochTime) {
		String result = String.format("%.0f", Double.parseDouble(epochTime));
		return (epochTime2String(Long.parseLong(result)));
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
			quoteDetail = URLOperations.ReadURL(quoteURL);

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

			// Process the time fields to be human readable
			try {
				if (returnData.get("latestUpdate") != "-")
					returnData.put("latestUpdate", Symbol.epochTime2String(returnData.get("latestUpdate")));
				if (returnData.get("openTime") != "-")
					returnData.put("openTime", Symbol.epochTime2String(returnData.get("openTime")));
				if (returnData.get("closeTime") != "-")
					returnData.put("closeTime", Symbol.epochTime2String(returnData.get("closeTime")));
				if (returnData.get("highTime") != "-")
					returnData.put("highTime", Symbol.epochTime2String(returnData.get("highTime")));
				if (returnData.get("lowTime") != "-")
					returnData.put("lowTime", Symbol.epochTime2String(returnData.get("lowTime")));
			} catch (Exception Ex) {
				// Leave them as dashes if there is an error
			}

			// Format Market Cap, volume, and previous volume into a more easily read string
			if (returnData.get("marketCap") != "-")
				returnData.put("marketCap", Format.Comma(Double.valueOf(returnData.get("marketCap")).longValue()));
			if (returnData.get("latestVolume") != "-")
				returnData.put("latestVolume", Format.Comma(Double.valueOf(returnData.get("latestVolume")).longValue()));
			if (returnData.get("previousVolume") != "-")
				returnData.put("previousVolume", Format.Comma(Double.valueOf(returnData.get("previousVolume")).longValue()));

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
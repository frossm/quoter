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
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fross.library.Debug;
import org.fross.library.Output;
import org.fusesource.jansi.Ansi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class QuoteOps {

	/**
	 * GetQuote: Get a stock quote from IEXCloud.io and return an array of key data
	 * 
	 * @param symb
	 * @param Token
	 * @return
	 */
	public static String[] GetQuote(String symb, String token) {
		String QUOTEURLTEMPLATE = "https://cloud.iexapis.com/stable/stock/SYMBOLHERE/quote?token=TOKENHERE";
		String[] JSONFields = { "symbol", "latestPrice", "change", "changePercent", "high", "low", "week52High", "week52Low", "ytdChange", "latestUpdate" };
		String quoteURL = "";
		String quoteDetail = "";
		String[] retArray = new String[10];

		// Rewrite the template URL with the provided values
		Output.debugPrint("Processing Symbol: '" + symb + "'");
		quoteURL = QUOTEURLTEMPLATE.replaceAll("SYMBOLHERE", symb);
		quoteURL = quoteURL.replaceAll("TOKENHERE", token);
		Output.debugPrint("Rewritten URL: " + quoteURL);

		// Query IEXCloud's REST API and get the security information in JSON format
		try {
			quoteDetail = URLOps.ReadURL(quoteURL);
		} catch (Exception ex) {
			String[] errorReturn = { symb, "Error", "Retrieving", "Quote", "", "", "", "", "" };
			return errorReturn;
		}
		Output.debugPrint("\nRaw Data from REST API call:\n" + quoteDetail + "\n");

		// Decode the JSON and extract the desired data
		try {
			GsonBuilder builder = new GsonBuilder();
			Gson gson = builder.create();

			// In Gson, convert the JSON into a map
			@SuppressWarnings("unchecked")
			Map<String, Object> gsonMap = gson.fromJson(quoteDetail, Map.class);

			// Loop through the returned JSON and map the fields to the return string array
			for (int i = 0; i < JSONFields.length; i++) {
				try {
					retArray[i] = gsonMap.get(JSONFields[i].toString()).toString();
				} catch (NullPointerException ex) {
					retArray[i] = "-";
				}
			}

			// Convert latest date to a readable string
			try {
				String dateString = String.format("%.0f", Double.parseDouble(gsonMap.get("latestUpdate").toString()));
				retArray[9] = EpochTime2String(Long.parseLong(dateString));

			} catch (NullPointerException Ex) {
				retArray[9] = "-";
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
	 * GetIndex: Returns an array of Strings that contains the Dow, Nasdaq, and S&P data. Unfortunately
	 * I have to scrape a web page for this information as IEX Cloud does not contain index data.
	 * 
	 * @param idx
	 * @return
	 */
	public static String[] GetIndex(String idx) {
		String[] retArray = new String[4];
		String idxPage;
		String URLTEMPLATE = "https://www.cnbc.com/quotes/?symbol=SYMBOLHERE";
		String URL = "ERROR";
		String[] searchPatterns = new String[4];

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

			// Define the regex patterns to look for in the URL provided above
			searchPatterns[1] = "\"last\":\"(.*?)\"";
			searchPatterns[2] = "\"change\":\"(.*?)\"";
			searchPatterns[3] = "\"change_pct\":\"(.*?)\"";

			retArray[0] = idx;
			for (int i = 1; i < searchPatterns.length; i++) {
				Pattern pat = Pattern.compile(searchPatterns[i]);
				Matcher m = pat.matcher(idxPage);
				if (m.find()) {
					retArray[i] = m.group(1).trim();
				}
			}

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

	/**
	 * EpochTime2String(): Take a Long number as a time epoch and return a human readable string
	 * 
	 * @param epochTime
	 * @return
	 */
	public static String EpochTime2String(Long epochTime) {
		String returnString;

		// Convert Epoch to Simple Date String
		try {
			Date d = new Date(epochTime);
			DateFormat dFormat = new SimpleDateFormat("EEEEE MMMMM dd, yyyy hh:mma");
			returnString = dFormat.format(d);
		} catch (NullPointerException Ex) {
			throw new NullPointerException();
		}

		return (returnString);
	}

	/**
	 * GetHistorical3M(): Return map of date/closePrice for 3 months
	 * 
	 * @return
	 */
	public static Map<String, Float> GetHistorical3M(String symb, String token) {
		String QUOTEURLTEMPLATE = "https://cloud.iexapis.com/stable/stock/SYMBOLHERE/chart/3m?token=TOKENHERE";
		// String QUOTEURLTEMPLATE =
		// "https://sandbox.iexapis.com/stable/stock/SYMBOLHERE/chart/3m?token=TOKENHERE";
		String quoteURL = "";
		String rawChartData = "";
		Map<String, Float> resultMap = new TreeMap<String, Float>();	// TreeMaps are sorted

		// Rewrite the template URL with the provided values
		Output.debugPrint("Processing Trending for Symbol: '" + symb + "'");
		quoteURL = QUOTEURLTEMPLATE.replaceAll("SYMBOLHERE", symb);
		quoteURL = quoteURL.replaceAll("TOKENHERE", token);
		Output.debugPrint("Rewritten Trending URL: " + quoteURL);

		// Query IEXCloud's REST API and get the historical security information in JSON format
		try {
			rawChartData = URLOps.ReadURL(quoteURL);
		} catch (Exception ex) {
			Output.fatalError("Could not query historical data from IEXCloud", 3);
		}

		// Remove any square brackets and the trailing comma
		rawChartData = rawChartData.replace("[", "");
		rawChartData = rawChartData.replace("]", "");

		// Break JSON into an array of days
		String[] rawChartArray = rawChartData.split("\\{");

		// Add the '{' back into each array and remove the trailing comma to keep JSON legal
		for (int i = 0; i < rawChartArray.length; i++) {
			rawChartArray[i] = "{" + rawChartArray[i];
			rawChartArray[i] = rawChartArray[i].replace("},", "}");
		}

		// Convert the raw JSON data into the map to return
		try {
			GsonBuilder builder = new GsonBuilder();
			Gson gson = builder.create();

			// Iterate through the list and populate the return map
			for (int i = 1; i < rawChartArray.length; i++) {
				// In Gson, convert the JSON into a map
				@SuppressWarnings("unchecked")
				Map<String, Object> gsonMap = gson.fromJson(rawChartArray[i], Map.class);
				resultMap.put(gsonMap.get("date").toString(), Float.parseFloat(gsonMap.get("close").toString()));
			}

		} catch (Exception ex) {
			Output.printColorln(Ansi.Color.RED, "Error parsing JSON from IEX Cloud:\n" + ex.getMessage());
		}

		return resultMap;
	}

} // END CLASS

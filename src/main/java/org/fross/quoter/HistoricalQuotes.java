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

import java.util.Map;
import java.util.TreeMap;

import org.fross.library.Output;
import org.fross.library.URLOperations;
import org.fusesource.jansi.Ansi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class HistoricalQuotes {

	/**
	 * GetHistorical3M(): Return map of date/closePrice for 3 months
	 * 
	 * In the value portion of the returned map: [0]=close price [1]=day high price [2]=day low price
	 * 
	 * @return
	 */
	public static Map<String, Float[]> getHistoricalQuotes(String symb, String token) {
		String QUOTEURLTEMPLATE = Main.IEXCloudBaseURL + "/stable/stock/SYMBOLHERE/chart/3m?token=TOKENHERE";
		String quoteURL = "";
		String rawChartData = "";

		// Key is a date string
		// Value is an array: [0]=close [1]=day high [2]=day low
		Map<String, Float[]> resultMap = new TreeMap<String, Float[]>();

		// Rewrite the template URL with the provided values
		Output.debugPrint("Processing Trending for Symbol: '" + symb + "'");
		quoteURL = QUOTEURLTEMPLATE.replaceAll("SYMBOLHERE", symb);
		quoteURL = quoteURL.replaceAll("TOKENHERE", token);
		Output.debugPrint("Rewritten Trending URL: " + quoteURL);

		// Query IEXCloud's REST API and get the historical security information in JSON format
		try {
			rawChartData = URLOperations.ReadURL(quoteURL);
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

				// Populate the array (which becomes resultMap value) with data fields from IEXCloud
				Float tempArray[] = new Float[3];
				tempArray[0] = Float.parseFloat(gsonMap.get("close").toString());
				tempArray[1] = Float.parseFloat(gsonMap.get("high").toString());
				tempArray[2] = Float.parseFloat(gsonMap.get("low").toString());

				// Show the array in debug mode
				Output.debugPrint(gsonMap.get("date").toString() + "\tClose:" + tempArray[0] + "\tHigh:" + tempArray[1] + "\tLow:" + tempArray[2]);

				resultMap.put(gsonMap.get("date").toString(), tempArray);
			}

		} catch (Exception ex) {
			Output.printColorln(Ansi.Color.RED, "Error parsing JSON from IEX Cloud:\n" + ex.getMessage());
		}

		return resultMap;
	}

	/**
	 * LargestMapValue(): Return largest daily high value of the provided map
	 * 
	 * @param map
	 */
	public static Float largestMapValue(Map<String, Float[]> map) {
		Float largestValue = Float.MIN_VALUE;

		for (Map.Entry<String, Float[]> i : map.entrySet()) {
			String key = i.getKey();
			if (map.get(key)[1] > largestValue)
				largestValue = map.get(key)[1];
		}
		return largestValue;

	}

	/**
	 * SmallestMapValue(): Return smallest daily lowvalue of the provided map
	 * 
	 * @param map
	 */
	public static Float smallestMapValue(Map<String, Float[]> map) {
		Float smallestValue = Float.MAX_VALUE;

		for (Map.Entry<String, Float[]> i : map.entrySet()) {
			String key = i.getKey();
			if (map.get(key)[2] < smallestValue)
				smallestValue = map.get(key)[2];
		}
		return smallestValue;

	}

	/**
	 * DisplayTrending(): Display three month trending data for provided stock
	 * 
	 * @param symb, token
	 */
	public static void displayTrendingMap(String symb, String token) {
		int GRAPHWIDTH = 80;
		Float slotsPerCostUnit;

		// Get the historical quotes
		Map<String, Float[]> resultTreeMap = getHistoricalQuotes(symb, token);

		// Calculate the largest and smallest security value in the historical data
		Float lv = largestMapValue(resultTreeMap);
		Float sv = smallestMapValue(resultTreeMap);
		Output.debugPrint("Largest Value in Historical Data:  " + lv);
		Output.debugPrint("Smallest Value in Historical Data: " + sv);

		// Determine how many spaces per dollar
		slotsPerCostUnit = GRAPHWIDTH / (lv - sv);
		Output.debugPrint("Map Slots: " + GRAPHWIDTH);
		Output.debugPrint("Slots per Cost Unit: " + slotsPerCostUnit);

		// Create a symbol object with IEXCloud Data
		Symbol symbolData = new Symbol(symb, token);

		// Display the symbol informational header
		Output.printColorln(Ansi.Color.WHITE, "\n\n+--Three Month Trend" + "-".repeat(GRAPHWIDTH - 7) + "+");
		Output.printColorln(Ansi.Color.YELLOW, symb.toUpperCase() + " / " + symbolData.get("companyName"));
		Output.printColorln(Ansi.Color.YELLOW, "Exchange:    " + symbolData.get("primaryExchange"));
		Output.printColorln(Ansi.Color.YELLOW, "PE Ratio:    " + symbolData.get("peRatio"));
		Output.printColorln(Ansi.Color.YELLOW, "Market Cap:  " + symbolData.get("marketCap"));
		Output.printColorln(Ansi.Color.WHITE, "+" + "-".repeat(GRAPHWIDTH + 12) + "+\n");

		// Display trending title bar
		// Output.printColorln(Ansi.Color.CYAN, " ".repeat(12) + sv + " ".repeat(GRAPHWIDTH -
		// sv.toString().length() - lv.toString().length() + 1) + lv);
		String midNumber = String.format("%.2f", ((sv + lv) / 2));
		int titleSpaces1 = (GRAPHWIDTH / 2) - sv.toString().length() - (int) midNumber.length() / 2;
		int titleSpaces2 = GRAPHWIDTH - sv.toString().length() - titleSpaces1 - lv.toString().length() - lv.toString().length();

		Output.printColorln(Ansi.Color.CYAN, " ".repeat(12) + sv + " ".repeat(titleSpaces1) + midNumber + " ".repeat(titleSpaces2) + lv);
		Output.printColor(Ansi.Color.CYAN, " ".repeat(11) + "+" + "-".repeat(GRAPHWIDTH / 2) + "+" + "-".repeat(GRAPHWIDTH / 2) + "+");
		Output.printColorln(Ansi.Color.CYAN, "  Low\tClose\tHigh");

		// Loop through the sorted data and display the graph
		for (Map.Entry<String, Float[]> i : resultTreeMap.entrySet()) {
			String date = i.getKey();
			Float close = resultTreeMap.get(date)[0];
			Float dailyHigh = resultTreeMap.get(date)[1];
			Float dailyLow = resultTreeMap.get(date)[2];

			// Calculate the number of spaces (slots) until we get to daily low value
			int numInitialSpaces = (int) ((dailyLow - sv) * slotsPerCostUnit);

			// Calculate number of dashes from low to close
			int numLowSpaces = (int) ((close - dailyLow) * slotsPerCostUnit);

			// Calculate number of dashes from close to high
			int numHighSpaces = (int) ((dailyHigh - close) * slotsPerCostUnit);

			// Calculate number of dashes at the end
			int numFinalSpaces = (GRAPHWIDTH - numInitialSpaces - numLowSpaces - numHighSpaces);

			try {
				Output.printColor(Ansi.Color.CYAN, date + " |");
				Output.print(" ".repeat(numInitialSpaces));
				Output.printColor(Ansi.Color.WHITE, "-".repeat(numLowSpaces));
				Output.printColor(Ansi.Color.YELLOW, "o");
				Output.printColor(Ansi.Color.WHITE, "-".repeat(numHighSpaces));
				Output.print(" ".repeat(numFinalSpaces));
				Output.printColorln(Ansi.Color.CYAN,
						"| " + String.format("%7.2f", dailyLow) + " " + String.format("%7.2f", close) + " " + String.format("%7.2f", dailyHigh));
			} catch (IllegalArgumentException ex) {
				System.out.println("**ERROR**");
			}
		}

		// Footer
		Output.printColor(Ansi.Color.CYAN, " ".repeat(11) + "+" + "-".repeat(GRAPHWIDTH / 2) + "+" + "-".repeat(GRAPHWIDTH / 2) + "+\n\n");
	}

}

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

import java.util.Map;
import java.util.TreeMap;

import org.fross.library.Output;
import org.fusesource.jansi.Ansi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class HistoricalQuotes {

	/**
	 * GetHistorical3M(): Return map of date/closePrice for 3 months
	 * 
	 * @return
	 */
	public static Map<String, Float> getHistoricalQuotes(String symb, String token) {
		String QUOTEURLTEMPLATE = "https://cloud.iexapis.com/stable/stock/SYMBOLHERE/chart/3m?token=TOKENHERE";
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

	/**
	 * LargestValue(): Return largest float value of the map
	 * 
	 * @param map
	 */
	public static Float largestValue(Map<String, Float> map) {
		Float lv = 0f;

		for (Map.Entry<String, Float> i : map.entrySet()) {
			String key = i.getKey();
			if (map.get(key) > lv)
				lv = map.get(key);
		}
		return lv;

	}

	/**
	 * SmallestValue(): Return smallest float value of the map
	 * 
	 * @param map
	 */
	public static Float smallestValue(Map<String, Float> map) {
		Float sv = Float.MAX_VALUE;

		for (Map.Entry<String, Float> i : map.entrySet()) {
			String key = i.getKey();
			if (map.get(key) < sv)
				sv = map.get(key);
		}
		return sv;

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
		Map<String, Float> resultTreeMap = getHistoricalQuotes(symb, token);

		// Calculate the largest and smallest security value in the historical data
		Float lv = largestValue(resultTreeMap);
		Float sv = smallestValue(resultTreeMap);
		Output.debugPrint("Largest Value in Historical Data:  " + lv);
		Output.debugPrint("Smallest Value in Historical Data: " + sv);

		// Determine how many spaces per dollar
		slotsPerCostUnit = GRAPHWIDTH / (lv - sv);
		Output.debugPrint("Map Slots: " + GRAPHWIDTH);
		Output.debugPrint("Slots per Cost Unit: " + slotsPerCostUnit);

		// Display the header
		Output.printColorln(Ansi.Color.YELLOW, "\nSecurity: " + symb.toUpperCase());
		Output.printColorln(Ansi.Color.CYAN, " ".repeat(12) + sv + " ".repeat(GRAPHWIDTH - sv.toString().length() - lv.toString().length() + 1) + lv);
		Output.printColorln(Ansi.Color.CYAN, " ".repeat(11) + "+" + "-".repeat(GRAPHWIDTH + 1) + "+");

		// Loop through the sorted data and display the graph
		for (Map.Entry<String, Float> i : resultTreeMap.entrySet()) {
			String date = i.getKey();
			Float value = resultTreeMap.get(date);
			int numSpaces = (int) ((value - sv) * slotsPerCostUnit);

			// Display the row of data
			Output.printColor(Ansi.Color.CYAN, date + " |");
			Output.printColor(Ansi.Color.YELLOW, " ".repeat(numSpaces) + "o");
			Output.printColorln(Ansi.Color.CYAN, " ".repeat(GRAPHWIDTH - numSpaces) + "| " + String.format("%.2f", value));
		}

		// Footer
		Output.printColorln(Ansi.Color.CYAN, "           +" + "-".repeat(GRAPHWIDTH + 1) + "+\n\n");
		
	} // END DISPLAYTRENDING

} // END CLASS

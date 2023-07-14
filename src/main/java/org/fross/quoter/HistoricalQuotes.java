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

import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;

import org.fross.library.Date;
import org.fross.library.Output;
import org.fross.library.URLOperations;
import org.fusesource.jansi.Ansi;

public class HistoricalQuotes {
	// Key is a date string
	// Value is an array: [0]=DayHigh [1]=DayLow [2]=DayClose
	Map<String, Float[]> resultMap = new TreeMap<String, Float[]>();

	// Number of days to go back from today in the trend
	final int NUM_DAYS_IN_TREND = Prefs.queryInt("trendduration");

	/**
	 * Constructor
	 * 
	 * @param symb
	 */
	public HistoricalQuotes(String symb) {
		this.resultMap = getHistoricalQuotes(symb);
	}

	/**
	 * GetHistoricalQuotes(): Return map of date/closePrice for 3 months
	 * 
	 * In the value portion of the returned map:
	 * 
	 * @return
	 */
	public Map<String, Float[]> getHistoricalQuotes(String symb) {
		Map<String, Float[]> map = new TreeMap<String, Float[]>();
		String quoteURL = "https://www.marketwatch.com/investing/stock/SYMBOL/downloaddatapartial?startdate=STARTDATE%2000:00:00&enddate=ENDDATE%2023:59:59&daterange=d30&frequency=p1d&csvdownload=true&downloadpartial=false&newdates=false";

		// Set the end date to today's date
		String endDate = Date.getCurrentMonth() + "/" + Date.getCurrentDay() + "/" + Date.getCurrentYear();
		Output.debugPrintln("Trending End Date set to: " + endDate);

		// Set the start date to today minus NUM_DAYS_IN_TREND
		LocalDateTime now = LocalDateTime.now();
		now = now.minusDays(NUM_DAYS_IN_TREND);
		String startDate = String.format("%02d", now.getMonthValue()) + "/" + now.getDayOfMonth() + "/" + now.getYear();
		Output.debugPrintln("Trending Start Date set to: " + startDate);

		// Add the symbol, start, and end dates to the URL
		quoteURL = quoteURL.replaceAll("SYMBOL", symb);
		quoteURL = quoteURL.replaceAll("STARTDATE", startDate);
		quoteURL = quoteURL.replaceAll("ENDDATE", endDate);
		Output.debugPrintln("Rewritten trending URL:\n" + quoteURL);

		// Download the historical data. Fields: Date,Open,High,Low,Close,Volume
		String historicalDataBlob = "";
		try {
			historicalDataBlob = URLOperations.ReadURL(quoteURL);

		} catch (Exception ex) {
			Output.printColorln(Ansi.Color.RED, "Could not read historical data for '" + symb + "'");
		}

		// Split each row (day) into an array
		String[] historicalData = historicalDataBlob.split("\n");

		// Loop through each day and populate the hash. Data order: Date,Open,High,Low,Close,Volume
		for (int rowRead = (historicalData.length - 1); rowRead > 0; rowRead--) {
			// The date is not quoted. Easier to quote the data so the following split works
			historicalData[rowRead] = historicalData[rowRead].replaceFirst("^", "\"");
			historicalData[rowRead] = historicalData[rowRead].replaceFirst(",", "\",");

			// Pull the first field (date) and set as the hash key
			String dateKey = historicalData[rowRead].split(",")[0].replaceAll("\"", "");

			// Split the fields of the line read by commas into an array
			String lineRead[] = historicalData[rowRead].split("\",\"");

			Float[] temp = new Float[3];
			temp[0] = Float.valueOf(lineRead[2].replace("[,\"]", ""));		// High
			temp[1] = Float.valueOf(lineRead[3].replace("[,\"]", ""));		// Low
			temp[2] = Float.valueOf(lineRead[4].replace("[,\"]", ""));		// Close

			// Add the historical data to the hashmap with the date as the key
			map.put(dateKey, temp);
		}

		return map;

	}

	/**
	 * get(): Return array with results. [0]=DayHigh [1]=DayLow [2]=DayClose
	 * 
	 * @param key
	 * @return
	 */
	public Float[] get(String key) {
		return resultMap.get(key);
	}

	/**
	 * getDayHigh(): Return the day's highest value on the provided date
	 * 
	 * @param key
	 * @return
	 */
	public Float getDayHigh(String key) {
		try {
			return resultMap.get(key)[0];
		} catch (Exception ex) {
			return -999f;
		}
	}

	/**
	 * getDayLow(): Return the day's lowest values on the provided date
	 * 
	 * @param key
	 * @return
	 */
	public Float getDayLow(String key) {
		try {
			return resultMap.get(key)[1];
		} catch (Exception ex) {
			return -999f;
		}
	}

	/**
	 * getDayClose(): Return the close price on the provided date
	 * 
	 * @param key
	 * @return
	 */
	public Float getDayClose(String key) {
		try {
			return resultMap.get(key)[2];
		} catch (Exception ex) {
			return -999f;
		}
	}

	/**
	 * getKeySet(): Return a set of the hash keys (dates)
	 * 
	 * @return
	 */
	public String[] getKeySet() {
		String[] keyArray = new String[resultMap.size()];
		return resultMap.keySet().toArray(keyArray);
	}

	/**
	 * LargestMapValue(): Return largest daily high value of the provided map
	 * 
	 * @param map
	 */
	public Float largestMapValue(Map<String, Float[]> map) {
		Float largestValue = Float.MIN_VALUE;

		for (Map.Entry<String, Float[]> i : map.entrySet()) {
			String key = i.getKey();
			if (this.getDayHigh(key) > largestValue)
				largestValue = this.getDayHigh(key);
		}
		return largestValue;

	}

	/**
	 * SmallestMapValue(): Return smallest daily low value of the provided map
	 * 
	 * @param map
	 */
	public Float smallestMapValue(Map<String, Float[]> map) {
		Float smallestValue = Float.MAX_VALUE;

		for (Map.Entry<String, Float[]> i : map.entrySet()) {
			String key = i.getKey();
			if (this.getDayLow(key) < smallestValue)
				smallestValue = this.getDayLow(key);
		}
		return smallestValue;

	}

	/**
	 * DisplayTrending(): Display three month trending data for provided stock
	 * 
	 * @param symb, token
	 */
	public void displayTrend(String symb) {
		int graphWidth;
		Float slotsPerCostUnit;
		int lengthOfCurrentPrice;

		// Calculate the largest value and smallest value for the security in the historical data
		Float lv = largestMapValue(resultMap);
		String lvStr = String.format("%.2f", lv);		// lv String to 2 decimals
		Float sv = smallestMapValue(resultMap);
		String svStr = String.format("%.2f", sv);		// sv String to 2 decimals

		Output.debugPrintln("Largest Value in Historical Data:  " + lv);
		Output.debugPrintln("Smallest Value in Historical Data: " + sv);

		// Create a symbol object with IEXCloud Data
		Symbol symbolData = new Symbol(symb);

		// Determine the output width. GraphWidth is TotalWidth - DateWidth - dailyLow/Close/High
		lengthOfCurrentPrice = symbolData.get("latestPrice").toString().length();
		graphWidth = Main.cli.clWidth - 10 - (lengthOfCurrentPrice * 3 + 10);

		Output.debugPrintln("Trending Graph Width set to: " + graphWidth);

		// Determine how many spaces per dollar
		slotsPerCostUnit = graphWidth / (lv - sv);
		Output.debugPrintln("Map Slots: " + graphWidth);
		Output.debugPrintln("Slots per Cost Unit: " + slotsPerCostUnit);

		// Display the symbol informational header
		Output.printColorln(Ansi.Color.WHITE, "\n\n+--" + String.format("%02d", NUM_DAYS_IN_TREND) + " Day Trend"
				+ "-".repeat(graphWidth - 2) + "+");
		Output.printColorln(Ansi.Color.YELLOW, symb.toUpperCase() + " : " + symbolData.get("fullname"));
		Output.printColorln(Ansi.Color.YELLOW, "Current Price:   " + symbolData.get("latestPrice"));
		Output.printColorln(Ansi.Color.YELLOW, NUM_DAYS_IN_TREND + " Day Low:     " + String.format("%,.2f", sv));
		Output.printColorln(Ansi.Color.YELLOW, NUM_DAYS_IN_TREND + " Day High:    " + String.format("%,.2f", lv));
		Output.printColorln(Ansi.Color.WHITE, "+" + "-".repeat(graphWidth + 12) + "+\n");

		// Display trending title bar
		String midNumber = String.format("%.2f", ((sv + lv) / 2));
		int titleSpaces1 = (graphWidth / 2) - svStr.length() - ((int) midNumber.length() / 2);
		int titleSpaces2 = graphWidth - svStr.length() - titleSpaces1 - lvStr.length() - lvStr.length();

		Output.printColorln(Ansi.Color.WHITE, " ".repeat(12) + svStr + " ".repeat(titleSpaces1) + midNumber + " ".repeat(titleSpaces2) + lvStr);
		Output.printColor(Ansi.Color.CYAN, " ".repeat(11) + "+" + "-".repeat(graphWidth / 2) + "+" + "-".repeat(graphWidth / 2) + "+");
		Output.printColorln(Ansi.Color.WHITE, "  Low" + " ".repeat(lengthOfCurrentPrice - 1) + "Close" + " ".repeat(lengthOfCurrentPrice - 3) + "High");

		// Loop through the sorted data and display the graph
		for (Map.Entry<String, Float[]> i : resultMap.entrySet()) {
			String date = i.getKey();
			Float close = this.getDayClose(date);
			Float dailyHigh = this.getDayHigh(date);
			Float dailyLow = this.getDayLow(date);

			// Calculate the number of spaces (slots) until we get to daily low value
			int numInitialSpaces = (int) ((dailyLow - sv) * slotsPerCostUnit);

			// Calculate number of dashes from low to close
			int numLowSpaces = (int) ((close - dailyLow) * slotsPerCostUnit);

			// Calculate number of dashes from close to high
			int numHighSpaces = (int) ((dailyHigh - close) * slotsPerCostUnit);

			// Calculate number of spaces at the end
			int numFinalSpaces = graphWidth - numInitialSpaces - numLowSpaces - numHighSpaces;

			try {
				Output.printColor(Ansi.Color.CYAN, date + " |");
				Output.print(" ".repeat(numInitialSpaces));
				Output.printColor(Ansi.Color.WHITE, "-".repeat(numLowSpaces));
				Output.printColor(Ansi.Color.YELLOW, "o");
				Output.printColor(Ansi.Color.WHITE, "-".repeat(numHighSpaces));
				Output.print(" ".repeat(numFinalSpaces));
				Output.printColorln(Ansi.Color.CYAN,
						"| " + String.format("%7.2f ", dailyLow) + String.format("%7.2f ", close) + String.format("%7.2f", dailyHigh));
			} catch (IllegalArgumentException ex) {
				System.out.println("**ERROR**");
			}
		}

		// Display the Footer
		Output.printColorln(Ansi.Color.CYAN, " ".repeat(11) + "+" + "-".repeat(graphWidth / 2) + "+" + "-".repeat(graphWidth / 2) + "+");
		Output.printColorln(Ansi.Color.WHITE, " ".repeat(12) + svStr + " ".repeat(titleSpaces1) + midNumber + " ".repeat(titleSpaces2) + lvStr + "\n");

		// Show the dollars / space in the graph
		String costPerSlotStr = String.format("Note: Each space on the graph is $%.2f\n", ((lv - sv) / graphWidth));
		Output.printColorln(Ansi.Color.CYAN, " ".repeat(12 + graphWidth / 2 - costPerSlotStr.length() / 2) + costPerSlotStr);

	}
}
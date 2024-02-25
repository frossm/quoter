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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Iterator;

import org.fross.library.Output;
import org.fusesource.jansi.Ansi;

public class QuoteConsoleOutput {
	final private CommandLineParser cli;

	public QuoteConsoleOutput(final CommandLineParser cli) {
		this.cli = cli;
	}

	/**
	 * invokeSymbolOutput - Fetches and displays latest symbol output.
	 * 
	 * @param IEXCloudToken
	 * @param exporter
	 */
	public void displayOutput(FileExporter exporter) {
		// Store the time stamp
		String timeStamp = "";

		// Display the header
		Output.printColorln(Ansi.Color.YELLOW, "\nQuoter v" + Main.VERSION + " " + Main.COPYRIGHT);

		// If symbols were entered, display the header for them
		if (cli.symbolList.size() > 0) {
			Output.printColorln(Ansi.Color.CYAN, "----------------------------------------------------------------------------------------");
			Output.printColorln(Ansi.Color.CYAN, "Symbol   Current    Chng   Chng%  DayHigh   Daylow  52WHigh   52WLow      YTD%    1Year%");
			Output.printColorln(Ansi.Color.CYAN, "----------------------------------------------------------------------------------------");
		}

		// Display the data for the symbols entered. If no symbols were entered, just
		// display the index data
		if (cli.symbolList.size() > 0) {
			// Loop through each entered symbol and display it's data
			Iterator<String> j = cli.symbolList.iterator();
			String currentSymbol = "";

			while (j.hasNext()) {
				currentSymbol = j.next();
				String[] outString = new String[10];
				Symbol symbolObj = new Symbol(currentSymbol);

				// Check to see if there was an error getting symbol data
				if (symbolObj.get("status") != "ok") {
					Output.printColorln(Ansi.Color.BLUE, "'" + currentSymbol + "' is invalid");

					// Remove this invalid symbol from the list and continue to the next iteration
					j.remove();
					continue;
				}

				// Format the Output into an array
				try {
					// Symbol
					outString[0] = String.format("%-8s", symbolObj.get("symbol"));

					// Current
					try {
						outString[1] = String.format("%,8.2f", Float.valueOf(symbolObj.get("latestPrice")));
					} catch (NumberFormatException Ex) {
						outString[1] = String.format("%8s", "-");
					}

					// Change Amount
					try {
						outString[2] = String.format("%+,8.2f", Float.valueOf(symbolObj.get("change")));
					} catch (NumberFormatException Ex) {
						outString[2] = String.format("%8s", "-");
					}

					// Change Percentage
					try {
						outString[3] = String.format("%+,7.2f%%", Float.valueOf(symbolObj.get("changePercent")));
					} catch (NumberFormatException Ex) {
						outString[3] = String.format("%8s", "-");
					}

					// Day High
					try {
						outString[4] = String.format("%,9.2f", Float.valueOf(symbolObj.get("dayHigh")));
					} catch (NumberFormatException Ex) {
						outString[4] = String.format("%9s", "-");
					}

					// Day Low
					try {
						outString[5] = String.format("%,9.2f", Float.valueOf(symbolObj.get("dayLow")));
					} catch (NumberFormatException Ex) {
						outString[5] = String.format("%9s", "-");
					}

					// 52 Week High
					try {
						outString[6] = String.format("%,9.2f", Float.valueOf(symbolObj.get("week52High")));
					} catch (NumberFormatException Ex) {
						outString[6] = String.format("%9s", "-");
					}

					// 52 Week Low
					try {
						outString[7] = String.format("%,9.2f", Float.valueOf(symbolObj.get("week52Low")));
					} catch (NumberFormatException Ex) {
						outString[7] = String.format("%9s", "-");
					}

					// Year to date
					try {
						outString[8] = String.format("%+9.2f%%", Float.valueOf(symbolObj.get("ytdChangePercent")));
					} catch (NumberFormatException Ex) {
						outString[8] = String.format("%9s", "-");
					}

					// Year to date
					try {
						outString[9] = String.format("%+9.2f%%", Float.valueOf(symbolObj.get("oneYearChangePercent")));
					} catch (NumberFormatException Ex) {
						outString[9] = String.format("%9s", "-");
					}

					// Time Stamp
					timeStamp = symbolObj.get("timeStamp");

				} catch (Exception Ex) {
					Output.printColorln(Ansi.Color.RED, "Unknown Error Occured formatting secuity output");
				}

				// Determine the color based on the change amount
				Ansi.Color outputColor = Ansi.Color.WHITE;
				try {
					if (Float.valueOf(symbolObj.get("change")) < 0) {
						outputColor = Ansi.Color.RED;
					}

					// Write the output to the screen
					for (int k = 0; k < outString.length; k++) {
						Output.printColor(outputColor, outString[k]);
					}

				} catch (Exception Ex) {
					Output.printColor(Ansi.Color.RED, "Error Retrieving Data for Symbol:\t" + outString[0]);
				}

				// Start a new line for the next security
				Output.println("");

				// Set the time stamp
				timeStamp = symbolObj.get("timeStamp");

				// If export is chosen, dump this security's data to the export file
				if (cli.clExport.isEmpty() == false && exporter.canWrite()) {
					exporter.exportSecurities(symbolObj);
				}
			}

		}

		// Unless disabled, display the index data
		if (cli.clHideIndex == false) {
			// Display Index Output Header
			Output.printColorln(Ansi.Color.CYAN, "\n----------------------------------------------------------------------------------------");
			Output.printColorln(Ansi.Color.CYAN, "Index        Current    Change    Change%       52WHigh       52WLow      YTD%    1Year%");
			Output.printColorln(Ansi.Color.CYAN, "----------------------------------------------------------------------------------------");

			// Loop through the three indexes and display the results
			String[] indexList = { "DOW", "NASDAQ", "S&P" };
			for (int i = 0; i < indexList.length; i++) {
				String[] outString = new String[8];

				Output.debugPrintln("\nDEBUG:  --- Getting Index data for: " + indexList[i] + " ---");
				Index indexObj = new Index(indexList[i]);

				// Check to see if the index object has an error status
				if (indexObj.get("status") != "ok") {
					Output.printColorln(Ansi.Color.BLUE, "'" + indexList[i] + "' data could not be retrieved");
					continue;
				}

				try {
					// Determine the color based on the change amount
					Ansi.Color outputColor = Ansi.Color.WHITE;
					if (Float.valueOf(indexObj.get("change")) < 0) {
						outputColor = Ansi.Color.RED;
					}

					// Format the Output
					// Symbol
					outString[0] = String.format("%-10s", indexObj.get("index"));

					// Current
					outString[1] = String.format("%,10.2f", Float.valueOf(indexObj.get("latestPrice")));

					// Change Amount
					outString[2] = String.format("%+,10.2f", Float.valueOf(indexObj.get("change")));

					// Change Percentage
					outString[3] = String.format("%+,10.2f%%", Float.valueOf(indexObj.get("changePercent")));

					// 52Week High
					outString[4] = String.format("%,14.2f", Float.valueOf(indexObj.get("week52High")));

					// 52Week Low
					outString[5] = String.format("%,13.2f", Float.valueOf(indexObj.get("week52Low")));

					// Year to Date Percent Change
					outString[6] = String.format("%+9.2f%%", Float.valueOf(indexObj.get("ytdChangePercent")));

					// One Year Percent Change
					outString[7] = String.format("%+9.2f%%", Float.valueOf(indexObj.get("oneYearChangePercent")));

					// Display Index results to the screen
					for (int k = 0; k < outString.length; k++) {
						Output.printColor(outputColor, outString[k]);
					}

					// Showing the index so use the index time stamp
					timeStamp = indexObj.get("timeStamp");

					// Start a new line for the next index
					Output.println("");

					// If export is chosen, dump this index's data to the export file
					if (cli.clExport.isEmpty() == false && exporter.canWrite()) {
						exporter.exportIndexes(indexObj);
					}
				} catch (Exception ex) {
					indexObj.put("status", "error");
				}
			}
		}

		// Display the open/closed status of the market
		Output.printColor(Ansi.Color.CYAN, "\nThe US markets are currently:  ");
		if (MarketState.queryMarketOpen() == true) {
			Output.printColorln(Ansi.Color.YELLOW, "==[ OPEN ]==");
		} else {
			Output.printColorln(Ansi.Color.YELLOW, "==[ CLOSED ]==");
		}

		// Convert to local time & time zone
		if (!timeStamp.isEmpty()) {
			Output.debugPrintln("Original Time Stamp from Website: " + timeStamp);

			try {
				// Remove the periods from a.m. & p.m. Also make them upper case required by the formatter
				if (!timeStamp.isEmpty()) {
					timeStamp = timeStamp.replaceAll(" [Pp]\\.[Mm]\\.", "PM");
					timeStamp = timeStamp.replaceAll(" [Aa]\\.[Mm]\\.", "AM");
				} else {
					timeStamp = "--";
					throw new DateTimeParseException(null, null, 0);
				}

				// Parse the time stamp into a LocalDateTime object & set the to the Eastern time zone
				LocalDateTime ldt = LocalDateTime.parse(timeStamp, DateTimeFormatter.ofPattern("MMM d yyyy h:mma"));
				ZonedDateTime zdtSrc = ldt.atZone(ZoneId.of("America/New_York"));

				// Get local time zone for this JVM from the JVM system properties
				ZoneId destTimeZone = ZoneId.of(System.getProperty("user.timezone"));
				ZonedDateTime zdtDest = zdtSrc.withZoneSameInstant(destTimeZone);

				// Build the updated time stamp
				timeStamp = zdtDest.format(DateTimeFormatter.ofPattern("MMM dd yyyy hh:mma z (O)"));

			} catch (DateTimeParseException ex) {
				// Just take the original time stamp from the financial website after adding the time zone
				timeStamp = timeStamp + " Eastern Time";
			}
		}

		// Changed the AM/PM to lower case as I think it looks better
		timeStamp = timeStamp.replaceAll("PM", "pm");
		timeStamp = timeStamp.replaceAll("AM", "am");

		// Display the time stamp
		Output.printColorln(Ansi.Color.CYAN, "Data as of " + timeStamp + ". Quotes are delayed.");

		// Display trending data if -t was provided and there is at least one valid symbol
		if (cli.clTrend == true) {
			if (!cli.symbolList.isEmpty()) {
				for (String i : cli.symbolList) {
					HistoricalQuotes hc = new HistoricalQuotes(i);
					hc.displayTrend(i);
				}

			} else {
				Output.printColorln(Ansi.Color.RED, "\nUnable to display security trend (-t) as no securities have been provided. Please see help (-h)");
			}
		}

		// Flush and close export file if needed
		if (cli.clExport.isEmpty() == false) {
			exporter.close();
			Output.printColorln(Ansi.Color.YELLOW, "\nData export has completed to file: '" + exporter.queryExportFilename() + "'");
		}

	}

}

package org.fross.quoter;

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
	public void invokeSymbolOutput(FileExporter exporter) {
		// Store the timestamp
		String timeStamp = "";

		// Display the header
		Output.printColorln(Ansi.Color.CYAN, "\nQuoter v" + Main.VERSION + " " + Main.COPYRIGHT);

		// If symbols were entered, display the header for them
		if (cli.symbolList.size() > 0) {
			Output.printColorln(Ansi.Color.CYAN, "-------------------------------------------------------------------------------");
			Output.printColorln(Ansi.Color.WHITE, "Symbol   Current    Chng   Chng%  DayHigh   Daylow  52WHigh   52WLow       YTD");
			Output.printColorln(Ansi.Color.CYAN, "-------------------------------------------------------------------------------");
		}

		// Display the data for the symbols entered. If no symbols were entered, just
		// display the index data
		if (cli.symbolList.size() > 0) {
			// Loop through each entered symbol and display it's data
			Iterator<String> j = cli.symbolList.iterator();
			String currentSymbol = "";

			while (j.hasNext()) {
				currentSymbol = j.next();
				String[] outString = new String[9];
				Symbol symbolObj = new Symbol(currentSymbol);

				// Validate the provided quote is valid
				// If invalid, remove it from symbol list so it doesn't get processed later with trend or export
				if (symbolObj.get("status").compareTo("error") == 0) {
					// Display error
					Output.printColorln(Ansi.Color.BLUE, "No information for '" + symbolObj.get("symbol") + "'");

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
						outString[8] = String.format("%+9.2f%%", Float.valueOf(symbolObj.get("ytdChange")));
					} catch (NumberFormatException Ex) {
						outString[8] = String.format("%9s", "-");
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

				// If export is chosen, dump this security's data to the export file
//				if (cli.clExport.isEmpty() == false && exporter.canWrite()) {
//					exporter.exportSecurities(symbolData);
//				}
			}

		}

		// Unless disabled, display the index data
		if (cli.clHideIndex == false) {
			// Display Index Output Header
			Output.printColorln(Ansi.Color.CYAN, "\n-------------------------------------------------------------------------------");
			Output.printColorln(Ansi.Color.WHITE, "Index        Current    Change    Change%       52WHigh       52WLow      YTD%");
			Output.printColorln(Ansi.Color.CYAN, "-------------------------------------------------------------------------------");

			// Loop through the three indexes and display the results
			String[] indexList = { "DOW", "NASDAQ", "S&P" };
			for (int i = 0; i < indexList.length; i++) {
				String[] outString = new String[7];
				Index indexObj = new Index(indexList[i]);

				Output.debugPrint("Getting Index data for: " + indexList[i]);

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

					// Year to Date
					outString[6] = String.format("%+9.2f%%", Float.valueOf(indexObj.get("ytd")));

					// Display Index results to the screen
					for (int k = 0; k < outString.length; k++) {
						Output.printColor(outputColor, outString[k]);
					}

					// Start a new line for the next index
					Output.println("");

					// If export is chosen, dump this index's data to the export file
//					if (cli.clExport.isEmpty() == false && exporter.canWrite()) {
//						exporter.exportIndexes(result);
//					}
				} catch (Exception ex) {
					Output.printColorln(Ansi.Color.RED, outString[0] + ": No Data");
				}
			}
		}

		// Display the open/closed status of the market
		Output.printColor(Ansi.Color.CYAN, "\nThe US index markets are currently: ");
		if (Index.marketOpen == true) {
			Output.printColorln(Ansi.Color.YELLOW, "OPEN");
		} else {
			Output.printColorln(Ansi.Color.YELLOW, "CLOSED");
		}

		Output.printColorln(Ansi.Color.CYAN, "Data as of " + timeStamp + " and may be 15min delayed");

		// Display detailed stock information if selected with the -d switch
//		if (cli.clDetailedOutput && !cli.symbolList.isEmpty()) {
//			final int HEADERWIDTH = 80;
//			String[] companyFields = { "symbol", "companyName", "exchange", "industry", "website", "description", "CEO", "securityName", "issueType", "sector",
//					"primarySicCode", "employees", "address", "address2", "city", "state", "zip", "country", "phone" };
//
//			String[] symbolFields = { "open", "openTime", "close", "closeTime", "high", "highTime", "low", "lowTime", "latestPrice", "latestVolume",
//					"previousClose", "previousVolume", "change", "changePercent", "agTotalVolume", "marketCap", "peRatio", "week52High", "week52Low" };
//
//			Output.printColorln(Ansi.Color.WHITE, "\nDetailed Security Information:");
//
//			// Display detail of each symbol provided on command line
//			for (String symb : cli.symbolList) {
//				// Query company data
//				IEXCloudAPICall companyDetail = new IEXCloudAPICall(Main.IEXCloudBaseURL + "/stable/stock/" + symb + "/company", IEXCloudToken);
//
//				// Query symbol data
//				Symbol symbolDetail = new Symbol(symb, IEXCloudToken);
//
//				// Display Header
//				Output.printColorln(Ansi.Color.CYAN, "-".repeat(HEADERWIDTH));
//				Output.printColorln(Ansi.Color.YELLOW, symb.toUpperCase() + " / " + companyDetail.get("companyName"));
//				Output.printColorln(Ansi.Color.CYAN, "-".repeat(HEADERWIDTH));
//
//				// Display company information
//				for (String field : companyFields) {
//					Output.printColor(Ansi.Color.WHITE, " " + String.format("%-16s", Format.Capitalize(field) + ":") + " ");
//					Output.printColorln(Ansi.Color.CYAN, " " + companyDetail.get(field));
//				}
//				Output.println("");
//
//				// Loop through each detailed field and display it
//				for (String field : symbolFields) {
//					Output.printColorln(Ansi.Color.WHITE, " " + String.format("%-16s", Format.Capitalize(field) + ":") + "  " + symbolDetail.get(field));
//				}
//				Output.println("");
//			}
//		}
//
//		// Display trending data if -t was provided and there is at least one valid symbol
//		if (cli.clTrend == true) {
//			if (!cli.symbolList.isEmpty()) {
//				for (String i : cli.symbolList) {
//					HistoricalQuotes.displayTrendingMap(i, IEXCloudToken);
//					if (cli.clSandbox == true) {
//						Output.printColorln(Ansi.Color.RED, Format.CenterText(cli.clWidth, "**** SANDBOX MODE ENABLED - DATA IS INCORRECT ****"));
//					}
//				}
//			} else {
//				Output.printColorln(Ansi.Color.RED, "\nUnable to display security trend (-t) as no securities have been provided. Please see help (-h)");
//			}
//		}

		// Flush and close export file if needed
		if (cli.clExport.isEmpty() == false) {
			exporter.close();
			Output.printColor(Ansi.Color.CYAN, "\nData Export Complete to '" + exporter.queryExportFilename() + "'\n");
		}

		if (cli.clAutoRefresh > 0) {
			Output.printColorln(Ansi.Color.RED, String.format("\nAuto-Refresh enabled for %d seconds. Press 'CTRL + C' to exit.", cli.clAutoRefresh));
		}
	}

}

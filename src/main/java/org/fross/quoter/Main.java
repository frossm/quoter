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

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Scanner;

import org.fross.library.Date;
import org.fross.library.Debug;
import org.fross.library.Format;
import org.fross.library.GitHub;
import org.fross.library.Output;
import org.fusesource.jansi.Ansi;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

/**
 * Main execution class
 *
 */
public class Main {
	// Class Constants
	public static String VERSION;
	public static String COPYRIGHT;
	public static final String PROPERTIES_FILE = "app.properties";
	public static final String PREFS_SAVED_SYMBOLS = "savedsymbols";
	public static final String IEXCLOUDPRODURL = "https://cloud.iexapis.com";
	public static final String IEXCLOUDSANDBOXURL = "https://sandbox.iexapis.com";
	public static String IEXCloudBaseURL = IEXCLOUDPRODURL;

	// Class Variables
	static CommandLineParser cli = new CommandLineParser();

	/**
	 * Main(): Program entry point
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		final String PREFS_IEXCLOUDPRODTOKEN = "iexcloudtoken";
		final String PREFS_IEXCLOUDSBOXTOKEN = "iexcloudsboxtoken";
		String IEXCloudToken = "";
		String latestTime = "None";
		FileExporter exporter = null;

		// Process application level properties file
		// Update properties from Maven at build time:
		// https://stackoverflow.com/questions/3697449/retrieve-version-from-maven-pom-xml-in-code
		try {
			InputStream iStream = Main.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE);
			Properties prop = new Properties();
			prop.load(iStream);
			VERSION = prop.getProperty("Application.version");
			COPYRIGHT = "Copyright " + prop.getProperty("Application.inceptionYear") + "-" + org.fross.library.Date.getCurrentYear() + " by Michael Fross";
		} catch (IOException ex) {
			Output.fatalError("Unable to read property file '" + PROPERTIES_FILE + "'", 3);
		}

		// Display the header
		Output.printColorln(Ansi.Color.CYAN, "\nQuoter v" + VERSION + " " + COPYRIGHT);

		// ---- BEGIN Command Line Parsing -------------------------------------------------------------

		// ---------------------------------------------------------------------------------------------
		// Process command line parameters with the following methods
		// ---------------------------------------------------------------------------------------------
		JCommander jc = new JCommander();

		// JCommander parses the command line
		try {
			jc.setProgramName("Quoter");
			jc = JCommander.newBuilder().addObject(cli).build();
			jc.parse(args);
		} catch (ParameterException ex) {
			System.out.println(ex.getMessage());
			jc.usage();
			System.exit(0);
		}

		// CLI: Debug Switch
		if (cli.clDebug == true)
			Debug.enable();

		// CLI: list Favorites
		if (cli.clListFavorites == true) {
			Output.printColorln(Ansi.Color.YELLOW, "Current Favorites:");
			for (String i : Prefs.queryString(PREFS_SAVED_SYMBOLS).split(" ")) {
				if (i != "Error") {
					Output.printColorln(Ansi.Color.CYAN, "  - " + i);
				} else {
					Output.printColorln(Ansi.Color.CYAN, "  - There are no saved favorites");
				}
			}
			System.exit(0);
		}

		// CLI: Turn on Sandbox Mode
		if (cli.clSandbox == true) {
			IEXCloudBaseURL = IEXCLOUDSANDBOXURL;
		}

		// CLI: Configure IEXCloud Secret Key
		if (cli.clConfigure == true) {
			Scanner scanner = new Scanner(System.in);
			if (cli.clSandbox == true) {
				Output.printColorln(Ansi.Color.WHITE, "Enter the IEXcloud.io Secret Token for the Sandbox environment: ");
				Prefs.set(PREFS_IEXCLOUDSBOXTOKEN, scanner.next());
				Output.printColorln(Ansi.Color.YELLOW, "IEXCloud.io Secret Sandbox Token Set To: '" + Prefs.queryString(PREFS_IEXCLOUDSBOXTOKEN) + "'");
			} else {
				Output.printColorln(Ansi.Color.WHITE, "Enter the IEXcloud.io Secret Token: ");
				Prefs.set(PREFS_IEXCLOUDPRODTOKEN, scanner.next());
				Output.printColorln(Ansi.Color.YELLOW, "IEXCloud.io Secret Production Token Set To: '" + Prefs.queryString(PREFS_IEXCLOUDPRODTOKEN) + "'");
			}
			scanner.close();
			System.exit(0);
		}

		// CLI: Display the currently configured IEXCloud secret key
		if (cli.clKeyDisplay == true) {
			Output.println("Sandbox Environment Key:\t" + Prefs.queryString(PREFS_IEXCLOUDSBOXTOKEN));
			Output.println("Production Environment Key:\t" + Prefs.queryString(PREFS_IEXCLOUDPRODTOKEN));
			System.exit(0);
		}

		// CLI: Export Data
		if (cli.clExport.isEmpty() == false) {
			exporter = new FileExporter(cli.clExport);
		}

		// CLI: Display IEXCloud Credit Information
		if (cli.clIEXCredits == true) {
			DisplayIEXQuota(Prefs.queryString(PREFS_IEXCLOUDPRODTOKEN));
		}

		// CLI: Display Version & Latest GitHub Release
		if (cli.clVersion == true) {
			Output.printColorln(Ansi.Color.WHITE, "\nLatest Release on GitHub: " + GitHub.updateCheck("quoter"));
			Output.printColorln(Ansi.Color.CYAN, "HomePage: https://github.com/frossm/quoter");
			System.exit(0);
		}

		// CLI: Remove saved securities
		if (cli.clRemoveFavorites == true) {
			Prefs.remove(PREFS_SAVED_SYMBOLS);
			Output.printColor(Ansi.Color.YELLOW, "Saved securities have been removed\n");
			System.exit(0);
		}

		// CLI: Read the preferences and make sure that a production API key has been entered with the -c option
		if (cli.clSandbox == true) {
			IEXCloudToken = Prefs.queryString(PREFS_IEXCLOUDSBOXTOKEN);
			Output.printColorln(Ansi.Color.RED, Format.CenterText(80, "**** SANDBOX MODE ENABLED - SYMBOL DATA IS INCORRECT ****"));
		} else {
			IEXCloudToken = Prefs.queryString(PREFS_IEXCLOUDPRODTOKEN);
		}
		if (IEXCloudToken == "Error") {
			Output.fatalError("No iexcloud.io secret token provided.  Use '-c' option to configure.", 1);
		}

		// CLI: Disable color output
		if (cli.clNoColor == true) {
			Output.enableColor(false);
		}

		// CLI: Show Help and Exit
		if (cli.clHelp == true) {
			Help.Display();
			System.exit(0);
		}

		// CLI: In debug mode, show the number of symbols listed on the command line
		Output.debugPrint("Number of Symbols entered on command line: " + cli.symbolList.size());

		// ---- END Command Line Parsing -------------------------------------------------------------

		// Save the symbols on the command line to preferences as a space delimited list
		if (cli.clSave == true && cli.symbolList.isEmpty() == false) {
			String flatSymbolList = "";

			for (String i : cli.symbolList) {
				flatSymbolList += i + " ";
			}
			Output.printColorln(Ansi.Color.YELLOW, " - Saving the following symbols: '" + flatSymbolList.trim() + "'");
			Prefs.set(PREFS_SAVED_SYMBOLS, flatSymbolList.trim());

			// Empty the symbol list after saving as they will be added back below. Don't want it twice
			cli.symbolList.clear();
		}

		// Add any saved symbols to the list of symbols to process
		if (cli.clIgnoreFavorites == false) {
			Output.debugPrint("Adding saved symbols: '" + Prefs.queryString(PREFS_SAVED_SYMBOLS) + "'");
			String[] savedSymbols = Prefs.queryString(PREFS_SAVED_SYMBOLS).split(" ");
			for (String i : savedSymbols) {
				if (i != "Error") {
					cli.symbolList.add(i);
				}
			}
		}

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

				// Create the symbol object
				Symbol symbolData = new Symbol(currentSymbol, IEXCloudToken);

				// Validate the provided quote is valid
				// If invalid, remove it from symbol list so it doesn't get processed later with trend or export
				if (symbolData.get("status").compareTo("Error") == 0) {
					// Display error
					Output.printColorln(Ansi.Color.BLUE, "'" + symbolData.get("symbol") + "' is invalid");

					// Remove this invalid symbol from the list and continue to the next iteration
					j.remove();
					continue;
				}

				// Format the Output into an array
				try {
					// Symbol
					outString[0] = String.format("%-8s", symbolData.get("symbol"));

					// Current
					try {
						outString[1] = String.format("%,8.2f", Float.valueOf(symbolData.get("latestPrice")));
					} catch (NumberFormatException Ex) {
						outString[1] = String.format("%8s", "-");
					}

					// Change Amount
					try {
						outString[2] = String.format("%+,8.2f", Float.valueOf(symbolData.get("change")));
					} catch (NumberFormatException Ex) {
						outString[2] = String.format("%8s", "-");
					}

					// Change Percentage
					try {
						outString[3] = String.format("%+,7.2f%%", (Float.valueOf(symbolData.get("changePercent")) * 100));
					} catch (NumberFormatException Ex) {
						outString[3] = String.format("%8s", "-");
					}

					// Day High
					try {
						outString[4] = String.format("%,9.2f", Float.valueOf(symbolData.get("high")));
					} catch (NumberFormatException Ex) {
						outString[4] = String.format("%9s", "-");
					}

					// Day Low
					try {
						outString[5] = String.format("%,9.2f", Float.valueOf(symbolData.get("low")));
					} catch (NumberFormatException Ex) {
						outString[5] = String.format("%9s", "-");
					}

					// 52 Week High
					try {
						outString[6] = String.format("%,9.2f", Float.valueOf(symbolData.get("week52High")));
					} catch (NumberFormatException Ex) {
						outString[6] = String.format("%9s", "-");
					}

					// 52 Week Low
					try {
						outString[7] = String.format("%,9.2f", Float.valueOf(symbolData.get("week52Low")));
					} catch (NumberFormatException Ex) {
						outString[7] = String.format("%9s", "-");
					}

					// Year to date
					try {
						outString[8] = String.format("%+,9.2f%%", (Float.valueOf(symbolData.get("ytdChange")) * 100));
					} catch (NumberFormatException Ex) {
						outString[8] = String.format("%9s", "-");
					}

				} catch (Exception Ex) {
					Output.printColorln(Ansi.Color.RED, "Unknown Error Occured formatting secuity output");
				}

				// Determine the color based on the change amount
				Ansi.Color outputColor = Ansi.Color.WHITE;
				try {
					if (Float.valueOf(symbolData.get("change")) < 0) {
						outputColor = Ansi.Color.RED;
					}

					// Write the output to the screen
					for (int k = 0; k < outString.length; k++) {
						Output.printColor(outputColor, outString[k]);
					}

				} catch (NumberFormatException Ex) {
					Output.printColor(Ansi.Color.RED, "Error Retrieving Data for Symbol:\t" + outString[0]);
				}

				// Set the latest time for output later. Since they should all be the same, just keep that last
				// symbol's data
				latestTime = symbolData.get("latestUpdate");

				// Start a new line for the next security
				Output.println("");

				// If export is chosen, dump this security's data to the export file
				if (cli.clExport.isEmpty() == false && exporter.canWrite()) {
					exporter.exportSecurities(symbolData);
				}
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
				// Download the web page and return the results array
				Output.debugPrint("Getting Index data for: " + indexList[i]);

				String[] outString = new String[7];
				String[] result = Index.getIndex(indexList[i]);

				try {
					// Determine the color based on the change amount
					Ansi.Color outputColor = Ansi.Color.WHITE;
					if (Float.valueOf(result[2]) < 0) {
						outputColor = Ansi.Color.RED;
					}

					// Format the Output
					// Symbol
					outString[0] = String.format("%-10s", result[0]);

					// Current
					outString[1] = String.format("%,10.2f", Float.valueOf(result[1]));

					// Change Amount
					outString[2] = String.format("%+,10.2f", Float.valueOf(result[2]));

					// Change Percentage
					outString[3] = String.format("%+,10.2f%%", Float.valueOf(result[3]));

					// 52Week High
					outString[4] = String.format("%,14.2f", Float.valueOf(result[4]));

					// 52Week Low
					outString[5] = String.format("%,13.2f", Float.valueOf(result[5]));

					// Year to Date
					outString[6] = String.format("%+9.2f%%", Float.valueOf(result[6]));

					// Display Index results to the screen
					for (int k = 0; k < outString.length; k++) {
						Output.printColor(outputColor, outString[k]);
					}

					// Start a new line for the next index
					Output.println("");

					// If export is chosen, dump this index's data to the export file
					if (cli.clExport.isEmpty() == false && exporter.canWrite()) {
						exporter.exportIndexes(result);
					}
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

		// Display date of the data as pulled from iecloud.net. If no symbols were provided and
		// just index data is displayed, grab a security in order to get the date
		if (cli.symbolList.isEmpty()) {
			Symbol getTime = new Symbol("IBM", IEXCloudToken);
			latestTime = getTime.get("latestUpdate");
		}
		Output.printColorln(Ansi.Color.CYAN, "Data as of " + latestTime + " and may be 15min delayed");

		// Display detailed stock information if selected with the -d switch
		if (cli.clDetailedOutput == true && !cli.symbolList.isEmpty()) {
			final int HEADERWIDTH = 80;
			String[] companyFields = { "symbol", "companyName", "exchange", "industry", "website", "description", "CEO", "securityName", "issueType", "sector",
					"primarySicCode", "employees", "address", "address2", "city", "state", "zip", "country", "phone" };

			String[] symbolFields = { "open", "openTime", "close", "closeTime", "high", "highTime", "low", "lowTime", "latestPrice", "latestVolume",
					"previousClose", "previousVolume", "change", "changePercent", "agTotalVolume", "marketCap", "peRatio", "week52High", "week52Low" };

			Output.printColorln(Ansi.Color.WHITE, "\nDetailed Security Information:");

			// Display detail of each symbol provided on command line
			for (String symb : cli.symbolList) {
				// Query company data
				IEXCloudAPICall companyDetail = new IEXCloudAPICall(Main.IEXCloudBaseURL + "/stable/stock/" + symb + "/company", IEXCloudToken);

				// Query symbol data
				Symbol symbolDetail = new Symbol(symb, IEXCloudToken);

				// Display Header
				Output.printColorln(Ansi.Color.CYAN, "-".repeat(HEADERWIDTH));
				Output.printColorln(Ansi.Color.YELLOW, symb.toUpperCase() + " / " + companyDetail.get("companyName"));
				Output.printColorln(Ansi.Color.CYAN, "-".repeat(HEADERWIDTH));

				// Display company information
				for (String field : companyFields) {
					Output.printColor(Ansi.Color.WHITE, " " + String.format("%-16s", Format.Capitalize(field) + ":") + " ");
					Output.printColorln(Ansi.Color.CYAN, " " + companyDetail.get(field));
				}
				Output.println("");

				// Loop through each detailed field and display it
				for (String field : symbolFields) {
					Output.printColorln(Ansi.Color.WHITE, " " + String.format("%-16s", Format.Capitalize(field) + ":") + "  " + symbolDetail.get(field));
				}
				Output.println("");
			}
		}

		// Display trending data if -t was provided and there is at least one valid symbol
		if (cli.clTrend == true) {
			if (!cli.symbolList.isEmpty()) {
				for (String i : cli.symbolList) {
					HistoricalQuotes.displayTrendingMap(i, IEXCloudToken);
					if (cli.clSandbox == true) {
						Output.printColorln(Ansi.Color.RED, Format.CenterText(cli.clWidth, "**** SANDBOX MODE ENABLED - DATA IS INCORRECT ****"));
					}
				}
			} else {
				Output.printColorln(Ansi.Color.RED, "\nUnable to display security trend (-t) as no securities have been provided. Please see help (-h)");
			}
		}

		// Flush and close export file if needed
		if (cli.clExport.isEmpty() == false) {
			exporter.close();
			Output.printColor(Ansi.Color.CYAN, "\nData Export Complete to '" + exporter.queryExportFilename() + "'\n");
		}

	} // END OF MAIN

	/**
	 * DisplayIEXQuote(): Display the current IEXCloud quota left and exit
	 * 
	 * @param IEXCloudToken
	 */
	private static void DisplayIEXQuota(String IEXCloudToken) {
		Output.printColorln(Ansi.Color.YELLOW, "\nIEXCloud Account Credit Limits for " + Date.getCurrentMonthNameLong() + " " + Date.getCurrentYear());

		try {
			IEXCloudAPICall metaData = new IEXCloudAPICall("https://cloud.iexapis.com/stable/account/metadata", IEXCloudToken);
			long creditsUsed = Long.parseLong(metaData.get("creditsUsed").substring(0, metaData.get("creditsUsed").indexOf('.')).strip());
			long creditLimit = Long.parseLong(metaData.get("creditLimit").substring(0, metaData.get("creditLimit").indexOf('.')).strip());
			double creditUsedPercentage = ((double) creditsUsed / creditLimit) * 100;

			Output.printColor(Ansi.Color.WHITE, "   Total Monthly Credits:");
			Output.printColorln(Ansi.Color.YELLOW, String.format("%10s", Format.Comma(creditLimit)));

			Output.printColor(Ansi.Color.WHITE, "   Current Credits Used:");
			Output.printColor(Ansi.Color.YELLOW, String.format("%11s", Format.Comma(creditsUsed)));
			Output.printColorln(Ansi.Color.YELLOW, "  (" + String.format("%3.2f%%", creditUsedPercentage) + ")");

		} catch (Exception ex) {
			Output.fatalError("Could not display IEXCloud credit usage", 4);
		}
		System.exit(0);

	} // END OF DISPLAYIEXQUOTA

}

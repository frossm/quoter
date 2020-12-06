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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.fross.library.Debug;
import org.fross.library.Output;
import org.fusesource.jansi.Ansi;

import gnu.getopt.Getopt;

/**
 * Main execution class
 *
 */
public class Main {
	// Class Constants
	public static String VERSION;
	public static String COPYRIGHT;
	public static final String PROPERTIES_FILE = "app.properties";

	public static void main(String[] args) {
		int optionEntry;
		String iexCloudToken;
		String latestTime = "None";
		File exportFile = null;
		FileWriter exportFileFW = null;
		boolean exportFlag = false;
		boolean trendFlag = false;

		// Process application level properties file
		// Update properties from Maven at build time:
		// https://stackoverflow.com/questions/3697449/retrieve-version-from-maven-pom-xml-in-code
		try {
			InputStream iStream = Main.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE);
			Properties prop = new Properties();
			prop.load(iStream);
			VERSION = prop.getProperty("Application.version");
			COPYRIGHT = "Copyright " + prop.getProperty("Application.inceptionYear") + "-" + org.fross.library.Date.getCurrentYear()
					+ " by Michael Fross.  All rights reserved";
		} catch (IOException ex) {
			Output.fatalError("Unable to read property file '" + PROPERTIES_FILE + "'", 3);
		}

		// Process Command Line Options
		Getopt optG = new Getopt("quote", args, "Dtckx:h?v");
		while ((optionEntry = optG.getopt()) != -1) {
			switch (optionEntry) {
			// Turn on Debug Mode
			case 'D':
				Debug.enable();
				break;

			// Show Stock Trending
			case 't':
				trendFlag = true;
				break;

			// Configure IEXCloud Secret Key
			case 'c':
				Scanner scanner = new Scanner(System.in);
				Output.printColorln(Ansi.Color.WHITE, "Enter the IEXcloud.io Secret Token: ");
				iexCloudToken = scanner.next();
				Output.debugPrint("Setting Preference iexcloudtoken: " + iexCloudToken);
				Prefs.Set("iexcloudtoken", iexCloudToken);
				Output.printColorln(Ansi.Color.YELLOW, "IEXCloud.io Secret Token Set To: '" + Prefs.QueryString("iexcloudtoken") + "'");
				System.exit(0);
				break;

			// Export Data
			case 'x':
				try {
					exportFile = new File(optG.getOptarg());
					if (exportFile.createNewFile() == false) {
						Output.fatalError("Could not create file: '" + exportFile + "'", 4);
					}
				} catch (IOException ex) {
					Output.fatalError("Could not create file: '" + exportFile + "'", 4);
				}
				exportFlag = true;
				try {
					exportFileFW = new FileWriter(exportFile);
				} catch (IOException ex) {
					Output.printColorln(Ansi.Color.RED, "Error writing to export file: " + ex.getMessage());
				}
				break;

			// Display configured IEXCloud Secret Key
			case 'k':
				Output.println(Prefs.QueryString("iexcloudtoken"));
				System.exit(0);
				break;

			// Display version of Quoter and exit
			case 'v':
				Output.println(VERSION);
				System.exit(0);
				break;

			// Access in program help
			case '?':
			case 'h':
				Help.Display();
				System.exit(0);
				break;

			default:
				Output.printColor(Ansi.Color.RED, "Unknown Command Line Option: '" + (char) optionEntry + "'");
				Help.Display();
				System.exit(0);
				break;
			}
		}

		// Read the preferences and make sure that an API key has been entered with the -c
		// option
		if (Prefs.QueryString("iexcloudtoken") == "Error") {
			Output.fatalError("No iexcloud.io secret token provided.  Use '-c' option to configure.", 1);
		}

		// Display the header
		Output.printColorln(Ansi.Color.CYAN, "\nQuoter v" + VERSION + " " + COPYRIGHT);

		// Build an array list of symbols entered in on the command line
		Output.debugPrint("Number of Symbols entered: " + (args.length - optG.getOptind()));
		ArrayList<String> symbolList = new ArrayList<String>();
		for (int i = optG.getOptind(); i < args.length; i++) {
			Output.debugPrint("Symbol entered on commandline: " + args[i]);
			symbolList.add(args[i]);
		}

		// If symbols were entered, display the header for them
		if (symbolList.size() > 0) {
			Output.printColorln(Ansi.Color.CYAN, "-------------------------------------------------------------------------------");
			Output.printColorln(Ansi.Color.WHITE, "Symbol   Current    Chng   Chng%  DayHigh   Daylow  52WHigh   52WLow       YTD");
			Output.printColorln(Ansi.Color.CYAN, "-------------------------------------------------------------------------------");
		}

		// Display the data for the symbols entered. If no symbols were entered, just
		// display the index data
		if (!symbolList.isEmpty()) {
			// Loop through each entered symbol and display it's data
			Iterator<String> j = symbolList.iterator();
			String currentSymbol = "";
			boolean exportHeaderWritten = false;

			while (j.hasNext()) {
				currentSymbol = j.next();
				String[] outString = new String[9];

				// Create the symbol object
				Symbol symbolData = new Symbol(currentSymbol, Prefs.QueryString("iexcloudtoken"));

				// Validate the provided quote is valid
				if (symbolData.query("status").compareTo("Error") == 0) {
					// Display error and skip to the next iteration
					Output.printColorln(Ansi.Color.BLUE, "'" + symbolData.query("symbol") + "' is invalid");
					continue;
				}

				// Format the Output into an array
				try {
					// Symbol
					outString[0] = String.format("%-8s", symbolData.query("symbol"));

					// Current
					try {
						outString[1] = String.format("%,8.2f", Float.valueOf(symbolData.query("latestPrice")));
					} catch (NumberFormatException Ex) {
						outString[1] = String.format("%8s", "-");
					}

					// Change Amount
					try {
						outString[2] = String.format("%+,8.2f", Float.valueOf(symbolData.query("change")));
					} catch (NumberFormatException Ex) {
						outString[2] = String.format("%8s", "-");
					}

					// Change Percentage
					try {
						outString[3] = String.format("%+,7.2f%%", (Float.valueOf(symbolData.query("changePercent")) * 100));
					} catch (NumberFormatException Ex) {
						outString[3] = String.format("%8s", "-");
					}

					// Day High
					try {
						outString[4] = String.format("%,9.2f", Float.valueOf(symbolData.query("high")));
					} catch (NumberFormatException Ex) {
						outString[4] = String.format("%8s", "-");
					}

					// Day Low
					try {
						outString[5] = String.format("%,9.2f", Float.valueOf(symbolData.query("low")));
					} catch (NumberFormatException Ex) {
						outString[5] = String.format("%8s", "-");
					}

					// 52 Week High
					try {
						outString[6] = String.format("%,9.2f", Float.valueOf(symbolData.query("week52High")));
					} catch (NumberFormatException Ex) {
						outString[6] = String.format("%8s", "-");
					}

					// 52 Week Low
					try {
						outString[7] = String.format("%,9.2f", Float.valueOf(symbolData.query("week52Low")));
					} catch (NumberFormatException Ex) {
						outString[7] = String.format("%8s", "-");
					}

					// Year to date
					try {
						outString[8] = String.format("%+,9.2f%%", (Float.valueOf(symbolData.query("ytdChange")) * 100));
					} catch (NumberFormatException Ex) {
						outString[8] = String.format("%8s", "-");
					}

				} catch (Exception Ex) {
					Output.printColorln(Ansi.Color.RED, "Unknown Error Occured");
				}

				// Determine the color based on the change amount
				Ansi.Color outputColor = Ansi.Color.WHITE;
				try {
					if (Float.valueOf(symbolData.query("change")) < 0) {
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
				latestTime = symbolData.query("latestUpdate");

				// Start a new line for the next security
				Output.println("");

				// If export is chosen, dump this security's data to the export file
				if (exportFlag == true && exportFile.canWrite()) {
					try {
						// Export the header row
						List<String> fields = symbolData.queryAllFieldNames();
						if (exportHeaderWritten == false) {
							for (String i : fields) {
								exportFileFW.append(i + ",");
							}
							exportFileFW.append("\n");
							exportHeaderWritten = true;
						}

						// Export the symbol data
						for (String i : fields) {
							exportFileFW.append(symbolData.query(i) + ",");
						}
						exportFileFW.append("\n");

					} catch (IOException ex) {

						Output.printColorln(Ansi.Color.RED, "Error writing to export file: " + ex.getMessage());
					}
				}
			}

			Output.println("");
		}

		// Display Index Output Header
		Output.printColorln(Ansi.Color.CYAN, "-------------------------------------------------------------------------------");
		Output.printColorln(Ansi.Color.WHITE, "Index        Current    Change    Change%");
		Output.printColorln(Ansi.Color.CYAN, "-------------------------------------------------------------------------------");

		// Loop through the three indexes and display the results
		String[] indexList = { "DOW", "NASDAQ", "S&P" };
		try {
			boolean exportHeaderWritten = false;
			for (int i = 0; i < indexList.length; i++) {
				// Download the web page and return the results array
				Output.debugPrint("Getting Index data for: " + indexList[i]);
				String[] result = Index.getIndex(indexList[i]);

				// Determine the color based on the change amount
				Ansi.Color outputColor = Ansi.Color.WHITE;
				if (Float.valueOf(result[2]) < 0) {
					outputColor = Ansi.Color.RED;
				}

				// Format the Output
				// Index Name
				String[] outString = new String[4];
				outString[0] = String.format("%-10s", result[0]);
				// Current
				outString[1] = String.format("%,10.2f", Float.valueOf(result[1]));
				// Change Amount
				outString[2] = String.format("%+,10.2f", Float.valueOf(result[2]));
				// Change Percentage
				outString[3] = String.format("%+,10.2f%%", Float.valueOf(result[3]));

				// Display Index results to the string
				for (int k = 0; k < outString.length; k++) {
					Output.printColor(outputColor, outString[k]);
				}

				// Start a new line for the next index
				Output.println("");

				// If export is chosen, dump this index's data to the export file
				if (exportFlag == true && exportFile.canWrite()) {
					try {

						// Dump the header information to the export file
						if (exportHeaderWritten == false) {
							exportFileFW.append("\nSymbol,Current,Chng,Chng%\n");
							exportHeaderWritten = true;
						}
						
						// Dump the index data
						for (int k = 0; k < result.length; k++) {
							exportFileFW.append(result[k] + ",");
						}
						exportFileFW.append("\n");

					} catch (IOException ex) {
						Output.printColorln(Ansi.Color.RED, "Error writing to export file: " + ex.getMessage());
					}
				}
			}

		} catch (Exception Ex) {
			Output.printColor(Ansi.Color.RED, "No Data");
		}

		// Display date of the data as pulled from iecloud.net. If no symbols were provided and
		// just index data is displayed, grab a security in order to get the date
		if (symbolList.isEmpty()) {
			Symbol getTime = new Symbol("IBM", Prefs.QueryString("iexcloudtoken"));
			latestTime = getTime.query("latestUpdate");
		}
		Output.printColorln(Ansi.Color.CYAN, "\nData as of " + latestTime);

		// Flush and close export file if needed
		if (exportFlag == true) {
			try {
				exportFileFW.flush();
				exportFileFW.close();
			} catch (IOException ex) {
				Output.printColorln(Ansi.Color.RED, "Error closing export file: " + ex.getMessage());
			}
		}

		// Display trending data if -t was provided and there is at least one symbol
		if (trendFlag == true && !symbolList.isEmpty()) {
			for (String i : symbolList) {
				HistoricalQuotes.displayTrendingMap(i, Prefs.QueryString("iexcloudtoken"));
			}
		}

	} // END MAIN

} // END CLASS

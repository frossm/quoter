package org.fross.quote;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Scanner;

import com.diogonunes.jcdp.color.api.Ansi.FColor;

import gnu.getopt.Getopt;

/**
 * Main execution class
 *
 */
public class Main {
	// Class Constants
	public static String VERSION;
	public static final String PROPERTIES_FILE = "quote.properties";

	public static void main(String[] args) {
		int optionEntry;
		String iexCloudToken;

		// Process application level properties file
		// Update properties from Maven at build time:
		// https://stackoverflow.com/questions/3697449/retrieve-version-from-maven-pom-xml-in-code
		try {
			InputStream iStream = Main.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE);
			Properties prop = new Properties();
			prop.load(iStream);
			VERSION = prop.getProperty("Application.version");
		} catch (IOException ex) {
			Output.FatalError("Unable to read property file '" + PROPERTIES_FILE + "'", 3);
		}

		// Process Command Line Options and set flags where needed
		Getopt optG = new Getopt("rpn", args, "Dcke:h?v");
		while ((optionEntry = optG.getopt()) != -1) {
			switch (optionEntry) {
			case 'D': // Debug Mode
				Debug.Enable();
				break;
			case 'c': // Configure
				Scanner scanner = new Scanner(System.in);
				Output.PrintColorln(FColor.WHITE, "Enter the IEXcloud.io Secret Token: ");
				iexCloudToken = scanner.next();
				Debug.Print("Setting Peference iexcloudtoken: " + iexCloudToken);
				Prefs.Set("iexcloudtoken", iexCloudToken);
				Output.PrintColorln(FColor.YELLOW,
						"IEXCloud.io Secret Token Set To: '" + Prefs.QueryString("iexcloudtoken") + "'");
				break;
			case 'e':
				Output.Println("Export Results - COMPLETE LATER");
				System.exit(0);
				break;
			case 'k':
				Output.Println("The Configured IEXCloud Secret Key: " + Prefs.QueryString("iexcloudtoken"));
				System.exit(0);
				break;
			case 'v':
				Output.Println("This version of Quote is: " + VERSION);
				System.exit(0);
			case '?': // Help
			case 'h':
				Help.Display();
				System.exit(0);
				break;
			default:
				Output.PrintError("Unknown Command Line Option: '" + (char) optionEntry + "'");
				Help.Display();
				System.exit(0);
				break;
			}
		}

		// Read the prefs and make sure that an API key has been entered with the -c
		// option
		iexCloudToken = Prefs.QueryString("iexcloudtoken");
		if (iexCloudToken == "Error") {
			Output.FatalError("No iexcloud.io secret token provided.  Use '-c' option to configure.", 1);
		}

		// Display the header
		Output.PrintColorln(FColor.CYAN, "\nQuote v" + VERSION + " Copyright 2019 by Michael Fross");
		Output.PrintColorln(FColor.CYAN,
				"-------------------------------------------------------------------------------");
		Output.PrintColorln(FColor.YELLOW,
				"Symbol   Current    Chng   Chng%  DayHigh   Daylow  52WHigh   52WLow     YTD");
		Output.PrintColorln(FColor.CYAN,
				"-------------------------------------------------------------------------------");

		// Build an array list of symbols entered in on the command line
		Debug.Print("Number of Symbols entered: " + (args.length - optG.getOptind()));
		ArrayList<String> symbolList = new ArrayList<String>();
		for (int i = optG.getOptind(); i < args.length; i++) {
			Debug.Print("Symbol entered on commandline: " + args[i]);
			symbolList.add(args[i]);
		}

		// Display the data for the symbols entered. If no symbols were entered, just
		// display the index data
		if (!symbolList.isEmpty()) {
			// Loop through each entered symbol and display it's data
			Iterator<String> j = symbolList.iterator();
			String currentSymbol = "";

			while (j.hasNext()) {
				currentSymbol = j.next();
				String[] result = QuoteOps.GetQuote(currentSymbol, Prefs.QueryString("iexcloudtoken"));
				String[] outString = new String[9];

				// Format the Output into an array
				// Symbol
				try {
					outString[0] = String.format("%-8s", result[0]);
					// Current
					outString[1] = String.format("%,8.2f", Float.valueOf(result[1]));
					// Change Amount
					outString[2] = String.format("%+,8.2f", Float.valueOf(result[2]));
					// Change Percentage
					outString[3] = String.format("%+,7.2f%%", (Float.valueOf(result[3]) * 100));
					// Day High
					outString[4] = String.format("%,9.2f", Float.valueOf(result[4]));
					// Day Low
					outString[5] = String.format("%,9.2f", Float.valueOf(result[5]));
					// 52 Week High
					outString[6] = String.format("%,9.2f", Float.valueOf(result[6]));
					// 52 Week Low
					outString[7] = String.format("%,9.2f", Float.valueOf(result[7]));
					// Year to date
					outString[8] = String.format("%+,9.2f%%", (Float.valueOf(result[8]) * 100));

				} catch (NumberFormatException Ex) {
					Output.PrintColorln(FColor.RED, "Could not process symbol: '" + currentSymbol + "'");
				} catch (NullPointerException Ex) {
					// Skip if we don't have a full set of data. Handle it during the output below
				} catch (Exception Ex) {
					Output.PrintColorln(FColor.RED, "Unknown Error Occured");
				}

				// Determine the color based on the change amount
				FColor outputColor = FColor.WHITE;
				if (Float.valueOf(result[2]) < 0) {
					outputColor = FColor.RED;
				}

				// Write the output to the screen
				for (int k = 0; k < outString.length; k++) {
					if (outString[k] != null) {
						Output.PrintColor(outputColor, outString[k]);
					} else {
						Output.PrintColor(outputColor, String.format("%8s", "-"));
					}
				}

				// Start a new line for the next security
				Output.Println("");

			}

			Output.Println("");
		}

		// Display Index Output Header
		Output.PrintColorln(FColor.CYAN,
				"-------------------------------------------------------------------------------");
		Output.PrintColorln(FColor.YELLOW, "Symbol       Current      Chng      Chng%");
		Output.PrintColorln(FColor.CYAN,
				"-------------------------------------------------------------------------------");

		// Loop through the three indexes and display the resul ts
		String[] indexList = { "DOW", "NASDAQ", "S&P" };
		try {
			for (int i = 0; i < indexList.length; i++) {

				// Download the web page and return the results array
				Debug.Print("Getting Index data for: " + indexList[i]);
				String[] result = QuoteOps.GetIndex(indexList[i]);

				// Determine the color based on the change amount
				FColor outputColor = FColor.WHITE;
				if (Float.valueOf(result[2]) < 0) {
					outputColor = FColor.RED;
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
					Output.PrintColor(outputColor, outString[k]);
				}

				// Start a new line for the next index
				Output.Println("");
			}

		} catch (Exception Ex) {
			Output.PrintColor(FColor.RED, "No Data");
		}

	}
}

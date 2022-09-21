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
import java.util.Properties;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

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
	protected static final CommandLineParser cli = new CommandLineParser();
	private static final QuoteConsoleOutput quoteConsoleOutput = new QuoteConsoleOutput(cli);

	/**
	 * Main(): Program entry point
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		final String PREFS_IEXCLOUDPRODTOKEN = "iexcloudtoken";
		final String PREFS_IEXCLOUDSBOXTOKEN = "iexcloudsboxtoken";
		String IEXCloudToken = "";
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
			quoteConsoleOutput.DisplayIEXQuota(Prefs.queryString(PREFS_IEXCLOUDPRODTOKEN));
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

		// Fetch and display ticker information
		quoteConsoleOutput.invokeSymbolOutput(IEXCloudToken, exporter);

		// Perform async fetches and display ticker information until user cancels application
		if (cli.clAutoRefresh > 0) {
			Output.debugPrint("Starting auto-refresh async timer.");
			if (!cli.clExport.isEmpty()) {
				Output.printColorln(Ansi.Color.RED, "Auto-Refresh flag cannot be used with exporting data to file.");
				System.exit(0);
			}
			final FetchLatestTask asyncTimer = new FetchLatestTask(IEXCloudToken, exporter);
			new Timer().schedule(asyncTimer, cli.clAutoRefresh * 1000, cli.clAutoRefresh * 1000);
		}

	} // END OF MAIN

	/**
	 * FetchLatestTask(): Setup timed task to refresh Quoter
	 * 
	 * @author pgalasti
	 *
	 */
	private static class FetchLatestTask extends TimerTask {

		private String IEXCloudToken;
		private FileExporter exporter;

		public FetchLatestTask(final String IEXCloudToken, final FileExporter exporter) {
			this.IEXCloudToken = IEXCloudToken;
			this.exporter = exporter;
		}

		@Override
		public void run() {
			Output.debugPrint("Invoking auto-refresh.");
			flushConsole();
			quoteConsoleOutput.invokeSymbolOutput(IEXCloudToken, exporter);
		}
	}

	private static void flushConsole() {
		//System.out.print("\033[H\033[2J");
		Output.clearScreen();
		System.out.flush();
	}
}

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
import java.util.Timer;
import java.util.TimerTask;

import org.fross.library.Debug;
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
	public static final int DEFAULT_TREND_DURATION = 90;

	// Class Variables
	protected static final CommandLineParser cli = new CommandLineParser();
	private static final QuoteConsoleOutput quoteConsoleOutput = new QuoteConsoleOutput(cli);

	/**
	 * Main(): Program entry point
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
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

		// -----------------------------------------------------------------
		// CLI: Debug Switch
		// -----------------------------------------------------------------
		if (cli.clDebug == true) {
			Debug.enable();
		}

		// -----------------------------------------------------------------
		// CLI: list Favorites
		// -----------------------------------------------------------------
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

		// -----------------------------------------------------------------
		// CLI: Export Data
		// -----------------------------------------------------------------
		if (cli.clExport.isEmpty() == false) {
			exporter = new FileExporter(cli.clExport);
		}

		// -----------------------------------------------------------------
		// CLI: Trend Duration
		// If there is no preference, set it to the default. If one is provided set prefs to it
		// -----------------------------------------------------------------
		if (Prefs.queryInt("trendduration") == 0) {
			Prefs.set("trendduration", DEFAULT_TREND_DURATION);
		}
		if (cli.clTrendDuration != 0) {
			Prefs.set("trendduration", cli.clTrendDuration);
			Output.printColorln(Ansi.Color.YELLOW, "Default trend duration has been set for " + cli.clTrendDuration + " days");
			System.exit(0);
		}

		// -----------------------------------------------------------------
		// CLI: Display Version & Latest GitHub Release
		// -----------------------------------------------------------------
		if (cli.clVersion == true) {
			Output.printColorln(Ansi.Color.YELLOW, "\nCurrent Quoter version:   v" + Main.VERSION);
			Output.printColorln(Ansi.Color.WHITE, "Latest Release on GitHub: " + GitHub.updateCheck("quoter"));
			Output.printColorln(Ansi.Color.CYAN, "HomePage: https://github.com/frossm/quoter");
			System.exit(0);
		}

		// -----------------------------------------------------------------
		// CLI: Remove saved securities
		// -----------------------------------------------------------------
		if (cli.clRemoveFavorites == true) {
			Prefs.remove(PREFS_SAVED_SYMBOLS);
			Output.printColor(Ansi.Color.YELLOW, "Saved securities have been removed\n");
			System.exit(0);
		}

		// -----------------------------------------------------------------
		// CLI: Disable color output
		// -----------------------------------------------------------------
		if (cli.clNoColor == true) {
			Output.enableColor(false);
		}

		// -----------------------------------------------------------------
		// CLI: Show Help and Exit
		// -----------------------------------------------------------------
		if (cli.clHelp == true) {
			Help.Display();
			System.exit(0);
		}

		// -----------------------------------------------------------------
		// CLI: In debug mode, show the number of symbols listed on the command line
		// -----------------------------------------------------------------
		Output.debugPrintln("Number of Symbols entered on command line: " + cli.symbolList.size());

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
			Output.debugPrintln("Adding saved symbols: '" + Prefs.queryString(PREFS_SAVED_SYMBOLS) + "'");
			String[] savedSymbols = Prefs.queryString(PREFS_SAVED_SYMBOLS).split(" ");
			for (String i : savedSymbols) {
				if (i != "Error") {
					cli.symbolList.add(i);
				}
			}
		}

		// Fetch and display ticker information
		quoteConsoleOutput.invokeSymbolOutput(exporter);

		// Perform async fetches and display ticker information until user cancels application
		if (cli.clAutoRefresh > 0) {
			Output.debugPrintln("Starting auto-refresh async timer.");
			if (!cli.clExport.isEmpty()) {
				Output.printColorln(Ansi.Color.RED, "Auto-Refresh flag cannot be used when exporting data to file.");
				System.exit(0);
			}
			final FetchLatestTask asyncTimer = new FetchLatestTask(exporter);

			// Schedule the timer. Set it for a bit more than what is selected so it happens after the count down bar
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
		private FileExporter exporter;

		public FetchLatestTask(final FileExporter exporter) {
			this.exporter = exporter;
		}

		@Override
		public void run() {
			Output.debugPrintln("Invoking auto-refresh.");
			flushConsole();
			quoteConsoleOutput.invokeSymbolOutput(exporter);
		}
	}

	private static void flushConsole() {
		Output.clearScreen();
		System.out.flush();
	}
}

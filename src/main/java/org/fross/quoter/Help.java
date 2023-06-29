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

import org.fross.library.Format;
import org.fross.library.Output;
import org.fusesource.jansi.Ansi;

/**
 * Help(): Display the help page when users enters 'h' command.
 * 
 * @author michael.d.fross
 *
 */
public class Help {
	static final int HEADERWIDTH = 72;

	/**
	 * Display(): Prints help in color using the JCDP library in the output module.
	 */
	public static void Display() {
		Output.printColorln(Ansi.Color.CYAN, "\n+" + "-".repeat(HEADERWIDTH - 2) + "+");
		Output.printColorln(Ansi.Color.CYAN, "+" + Format.CenterText(HEADERWIDTH - 2, "Quoter  v" + Main.VERSION) + "+");
		Output.printColorln(Ansi.Color.CYAN, "+" + Format.CenterText(HEADERWIDTH - 2, Main.COPYRIGHT) + "+");
		Output.printColorln(Ansi.Color.CYAN, "+" + "-".repeat(HEADERWIDTH - 2) + "+");
		Output.printColorln(Ansi.Color.CYAN, Format.CenterText(HEADERWIDTH - 2, "Quoter displays stock quotes, US index data, and more"));
		Output.printColorln(Ansi.Color.CYAN, Format.CenterText(HEADERWIDTH - 2, "Hompage: https://github.com/frossm/quoter") + "\n");

		Output.printColorln(Ansi.Color.YELLOW, "Command Line Options");

		Output.printColorln(Ansi.Color.YELLOW, "\nConfiguration:");
		Output.printColorln(Ansi.Color.WHITE, "  -z        Disable colorized output");
		Output.printColorln(Ansi.Color.WHITE, "  -w WIDTH  Width, in columns, of the trending display");
		Output.printColorln(Ansi.Color.WHITE, "  -n        Hide the Index information and just show the stock quotes");
		Output.printColorln(Ansi.Color.WHITE, "  -a SEC    Set a refresh time for quotes in seconds");
		Output.printColorln(Ansi.Color.WHITE, "  -d DAYS   Set duration of trend display. Default is 90 days of historical data");

		Output.printColorln(Ansi.Color.YELLOW, "\nSaved Favorites:");
		Output.printColorln(Ansi.Color.WHITE, "  -s        Save securities provided as favorites and show them automatically");
		Output.printColorln(Ansi.Color.WHITE, "  -l        List currently saved favorites");
		Output.printColorln(Ansi.Color.WHITE, "  -r        Remove saved favorites");
		Output.printColorln(Ansi.Color.WHITE, "  -i        Ignore favorites for this execution");

		Output.printColorln(Ansi.Color.YELLOW, "\nSaved Favorites:");
		Output.printColorln(Ansi.Color.WHITE, "  -t        Display daily trending historical data");
		Output.printColorln(Ansi.Color.WHITE, "  -x FILE   Export data to the provided filename");

		Output.printColorln(Ansi.Color.YELLOW, "\nMisc:");
		Output.printColorln(Ansi.Color.WHITE, "  -D        Start in debug mode and display details for developers");
		Output.printColorln(Ansi.Color.WHITE, "  -v        Display program version and lastest GitHub release and exit");
		Output.printColorln(Ansi.Color.WHITE, "  -? | -h   Display this help information");

		Output.printColorln(Ansi.Color.YELLOW, "\nNotes:");
		Output.printColorln(Ansi.Color.WHITE, "  - Quoter security and index data is pulled from a financial website");
		Output.printColorln(Ansi.Color.WHITE, "  - If the website changes it's structure Quoter could break.  I'll update it should that occur.");

		Output.printColorln(Ansi.Color.YELLOW, "\nSNAP Installation Notes:");
		Output.printColorln(Ansi.Color.WHITE, "  - To export data, you must assign Quoter access to your home directory via:");
		Output.printColorln(Ansi.Color.CYAN, "  - sudo snap connect quoter:home");
	}
}

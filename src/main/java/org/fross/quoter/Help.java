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
		Output.printColorln(Ansi.Color.CYAN, Format.CenterText(HEADERWIDTH - 2, "Quoter is a tool to display stock quotes and index data"));
		Output.printColorln(Ansi.Color.CYAN, Format.CenterText(HEADERWIDTH - 2, "https://github.com/frossm/quoter") + "\n");

		Output.printColorln(Ansi.Color.WHITE, "Command Line Options");

		Output.printColorln(Ansi.Color.YELLOW, "\nConfiguration:");
		Output.printColorln(Ansi.Color.WHITE, "  -b        Use IEXCloud Sandbox for testing instead of production. Must be FIRST switch");
		Output.printColorln(Ansi.Color.WHITE, "  -c        Configure the IEXCloud secret key. See GitHub homepage for details.");
		Output.printColorln(Ansi.Color.WHITE, "  -k        Display the IEXCloud secret key being used");
		Output.printColorln(Ansi.Color.WHITE, "  -s        Save securities provided as favorites and show them automatically");
		Output.printColorln(Ansi.Color.WHITE, "  -l        List currently saved favorites");
		Output.printColorln(Ansi.Color.WHITE, "  -r        Remove saved favorites");
		Output.printColorln(Ansi.Color.WHITE, "  -i        Ignore favorites for this execution");
		Output.printColorln(Ansi.Color.WHITE, "  -z        Disable colorized output");
		Output.printColorln(Ansi.Color.WHITE, "  -w WIDTH  Width, in columns, of the trending display");
		Output.printColorln(Ansi.Color.WHITE, "  -n        Hide the Index information and just show the stock quotes");
		
		Output.printColorln(Ansi.Color.YELLOW, "\nSecurity Information:");
		Output.printColorln(Ansi.Color.WHITE, "  -d        Display more detailed security information");
		Output.printColorln(Ansi.Color.WHITE, "  -t        Include a 3 month historical trend");
		Output.printColorln(Ansi.Color.WHITE, "  -x FILE   Export data to the provided filename");

		Output.printColorln(Ansi.Color.YELLOW, "\nMisc:");
		Output.printColorln(Ansi.Color.WHITE, "  -I        Display IEXCloud credit information for month");
		Output.printColorln(Ansi.Color.WHITE, "  -D        Start in debug mode and display details for developers");
		Output.printColorln(Ansi.Color.WHITE, "  -v        Display program version and lastest GitHub release and exit");
		Output.printColorln(Ansi.Color.WHITE, "  -? | -h   Display this help information");

		Output.printColorln(Ansi.Color.YELLOW, "\nNotes:");
		Output.printColorln(Ansi.Color.WHITE, " - Quoter data is sourced from IEXCloud.io. You'll need, minimally, the free account");
		Output.printColorln(Ansi.Color.WHITE, " - The Index data is pulled from a financial website\n");
	}
}
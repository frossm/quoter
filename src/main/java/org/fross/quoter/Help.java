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

import org.fusesource.jansi.Ansi;
import org.fross.library.Output;

/**
 * Help(): Display the help page when users enters 'h' command.
 * 
 * @author michael.d.fross
 *
 */
public class Help {
	/**
	 * Display(): Prints help in color using the JCDP library in the output module.
	 */
	public static void Display() {
		Output.printColorln(Ansi.Color.CYAN, "\n+----------------------------------------------------------------------+");
		Output.printColorln(Ansi.Color.CYAN, "+                       Quoter v" + Main.VERSION + "  Help                       +");
		Output.printColorln(Ansi.Color.CYAN, "+      " + Main.COPYRIGHT + "      +");
		Output.printColorln(Ansi.Color.CYAN, "+----------------------------------------------------------------------+");
		Output.printColorln(Ansi.Color.CYAN, "         Quoter is a tool to display stock quotes and index data");
		Output.printColorln(Ansi.Color.CYAN, "                    https://github.com/frossm/quoter\n");

		Output.printColorln(Ansi.Color.YELLOW, "Command Line Options:");
		Output.printColorln(Ansi.Color.WHITE, " -c        Configure the IEXCloud secret key. See link above for details.");
		Output.printColorln(Ansi.Color.WHITE, " -x <file> Export results into the specified file in CSV format");
		Output.printColorln(Ansi.Color.WHITE, " -k        Display the IEXCloud secret key being used");
		Output.printColorln(Ansi.Color.WHITE, " -D        Start in debug mode.  Same as using the 'debug' command");
		Output.printColorln(Ansi.Color.WHITE, " -v        Display program version and exit");
		Output.printColorln(Ansi.Color.WHITE, " -? | -h   Display this help information");

		Output.printColorln(Ansi.Color.YELLOW, "\nNote:");
		Output.printColorln(Ansi.Color.WHITE, " - Quoter data is sourced from IEXCloud.io.  You'll need at least the free account");
		Output.printColorln(Ansi.Color.WHITE, " - The Index data is pulled from a financial website\n");
	}
}

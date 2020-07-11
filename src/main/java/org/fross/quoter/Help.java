/**************************************************************************************************************
 * Quoter.jar
 * 
 * Quoter is a command line program that display stock quotes and index data.
 * 
 * License:  
 *  MIT License / https://opensource.org/licenses/MIT
 *  Please see included LICENSE.txt file for additional details
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
		Output.printColorln(Ansi.Color.YELLOW, "\n+----------------------------------------------------------------------+");
		Output.printColorln(Ansi.Color.YELLOW, "+                       Quoter v" + Main.VERSION + "  Help                       +");
		Output.printColorln(Ansi.Color.YELLOW, "+      " + Main.COPYRIGHT + "      +");
		Output.printColorln(Ansi.Color.YELLOW, "+----------------------------------------------------------------------+");
		Output.printColorln(Ansi.Color.WHITE, "         Quoter is a tool to display stock quotes and index data");
		Output.printColorln(Ansi.Color.WHITE, "                    https://github.com/frossm/quoter\n");

		Output.printColorln(Ansi.Color.YELLOW, "Command Line Options:");
		Output.printColorln(Ansi.Color.WHITE, " -c        Configure the IEXCloud secret key. See link above for details.");
		Output.printColorln(Ansi.Color.WHITE, " -e <file> Export results in a CSV file");
		Output.printColorln(Ansi.Color.WHITE, " -k        Display the IEXCloud secret key being used");
		Output.printColorln(Ansi.Color.WHITE, " -D        Start in debug mode.  Same as using the 'debug' command");
		Output.printColorln(Ansi.Color.WHITE, " -v        Display program version and exit");
		Output.printColorln(Ansi.Color.WHITE, " -? | -h   Display this help information");

		Output.printColorln(Ansi.Color.WHITE, "\nNote: Quoter data is sources from IEXCloud.  The Index data is pulled from a financial website.\n");
	}
}

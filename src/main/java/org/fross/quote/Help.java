/**************************************************************************************************************
 * Quote.jar
 * Copyright 2019 Michael Fross, all rights reserved
 * 
 * Quote is a command line program that display stock quotes and index data.
 * 
 * License:  
 *  MIT License / https://opensource.org/licenses/MIT
 *  Please see included LICENSE.txt file for additional details
 *   
 ***************************************************************************************************************/

package org.fross.quote;

import org.fusesource.jansi.Ansi;

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
		Output.PrintColorln(Ansi.Color.YELLOW, "\n+----------------------------------------------------------------------+");
		Output.PrintColorln(Ansi.Color.YELLOW, "+                   Quote v" + Main.VERSION + "  Help Document                   +");
		Output.PrintColorln(Ansi.Color.YELLOW, "+----------------------------------------------------------------------+");
		Output.PrintColorln(Ansi.Color.WHITE, "         Quote is a tool to display stock quotes and index data");
		Output.PrintColorln(Ansi.Color.WHITE, "                    https://github.com/frossm/quote\n");

		Output.PrintColorln(Ansi.Color.YELLOW, "Command Line Options:");
		Output.PrintColorln(Ansi.Color.WHITE, " -c        Configure the IEXCloud secret key. See link above for details.");
		Output.PrintColorln(Ansi.Color.WHITE, " -e <file> Export results in a CSV file");
		Output.PrintColorln(Ansi.Color.WHITE, " -k        Display the IEXCloud secret key being used");
		Output.PrintColorln(Ansi.Color.WHITE, " -D        Start in debug mode.  Same as using the 'debug' command");
		Output.PrintColorln(Ansi.Color.WHITE, " -v        Display program version and exit");
		Output.PrintColorln(Ansi.Color.WHITE, " -? | -h   Display this help information");

		Output.PrintColorln(Ansi.Color.WHITE, "\nNote: Quote data is sources from IEXCloud.  The Index data is pulled from a financial website.\n");
	}
}

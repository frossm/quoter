package org.fross.quote;

import com.diogonunes.jcdp.color.api.Ansi.FColor;

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
		Output.PrintColorln(FColor.YELLOW,
				"\n+----------------------------------------------------------------------+");
		Output.PrintColorln(FColor.YELLOW,
				"+                   Quote v" + Main.VERSION + "  Help Document                   +");
		Output.PrintColorln(FColor.YELLOW, "+----------------------------------------------------------------------+");
		Output.PrintColorln(FColor.WHITE, "         Quote is a tool to display stock quotes and index data");
		Output.PrintColorln(FColor.WHITE, "                   https://bitbucket.org/frossm/quote\n");

		Output.PrintColorln(FColor.YELLOW, "Command Line Options:");
		Output.PrintColorln(FColor.WHITE, " -c        Configure the IEXCloud secret key. See link above for details.");
		Output.PrintColorln(FColor.WHITE, " -e <file> Export results in a CSV file");
		Output.PrintColorln(FColor.WHITE, " -k        Display the IEXCloud secret key being used");
		Output.PrintColorln(FColor.WHITE, " -D        Start in debug mode.  Same as using the 'debug' command");
		Output.PrintColorln(FColor.WHITE, " -v        Display program version and exit");
		Output.PrintColorln(FColor.WHITE, " -? | -h   Display this help information");

		Output.PrintColorln(FColor.WHITE,
				"\nNote: Quote data is sources from IEXCloud.  The Index data is pulled from a financial website.\n");
	}
}
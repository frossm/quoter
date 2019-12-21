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

/******************************************************************************
/* Leverages the JCDP Color library:  
 *   https://github.com/dialex/JCDP
 *   http://dialex.github.io/JCDP/javadoc/
 *   <!-- https://mvnrepository.com/artifact/com.diogonunes/JCDP -->
******************************************************************************/

package org.fross.quote;

import static org.fusesource.jansi.Ansi.*;
import org.fusesource.jansi.Ansi;

public class Output {
	/**
	 * PrintColorln(): Print to the console with the provided foreground color
	 * 
	 * Allowable colors are:
	 * - Ansi.Color.BLACK
	 * - Ansi.Color.RED
	 * - Ansi.Color.GREEN
	 * - Ansi.Color.YELLOW
	 * - Ansi.Color.BLUE
	 * - Ansi.Color.MAGENTA
	 * - Ansi.Color.CYAN
	 * - Ansi.Color.WHITE
	 * - Ansi.Color.DEFAULT
	 * 
	 * @param Color
	 * @param msg
	 */
	public static void PrintColorln(Ansi.Color clr, String msg) {
		System.out.println(ansi().a(Attribute.INTENSITY_BOLD).fg(clr).a(msg).reset());
	}

	/**
	 * Printcolor(): Print to the console without a newline
	 * 
	 * @param Color
	 * @param msg
	 */
	public static void PrintColor(Ansi.Color clr, String msg) {
		System.out.print(ansi().a(Attribute.INTENSITY_BOLD).fg(clr).a(msg).reset());
	}

	/**
	 * println: Basic System.out.println call. It's here so out text output can go
	 * through this function.
	 * 
	 * @param msg
	 */
	public static void Println(String msg) {
		System.out.println(msg);
	}

	/**
	 * Print(): Basic System.out.print call. It's here so out text output can go
	 * through this function.
	 * 
	 * @param msg
	 */
	public static void Print(String msg) {
		System.out.print(msg);
	}

	/**
	 * PrintError(): Display an error message in RED preceded by "ERROR:"
	 * 
	 * @param msg
	 */
	public static void PrintError(String msg) {
		PrintColorln(Ansi.Color.RED, "ERROR:  " + msg);
	}

	/**
	 * FatalError(): Print the provided string in RED and exit the program with the
	 * error code given
	 * 
	 * @param msg
	 * @param errorcode
	 */
	public static void FatalError(String msg, int errorcode) {
		Output.PrintColorln(Ansi.Color.RED, "\nFATAL ERROR: " + msg);
		System.exit(errorcode);
	}
}
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

import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi.Attribute;
import com.diogonunes.jcdp.color.api.Ansi.FColor;

public class Output {
	/**
	 * printColorln: Print to the console with the provided foreground color
	 * Acceptable ColorNames: FColor.BLUE, FColor.CYAN, FColor.GREEN,
	 * FColor.MAGENTA, FColor.NONE, FColor.RED, FColor.WHITE, FColor.YELLOW
	 * 
	 * @param Color
	 * @param msg
	 */
	public static void PrintColorln(FColor clr, String msg) {
		ColoredPrinter cp = new ColoredPrinter.Builder(1, false).foreground(clr).build();
		cp.setAttribute(Attribute.LIGHT);
		cp.println(msg);
		cp.clear();
	}

	/**
	 * printColor: Print to the console with NoNewLine. The provided foreground
	 * color Acceptable ColorNames: FColor.BLUE, FColor.CYAN, FColor.GREEN,
	 * FColor.MAGENTA, FColor.NONE, FColor.RED, FColor.WHITE, FColor.YELLOW
	 * 
	 * @param Color
	 * @param msg
	 */
	public static void PrintColor(FColor clr, String msg) {
		ColoredPrinter cp = new ColoredPrinter.Builder(1, false).foreground(clr).build();
		cp.setAttribute(Attribute.LIGHT);
		cp.print(msg);
		cp.clear();
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
	 * print: Basic System.out.print call. It's here so out text output can go
	 * through this function.
	 * 
	 * @param msg
	 */
	public static void Print(String msg) {
		System.out.print(msg);
	}

	/**
	 * printError: Display an error message in RED preceded by "ERROR:"
	 * 
	 * @param msg
	 */
	public static void PrintError(String msg) {
		ColoredPrinter cp = new ColoredPrinter.Builder(1, false).foreground(FColor.RED).build();
		cp.setAttribute(Attribute.LIGHT);
		cp.println("ERROR:  " + msg);
		cp.clear();
	}

	/**
	 * fatalerror(): Print the provided string in RED and exit the program with the
	 * error code given
	 * 
	 * @param msg
	 * @param errorcode
	 */
	public static void FatalError(String msg, int errorcode) {
		Output.PrintColorln(FColor.RED, "\nFATAL ERROR: " + msg);
		System.exit(errorcode);
	}
}
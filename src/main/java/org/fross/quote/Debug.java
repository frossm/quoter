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

import com.diogonunes.jcdp.color.api.Ansi.FColor;

/**
 * Debug contains static methods to maintain the debug state and display
 * messages when enabled.
 * 
 * @author michael.d.fross
 *
 */
public class Debug {
	// Class Variables
	private static boolean clDebug = false;

	/**
	 * Query(): Query current state of this object's debug setting
	 * 
	 * @return
	 */
	public static boolean Query() {
		return clDebug;
	}

	/**
	 * Enable(): Turn debugging on for this object
	 */
	public static void Enable() {
		clDebug = true;
	}

	/**
	 * Disable(): Disable debugging for this object
	 */
	public static void Disable() {
		clDebug = false;
	}

	/**
	 * Print(): Print the output of the String if debugging is enabled. It displays
	 * in RED using the output module.
	 * 
	 * @param msg
	 */
	public static void Print(String msg) {
		if (clDebug == true) {
			Output.PrintColorln(FColor.RED, "DEBUG:  " + msg);
		}
	}
}
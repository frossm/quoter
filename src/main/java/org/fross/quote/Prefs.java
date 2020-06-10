/**************************************************************************************************************
 * Quote.jar
 * Copyright 2019-2020 Michael Fross, all rights reserved
 * 
 * Quote is a command line program that display stock quotes and index data.
 * 
 * License:  
 *  MIT License / https://opensource.org/licenses/MIT
 *  Please see included LICENSE.txt file for additional details
 *   
 ***************************************************************************************************************/

package org.fross.quote;

import java.util.prefs.Preferences;

/**
 * Prefs: Holds the logic and calls to the java preferences system. Used to save
 * and restore the stacks between sessions.
 * 
 * @author michael.d.fross
 *
 */
public class Prefs {
	// Class Constants
	private static final String PREFS_PATH = "/org/fross/quote";

	// Class Variables
	private static Preferences prefs = Preferences.userRoot().node(PREFS_PATH);

	/**
	 * QueryBoolean(): Returns a boolean preference item
	 * 
	 * @param key
	 * @return
	 */
	public static boolean QueryBoolean(String key) {
		return prefs.getBoolean(key, false);
	}

	/**
	 * QueryDouble(): Returns a Double preference item
	 * 
	 * @param key
	 * @return
	 */
	public static Double QueryDouble(String key) {
		return prefs.getDouble(key, 0);
	}

	/**
	 * QueryString(): Returns a String preference item
	 * 
	 * @param key
	 * @return
	 */
	public static String QueryString(String key) {
		return prefs.get(key, "Error");
	}

	/**
	 * Set Sets a boolean preference
	 * 
	 * @param key
	 * @param value
	 */
	public static void Set(String key, boolean value) {
		prefs.putBoolean(key, value);
	}

	/**
	 * Set(): Sets a integer preference
	 * 
	 * @param key
	 * @param value
	 */
	public static void Set(String key, int value) {
		prefs.putInt(key, value);
	}

	/**
	 * Set(): Sets a double preference
	 * 
	 * @param key
	 * @param value
	 */
	public static void Set(String key, double value) {
		prefs.putDouble(key, value);
	}

	/**
	 * Set(): Sets a string preference
	 * 
	 * @param key
	 * @param value
	 */
	public static void Set(String key, String value) {
		prefs.put(key, value);
	}
}
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
	public static boolean queryBoolean(String key) {
		return prefs.getBoolean(key, false);
	}

	/**
	 * QueryDouble(): Returns a Double preference item
	 * 
	 * @param key
	 * @return
	 */
	public static Double queryDouble(String key) {
		return prefs.getDouble(key, 0);
	}
	
	/**
	 * QueryInt(): Returns an int preference item
	 * 
	 * @param key
	 * @return
	 */
	public static int queryInt(String key) {
		return prefs.getInt(key, 0);
	}	

	/**
	 * QueryString(): Returns a String preference item
	 * 
	 * @param key
	 * @return
	 */
	public static String queryString(String key) {
		return prefs.get(key, "Error");
	}

	/**
	 * Set Sets a boolean preference
	 * 
	 * @param key
	 * @param value
	 */
	public static void set(String key, boolean value) {
		prefs.putBoolean(key, value);
	}

	/**
	 * Set(): Sets a integer preference
	 * 
	 * @param key
	 * @param value
	 */
	public static void set(String key, int value) {
		prefs.putInt(key, value);
	}

	/**
	 * Set(): Sets a double preference
	 * 
	 * @param key
	 * @param value
	 */
	public static void set(String key, double value) {
		prefs.putDouble(key, value);
	}

	/**
	 * Set(): Sets a string preference
	 * 
	 * @param key
	 * @param value
	 */
	public static void set(String key, String value) {
		prefs.put(key, value);
	}
	
	/**
	 * remove(): Remove the provided preference
	 * @param key
	 */
	public static void remove(String key) {
		prefs.remove(key);
	}
}
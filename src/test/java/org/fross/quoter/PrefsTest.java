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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

class PrefsTest {

	// Add and retrieve some preferences to ensure they are working
	@Test
	void test() {
		try {
			// Set one of each type
			Prefs.set("test-boolean", true);
			Prefs.set("test-int", 71);
			Prefs.set("test-double", Double.parseDouble("100.01"));
			Prefs.set("test-string", "hello there");

			// Query to ensure they match
			assertTrue(Prefs.queryBoolean("test-boolean"));
			assertEquals(71, Prefs.queryInt("test-int"));
			assertEquals(100.01, Prefs.queryDouble("test-double"));
			assertEquals("hello there", Prefs.queryString("test-string"));

			// Remove the test preferences
			Prefs.remove("test-boolean");
			Prefs.remove("test-int");
			Prefs.remove("test-double");
			Prefs.remove("test-string");

		} catch (Exception ex) {
			fail(ex.getMessage());
		}

	}

}

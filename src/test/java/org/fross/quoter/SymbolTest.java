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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

class SymbolTest {

	// Look through a list of symbols and ensure all the right fields are present
	@Test
	void testFields() {
		String[] testSymbols = { "IBM", "TSLA", "GOOG" };
		String[] symbolFullName = { "International Business Machines Corp.", "Tesla Inc.", "Alphabet Inc. Cl C" };
		String[] testFields = { "symbol", "latestPrice", "change", "changePercent", "dayHigh", "dayLow", "ytdChangePercent", "oneYearChangePercent",
				"timeStamp", "week52High", "week52Low", "fullname", "status" };

		// Loop through each symbol we are testing
		for (int i = 0; i < testSymbols.length; i++) {
			Symbol symb = new Symbol(testSymbols[i]);

			// Loop through each field and ensure it exists in the currently being tested symbol
			for (int j = 0; j < testFields.length; j++) {
				try {
					assertNotNull(symb.get(testFields[j]));

				} catch (Exception ex) {
					fail("'" + testFields[i] + "' does not exist");
				}
			}

			// Since the names shouldn't change, lets test a few of the fullname fields
			assertEquals(symb.get("fullname"), symbolFullName[i]);
		}
	}
}

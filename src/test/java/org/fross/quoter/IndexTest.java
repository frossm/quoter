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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

class IndexTest {
	// Look through a list of indexes and ensure all the right fields are present
	@Test
	void test() {
		String[] testIndexes = { "DOW", "NASDAQ", "S&P" };
		String[] testFields = { "index", "latestPrice", "change", "changePercent", "ytdChangePercent", "oneYearChangePercent", "timeStamp", "week52High",
				"week52Low", "status" };

		// Loop through each symbol we are testing
		for (int i = 0; i < testIndexes.length; i++) {
			Index idx = new Index(testIndexes[i]);

			// Loop through each field and ensure it exists in the currently being tested index
			for (int j = 0; j < testFields.length; j++) {
				try {
					assertNotNull(idx.get(testFields[j]));

				} catch (Exception ex) {
					fail("'" + testFields[i] + "' does not exist");
				}
			}
		}

	}

}

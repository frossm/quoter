/**************************************************************************************************************
 * Quoter.jar
 * 
 * Quoter is a command line program that display stock quotes and index data.
 * 
 * Copyright (c) 2019-2023 Michael Fross
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class XPathLookupTest {

	// Simple test - make sure each of the xPath hash maps have the right number of fields
	@Test
	void fieldTest() {
		XPathLookup xPathLookup = new XPathLookup();

		assertEquals(9, xPathLookup.symbolClosed.size());
		assertEquals(9, xPathLookup.symbolOpen.size());
		assertEquals(7, xPathLookup.indexClosed.size());
		assertEquals(8, xPathLookup.indexOpen.size());
	}

	// Ensure that some text exists within each lookup value
	@Test
	void contentTest() {
		XPathLookup xPathLookup = new XPathLookup();

		// lookupSymbolClosed
		for (String value : xPathLookup.symbolClosed.keySet())
			assertFalse(xPathLookup.symbolClosed.get(value).isEmpty());

		// lookupSymbolOpen
		for (String value : xPathLookup.symbolOpen.keySet())
			assertFalse(xPathLookup.symbolOpen.get(value).isEmpty());

		// lookupIndexClosed
		for (String value : xPathLookup.indexClosed.keySet())
			assertFalse(xPathLookup.indexClosed.get(value).isEmpty());

		// lookupIndexOpen
		for (String value : xPathLookup.indexOpen.keySet())
			assertFalse(xPathLookup.indexOpen.get(value).isEmpty());

	}

	// Do a few lookups and just make sure we get non-null back
	@Test
	void lookupTests() {
		XPathLookup xPathLookup = new XPathLookup();

		// lookupSymbolClosed
		assertNotNull(xPathLookup.lookupSymbolClosed("latestPrice"));
		assertNotNull(xPathLookup.lookupSymbolClosed("fullname"));
		assertNull(xPathLookup.lookupSymbolClosed("xxx"));
		assertNull(xPathLookup.lookupSymbolClosed("yyy"));

		// lookupSymbolOpen
		assertNotNull(xPathLookup.lookupSymbolOpen("change"));
		assertNotNull(xPathLookup.lookupSymbolOpen("52weekRange"));
		assertNull(xPathLookup.lookupSymbolOpen("xxx"));
		assertNull(xPathLookup.lookupSymbolOpen("yyy"));

		// lookupIndexClosed
		assertNotNull(xPathLookup.lookupIndexClosed("oneYearChangePercent"));
		assertNotNull(xPathLookup.lookupIndexClosed("timeStamp"));
		assertNull(xPathLookup.lookupIndexClosed("xxx"));
		assertNull(xPathLookup.lookupIndexClosed("yyy"));

		// lookupIndexOpen
		assertNotNull(xPathLookup.lookupIndexOpen("marketStatus"));
		assertNotNull(xPathLookup.lookupIndexOpen("52weekRange"));
		assertNull(xPathLookup.lookupIndexOpen("xxx"));
		assertNull(xPathLookup.lookupIndexOpen("yyy"));

	}

}

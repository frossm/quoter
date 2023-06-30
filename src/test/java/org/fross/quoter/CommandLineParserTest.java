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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.beust.jcommander.JCommander;

class CommandLineParserTest {
	// Test all of the short options
	@Test
	void testShortCommandLineArgs() {
		// Test Short Options
		String[] argv1 = { "-z", "-w", "100", "-n", "-a", "60", "-d", "30", "-s", "-l", "-r", "-i", "-t", "-x", "out.csv", "-D", "-v", "-h" };

		CommandLineParser cli = new CommandLineParser();
		JCommander jc = new JCommander();
		jc.setProgramName("Quoter");

		jc = JCommander.newBuilder().addObject(cli).build();
		jc.parse(argv1);

		assertTrue(cli.clNoColor);
		assertEquals(cli.clWidth, 100);
		assertTrue(cli.clHideIndex);
		assertEquals(cli.clAutoRefresh, 60);
		assertEquals(cli.clTrendDuration, 30);
		assertTrue(cli.clSave);
		assertTrue(cli.clListFavorites);
		assertTrue(cli.clRemoveFavorites);
		assertTrue(cli.clIgnoreFavorites);
		assertTrue(cli.clTrend);
		assertEquals(cli.clExport, "out.csv");
		assertTrue(cli.clDebug);
		assertTrue(cli.clVersion);
		assertTrue(cli.clHelp);
	}

	// Test all of the long options
	@Test
	void testLongCommandLineArgs() {
		// Test Long Options
		String[] argv1 = { "--no-color", "--width", "100", "--hide-index", "--auto-refresh", "60", "--trend-duration", "30", "--save", "--list-favorites",
				"--remove-favorites", "--ignore-favorites", "--trend", "--export", "out.csv", "--debug", "--version", "--help" };

		CommandLineParser cli = new CommandLineParser();
		JCommander jc = new JCommander();
		jc.setProgramName("Quoter");

		jc = JCommander.newBuilder().addObject(cli).build();
		jc.parse(argv1);

		assertTrue(cli.clNoColor);
		assertEquals(cli.clWidth, 100);
		assertTrue(cli.clHideIndex);
		assertEquals(cli.clAutoRefresh, 60);
		assertEquals(cli.clTrendDuration, 30);
		assertTrue(cli.clSave);
		assertTrue(cli.clListFavorites);
		assertTrue(cli.clRemoveFavorites);
		assertTrue(cli.clIgnoreFavorites);
		assertTrue(cli.clTrend);
		assertEquals(cli.clExport, "out.csv");
		assertTrue(cli.clDebug);
		assertTrue(cli.clVersion);
		assertTrue(cli.clHelp);
	}

	// Test a mixture of short and long option types
	@Test
	void testMixedCommandLineArgs1() {
		// Test Mix of Options
		String[] argv3 = { "--no-color", "-?", "-s", "--version", "--trend", "-d 45" };

		CommandLineParser cli = new CommandLineParser();
		JCommander jc = new JCommander();
		jc.setProgramName("Quoter");

		jc = JCommander.newBuilder().addObject(cli).build();
		jc.parse(argv3);

		assertTrue(cli.clNoColor);
		assertEquals(cli.clWidth, 120);
		assertFalse(cli.clHideIndex);
		assertEquals(cli.clAutoRefresh, 0);
		assertEquals(cli.clTrendDuration, 0);
		assertTrue(cli.clSave);
		assertFalse(cli.clListFavorites);
		assertFalse(cli.clRemoveFavorites);
		assertFalse(cli.clIgnoreFavorites);
		assertTrue(cli.clTrend);
		assertEquals(cli.clExport, "");
		assertFalse(cli.clDebug);
		assertTrue(cli.clVersion);
		assertTrue(cli.clHelp);
	}

	// Test a mixture of short and long option types
	void testMixedCommandLineArgs2() {
		// Test Mix of Options
		String[] argv4 = { "-w", "400", "--hide-index", "--ignore-favorites", "-x", "testoutput", "-v", "-a", "500", "--debug" };

		CommandLineParser cli = new CommandLineParser();
		JCommander jc = new JCommander();
		jc.setProgramName("Quoter");

		jc = JCommander.newBuilder().addObject(cli).build();
		jc.parse(argv4);

		assertFalse(cli.clNoColor);
		assertEquals(cli.clWidth, 400);
		assertTrue(cli.clHideIndex);
		assertEquals(cli.clAutoRefresh, 500);
		assertEquals(cli.clTrendDuration, 0);
		assertFalse(cli.clSave);
		assertFalse(cli.clListFavorites);
		assertFalse(cli.clRemoveFavorites);
		assertTrue(cli.clIgnoreFavorites);
		assertFalse(cli.clTrend);
		assertEquals(cli.clExport, "testoutput");
		assertTrue(cli.clDebug);
		assertTrue(cli.clVersion);
		assertFalse(cli.clHelp);
	}

}

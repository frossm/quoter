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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.fross.library.Output;
import org.fusesource.jansi.Ansi;
import org.junit.jupiter.api.Test;

class FileExporterTest {

	// Export the symbol data and then read it back in to ensure it's accurate
	@Test
	void testSymbolExport() {
		String testFileName = "target/testSymbol.export";

		// Delete export test file if it exists
		try {
			File file = new File(testFileName);
			if (file.canWrite()) {
				file.delete();
			}
		} catch (Exception ex) {
			Output.printColorln(Ansi.Color.RED, "Error deleting " + testFileName + " during FileExporterTest");
			fail(ex.getMessage());
		}

		// Export to the test file
		FileExporter fe = new FileExporter(testFileName);

		// Create several securities and export it
		String[] testSymbols = { "ACN", "AMZN", "F" };
		for (int i = 0; i < testSymbols.length; i++) {
			fe.exportSecurities(new Symbol(testSymbols[i]));
		}
		fe.close();

		// Lets read the file and ensure that the symbol names exist
		try {
			File file = new File(testFileName);

			// Read all lines from the file into an array list
			ArrayList<String> fileContents = new ArrayList<>(Files.readAllLines(Paths.get(testFileName)));

			// Verify the lines read from the file match the security number + header
			assertEquals(testSymbols.length + 1, fileContents.size());

			// Read each line and verify it against the test symbol array values
			String lineRead = "";
			if (file.canRead() && file.isFile()) {
				for (int i = 1; i < fileContents.size(); i++) {
					if (!fileContents.get(i).isEmpty()) {
						lineRead = fileContents.get(i).toUpperCase();
						assertTrue(lineRead.contains(testSymbols[i - 1].toUpperCase()));
					}
				}
			} else {
				throw new Exception();
			}

		} catch (Exception ex) {
			fail(ex.getMessage());
		}

	}

	// Export the index data and then read it back in to ensure it's accurate
	@Test
	void testIndexExport() {
		String testFileName = "target/testIndex.export";

		// Delete export test file if it exists
		try {
			File file = new File(testFileName);
			if (file.canWrite()) {
				file.delete();
			}
		} catch (Exception ex) {
			Output.printColorln(Ansi.Color.RED, "Error deleting " + testFileName + " during FileExporterTest");
			fail(ex.getMessage());
		}

		// Export to the test file
		FileExporter fe = new FileExporter(testFileName);

		// Create several securities and export it
		String[] testIndexes = { "DOW", "NASDAQ", "S&P" };
		for (int i = 0; i < testIndexes.length; i++) {
			fe.exportIndexes(new Index(testIndexes[i]));
		}
		fe.close();

		// Lets read the file and ensure that the symbol names exist
		try {
			File file = new File(testFileName);

			// Read all lines from the file into an array list
			ArrayList<String> fileContents = new ArrayList<>(Files.readAllLines(Paths.get(testFileName)));

			// Verify the lines read from the file match the security number + header (and the inserted blank lines)
			assertEquals(testIndexes.length + 2, fileContents.size());

			// Read each line and verify it against the test symbol array values
			String lineRead = "";
			if (file.canRead() && file.isFile()) {
				for (int i = 2; i < fileContents.size(); i++) {
					lineRead = fileContents.get(i).toUpperCase();
					assertTrue(lineRead.contains(testIndexes[i - 2].toUpperCase()));
				}
			} else {
				throw new Exception();
			}

		} catch (Exception ex) {
			fail(ex.getMessage());
		}

	}

}

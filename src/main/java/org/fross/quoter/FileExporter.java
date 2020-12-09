/**************************************************************************************************************
 * Quoter.jar
 * 
 * Quoter is a command line program that display stock quotes and index data.
 * 
 *  Copyright (c) 2019 Michael Fross
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.fross.library.Output;
import org.fusesource.jansi.Ansi;

public class FileExporter {
	File exportFile = null;
	FileWriter exportFileFW = null;
	boolean exportSymbolHeaderWritten = false;
	boolean exportIndexHeaderWritten = false;

	/**
	 * FileExporter(): FileExporter Constructor
	 * 
	 * @param fileName
	 */
	public FileExporter(String fileName) {
		// Create the export file
		try {
			this.exportFile = new File(fileName);
			if (this.exportFile.createNewFile() == false) {
				Output.fatalError("Could not create export file: '" + fileName + "'", 4);
			}
		} catch (IOException ex) {
			Output.fatalError("Could not create export file: '" + fileName + "'", 4);
		}

		// Create the FileWriter object for writing to the export file
		try {
			exportFileFW = new FileWriter(this.exportFile);
		} catch (IOException ex) {
			Output.printColorln(Ansi.Color.RED, "Error writing to export file '" + fileName + "'\n" + ex.getMessage());
		}
	}

	/**
	 * canWrite(): Return true or false depending on the ability to write to the export file
	 * 
	 * @return
	 */
	public boolean canWrite() {
		try {
			return exportFile.canWrite();
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * exportSecurities(): Dump the security symbol data to the export file
	 * 
	 * @param symbolData
	 * @return
	 */
	public boolean exportSecurities(Symbol symbolData) {
		try {
			// Export the header row
			List<String> fields = symbolData.queryAllFieldNames();
			if (this.exportSymbolHeaderWritten == false) {
				for (String i : fields) {
					exportFileFW.append(i + ",");
				}
				exportFileFW.append("\n");
				this.exportSymbolHeaderWritten = true;
			}

			// Export the symbol data
			for (String i : fields) {
				// If the data has a ',' in it remove it
				String item = symbolData.query(i).replaceAll(",", "");
				exportFileFW.append(item + ",");
			}
			exportFileFW.append("\n");

		} catch (IOException ex) {
			Output.printColorln(Ansi.Color.RED, "Error writing data to the export file: " + ex.getMessage());
		}

		return true;
	}

	/**
	 * exportIndexes(): Dump the index data to the export file
	 */
	public void exportIndexes(String[] indexData) {
		try {
			// Dump the header information to the export file
			if (this.exportIndexHeaderWritten == false) {
				exportFileFW.append("\nSymbol,Current,Chng,Chng%\n");
				this.exportIndexHeaderWritten = true;
			}

			// Dump the index data
			for (int k = 0; k < indexData.length; k++) {
				exportFileFW.append(indexData[k] + ",");
			}
			exportFileFW.append("\n");

		} catch (IOException ex) {
			Output.printColorln(Ansi.Color.RED, "Error writing to export file: " + ex.getMessage());
		}
	}
	
	/**
	 * close():  Flush and close the export file
	 */
	public void close() {
		try {
			this.exportFileFW.flush();
			this.exportFileFW.close();
		} catch (IOException ex) {
			Output.printColorln(Ansi.Color.RED, "Error closing export file: " + ex.getMessage());
		}
		
	}
	
	/**
	 * queryExportFilename(): Return the name of the export file as a string
	 * @return
	 */
	public String queryExportFilename() {
		return this.exportFile.toString();
	}

}

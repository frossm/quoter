/**************************************************************************************************************
 * Quoter.jar
 * 
 * Quoter is a command line program that display stock quotes and index data.
 * 
 *  Copyright (c) 2019-2023 Michael Fross
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

import java.io.Console;

import org.fross.library.Output;
import org.fusesource.jansi.Ansi;

public class EnterPressed extends Thread {
	boolean enterPressed;

	public EnterPressed() {
		this.enterPressed = false;
	}

	/**
	 * run(): Check to see if enter was pressed and update the class flag
	 * 
	 */
	@Override
	public void run() {
		// Check for the enter key being pressed
		try {
			Console c = System.console();

			if (c.readLine() != null) {
				this.enterPressed = true;
				Output.printColorln(Ansi.Color.DEFAULT, "\nExiting...");

				// This seems sloppy, but having Main check the status wasn't working reliably
				System.exit(0);
			}

		} catch (Exception ex) {
			// Just keeping 'er movin'
		}
	}

	/**
	 * queryEnterPressed(): Return the current state of the flag
	 * 
	 * @return
	 */
	public boolean queryEnterPressed() {
		return this.enterPressed;
	}

}

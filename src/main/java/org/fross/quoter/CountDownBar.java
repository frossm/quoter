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

import static org.fusesource.jansi.Ansi.ansi;

import java.util.concurrent.TimeUnit;

import org.fross.library.Output;
import org.fusesource.jansi.Ansi;

public class CountDownBar extends Thread {
	private int countDown = 30;

	// Constructor: Sets the time for the countdown in seconds
	public CountDownBar(int sec) {
		this.countDown = sec;
	}

	/**
	 * run(): Executes in a thread when the this.start() method is called
	 * 
	 */
	@Override
	public void run() {
		char leftChar = (char) 9617;
		char rightChar = (char) 183;
		int countDownLength = 60;
		int countDown = this.countDown;
		int numSlots = countDownLength / this.countDown;

		while (countDown >= 0) {
			System.out.print(ansi().cursorLeft(5000));		// Use a large number so it hits the front of the line

			Output.printColor(Ansi.Color.WHITE,
					"Refresh in " + String.format("%02d", countDown) + " seconds:  " + "|" + String.valueOf(leftChar).repeat(countDown * numSlots));
			Output.printColor(Ansi.Color.WHITE, String.valueOf(rightChar).repeat(countDownLength - (countDown * numSlots)));
			Output.printColor(Ansi.Color.WHITE, "|");

			// Sleep for 1 second
			try {
				TimeUnit.MILLISECONDS.sleep(1000);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}

			countDown--;
		}
		
		// Stop the count down
		this.interrupt();

	}

}

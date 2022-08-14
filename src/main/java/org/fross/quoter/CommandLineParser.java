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

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;

public class CommandLineParser {
	// ---------------------------------------------------------------------------------------------
	// Define command line options that can be used
	// ---------------------------------------------------------------------------------------------

	@Parameter(names = { "-h", "-?", "--help" }, help = true, description = "Display Quoter help and exit")
	protected boolean clHelp = false;

	@Parameter(names = { "-v", "--version" }, description = "Show current program version and latest release on GitHub")
	protected boolean clVersion = false;

	@Parameter(names = { "-D", "--debug" }, description = "Turn on Debug mode to display extra program information")
	protected boolean clDebug = false;

	@Parameter(names = { "-z", "--no-color" }, description = "Disable colorized output")
	protected boolean clNoColor = false;

	@Parameter(names = { "-b", "--sandbox" }, description = "Leverage IEXCloud Sandbox instead of production")
	protected boolean clSandbox = false;

	@Parameter(names = { "-c", "--configure" }, description = "Configure Quoter with IEXCloud Secret Key")
	protected boolean clConfigure = false;

	@Parameter(names = { "-k", "--key-display" }, description = "Display the current IEXCloud Secret Key and Exit")
	protected boolean clKeyDisplay = false;

	@Parameter(names = { "-n", "--hide-index" }, description = "Do not display index information")
	protected boolean clHideIndex = false;

	@Parameter(names = { "-w", "--width" }, description = "Set screen width in columns for 3 month trend display")
	protected int clWidth = 120;

	@Parameter(names = { "-x", "--export" }, description = "Export data to the provided filename")
	protected String clExport = "";

	@Parameter(names = { "-s", "--save" }, description = " Save securities provided as favorites and show them automatically")
	protected boolean clSave = false;

	@Parameter(names = { "-l", "--list-favorites" }, description = "List currently saved favorites")
	protected boolean clListFavorites = false;

	@Parameter(names = { "-r", "--remove-favorites" }, description = "Remove saved favorites")
	protected boolean clRemoveFavorites = false;

	@Parameter(names = { "-i", "--ignore-favorites" }, description = "Ignore favorites for this execution")
	protected boolean clIgnoreFavorites = false;

	@Parameter(names = { "-d", "--detailed" }, description = "Include more detailed information on each security")
	protected boolean clDetailedOutput = false;

	@Parameter(names = { "-t", "--trend" }, description = "Include more detailed information on each security")
	protected boolean clTrend = false;

	@Parameter(names = { "-I", "--credits" }, description = "Display current IEXCloud credits. They reset monthly")
	protected boolean clIEXCredits = false;

	@Parameter(description = "Stock Symbols")
	protected List<String> symbolList = new ArrayList<>();

}
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

import java.util.ArrayList;
import java.util.List;

import org.fross.library.Output;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

public class CommandLineParser {
	// ---------------------------------------------------------------------------------------------
	// Define command line options that can be used
	// ---------------------------------------------------------------------------------------------

	// Configuration
	@Parameter(names = { "-z", "--no-color" }, description = "Disable colorized output")
	protected boolean clNoColor = false;

	@Parameter(names = { "-w", "--width" }, description = "Set screen width in columns for 3 month trend display")
	protected int clWidth = 120;

	@Parameter(names = { "-n", "--hide-index" }, description = "Do not display index information")
	protected boolean clHideIndex = false;

	@Parameter(names = { "-a", "--auto-refresh" }, description = "Set a refresh time for quotes in seconds", validateWith = AutoRefreshValidator.class)
	protected int clAutoRefresh = 0;

	@Parameter(names = { "-d",
			"--trend-duration" }, description = "Set the number of historical days to include in the trend", validateWith = TrendDurationValidator.class)
	protected int clTrendDuration = 0;

	// Saved Favorites
	@Parameter(names = { "-s", "--save" }, description = " Save securities provided as favorites and show them automatically")
	protected boolean clSave = false;

	@Parameter(names = { "-l", "--list-favorites" }, description = "List currently saved favorites")
	protected boolean clListFavorites = false;

	@Parameter(names = { "-r", "--remove-favorites" }, description = "Remove saved favorites")
	protected boolean clRemoveFavorites = false;

	@Parameter(names = { "-i", "--ignore-favorites" }, description = "Ignore favorites for this execution")
	protected boolean clIgnoreFavorites = false;

	// Security Information
	@Parameter(names = { "-t", "--trend" }, description = "Display daily graph of historical data")
	protected boolean clTrend = false;

	@Parameter(names = { "-x", "--export" }, description = "Export data to the provided filename")
	protected String clExport = "";

	// Misc
	@Parameter(names = { "-D", "--debug" }, description = "Turn on Debug mode to display extra program information")
	protected boolean clDebug = false;

	@Parameter(names = { "-v", "--version" }, description = "Show current program version and the latest release on GitHub")
	protected boolean clVersion = false;

	@Parameter(names = { "-h", "-?", "--help" }, help = true, description = "Display Quoter help and exit")
	protected boolean clHelp = false;

	// Symbols
	@Parameter(description = "Stock Symbols")
	protected List<String> symbolList = new ArrayList<>();

	/** Special Param Validators **/
	final static public class AutoRefreshValidator implements IParameterValidator {
		public AutoRefreshValidator() {
		}

		@Override
		public void validate(String name, String value) throws ParameterException {
			int intVal;
			try {
				intVal = Integer.parseInt(value);
				if (intVal < 1) {
					throw new ParameterException(String.format("Option %s must be a whole number greater than 0 if used. Value Provided: %s", name, value));
				}
			} catch (Exception e) {
				Output.fatalError(String.format("Option %s must be a whole number greater than 0 if used. Value Provided: %s", name, value), 1);
			}
		}
	}

	// Validate the trending duration value provided is between 1 and 99
	final static public class TrendDurationValidator implements IParameterValidator {
		public TrendDurationValidator() {
		}

		@Override
		public void validate(String name, String value) {
			int intVal;
			try {
				intVal = Integer.parseInt(value);
				if (intVal < 1 || intVal > 365) {
					Output.fatalError("Trend duration can not be '" + value + "'.  Value must be between 1 and 365", 1);
				}
			} catch (Exception e) {
				Output.fatalError("Trend duration can not be '" + value + "'.  Value must be between 1 and 365", 1);
			}
		}
	}

}

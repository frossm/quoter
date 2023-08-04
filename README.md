<p align="center"> <img width="60%" src ="https://github.com/frossm/quoter/raw/master/graphics/ScreenShot.jpg"></p> 

# Quoter - The Console Based Stock Quote Tool

<img align="right" width="450" src="https://github.com/frossm/quoter/raw/master/graphics/ScreenShot-Trending.jpg">Quoter is a small command line tool to fetch stock quotes and US index data.  It's a single executable JAR file.  No installation needed, just download the file and run it from the command line with `java -jar quoter.jar`

Quoter simply pulls the stock and index data from a financial website. Because the data iss scraped from a website, if the provider changes the site structure, the is a high likelihood that Quoter will break.  It's not a hard fix usually, but does mean I'll need to update it.  This is why it's always a good idea to be running the latest version.

I live in the US and quoter is configured to pull indexes from the three common US exchanges:  DOW, S&P500, and the NASDAQ.  However, stock values should be able to be pulled from other countries assuming it's supported.

## Program Options

#### Configuration
|Option|Description|
|------|-----------|
|-z | Disable colorized output|
|-w COLUMNS| Set a custom width for the trending display.  This is the number of columns the output should use.  I have Quoter aliased and I call it with my current column width minus a few characters|
|-n| Hide the index display and just show the stock quotes.  If no stocks are provided, then nothing will happen|
|-a SEC| Auto refresh the screen every `SEC` seconds until application is cancelled by hitting `Enter` or `Ctrl-C`. The screen will be cleared at each refresh|
|-d DAYS| Trend duration. Set the number of days to include in the historical trend display `-t`. The default is 90 days so you'll get approximately three months of trend data. This setting is saved in the preferences system and is "sticky". If it's set to 30 days, for example, the next time trend is run it will show 30 days. It can be changed by re-running Quoter with the `-d DAYS` switch. The maximum trending duration is 99 days.|

#### Saved Favorites
|Option|Description|
|------|-----------|
|-s | Save the securities provided into the preferences system as a favorite that will be executed each time Quoter is run.  If the `-t` Trend display is requested, the saved securities will be included.  If an symbol is added on the command line it will be shown along with those that are saved.  If `-s` is provided and there are already saved securities, the current list will overwrite the existing list |
|-l | List the current saved favorites|
|-r | Remove the saved favorites and exit.  If you'd like them back you'll simply need to re-save them with the `-s` flag|
|-i | Ignore saved favorites for this execution.  They will remain saved|

#### Security Information
|Option|Description|
|------|-----------|
|-t|After the initial quote information, display a six month historical view of daily high, low, and close values. Please see the discussion on trending below
|-x FileName| Export the results into the specified file in CSV format.  Note it needs to be a location can can be written to by the user|

#### Miscellaneous
|Option|Description|
|------|-----------|
|-D | Start in Debug Mode which will display additional debugging data. Normally not used|
|-v | Display the current program version as well as check for an updated release on GitHub.  If you installed via the Quoter SNAP, updates will occur automatically and you won't need to do anything.  In fact, snaps are usually at a newer version than the GitHub releases.  If you installed via GitHub, just download the latest .JAR file and replace the old one.  No installation necessary.|
|-h or -?| Display the help page|

## Parameters

No parameters are required.  If none are entered, the program will provide the DOW, NASDAQ, and S&P500 index data. 

However, you probably want some quotes.  Therefore, provide one or more.

Example:

    java -jar quoter.jar amzn msft acn ibm
    java -jar quoter.jar -x outputfile.csv t bp cmcsa ni vz 

## Saved Securities
If you frequently check the same set of securities, as I do, you can save them in your preferences file as facorites so you don't need to type them in each time or setup an alias.  By simply adding `-s` to your quoter command line, the provided securities will be saved overwriting any that were previously saved.  Then by running Quoter, those will be be used in addition to any you've added to the command line.

Running Quoter with `-r` will remove any saved quotes.  Running with `-i` will ignore the saved quotes for that execution. Running with a `-l` will list your current saved favorites.

## Trending
<img align="right" width="200" src="https://github.com/frossm/quoter/raw/master/graphics/PostIt-512x512.jpg">This powerful feature will display the requested (with `-d`) days to trend.  The default is 90 days.   The dates are on the Y axis, an the cost is on the X axis, you just tilt your head to the right (like you were eating a taco) and you'll be set :-)

It's executed by giving Quoter the **`-t`** command line switch.  If there are 5 symbols on the command line, it will trend them all in sequence.

The display will show you the last three months of data with the daily range and the close price.

### SNAP Installation

[![quoter](https://snapcraft.io//quoter/badge.svg)](https://snapcraft.io/quoter)

I would encourage anyone with a supported Linux platform to install Quoter as a snap.  See [Snapcraft Homepage](https://snapcraft.io) for more information. You can download, install, and keep the application up to date automatically by installing the snap via :

``sudo snap install quoter``  (Assuming snap is installed.  Ubuntu has it by default)

This will install the application into a sandbox where it is separate from other applications.  Java is even included in the SNAP package so you don't evey have to have it elsewhere.

SNAPs are easy to install and remove. They are automatically updated and run with everything it needs in the package.  For example, Quoter is a Java application, but you don't have to even  have Java installed on your system as it's packaged within the SNAP.

If you are going to use the export capability, you'll need to give the snap access to your home directory.  This is done after the snap installation by connecting quoter to the home interface.  Simply run the following:

``sudo snap connect quoter:home``

[![Get it from the Snap Store](https://snapcraft.io/static/images/badges/en/snap-store-black.svg)](https://snapcraft.io/quoter)

## Feedback

This is obviously not meant to be a large financial package.  It's just a small utility that I wanted to use to tell me if I'll ever be able to retire :-)   If you have suggestions or idea, please let me know.

quoter at fross dot org

## License

[The MIT License](https://opensource.org/licenses/MIT)

Copyright 2018-2023 by Michael Fross

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

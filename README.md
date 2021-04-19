<p align="center"> <img width="70%" src ="https://github.com/frossm/quoter/blob/master/graphics/ScreenShot.jpg"> </p> 

# Quoter - The Console Based Stock Quote Tool

<img align="right" width="200" src="https://github.com/frossm/quoter/blob/master/graphics/PostIt-512x512.jpg">Quoter is a small command line tool to fetch stock quotes and US index data.  It's a single executable JAR file.  No installation needed, just download the file and run it from the command line with `java -jar quoter.jar`

In order to minimize HTML scraping, it retrieves quotes from [IEXCloud](https://iexcloud.io).  You can signup for free and get 500k stock quotes per month.  Please check their usage agreements prior to signing up and ensure you are allowed to user their service.

After getting an account, log into the IEXCloud dashboard and you can see your API tokens.  You'll need the secret token to use with Quoter.  The secret key starts with ``sk_``

Unfortunately, IEXCloud does not provide index data.  Therefore the DOW, NASDAQ, and S&P500 index data is scraped from a finance website.  I don't really like this, but it seems the only option at this point.  It does mean that there is a likelihood that I'll probably have to keep updating the program if the web page format changes.  If IEXCloud starts to provide these for free, I'll switch over to that.

I live in the US and quoter is configured to pull indexes from the three common US exchanges:  DOW, S&P500, and the NASDAQ.  However, stock values should be able to be pulled for any that IEXCloud supports.  This would include the trending and detailed capabilities.

## Program Setup

Before you can start to use the tool, you'll need to store your API Secret Key.  To do this execute the following:

    java -jar quoter.jar -c

This will prompt you for the key which will be stored in the java preferences system.  On Windows this is the registry.  In Linux it's a hidden directory inside your home directory. Currently it is not encrypted, but I'll need to look into this at some point.  

Note that if Quoter has been installed via a snap, `quoter -c` is all that is needed.

## Program Options

#### Configuration
|Option|Description|
|------|-----------|
|-c | Configure the [IEXCloud.IO](https://iexcloud.io) API key.  If you are in sandbox mode with `-b` it will configure the sandbox key.  Please ensure that the `-b` is entered on the command line prior to the `-c`|
|-k | Display the configured IEX secret API key and exit.  It will display the production key normally, but if we're in sandbox mode (`-b`) it will display the sandbox key|
|-s | Save the securities provided into the preferences system to be executed each time Quoter is run.  If a `-d` Detailed or `-t` Trend is requested, the saved securities will be included.  If a symbol is added on the command line it will be shown along with those that are saved.  If `-s` is provided and there are already saved securities, the current list will overwrite the old list |
|-r | Remove saved securities.  If you'd like them back you'll need to re-save them |
|-i | Ignore saved queries for this execution.  They will remain saved |
|-z | Disable colorized output|
|-b | Use the IEXCloud.io sandbox instead of the production environment.  Note, the `-b` sandbox switch must be the first command line switch.  It's used during development and probably not much use for normal users.  Please note that the sandbox requires a sandbox key, not the production key.  This can be obtained via the IEXCloud dashboard and set with the `-c` command line switch when in sandbox (`-b`) mode|

#### Security Information
|Option|Description|
|------|-----------|
|-d | Display detailed stock information for the symbols provided.  This is simply additional information retreived from IEXCloud|
|-t | After the initial quote information, display a three month historical view of close prices.  Please see the discussion on trending below. Please note that this call is heavily weighted by IEXCLOUD and will use quite a few messages|
|-x FileName| Export the results into the specified file in CSV format.  Note it needs to be a location can can be written to by the user|

#### Miscellaneous
|Option|Description|
|------|-----------|
|-D | Start in Debug Mode which will display additional debugging data. Normally not used|
|-v | Display the version and exit.  The `-v` version check will also check GitHub and display the latest released version|
|-h or -?| Display the help page|

## Parameters

No parameters are required.  If none are entered, the program will provide the DOW, NASDAQ, and S&P500 index data.  

However, you probably want some quotes.  Therefore, provide one or more.  

Example:

    java -jar quoter.jar amzn msft acn ibm
    java -jar quoter.jar -x outputfile.csv t bp cmcsa ni vz 

## Trending
<img align="right" width="300" src="https://github.com/frossm/quoter/blob/master/graphics/ScreenShot-Trending.jpg">This feature will allow for approximately three months of trending.  Quoter will pull the data from IEXCloud.io and show a simple time based graph.  The dates are on the Y axis, an the cost is on the X axis.

It's executed by giving Quoter the **`-t`** command line switch.  If there are 5 symbols on the command line, it will trend them all.

The display will show you the last three months of data with the daily range and the close price.

Please note that this call to IEXCloud is weighted heavily and will use quite a few of your allowed monthly calls.  However, given you get 500,000 calls per month, there is probably plenty if it's not massively over used.


### SNAP Installation

[![quoter](https://snapcraft.io//quoter/badge.svg)](https://snapcraft.io/quoter)

I would encourage anyone with a supported Linux platform to use snap.  See [Snapcraft Homepage](https://snapcraft.io) for more information. You can download, install, and keep the application up to date automatically by installing the snap via :

`sudo snap install quoter`  (Assuming snap is installed)

This will install the application into a sandbox where it is separate from other applications.  Java is even included in the SNAP package so you don't evey have to have it elsewhere.  I do want to look at packaging it via Flatpak as well, but my understanding is that Maven is not well supported.  However, I need to do more investigation.

[![Get it from the Snap Store](https://snapcraft.io/static/images/badges/en/snap-store-black.svg)](https://snapcraft.io/quoter)

## Feedback

This is obviously not meant to be a large financial package.  It's just a small utility that I wanted to use to tell me if I'll ever be able to retire :-)   If you have suggestions or idea, please let me know.  

quoter at fross dot org.

## License

[The MIT License](https://opensource.org/licenses/MIT)

Copyright 2018-2020 by Michael Fross

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

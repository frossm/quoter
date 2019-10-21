
# cal - The Console Calendar Generator

I wanted a small command line program to check stock and index prices throughout the day.  In order to minimize HTML scraping, I decided to use the quotes from IEXCloud.IO.  You can signup for free and get 500k stock quotes per month for free.  Please check their usage agreements prior to signing up and ensure you are allowed to user their service.

After getting an account, log into the dashboard and you can see your API tokens.  You'll need the secret token to use this program.

Unfortunately, IEXCloud does not provide index data.  These are scraped from a finance website.  I don't really like this, but it seems the only option at this point.  It does mean that there is a likelihood that I'll probably have to keep updating the program if the web page format changes.

**Program Setup**

Before you can start to use the tool, you'll need to store your API Secret Key.  To do this execute the following:

    java -jar quote.jar -c

This will prompt you for the key which will be stored in the java preferences system.  On Windows this is the registry.  In Linux it's a hidden directory inside your home directory. Currently it is not encrypted, but I'll need to look into this at some point.

**Program Options**

|Option|Description|
|------|-----------|
|-D | Start in Debug Mode which will display additional debugging data. Normally not used|
|-c | Configure the [IEXCloud.IO](https://iexcloud.io) API key|
|-e | Export the results into a CSV file|
|-v | Display the version and exit|
|-k | Display the configured IEX secret API key and exit|
|-h or -?| Display the help page|

**Parameters**

No parameters are required.  If none are entered, the program will provide the DOW, NASDAQ, and S&P500 index data.  

However, you probably want some quotes.  Therefore, provide one or more.  

Example:

    java -jar quote.jar amzn msft acn ibm

**Feedback**

This is obviously not meant to be a large financial package.  It's just a small utility that I wanted to use to tell me if I'll ever be able to retire :-)  If you have suggestions or idea, please let me know.

**License**

[The MIT License](https://opensource.org/licenses/MIT)

Copyright 2019 by Michael Fross

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

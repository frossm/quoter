package org.fross.quote;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class QuoteOps {

	/**
	 * GetQuote: Get a stock quote from IEXCloud.io and return an array of key data:
	 * 0 = symbol; 1 = latest realtime price; 2 = change; 3 = changePercent; 4 =
	 * dayhigh; 5 = daylow; 6 = year to date change percentage;
	 * 
	 * @param symb
	 * @param Token
	 * @return
	 */
	public static String[] GetQuote(String symb, String token) {
		String QUOTEURLTEMPLATE = "https://cloud.iexapis.com/stable/stock/SYMBOLHERE/quote?token=TOKENHERE";
		String quoteURL = "";
		String quoteDetail = "";
		String[] retArray = new String[9];

		// Get the quote data in JSON format
		Debug.Print("Processing Symbol: '" + symb + "'");
		quoteURL = QUOTEURLTEMPLATE.replaceAll("SYMBOLHERE", symb);
		quoteURL = quoteURL.replaceAll("TOKENHERE", token);
		Debug.Print("Rewritten URL: " + quoteURL);

		try {
			quoteDetail = URLOps.ReadURL(quoteURL);
		} catch (Exception ex) {
			Output.FatalError("Could not contact iexapis.com to retrieve quote: '" + symb + "'\n" + ex.getMessage(), 2);
		}

		// Display the returned JSON data
		// Debug.Print(quoteDetail);

		// Decode the JSON and extract the desired data
		JSONParser jp = new JSONParser();
		try {
			Object obj = jp.parse(quoteDetail);
			JSONObject jo = (JSONObject) obj;

			retArray[0] = jo.get("symbol").toString();
			retArray[1] = jo.get("latestPrice").toString();
			retArray[2] = jo.get("change").toString();
			retArray[3] = jo.get("changePercent").toString();
			retArray[4] = jo.get("high").toString();
			retArray[5] = jo.get("low").toString();
			retArray[6] = jo.get("week52High").toString();
			retArray[7] = jo.get("week52Low").toString();
			retArray[8] = jo.get("ytdChange").toString();

			// If we are in debug mode, display the values we are returning
			if (Debug.Query() == true) {
				Debug.Print("Data Returned from Web:");
				for (int i = 0; i < retArray.length; i++) {
					Debug.Print("    " + i + ": " + retArray[i]);
				}
			}

		} catch (Exception ex) {
			Output.PrintError(ex.getMessage());
		}

		return retArray;
	}

	/**
	 * GetIndex: Returns an array of Strings that contains the Dow, Nasdaq, and S&P
	 * data. Unfortunately I have to scrape a web page for this information as IEX
	 * Cloud does not contain index data.
	 * 
	 * @param idx
	 * @return
	 */
	public static String[] GetIndex(String idx) {
		String[] retArray = new String[4];
		String idxPage;
		String URLTEMPLATE = "https://www.cnbc.com/quotes/?symbol=SYMBOLHERE";
		String URL="ERROR";

		// Ensure a valid value was passed
		if (idx.toUpperCase() == "DOW") {
			URL = URLTEMPLATE.replaceAll("SYMBOLHERE", ".dji");
		} else if (idx.toUpperCase() == "NASDAQ") {
			URL = URLTEMPLATE.replaceAll("SYMBOLHERE", ".ixic");
		} else if (idx.toUpperCase() == "S&P") {
			URL = URLTEMPLATE.replaceAll("SYMBOLHERE", ".inx");
		} else {
			Output.FatalError("Call to GetIndex must be 'DOW', 'NASDAQ', or 'S&P'", 4);
		}

		Debug.Print("Index URL rewritten to: " + URL);

		try {
			// Download the web page with
			idxPage = URLOps.ReadURL(URL);

			retArray[0] = idx;
			retArray[1] = StringUtils.substringBetween(idxPage, "\"last\":\"", "\"");
			retArray[2] = StringUtils.substringBetween(idxPage, "\"change\":\"", "\"");
			retArray[3] = StringUtils.substringBetween(idxPage, "\"change_pct\":\"", "\"");

			// If we are in debug mode, display the values we are returning
			if (Debug.Query() == true) {
				Debug.Print("Index Data Returned from Web:");
				for (int i = 0; i < retArray.length; i++) {
					Debug.Print("    " + i + ": " + retArray[i]);
				}
			}

		} catch (Exception ex) {
			Output.PrintError(ex.getMessage());
		}

		return retArray;
	}

}

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

import java.util.HashMap;
import java.util.Map;

import org.fross.library.Output;
import org.fross.library.URLOperations;
import org.fusesource.jansi.Ansi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class IEXCloudAPICall {
	HashMap<String, String> callData = new HashMap<String, String>();

	public IEXCloudAPICall(String URL, String token) {
		this.callData.put("Status", "ok");
		getData(URL, token);
	}

	protected void getData(String URL, String token) {
		HashMap<String, String> returnMap = new HashMap<String, String>();
		String URLResult = "";

		// Get the data from IEXCloud
		try {
			String updatedURL = URL + "?token=" + token;
			Output.debugPrint("Updated URL for call: " + updatedURL);
			URLResult = URLOperations.ReadURL(updatedURL);
		} catch (Exception ex) {
			returnMap.put("Status", "error");
		}

		// Move JSON into HashMap
		try {
			GsonBuilder builder = new GsonBuilder();
			Gson gson = builder.create();

			@SuppressWarnings("unchecked")
			Map<String, Object> tempMap = gson.fromJson(URLResult, HashMap.class);
			for (Map.Entry<String, Object> i : tempMap.entrySet()) {
				String key = i.getKey();
				try {
					this.callData.put(key, tempMap.get(key).toString());
				} catch (Exception ex) {
					this.callData.put(key, "-");
				}
			}

		} catch (Exception ex) {
			Output.printColorln(Ansi.Color.RED, "Error parsing " + URL);
		}

	}

	/**
	 * get(): Returns security detail based on passed field
	 * 
	 * @param field
	 * @return
	 */
	protected String get(String field) {
		try {
			return this.callData.get(field);
		} catch (Exception ex) {
			Output.fatalError("Could not query '" + field + "' field", 2);
			return "error";
		}
	}

	/**
	 * getAll():  Return HashMap of all data
	 * @return
	 */
	protected HashMap<String, String> getAll() {
		return callData;
	}

}

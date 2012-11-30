/*
* 
* Copyright (C) 2012 Hyuk Don Kwon
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package edu.illinois.whereru;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class HTTPRequest {
	private static final String DEBUG_TAG = "[HTTPRequest]";
	
	// Parsing JSON object from contents returned from DB
	public static JSONObject makeHttpRequest(String urlString, String method,
			List<NameValuePair> params) {

		JSONObject jsonObject = null;

		try {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResponse;

			if (method.equals("POST")) {
				// init post
				HttpPost httpPost = new HttpPost(urlString);
				// set the resource to send
				httpPost.setEntity(new UrlEncodedFormEntity(params));
				// send the request, retrieve response
				httpResponse = httpClient.execute(httpPost);

			} else {
				// GET method
				// formulate url
				String paramString = URLEncodedUtils.format(params, "utf-8");
				urlString += "?" + paramString;
				// init GET
				HttpGet httpGet = new HttpGet(urlString);
				// send the request, retrieve response
				httpResponse = httpClient.execute(httpGet);

			}

			// retrieve content from the response
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream is = httpEntity.getContent();

			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			String line = null;

			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}

			is.close();
			jsonObject = new JSONObject(sb.toString());
		}catch(NullPointerException e){
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonObject;
	}
}

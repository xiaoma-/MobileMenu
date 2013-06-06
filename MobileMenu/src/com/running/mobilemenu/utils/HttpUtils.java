package com.running.mobilemenu.utils;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpUtils {
	public static InputStream getStreamFromURL(String imageURL) {
		InputStream in = null;
		try {
//			URL url = new URL(imageURL);
//			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//			in = connection.getInputStream();
			HttpGet httpRequest = new HttpGet(imageURL);
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);
			HttpEntity entity = response.getEntity();
			BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(entity);
			in = bufferedHttpEntity.getContent();

		} catch (Exception e) {
			// TODOAuto-generatedcatchblock
			e.printStackTrace();
		}
		return in;

	}
}

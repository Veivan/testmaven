package com.ucoz.time;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Utils {
	private static final String USER_AGENT = "Mozilla/5.0";

	public static HttpURLConnection getConnection(final URL url_orig,
			final int timeout_millis, final boolean ignore_ssl_error/*
																	 * , final
																	 * Proxy
																	 * proxy ,
																	 * final
																	 * HostAddressResolver
																	 * resolver
																	 */)
			throws IOException {
		if (url_orig == null)
			return null;
		final HttpURLConnection con;
		final String url_string = url_orig.toString();
		final String host = url_orig.getHost();

		// final String resolved_host = resolver != null ?
		// resolver.resolve(host) : null;
		final String resolved_host = null;

		con = (HttpURLConnection) new URL(
				resolved_host != null ? url_string.replace("://" + host, "://"
						+ resolved_host) : url_string).openConnection();
		// .openConnection(proxy);
		con.setConnectTimeout(timeout_millis);
		if (resolved_host != null) {
			con.setRequestProperty("Host", host);
		}
		con.setInstanceFollowRedirects(false);
		/*
		 * if (ignore_ssl_error) { setIgnoreSSLError(con); }
		 */
		return con;
	}

	public static String GetPageContent(String url) throws Exception {

		HttpClient client = HttpClientBuilder.create().setUserAgent(USER_AGENT)
				.build();
		HttpGet request = new HttpGet(url);

		request.setHeader("User-Agent", USER_AGENT);
		request.setHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		request.setHeader("Accept-Language",
				"ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4,bg;q=0.2");

		HttpResponse response = client.execute(request);
		int responseCode = response.getStatusLine().getStatusCode();

		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		return result.toString();
	}
	
	public static List<NameValuePair> getFormParams(String html, String username,
			String password) throws UnsupportedEncodingException {

		System.out.println("Extracting form's data...");
		Document doc = Jsoup.parse(html);

		// Login form id
		Element loginform = doc.getElementById("oauth_form");
				//.getElementsByClass("LoginForm js-front-signin").first();// 
		Elements inputElements = loginform.getElementsByTag("input");

		List<NameValuePair> paramList = new ArrayList<NameValuePair>();

		for (Element inputElement : inputElements) {
			String key = inputElement.attr("name");
			String value = inputElement.attr("value");

			if (key.equals("session[username_or_email]"))
				value = username;
			else if (key.equals("session[password]"))
				value = password;
			else if (key.equals("remember_me"))
				value = "0";

			paramList.add(new BasicNameValuePair(key, value));
		}
		return paramList;
	}
	
}

package com.ucoz.time;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	public static List<NameValuePair> getFormParams(String html,
			String username, String password)
			throws UnsupportedEncodingException {

		System.out.println("Extracting form's data...");
		Document doc = Jsoup.parse(html);

		// Login form id
		Element loginform = doc.getElementById("oauth_form");
		// .getElementsByClass("LoginForm js-front-signin").first();//
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

	public static String readAuthenticityToken(String html)
			throws UnsupportedEncodingException {

		System.out.println("Extracting authenticity_token...");
		Document doc = Jsoup.parse(html);
		String result = "";
		// Login form id
		Element loginform = doc.getElementById("oauth_form");
		Elements inputElements = loginform.getElementsByTag("input");

		for (Element inputElement : inputElements) {
			String key = inputElement.attr("name");
			String value = inputElement.attr("value");
			if (key.equals("authenticity_token")) {
				result = value;
				break;
			}
		}
		return result;
	}

	public static String readOauthVerifier(String html) {
		Document document = Jsoup.parse(html);
		String result = "";
		Elements metalinks = document.select("meta[http-equiv=refresh]");
		try {
			String content = metalinks.attr("content").split(";")[1];
			Pattern pattern = Pattern.compile(".*oauth_verifier=?(.*)$",
					Pattern.CASE_INSENSITIVE);
			Matcher m = pattern.matcher(content);
			result = m.matches() ? m.group(1) : null;
			System.out.println(result);
			return result;
		} catch (Exception e) {
			return null;
		}
	}

	public static void Save2file(String buffer, String filename)
			throws Exception {
		BufferedWriter bwr = new BufferedWriter(new FileWriter(new File(
				filename)));
		// write contents of StringBuffer to a file
		bwr.write(buffer);
		// flush the stream
		bwr.flush();
		// close the stream
		bwr.close();
	}
}

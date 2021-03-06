package com.ucoz.time;

import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.nodes.Element;

import twitter4j.HttpParameter;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;

public class Twitter4jEx {

	private static String CONSUMER_KEY;
	private static String CONSUMER_SECRET;
	private static String USER;
	private static String USER_PASS;

	private static String ACCESS_TOKEN;
	private static String ACCESS_TOKEN_SECRET;
	
	public static final String DEFAULT_OAUTH_CALLBACK = "http://www.ya.ru"; 
	private static final String USER_AGENT = "Mozilla/5.0";
	private String cookies;
	 
//	 HttpClient client = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).setUserAgent(USER_AGENT).build();
	//HttpClient client = HttpClientBuilder.create().setUserAgent(USER_AGENT).build();

	 private static HttpClient client = HttpClientBuilder.create().setUserAgent(USER_AGENT).build();

	public Twitter4jEx() {
	}

	public static void OperateWithAuthToken() throws IOException,
			TwitterException {

		ReadINI();

		// The factory instance is re-useable and thread safe.
		TwitterFactory factory = new TwitterFactory();
		AccessToken accessToken = new AccessToken(ACCESS_TOKEN,
				ACCESS_TOKEN_SECRET);
		Twitter twitter = factory.getInstance();
		twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
		twitter.setOAuthAccessToken(accessToken);

		// Now let's go and ask for a protected resource!
		System.out.println("Now we're going to access a protected resource...");
		Status status = twitter.updateStatus("2sleep2");
		System.out.println("Successfully updated the status to ["
				+ status.getText() + "].");
		System.exit(0);
	}

	private static void ReadINI() throws FileNotFoundException, IOException {
		Properties props = new Properties();
		props.load(new FileInputStream(new File("example.ini")));
		CONSUMER_KEY = props.getProperty("CONSUMER_KEY");
		CONSUMER_SECRET = props.getProperty("CONSUMER_SECRET");
		USER = props.getProperty("USER");
		USER_PASS = props.getProperty("USER_PASS");
		ACCESS_TOKEN = props.getProperty("ACCESS_TOKEN");
		ACCESS_TOKEN_SECRET = props.getProperty("ACCESS_TOKEN_SECRET");
	}

	public static void SetPropsINI(AccessToken accessToken)
			throws FileNotFoundException, IOException {
		String fname = "example.ini";
		File file = new File(fname);
		Properties props = new Properties();
		props.load(new FileInputStream(file));

		OutputStream os = null;
		try {
			props.setProperty("ACCESS_TOKEN", accessToken.getToken());
			props.setProperty("ACCESS_TOKEN_SECRET",
					accessToken.getTokenSecret());
			os = new FileOutputStream(file);
			props.store(os, "twitter4j.properties");
			os.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(-1);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException ignore) {
				}
			}
		}
	}

// This works when callback url not set in app
	public void getOAuthAccessToken() {
		try {
			ReadINI();
			Twitter twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
			RequestToken requestToken = twitter.getOAuthRequestToken();
			System.out.println("Got request token.");
			System.out.println("Request token: " + requestToken.getToken());
			System.out.println("Request token secret: "
					+ requestToken.getTokenSecret());
			AccessToken accessToken = null;

			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			while (null == accessToken) {
				System.out
						.println("Open the following URL and grant access to your account:");
				System.out.println(requestToken.getAuthorizationURL());
				try {
					Desktop.getDesktop().browse(
							new URI(requestToken.getAuthorizationURL()));
				} catch (UnsupportedOperationException ignore) {
				} catch (IOException ignore) {
				} catch (URISyntaxException e) {
					throw new AssertionError(e);
				}
				System.out
						.print("Enter the PIN(if available) and hit enter after you granted access.[PIN]:");
				String pin = br.readLine();
				try {
					if (pin.length() > 0) {
						accessToken = twitter.getOAuthAccessToken(requestToken,
								pin);
					} else {
						accessToken = twitter.getOAuthAccessToken(requestToken);
					}
				} catch (TwitterException te) {
					if (401 == te.getStatusCode()) {
						System.out.println("Unable to get the access token.");
					} else {
						te.printStackTrace();
					}
				}
			}
			System.out.println("Got access token.");
			System.out.println("Access token: " + accessToken.getToken());
			System.out.println("Access token secret: "
					+ accessToken.getTokenSecret());
			SetPropsINI(accessToken);
			System.exit(0);
		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to get accessToken: " + te.getMessage());
			System.exit(-1);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.out.println("Failed to read the system input.");
			System.exit(-1);
		}
	}

	// Getting token without PIN
	public void getOAuthAccessTokenSilent() throws Exception {
		try {
			ReadINI();
			Twitter twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
			final RequestToken requestToken = twitter.getOAuthRequestToken(DEFAULT_OAUTH_CALLBACK);
			//final RequestToken requestToken = twitter.getOAuthRequestToken();
			final String oauth_token = requestToken.getToken();
			System.out.println("Got request token.");
			System.out.println("Request token: " + oauth_token);
			System.out.println("Request token secret: "
					+ requestToken.getTokenSecret());
			AccessToken accessToken = null;

			
			System.out.println("AuthorizationURL : " + requestToken.getAuthorizationURL());
			/*InputStream stream1 = getHTTPContent(requestToken.getAuthorizationURL(), false, null);
			String page = ReadStream(stream1); */
			
			// make sure cookies is turn on
			CookieHandler.setDefault(new CookieManager());

			String page = GetPageContent(requestToken.getAuthorizationURL());
//			List<NameValuePair> postParams = Utils.getFormParams(page, USER, USER_PASS);

			String authenticity_token = Utils.readAuthenticityToken(page);
			if (authenticity_token.isEmpty())
				throw new AuthenticationException(
						"Cannot get authenticity_token.");

			final Configuration conf = twitter.getConfiguration();
			System.out.println("OAuthAuthorizationURL : " + conf.getOAuthAuthorizationURL());

			/*final HttpParameter[] params = new HttpParameter[4];
			params[0] = new HttpParameter("authenticity_token",
					authenticity_token);
			params[1] = new HttpParameter("oauth_token", oauth_token);
			params[2] = new HttpParameter("session[username_or_email]", USER);
			params[3] = new HttpParameter("session[password]", USER_PASS);
			InputStream stream = getHTTPContent(
					conf.getOAuthAuthorizationURL().toString(), true, params);

			String page2 = ReadStream(stream);
			Utils.Save2file(page2, "d:/demo.txt"); */

			// readCallbackURL(getHTTPContent(conf.getOAuthAuthorizationURL().toString(),
			// true, params));
			
			List<NameValuePair> paramList = new ArrayList<NameValuePair>();

			paramList.add(new  BasicNameValuePair("oauth_token", URLEncoder.encode(oauth_token, "UTF-8")));
			paramList.add(new  BasicNameValuePair("session[username_or_email]", URLEncoder.encode(USER, "UTF-8")));
			paramList.add(new  BasicNameValuePair("session[password]", URLEncoder.encode(USER_PASS, "UTF-8")));
			paramList.add(new  BasicNameValuePair("authenticity_token", URLEncoder.encode(authenticity_token, "UTF-8")));

			String page2 = sendPost(conf.getOAuthAuthorizationURL().toString(), paramList);

			final String oauth_verifier = Utils.readOauthVerifier(page2);
			// parseParameters(callback_url.substring(callback_url.indexOf("?")
			// + 1)).get(OAUTH_VERIFIER);

			if (oauth_verifier.isEmpty())
				throw new AuthenticationException("Cannot get OAuth verifier.");

			try {
				accessToken = twitter.getOAuthAccessToken(requestToken,
						oauth_verifier);
			} catch (TwitterException te) {
				if (401 == te.getStatusCode()) {
					System.out.println("Unable to get the access token.");
				} else {
					te.printStackTrace();
				}
			}

			System.out.println("Got access token.");
			System.out.println("Access token: " + accessToken.getToken());
			System.out.println("Access token secret: "
					+ accessToken.getTokenSecret());
			SetPropsINI(accessToken);
			System.exit(0);
		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to get accessToken: " + te.getMessage());
			System.exit(-1);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.out.println("Failed to read the system input.");
			System.exit(-1);
		}
	}

	public String GetPageContent(String url) throws Exception {
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


		// set cookies
		setCookies(response.getFirstHeader("set-cookie") == null ? ""
				: collectCookiesresponse(response.getHeaders("set-cookie")));

		return result.toString();


	}

	private static String collectCookiesresponse(Header[] headers) {
		StringBuilder result = new StringBuilder();
		for (Header header : headers) {
			if (result.length() == 0) {
				result.append(header.toString());
			} else {
				result.append(";" + header.getValue());
			}
		}
		return result.toString();
	}

	private String sendPost(String url, List<NameValuePair> postParams)
			throws Exception {
		
		HttpResponse response = null;
		String result = "";
		
		try {
			HttpPost post = new HttpPost(url);

			// add header
			post.setHeader("Host", "twitter.com");
			//post.setHeader("User-Agent", USER_AGENT);
			post.setHeader("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			post.setHeader("Accept-Language",
					"ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4,bg;q=0.2");
			post.setHeader("Cookie", getCookies());
			post.setHeader("Connection", "keep-alive");
			post.setHeader("Referer", "https://twitter.com");
			post.setHeader("Content-Type", "application/x-www-form-urlencoded");

			post.setEntity(new UrlEncodedFormEntity(postParams, "UTF-8"));

			response = client.execute(post);

			int responseCode = response.getStatusLine().getStatusCode();

			System.out.println("\nSending 'POST' request to URL : " + url);
			System.out.println("Post parameters : " + postParams);
			System.out.println("Response Code : " + responseCode);

			result = ReadStream(response.getEntity().getContent());
			
/*			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			} */

			//Utils.Save2file(result.toString(), "d:/demo.txt");
			Utils.Save2file(result, "d:/demo.txt");

           // String cooky = collectCookiesresponse(response.getHeaders("set-cookie"));
		//	System.out.println(cooky);

		} finally {
			/*if (response != null) {
				response.close();
			} */
		}

		// System.out.println(result.toString());
		return result;
	}
	
	public String getCookies() {
		return cookies;
	}

	public void setCookies(String cookies) {
		this.cookies = cookies;
	}

	private InputStream getHTTPContent(final String url_string,
			final boolean post, final HttpParameter[] params)
			throws IOException {
		final URL url = new URL(url_string);

		int connection_timeout = 100;
		final String user_agent = "Mozilla/5.0";

		boolean ignore_ssl_error = true;
		final HttpURLConnection conn = Utils.getConnection(url,
				connection_timeout, ignore_ssl_error /* , proxy, resolver */);
		if (conn == null)
			return null;
		conn.addRequestProperty("User-Agent", user_agent);
		conn.setRequestMethod(post ? "POST" : "GET");
		if (post && params != null) {
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			final String postParam = HttpParameter.encodeParameters(params);
			final byte[] bytes = postParam.getBytes("UTF-8");
			conn.setRequestProperty("Content-Length",
					Integer.toString(bytes.length));
			conn.setDoOutput(true);
			final OutputStream os = conn.getOutputStream();
			os.write(bytes);
			os.flush();
			os.close();
		}
		return conn.getInputStream();
	}

	private String ReadStream(InputStream inputStream) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(inputStream);
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		int result = bis.read();
		while (result != -1) {
			buf.write((byte) result);
			result = bis.read();
		}
		return buf.toString();
	}

}

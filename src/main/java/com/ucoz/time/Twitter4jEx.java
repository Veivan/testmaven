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
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthenticationException;

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
	
	public static final String PROTOCOL_TWIDERE = "twid" + "://"; 	 
	public static final String DEFAULT_OAUTH_CALLBACK = PROTOCOL_TWIDERE + "com.twitter.oauth/"; 
	 
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
		Status status = twitter.updateStatus("2sleep");
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
			final String oauth_token = requestToken.getToken();
			System.out.println("Got request token.");
			System.out.println("Request token: " + oauth_token);
			System.out.println("Request token secret: "
					+ requestToken.getTokenSecret());
			AccessToken accessToken = null;

			
			System.out.println("AuthorizationURL : " + requestToken.getAuthorizationURL());
			InputStream stream1 = getHTTPContent(requestToken.getAuthorizationURL(), false, null);
			String page = ReadStream(stream1);
			
			//String page = Utils.GetPageContent(requestToken.getAuthorizationURL());
			// List<NameValuePair> postParams = Utils.getFormParams(page, USER,
			// USER_PASS);

			String authenticity_token = Utils.readAuthenticityToken(page);
			if (authenticity_token.isEmpty())
				throw new AuthenticationException(
						"Cannot get authenticity_token.");

			final Configuration conf = twitter.getConfiguration();
			System.out.println("OAuthAuthorizationURL : " + conf.getOAuthAuthorizationURL());

			final HttpParameter[] params = new HttpParameter[4];
			params[0] = new HttpParameter("authenticity_token",
					authenticity_token);
			params[1] = new HttpParameter("oauth_token", oauth_token);
			params[2] = new HttpParameter("session[username_or_email]", USER);
			params[3] = new HttpParameter("session[password]", USER_PASS);
			InputStream stream = getHTTPContent(
					conf.getOAuthAuthorizationURL().toString(), true, params);

			String page2 = ReadStream(stream);
			Utils.Save2file(page2, "d:/demo.txt");

			// readCallbackURL(getHTTPContent(conf.getOAuthAuthorizationURL().toString(),
			// true, params));

			final String oauth_verifier = "qq";
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

	/*
	 * private void readAuthenticityToken(final InputStream stream) throws
	 * SAXException, IOException { final InputSource source = new
	 * InputSource(stream); final Parser parser = new Parser();
	 * parser.setProperty(Parser.schemaProperty, HtmlParser.schema);
	 * parser.setContentHandler(mAuthenticityTokenHandler);
	 * parser.parse(source); }
	 */

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

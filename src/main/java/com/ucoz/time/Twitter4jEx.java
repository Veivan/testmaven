package com.ucoz.time;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class Twitter4jEx {

	private static String CONSUMER_KEY;
	private static String CONSUMER_SECRET;
	private static String USER;
	private static String USER_PASS;

	private static String oauth_token;
	private static String oauth_token_secret;

	public Twitter4jEx() {
	}

	private static void ReadINI() throws FileNotFoundException, IOException {
		Properties props = new Properties();
		props.load(new FileInputStream(new File("example.ini")));
		CONSUMER_KEY = props.getProperty("CONSUMER_KEY");
		CONSUMER_SECRET = props.getProperty("CONSUMER_SECRET");
		USER = props.getProperty("USER");
		USER_PASS = props.getProperty("USER_PASS");
		oauth_token = props.getProperty("oauth_token");
		oauth_token_secret = props.getProperty("oauth_token_secret");
	}

	public static void SetPropsINI() throws FileNotFoundException, IOException {
		// public static void SetPropsINI(AccessToken accessToken) throws
		// FileNotFoundException, IOException {

		String fname = "example.ini";
		File file = new File(fname);
		Properties props = new Properties();
		props.load(new FileInputStream(file));

		OutputStream os = null;
		try {
			props.setProperty("oauth_token", "qq1");
			props.setProperty("oauth_token_secret", "qq2");
			// props.setProperty("oauth_token", accessToken.getToken());
			// props.setProperty("oauth_token_secret",
			// accessToken.getTokenSecret());
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
			Twitter twitter = new TwitterFactory().getInstance();
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

			// System.exit(0);
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
	 * public AccessToken getOAuthAccessToken(final String username, final
	 * String password) throws AuthenticationException,
	 * OAuthPasswordAuthenticator.CallbackURLException { authenticity_token =
	 * null; callback_url = null; try { final RequestToken request_token =
	 * twitter.getOAuthRequestToken(DEFAULT_OAUTH_CALLBACK); final String
	 * oauth_token = request_token.getToken();
	 * readAuthenticityToken(getHTTPContent(request_token.getAuthorizationURL(),
	 * false, null)); if (authenticity_token == null) throw new
	 * AuthenticationException("Cannot get authenticity token."); final
	 * Configuration conf = twitter.getConfiguration(); final HttpParameter[]
	 * params = new HttpParameter[4]; params[0] = new
	 * HttpParameter("authenticity_token", authenticity_token); params[1] = new
	 * HttpParameter("oauth_token", oauth_token); params[2] = new
	 * HttpParameter("session[username_or_email]", username); params[3] = new
	 * HttpParameter("session[password]", password);
	 * readCallbackURL(getHTTPContent
	 * (conf.getOAuthAuthorizationURL().toString(), true, params)); if
	 * (callback_url == null) throw new CallbackURLException(); if
	 * (!callback_url.startsWith(DEFAULT_OAUTH_CALLBACK)) throw new
	 * IOException("Wrong OAuth callback URL " + callback_url); final String
	 * oauth_verifier =
	 * parseParameters(callback_url.substring(callback_url.indexOf("?") +
	 * 1)).get( OAUTH_VERIFIER); if (isNullOrEmpty(oauth_verifier)) throw new
	 * AuthenticationException("Cannot get OAuth verifier."); return
	 * twitter.getOAuthAccessToken(request_token, oauth_verifier); } catch
	 * (final IOException e) { throw new AuthenticationException(e); } catch
	 * (final SAXException e) { throw new AuthenticationException(e); } catch
	 * (final TwitterException e) { throw new AuthenticationException(e); }
	 * catch (final NullPointerException e) { throw new
	 * AuthenticationException(e); } }
	 */

}

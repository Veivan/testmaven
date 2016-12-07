package com.ucoz.time;

import java.util.Properties;
import java.util.Scanner;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TwitterExample {

	private static final String PROTECTED_RESOURCE_URL = "https://api.twitter.com/1.1/account/verify_credentials.json";

	private static String CONSUMER_KEY;
	private static String CONSUMER_SECRET;
	private static String USER;
	private static String USER_PASS;

	private static String oauth_token;
	private static String oauth_token_secret;

	public TwitterExample() {
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

	public static void OperateWithAuthToken() throws IOException {

		ReadINI();

		final OAuth10aService service = new ServiceBuilder()
				.apiKey(CONSUMER_KEY).apiSecret(CONSUMER_SECRET)
				.build(TwitterApi.instance());

		final OAuth1AccessToken accessToken = new OAuth1AccessToken(oauth_token, oauth_token_secret);

		// Now let's go and ask for a protected resource!
		System.out.println("Now we're going to access a protected resource...");
		final OAuthRequest request = new OAuthRequest(Verb.GET,
				PROTECTED_RESOURCE_URL, service);
		service.signRequest(accessToken, request);
		final Response response = request.send();
		System.out.println("Got it! Lets see what we found...");
		System.out.println();
		System.out.println(response.getBody());

		System.out.println();
		System.out
				.println("That's it man! Go and build something awesome with ScribeJava! :)");
	}

	public static void twgo() throws IOException {

		ReadINI();

		final OAuth10aService service = new ServiceBuilder()
				.apiKey(CONSUMER_KEY).apiSecret(CONSUMER_SECRET)
				.build(TwitterApi.instance());
		final Scanner in = new Scanner(System.in);

		System.out.println("=== Twitter's OAuth Workflow ===");
		System.out.println();

		// Obtain the Request Token
		System.out.println("Fetching the Request Token...");
		final OAuth1RequestToken requestToken = service.getRequestToken();
		System.out.println("Got the Request Token!");
		System.out.println();

		System.out.println("Now go and authorize ScribeJava here:");
		System.out.println(service.getAuthorizationUrl(requestToken));
		System.out.println("And paste the verifier here");
		System.out.print(">>");
		final String oauthVerifier = in.nextLine();
		System.out.println();

		// Trade the Request Token and Verfier for the Access Token
		System.out.println("Trading the Request Token for an Access Token...");
		final OAuth1AccessToken accessToken = service.getAccessToken(
				requestToken, oauthVerifier);
		System.out.println("Got the Access Token!");
		System.out.println("(if your curious it looks like this: "
				+ accessToken + ", 'rawResponse'='"
				+ accessToken.getRawResponse() + "')");
		System.out.println();

		// Now let's go and ask for a protected resource!
		System.out.println("Now we're going to access a protected resource...");
		final OAuthRequest request = new OAuthRequest(Verb.GET,
				PROTECTED_RESOURCE_URL, service);
		service.signRequest(accessToken, request);
		final Response response = request.send();
		System.out.println("Got it! Lets see what we found...");
		System.out.println();
		System.out.println(response.getBody());

		System.out.println();
		System.out
				.println("That's it man! Go and build something awesome with ScribeJava! :)");
		in.close();
	}
}
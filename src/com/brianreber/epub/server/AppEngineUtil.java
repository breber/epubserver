package com.brianreber.epub.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class AppEngineUtil {
	/**
	 * Get the id of the currently logged in user
	 * 
	 * @return the id of the currently logged in user
	 */
	public static String getUserId() {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		if (user != null) {
			return user.getUserId();
		} else {
			return null;
		}
	}

	/**
	 * Gets the email address of the currently logged in user
	 * 
	 * @return the email address of the currently logged in user
	 */
	public static String getUserEmail() {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		if (user != null) {
			return user.getEmail();
		} else {
			return null;
		}
	}

	/**
	 * Reads an entire input stream as a String. Closes the input stream.
	 */
	public static String readStreamAsString(InputStream in) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
			byte[] buffer = new byte[1024];
			int count;
			do {
				count = in.read(buffer);
				if (count > 0) {
					out.write(buffer, 0, count);
				}
			} while (count >= 0);
			return out.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("The JVM does not support the compiler's default encoding.", e);
		} catch (IOException e) {
			return null;
		} finally {
			try {
				in.close();
			} catch (IOException ignored) {	}
		}
	}
}

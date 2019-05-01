package br.com.leoneperdigao.wrappers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class UrlWrapper {

	private URL url;

	public UrlWrapper(String spec) throws MalformedURLException {
		url = new URL(spec);
	}

	public URLConnection openConnection() throws IOException {
		return url.openConnection();
	}
}
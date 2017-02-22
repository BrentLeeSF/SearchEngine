import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An example class designed to make fetching the results of different HTTP
 * operations easier.
 */
public class HTTPFetcher {

	/** Port used by socket. For web servers, should be port 80. */
	public static final int PORT = 80;

	/** Version of HTTP used and supported. */
	public static final String version = "HTTP/1.1";

	// See: http://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html#sec5.1.1
	/** Valid HTTP method types. */
	public static enum HTTP {
		OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, CONNECT
	};


	/**
	 * Crafts a minimal HTTP/1.1 request for the provided method.
	 *
	 * @param url - url to fetch
	 * @param type - HTTP method to use
	 *
	 * @return HTTP/1.1 request
	 *
	 * @see {@link HTTP}
	 */
	public static String craftHTTPRequest(URL url, HTTP type) {

		String host = url.getHost(); 
		String resource = url.getFile().isEmpty() ? "/" : url.getFile(); 

		return String.format(
				"%s %s %s\n" +
						"Host: %s\n" +
						"Connection: close\n" +
						"\r\n",
						type.name(), resource, version, host);

		// it returns string with format that will be sent to server by client
		// to access specific resource (web page, image file, file, etc)
	}



	/**
	 * Fetches the HTML for the specified URL (without headers).
	 *
	 * @param url - url to fetch
	 * @return HTML as a single {@link String}, or null if not HTML
	 *
	 * @throws UnknownHostException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static String fetchHTML(URL target)
			throws UnknownHostException, MalformedURLException, IOException
	{

		// gets HTTP request with host, webFileName, HTTP type (GET, POST), and version (HTTP 1.1)
		String request = craftHTTPRequest(target, HTTP.GET);

		// connect to the web server and fetch the content of the webpage
		List<String> lines = fetchLines(target, request);

		int start = 0;
		int end = lines.size();

		// Determines start of HTML versus headers.Where headers end
		while (!lines.get(start).trim().isEmpty() && start < end) {
			start++;
		}

		Map<String, String> fields = parseHeaders(lines.subList(0, start + 1));
		String type = fields.get("Content-Type");

		if (type != null && type.toLowerCase().contains("html")) {
			return String.join(System.lineSeparator(), lines.subList(start + 1, end));
		}

		return null;
	}


	/**
	 * Will connect to the web server and fetch the URL using the HTTP
	 * request provided. It would be more efficient to operate on each
	 * line as returned instead of storing the entire result as a list.
	 *
	 * @param url - url to fetch
	 * @param request - full HTTP request
	 *
	 * @return the lines read from the web server
	 *
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public static List<String> fetchLines(URL url, String request)
			throws UnknownHostException, IOException
	{
		ArrayList<String> lines = new ArrayList<>();

		try (
				// create a socket to access server
				Socket socket = new Socket(url.getHost(), PORT); 

				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				// sends information to server. We send request
				PrintWriter writer = new PrintWriter(socket.getOutputStream());
				) {
			
			// prints to server
			writer.println(request);
			writer.flush();

			String line = null;

			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
		}
		return lines;
	}


	/**
	 * Helper method that parses HTTP headers into a map where the KEY IS FIELD NAME 
	 * (key = Status (value = 200,404), key = ContentType (value = html,mp4), key = ServerOS (value = Windows, Mac OS X) 
	 * and the value is the field value (). The status code
	 * will be stored under the key "Status".
	 *
	 * @param headers - HTTP/1.1 header lines
	 * @return field names mapped to values if the headers are properly formatted
	 */
	public static Map<String, String> parseHeaders(List<String> headers) {

		// create a map to get key/value of server, content-type, status, etc
		Map<String, String> fields = new HashMap<>();

		if (headers.size() > 0 && headers.get(0).startsWith(version)) {

			/** Version of HTTP used and supported. HTTP 1.1 */
			fields.put("Status", headers.get(0).substring(version.length()).trim());

			for (String line : headers.subList(1, headers.size())) {
				String[] pair = line.split(":", 2);

				if (pair.length == 2) {
					fields.put(pair[0].trim(), pair[1].trim());
				}
			}
		}
		return fields;
	}

}
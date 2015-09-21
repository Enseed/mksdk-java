package com.enseed.mksdk.core.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.enseed.mksdk.core.json.JSONParser;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;

public class HTTPClient implements java.lang.AutoCloseable {

	private static Logger s_log = Logger.getLogger(HTTPClient.class);
	
	public HTTPClient(URI host)
	{
		m_host = host;
	}

	public HTTPRequest get(String subPath)
	{
		return new HTTPRequest(HTTPMethod.GET, m_host, subPath);
	}

	public HTTPRequest put(String subPath)
	{
		return new HTTPRequest(HTTPMethod.PUT, m_host, subPath);
	}

	public HTTPRequest delete(String subPath)
	{
		return new HTTPRequest(HTTPMethod.DELETE, m_host, subPath);
	}
	
	public HTTPRequest post(String subPath)
	{
		return new HTTPRequest(HTTPMethod.POST, m_host, subPath);
	}
	
	public HTTPClient setTimeouts(int connectionTimeoutMs, Integer socketTimeoutMs) {
		m_globalRequestConfig.setConnectionRequestTimeout(connectionTimeoutMs);
		m_globalRequestConfig.setSocketTimeout(socketTimeoutMs);
		
		return this;
	}

	public HTTPClient setHeader(String key, String value) {
		if (m_globalHeader == null)
			m_globalHeader = new HTTPHeaders();

		m_globalHeader.setHeader(key, value);
		return this;
	}

	public HTTPClient setProperty(String key, String value) {
		if (m_globalProperties == null)
			m_globalProperties = new HTTPProperties();

		m_globalProperties.setProperty(key, value);
		return this;
	}

	public HTTPClient addQueryParam(String key, String value) {
		if (m_globalQueryParams == null)
			m_globalQueryParams = new Vector<NameValuePair>();

		m_globalQueryParams.add(new BasicNameValuePair(key, value));
		return this;
	}
	
	public HTTPClient setCredentials(String username, String password) {
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, credentials);

		if (m_context == null)
			m_context = new HttpClientContext();

		m_context.setCredentialsProvider(credentialsProvider);

		return this;
	}

	public class HTTPRequest {
		private HTTPHeaders m_localHeader = new HTTPHeaders();
		private HTTPProperties m_localProperties = new HTTPProperties();
		private List<NameValuePair> m_localQueryParams = null;
		private RequestConfig.Builder m_localRequestConfig = RequestConfig.copy(m_globalRequestConfig.build());
		private HttpEntity  m_body;

		private HTTPMethod m_method;
		
		private URI m_uri;
		private String m_path;
		
		public HTTPRequest(HTTPMethod method, URI schemeAndHostURI, String subPath)
		{
			m_method = method;
			m_uri = schemeAndHostURI;
			m_path = schemeAndHostURI.getPath() + subPath;
		}
		
		public HTTPRequest setTimeouts(int connectionTimeoutMs, int socketTimeoutMs) {
			m_localRequestConfig.setConnectionRequestTimeout(connectionTimeoutMs);
			m_localRequestConfig.setSocketTimeout(socketTimeoutMs);
			return this;
		}

		public HTTPRequest setHeader(String key, String value) {
			if (m_localHeader == null)
				m_localHeader = new HTTPHeaders();

			m_localHeader.setHeader(key, value);
			return this;
		}

		public HTTPRequest setProperty(String key, String value) {
			if (m_localProperties == null)
				m_localProperties = new HTTPProperties();

			m_localProperties.setProperty(key, value);
			return this;
		}

		public HTTPRequest addQueryParam(String key, String value) {
			if (m_localQueryParams == null)
				m_localQueryParams = new Vector<NameValuePair>();

			m_localQueryParams.add(new BasicNameValuePair(key, value));
			return this;
		}
		
		public HTTPResponse execute() throws ClientProtocolException, IOException, URISyntaxException
		{
			HttpRequestBase  method = null;
			URI uri = getQueryURI();
			
			switch(m_method)
			{
			case DELETE:
				method = new HttpDelete(uri);
				break;
			case GET:
				method = new HttpGet(uri);
				break;
			case POST:
				HttpPost postMethod = new HttpPost(uri);
				if (m_body != null)
					postMethod.setEntity(m_body);
				method = postMethod;
				break;
			case PUT:
				HttpPut putMethod = new HttpPut(uri);
				if (m_body != null)
					putMethod.setEntity(m_body);
				method = putMethod;
				break;
			}
			
			method.setConfig(m_localRequestConfig.build());
			// merge the headers
			Map<String, String> merge = mergeHeaders();
			for(Entry<String, String> entry : merge.entrySet())
				method.setHeader(entry.getKey(), entry.getValue());

			s_log.info(" - " + m_method + " " + uri);
			CloseableHttpResponse response = m_httpClient.execute(method, m_context);
			return new HTTPResponse(response);
		}

		private Map<String, String> mergeHeaders() {
			Map<String, String> merge = new HashMap<String, String>();
			merge.putAll(m_globalHeader.getHeaders());
			merge.putAll(m_localHeader.getHeaders());
			return merge;
		}

		public URI getQueryURI() throws URISyntaxException {
			String fragment = null;

			List<NameValuePair> queryParameters = new Vector<NameValuePair>();
			if (m_globalQueryParams != null && !m_globalQueryParams.isEmpty())
			{
				for(NameValuePair pair : m_globalQueryParams)
					queryParameters.add(new BasicNameValuePair(pair.getName(), resolveProperties(pair.getValue())));
			}
			if (m_localQueryParams != null && !m_localQueryParams.isEmpty())
			{
				for(NameValuePair pair : m_localQueryParams)
					queryParameters.add(new BasicNameValuePair(pair.getName(), resolveProperties(pair.getValue())));
			}
			
			URIBuilder uriBuilder = new URIBuilder()
				.setScheme(m_uri.getScheme())
				.setHost(m_uri.getHost())
				.setPort(m_uri.getPort())
				.setPath(resolveProperties(m_path))
				.setFragment(fragment);
			
			if (!queryParameters.isEmpty())
			{
				uriBuilder.setParameters(queryParameters);
			}

			return uriBuilder.build();
		}

		private String resolveProperties(String string) 
		{
			Pattern propertyPattern = Pattern.compile("(\\{[^}]+\\})");
			Matcher matcher = propertyPattern.matcher(string);
			StringBuffer resolvedString = new StringBuffer(string.length());

			while (matcher.find())
			{
				String uriProperty = matcher.group(1);
				String property = uriProperty.substring(1, uriProperty.length()-1);

				String value = null;
				if (m_localProperties != null)
					value =  m_localProperties.getProperty(property);

				if (value == null && m_globalProperties != null)
					value = m_globalProperties.getProperty(property);

				if (value == null)
					value = uriProperty;

				matcher.appendReplacement(resolvedString, Matcher.quoteReplacement(value));
			}
			matcher.appendTail(resolvedString);

			return resolvedString.toString();
		}

		public HTTPRequest setJsonBody(Object object) throws UnsupportedEncodingException
		{
			Gson gson = new Gson();
			String body = gson.toJson(object);
			setBody(body, ContentType.APPLICATION_JSON);
			return this;
		}
		
		public HTTPRequest setBody(String body, ContentType contentType) throws UnsupportedEncodingException
		{
			m_body = new StringEntity(body, contentType);
			return this;
		}

		public HTTPRequest setBody(byte[] body, ContentType contentType) throws UnsupportedEncodingException
		{
			m_body = new ByteArrayEntity(body, contentType);
			return this;
		}

		public class HTTPResponse implements java.lang.AutoCloseable {

			CloseableHttpResponse m_response;
			HTTPHeaders m_headers;
			
			private HTTPResponse(CloseableHttpResponse response)
			{
				m_response = response;
			}

			public HTTPHeaders getHeaders()
			{
				if (m_headers == null)
				{
					m_headers = new HTTPHeaders();
				
					Header[] headers = m_response.getAllHeaders();
					for(Header header : headers)
					{
						m_headers.setHeader(header.getName(), header.getValue());
					}
				}

				return m_headers;
			}
			
			public String getFirstHeader(String name)
			{
				Header header = m_response.getFirstHeader(name);
				if (header == null)
					return null;

				return header.getValue();
			}

			public int getStatusCode()
			{
				return m_response.getStatusLine().getStatusCode();
			}
			
			public HTTPStatusFamily getStatusCodeFamily()
			{
				return HTTPStatusFamily.fromInt(getStatusCode());
			}
			
			public boolean wasSucessful()
			{
				return getStatusCodeFamily() == HTTPStatusFamily.SUCCESSFUL_2XX;
			}
			
			public String getStatusMessage()
			{
				return m_response.getStatusLine().getReasonPhrase();
			}

			public InputStream getContent() throws UnsupportedOperationException, IOException
			{
				return m_response.getEntity().getContent();
			}

			// assumes json for now
			@SuppressWarnings("unchecked")
			public <T> T getContent(Class<T> clazz) throws UnsupportedOperationException, IOException
			{
				if (clazz == String.class)
				{
					return (T) CharStreams.toString(new InputStreamReader(getContent(), Charsets.UTF_8));
				}
				else
				{
					JSONParser parser = new JSONParser();
					return parser.fromJSON(getContent(), clazz);
				}
			}

			public ContentType getContentType()
			{
				HttpEntity entity = m_response.getEntity();
				if (entity == null)
					return null;

				return ContentType.getLenient(entity);
			}

			@Override
			public void close() throws Exception
			{
				if (m_response != null)
				{
					EntityUtils.consumeQuietly(m_response.getEntity());
					m_response.close();
				}
			}
		}
	}

	private enum HTTPMethod
	{
		PUT,
		GET,
		POST,
		DELETE
	}

	private HTTPHeaders m_globalHeader = new HTTPHeaders();
	private HTTPProperties m_globalProperties = new HTTPProperties();
	private List<NameValuePair> m_globalQueryParams = null;
	private HttpClientContext m_context;
	private RequestConfig.Builder m_globalRequestConfig = RequestConfig.copy(RequestConfig.DEFAULT);
	private URI m_host;

	private static class HTTPProperties
	{
		Map<String, String> m_values = new HashMap<String, String>();
		
		public void setProperty(String key, String value) 
	 	{
	 		if (value == null) 
	 		{
	 			m_values.remove(key);
	 		}
	 		else 
	 		{
	 			m_values.put(key, value);
	 		}
	 	}
		
		public String getProperty(String key)
		{
			return m_values.get(key);
		}
	}


	private CloseableHttpClient m_httpClient = HttpClients.createDefault();


	@Override
	public void close() throws Exception {
		m_httpClient.close();
	}
}

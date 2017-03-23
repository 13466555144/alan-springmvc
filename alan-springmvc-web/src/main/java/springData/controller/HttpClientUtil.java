package springData.controller;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * @author liht
 * 
 * @time 2017-02-22 下午02:25:00
 */
public class HttpClientUtil {

	private static CloseableHttpClient createSSLClientDefault() {
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(
					null, new TrustStrategy() {
						// 信任所有
						@Override
						public boolean isTrusted(X509Certificate[] chain,
								String authType) throws CertificateException {
							return true;
						}
					}).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
					sslContext);
			return HttpClients.custom().setSSLSocketFactory(sslsf).build();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
		return HttpClients.createDefault();
	}
	
	
	public static CloseableHttpClient acceptsUntrustedCertsHttpClient() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
	        HttpClientBuilder b = HttpClientBuilder.create();

	        // setup a Trust Strategy that allows all certificates.
	        //
	        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
	            public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
	                return true;
	            }
	        }).build();
	        b.setSslcontext(sslContext);

	        // don't check Hostnames, either.
	        //      -- use SSLConnectionSocketFactory.getDefaultHostnameVerifier(), if you don't want to weaken
	        HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;

	        // here's the special part:
	        //      -- need to create an SSL Socket Factory, to use our weakened "trust strategy";
	        //      -- and create a Registry, to register it.
	        //
	        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
	        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
	                .register("http", PlainConnectionSocketFactory.getSocketFactory())
	                .register("https", sslSocketFactory)
	                .build();

	        // now, we create connection-manager using our Registry.
	        //      -- allows multi-threaded use
	        PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager( socketFactoryRegistry);
	        connMgr.setMaxTotal(200);
	        connMgr.setDefaultMaxPerRoute(100);
	        b.setConnectionManager( connMgr);

	        // finally, build the HttpClient;
	        //      -- done!
	        CloseableHttpClient client = b.build();

	        return client;
	    }
	
	
	public static String post(String url, String sendBody,
			List<NameValuePair> headerParams) {
		CloseableHttpClient httpClient = HttpClientUtil.createSSLClientDefault();
		String body = null;
		try {
			// Post请求
			HttpPost post = new HttpPost(url);
			StringEntity se = new StringEntity(sendBody, "UTF-8");
			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json;charset=UTF-8"));
			post.setHeader("Connection","close");
			post.setEntity(se);
			if (headerParams != null) {
				for (NameValuePair param : headerParams) {
					if (param != null && param.getName() != null
							&& param.getValue() != null) {
						post.setHeader(param.getName(), param.getValue());
					}
				}
			}
			try {
				// 发送请求
				RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(2000).build();//设置请求和传输超时时间
				post.setConfig(requestConfig);
				HttpResponse httpresponse = httpClient.execute(post);
				// 获取返回数据
				HttpEntity entity = httpresponse.getEntity();

				body = EntityUtils.toString(entity);
				body = new String(body.getBytes("ISO-8859-1"), "UTF-8");
				if (entity != null) {
					EntityUtils.consume(entity);
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				if(post != null)
					post.releaseConnection();
			}

		} /*
		 * catch (UnsupportedEncodingException e) { e.printStackTrace(); }
		 */catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			//httpClient.getConnectionManager().shutdown();
			try {  
				httpClient.close();  
			} catch (IOException e) {  
				e.printStackTrace();  
			}
		}
		return body;
	}
}

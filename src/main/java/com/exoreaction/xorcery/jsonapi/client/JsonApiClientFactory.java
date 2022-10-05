package com.exoreaction.xorcery.jsonapi.client;

import com.exoreaction.xorcery.configuration.Configuration;
import com.exoreaction.xorcery.jaxrs.readers.JsonElementMessageBodyReader;
import com.exoreaction.xorcery.jaxrs.writers.JsonElementMessageBodyWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.dynamic.HttpClientTransportDynamic;
import org.eclipse.jetty.client.http.HttpClientConnectionFactory;
import org.eclipse.jetty.http2.client.HTTP2Client;
import org.eclipse.jetty.http2.client.http.ClientConnectionFactoryOverHTTP2;
import org.eclipse.jetty.http3.client.HTTP3Client;
import org.eclipse.jetty.http3.client.http.ClientConnectionFactoryOverHTTP3;
import org.eclipse.jetty.io.ClientConnectionFactory;
import org.eclipse.jetty.io.ClientConnector;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jetty.connector.JettyConnectorProvider;
import org.glassfish.jersey.jetty.connector.JettyHttpClientContract;
import org.glassfish.jersey.jetty.connector.JettyHttpClientSupplier;
import org.glassfish.jersey.logging.LoggingFeature;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.time.Duration;

import static org.eclipse.jetty.util.ssl.SslContextFactory.Client.SniProvider.NON_DOMAIN_SNI_PROVIDER;

public class JsonApiClientFactory {

    public JsonApiClient create(Configuration configuration) {
        JettyHttpClientContract instance = new JettyHttpClientSupplier(createClient(configuration));
        Client client = ClientBuilder.newBuilder()
                .withConfig(new ClientConfig()
                        .register(new JsonElementMessageBodyReader(new ObjectMapper()))
                        .register(new JsonElementMessageBodyWriter(new ObjectMapper()))
                        .register(new LoggingFeature.LoggingFeatureBuilder().withLogger(java.util.logging.Logger.getLogger(JsonApiClientFactory.class.getPackage().getName())).build())
                        .connectorProvider(new JettyConnectorProvider())
                        .register(instance))
                .build();
        JsonApiClient jsonApiClient = new JsonApiClient(client);
        return jsonApiClient;
    }

    public HttpClient createClient(Configuration configuration) {
        // Client setup
        ClientConnector connector = new ClientConnector();
        connector.setIdleTimeout(Duration.ofSeconds(configuration.getLong("client.idle_timeout").orElse(-1L)));

        // HTTP 1.1
        ClientConnectionFactory.Info http1 = HttpClientConnectionFactory.HTTP11;

        ClientConnectionFactoryOverHTTP2.HTTP2 http2 = null;
        ClientConnectionFactoryOverHTTP3.HTTP3 http3 = null;

        if (configuration.getBoolean("client.http2.enabled").orElse(false)) {
            // HTTP/2
            HTTP2Client http2Client = new HTTP2Client(connector);
            http2Client.setIdleTimeout(configuration.getLong("client.idle_timeout").orElse(-1L));

            http2 = new ClientConnectionFactoryOverHTTP2.HTTP2(http2Client);
        }

        HttpClientTransportDynamic transport = null;
        if (configuration.getBoolean("client.ssl.enabled").orElse(false)) {

            SslContextFactory.Client sslClientContextFactory = new SslContextFactory.Client() {
                @Override
                protected KeyStore loadTrustStore(Resource resource) throws Exception {
                    KeyStore keyStore = super.loadTrustStore(resource);
                    addDefaultRootCaCertificates(keyStore);
                    return keyStore;
                }
            };
            sslClientContextFactory.setKeyStoreType(configuration.getString("client.ssl.keystore.type").orElse("PKCS12"));
            sslClientContextFactory.setKeyStorePath(configuration.getString("client.ssl.keystore.path")
                    .orElseGet(() -> getClass().getResource("/keystore.p12").toExternalForm()));
            sslClientContextFactory.setKeyStorePassword(configuration.getString("client.ssl.keystore.password").orElse("password"));

//                        sslClientContextFactory.setTrustStoreType(configuration.getString("client.ssl.truststore.type").orElse("PKCS12"));
            sslClientContextFactory.setTrustStorePath(configuration.getString("client.ssl.truststore.path")
                    .orElseGet(() -> getClass().getResource("/keystore.p12").toExternalForm()));
            sslClientContextFactory.setTrustStorePassword(configuration.getString("client.ssl.truststore.password").orElse("password"));

            sslClientContextFactory.setEndpointIdentificationAlgorithm("HTTPS");
            sslClientContextFactory.setHostnameVerifier((hostName, session) -> true);
            sslClientContextFactory.setTrustAll(configuration.getBoolean("client.ssl.trustall").orElse(false));
            sslClientContextFactory.setSNIProvider(NON_DOMAIN_SNI_PROVIDER);


            connector.setSslContextFactory(sslClientContextFactory);

            // HTTP/3
            if (configuration.getBoolean("client.http3.enabled").orElse(false)) {
                HTTP3Client h3Client = new HTTP3Client();
                h3Client.getClientConnector().setIdleTimeout(Duration.ofSeconds(configuration.getLong("client.idle_timeout").orElse(-1L)));
                h3Client.getQuicConfiguration().setSessionRecvWindow(64 * 1024 * 1024);
                http3 = new ClientConnectionFactoryOverHTTP3.HTTP3(h3Client);
                h3Client.getClientConnector().setSslContextFactory(sslClientContextFactory);
            }
        }

        // Figure out correct transport dynamics
        if (http3 != null) {
            if (http2 != null) {
                transport = new HttpClientTransportDynamic(connector, http1, http3, http2);
            } else {
                transport = new HttpClientTransportDynamic(connector, http1, http3);
            }
        } else if (http2 != null) {
            transport = new HttpClientTransportDynamic(connector, http1, http2);
        } else {
            transport = new HttpClientTransportDynamic(connector, http1);
        }

        return new HttpClient(transport);
    }

    protected void addDefaultRootCaCertificates(KeyStore trustStore) throws GeneralSecurityException {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        // Loads default Root CA certificates (generally, from JAVA_HOME/lib/cacerts)
        trustManagerFactory.init((KeyStore) null);
        for (TrustManager trustManager : trustManagerFactory.getTrustManagers()) {
            if (trustManager instanceof X509TrustManager) {
                for (X509Certificate acceptedIssuer : ((X509TrustManager) trustManager).getAcceptedIssuers()) {
                    trustStore.setCertificateEntry(acceptedIssuer.getSubjectDN().getName(), acceptedIssuer);
                }
            }
        }
    }
}

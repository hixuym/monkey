package io.sunflower.undertow;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import io.sunflower.validation.PortRange;
import io.undertow.Undertow;

/**
 * Builds HTTP connectors.
 *
 * <p/>
 * <b>Configuration Parameters:</b> <table> <tr> <td>Name</td> <td>Default</td> <td>Description</td> </tr> <tr>
 * <td>{@code port}</td> <td>8080</td> <td>The TCP/IP port on which to listen for incoming connections.</td> </tr> <tr>
 * <td>{@code bindHost}</td> <td>(none)</td> <td>The hostname to bind to.</td> </tr> <tr> <td>{@code
 * inheritChannel}</td> <td>false</td> <td> Whether this connector uses a channel inherited from the JVM. Use it with <a
 * href="https://github.com/kazuho/p5-Server-Starter">Server::Starter</a>, to launch an instance of Jetty on demand.
 * </td> </tr> <tr> <td>{@code headerCacheSize}</td> <td>512 bytes</td> <td>The size of the header field cache.</td>
 * </tr> <tr> <td>{@code outputBufferSize}</td> <td>32KiB</td> <td> The size of the buffer into which response content
 * is aggregated before being sent to the client.  A larger buffer can improve performance by allowing a content
 * producer to run without blocking, however larger buffers consume more memory and may induce some latency before a
 * client starts processing the content. </td> </tr> <tr> <td>{@code maxRequestHeaderSize}</td> <td>8KiB</td> <td> The
 * maximum size of a request header. Larger headers will allow for more and/or larger cookies plus larger form content
 * encoded  in a URL. However, larger headers consume more memory and can make a server more vulnerable to denial of
 * service attacks. </td> </tr> <tr> <td>{@code maxResponseHeaderSize}</td> <td>8KiB</td> <td> The maximum size of a
 * response header. Larger headers will allow for more and/or larger cookies and longer HTTP headers (eg for
 * redirection).  However, larger headers will also consume more memory. </td> </tr> <tr> <td>{@code
 * inputBufferSize}</td> <td>8KiB</td> <td>The size of the per-connection input buffer.</td> </tr> <tr> <td>{@code
 * idleTimeout}</td> <td>30 seconds</td> <td> The maximum idle time for a connection, which roughly translates to the
 * {@link java.net.Socket#setSoTimeout(int)} call, although with NIO implementations other mechanisms may be used to
 * implement the timeout.
 * <p/>
 * The max idle time is applied: <ul> <li>When waiting for a new message to be received on a connection</li> <li>When
 * waiting for a new message to be sent on a connection</li> </ul>
 * <p/>
 * This value is interpreted as the maximum time between some progress being made on the connection. So if a single byte
 * is read or written, then the timeout is reset. </td> </tr> <tr> <td>{@code blockingTimeout}</td> <td>(none)</td>
 * <td>The timeout applied to blocking operations. This timeout is in addition to the {@code idleTimeout}, and applies
 * to the total operation (as opposed to the idle timeout that applies to the time no data is being sent). </td> </tr>
 * <tr> <td>{@code minBufferPoolSize}</td> <td>64 bytes</td> <td>The minimum size of the buffer pool.</td> </tr> <tr>
 * <td>{@code bufferPoolIncrement}</td> <td>1KiB</td> <td>The increment by which the buffer pool should be
 * increased.</td> </tr> <tr> <td>{@code maxBufferPoolSize}</td> <td>64KiB</td> <td>The maximum size of the buffer
 * pool.</td> </tr> <tr> <td>{@code acceptorThreads}</td> <td>(Jetty's default)</td> <td>The number of worker threads
 * dedicated to accepting connections. By default is <i>max</i>(1, <i>min</i>(4, #CPUs/8)).</td> </tr> <tr> <td>{@code
 * selectorThreads}</td> <td>(Jetty's default)</td> <td>The number of worker threads dedicated to sending and receiving
 * data. By default is <i>max</i>(1, <i>min</i>(4, #CPUs/2)).</td> </tr> <tr> <td>{@code acceptQueueSize}</td> <td>(OS
 * default)</td> <td>The size of the TCP/IP accept queue for the listening socket.</td> </tr> <tr> <td>{@code
 * reuseAddress}</td> <td>true</td> <td>Whether or not {@code SO_REUSEADDR} is enabled on the listening socket.</td>
 * </tr> <tr> <td>{@code soLingerTime}</td> <td>(disabled)</td> <td>Enable/disable {@code SO_LINGER} with the specified
 * linger time.</td> </tr> <tr> <td>{@code useServerHeader}</td> <td>false</td> <td>Whether or not to add the {@code
 * Server} header to each response.</td> </tr> <tr> <td>{@code useDateHeader}</td> <td>true</td> <td>Whether or not to
 * add the {@code Date} header to each response.</td> </tr> <tr> <td>{@code useForwardedHeaders}</td> <td>true</td> <td>
 * Whether or not to look at {@code X-Forwarded-*} headers added by proxies. See {@link ForwardedRequestCustomizer} for
 * details. </td> </tr> <tr> <td>{@code httpCompliance}</td> <td>RFC7230</td> <td> This sets the server compliance level
 * used by Jetty when parsing server, this can be useful when using a non-RFC7230 compliant front end, such as nginx,
 * which can produce multi-line headers when forwarding client certificates using proxy_set_header X-SSL-CERT
 * $ssl_client_cert;
 *
 * Possible values are set forth in the org.eclipse.jetty.server.HttpCompliance enum: <ul> <li>RFC7230: Disallow header
 * folding.</li> <li>RFC2616: Allow header folding.</li> </ul> </td> </tr> </table>
 */
@JsonTypeName("server")
public class HttpConnectorFactory implements ConnectorFactory {
    public static ConnectorFactory application() {
        final HttpConnectorFactory factory = new HttpConnectorFactory();
        factory.port = 8080;
        return factory;
    }

    public static ConnectorFactory admin() {
        final HttpConnectorFactory factory = new HttpConnectorFactory();
        factory.port = 8081;
        return factory;
    }

    @PortRange
    private int port = 8080;

    private String bindHost = null;

    @JsonProperty
    public int getPort() {
        return port;
    }

    @JsonProperty
    public void setPort(int port) {
        this.port = port;
    }

    @JsonProperty
    public String getBindHost() {
        return bindHost;
    }

    @JsonProperty
    public void setBindHost(String bindHost) {
        this.bindHost = bindHost;
    }

    @Override
    public Undertow.ListenerBuilder build() {

        Undertow.ListenerBuilder builder = new Undertow.ListenerBuilder();

        builder.setType(Undertow.ListenerType.HTTP);
        builder.setHost(bindHost);
        builder.setPort(port);

        return builder;
    }
}

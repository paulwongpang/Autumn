package cn.imaq.autumn.http.client;

import cn.imaq.autumn.http.client.protocol.HttpConnection;
import cn.imaq.autumn.http.protocol.AutumnHttpRequest;
import cn.imaq.autumn.http.protocol.AutumnHttpResponse;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class AutumnHttpClient {
    private static final ThreadLocal<Map<InetSocketAddress, HttpConnection>> localConnections = new ThreadLocal<>();
    private static final Pattern httpUrlPattern = Pattern.compile("^http://(.+?)[/(.*)]$", Pattern.CASE_INSENSITIVE);

    private static HttpConnection getConnection(InetSocketAddress address) {
        Map<InetSocketAddress, HttpConnection> connectionMap = localConnections.get();
        if (connectionMap == null) {
            connectionMap = new HashMap<>();
            localConnections.set(connectionMap);
        }
        HttpConnection conn = connectionMap.get(address);
        // reuse existing connections
        if (conn != null && conn.isAvailable()) {
            return conn;
        }
        // open new connection
        try {
            conn = new HttpConnection(address);
            connectionMap.put(address, conn);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static AutumnHttpResponse request(String method, String urlStr, String contentType, byte[] body, int timeoutMillis) throws IOException {
        URL url = new URL(urlStr);
        String path = url.getPath();
        if (path.isEmpty()) {
            path = "/";
        }
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Host", Collections.singletonList(url.getHost()));
        if (contentType != null) {
            headers.put("Content-Type", Collections.singletonList(contentType));
        }
        AutumnHttpRequest request = AutumnHttpRequest.builder()
                .method(method)
                .path(path)
                .protocol("HTTP/1.1")
                .headers(headers)
                .body(body)
                .build();
        int port = url.getPort() > 0 ? url.getPort() : 80;
        InetSocketAddress socketAddress = new InetSocketAddress(url.getHost(), port);
        HttpConnection connection = getConnection(socketAddress);
        return connection.writeThenRead(request.toRequestString().getBytes(), timeoutMillis);
    }

    public static AutumnHttpResponse get(String urlStr, int timeoutMillis) throws IOException {
        return request("GET", urlStr, null, null, timeoutMillis);
    }

    public static AutumnHttpResponse post(String urlStr, String contentType, byte[] body, int timeoutMillis) throws IOException {
        return request("POST", urlStr, contentType, body, timeoutMillis);
    }
}

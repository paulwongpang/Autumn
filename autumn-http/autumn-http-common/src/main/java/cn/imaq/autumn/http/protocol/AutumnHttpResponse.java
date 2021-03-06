package cn.imaq.autumn.http.protocol;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class AutumnHttpResponse {
    private String protocol;

    private int status;

    private Map<String, List<String>> headers;

    private String contentType;

    private byte[] body;

    public static class ResponseCodes {
        static final String[][] RESPONSE_CODES = {
                {"Continue", "Switching Protocols", "Processing", "Early Hints"},
                {"OK", "Created", "Accepted", "Non-authoritative Information", "No Content", "Reset Content", "Partial Content", "Multi-Status", "Already Reported"},
                {"Multiple Choices", "Moved Permanently", "Found", "See Other", "Not Modified", "Use Proxy", "Switch Proxy", "Temporary Redirect", "Permanent Redirect"},
                {"Bad Request", "Unauthorized", "Payment Required", "Forbidden", "Not Found", "Method Not Allowed", "Not Acceptable", "Proxy Authentication Required", "Request Timeout", "Conflict", "Gone", "Length Required", "Precondition Failed", "Payload Too Large", "Request-URI Too Long", "Unsupported Media Type", "Requested Range Not Satisfiable", "Expectation Failed", "I'm a teapot", null, null, "Misdirected Request", "Unprocessable Entity", "Locked", "Failed Dependency", null, "Upgrade Required", null, "Precondition Required", "Too Many Requests", null, "Request Header Fields Too Large", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "Unavailable For Legal Reasons"},
                {"Internal Server Error", "Not Implemented", "Bad Gateway", "Service Unavailable", "Gateway Timeout", "HTTP Version Not Supported", "Variant Also Negotiates", "Insufficient Storage", "Loop Detected", null, "Not Extended", "Network Authentication Required"}
        };

        public static String get(int code) {
            try {
                return RESPONSE_CODES[code / 100 - 1][code % 100];
            } catch (Exception e) {
                return null;
            }
        }
    }
}

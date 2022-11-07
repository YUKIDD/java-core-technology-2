package org.example.chap04.client;


import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.*;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

class MoreBodyPublishers {
    public static BodyPublisher ofFormData(Map<Object,Object> data) {
        boolean first = true;
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Object,Object> entry : data.entrySet()) {
            if (first) {
                first = false;
            } else {
                builder.append("&");
                builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
                builder.append("=");
                builder.append(URLEncoder.encode(entry.getValue().toString(),StandardCharsets.UTF_8));
            }
        }
        return BodyPublishers.ofString(builder.toString());
    }

    private static byte[] bytes(String s) {
        return s.getBytes(StandardCharsets.UTF_8);
    }

    public static BodyPublisher ofMimeMultipartData(Map<Object,Object> data,String boundary) throws IOException {
        ArrayList byteArray = new ArrayList<byte[]>();
        byte[] separator = bytes("--" + boundary + "\nContent-Disposition: form-data; name=");
        for (Map.Entry<Object,Object> entry : data.entrySet()) {
            byteArray.add(separator);
            if (entry.getValue() instanceof Path) {
                Path path = (Path) entry.getValue();
                String mimeType = Files.probeContentType(path);
                byteArray.add(bytes("\"" + entry.getKey() + "\"; filename=\"" + path.getFileName()
                + "\"\nContent-Type: " + mimeType + "\n\n"));
                byteArray.add(Files.readAllBytes(path));
            } else {
                byteArray.add(bytes("\"" + entry.getKey() + "\"\n\n" + entry.getValue() + "\n"));
            }
        }
        byteArray.add(bytes("--" + boundary + "--"));
        return BodyPublishers.ofByteArrays(byteArray);
    }

    public static BodyPublisher ofSimpleJSON(Map<Object,Object> data) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        boolean first = true;
        for (Map.Entry<Object,Object> entry : data.entrySet()) {
            if (first) {
                first = false;
            } else {
                builder.append(",");
            }
            builder.append(jsonEscape(entry.getKey().toString()))
                    .append(": ")
                    .append(jsonEscape(entry.getValue().toString()));
        }
        builder.append("}");
        return BodyPublishers.ofString(builder.toString());
    }

    private static Map<Character,String> replacements = Map.of('\b',"\\b",
            '\f',"\\f",'\n',"\\n",'\r',"\\r",
            '\t',"\\t",'"',"\\\"",'\\',"\\\\");

    private static StringBuilder jsonEscape(String str) {
        StringBuilder result = new StringBuilder("\"");
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            String replacement = replacements.get(ch);
            if (replacement == null) {
                result.append(ch);
            } else {
                result.append(replacement);
            }
        }
        result.append("\"");
        return result;
    }
}

public class HttpClientTest {
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        System.setProperty("jdk.httpclient.HttpClient.log","headers,errors");
        String propsFilename = args.length > 0 ? args[0] : "src/main/java/org/example/chap04/client/json.properties";
        Path propsPath = Paths.get(propsFilename);
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(propsPath)) {
            props.load(in);
        }
        String urlString = "" + props.remove("url");
        String contentType = "" + props.remove("Content-Type");
        if (contentType.equals("multipart/form-data")) {
            Random generator = new Random();
            String boundary = new BigInteger(256,generator).toString();
            contentType += ";boundary=" + boundary;
            props.replaceAll((k,v) ->
                    v.toString().startsWith("file://") ? propsPath.getParent().resolve(Paths.get(v.toString().substring(7))) : v);
        }
        String result = doPost(urlString, contentType,props);
        System.out.println(result);
    }

    public static String doPost(String url,String contentType, Map<Object,Object> data) throws IOException, URISyntaxException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS).build();

        BodyPublisher publisher = null;
        if (contentType.startsWith("multipart/form-data")) {
            String boundary = contentType.substring(contentType.lastIndexOf("=") + 1);
            publisher = MoreBodyPublishers.ofMimeMultipartData(data,boundary);
        } else if (contentType.equals("application/x-www-form-urlencoded")) {
            publisher = MoreBodyPublishers.ofFormData(data);
        } else {
            contentType = "application/json";
            publisher = MoreBodyPublishers.ofSimpleJSON(data);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .header("Content-Type",contentType)
                .POST(publisher)
                .build();
        HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}

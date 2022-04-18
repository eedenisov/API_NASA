import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {

    public static final String URI =
            "https://api.nasa.gov/planetary/apod?api_key=12AyLPs7NeFZOei0H1rLQ1KTybwBodNG5h2SNDGg";
    public static ObjectMapper objectMapper = new ObjectMapper();
    public static Nasa nasa;

    public static void main(String[] args) {
        try {
            CloseableHttpClient http = HttpClientBuilder.create()
                    .setUserAgent("NASA")
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setConnectTimeout(4000)
                            .setSocketTimeout(20000)
                            .setRedirectsEnabled(false)
                            .build())
                    .build();

            HttpGet request = new HttpGet(URI);
            request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

            CloseableHttpResponse response = http.execute(request);

            nasa = objectMapper.readValue(response.getEntity().getContent(),
                    new TypeReference<>() {
                    }
            );
            System.out.println(nasa);

        } catch (IOException e) {
            e.printStackTrace();
        }

        String urlImage = nasa.getUrl();

        try {
            CloseableHttpClient http2 = HttpClientBuilder.create()
                    .setUserAgent("NASA_Image")
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setConnectTimeout(4000)
                            .setSocketTimeout(20000)
                            .setRedirectsEnabled(false)
                            .build())
                    .build();

            HttpResponse response2 = http2.execute(
                    new HttpGet(urlImage));

            String[] parts = urlImage.split("/");

            String name = parts[parts.length - 1];

            FileOutputStream fos = new FileOutputStream(name, false);

            response2.getEntity().writeTo(fos);
            System.out.println("\nThe picture is saved " + name);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

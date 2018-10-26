import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;

/**
 * @author 고종봉 (jongbong.ko@navercorp.com)
 */
public class HttpClientTest {
    @Test
    public void testGet() throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet("com.mercu.http.http://www.google.com");

//        request.addHeader("User-Agent", "");
        HttpResponse response = client.execute(request);
        System.out.println("response.statusLine.statusCode : " + response.getStatusLine().getStatusCode());

        BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        System.out.println("=== response body ===");
        String line = "";
        while((line = br.readLine()) != null) {
            System.out.println(line);
        }

    }
}

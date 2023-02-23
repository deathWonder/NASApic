import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class Main {

    private static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();

        HttpGet request = new HttpGet(
                "https://api.nasa.gov/planetary/apod?api_key=KwNhupDYakuFL47wX9An8R1YRVHfWGCts70zcfkX");

        CloseableHttpResponse response = httpClient.execute(request);
        NasaObject nasaObject = mapper.readValue(response.getEntity().getContent(), NasaObject.class);
        String HDUrl = nasaObject.getUrl();

        CloseableHttpResponse responsePic = httpClient.execute(new HttpGet(HDUrl));
        byte[] bytes = responsePic.getEntity()
                .getContent()
                .readAllBytes();


        response.close();
        responsePic.close();
        httpClient.close();


        try (FileOutputStream out = new FileOutputStream("SunHalphaC_Ergun_2065.jpg");
             BufferedOutputStream bos = new BufferedOutputStream(out)) {
            bos.write(bytes, 0, bytes.length);
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }
}


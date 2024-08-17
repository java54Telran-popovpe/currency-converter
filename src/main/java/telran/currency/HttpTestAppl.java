package telran.currency;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.json.JSONObject;

public class HttpTestAppl {

	public static void main(String[] args) throws Exception {
		String uri = "https://data.fixer.io/api/latest?access_key=7a4100d95449c972d689de023991830b";
		HttpClient httpClient = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder(new URI(uri)).build();
		HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
		System.out.println(response);
		JSONObject jsonObject = new JSONObject(response.body());
		JSONObject jsonRates = jsonObject.getJSONObject("rates");
		String[] codes = {"USD", "EUR","ILS", "RUB"};
		Map<String, Double> map = Arrays.stream(codes).collect(Collectors.toMap(Function.identity(), c -> jsonRates.getDouble(c)));
	}

}

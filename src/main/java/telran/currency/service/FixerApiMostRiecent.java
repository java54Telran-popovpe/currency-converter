package telran.currency.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.json.JSONObject;

public class FixerApiMostRiecent extends AbstractCurrencyConvertor {
	protected final String uriString = "https://data.fixer.io/api/latest?access_key=7a4100d95449c972d689de023991830b";
	protected final String pointerToRatesObject = "/rates";
	private String etagHeaderValue = null;
	private String dateHeaderValue = null;
	
	public FixerApiMostRiecent() {
		rates = getRates();
	}
	
	protected Map<String, Double> getRates() {
		Map<String, Double> result;
		HttpClient httpClient = HttpClient.newHttpClient();
		HttpRequest request = null;
		HttpResponse<String> response = null;
		try {
			request = getHttpRequest();
			response = httpClient.send(request, BodyHandlers.ofString());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		if ( response.statusCode() == 304 ) {
			System.out.println("Using existing data...");
			result = rates;
		} else {
			System.out.println("Fetching new data...");
			result = getRatesFromResponse(response);
		}
		return result;
	}
	private Map<String, Double> getRatesFromResponse(HttpResponse<String> response) {
		JSONObject jsonBody = new JSONObject(response.body());
		if (!jsonBody.getBoolean("success")) {
			throw new RuntimeException("Error " + 
					(int)jsonBody.optQuery("/error/code") + 
					": " + (String) jsonBody.optQuery("/error/info"));
		}
		HttpHeaders headers = response.headers();
		etagHeaderValue = headers.allValues("ETag").get(0);
		dateHeaderValue = headers.allValues("Date").get(0);
		JSONObject jsonRates = (JSONObject) jsonBody.optQuery(pointerToRatesObject);
		return Arrays.stream(JSONObject.getNames(jsonRates))
				.collect(Collectors.toMap(Function.identity(), c -> jsonRates.getDouble(c)) );
	}
	
	private HttpRequest getHttpRequest() throws URISyntaxException {
		var builder = HttpRequest.newBuilder(new URI(uriString));
		if (etagHeaderValue != null) {
			builder.headers("If-None-Match", etagHeaderValue, "If-Modified-Since", dateHeaderValue);
		}
		return builder.build();
	}
	
	@Override
	public List<String> strongestCurrencies(int amount) {
		rates = getRates();
		return super.strongestCurrencies(amount);
		
	}
	@Override
	public List<String> weakestCurrencies(int amount) {
		rates = getRates();
		return super.weakestCurrencies(amount);
		
	}
	
	@Override
	public double convert(String codeFrom, String codeTo, int amount) {
		rates = getRates();
		return super.convert(codeFrom, codeTo, amount);
		
	}
}

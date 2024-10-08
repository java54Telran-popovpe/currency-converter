package telran.currency.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.json.JSONObject;

public class FixerApiPerDay extends AbstractCurrencyConvertor {
	
	private LocalDateTime ratesValidBefore;
	protected final String uriString = "https://data.fixer.io/api/latest?access_key=7a4100d95449c972d689de023991830b";
	protected final String pointerToRatesObject = "/rates";
	
	public FixerApiPerDay() {
		rates = getRates();
	}
	
	protected Map<String, Double> getRates() {
		HttpClient httpClient = HttpClient.newHttpClient();
		HttpRequest request = null;
		HttpResponse<String> response = null;
		try {
			request = HttpRequest.newBuilder(new URI(uriString)).build();
			response = httpClient.send(request, BodyHandlers.ofString());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		JSONObject jsonBody = new JSONObject(response.body());
		long rateTimeStamp = jsonBody.getLong("timestamp");
		ratesValidBefore = LocalDateTime.ofInstant(Instant.ofEpochSecond(rateTimeStamp), ZoneId.of("GMT")).plusDays(1);
		return getRatesFromResponse(jsonBody);
	}
	
	private Map<String, Double> getRatesFromResponse(JSONObject jsonBody) {
		if (!jsonBody.getBoolean("success")) {
			throw new RuntimeException("Error " + 
					(int)jsonBody.optQuery("/error/code") + 
					": " + (String) jsonBody.optQuery("/error/info"));
		}
		JSONObject jsonRates = (JSONObject) jsonBody.optQuery(pointerToRatesObject);
		return Arrays.stream(JSONObject.getNames(jsonRates))
				.collect(Collectors.toMap(Function.identity(), c -> jsonRates.getDouble(c)) );
	}
	
	@Override
	public List<String> strongestCurrencies(int amount) {
		refresh();
		return super.strongestCurrencies(amount);
		
	}
	@Override
	public List<String> weakestCurrencies(int amount) {
		refresh();
		return super.weakestCurrencies(amount);
		
	}
	
	@Override
	public double convert(String codeFrom, String codeTo, int amount) {
		refresh();
		return super.convert(codeFrom, codeTo, amount);
		
	}
	
	private void refresh() {
		if ( LocalDateTime.now().isAfter(ratesValidBefore)) {
			rates = getRates();
		}
	}
}

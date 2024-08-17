package telran.currency.service;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public abstract class AbstractCurrencyConvertor implements CurrencyConverter {
	
	protected Map<String,Double> rates; //key - currency ISO code, value - amount of currency unit in 1 EUR

	@Override
	public List<String> strongestCurrencies(int amount) {
		return getTopNKeys(amount, Comparator.naturalOrder());
	}

	@Override
	public List<String> weakestCurrencies(int amount) {
		return getTopNKeys(amount, Comparator.reverseOrder());
	}

	@Override
	public double convert(String codeFrom, String codeTo, int amount) {
		double fromRate = getRate(codeFrom);
		double toRate = getRate(codeTo);
		return amount / fromRate * toRate;
	}
	
	@Override
	public HashSet<String> getAllCodes() {
		return rates.keySet().stream().collect(Collectors.toCollection(HashSet<String>::new));
	}
	
	private List<String> getTopNKeys(int n, Comparator<Double> comparator) {
		return rates.entrySet().stream()
				.sorted(Map.Entry.comparingByValue(comparator))
				.limit(n)
				.map( entry -> entry.getKey())
				.toList();
	}
	
	private double getRate(String code) {
		Double rate = rates.get(code);
		if ( rate == null ) {
			throw new RuntimeException("Unkown currency code " + code );
		}
		return rate;
	}

}

package telran.currency.service;

import java.util.*;

public interface CurrencyConverter {
	List<String> strongestCurrencies(int amount);
	List<String> weakestCurrencies(int amount);
	double convert(String codeFrom, String codeTo, int amount);
	HashSet<String> getAllCodes();
}

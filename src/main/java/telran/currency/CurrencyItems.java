package telran.currency;

import java.util.*;

import telran.currency.service.CurrencyConverter;
import telran.view.InputOutput;
import telran.view.Item;

public class CurrencyItems {
	
	private static final String UNKNOWN_CODE_ERR_MESSAGE = "Unknown code.\nPlease enter currency ISO code e.g. EUR for Euro)";
	private static CurrencyConverter currencyConverter;
	
	public static List<Item> getItems(CurrencyConverter currencyConverter) {
		CurrencyItems.currencyConverter = currencyConverter;
		Item[] items = {
				Item.of("Convert sum of currency A to currency B", CurrencyItems::convert),
				Item.of("Top strongest currencies", CurrencyItems::strongestCurrencies),
				Item.of("Top weakest currencies", CurrencyItems::weakestCurrencies),
				
		};
		return new LinkedList<Item>(List.of(items));
	}
	
	private static void convert(InputOutput io) {
		HashSet<String> availableCodes = currencyConverter.getAllCodes();
		String currA = io.readStringOptions("??? -->", UNKNOWN_CODE_ERR_MESSAGE, availableCodes);
		String currB = io.readStringOptions(currA + " --> ???", UNKNOWN_CODE_ERR_MESSAGE, availableCodes);
		int amount = io.readInt("Enter amount", "Erroneus input");
		double convertedAmount = currencyConverter.convert(currA, currB, amount);
		io.writeLine(String.format("%d %s --> %.8f %s",amount, currA, convertedAmount, currB ));
		
	}
	
	private static void strongestCurrencies(InputOutput io) {
		int numberOfItems = io.readInt("TOP ??? (Enter amount)", "Erroneus input");
		io.writeLine("TOP " + numberOfItems + " STRONGEST CURRENCIES");
		currencyConverter.strongestCurrencies(numberOfItems)
			.forEach( i -> io.writeLine(String.format("%s\t %.8f EUR", i, currencyConverter.convert(i, "EUR", 1))));
	}
	
	private static void weakestCurrencies(InputOutput io) {
		int numberOfItems = io.readInt("TOP ??? (Enter amount)", "Erroneus input");
		io.writeLine("TOP " + numberOfItems + " WEAKEST CURRENCIES");
		currencyConverter.weakestCurrencies(numberOfItems)
			.forEach( i -> io.writeLine(String.format("%s\t %.8f EUR", i, currencyConverter.convert(i, "EUR", 1))));
	}
	

}

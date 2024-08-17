package telran.currency;

import java.util.List;

import telran.currency.service.CurrencyConverter;
import telran.currency.service.FixerApiPerDay;
import telran.view.Item;
import telran.view.Menu;
import telran.view.SystemInputOutput;

public class CurrencyConvertorAppl {

	public static void main(String[] args) {
		CurrencyConverter converter = new FixerApiPerDay();
		List<Item> menuItems = CurrencyItems.getItems(converter);
		menuItems.add(Item.ofExit());
		Menu menu = new Menu("TCP Client Application", menuItems.toArray(Item[]::new));
		menu.perform(new SystemInputOutput());
	}

}

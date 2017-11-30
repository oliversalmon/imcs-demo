package com.trade.injector.miscellanous.testing;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

public class TestLists {

	@Test
	public void test_date_manipulation() {
		
		Calendar cal = Calendar.getInstance();
		 System.out.println("Calendar's toString() is : " + cal + "\n");
	      System.out.println("Time zone is: " + cal.getTimeZone() + "\n");
	      
	      Date date = cal.getTime();
	      //date.setTime(System.currentTimeMillis());
	      System.out.println("Current date and time in Date's toString() is : " + date + "\n");

	      System.out.println("Current date and time in Date's toString() is : ");
	}

	@Test
	public void test_list_addition_modification_with_streams() {
		List<String> master = new ArrayList<String>();
		master.add("A");
		master.add("B");
		master.add("C");
		master.add("D");

		assertEquals(4, master.size());
		master.forEach(a -> System.out.println(a));

		// ok now modify the list based on a condition
		List<String> modifiedList = master.stream().filter(a -> a.equals("A")).map(a -> changeToNumber(a))
				.collect(Collectors.toList());
		assertEquals(1, modifiedList.size());
		modifiedList.forEach(a -> System.out.println(a));

		assertEquals(4, master.size());
		master.forEach(a -> System.out.println(a));

		// now do the non modified list
		List<String> nonModified = master.stream().filter(a -> !a.equals("A")).collect(Collectors.toList());
		assertEquals(3, nonModified.size());
		nonModified.forEach(a -> System.out.println(a));

		// now merge the two lists
		master = Stream.concat(modifiedList.stream(), nonModified.stream()).collect(Collectors.toList());
		assertEquals(4, master.size());
		master.forEach(a -> System.out.println(a));

		// now add one element to modified list
		modifiedList.add("Dinesh");

		// back to master
		master = Stream.concat(modifiedList.stream(), nonModified.stream()).collect(Collectors.toList());
		assertEquals(5, master.size());
		master.forEach(a -> System.out.println(a));

	}

	private String changeToNumber(String alphabet) {

		if (alphabet.equals("A"))
			return "1";
		else
			return alphabet;

	}
}

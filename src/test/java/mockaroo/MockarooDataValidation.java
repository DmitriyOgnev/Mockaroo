package mockaroo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class MockarooDataValidation {

	WebDriver driver;
	List<String[]> records;

	@BeforeClass
	public void setupClass() {
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
	}

	@Test(priority = 1)
	public void testNavigatingToMackaroo() {
		driver.get("https://mockaroo.com/");
		Assert.assertTrue(driver.getTitle().contains("Random Data Generator and API Mocking Tool"));

		String expStr1 = driver.findElement(By.xpath("//div[@class='brand']")).getText();
		String expStr2 = driver.findElement(By.xpath("//div[@class='tagline']")).getText();

		Assert.assertTrue(expStr1.equals("mockaroo") && expStr2.equals("realistic data generator"));

		// input[@id='schema_columns_attributes_6__destroy']/following-sibling::a
	}

	@Test(priority = 2)
	public void testDeletingDefaultFileds() throws InterruptedException {
		int incNum = 0;
		String elementName = "schema_columns_attributes_" + incNum + "__destroy";
		// input[@id='schema_columns_attributes_6__destroy']/following-sibling::a
		while (driver.findElement(By.xpath("//input[@id='" + elementName + "']/following-sibling::a")).isDisplayed()) {

			driver.findElement(By.xpath("//input[@id='" + elementName + "']/following-sibling::a")).click();

			incNum++;
			elementName = "schema_columns_attributes_" + incNum + "__destroy";

			try {
				driver.findElement(By.xpath("//input[@id='" + elementName + "']/following-sibling::a")).isDisplayed();
			} catch (NoSuchElementException e) {
				--incNum;
				elementName = "schema_columns_attributes_" + incNum + "__destroy";
				break;
			}
		}

		Assert.assertFalse(
				driver.findElement(By.xpath("//input[@id='" + elementName + "']/following-sibling::a")).isDisplayed());
	}

	@Test(priority = 3)
	public void testFormSetup() {

		List<WebElement> divSet = driver.findElements(By.xpath("//div[@class='table-header']/div"));
		Assert.assertTrue(divSet.get(0).getText().equals("Field Name") && divSet.get(1).getText().equals("Type")
				&& divSet.get(2).getText().equals("Options"));

		Assert.assertTrue(driver
				.findElement(By.xpath("//div[@class='table-body']//a[@data-blueprint-id=\"columns_fields_blueprint\"]"))
				.isEnabled());

		Assert.assertEquals(driver.findElement(By.xpath("//input[@id='num_rows']")).getAttribute("value"), "1000");

		Select select = new Select(driver.findElement(By.xpath("//select[@id='schema_file_format']")));
		Assert.assertEquals(select.getFirstSelectedOption().getText(), "CSV");

		select = new Select(driver.findElement(By.xpath("//select[@id='schema_line_ending']")));
		Assert.assertEquals(select.getFirstSelectedOption().getText(), "Unix (LF)");

		Assert.assertTrue(driver.findElement(By.xpath("//input[@id='schema_include_header']")).isSelected());
		Assert.assertFalse(driver.findElement(By.xpath("//input[@id='schema_bom']")).isSelected());

	}

	@Test(priority = 4)
	public void testAddFields() throws InterruptedException {

		addField("City");
		addField("Country");

		// Click Download
		driver.findElement(By.xpath("//button[@id='download']")).click();
		Thread.sleep(2000);

	}

	@Test(priority = 5)
	public void testReadExcelFile() {

		// C:\Users\dmitr_000\Downloads\MOCK_DATA.csv

		String mockFile = "C:\\Users\\dmitr_000\\Downloads\\MOCK_DATA.csv";

		String line = "";
		String cvsSplitBy = ",";
		records = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(mockFile))) {

			while ((line = br.readLine()) != null) {

				records.add(line.split(cvsSplitBy));

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		Assert.assertEquals(records.get(0)[0], "Country");
		Assert.assertEquals(records.get(0)[1], "City");
		Assert.assertEquals(records.size(), 1001);

	}

	@Test(priority = 6)
	public void testSplitContent() {

		List<String> citiesList = new ArrayList<String>();
		List<String> countriesList = new ArrayList<String>();

		for (String[] row : records) {
			countriesList.add(row[0]);
			citiesList.add(row[1]);
		}

		List<String> sortedCities = new ArrayList<String>(new TreeSet<String>(citiesList));
		System.out.println(sortedCities);

		String longestCityTitle = sortedCities.get(0);
		String shortestCityTitle = sortedCities.get(0);

		// Calculates the shortest and the longest city names from the list
		for (String city : sortedCities) {
			if (city.length() > longestCityTitle.length())
				longestCityTitle = city;

			if (city.length() < shortestCityTitle.length())
				shortestCityTitle = city;
		}

		System.out.println("Shortest city name is: " + shortestCityTitle);
		System.out.println("Longest city name is: " + longestCityTitle);
		
		
		// Calculates the frequency of country names
		Map<String, Integer> mapCountryFrequency = new HashMap<String, Integer>();
		
		// Goes over each item in the countryList and adds its to the map where country name is a key and it's frequency is the value
		for (String key : countriesList) {
			mapCountryFrequency.put(key, Collections.frequency(countriesList, key));
		}
		
		System.out.println(mapCountryFrequency);
		
	
	
	
	}

	public void addField(String field) throws InterruptedException {
		driver.findElement(By.xpath("//div[@class='table-body']//a[@data-blueprint-id=\"columns_fields_blueprint\"]"))
				.click();

		driver.findElement(
				By.xpath("//div[@class='fields'][not(@style!='display:')]/div/input[contains(@id, '_name')]")).clear();
		driver.findElement(
				By.xpath("//div[@class='fields'][not(@style!='display:')]/div/input[contains(@id, '_name')]"))
				.sendKeys(field);
		driver.findElement(By.xpath(
				"//div[@class='fields'][not(@style!='display:')]/div[contains(@class, 'column')][contains(@class, 'column-type')]/input[contains(@class, 'btn')][contains(@class, 'btn-default')]"))
				.click();

		Thread.sleep(1000);

		// Asserts if the highlightbox is visible
		Assert.assertTrue(driver.findElement(By.xpath("//div[@id='type_dialog_wrap']")).isDisplayed());

		driver.findElement(
				By.xpath("//div[@id='type_dialog_wrap']//div[@class='pull-right']/input[@id='type_search_field']"))
				.clear();

		driver.findElement(
				By.xpath("//div[@id='type_dialog_wrap']//div[@class='pull-right']/input[@id='type_search_field']"))
				.sendKeys(field);

		driver.findElement(By.xpath("//div[@id='type_dialog_wrap']//div[@class='type-name']")).click();

		Thread.sleep(1000);

	}

	@AfterClass
	public void teardownClass() {
		driver.close();
	}

}

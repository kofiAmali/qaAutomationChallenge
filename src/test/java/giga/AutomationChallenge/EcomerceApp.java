package giga.AutomationChallenge;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;

import static org.testng.Assert.assertEquals;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EcomerceApp {
	WebDriver driver;
	
	public EcomerceApp(WebDriver driver) {
		this.driver=driver;
	}
	
	Double getCheckedOutItemsTotalPrice(List <WebElement> elementCartItems) {
		List <Double> itemPricesArray=elementCartItems.stream().map(checkOutItem->{
    		String checkoutItemPrice=checkOutItem.findElement(By.className("inventory_item_price")).getText().replace('$', ' ').trim();
    		
    		return Double.parseDouble(checkoutItemPrice);
    	}).collect(Collectors.toList());
    	
    	Double totalAmount=itemPricesArray.stream().reduce(0.00, (subTotal,element)-> subTotal+element);
    	
    	
    	Double taxValue= Double.parseDouble(this.driver.findElement(By.className("summary_tax_label")).getText().replaceAll("Tax:", "").replace('$', ' ').trim()); 
    	
    	Double totalPrice= totalAmount+taxValue;
    	
    	return totalPrice;
	}
	
    public static void main(String[] args) {
        // Set the path to the ChromeDriver executable
        
        WebDriver driver = new ChromeDriver();
        
        EcomerceApp app= new EcomerceApp(driver);

        app.driver.get("https://www.saucedemo.com/");

        // Find and enter username
        WebElement usernameInput = app.driver.findElement(By.xpath("//input[@data-test='username']"));
        usernameInput.sendKeys("standard_user");

        // Find and enter password
        WebElement passwordInput = app.driver.findElement(By.xpath("//input[@data-test='password']"));
        passwordInput.sendKeys("secret_sauce");

        // Click the login button
        WebElement loginButton = app.driver.findElement(By.xpath("//input[@data-test='login-button']"));
        loginButton.click();

        // Wait for the elements to load
        app.driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        // Maximize the window
        app.driver.manage().window().maximize();

        // List of items to select
        List<String> itemsToSelect = new ArrayList<>();
        itemsToSelect.add("Sauce Labs Backpack");
        itemsToSelect.add("Sauce Labs Bolt T-Shirt");
        itemsToSelect.add("Sauce Labs Onesie");

        // Find all shopping cards
        List<WebElement> shopCards = app.driver.findElements(By.className("inventory_item"));

        
        
        
        
        	for (WebElement containerElement : shopCards) {
                // Find the item name within each containerElement
               WebElement itemNameElement = containerElement.findElement(By.xpath(".//div[@class='inventory_item_label']//a"));
               String itemName = itemNameElement.getText();
                

                // Check if the item name is in the list of items to select
                if (itemsToSelect.contains(itemName)) {
                    // Click the button within the containerElement
                    WebElement addButton = containerElement.findElement(By.xpath(".//button[contains(@class,'btn_primary')]"));
                    addButton.click();
                    
                }
            }
        	
        	//Validate that three items have been added to the card on the cart icon.
        	String countItemsInCart= app.driver.findElement(By.xpath("//span[@class='shopping_cart_badge']")).getText();
        	Assert.assertTrue(countItemsInCart.equals("3"));
        	
        	//Click on the cart Icon
        	app.driver.findElement(By.xpath("//a[@class='shopping_cart_link']")).click();
        	
        	//verify the items on the page match the items you have added to the cart.
        	List <WebElement> cartItems= app.driver.findElements(By.className("cart_item"));

        	
        	cartItems.stream().forEach(cartItem->{
        		String cartItemName= cartItem.findElement(By.className("inventory_item_name")).getText();
        		// Check whether each cartItem name matches an item in the items to select list.
        		Assert.assertTrue(itemsToSelect.contains(cartItemName));
        		
        	});
        	
        
        	
        	//Click on checkOut button
        	app.driver.findElement(By.id("checkout")).click();
        	
        	WebElement firstName= app.driver.findElement(By.id("first-name"));
        	WebElement lastName= app.driver.findElement(By.id("last-name"));
        	WebElement postalCode= app.driver.findElement(By.id("postal-code"));
        	//Enter first name
        	firstName.sendKeys("Test");
        	
        	//Enter Last Name
        	lastName.sendKeys("Ignore");
        	
        	//Enter a zip code
        	postalCode.sendKeys("123");
        	
        	//Verify that the names and zip codes are present on the page.
        	Assert.assertEquals(firstName.getDomProperty("value"), "Test");
        	Assert.assertEquals(lastName.getDomProperty("value"), "Ignore");
        	Assert.assertEquals(postalCode.getDomProperty("value"), "123");
        	
        	//Click on continue
        	app.driver.findElement(By.id("continue")).click();
        	
        	//verify the total amout on the page include tax.
        	
        	List <WebElement> checkOutItemElements = driver.findElements(By.className("cart_item"));
        	
        	//Calculate the total amout on the page including tax
        	Double totalPrice=app.getCheckedOutItemsTotalPrice(checkOutItemElements);
        	
        	
        	
        	// Get the actual total Price on  the check out page.
        	Double actualTotalPrice= Double.parseDouble(driver.findElement(By.xpath("//div[contains(@class,'summary_info_label summary_total_label')]")).getText().replaceAll("Total:", "").replace('$', ' ').trim());  
        	
        	Assert.assertEquals(actualTotalPrice, totalPrice);
        
        	checkOutItemElements.stream().forEach(checkoutElement->{
        		String checkOutItemName= checkoutElement.findElement(By.className("inventory_item_name")).getText();
        		//Verify Items on the page again.
        		Assert.assertTrue(itemsToSelect.contains(checkOutItemName));
        	});
        	
        	//Click on the Finish button
        	app.driver.findElement(By.id("finish")).click();
        	
        	String OrderCompleteText= app.driver.findElement(By.className("complete-header")).getText();
        	
        	Assert.assertEquals(OrderCompleteText, "Thank you for your order!");
        	

        // Close the browser
        app.driver.quit();
    }
}


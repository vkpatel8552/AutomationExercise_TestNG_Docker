package com.test.automation.AutomationExercise_TestNG_Docker.MyWishList;

import java.util.Map;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.automation.AutomationExercise_TestNG_Docker.Pages.HomePage;
import com.test.automation.AutomationExercise_TestNG_Docker.Pages.LoginPage;
import com.test.automation.AutomationExercise_TestNG_Docker.Pages.MenPage;
import com.test.automation.AutomationExercise_TestNG_Docker.Pages.MyWishListPage;
import com.test.automation.AutomationExercise_TestNG_Docker.Pages.ProductDetailsPage;
import com.test.automation.AutomationExercise_TestNG_Docker.Pages.StartUpPage;
import com.test.automation.AutomationExercise_TestNG_Docker.TestBase.TestBase;

import test.automationframework.Exception.CustomException;
import test.automationframework.Utils.Efficacies;
import test.automationframework.Utils.WebDriverUtils;

public class MyWishListTest extends TestBase {

	public WebDriver driver;
	public Properties property;
	public StartUpPage startUpPage;
	public LoginPage loginPage;
	public HomePage homePage;
	public ProductDetailsPage productDetailsPage;
	public MyWishListPage myWishListPage;
	public MenPage menPage;
	public String instanceURL;
	SoftAssert softAssert;
	String homeUrl;
	public ObjectMapper mapper;

	@BeforeClass(alwaysRun = true)
	@Parameters({ "environment", "browser","browserVersion","platform","hubURL" })
	public void LoginToAdminApp(String environment, String browser, String browserVersion, String platform, String hubURL) throws Exception {
		WebDriverUtils utils = new WebDriverUtils();
		utils.initializeDriver(browser, browserVersion, platform, hubURL);
		property = new Efficacies().loadPropertyFile(environment);
		driver = WebDriverUtils.getDriver();
		startUpPage = new StartUpPage(driver);
		homePage = startUpPage.navigateToPage(property.getProperty("baseURL"));
		homeUrl = property.getProperty("baseURL");
		mapper = new ObjectMapper();
	}
	
	@Test(description = "Luma_MyWishList_TC_1: Remove Product from My WishList Page", groups = {
			"Regression Test" })
	public void removeProductFromMyWishList() throws Exception {
		String jsonFilePath = "/MyWishList/MyWishList_TC1.json";

		try {
			softAssert = new SoftAssert();

			// Read data from Json File
			Map<String, String> testData = new Efficacies().readJsonElementInOrder(jsonFilePath, "Data");
			
			// Navigate to Home Page"
			homePage = homePage.navigateToHome(homeUrl)
							   .waitForHomePageToLoad();
			
			softAssert.assertEquals(homePage.getHomePageTitle(), testData.get("homePageTitle"),
					"Home Page Loaded successfully");
			
			loginPage = homePage.clickOnLoginlnk()
								.waitForLoginPageToLoad();

			softAssert.assertEquals(loginPage.getLoginPageHeader(), testData.get("loginPageHeader"),
					"Login Page Loaded successfully");

			homePage = loginPage.enterUserEmail(testData.get("loggedInUserName"))
					.enterPassword(testData.get("loggedInUserPassword"))
					.clickOnLoginBtn()
					.waitForHomePageWithLoggedInUserToLoad();

			softAssert.assertEquals(homePage.getLoggedInUser(), testData.get("userWelcomeMsg"),
					"Login Page Loaded successfully");

			menPage = homePage.clickOnMenlnk()
							   .waitForMenPageToLoad()
							   .scrollToHotSellerSection()
							   .waitForMenPageToLoad();
			
			softAssert.assertEquals(homePage.getHotSellerPageHeader(), testData.get("hotSellerHeader"),
					"Hot Seller section visible after scroll down");

			productDetailsPage = homePage.clickOnSpecificProduct(testData.get("productName"))
										 .waitForProductDetailsPageToLoad();
			
			myWishListPage = productDetailsPage.selectProductSize(testData.get("productSize"))
												   .selectProductColor(testData.get("productColor"))
												   .enterProductQty(testData.get("productQty"))
												   .clickOnAddToWishListlnk();
					
			softAssert.assertEquals(myWishListPage.getAddToWishListSuccessMsg(),testData.get("addToWishListMsg"),
					"Verifying Product Successfully added to Wishlist message");
			
			myWishListPage.clickOnRemoveProductFromWishList(testData.get("productName"))
						  .waitForMyWishListPageToLoad();
			
			softAssert.assertEquals(myWishListPage.getAddToWishListSuccessMsg(),testData.get("removeWishListMsg"),
					"Verifying Product Successfully removed from Wishlist message");
			
			homePage.clickOnLogoutlnk()
					.waitForHomePageToLoad();
			
			softAssert.assertAll();
		} catch (Exception exception) {
			throw new CustomException(exception, driver);
		}
	}

	@AfterClass(alwaysRun = true)
	public void tearDown() {
		startUpPage.killDriver();
	}

}
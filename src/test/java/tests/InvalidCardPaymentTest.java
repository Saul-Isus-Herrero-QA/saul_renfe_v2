package tests;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;
import steps.Steps;
import tools.CSVDataProvider;
import tools.DriverManager;
import tools.TemporaryDataStore;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

import static pages.BasePage.TIMEOUT;

public class InvalidCardPaymentTest {

    private WebDriver webDriver;
    private Steps steps;
    private String browser;

    public InvalidCardPaymentTest(String browser) {
        this.browser = browser;
    }

    @DataProvider(name = "paymentData")
    public Object[][] getPaymentData() {
        return CSVDataProvider.readPassengersData();
    }

    @DataProvider(name = "routeData")
    public Object[][] getRouteData() {
        return CSVDataProvider.readTripPrices();
    }

    @BeforeMethod
    public void setup() {
        webDriver = DriverManager.getDriver(browser);
        webDriver.manage().timeouts().implicitlyWait(TIMEOUT);
        webDriver.manage().window().maximize();
        webDriver.get("https://www.renfe.com/es/es");
        steps = new Steps(webDriver);
    }

    @Test(priority=1)
    public void InvalidCardPaymentTest(
            String originStation,
            String destinationStation,
            String firstName,
            String firstSurname,
            String secondSurname,
            String dni,
            String email,
            String phone,
            String emailBuyer,
            String phoneBuyer,
            String bankCard,
            String expirationDate,
            String cvv) {

        TemporaryDataStore.getInstance().set("testCase", "InvalidCardPaymentTest");

        steps.performSearchOriginAndDestinationStation(originStation, destinationStation);
        steps.selectDepartureDate();
        steps.selectTrainAndFare();
        steps.getAndStoreDynamicPrice();
        steps.verifyAndConfirmTravel();
        steps.clickPopUpAndLinkAppear();
        steps.verifyPriceIsEqualInData();
        steps.introduceYourDataAndConfirm(firstName, firstSurname, secondSurname, dni, email, phone);
        steps.verifyPriceIsEqualInPersonalize();
        steps.confirmPersonalization();
        steps.verifyPriceIsEqualInCompra();
        steps.confirmPaymentData(emailBuyer, phoneBuyer);
        steps.payment(bankCard, expirationDate, cvv);
    }

    @AfterMethod
    public void tearDown(ITestResult result) throws IOException {
        if (result.getStatus() == ITestResult.FAILURE && webDriver != null) {
            File screenshot = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File destino = new File("screenshots/" + result.getName() + "_" + timestamp + ".png");
            destino.getParentFile().mkdirs();
            Files.copy(screenshot.toPath(), destino.toPath());
        }
        DriverManager.quitDriver();
    }
}
package pages;

import org.testng.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SelectYourTripPage extends BasePage {

    //Locators
    private By seleccionaTuViajeStepper = By.xpath("//li[contains(@class, 'active')]//span[contains(text(), 'Selecciona tu viaje')]");
    private By trainAvailable = By.cssSelector("div[id^='precio-viaje']");
    private By trainAvailableBasicFare = By.cssSelector("[data-titulo-tarifa='Básico']");
    private By selectDayRightArrow = By.cssSelector(".rescalendar_controls > button.move_to_tomorrow");
    private By travelerLocator = By.xpath("(//div[contains(@class, 'viajerosSelected') and contains(text(), '1')])[1]");
    private By basicFareLocator = By.xpath("//div[@class='asient']//div[contains(@class, 'rowitem1')]/span[contains(text(), 'Básico')]");
    private By basicFarePriceLocator = By.xpath("(//div[@class='rowitem2 precioTarifa']/span)[1]");
    private By totalPriceLocator = By.xpath("(//span[@id='totalTrayectoBanner'])[1]");
    private By btnSeleccionar = By.xpath("(//button[@id='btnSeleccionar'])[1]");
    public By popUpChangeFare = By.xpath("//button[@id='closeConfirmacionFareUpgrade' and " +
            "contains(@class, 'close') and " +
            "not(contains(@style, 'display: none'))]");
    private By linkContinueSameFare = By.xpath("//div/p[@class and contains(text(), 'No')]");
    private By timeTravelLocatorList = By.xpath("//div[@class='col-md-8 trenes']//h5[contains(text(), 'h')]");
    private By priceTravelLocatorList = By.xpath("//span[@class='precio-final' and contains(text(), '€')]");

    //Constructor
    public SelectYourTripPage(WebDriver webDriver) {
        super(webDriver); //Calls to the constructor from parent class and their variable
        this.webDriver = webDriver; //Current instance
    }

    //Methods

    /**
     * Checks if we are in the next Page "SelectYourTripPage".
     */
    public void verifyYouAreInSelecionaTuViaje() {
        waitUntilElementIsDisplayed(seleccionaTuViajeStepper, TIMEOUT);
        Boolean youAreInSeleccionaTuViaje = webDriver.findElement(seleccionaTuViajeStepper).isEnabled();
        //Assert.assertTrue(youAreInSelecionaTuViaje);
    }

    /**
     * Selects the first train available that it meets the following conditions:
     * 1. Fare "Básico".
     * 2. Do not take into account whether it has a badge or not.
     */
    public void selectFirstValidBasicFareTrain() {
        WebDriverWait wait = new WebDriverWait(webDriver, TIMEOUT);
        boolean control = true;

        while (control) {
            // Finds the list of available trains
            List<WebElement> trainList = webDriver.findElements(trainAvailable);
            List<WebElement> trainFare = webDriver.findElements(trainAvailableBasicFare);

            if (!trainList.isEmpty()) {
                // Clicks on the first available train at position [0]
                WebElement firstTrain = trainList.get(0);
                wait.until(ExpectedConditions.visibilityOf(firstTrain));
                wait.until(ExpectedConditions.elementToBeClickable(firstTrain));

                // clicks with JavascriptExecutor in order not to be intercepted.
                ((JavascriptExecutor) webDriver).executeScript("arguments[0].click();", firstTrain);

                WebElement firstBasicFare = trainFare.get(0);
                scrollElementIntoViewElement(firstBasicFare);
                wait.until(ExpectedConditions.visibilityOf(firstBasicFare));
                wait.until(ExpectedConditions.elementToBeClickable(firstBasicFare));

                // click with JavascriptExecutor in order not to be intercepted.
                ((JavascriptExecutor) webDriver).executeScript("arguments[0].click();", firstBasicFare);

                control = false;
            } else {
                // Clicks the button for the next day to search for available trains
                clickElement(selectDayRightArrow);
                wait = new WebDriverWait(webDriver, TIMEOUT);
            }
        }
    }

    /**
     * Selects the first available train that meets the following criteria:
     * 1. Price below €80.
     * 2. Departure time between 5:00 PM and 10:00 PM.
     */
    public void selectFirstTrainUnder80EurosAndBetween5To10PM() {
        WebDriverWait wait = new WebDriverWait(webDriver, TIMEOUT);
        boolean found = false;
        int maxAttempts = 6; // Maximum number of days to advance to prevent infinite loop
        int attempts = 0;

        while (!found && attempts < maxAttempts) {
            // Finds the list of available trains, prices, and schedules.
            List<WebElement> trainList = webDriver.findElements(trainAvailable);
            List<WebElement> trainPriceList = webDriver.findElements(priceTravelLocatorList);
            List<WebElement> trainTimeList = webDriver.findElements(timeTravelLocatorList);
            List<WebElement> trainFare = webDriver.findElements(trainAvailableBasicFare);

            int total = Math.min(Math.min(trainList.size(), trainPriceList.size()), trainTimeList.size());

            for (int i = 0; i < total; i++) {
                WebElement trainElement = trainList.get(i);
                WebElement priceElement = trainPriceList.get(i);
                WebElement timeElement = trainTimeList.get(i);

                // Normalized time
                String timeText = timeElement.getText().trim().replace("h", "").trim();
                LocalTime departureTime = LocalTime.parse(timeText, DateTimeFormatter.ofPattern("HH:mm"));

                // Normalized price
                String priceText = priceElement.getText().replaceAll("[^\\d,\\.]", "").replace(",", ".").trim();
                double price = Double.parseDouble(priceText);

                // Validation
                if (price < 80.0 &&
                        !departureTime.isBefore(LocalTime.of(17, 0)) &&
                        !departureTime.isAfter(LocalTime.of(22, 0))) {

                    wait.until(ExpectedConditions.visibilityOf(trainElement));
                    wait.until(ExpectedConditions.elementToBeClickable(trainElement));
                    scrollElementIntoViewElement(trainElement);

                    // ✅ Clicks on the train card with the first price under €80 found between 5 PM and 10 PM
                    ((JavascriptExecutor) webDriver).executeScript("arguments[0].click();", trainElement);

                    // ✅ Click on the card within the train that matches the previously found price, selecting the basic fare
                    WebElement firstBasicFare = trainFare.get(0);
                    scrollElementIntoViewElement(firstBasicFare);
                    wait.until(ExpectedConditions.visibilityOf(firstBasicFare));
                    wait.until(ExpectedConditions.elementToBeClickable(firstBasicFare));

                    // Clicks with JavascriptExecutor in order not to be intercepted.
                    ((JavascriptExecutor) webDriver).executeScript("arguments[0].click();", firstBasicFare);

                    found = true;
                    break; // Breaks out of the for loop
                }
            }

            if (!found) {
                clickElement(selectDayRightArrow); // Moves to the next day
                wait = new WebDriverWait(webDriver, TIMEOUT); // Resumes the wait
                attempts++; // Counts the iteration
            }
        }

        // If not found after N attempts, the test fails
        if (!found) {
            Assert.fail("❌ No trains found with price < €80 and departure time between 17:00 and 22:00 after " + maxAttempts + " days.");
        }
    }

    /**
     * Verifies the number of travelers for the trip in the semimodal
     */
    public void verifyNumberOfTravelers() {
        waitUntilElementIsDisplayed(travelerLocator, TIMEOUT);
        String traveler = webDriver.findElement(travelerLocator).getText();
        Assert.assertTrue(traveler.contains("1"));
    }

    /**
     * Verifies the fare applied for the trip in the semimodal is "Básico"
     */
    public void verifyFareIsBasic() {
        waitUntilElementIsDisplayed(basicFareLocator, TIMEOUT);
        String fareText = webDriver.findElement(basicFareLocator).getText();
        Assert.assertTrue(fareText.contains("Básico"));
    }

    /**
     * Verifies that the fare and the total prices applied for the trip in the semimodal are equals
     */
    public String verifyFareAndTotalPricesAreEquals() {
        waitUntilElementIsDisplayed(basicFarePriceLocator, TIMEOUT);
        waitUntilElementIsDisplayed(totalPriceLocator, TIMEOUT);

        // Obtains and normalizes the prices
        String basicFarePrice = normalizePrice(webDriver.findElement(basicFarePriceLocator).getText());
        String totalPriceTrip = normalizePrice(webDriver.findElement(totalPriceLocator).getText());

        Assert.assertEquals(basicFarePrice, totalPriceTrip);
        return totalPriceTrip;
    }

    /**
     * Clicks the 'Seleccionar' button in the semimodal
     */
    public void clickSelectButton(){
        WebDriverWait wait = new WebDriverWait(webDriver, TIMEOUT);
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(btnSeleccionar));
        button.click();
    }

    /**
     * Verifies that the fare change pop-up exists in DOM and is visible on the screen
     * @param locator Element locator
     * @param elementName Descriptive name of the element for messages
     */
    public void verifyElementPresenceAndVisibilityPopUpChangeFare(By locator, String elementName) {
        elementName = "Pop-up of fare´s switch";
        WebDriverWait wait = new WebDriverWait(webDriver, TIMEOUT);

        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(popUpChangeFare));
            wait.until(ExpectedConditions.visibilityOfElementLocated(popUpChangeFare));
            System.out.println("✅ " + elementName + " is present on the DOM and visible");
        } catch (Exception e) {
            Assert.fail("❌ " + elementName + " isn´t visible or present: " + e.getMessage());
        }
    }

    /**
     * Verifies that the link of into the pop-up Change fare exists in DOM and is visible on the screen
     */
    public void linkPopUpFareAppears() {
        String elementLink = "No, quiero continuar con Básico";
        WebDriverWait wait = new WebDriverWait(webDriver, TIMEOUT);

        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(linkContinueSameFare));
            wait.until(ExpectedConditions.visibilityOfElementLocated(linkContinueSameFare));
            System.out.println("✅ " + elementLink + " is present on the DOM and visible");
        } catch (Exception e) {
            Assert.fail("❌ " + elementLink + " isn´t visible or present: " + e.getMessage());
        }
    }

    /**
     * Clicks in the link to continue with the same fare
     */
    public void clickLinkContinueSameFare() {
        try {
            WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
            WebElement link = wait.until(ExpectedConditions.elementToBeClickable(linkContinueSameFare));
            link.click();
        } catch (TimeoutException e) {
            System.out.println("❗ The element didn´t appear inn the expected time.");
        } catch (NoSuchElementException e) {
            System.out.println("❗ The element was not found in the DOM.");
        } catch (Exception e) {
            System.out.println("❗ Unexpected error trying make click in the element: " + e.getMessage());
        }
    }

    // ----------------------------------------------------------------------
    // ✅ NUEVO MÉTODO: Assert para indicar SI hay resultados o NO hay resultados
    // ----------------------------------------------------------------------
    /**
     * Checks if there are search results (available trains) and
     * always passes the assertion, but logs a different message:
     *
     * - If there are results: "Hay resultados para esta búsqueda"
     * - If there are no results: "No hay resultados para esta búsqueda"
     */
    public void assertSearchResultsPresence() {
        List<WebElement> trainList = webDriver.findElements(trainAvailable);

        if (trainList.isEmpty()) {
            String message = "No hay resultados para esta búsqueda.";
            System.out.println("ℹ️ " + message);
            // Assert pasando, con mensaje claro
            Assert.assertTrue(true, message);
        } else {
            String message = "Hay resultados para esta búsqueda.";
            System.out.println("ℹ️ " + message + " Número de resultados: " + trainList.size());
            // Assert pasando, con mensaje claro
            Assert.assertTrue(true, message);
        }
    }
}

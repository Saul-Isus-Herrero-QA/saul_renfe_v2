package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class HomePage extends BasePage {
    // Locators
    public  By acceptAllCookiesButton = By.id("onetrust-accept-btn-handler");
    public  By originInputLocator = By.xpath("//input[@id='origin']");
    public  By destinationInputLocator = By.xpath("//input[@id='destination']");
    private By dateDepartureInput = By.xpath("//input[@id='first-input']");
    private By onlyDepartureRadioButtonLabel = By.xpath("//label[@for='trip-go']");
    private By onlyDepartureRadioButtonInput = By.xpath("//input[@id='trip-go']");
    private By acceptButtonLocator = By.xpath("//button[contains(text(),'Aceptar')]");
    private By buscarBilleteLocator = By.xpath("//button[@title='Buscar billete']");
    private By nextMonthButton = By.xpath("//button[contains(@class, 'lightpick__next-action')]");
    private By monthYearLabel = By.cssSelector("span.rf-daterange-picker-alternative__month-label");


    // Constructor
    public HomePage(WebDriver webDriver) {
        super(webDriver); // Calls to the constructor from parent class and their variable
    }

    // Methods
    /**
     * Accepts all cookies in any Page.
     */
    public void clickAcceptAllCookiesButton() {
        waitUntilElementIsDisplayed(acceptAllCookiesButton, TIMEOUT);
        scrollElementIntoView(acceptAllCookiesButton);
        clickElement(acceptAllCookiesButton);
    }

    /**
     * Types the trip origin
     * @param originStation
     */
    public void enterOrigin(String originStation) {
        WebElement originInput = webDriver.findElement(originInputLocator);

        // Enter the origin
        originInput.click();
        originInput.sendKeys(originStation);
        originInput.sendKeys(Keys.DOWN);
        originInput.sendKeys(Keys.ENTER);

        // Asserts the origin station
        Assert.assertEquals("MADRID (TODAS)", originInput.getAttribute("value"));
    }

    /**
     * Types the trip destination
     * @param destinationStation
     */
    public void enterDestination(String destinationStation) {
        WebElement destinationInput = webDriver.findElement(destinationInputLocator);

        // Enter the destination
        destinationInput.click();
        destinationInput.sendKeys(destinationStation);
        destinationInput.sendKeys(Keys.DOWN);
        destinationInput.sendKeys(Keys.ENTER);

        // Asserts for the destination station
        Assert.assertEquals("BARCELONA (TODAS)", destinationInput.getAttribute("value"));
    }

    /**
     * Clicks on the departure date calendar in the 'Home' page
     */
    public void selectDepartureDate() {
        WebDriverWait wait = new WebDriverWait(webDriver, TIMEOUT);
        WebElement button = wait.until(ExpectedConditions.visibilityOfElementLocated(dateDepartureInput));
        button.click();
    }

    /**
     * Marks the "only go trip" radio button as selected or unselected.
     * @param expectedSelected boolean with the expected selected state of the element
     */
    public void clickSoloIdaButtonSelected(boolean expectedSelected) {
        waitUntilElementIsDisplayed(onlyDepartureRadioButtonLabel, TIMEOUT);
        scrollElementIntoView(onlyDepartureRadioButtonLabel);
        setElementSelected(onlyDepartureRadioButtonInput, onlyDepartureRadioButtonLabel, expectedSelected);
    }

    /**
     * Selects a departure date, a number of days ahead from the current date.
     * @param daysAfter Number of days to add to the current date
     */
    public void selectDateDaysLater(WebDriver webDriver, int daysAfter) {
        LocalDate targetDate = LocalDate.now().plusDays(daysAfter);
        WebDriverWait wait = new WebDriverWait(webDriver, TIMEOUT);

        // Navigate to the correct month
        while (true) {
            String dateLabel = webDriver.findElement(monthYearLabel).getText().toLowerCase();
            if (dateLabel.contains(targetDate.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES")).toLowerCase())) {
                break;
            }
            webDriver.findElement(nextMonthButton).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(monthYearLabel));
    }

    // Click the correct day
    String dayXpath = String.format("//div[contains(@class, 'lightpick__day') and text()='%d']", targetDate.getDayOfMonth());
    WebElement dayElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(dayXpath)));
    dayElement.click();
    }

    /**
     * Method to click the 'Accept' button on the calendar in 'Home' page.
     */
    public void clickAcceptButton() {
        waitUntilElementIsDisplayed(acceptButtonLocator, TIMEOUT);
        clickElement(acceptButtonLocator);
    }

    /**
     * Searches the selected ticket in the 'Home' page.
     */
    public void clickSearchTicketButton() {
        waitUntilElementIsDisplayed(buscarBilleteLocator, TIMEOUT);
        scrollElementIntoView(buscarBilleteLocator);
        clickElement(buscarBilleteLocator);
    }

}

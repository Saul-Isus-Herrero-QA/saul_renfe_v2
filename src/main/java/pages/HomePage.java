package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class HomePage extends BasePage {

    // Locators
    public By acceptAllCookiesButton = By.id("onetrust-accept-btn-handler");
    public By originInputLocator = By.xpath("//input[@id='origin']");
    public By destinationInputLocator = By.xpath("//input[@id='destination']");
    private By dateDepartureInput = By.xpath("//input[@id='first-input']");
    private By onlyDepartureRadioButtonLabel = By.xpath("//label[@for='trip-go']");
    private By onlyDepartureRadioButtonInput = By.xpath("//input[@id='trip-go']");
    private By acceptButtonLocator = By.xpath("//button[contains(text(),'Aceptar')]");
    private By buscarBilleteLocator = By.xpath("//button[@title='Buscar billete']");
    private By nextMonthButton = By.xpath("//button[contains(@class, 'lightpick__next-action')]");
    private By monthYearLabel = By.cssSelector("span.rf-daterange-picker-alternative__month-label");
    private By passengersButton = By.id("passengers-button");          // botón para abrir el selector
    private By adultMinusButton = By.id("adult-minus");                // botón -
    private By adultPlusButton = By.id("adult-plus");                  // botón +
    private By adultCountLabel = By.id("adult-count");                 // label con el número de adultos
    private By passengersSummaryLabel = By.id("passengers-summary");   // resumen tipo "1 Adulto"

    // Constructor
    public HomePage(WebDriver webDriver) {
        super(webDriver); // Calls the constructor from parent class and its variable
    }

    // ---------- Helpers generales ----------

    /**
     * Returns the "Buscar billete" (search ticket) button.
     */
    private WebElement getSearchButton() {
        waitUntilElementIsDisplayed(buscarBilleteLocator, TIMEOUT);
        return webDriver.findElement(buscarBilleteLocator);
    }

    /**
     * Asserts that the search button's enabled state is consistent with
     * the current origin and destination station fields:
     *
     * - If BOTH origin and destination have a non-empty value → button MUST be enabled.
     * - Otherwise → button MUST be disabled.
     */
    private void assertSearchButtonStateMatchesStations() {
        WebElement originInput = webDriver.findElement(originInputLocator);
        WebElement destinationInput = webDriver.findElement(destinationInputLocator);
        WebElement searchButton = getSearchButton();

        String originValue = originInput.getAttribute("value");
        String destinationValue = destinationInput.getAttribute("value");

        boolean originFilled = originValue != null && !originValue.trim().isEmpty();
        boolean destinationFilled = destinationValue != null && !destinationValue.trim().isEmpty();

        if (originFilled && destinationFilled) {
            Assert.assertTrue(
                    searchButton.isEnabled(),
                    "Search button should be ENABLED when both origin and destination are selected. " +
                            "Origin='" + originValue + "', Destination='" + destinationValue + "'."
            );
        } else {
            Assert.assertFalse(
                    searchButton.isEnabled(),
                    "Search button should be DISABLED when origin or destination is missing. " +
                            "Origin='" + originValue + "', Destination='" + destinationValue + "'."
            );
        }
    }

    // ---------- Helpers de pasajeros (1 adulto) ----------

    /**
     * Abre el selector de pasajeros.
     */
    private void openPassengersSelector() {
        waitUntilElementIsDisplayed(passengersButton, TIMEOUT);
        scrollElementIntoView(passengersButton);
        clickElement(passengersButton);
    }

    /**
     * Asserts that the passenger selection is exactly 1 adult.
     *
     * - Adult count label must show "1"
     * - Passenger summary must contain "1" and "Adult" (o "Adulto" según el idioma de la UI)
     */
    public void assertOneAdultPassengerSelected() {
        // Aseguramos que los elementos existen
        WebElement adultCount = webDriver.findElement(adultCountLabel);
        String countText = adultCount.getText().trim();

        int currentCount;
        try {
            currentCount = Integer.parseInt(countText);
        } catch (NumberFormatException e) {
            Assert.fail("Adult count label is not a number. Actual text: '" + countText + "'");
            return;
        }

        // 🔹 ASSERT 1: el número de adultos es 1
        Assert.assertEquals(
                currentCount,
                1,
                "Expected 1 adult passenger but found " + currentCount
        );

        // 🔹 ASSERT 2: el resumen indica claramente 1 adulto
        WebElement summary = webDriver.findElement(passengersSummaryLabel);
        String summaryText = summary.getText().trim().toLowerCase();

        // Ajusta "adult" / "adulto" según el idioma que use la web
        boolean mentionsOne = summaryText.contains("1");
        boolean mentionsAdult = summaryText.contains("adult");

        Assert.assertTrue(
                mentionsOne && mentionsAdult,
                "Passenger summary must indicate exactly 1 adult. Actual text: '" + summary.getText() + "'"
        );
    }

    /**
     * Selecciona EXPLÍCITAMENTE 1 pasajero adulto y lo valida.
     * Se llama a este método desde Steps para que HomePage controle también la selección.
     */
    public void selectOneAdultPassenger() {
        openPassengersSelector();

        WebElement adultMinus = webDriver.findElement(adultMinusButton);
        WebElement adultPlus = webDriver.findElement(adultPlusButton);
        WebElement adultCount = webDriver.findElement(adultCountLabel);

        // Normalizamos: bajamos hasta 0 adultos (si la UI lo permite)
        int currentCount;
        try {
            currentCount = Integer.parseInt(adultCount.getText().trim());
        } catch (NumberFormatException e) {
            throw new AssertionError("Adult count label is not a number. Actual text: '" + adultCount.getText() + "'");
        }

        while (currentCount > 0) {
            adultMinus.click();
            currentCount = Integer.parseInt(adultCount.getText().trim());
        }

        // Subimos a 1 adulto
        adultPlus.click();
        currentCount = Integer.parseInt(adultCount.getText().trim());

        // Valida inmediatamente que hay 1 adulto
        Assert.assertEquals(
                currentCount,
                1,
                "After selection, the number of adult passengers must be 1."
        );

        // Y valida el resumen
        assertOneAdultPassengerSelected();
    }

    // ---------- Métodos existentes con asserts ----------

    /**
     * Accepts all cookies on any page.
     */
    public void clickAcceptAllCookiesButton() {
        waitUntilElementIsDisplayed(acceptAllCookiesButton, TIMEOUT);
        scrollElementIntoView(acceptAllCookiesButton);
        clickElement(acceptAllCookiesButton);

        // Verificamos regla de botón de búsqueda
        assertSearchButtonStateMatchesStations();
    }

    /**
     * Types the trip origin
     *
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
        Assert.assertEquals(
                originInput.getAttribute("value"),
                "MADRID (TODAS)",
                "Origin station value is not as expected."
        );

        // Regla del botón de búsqueda
        assertSearchButtonStateMatchesStations();
    }

    /**
     * Types the trip destination
     *
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
        Assert.assertEquals(
                destinationInput.getAttribute("value"),
                "BARCELONA (TODAS)",
                "Destination station value is not as expected."
        );

        // Regla del botón de búsqueda
        assertSearchButtonStateMatchesStations();
    }

    /**
     * Clicks on the departure date calendar in the 'Home' page
     */
    public void selectDepartureDate() {
        WebDriverWait wait = new WebDriverWait(webDriver, TIMEOUT);
        WebElement button = wait.until(ExpectedConditions.visibilityOfElementLocated(dateDepartureInput));
        button.click();

        // Regla del botón de búsqueda sigue aplicando
        assertSearchButtonStateMatchesStations();
    }

    /**
     * Marks the "only go trip" radio button as selected or unselected.
     *
     * @param expectedSelected boolean with the expected selected state of the element
     */
    public void clickSoloIdaButtonSelected(boolean expectedSelected) {
        waitUntilElementIsDisplayed(onlyDepartureRadioButtonLabel, TIMEOUT);
        scrollElementIntoView(onlyDepartureRadioButtonLabel);
        setElementSelected(onlyDepartureRadioButtonInput, onlyDepartureRadioButtonLabel, expectedSelected);

        WebElement radioInput = webDriver.findElement(onlyDepartureRadioButtonInput);
        Assert.assertEquals(
                radioInput.isSelected(),
                expectedSelected,
                "The 'solo ida' radio button selected state does not match the expected value."
        );

        // Regla del botón de búsqueda
        assertSearchButtonStateMatchesStations();
    }

    /**
     * Selects a departure date, a number of days ahead from the current date.
     *
     * @param webDriver driver instance
     * @param daysAfter Number of days to add to the current date
     */
    public void selectDateDaysLater(WebDriver webDriver, int daysAfter) {
        LocalDate targetDate = LocalDate.now().plusDays(daysAfter);
        WebDriverWait wait = new WebDriverWait(webDriver, TIMEOUT);

        // Navigate to the correct month
        while (true) {
            String dateLabel = webDriver.findElement(monthYearLabel).getText().toLowerCase();
            String targetMonthName = targetDate.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES")).toLowerCase();

            if (dateLabel.contains(targetMonthName)) {
                break;
            }

            webDriver.findElement(nextMonthButton).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(monthYearLabel));
        }

        // Click the correct day
        String dayXpath = String.format(
                "//div[contains(@class, 'lightpick__day') and text()='%d']",
                targetDate.getDayOfMonth()
        );
        WebElement dayElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(dayXpath)));
        dayElement.click();

        // Regla del botón de búsqueda
        assertSearchButtonStateMatchesStations();
    }

    /**
     * Method to click the 'Accept' button on the calendar in 'Home' page.
     */
    public void clickAcceptButton() {
        waitUntilElementIsDisplayed(acceptButtonLocator, TIMEOUT);
        clickElement(acceptButtonLocator);

        // Regla del botón de búsqueda
        assertSearchButtonStateMatchesStations();
    }

    /**
     * Searches the selected ticket in the 'Home' page.
     */
    public void clickSearchTicketButton() {
        WebElement originInput = webDriver.findElement(originInputLocator);
        WebElement destinationInput = webDriver.findElement(destinationInputLocator);
        WebElement searchButton = getSearchButton();

        String originValue = originInput.getAttribute("value");
        String destinationValue = destinationInput.getAttribute("value");

        // Preconditions before clicking
        Assert.assertTrue(
                originValue != null && !originValue.trim().isEmpty(),
                "Origin station must be selected before searching."
        );
        Assert.assertTrue(
                destinationValue != null && !destinationValue.trim().isEmpty(),
                "Destination station must be selected before searching."
        );
        Assert.assertTrue(
                searchButton.isEnabled(),
                "Search button must be enabled before clicking it."
        );

        scrollElementIntoView(buscarBilleteLocator);
        clickElement(buscarBilleteLocator);
    }
}
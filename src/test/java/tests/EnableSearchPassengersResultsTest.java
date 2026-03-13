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

public class EnableSearchPassengersResultsTest {

    private WebDriver webDriver;
    private Steps steps;

    /**
     *  TEST que es para definir el navegador a usar.
     * Se puede pasar por línea de comandos:
     *   -Dbrowser=firefox
     * Si no se pasa via CLI, usa "chrome" por defecto.
     */
    private final String browser = System.getProperty("browser", "chrome");

    // ✅ TestNG necesita un constructor sin parámetros
    public EnableSearchPassengersResultsTest(String browser) {
    }

    /**
     * DataProvider con todos los datos necesarios para el test:
     * originStation, destinationStation, firstName, firstSurname, secondSurname,
     * dni, email, phone, emailBuyer, phoneBuyer, bankCard, expirationDate, cvv
     *
     * IMPORTANTE:
     * Asegúrate de que CSVDataProvider.readPassengersData() devuelve
     * 13 columnas por fila en este orden.
     */
    @DataProvider(name = "paymentData")
    public Object[][] getPaymentData() {
        return CSVDataProvider.readPassengersData();
    }

    // Puedes dejar este DataProvider si lo vas a usar en otros tests
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

    /**
     * Test que utiliza datos del DataProvider "paymentData".
     * Cada fila del Object[][] debe mapearse exactamente a estos 13 parámetros.
     */
    @Test(priority = 1, dataProvider = "paymentData")
    public void EnablementSearchAndPassengersTest(
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
        steps.verifyJourneyResults();
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
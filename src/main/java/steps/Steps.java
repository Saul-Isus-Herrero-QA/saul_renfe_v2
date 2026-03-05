package steps;

import org.openqa.selenium.WebDriver;
import pages.*;
import tools.TemporaryDataStore;

public class Steps extends BasePage {

    private final TemporaryDataStore dataStore;

    public Steps(WebDriver webDriver) {
        // Empty constructor
        super(webDriver); //Calls to the constructor from parent class and their variable
        this.webDriver = webDriver; //Current instance
        this.dataStore = TemporaryDataStore.getInstance();
    }

    /**
     * Select origin and destination stations,
     * @param originStation
     * @param destinationStation
     */
    public void performSearchOriginAndDestinationStation(String originStation, String destinationStation) {
        new HomePage(webDriver).clickAcceptAllCookiesButton();
        HomePage homePage = new HomePage(webDriver);
        homePage.enterOrigin(originStation);
        homePage.enterDestination(destinationStation);
    }

    /**
    * Select the current date as a departure date
    */
    public void selectDepartureDate() {
        HomePage homePage = new HomePage(webDriver);
        homePage.selectDepartureDate();
        homePage.clickSoloIdaButtonSelected(true);

        // Check using the key 'testCase' whether the journey is 5 or 15 days ahead.
        String testCase = (String) TemporaryDataStore.getInstance().get("testCase");

        if ("InvalidDataTraveler15days".equalsIgnoreCase(testCase)) {
            TemporaryDataStore.getInstance().get("totalPriceTrip");
            homePage.selectDateDaysLater(webDriver, 15);
        } else if ("EmptyBuyerDataTest5days".equalsIgnoreCase(testCase)) {
            TemporaryDataStore.getInstance().get("totalPriceTrip");
            homePage.selectDateDaysLater(webDriver, 5);
        } else {
            System.out.println("the trip price isn´t 0");
        }
        homePage.clickAcceptButton();
        homePage.clickSearchTicketButton();
    }

    /**
     * Selects train with less than €80 price
     */
    public void selectTrainMinor80EurosAndBetween5To10PM(){
        new SelectYourTripPage(webDriver).verifyYouAreInSelecionaTuViaje();
        new SelectYourTripPage(webDriver).selectFirstTrainUnder80EurosAndBetween5To10PM();
    }

    /**
     * Verifies if there are results or not
     */
    public void verifyJourneyResults(){
        new SelectYourTripPage(webDriver).verifyYouAreInSelecionaTuViaje();
        new SelectYourTripPage(webDriver).assertSearchResultsPresence();
    }

    /**
     * Selects train and basic fare
     */
    public void selectTrainAndFare(){
        new SelectYourTripPage(webDriver).selectFirstValidBasicFareTrain();
    }

    /**
     * Gets the dynamic price of the page and stores the price in TemporaryDataStore
     */
    public void getAndStoreDynamicPrice() {
        SelectYourTripPage selectYourTripPage = new SelectYourTripPage(webDriver);
        // Retrieve the dynamic price from the Page
        String totalPriceTrip = selectYourTripPage.verifyFareAndTotalPricesAreEquals();
        // Stores the price in TemporaryDataStore
        TemporaryDataStore.getInstance().set("totalPriceTrip", totalPriceTrip);
    }

    /**
     * Verifications of: traveler/s, fare, price and clicks Select button
     */
    public void verifyAndConfirmTravel(){
        new SelectYourTripPage(webDriver).verifyNumberOfTravelers();
        new SelectYourTripPage(webDriver).verifyFareIsBasic();
        new SelectYourTripPage(webDriver).verifyFareAndTotalPricesAreEquals();
        new SelectYourTripPage(webDriver).clickSelectButton();
    }

    /**
     * Verifies the Pop-up and links to continue with the same fare appears, and clicks continue with the same fare
     */
    public void clickPopUpAndLinkAppear(){
        SelectYourTripPage selectYourTripPage = new SelectYourTripPage(webDriver);
        new SelectYourTripPage(webDriver).verifyElementPresenceAndVisibilityPopUpChangeFare(selectYourTripPage.popUpChangeFare, "The PopUp fare change is present and visible");
        new SelectYourTripPage(webDriver).linkPopUpFareAppears();
        new SelectYourTripPage(webDriver).clickLinkContinueSameFare();
    }

    /**
     * Verifies the price in the Page, must be the same in all the flow
     */
    public void verifyPriceIsEqualInData(){
        IntroduceYourDataPage introduceYourDataPage = new IntroduceYourDataPage(webDriver);
        introduceYourDataPage.verifyTotalPrice((String) TemporaryDataStore.getInstance().get("totalPriceTrip"));
    }

    /**
     * Fills in and confirms the traveler's personal data form.
     *
     * This method interacts with the "Introduce Your Data" page. It first verifies that the user
     * is on the correct page, then fills in the traveler's first name, surnames, ID (DNI),
     * email, and phone number. Finally, it clicks the button to continue personalizing the trip.
     *
     * @param firstName       The traveler's first name.
     * @param primerApellido  The traveler's first surname.
     * @param segundoApellido The traveler's second surname.
     * @param dni             The traveler's national ID (DNI).
     * @param email           The traveler's email address.
     * @param phone           The traveler's phone number.
     */
    public void introduceYourDataAndConfirm(String firstName,String primerApellido,String segundoApellido,String dni,String email,String phone){
        IntroduceYourDataPage introduceYourDataPage = new IntroduceYourDataPage(webDriver);
        introduceYourDataPage.verifyYouAreInIntroduceYourDataPage();
        introduceYourDataPage.writeFirstNameField(firstName);
        introduceYourDataPage.writeFirstSurnameField(primerApellido);
        introduceYourDataPage.writeSecondSurnameField(segundoApellido);
        introduceYourDataPage.writeDNIField(dni);
        introduceYourDataPage.writeEmailField(email);
        introduceYourDataPage.writePhoneField(phone);

        String testCase = (String) TemporaryDataStore.getInstance().get("testCase");

        if ("InvalidDataTraveler15days".equalsIgnoreCase(testCase)) {
            introduceYourDataPage.checkErrorInDataField();
        }else {
            introduceYourDataPage.clickPersonalizeTrip();
        }
    }

    /**
     * Verifies the price in the Page, must be the same in all the flow
     */
    public void verifyPriceIsEqualInPersonalize(){
        PersonalizeYourTripPage personalizeYourTripPage = new PersonalizeYourTripPage(webDriver);
        personalizeYourTripPage.verifyTotalPrice((String) TemporaryDataStore.getInstance().get("totalPriceTrip"));
    }

    /**
     * Personalize the travel
     */
    public void confirmPersonalization(){
        new PersonalizeYourTripPage(webDriver).verifyYouAreInPersonalizeYourTravelPage();
        new PersonalizeYourTripPage(webDriver).continueWithPurchase();
    }

    /**
     * Verifies the price in the Page, must be the same in all the flow
     */
    public void verifyPriceIsEqualInCompra(){
        BuyPage buyPage = new BuyPage(webDriver);
        buyPage.verifyTotalPrice((String) TemporaryDataStore.getInstance().get("totalPriceTrip"));
    }

    /**
     * Completes and confirms the payment data form on the purchase page.
     *
     * This method verifies that the user is on the purchase page, then enters the buyer's
     * email and phone number. It proceeds to simulate selecting a new payment card,
     * accepting the purchase conditions, and clicking the button to continue the purchase flow.
     *
     * @param emailBuyer  The buyer's email address.
     * @param phoneBuyer  The buyer's phone number.
     */
    public void confirmPaymentData(String emailBuyer, String phoneBuyer){
        new BuyPage(webDriver).verifyYouAreInCompraPage();
        BuyPage buyPage = new BuyPage(webDriver);
        buyPage.typeEmail(emailBuyer);
        buyPage.writePhoneField(phoneBuyer);
        buyPage.clickPurchaseCard();
        buyPage.clickNewCard();
        buyPage.clickPurchaseCondition();
        buyPage.clickContinuarCompra();
    }

    /**
     * Enters and submits the payment information on the payment gateway page.
     *
     * This method verifies that the user is on the payment gateway page, checks the total price
     * against stored temporary data, and fills in the payment form with the card number, expiration date,
     * and CVV code. Finally, it clicks the button to complete the payment flow.
     *
     * @param bankCard        The credit/debit card number.
     * @param expirationDate  The card's expiration date (MM/YY format).
     * @param cvv             The card's CVV security code.
     */
    public void payment(String bankCard, String expirationDate, String cvv){
        new PaymentGatewayPage(webDriver).verifyYouAreInPasarelaPagoPage();
        PaymentGatewayPage paymentGatewayPage = new PaymentGatewayPage(webDriver);
        paymentGatewayPage.verifyTotalPrice((String) TemporaryDataStore.getInstance().get("totalPriceTrip"));
        paymentGatewayPage.typeBankCard(bankCard);
        paymentGatewayPage.typeExpirationDate(expirationDate);
        paymentGatewayPage.typeCVV(cvv);
        String testCase = (String) TemporaryDataStore.getInstance().get("testCase");

        if ("InvalidCardPaymentTest".equalsIgnoreCase(testCase)) {
            paymentGatewayPage.checkEnabledButton();
            paymentGatewayPage.clickButtonPagar();
            paymentGatewayPage.checkPoupUpError();
        }else if( "EmptyBuyerDataTest5days".equalsIgnoreCase(testCase)) {
            paymentGatewayPage.checkEnabledButton();
        }
    }
}
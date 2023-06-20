
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class FinalProject {

    private HomePage homePage;
    private LoginPage loginPage;
    private RegistrationPage registrationPage;
    private SearchPage searchPage;

    @BeforeMethod
    public void setUp() {
        com.codeborne.selenide.Configuration.timeout = 10000;
        com.codeborne.selenide.Configuration.browser = "chrome";

        homePage = new HomePage();
        loginPage = new LoginPage();
        registrationPage = new RegistrationPage();
        searchPage = new SearchPage();
    }

    @Test
    public void testLoginInvalidCredentials() {
        homePage.openPage();
        homePage.clickLoginButton();
        loginPage.setEmail("random@mail.com");
        loginPage.setPassword("12345678");
        loginPage.clickSubmitButton();
        loginPage.verifyErrorMessage("მეილი ან პაროლი არასწორია, თუ დაგავიწყდათ პაროლი,გთხოვთ ისარგებლოთ პაროლის აღდგენის ფუნქციით!");
        loginPage.verifyPasswordFieldEmpty();
    }

    @Test(dependsOnMethods = "testLoginInvalidCredentials")
    public void testRegistrationForm() {
        homePage.openPage();
        homePage.clickLoginButton();
        loginPage.clickRegistrationButton();
        registrationPage.clickPhysPersonButton();
        registrationPage.clickRegistrationButton();
        registrationPage.verifyEmailCheckboxChecked();
        registrationPage.verifyErrorMessageDisplayed();
    }

    @Test(dependsOnMethods = "testRegistrationForm")
    public void testFilterPriceRange() {
        homePage.openPage();
        homePage.clickRestButton();
        searchPage.scrollDown();
        searchPage.setPriceRange("170", "180");
        searchPage.clickSearchButton();
        searchPage.verifyPricesInRange(170, 180);
    }

    public class HomePage {
        public void openPage() {
            open("https://www.swoop.ge/");
        }

        public void clickLoginButton() {
            $("div[class='HeaderTools swoop-login']").click();
        }

        public void clickRestButton() {
            $("div[style='-webkit-mask: url(/Images/NewDesigneImg/ReHeader/travel.svg) no-repeat center;']").click();
        }
    }

    public class LoginPage {
        public void setEmail(String email) {
            $("input[name='Email']").setValue(email);
        }

        public void setPassword(String password) {
            $("input[name='password']").setValue(password);
        }

        public void clickSubmitButton() {
            $("a[id='AuthBtn']").click();
        }

        public void verifyErrorMessage(String message) {
            $("p[id='authInfo']").shouldHave(text(message));
        }

        public void verifyPasswordFieldEmpty() {
            $("input[name='password']").shouldHave(value(""));
        }

        public void clickRegistrationButton() {
            $("a[id='ui-id-3']").click();
        }
    }

    public class RegistrationPage {
        public void clickPhysPersonButton() {
            $("a[class='profile-tabs__link ui-tabs-anchor']").shouldBe(visible).click();
        }

        public void clickRegistrationButton() {
            $("a[onclick='checkPhysicalFormSubmit()']").click();
        }

        public void verifyEmailCheckboxChecked() {
            $("input[id='pIsSubscribedNewsletter']").shouldHave(attribute("checked"));
        }

        public void verifyErrorMessageDisplayed() {
            $("p[id='physicalInfoMassage']").shouldBe(visible);
        }
    }

    public class SearchPage {
        public void scrollDown() {
            executeJavaScript("window.scrollBy(0,250)");
        }

        public void setPriceRange(String from, String to) {
            $("input[id='minprice']").setValue(from);
            $("input[id='maxprice']").setValue(to);
        }

        public void clickSearchButton() {
            $("div[class='submit-button']").click();
        }

        public void verifyPricesInRange(int minPrice, int maxPrice) {
            $$("p[class='deal-voucher-price']").forEach(item -> {
                int price = Integer.parseInt(item.$("p[class='deal-voucher-price']").getText().replaceAll("[^0-9]", ""));
                assert price >= minPrice && price <= maxPrice;
            });
        }
    }
}

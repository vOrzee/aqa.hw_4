package ru.netology;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;

public class RegistrationTest  {

    public String generateDate(int days, String pattern) {
        return LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern(pattern));
    }

    @Test
    void shouldSubmitDeliveryCardRequestSuccessfully() {
        open("http://localhost:9999");
        $("[data-test-id='city'] input").setValue("Оренбург");
        SelenideElement dateElement = $("[data-test-id='date'] input");
        dateElement.sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        dateElement.setValue(generateDate(3, "dd.MM.yyyy"));
        $("[data-test-id='name'] input").setValue("Смирнов Роман");
        $("[data-test-id='phone'] input").setValue("+79200000000");
        $("[data-test-id='agreement']").click();
        $$("button").find(Condition.text("Забронировать")).click();
        $(withText("Встреча успешно забронирована")).shouldBe(visible, Duration.ofSeconds(15));
    }

    @Test
    void shouldSubmitDeliveryCardRequestWithComplexElementsSuccessfully(){
        open("http://localhost:9999");
        $("[data-test-id='city'] input").setValue("Ор");
        $$(".menu-item").find(Condition.text("Оренбург")).click();
        $("[data-test-id='date'] input").click();
        int currentDay = Integer.parseInt($(".calendar__day_state_today").getText());
        int defermentDays = 3;
        String dateEvent = generateDate(defermentDays, "d.MM.yyyy");
        // Можно обойтись и без поиска последнего дня, сравнив день в dateEvent с currentDay, если меньше, то листаем, но отсрочка в теории может быть и больше месяца
        int lastDayInCurrentMonth = Integer.parseInt(
                $$("[role='gridcell']").filterBy(Condition.not(Condition.empty)).last().getText()
        );
        while (currentDay + defermentDays > lastDayInCurrentMonth){
            lastDayInCurrentMonth = Integer.parseInt(
                    $$("[role='gridcell']").filterBy(Condition.not(Condition.empty)).last().getText()
            );
            $(".calendar__title [data-step='1']").click();
            defermentDays -= lastDayInCurrentMonth - currentDay;
            currentDay = 0;
        }
        $$("[role='gridcell']").find(Condition.text(dateEvent.substring(0, dateEvent.indexOf('.')))).click();
        $("[data-test-id='name'] input").setValue("Смирнов Роман");
        $("[data-test-id='phone'] input").setValue("+79200000000");
        $("[data-test-id='agreement']").click();
        $$("button").find(Condition.text("Забронировать")).click();
        $(withText("Встреча успешно забронирована")).shouldBe(visible, Duration.ofSeconds(15));
    }
}

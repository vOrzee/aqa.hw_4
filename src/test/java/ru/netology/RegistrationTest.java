package ru.netology;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class RegistrationTest  {

    final int defermentDays = 3;

    public String generateDate(int days, String pattern) {
        return LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern(pattern));
    }

    @Test
    void shouldSubmitDeliveryCardRequestSuccessfully() {
        open("http://localhost:9999");
        $("[data-test-id='city'] input").setValue("Оренбург");
        SelenideElement dateElement = $("[data-test-id='date'] input");
        dateElement.sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        String dateEvent = generateDate(defermentDays, "dd.MM.yyyy");
        dateElement.setValue(dateEvent);
        $("[data-test-id='name'] input").setValue("Смирнов Роман");
        $("[data-test-id='phone'] input").setValue("+79200000000");
        $("[data-test-id='agreement']").click();
        $$("button").find(Condition.text("Забронировать")).click();
        $("[data-test-id='notification']").shouldBe(visible, Duration.ofSeconds(15));
        $("[data-test-id='notification'] .notification__content").shouldBe(Condition.matchText(dateEvent), Duration.ofSeconds(15));
    }

    @Test
    void shouldSubmitDeliveryCardRequestWithComplexElementsSuccessfully(){
        open("http://localhost:9999");
        $("[data-test-id='city'] input").setValue("Ор");
        $$(".menu-item").find(Condition.text("Оренбург")).click();
        $("[data-test-id='date'] input").click();
        int currentDay = Integer.parseInt($(".calendar__day_state_today").getText());
        int lastDayInCurrentMonth = Integer.parseInt(
                $$("[role='gridcell']").filterBy(Condition.not(Condition.empty)).last().getText()
        );
        int maxIterations = defermentDays/28 + 1;
        int remainingDays = defermentDays;
        while (currentDay + remainingDays > lastDayInCurrentMonth && maxIterations > 0){
            lastDayInCurrentMonth = Integer.parseInt(
                    $$("[role='gridcell']").filterBy(Condition.not(Condition.empty)).last().getText()
            );
            $(".calendar__title [data-step='1']").click();
            remainingDays -= lastDayInCurrentMonth - currentDay;
            currentDay = 0;
            maxIterations--;
        }
        $$("[role='gridcell']").find(Condition.text(generateDate(defermentDays, "d"))).click();
        $("[data-test-id='name'] input").setValue("Смирнов Роман");
        $("[data-test-id='phone'] input").setValue("+79200000000");
        $("[data-test-id='agreement']").click();
        $$("button").find(Condition.text("Забронировать")).click();
        $("[data-test-id='notification']").shouldBe(visible, Duration.ofSeconds(15));
        $("[data-test-id='notification'] .notification__content").shouldBe(Condition.matchText(generateDate(defermentDays, "dd.MM.yyyy")), Duration.ofSeconds(15));
    }
}

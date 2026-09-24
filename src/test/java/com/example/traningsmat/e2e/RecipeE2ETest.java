package com.example.traningsmat.e2e;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RecipeE2ETest {

    @LocalServerPort
    private int port;

    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        context = browser.newContext();
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true));
        page = context.newPage();
        page.navigate("http://localhost:" + port + "/");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        context.tracing().stop(new Tracing.StopOptions()
                .setPath(Paths.get("trace-" + testInfo.getTestMethod().get().getName() + ".zip")));
        browser.close();
        playwright.close();
    }

    @Test
    void userCanCreateRecipeAndSeeItInList() {
        page.fill("#title", "Glass");
        page.fill("#description", "Glass med nötter och frukt");
        page.selectOption("#category", "ATERHAMTNING");
        page.fill("#calories", "450");
        page.fill("#proteinGrams", "35");
        page.selectOption("#difficulty", "LATT");

        page.click("button:has-text('Spara recept')");

        assertThat(page.locator("#recipe-list")).containsText("Glass");
        assertThat(page.locator("#recipe-list")).containsText("Glass med nötter och frukt");
    }

    @Test
    void userCanDeleteRecipe() {
        page.fill("#title", "Tillfalligt recept");
        page.fill("#description", "Ska tas bort");
        page.selectOption("#category", "STYRKA");
        page.fill("#calories", "300");
        page.fill("#proteinGrams", "20");
        page.selectOption("#difficulty", "MEDEL");
        page.click("button:has-text('Spara recept')");

        assertThat(page.locator("#recipe-list")).containsText("Tillfalligt recept");

        page.click("button:has-text('Ta bort')");

        assertThat(page.locator("#recipe-list")).not().containsText("Tillfalligt recept");
    }
}
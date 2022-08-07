package api;

import io.qameta.allure.Step;
import io.restassured.response.Response;

public class IngredientsApi extends MainApi {
    @Step("Послать GET запрос на ручку /ingredients")
    public Response sendGetIngredients() {
        return reqSpec
                .get("/ingredients");
    }
}

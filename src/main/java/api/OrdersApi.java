package api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import pojo.OrdersPojo;

public class OrdersApi extends MainApi {
    @Step("Послать POST запрос на ручку /orders без accessToken")
    public Response sendPostCreateOrderWithoutAuth(OrdersPojo ordersPojo) {
        return reqSpec.body(ordersPojo)
                .when()
                .post("/orders");
    }

    @Step("Послать POST запрос на ручку /orders с accessToken")
    public Response sendPostCreateOrderWithAuth(OrdersPojo ordersPojo, String accessToken) {
        String pureToken = accessToken.substring(7);
        return reqSpec.auth().oauth2(pureToken)
                .and()
                .body(ordersPojo)
                .when()
                .post("/orders");
    }

    @Step("Послать GET запрос на ручку /orders без accessToken")
    public Response sendGetOrdersWithoutAuth() {
        return reqSpec
                .get("/orders");
    }

    @Step("Послать GET запрос на ручку /orders с accessToken")
    public Response sendGetOrdersWithAuth(String accessToken) {
        String pureToken = accessToken.substring(7);
        return reqSpec.auth().oauth2(pureToken)
                .when()
                .get("/orders");
    }
}

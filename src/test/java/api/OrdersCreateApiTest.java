package api;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pojo.IngredientsResponsePojo;
import pojo.OrdersPojo;
import pojo.UserPojo;

import java.util.List;

public class OrdersCreateApiTest {
    private UserPojo user;
    private UserApi userApi;
    private Response response;
    private boolean created;
    private Response deleteResponse;
    private String expectedMessage;
    private String actualMessage;
    private String accessToken;
    private OrdersApi ordersApi;
    private IngredientsApi ingredientsApi;
    private OrdersPojo ordersPojo;
    private Response ingredientsResponse;
    private IngredientsResponsePojo ingredientsResponsePojo;
    private Response orderResponse;
    private boolean orderCreated;

    @Before
    public void setup() {
        ordersApi = new OrdersApi();
        ingredientsApi = new IngredientsApi();
    }

    @After
    public void teardown() {
        if (created) {
            deleteResponse = userApi.sendDeleteUser(accessToken);
            boolean deleted = userDeletedSuccess(deleteResponse);
        }
    }

    //СОЗДАНИЕ ЗАКАЗА БЕЗ АВТОРИЗАЦИИ
    @Test
    @DisplayName("Проверка создания заказа без авторизации")
    public void createOrderWithoutAuthSuccess() {
        ingredientsResponse = ingredientsApi.sendGetIngredients();
        ingredientsResponsePojo = ingredientsList(ingredientsResponse);
        ordersPojo = new OrdersPojo(List.of(ingredientsResponsePojo.getData().get(0).get_id()));
        orderResponse = ordersApi.sendPostCreateOrderWithoutAuth(ordersPojo);
        orderCreated = orderCreatedSuccess(orderResponse);
        Assert.assertTrue("Ожидается, что заказ успеш создасться", orderCreated);
    }

    //СОЗДАНИЕ ЗАКАЗА БЕЗ АВТОРИЗАЦИИ И БЕЗ ИНГРЕДИЕНТОВ
    @Test
    @DisplayName("Проверка ошибки при создании заказа без авторизации и без ингридиентов")
    public void createOrderWithoutAuthNIngredients() {
        expectedMessage = "Ingredient ids must be provided";
        ordersPojo = new OrdersPojo(null);
        orderResponse = ordersApi.sendPostCreateOrderWithoutAuth(ordersPojo);
        actualMessage = orderNotCreatedMustBeIds400(orderResponse);
        Assert.assertEquals("Ожидается сообщение о том, что нужно заполнить все обязательные поля", expectedMessage, actualMessage);
    }

    //СОЗДАНИЕ ЗАКАЗА БЕЗ АВТОРИЗАЦИИ И C НЕПРАВИЛЬНЫМ ID ИНГРЕДИЕНТА
    @Test
    @DisplayName("Проверка ошибки при создании заказа без авторизации и с неправильным _ID ингридиента")
    public void createOrderWithoutAuthNBadIngredientsIds() {
        ordersPojo = new OrdersPojo(List.of("123"));
        orderResponse = ordersApi.sendPostCreateOrderWithoutAuth(ordersPojo);
        orderNotCreatedIncorrectIds500(orderResponse);
    }

    //СОЗДАНИЕ ЗАКАЗА С АВТОРИЗАЦИЕЙ
    @Test
    @DisplayName("Проверка создания заказа с авторизацией")
    public void createOrderWithAuthSuccess() {
        userApi = new UserApi();
        user = UserPojo.getRandom();
        response = userApi.sendPostRequestRegisterUser(user);
        created = userCreatedSuccess(response);
        accessToken = userAccessToken(response);
        ingredientsResponse = ingredientsApi.sendGetIngredients();
        ingredientsResponsePojo = ingredientsList(ingredientsResponse);
        ordersPojo = new OrdersPojo(List.of(ingredientsResponsePojo.getData().get(0).get_id()));
        orderResponse = ordersApi.sendPostCreateOrderWithAuth(ordersPojo, accessToken);
        orderCreated = orderCreatedSuccess(orderResponse);
        Assert.assertTrue("Ожидается, что заказ успеш создасться", orderCreated);
    }

    //СОЗДАНИЕ ЗАКАЗА С АВТОРИЗАЦИЕЙ И БЕЗ ИНГРЕДИЕНТОВ
    @Test
    @DisplayName("Проверка ошибки при создании заказа с авторизацией и без ингридиентов")
    public void createOrderWithAuthNIngredients() {
        expectedMessage = "Ingredient ids must be provided";
        userApi = new UserApi();
        user = UserPojo.getRandom();
        response = userApi.sendPostRequestRegisterUser(user);
        created = userCreatedSuccess(response);
        accessToken = userAccessToken(response);
        ordersPojo = new OrdersPojo(null);
        orderResponse = ordersApi.sendPostCreateOrderWithAuth(ordersPojo, accessToken);
        actualMessage = orderNotCreatedMustBeIds400(orderResponse);
        Assert.assertEquals("Ожидается сообщение о том, что нужно заполнить все обязательные поля", expectedMessage, actualMessage);
    }

    //СОЗДАНИЕ ЗАКАЗА С АВТОРИЗАЦИЕЙ И C НЕПРАВИЛЬНЫМ ID ИНГРЕДИЕНТА
    @Test
    @DisplayName("Проверка ошибки при создании заказа с авторизацией и с неправильным _ID ингридиента")
    public void createOrderWithAuthNBadIngredientsIds() {
        userApi = new UserApi();
        user = UserPojo.getRandom();
        response = userApi.sendPostRequestRegisterUser(user);
        created = userCreatedSuccess(response);
        accessToken = userAccessToken(response);
        ordersPojo = new OrdersPojo(List.of("123"));
        orderResponse = ordersApi.sendPostCreateOrderWithoutAuth(ordersPojo);
        orderNotCreatedIncorrectIds500(orderResponse);
    }

    public IngredientsResponsePojo ingredientsList(Response response) {
        return response.body().as(IngredientsResponsePojo.class);
    }

    @Step("Получить статус об успешном создании заказа - 200")
    public boolean orderCreatedSuccess(Response response) {
        return response.then()
                .assertThat()
                .statusCode(200)
                .extract()
                .path("success");
    }

    @Step("Получить сообщение о том, что для создания заказа должны быть посланы ID ингридиентов - 400")
    public String orderNotCreatedMustBeIds400(Response response) {
        return response.then()
                .assertThat()
                .statusCode(400)
                .extract()
                .path("message");
    }

    @Step("Получить сообщение о том, что были посланы неправильные ID ингридиентов - 400")
    public void orderNotCreatedIncorrectIds500(Response response) {
        response.then()
                .assertThat()
                .statusCode(500);
    }

    @Step("Получить статус об успешном создании пользователя - 200")
    public boolean userCreatedSuccess(Response response) {
        return response.then()
                .assertThat()
                .statusCode(200)
                .extract()
                .path("success");
    }

    @Step("Получить accessToken")
    public String userAccessToken(Response response) {
        return response.then()
                .extract()
                .path("accessToken");
    }

    @Step("Получить статус об успешном удалении пользователя - 202")
    public boolean userDeletedSuccess(Response response) {
        return response.then()
                .assertThat()
                .statusCode(202)
                .extract()
                .path("success");
    }
}

package api;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pojo.UserPojo;

public class UserUpdateApiTest {
    private UserPojo user;
    private UserPojo userEdited;
    private UserApi userAPI;
    private Response response;
    private boolean created;
    private boolean updated;
    private Response deleteResponse;
    private Response updateResponse;
    private String accessToken;


    @Before
    public void setup() {
        userAPI = new UserApi();
    }

    @After
    public void teardown() {
        if (created) {
            deleteResponse = userAPI.sendDeleteUser(accessToken);
            boolean deleted = userDeletedSuccess(deleteResponse);
        }
    }

    //ИЗМЕНЕНИЕ ДАННЫХ ПОЛЬЗОВАТЕЛЯ С АВТОРИЗАЦИЕЙ
    @Test
    @DisplayName("Проверка обновления пользователя с авторизацией")
    public void editUserWithAuth() {
        user = UserPojo.getRandom();
        response = userAPI.sendPostRequestRegisterUser(user);
        created = userCreatedSuccess(response);
        accessToken = userAccessToken(response);
        userEdited = UserPojo.getRandom();
        updateResponse = userAPI.sendPatchUserWithAuthToken(userEdited, accessToken);
        updated = userUpdatedSuccess(updateResponse);
        Assert.assertTrue("Пользователь не был обновлен, ошибка обновления", updated);
    }

    //ИЗМЕНЕНИЕ ДАННЫХ ПОЛЬЗОВАТЕЛЯ БЕЗ АВТОРИЗАЦИИ
    @Test
    @DisplayName("Проверка обновления пользователя без авторизации")
    public void editUserWithoutAuth() {
        user = UserPojo.getRandom();
        response = userAPI.sendPostRequestRegisterUser(user);
        created = userCreatedSuccess(response);
        accessToken = userAccessToken(response);
        userEdited = UserPojo.getRandom();
        updateResponse = userAPI.sendPatchUserWithoutAuthToken(userEdited);
        updated = updateResponse.then()
                .assertThat()
                .statusCode(401)
                .extract()
                .path("success");
        ;
        Assert.assertFalse("Ожидается, что updateSuccess = false, но пришло true", updated);
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

    @Step("Получить статус об успешном обновлении пользователя - 200")
    public boolean userUpdatedSuccess(Response response) {
        return response.then()
                .assertThat()
                .statusCode(200)
                .extract()
                .path("success");
    }
}

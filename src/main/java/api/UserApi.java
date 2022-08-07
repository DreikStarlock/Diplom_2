package api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import pojo.UserCredentials;
import pojo.UserPojo;

public class UserApi extends MainApi {
    @Step("Послать POST запрос на ручку /auth/register")
    public Response sendPostRequestRegisterUser(UserPojo userPojo) {
        return reqSpec.body(userPojo)
                .when()
                .post("/auth/register");
    }

    @Step("Послать POST запрос на ручку /auth/login")
    public Response sendPostLoginUser(UserCredentials credentials) {
        return reqSpec.body(credentials)
                .when()
                .post("/auth/login");
    }

    @Step("Послать DELETE запрос на ручку /auth/user c accessToken")
    public Response sendDeleteUser(String token) {
        String pureToken = token.substring(7);
        return reqSpec.auth().oauth2(pureToken)
                .when()
                .delete("/auth/user");
    }

    @Step("Послать PATCH запрос на ручку /auth/user c accessToken")
    public Response sendPatchUserWithAuthToken(UserPojo userPojo, String accessToken) {
        String pureToken = accessToken.substring(7);
        return reqSpec.auth().oauth2(pureToken)
                .and()
                .body(userPojo)
                .when()
                .patch("/auth/user");
    }

    @Step("Послать PATCH запрос на ручку /auth/user без accessToken")
    public Response sendPatchUserWithoutAuthToken(UserPojo userPojo) {
        return reqSpec
                .body(userPojo)
                .when()
                .patch("/auth/user");
    }
}

package com.dverbivskyi.controller;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.containsString;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "security.user.name=validUser",
        "security.user.password=secret",
        "security.enable-csrf=true",
        "security.session=if_required"
})
public class ControllerTest {

    @LocalServerPort
    private int serverPort;

    @Before
    public void setUp() throws Exception {
        RestAssured.port = serverPort;
        RestAssured.filters(new ResponseLoggingFilter());
        RestAssured.filters(new RequestLoggingFilter());
    }

    @Test
    public void defaultSSConfigurationBlocksAnyUnauthorizedRequests() throws Exception {
        when().get("/")
                .then().statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void authorizedUserCanGetResource() throws Exception {
        given().auth().preemptive().basic("validUser", "secret")
                .when().get("/")
                .then().statusCode(HttpStatus.OK.value())
                .content(containsString("root GET: "));
    }

    @Test
    public void someUrlsAreNotSecuredByDefaultSSConfiguration() throws Exception {
        //  "/css/**", "/js/**", "/images/**", "/webjars/**", "/**/favicon.ico", "/error"
        when().get("css/backdoor")
                .then().statusCode(HttpStatus.OK.value())
                .content(containsString("If url matches any of these patterns, it's not secured with basic configuration"));
    }

    //0 = {org.springframework.security.web.util.matcher.AntPathRequestMatcher@6579} "Ant [pattern='/css/**']"
    //1 = {org.springframework.security.web.util.matcher.AntPathRequestMatcher@6580} "Ant [pattern='/js/**']"
    //2 = {org.springframework.security.web.util.matcher.AntPathRequestMatcher@6581} "Ant [pattern='/images/**']"
    //3 = {org.springframework.security.web.util.matcher.AntPathRequestMatcher@6582} "Ant [pattern='/webjars/**']"
    //4 = {org.springframework.security.web.util.matcher.AntPathRequestMatcher@6583} "Ant [pattern='/**/favicon.ico']"
    //5 = {org.springframework.security.web.util.matcher.AntPathRequestMatcher@6584} "Ant [pattern='/error']"

}
package com.example.service;

import com.example.api.response.LoginResponse;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@WebServlet("/userapi")
public class UsersServlet extends HttpServlet {

    private Gson gson = new Gson();

    public void checkAndRedirect(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        UserService userService = UserServiceFactory.getUserService();
        String thisUrl = req.getRequestURI();

        PrintWriter out = resp.getWriter();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        String currentUser = getCurrentUser(req);
        if (currentUser == null) {
            String responseStr = gson.toJson(
                    LoginResponse.builder()
                            .error("Please first sign in via " + userService.createLoginURL(thisUrl))
                            .build());
            out.print(responseStr);
            out.flush();

        } else {
            String responseStr = gson.toJson(
                    LoginResponse.builder()
                            .userName(currentUser)
                            .logoutUrl(userService.createLogoutURL(thisUrl))
                            .build());
            out.print(responseStr);
            out.flush();
        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("content-type", "application/json");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        checkAndRedirect(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("content-type", "application/json");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        checkAndRedirect(req, resp);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "X-Requested-With, content-type, Authorization");
        super.doOptions(req, resp);
    }

    private String getCurrentUser(HttpServletRequest req){
        return req.getUserPrincipal()!= null? req.getUserPrincipal().getName(): "Backend-user";
    }
}

package ru.netology.servlet;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.config.AppConfig;
import ru.netology.controller.PostController;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {
    private static final String GET_METHOD = "GET";
    private static final String POST_METHOD = "POST";
    private static final String DELETE_METHOD = "DELETE";
    private static final String API_POSTS_PATH = "/api/posts";
    private static final String API_POSTS_WITH_ID_PATTERN = "/api/posts/\\d+";

    private PostController controller;

    @Override
    public void init() {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        controller = context.getBean(PostController.class);
    }

    private long extractId(String path) {
        return Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final var path = req.getRequestURI();
            final var method = req.getMethod();

            if (method.equals(GET_METHOD) && path.equals(API_POSTS_PATH)) {
                controller.all(resp);
                return;
            }

            if (method.equals(GET_METHOD) && path.matches(API_POSTS_WITH_ID_PATTERN)) {
                final var id = extractId(path);
                controller.getById(id, resp);
                return;
            }

            if (method.equals(POST_METHOD) && path.equals(API_POSTS_PATH)) {
                controller.save(req.getReader(), resp);
                return;
            }

            if (method.equals(DELETE_METHOD) && path.matches(API_POSTS_WITH_ID_PATTERN)) {
                final var id = extractId(path);
                controller.removeById(id, resp);
                return;
            }

            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
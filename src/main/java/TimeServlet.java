import org.springframework.context.ApplicationContext;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeServlet extends HttpServlet {

    private SpringTemplateEngine templateEngine;
    private static final String COOKIE_NAME = "lastTimezone";

    @Override
    public void init() throws ServletException {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setApplicationContext((ApplicationContext) getServletContext());
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");

        templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        String timeZone = request.getParameter("timezone");

        if (timeZone != null && !timeZone.isEmpty() && isValidTimezone(timeZone)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
            dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
            String currentTime = dateFormat.format(new Date());

            // Save the timezone in a cookie
            Cookie cookie = new Cookie(COOKIE_NAME, timeZone);
            response.addCookie(cookie);

            Context context = new Context();
            context.setVariable("currentTime", currentTime);
            context.setVariable("timezone", timeZone);

            templateEngine.process("time", context, response.getWriter());
        } else {
            // Try to get the timezone from the cookie
            String savedTimezone = getTimezoneFromCookie(request);

            if (savedTimezone != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
                dateFormat.setTimeZone(TimeZone.getTimeZone(savedTimezone));
                String currentTime = dateFormat.format(new Date());

                Context context = new Context();
                context.setVariable("currentTime", currentTime);
                context.setVariable("timezone", savedTimezone);

                templateEngine.process("time", context, response.getWriter());
            } else {
                // Fallback to UTC if no valid timezone found
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                templateEngine.process("error", new Context(), response.getWriter());
            }
        }
    }

    private boolean isValidTimezone(String timeZone) {
        try {
            TimeZone.getTimeZone(timeZone);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String getTimezoneFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
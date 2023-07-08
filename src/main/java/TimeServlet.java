import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

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

    private TemplateEngine templateEngine;

    @Override
    public void init() throws ServletException {
        FileTemplateResolver templateResolver = new FileTemplateResolver();
        templateResolver.setPrefix("/WEB-INF/templates/"); // Встановіть шлях до вашого каталогу з шаблонами
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");

        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        String timezone = request.getParameter("timezone");
        String currentTime;
        String currentTimezone;

        if (timezone != null && !timezone.isEmpty() && isValidTimezone(timezone)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
            dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
            currentTime = dateFormat.format(new Date());
            currentTimezone = timezone;

            // Збереження таймзони у куці lastTimezone
            Cookie cookie = new Cookie("lastTimezone", timezone);
            response.addCookie(cookie);
        } else {
            String savedTimezone = getTimezoneFromCookie(request);

            if (savedTimezone != null && !savedTimezone.isEmpty() && isValidTimezone(savedTimezone)) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
                dateFormat.setTimeZone(TimeZone.getTimeZone(savedTimezone));
                currentTime = dateFormat.format(new Date());
                currentTimezone = savedTimezone;
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                currentTime = dateFormat.format(new Date());
                currentTimezone = "UTC";
            }
        }

        Context context = new Context();
        context.setVariable("currentTime", currentTime);
        context.setVariable("timezone", currentTimezone);

        templateEngine.process("time", context, response.getWriter());
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
                if (cookie.getName().equals("lastTimezone")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}

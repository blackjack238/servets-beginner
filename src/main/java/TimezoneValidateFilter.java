import java.io.IOException;
import java.util.TimeZone;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TimezoneValidateFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Ініціалізація фільтра (не потрібно для цього завдання)
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String timeZone = httpRequest.getParameter("timezone");

        if (timeZone != null && !timeZone.isEmpty() && isValidTimezone(timeZone)) {
            // Часовий пояс передано і є валідним - пропускаємо запит далі
            chain.doFilter(request, response);
        } else {
            // Часовий пояс не передано або є некоректним - відправляємо відповідь з помилкою
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            httpResponse.setContentType("text/html");
            httpResponse.getWriter().println("<html><body><h1>Invalid timezone</h1></body></html>");
        }
    }

    @Override
    public void destroy() {
        // Завершення фільтра (не потрібно для цього завдання)
    }

    private boolean isValidTimezone(String timeZone) {
        try {
            TimeZone.getTimeZone(timeZone);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TimeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String timeZone = request.getParameter("timezone");

        // Встановлюємо часовий пояс
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        if (timeZone != null && !timeZone.isEmpty()) {
            dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
        } else {
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        }

        String currentTime = dateFormat.format(new Date());

        // Генеруємо HTML відповідь
        out.println("<html>");
        out.println("<head><title>Current Time</title></head>");
        out.println("<body>");
        out.println("<h1>Current Time</h1>");
        out.println("<p>" + currentTime + "</p>");
        out.println("</body>");
        out.println("</html>");
    }
}
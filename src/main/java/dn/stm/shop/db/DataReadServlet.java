package dn.stm.shop.db;

import dn.stm.shop.utils.ExcelReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/DataReadServlet")
public class DataReadServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(DataReadServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        ExcelReader reader = new ExcelReader();
        // TODO remove it!
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        reader.readFile();

        // put groups and items into session 
//        HttpSession session = httpRequest.getSession(false);
//        session.setAttribute(ITEMS, reader.getAllItems());
//        session.setAttribute(GROUPS, reader.getAllGroups());

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("<h3>");
        response.getWriter().write(reader.getMessage());
        response.getWriter().write("</h3>");
        LOGGER.log(Level.INFO, "Message {0}", reader.getMessage());
        List<String> errorMessages = reader.getErrorMessages();
        if (errorMessages.isEmpty()) {
            response.getWriter().write("Ошибок нет.");
        } else {
            for (String errorMessage : reader.getErrorMessages()) {
                LOGGER.log(Level.INFO, errorMessage);
                response.getWriter().write("<ul style='color:red;'>");
                response.getWriter().write(errorMessage);
                response.getWriter().write("</ul>");
            }
        }
    }
}

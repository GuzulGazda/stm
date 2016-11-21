package dn.stm.shop.db;

import dn.stm.shop.model.Item;
import dn.stm.shop.model.ItemGroup;
import dn.stm.shop.utils.DbUploader;
import static dn.stm.shop.utils.ExcelReader.GROUPS;
import static dn.stm.shop.utils.ExcelReader.ITEMS;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/DataSaveServlet")
public class DataSaveServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(DataSaveServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
	response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        // get groups and items from session 
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession(false);
        List<ItemGroup> groups = (List<ItemGroup>) session.getAttribute(GROUPS);
        List<Item> items = (List<Item>) session.getAttribute(ITEMS);
        // load them to database
        DbUploader.upload(groups, items);
        response.getWriter().write("<h3>");
        // TODO move to resource
        response.getWriter().write("База данных успешно обновлена");
        response.getWriter().write("</h3>");

    }

}

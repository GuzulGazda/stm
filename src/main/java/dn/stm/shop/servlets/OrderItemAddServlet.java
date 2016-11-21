package dn.stm.shop.servlets;

import dn.stm.shop.beans.Catalog;
import dn.stm.shop.beans.OrderBean;
import dn.stm.shop.model.Item;
import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/OrderItemAddServlet")
public class OrderItemAddServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(OrderItemAddServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String itemIdParam = request.getParameter("itemId");
        OrderBean order = (OrderBean) request.getSession(false).getAttribute("Order");
        Catalog catalog = (Catalog) request.getSession(false).getAttribute("Catalog");
	
	request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        try {
            Item item = catalog.getItemById(itemIdParam);
            order.add(item);
            String newOrderedItemsCount = order.getOrderedItemsCount();
            response.getWriter().write(newOrderedItemsCount);
        } catch (Exception e) {
            // TODO Do it in a right way!!
            response.getWriter().write("error");
        } 

    }

}

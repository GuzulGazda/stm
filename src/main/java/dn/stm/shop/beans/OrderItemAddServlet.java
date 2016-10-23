/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dn.stm.shop.beans;

import dn.stm.shop.model.Item;
import java.io.IOException;
import java.util.logging.Level;
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
        LOGGER.log(Level.INFO, "ItemId param is {0}", itemIdParam);

        OrderBean order = (OrderBean) request.getSession().getAttribute("Order");
        Catalog catalog = (Catalog) request.getSession().getAttribute("Catalog");

        response.setContentType("text/plain");
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

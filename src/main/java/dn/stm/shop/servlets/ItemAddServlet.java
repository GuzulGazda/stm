package dn.stm.shop.servlets;

import dn.stm.shop.beans.OrderBean;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/OrderChangeServlet")
public class ItemAddServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ItemAddServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String itemIdParam = request.getParameter("itemId");
        String amountParam = request.getParameter("itemAmount");
        String itemToDeleteParam = request.getParameter("itemToDelete");
        OrderBean orderBean = (OrderBean) request.getSession(false).getAttribute("Order");
	
	request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");


        if (itemToDeleteParam != null && !itemToDeleteParam.isEmpty()) {
            try {
                orderBean.removeItemById(itemToDeleteParam);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, e.getMessage());
            }
            response.getWriter().write("[");
            response.getWriter().write("\"");
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setGroupingSeparator(' ');
            DecimalFormat format = new DecimalFormat("#,###.00", symbols);
            double newOrderCost = orderBean.getOverallCost();
            if  (newOrderCost == 0.0){
                response.getWriter().write("0,00");
            } else {
                response.getWriter().write(format.format(orderBean.getOverallCost()));
            }
            response.getWriter().write("\"");
            response.getWriter().write(",");
            response.getWriter().write(orderBean.getOrderedItemsCount());
            response.getWriter().write("]");
            return;
        }

        if (amountParam == null || amountParam.isEmpty()) {
            // return current amount for this item
            try {
                double oldAmount = orderBean.getAmountForItem(itemIdParam);
                response.getWriter().write(Double.toString(oldAmount));
            } catch (NumberFormatException | IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage());
            }
            return;

        }
        try {
            double amount = Double.parseDouble(amountParam);
            orderBean.setItemAmount(itemIdParam, amount);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        DecimalFormat format = new DecimalFormat("#,###.00", symbols);
        response.getWriter().write("[");
        response.getWriter().write("\"");
        response.getWriter().write(format.format(orderBean.getOverallCost()));
        response.getWriter().write("\"");
        response.getWriter().write(",");
        response.getWriter().write("\"");
        response.getWriter().write(orderBean.getOrderedItemsCount());
        response.getWriter().write("\"");
        response.getWriter().write("]");
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dn.stm.shop.beans;

import java.io.IOException;
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
        System.out.println("ORDEF CHANGE SERVLET doGet");
        String itemIdParam = request.getParameter("itemId");
        String amountParam = request.getParameter("itemAmount");
        String itemToDeleteParam = request.getParameter("itemToDelete");
        System.out.println("Params: itemId" + itemIdParam + ", amountParam: " + amountParam + ", itemToDlete:  " + itemToDeleteParam);

        OrderBean orderBean = (OrderBean) request.getSession().getAttribute("Order");

        response.setContentType("text/plain");

        if (itemToDeleteParam != null && !itemToDeleteParam.isEmpty()) {
            
            System.out.println("\t\tDELETE ITEM WITH ID:  " + itemToDeleteParam);
            try {
                orderBean.removeItemById(itemToDeleteParam);
                System.out.println("\t\t DELETE OK Amount is " + orderBean.getOverallAmount());
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, e.getMessage());
            }
            response.getWriter().write("[");
            response.getWriter().write(Integer.toString(orderBean.getOverallCost()));
            response.getWriter().write(",");
            response.getWriter().write(Integer.toString(orderBean.getOverallAmount()));
            response.getWriter().write("]");
            return;
        }

        if (amountParam == null || amountParam.isEmpty()) {
            // return current amount for this item
            try {
                int oldAmount = orderBean.getAmountForItem(itemIdParam);
                response.getWriter().write(Integer.toString(oldAmount));
            } catch (NumberFormatException | IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage());
            }
            return;

        }
        try {
            int amount = Integer.parseInt(amountParam);
            orderBean.setItemAmount(itemIdParam, amount);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }

        response.getWriter().write("[");
        response.getWriter().write(Integer.toString(orderBean.getOverallCost()));
        response.getWriter().write(",");
        response.getWriter().write(Integer.toString(orderBean.getOverallAmount()));
        response.getWriter().write("]");
    }

}

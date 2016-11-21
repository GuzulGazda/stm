/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dn.stm.shop.servlets;

import dn.stm.shop.beans.OrderBean;
import dn.stm.shop.model.Item;
import dn.stm.shop.utils.MailSender;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author home
 */
@WebServlet("/SendMail")
public class SendMailServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(SendMailServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String name = request.getParameter("name").trim();
        String message = request.getParameter("message").trim();
        String email = request.getParameter("email").trim();

        OrderBean order = (OrderBean) request.getSession().getAttribute("Order");

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        // prepare subject
        String subj = "Заказ на товары от " + name;
        // prepare mail text
        StringBuilder sb = new StringBuilder("Заказ на товары от ")
                .append(name)
                .append(". <br/><br/>");
        sb.append("<table style=\"border: 1px solid rgb(152, 190, 47); border-radius: 10px; text-align: left; width: 100%;\">")
                .append("<th style=\"background-color: #98BE2F; color: #FFF; text-align: center;\">ID</th>")
                .append("<th style=\"background-color: #98BE2F; color: #FFF; text-align: center;\">Наименование товара</th>")
                .append("<th style=\"background-color: #98BE2F; color: #FFF; text-align: center;\">Ед.</th>")
                .append("<th style=\"background-color: #98BE2F; color: #FFF; text-align: center;\">Цена_1</th>")
                .append("<th style=\"background-color: #98BE2F; color: #FFF; text-align: center;\">Цена_2</th>")
                .append("<th style=\"background-color: #98BE2F; color: #FFF; text-align: center;\">Цена_3</th>")
                .append("<th style=\"background-color: #98BE2F; color: #FFF; text-align: center;\">Количество</th>");

        int counter = 0;
        for (Item item : order.getOrderedItems()) {
            if (counter++ % 2 != 0) {
                sb.append("<tr style=\"background-color: #daebad; height: 40px;\">");
            } else {
                sb.append("<tr style=\"height: 40px;\">");
            }
            // ID
            sb.append("<td style=\"text-align: center; border: 1px solid #93b82e;\"> ")
                    .append(item.getId())
                    .append("</td>");
            // name
            sb.append("<td style=\"text-align: left; border: 1px solid #93b82e;\"> ")
                    .append(item.getName())
                    .append("</td>");
            // price unit
            sb.append("<td style=\"text-align: center; border: 1px solid #93b82e;\"> ")
                    .append(item.getItemUnit().getName())
                    .append("</td>");
            // price_1
            sb.append("<td style=\"text-align: center; border: 1px solid #93b82e;\"> ")
                    .append(item.getPrice_1())
                    .append("</td>");
            // price_2
            sb.append("<td style=\"text-align: center; border: 1px solid #93b82e;\"> ")
                    .append(item.getPrice_2())
                    .append("</td>");
            // price_3
            sb.append("<td style=\"text-align: center; border: 1px solid #93b82e;\"> ")
                    .append(item.getPrice_3())
                    .append("</td>");
            // amount
            sb.append("<td style=\"text-align: center; font-weight: bold; border: 1px solid #93b82e;\"> ")
                    .append(getFormattedAmount(item.getAmount(), item.canBePartial()))
                    .append("</td>");
            LOGGER.log(Level.INFO, "!!FORMATTED AMOUNT::: {0}", getFormattedAmount(item.getAmount(), item.canBePartial()));
            // close row
            sb.append("</tr>");
        }
        // close table
        sb.append("</table>");
        // Overall cost:
        String formattedOrderCost = getFormattedCost(order.getOverallCost());
        sb.append("<h3>Общая стоимость заказа: ")
                .append(formattedOrderCost)
                .append("</h3><br/>");
        // additional message
        if (message != null && message.length() > 1) {
            sb.append("<h3>Дополнительное сообщение от ")
                    .append(name)
                    .append("</h3><br/>")
                    .append(message);
        }
        String messageBody = sb.toString();
        LOGGER.log(Level.INFO, "Message subj: {0}", subj);
        LOGGER.log(Level.INFO, "Message email: {0}", email);
        LOGGER.log(Level.INFO, "Message body: \n{0}", messageBody);
        LOGGER.log(Level.INFO, "message: \n{0}", message);
        Thread thread = new Thread(new MailSender(email, subj, messageBody));
        thread.start();
        LOGGER.log(Level.INFO, "Mail sent from {0}", email);
        order.clear();
        response.getWriter().write("OK");
    }

    // method returns String for output order cost
    private String getFormattedCost(double cost) {
        // prepare String for output overall cost
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        DecimalFormat format = new DecimalFormat("#,###.00", symbols);
        String formattedOrderCost = format.format(cost);
        return formattedOrderCost;
    }

    // method returns String for output items amount
    // If item can be partial - show decimal number
    // If not - show integer without comma
    private String getFormattedAmount(double amount, boolean isPartial) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');

        if (isPartial) {
            DecimalFormat dFormat = new DecimalFormat("#,###.000", symbols);
            dFormat.setMaximumFractionDigits(3);
            return dFormat.format(amount);
        }

        DecimalFormat nFormat = new DecimalFormat("#,###", symbols);
        nFormat.setRoundingMode(RoundingMode.HALF_UP);
        nFormat.setMaximumFractionDigits(0);
        return nFormat.format(amount);
    }

}

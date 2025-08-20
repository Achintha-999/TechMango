package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Cart;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Util;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;


@WebServlet(name = "RemoveGromCart", urlPatterns = {"/RemoveGromCart"})
public class RemoveGromCart extends HttpServlet {


@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String cartId = request.getParameter("cartid");
    Gson gson = new Gson();
    JsonObject responseObject = new JsonObject();
    responseObject.addProperty("status", false);

    if (!Util.isInteger(cartId)) {
        responseObject.addProperty("message", "Invalid cart ID!");
    } else {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();
        Transaction tr = s.beginTransaction();

        try {
            User user = (User) request.getSession().getAttribute("user");
            if (user != null) {
             
                Cart cart = (Cart) s.get(Cart.class, Integer.valueOf(cartId));
                if (cart != null && cart.getUser().getId() == user.getId()) {
                    s.delete(cart);
                    tr.commit();
                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "Product removed from cart successfully");
                } else {
                    responseObject.addProperty("message", "Cart item not found or doesn't belong to you");
                }
            } else {
               
                HttpSession session = request.getSession();
                ArrayList<Cart> sessionCart = (ArrayList<Cart>) session.getAttribute("sessionCart");
                
                if (sessionCart != null) {
                    int idToRemove = Integer.parseInt(cartId);
                    boolean removed = false;
                    
                    for (Iterator<Cart> iterator = sessionCart.iterator(); iterator.hasNext();) {
                        Cart cart = iterator.next();
                        if (cart.getProduct().getId() == idToRemove) {
                            iterator.remove();
                            removed = true;
                            break;
                        }
                    }
                    
                    if (removed) {
                        responseObject.addProperty("status", true);
                        responseObject.addProperty("message", "Product removed from cart successfully");
                    } else {
                        responseObject.addProperty("message", "Cart item not found");
                    }
                } else {
                    responseObject.addProperty("message", "Your cart is empty");
                }
            }
        } catch (Exception e) {
            if (tr != null) tr.rollback();
            responseObject.addProperty("message", "Error removing item from cart: " + e.getMessage());
        } finally {
            s.close();
        }
    }

    response.setContentType("application/json");
    response.getWriter().write(gson.toJson(responseObject));
}



}

package Controller;

import DAL.OrderDAO;
import DAL.OrderDetailDAO;
import Model.Order;
import Model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name="OrdersController", urlPatterns={"/Orders"})
public class OrdersController extends HttpServlet {
    private OrderDAO orderDAO;
    private OrderDetailDAO orderDetailDAO;
    @Override
    public void init() { 
        orderDAO = new OrderDAO(); 
        orderDetailDAO = new OrderDetailDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect("Login");
            return;
        }
        String action = request.getParameter("action");
        if (action == null || action.isEmpty()) action = "list";

        switch (action) {
            case "delete":
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    orderDetailDAO.deleteByOrder(id);
                    orderDAO.delete(id);
                } catch (NumberFormatException ignored) {}
                response.sendRedirect("Orders");
                return;
            case "detail":
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    Model.Order order = orderDAO.getById(id);
                    request.setAttribute("order", order);
                    request.setAttribute("items", orderDetailDAO.getByOrder(id));
                    // preload for display names
                    request.setAttribute("suppliers", new DAL.SupplierDAO().getAllSuppliers());
                    request.setAttribute("products", new DAL.ProductDAO().getAllProducts());
                    request.getRequestDispatcher("/WEB-INF/jsp/admin/order_detail.jsp").forward(request, response);
                    return;
                } catch (NumberFormatException ignored) {}
                response.sendRedirect("Orders");
                return;
            case "edit":
            case "create":
                // forward to form
                if ("edit".equals(action)) {
                    try {
                        int id = Integer.parseInt(request.getParameter("id"));
                        request.setAttribute("order", orderDAO.getById(id));
                        request.setAttribute("details", orderDetailDAO.getByOrder(id));
                    } catch (NumberFormatException ignored) {}
                }
                request.getRequestDispatcher("/WEB-INF/jsp/admin/order_form.jsp").forward(request, response);
                return;
            default:
                request.setAttribute("orders", orderDAO.getAll());
                // preload suppliers & products for create modal
                request.setAttribute("suppliers", new DAL.SupplierDAO().getAllSuppliers());
                request.setAttribute("products", new DAL.ProductDAO().getAllProducts());
                request.getRequestDispatcher("/WEB-INF/jsp/admin/orders.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect("Login");
            return;
        }
        String action = request.getParameter("action");
        if ("updateStatus".equals(action)) {
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                String status = request.getParameter("status");
                orderDAO.updateStatus(id, status);
            } catch (NumberFormatException ignored) {}
            response.sendRedirect("Orders");
        } else if ("create".equals(action)) {
            try {
                int supplierId = Integer.parseInt(request.getParameter("supplierId"));
                String status = request.getParameter("status");
                User current = (User) session.getAttribute("currentUser");
                Order o = new Order();
                o.setSupplierId(supplierId);
                o.setOrderedBy(current.getUserId());
                o.setStatus(status != null && !status.isBlank() ? status : "Pending");
                // insert order first
                orderDAO.insert(o);
                // fetch id of newly created order (simple way: get latest of current user)
                Order latest = orderDAO.getAll().stream().filter(or -> or.getOrderedBy() == current.getUserId()).findFirst().orElse(null);
                int orderId = latest != null ? latest.getOrderId() : -1;
                if (orderId > 0) {
                    String[] productIds = request.getParameterValues("productId");
                    String[] quantities = request.getParameterValues("quantity");
                    String[] prices = request.getParameterValues("price");
                    if (productIds != null) {
                        for (int i = 0; i < productIds.length; i++) {
                            try {
                                Model.OrderDetail d = new Model.OrderDetail();
                                d.setOrderId(orderId);
                                d.setProductId(Integer.parseInt(productIds[i]));
                                d.setQuantity(Integer.parseInt(quantities[i]));
                                d.setPrice(Double.parseDouble(prices[i]));
                                orderDetailDAO.insert(d);
                            } catch (Exception ignore) {}
                        }
                    }
                }
            } catch (NumberFormatException ignored) {}
            response.sendRedirect("Orders");
        } else {
            response.sendRedirect("Orders");
        }
    }
}



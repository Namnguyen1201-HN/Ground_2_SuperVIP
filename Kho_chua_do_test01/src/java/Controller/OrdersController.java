package Controller;

import DAL.OrderDAO;
import DAL.OrderDetailDAO;
import DAL.UserDAO;
import DAL.BranchDAO;
import DAL.ProductDAO;
import DAL.ProductDetailDAO;
import Model.Order;
import Model.OrderDetail;
import Model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet(name="OrdersController", urlPatterns={"/Orders"})
public class OrdersController extends HttpServlet {
    private OrderDAO orderDAO;
    private OrderDetailDAO detailDAO;
    private UserDAO userDAO;
    private BranchDAO branchDAO;
    private ProductDAO productDAO;
    private ProductDetailDAO productDetailDAO;

    @Override
    public void init() {
        orderDAO = new OrderDAO();
        detailDAO = new OrderDetailDAO();
        userDAO = new UserDAO();
        branchDAO = new BranchDAO();
        productDAO = new ProductDAO();
        productDetailDAO = new ProductDetailDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
//        if (session == null || session.getAttribute("currentUser") == null) {
//            response.sendRedirect("Login");
//            return;
//        }

        String action = request.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "detail":
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    Model.Order order = orderDAO.getById(id);
                    request.setAttribute("order", order);
                    request.setAttribute("items", detailDAO.getByOrder(id));

                    // Attach display names for detail page
                    if (order != null) {
                        Model.User creator = userDAO.getUserById(order.getCreatedBy());
                        if (creator != null) request.setAttribute("creatorName", creator.getFullName());
                        if (order.getBranchId() != null) {
                            java.util.List<Model.Branch> branches = branchDAO.getAllBranches();
                            if (branches != null) {
                                for (Model.Branch b : branches) {
                                    if (b.getBranchId() == order.getBranchId()) {
                                        request.setAttribute("branchName", b.getBranchName());
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    // Pass all branches for select display
                    request.setAttribute("allBranches", branchDAO.getAllBranches());

                    // Pass product detail labels for current items
                    java.util.List<Model.OrderDetail> curItems = detailDAO.getByOrder(id);
                    java.util.Set<Integer> pdIds = new java.util.HashSet<>();
                    if (curItems != null) {
                        for (Model.OrderDetail d : curItems) pdIds.add(d.getProductDetailId());
                    }
                    DAL.ProductDetailDAO pddao = new DAL.ProductDetailDAO();
                    java.util.Map<Integer,String> pdLabels = pddao.getLabelsByIds(pdIds);
                    request.setAttribute("pdLabels", pdLabels);
                    java.util.Map<Integer, DAL.ProductDetailDAO.DetailInfo> pdInfos = pddao.getInfoByIds(pdIds);
                    request.setAttribute("pdInfos", pdInfos);

                    request.getRequestDispatcher("/WEB-INF/jsp/admin/order_detail.jsp").forward(request, response);
                    return;
                } catch (NumberFormatException ignored) {}
                break;
            case "delete":
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    detailDAO.deleteByOrder(id);
                    orderDAO.delete(id);
                } catch (NumberFormatException ignored) {}
                response.sendRedirect("Orders?msg=deleted");
                return;
            default:
                // filters
                Integer fBranch = null; try { String b = request.getParameter("branch"); if (b!=null && !b.isBlank()) fBranch = Integer.parseInt(b); } catch(Exception ignored) {}
                String fStatus = request.getParameter("st");
                String fKw = request.getParameter("q");
                String fCustomer = request.getParameter("customer");
                String fProduct = request.getParameter("product");
                java.sql.Timestamp fFrom = null, fTo = null;
                try { String fd = request.getParameter("from"); if (fd!=null && !fd.isBlank()) fFrom = java.sql.Timestamp.valueOf(fd + " 00:00:00"); } catch(Exception ignored) {}
                try { String td = request.getParameter("to"); if (td!=null && !td.isBlank()) fTo = java.sql.Timestamp.valueOf(td + " 23:59:59"); } catch(Exception ignored) {}
                int page = 1; try { page = Integer.parseInt(request.getParameter("page")); } catch (Exception ignored) {}
                int pageSize = 10;
                DAL.OrderDAO.PagedOrders po = orderDAO.search(fBranch, fStatus, fKw, fCustomer, fProduct, fFrom, fTo, page, pageSize);
                request.setAttribute("orders", po.orders);
                request.setAttribute("total", po.total);
                request.setAttribute("page", po.page);
                request.setAttribute("pageSize", po.pageSize);
                request.setAttribute("fBranch", fBranch);
                request.setAttribute("fStatus", fStatus);
                request.setAttribute("fKw", fKw);
                request.setAttribute("fCustomer", fCustomer);
                request.setAttribute("fProduct", fProduct);
                request.setAttribute("fFrom", request.getParameter("from"));
                request.setAttribute("fTo", request.getParameter("to"));

                // Build display-name maps for branch and creator
                java.util.Map<Integer, String> creatorNames = new java.util.HashMap<>();
                java.util.Map<Integer, String> branchNames = new java.util.HashMap<>();

                // Collect unique creator IDs to avoid many queries
                java.util.Set<Integer> creatorIds = new java.util.HashSet<>();
                if (po.orders != null) {
                    for (Model.Order o : po.orders) {
                        creatorIds.add(o.getCreatedBy());
                        if (o.getBranchId() != null) {
                            branchNames.put(o.getBranchId(), null); // placeholder keys
                        }
                    }
                }

                // Resolve user names
                for (Integer uid : creatorIds) {
                    Model.User u = userDAO.getUserById(uid);
                    if (u != null) creatorNames.put(uid, u.getFullName());
                }

                // Resolve branches via one fetch
                java.util.List<Model.Branch> branches = branchDAO.getAllBranches();
                if (branches != null) {
                    for (Model.Branch b : branches) {
                        branchNames.put(b.getBranchId(), b.getBranchName());
                    }
                }

                request.setAttribute("creatorNames", creatorNames);
                request.setAttribute("branchNames", branchNames);
                request.setAttribute("allBranches", branches);

                // Pass message flag for SweetAlert2
                String msg = request.getParameter("msg");
                if (msg != null) request.setAttribute("msg", msg);
                request.getRequestDispatcher("/WEB-INF/jsp/admin/orders.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        // Allow updating status without requiring login (no roles yet)
        if ("updateStatus".equals(action)) {
            try {
                int orderId = Integer.parseInt(request.getParameter("orderId"));
                String statusVal = request.getParameter("status");
                if (statusVal != null && !statusVal.isBlank()) {
                    orderDAO.updateStatus(orderId, statusVal);
                }
                response.sendRedirect("Orders?msg=status-updated");
                return;
            } catch (Exception ignored) {}
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect("Login");
            return;
        }
        if ("create".equals(action)) {
            User current = (User) session.getAttribute("currentUser");
            Order o = new Order();
            String branchId = request.getParameter("branchId");
            if (branchId != null && !branchId.isBlank()) o.setBranchId(Integer.parseInt(branchId));
            o.setCreatedBy(current.getUserId());
            o.setOrderStatus(request.getParameter("status"));
            String custId = request.getParameter("customerId");
            if (custId != null && !custId.isBlank()) o.setCustomerId(Integer.parseInt(custId));
            o.setPaymentMethod(request.getParameter("paymentMethod"));
            o.setNotes(request.getParameter("notes"));
            o.setGrandTotal(new BigDecimal("0"));
            int orderId = orderDAO.insert(o);
            if (orderId > 0) {
                String[] productDetailIds = request.getParameterValues("productDetailId");
                String[] quantities = request.getParameterValues("quantity");
                if (productDetailIds != null) {
                    for (int i = 0; i < productDetailIds.length; i++) {
                        try {
                            OrderDetail d = new OrderDetail();
                            d.setOrderId(orderId);
                            d.setProductDetailId(Integer.parseInt(productDetailIds[i]));
                            d.setQuantity(Integer.parseInt(quantities[i]));
                            detailDAO.insert(d);
                        } catch (Exception ignored) {}
                    }
                }
            }
            response.sendRedirect("Orders?msg=created");
            return;
        } else if ("updateBranch".equals(action)) {
            try {
                int orderId = Integer.parseInt(request.getParameter("orderId"));
                String bid = request.getParameter("branchId");
                Integer branchId = (bid == null || bid.isBlank()) ? null : Integer.parseInt(bid);
                orderDAO.updateBranch(orderId, branchId);
                response.sendRedirect("Orders?action=detail&id=" + orderId + "&msg=branch-updated");
                return;
            } catch (Exception ignored) {}
        }
        response.sendRedirect("Orders");
    }
}



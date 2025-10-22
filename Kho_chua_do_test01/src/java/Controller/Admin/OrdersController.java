package Controller.Admin;

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
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@WebServlet(name = "OrdersController", urlPatterns = {"/Orders"})
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
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect("Login");
            return;
        }

        // Check role permission
        User currentUser = (User) session.getAttribute("currentUser");
        int roleID = currentUser.getRoleId();
        
        // Chỉ cho phép Chủ chuỗi (0), Quản lý chi nhánh (1), và Nhân viên bán hàng (2) xem orders
        if (roleID != 0 && roleID != 1 && roleID != 2) {
            response.sendRedirect("DashBoard?error=no_permission");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "detail":
                handleDetail(request, response);
                return;
            default:
                handleList(request, response);
                return;
        }
    }

    private void handleDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Order order = orderDAO.getById(id);
            
            if (order == null) {
                response.sendRedirect("Orders?error=order_not_found");
                return;
            }
            
            // Check permission: only allow viewing orders from own branch (except role 0)
            HttpSession session = request.getSession();
            User currentUser = (User) session.getAttribute("currentUser");
            int roleID = currentUser.getRoleId();
            Integer userBranchID = currentUser.getBranchId();
            
            // Role 1 or 2 can only view orders from their branch
            if ((roleID == 1 || roleID == 2) && order.getBranchId() != null) {
                if (userBranchID == null || !userBranchID.equals(order.getBranchId())) {
                    response.sendRedirect("Orders?error=no_permission");
                    return;
                }
            }
            
            request.setAttribute("order", order);
            request.setAttribute("items", detailDAO.getByOrder(id));

            if (order != null) {
                User creator = userDAO.getUserById(order.getCreatedBy());
                if (creator != null) request.setAttribute("creatorName", creator.getFullName());

                if (order.getBranchId() != null) {
                    List<Model.Branch> branches = branchDAO.getAllBranches();
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

            // for selects in detail page
            request.setAttribute("allBranches", branchDAO.getAllBranches());

            // product detail labels for items
            List<OrderDetail> curItems = detailDAO.getByOrder(id);
            Set<Integer> pdIds = new HashSet<>();
            if (curItems != null) {
                for (OrderDetail d : curItems) pdIds.add(d.getProductDetailId());
            }
            DAL.ProductDetailDAO pddao = new DAL.ProductDetailDAO();
            Map<Integer, String> pdLabels = pddao.getLabelsByIds(pdIds);
            request.setAttribute("pdLabels", pdLabels);
            Map<Integer, DAL.ProductDetailDAO.DetailInfo> pdInfos = pddao.getInfoByIds(pdIds);
            request.setAttribute("pdInfos", pdInfos);

            request.getRequestDispatcher("/WEB-INF/jsp/admin/order_detail.jsp").forward(request, response);
        } catch (NumberFormatException ignored) {
            response.sendRedirect("Orders");
        }
    }

    private void handleList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        int roleID = currentUser.getRoleId();
        Integer userBranchID = currentUser.getBranchId();
        
        // ---------- read filters ----------
        Integer fBranch = null;
        try {
            // accept either "branch" or "branchId" coming from different versions of the JSP
            String b = request.getParameter("branch");
            if (b == null || b.isBlank()) b = request.getParameter("branchId");
            if (b != null && !b.isBlank() && !"0".equals(b)) {
                fBranch = Integer.parseInt(b);
            }
        } catch (Exception ignored) {}
        
        // Force filter by branch for role 1 and 2
        if (roleID == 1 || roleID == 2) {
            fBranch = userBranchID;
        }

        String fStatus = request.getParameter("st");
        String fKw = request.getParameter("q");
        String fCustomer = request.getParameter("customer");
        String fProduct = request.getParameter("product");

        // dates (fromDate/toDate expected in yyyy-MM-dd from <input type="date">)
        Timestamp fFrom = null, fTo = null;
        try {
            String fd = request.getParameter("fromDate");
            if (fd != null && !fd.isBlank()) {
                fFrom = Timestamp.valueOf(fd + " 00:00:00");
            }
        } catch (Exception ignored) {}
        try {
            String td = request.getParameter("toDate");
            if (td != null && !td.isBlank()) {
                fTo = Timestamp.valueOf(td + " 23:59:59");
            }
        } catch (Exception ignored) {}

        // min/max spent (we use Double here; change to BigDecimal if your DAO expects BigDecimal)
        Double minSpent = null, maxSpent = null;
        try {
            String ms = request.getParameter("minSpent");
            if (ms != null && !ms.isBlank()) minSpent = Double.parseDouble(ms);
        } catch (Exception ignored) {}
        try {
            String mx = request.getParameter("maxSpent");
            if (mx != null && !mx.isBlank()) maxSpent = Double.parseDouble(mx);
        } catch (Exception ignored) {}

        // pagination
        int page = 1;
        try {
            page = Integer.parseInt(request.getParameter("page"));
            if (page < 1) page = 1;
        } catch (Exception ignored) {}
        int pageSize = 10;

        // ---------- call DAO ----------
        OrderDAO.PagedOrders po = orderDAO.search(
                fBranch, fStatus, fKw, fFrom, fTo, minSpent, maxSpent, page, pageSize
        );

        // ---------- set attributes for JSP ----------
        request.setAttribute("orders", po.orders);
        request.setAttribute("total", po.total);
        request.setAttribute("page", po.page);
        request.setAttribute("pageSize", po.pageSize);

        // keep current filter values so JSP can re-populate inputs
        request.setAttribute("fBranch", fBranch);
        request.setAttribute("fStatus", fStatus);
        request.setAttribute("fKw", fKw);
        request.setAttribute("fCustomer", fCustomer);
        request.setAttribute("fProduct", fProduct);
        request.setAttribute("fFromDate", request.getParameter("fromDate"));
        request.setAttribute("fToDate", request.getParameter("toDate"));

        // expose min/max back to JSP so inputs keep their values
        request.setAttribute("fMinSpent", request.getParameter("minSpent"));
        request.setAttribute("fMaxSpent", request.getParameter("maxSpent"));

        // Build display-name maps for branch and creator (minimize DB calls)
        Map<Integer, String> creatorNames = new HashMap<>();
        Map<Integer, String> branchNames = new HashMap<>();

        Set<Integer> creatorIds = new HashSet<>();
        if (po.orders != null) {
            for (Order o : po.orders) {
                creatorIds.add(o.getCreatedBy());
                if (o.getBranchId() != null) branchNames.put(o.getBranchId(), null);
            }
        }

        for (Integer uid : creatorIds) {
            User u = userDAO.getUserById(uid);
            if (u != null) creatorNames.put(uid, u.getFullName());
        }

        List<Model.Branch> branches = branchDAO.getAllBranches();
        if (branches != null) {
            for (Model.Branch b : branches) branchNames.put(b.getBranchId(), b.getBranchName());
        }

        request.setAttribute("creatorNames", creatorNames);
        request.setAttribute("branchNames", branchNames);
        request.setAttribute("allBranches", branches);
        
        // Pass user role info to JSP
        request.setAttribute("currentUserRole", roleID);
        request.setAttribute("currentUserBranch", userBranchID);

        // Pass message flag for SweetAlert2
        String msg = request.getParameter("msg");
        String error = request.getParameter("error");
        if (msg != null) request.setAttribute("msg", msg);
        if (error != null) request.setAttribute("error", error);

        request.getRequestDispatcher("/WEB-INF/jsp/admin/orders.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect("Login");
            return;
        }
        
        User currentUser = (User) session.getAttribute("currentUser");
        int roleID = currentUser.getRoleId();
        Integer userBranchID = currentUser.getBranchId();
        
        String action = request.getParameter("action");
        
        if ("updateStatus".equals(action)) {
            try {
                int orderId = Integer.parseInt(request.getParameter("orderId"));
                Order order = orderDAO.getById(orderId);
                
                if (order == null) {
                    response.sendRedirect("Orders?error=order_not_found");
                    return;
                }
                
                // Check permission: only allow updating orders from own branch (except role 0)
                if ((roleID == 1 || roleID == 2) && order.getBranchId() != null) {
                    if (userBranchID == null || !userBranchID.equals(order.getBranchId())) {
                        response.sendRedirect("Orders?error=no_permission");
                        return;
                    }
                }
                
                String statusVal = request.getParameter("status");
                if (statusVal != null && !statusVal.isBlank()) {
                    orderDAO.updateStatus(orderId, statusVal);
                }
                response.sendRedirect("Orders?msg=status-updated");
                return;
            } catch (Exception ignored) {
            }
        }
        
        if ("create".equals(action)) {
            User current = (User) session.getAttribute("currentUser");
            Order o = new Order();
            String branchId = request.getParameter("branchId");
            if (branchId != null && !branchId.isBlank()) {
                o.setBranchId(Integer.parseInt(branchId));
            }
            o.setCreatedBy(current.getUserId());
            o.setOrderStatus(request.getParameter("status"));
            String custId = request.getParameter("customerId");
            if (custId != null && !custId.isBlank()) {
                o.setCustomerId(Integer.parseInt(custId));
            }
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
                        } catch (Exception ignored) {
                        }
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
            } catch (Exception ignored) {
            }
        }
        response.sendRedirect("Orders");
    }
}

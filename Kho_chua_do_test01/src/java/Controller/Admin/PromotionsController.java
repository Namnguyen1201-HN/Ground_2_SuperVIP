package Controller.Admin;

import DAL.PromotionDAO;
import DAL.BranchDAO;
import DAL.ProductDetailDAO;
import Model.Promotion;
import Model.User;
import Model.Branch;
import Model.ProductDetail;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "PromotionsController", urlPatterns = {"/Promotions"})
public class PromotionsController extends HttpServlet {

    private PromotionDAO promotionDAO;
    private BranchDAO branchDAO;
    private ProductDetailDAO productDetailDAO;

    @Override
    public void init() {
        promotionDAO = new PromotionDAO();
        branchDAO = new BranchDAO();
        productDetailDAO = new ProductDetailDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null) {
            response.sendRedirect("Login");
            return;
        }

        String action = request.getParameter("action");
        
        if ("create".equals(action)) {
            if (currentUser.getRoleId() != 0) {
                response.sendRedirect("Promotions?error=access_denied");
                return;
            }
            showCreateForm(request, response);
        } else if ("edit".equals(action)) {
            if (currentUser.getRoleId() != 0) {
                response.sendRedirect("Promotions?error=access_denied");
                return;
            }
            showEditForm(request, response);
        } else if ("view".equals(action)) {
            showPromotionDetail(request, response);
        } else if ("loadProducts".equals(action)) {
            if (currentUser.getRoleId() != 0) {
                response.sendRedirect("Promotions?error=access_denied");
                return;
            }
            loadProductsJson(request, response);
        } else {
            showPromotionsList(request, response, currentUser);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        // Check user authentication and role
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null) {
            response.sendRedirect("Login");
            return;
        }
        
        // Only Admin can perform create, update, delete operations
        if (currentUser.getRoleId() != 0) {
            response.sendRedirect("Promotions?error=access_denied");
            return;
        }
        
        String action = request.getParameter("action");
        
        if ("create".equals(action)) {
            createPromotion(request, response);
        } else if ("update".equals(action)) {
            updatePromotion(request, response);
        } else if ("delete".equals(action)) {
            deletePromotion(request, response);
        }
    }

    private void showPromotionsList(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws ServletException, IOException {
        
        // Get filter parameters
        String statusFilter = request.getParameter("status");
        String discountFilter = request.getParameter("discount");
        String searchKeyword = request.getParameter("search");
        
        // Debug logging
        System.out.println("Filter Parameters - Status: " + statusFilter + ", Discount: " + discountFilter + ", Search: " + searchKeyword);
        
        List<Promotion> promotions;
        
        // Role-based data filtering
        if (currentUser.getRoleId() == 0) { // Admin - see all promotions
            promotions = promotionDAO.getAllPromotions();
        } else { // Branch Manager - see only branch promotions
            promotions = promotionDAO.getPromotionsByBranch(currentUser.getBranchId());
        }
        
        // Apply filters
        promotions = applyFilters(promotions, statusFilter, discountFilter, searchKeyword);
        
        // Get statistics for cards
        int totalPromotions = currentUser.getRoleId() == 0 
            ? promotionDAO.countAllPromotions()
            : promotionDAO.countPromotionsByBranch(currentUser.getBranchId());
        
        int activePromotions = promotionDAO.countActivePromotions();
        int expiredPromotions = promotionDAO.countExpiredPromotions();
        int scheduledPromotions = promotionDAO.countScheduledPromotions();
        
        request.setAttribute("promotions", promotions);
        request.setAttribute("totalPromotions", totalPromotions);
        request.setAttribute("activePromotions", activePromotions);
        request.setAttribute("expiredPromotions", expiredPromotions);
        request.setAttribute("scheduledPromotions", scheduledPromotions);
        request.setAttribute("currentUser", currentUser);
        
        request.getRequestDispatcher("/WEB-INF/jsp/admin/promotions.jsp").forward(request, response);
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Get all branches for dropdown
        List<Branch> branches = branchDAO.getAllBranches();
        request.setAttribute("branches", branches);
        
        // Get all products for dropdown (you might want to implement pagination here)
        // For now, we'll just provide the structure
        
        request.getRequestDispatcher("/WEB-INF/jsp/admin/promotion_create.jsp").forward(request, response);
    }

    private void createPromotion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            String promoName = request.getParameter("promoName");
            String discountPercentStr = request.getParameter("discountPercent");
            String startDateStr = request.getParameter("startDate");
            String endDateStr = request.getParameter("endDate");
            String[] branchIdStrs = request.getParameterValues("branchIds");
            String[] productDetailIdStrs = request.getParameterValues("productDetailIds");
            
            // Validate inputs
            if (promoName == null || promoName.trim().isEmpty()) {
                response.sendRedirect("Promotions?error=invalid_name");
                return;
            }
            
            BigDecimal discountPercent = new BigDecimal(discountPercentStr);
            if (discountPercent.compareTo(BigDecimal.ZERO) <= 0 || discountPercent.compareTo(new BigDecimal("100")) > 0) {
                response.sendRedirect("Promotions?error=invalid_discount");
                return;
            }
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date startDate = sdf.parse(startDateStr);
            java.util.Date endDate = sdf.parse(endDateStr);
            
            if (startDate.after(endDate)) {
                response.sendRedirect("Promotions?error=invalid_date_range");
                return;
            }
            
            // Create promotion object
            Promotion promotion = new Promotion();
            promotion.setPromoName(promoName);
            promotion.setDiscountPercent(discountPercent);
            promotion.setStartDate(startDate);
            promotion.setEndDate(endDate);
            
            // Parse branch IDs - if empty, apply to all branches
            List<Integer> branchIds = new ArrayList<>();
            if (branchIdStrs != null && branchIdStrs.length > 0) {
                for (String branchIdStr : branchIdStrs) {
                    if (branchIdStr != null && !branchIdStr.trim().isEmpty()) {
                        branchIds.add(Integer.parseInt(branchIdStr));
                    }
                }
            }
            // If no branches selected, get all branches
            if (branchIds.isEmpty()) {
                List<Branch> allBranches = branchDAO.getAllBranches();
                for (Branch branch : allBranches) {
                    branchIds.add(branch.getBranchId());
                }
            }
            
            // Parse product detail IDs - if empty, apply to all products
            List<Integer> productDetailIds = new ArrayList<>();
            if (productDetailIdStrs != null && productDetailIdStrs.length > 0) {
                for (String productDetailIdStr : productDetailIdStrs) {
                    if (productDetailIdStr != null && !productDetailIdStr.trim().isEmpty()) {
                        productDetailIds.add(Integer.parseInt(productDetailIdStr));
                    }
                }
            }
            // If no products selected, leave empty (DAO will handle as "apply to all")
            
            // Create promotion
            boolean success = promotionDAO.createPromotion(promotion, branchIds, productDetailIds);
            
            if (success) {
                response.sendRedirect("Promotions?msg=created");
            } else {
                response.sendRedirect("Promotions?error=create_failed");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("Promotions?error=invalid_input");
        }
    }

    private void deletePromotion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int promotionId = Integer.parseInt(request.getParameter("promotionId"));
            
            boolean success = promotionDAO.deletePromotion(promotionId);
            
            if (success) {
                response.sendRedirect("Promotions?msg=deleted");
            } else {
                response.sendRedirect("Promotions?error=delete_failed");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect("Promotions?error=invalid_id");
        }
    }

    private void loadProductsJson(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String search = request.getParameter("search");
        if (search == null) search = "";
        
        // Use ProductDetailDAO to get products with details
        List<ProductDetailDAO.DetailMini> products = productDetailDAO.search(search);
        
        StringBuilder json = new StringBuilder();
        json.append("{\"items\":[");
        
        for (int i = 0; i < products.size(); i++) {
            ProductDetailDAO.DetailMini product = products.get(i);
            if (i > 0) json.append(",");
            
            json.append("{")
                .append("\"id\":").append(product.getId()).append(",")
                .append("\"text\":\"").append(escapeJson(product.getName()))
                .append(product.getSku() != null ? " - " + escapeJson(product.getSku()) : "")
                .append("\"")
                .append("}");
        }
        
        json.append("],\"total_count\":").append(products.size()).append("}");
        
        response.getWriter().write(json.toString());
    }
    
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int promotionId = Integer.parseInt(request.getParameter("id"));
            
            Promotion promotion = promotionDAO.getPromotionById(promotionId);
            if (promotion == null) {
                response.sendRedirect("Promotions?error=not_found");
                return;
            }
            
            // Get all branches for dropdown
            List<Branch> branches = branchDAO.getAllBranches();
            
            // Get selected branch IDs and product IDs for pre-selection
            List<Integer> selectedBranchIds = promotionDAO.getPromotionBranchIds(promotionId);
            List<Integer> selectedProductIds = promotionDAO.getPromotionProductIds(promotionId);
            
            request.setAttribute("branches", branches);
            request.setAttribute("promotion", promotion);
            request.setAttribute("selectedBranchIds", selectedBranchIds);
            request.setAttribute("selectedProductIds", selectedProductIds);
            
            request.getRequestDispatcher("/WEB-INF/jsp/admin/promotion_edit.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect("Promotions?error=invalid_id");
        }
    }

    private void showPromotionDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int promotionId = Integer.parseInt(request.getParameter("id"));
            
            Promotion promotion = promotionDAO.getPromotionById(promotionId);
            if (promotion == null) {
                response.sendRedirect("Promotions?error=not_found");
                return;
            }
            
            // Load branches and products for this promotion
            List<Integer> branchIds = promotionDAO.getPromotionBranchIds(promotionId);
            List<Integer> productIds = promotionDAO.getPromotionProductIds(promotionId);
            
            // Get actual branch and product objects
            List<Branch> promotionBranches = new ArrayList<>();
            if (!branchIds.isEmpty()) {
                for (Integer branchId : branchIds) {
                    Branch branch = branchDAO.getBranchById(branchId);
                    if (branch != null) {
                        promotionBranches.add(branch);
                    }
                }
            }
            
            List<ProductDetail> promotionProducts = new ArrayList<>();
            if (!productIds.isEmpty()) {
                for (Integer productId : productIds) {
                    ProductDetail product = productDetailDAO.getProductDetailById(productId);
                    if (product != null) {
                        promotionProducts.add(product);
                    }
                }
            }
            
            request.setAttribute("promotion", promotion);
            request.setAttribute("promotionBranches", promotionBranches);
            request.setAttribute("promotionProducts", promotionProducts);
            request.getRequestDispatcher("/WEB-INF/jsp/admin/promotion_detail.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect("Promotions?error=invalid_id");
        }
    }

    private void updatePromotion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int promotionId = Integer.parseInt(request.getParameter("promotionId"));
            String promoName = request.getParameter("promoName");
            String discountPercentStr = request.getParameter("discountPercent");
            String startDateStr = request.getParameter("startDate");
            String endDateStr = request.getParameter("endDate");
            String[] branchIdStrs = request.getParameterValues("branchIds");
            String[] productDetailIdStrs = request.getParameterValues("productDetailIds");
            
            // Validate inputs
            if (promoName == null || promoName.trim().isEmpty()) {
                response.sendRedirect("Promotions?error=invalid_name");
                return;
            }
            
            BigDecimal discountPercent = new BigDecimal(discountPercentStr);
            if (discountPercent.compareTo(BigDecimal.ZERO) <= 0 || discountPercent.compareTo(new BigDecimal("100")) > 0) {
                response.sendRedirect("Promotions?error=invalid_discount");
                return;
            }
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date startDate = sdf.parse(startDateStr);
            java.util.Date endDate = sdf.parse(endDateStr);
            
            if (startDate.after(endDate)) {
                response.sendRedirect("Promotions?error=invalid_date_range");
                return;
            }
            
            // Create promotion object
            Promotion promotion = new Promotion();
            promotion.setPromotionId(promotionId);
            promotion.setPromoName(promoName);
            promotion.setDiscountPercent(discountPercent);
            promotion.setStartDate(startDate);
            promotion.setEndDate(endDate);
            
            // Parse branch IDs - if empty, apply to all branches
            List<Integer> branchIds = new ArrayList<>();
            if (branchIdStrs != null && branchIdStrs.length > 0) {
                for (String branchIdStr : branchIdStrs) {
                    if (branchIdStr != null && !branchIdStr.trim().isEmpty()) {
                        branchIds.add(Integer.parseInt(branchIdStr));
                    }
                }
            }
            // If no branches selected, get all branches
            if (branchIds.isEmpty()) {
                List<Branch> allBranches = branchDAO.getAllBranches();
                for (Branch branch : allBranches) {
                    branchIds.add(branch.getBranchId());
                }
            }
            
            // Parse product detail IDs
            List<Integer> productDetailIds = new ArrayList<>();
            if (productDetailIdStrs != null && productDetailIdStrs.length > 0) {
                for (String productDetailIdStr : productDetailIdStrs) {
                    if (productDetailIdStr != null && !productDetailIdStr.trim().isEmpty()) {
                        productDetailIds.add(Integer.parseInt(productDetailIdStr));
                    }
                }
            }
            
            // Update promotion
            boolean success = promotionDAO.updatePromotion(promotion, branchIds, productDetailIds);
            
            if (success) {
                response.sendRedirect("Promotions?msg=updated");
            } else {
                response.sendRedirect("Promotions?error=update_failed");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("Promotions?error=invalid_input");
        }
    }

    private List<Promotion> applyFilters(List<Promotion> promotions, String statusFilter, String discountFilter, String searchKeyword) {
        java.util.Date currentDate = new java.util.Date();
        
        return promotions.stream()
            .filter(promotion -> {
                // Search filter
                if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                    if (!promotion.getPromoName().toLowerCase().contains(searchKeyword.toLowerCase())) {
                        return false;
                    }
                }
                
                // Status filter
                if (statusFilter != null && !statusFilter.isEmpty()) {
                    boolean isActive = currentDate.after(promotion.getStartDate()) && currentDate.before(promotion.getEndDate());
                    boolean isExpired = currentDate.after(promotion.getEndDate());
                    boolean isScheduled = currentDate.before(promotion.getStartDate());
                    
                    switch (statusFilter) {
                        case "active":
                            if (!isActive) return false;
                            break;
                        case "expired":
                            if (!isExpired) return false;
                            break;
                        case "scheduled":
                            if (!isScheduled) return false;
                            break;
                    }
                }
                
                // Discount filter
                if (discountFilter != null && !discountFilter.isEmpty()) {
                    double discount = promotion.getDiscountPercent().doubleValue();
                    switch (discountFilter) {
                        case "low":
                            if (discount >= 15) return false;
                            break;
                        case "medium":
                            if (discount < 15 || discount > 25) return false;
                            break;
                        case "high":
                            if (discount <= 25) return false;
                            break;
                    }
                }
                
                return true;
            })
            .collect(java.util.stream.Collectors.toList());
    }

    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\"", "\\\"")
                  .replace("\\", "\\\\")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}
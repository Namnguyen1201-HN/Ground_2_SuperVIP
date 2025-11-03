package Controller.Admin;

import DAL.BranchDAO;
import DAL.ProductDetailDAO;
import DAL.PromotionDAO;
import Model.Branch;
import Model.ProductDetail;
import Model.Promotion;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "PromotionController", urlPatterns = {"/Promotion"})
public class PromotionController extends HttpServlet {

    private PromotionDAO promotionDAO;

    @Override
    public void init() {
        promotionDAO = new PromotionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("delete".equals(action)) {
            handleDelete(request, response);
            return;
        }
        
        if ("edit".equals(action)) {
            handleEditGet(request, response);
            return;
        }
        
        if ("view".equals(action)) {
            handleView(request, response);
            return;
        }
        
        // Get all promotions
        List<Promotion> promotions = promotionDAO.getAllPromotions();
        request.setAttribute("promotions", promotions);
        
        // Load branches and products for form
        loadFormData(request);
        
        // Forward to JSP
        request.getRequestDispatcher("/WEB-INF/jsp/admin/promotion_management.jsp").forward(request, response);
    }

    private void loadFormData(HttpServletRequest request) {
        BranchDAO branchDAO = new BranchDAO();
        ProductDetailDAO productDetailDAO = new ProductDetailDAO();
        
        List<Branch> branches = branchDAO.getAllBranches();
        List<ProductDetail> products = productDetailDAO.getAllProductDetails();
        
        request.setAttribute("branches", branches);
        request.setAttribute("products", products);
    }

    private void handleEditGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect("Promotion?error=invalid_id");
            return;
        }
        
        try {
            int promotionId = Integer.parseInt(idParam);
            Promotion promo = promotionDAO.getPromotionById(promotionId);
            
            if (promo == null) {
                response.sendRedirect("Promotion?error=not_found");
                return;
            }
            
            // Get selected branches and products
            List<Integer> selectedBranchIds = promotionDAO.getBranchIdsByPromotion(promotionId);
            List<Integer> selectedProductDetailIds = promotionDAO.getProductDetailIdsByPromotion(promotionId);
            
            // Ensure lists are not null
            if (selectedBranchIds == null) {
                selectedBranchIds = new ArrayList<>();
            }
            if (selectedProductDetailIds == null) {
                selectedProductDetailIds = new ArrayList<>();
            }
            
            request.setAttribute("promotion", promo);
            request.setAttribute("selectedBranchIds", selectedBranchIds);
            request.setAttribute("selectedProductDetailIds", selectedProductDetailIds);
            
            // Load branches and products
            loadFormData(request);
            
            // Get all promotions for table
            List<Promotion> promotions = promotionDAO.getAllPromotions();
            request.setAttribute("promotions", promotions);
            
            request.getRequestDispatcher("/WEB-INF/jsp/admin/promotion_management.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect("Promotion?error=invalid_id");
        }
    }

    private void handleView(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect("Promotion?error=invalid_id");
            return;
        }
        
        try {
            int promotionId = Integer.parseInt(idParam);
            Promotion promo = promotionDAO.getPromotionById(promotionId);
            
            if (promo == null) {
                response.sendRedirect("Promotion?error=not_found");
                return;
            }
            
            // Get selected branches and products with details
            List<Integer> selectedBranchIds = promotionDAO.getBranchIdsByPromotion(promotionId);
            List<Integer> selectedProductDetailIds = promotionDAO.getProductDetailIdsByPromotion(promotionId);
            
            // Load branch and product details
            BranchDAO branchDAO = new BranchDAO();
            ProductDetailDAO productDetailDAO = new ProductDetailDAO();
            
            List<Branch> selectedBranches = new ArrayList<>();
            if (selectedBranchIds != null && !selectedBranchIds.isEmpty()) {
                List<Branch> allBranches = branchDAO.getAllBranches();
                for (Branch branch : allBranches) {
                    if (selectedBranchIds.contains(branch.getBranchId())) {
                        selectedBranches.add(branch);
                    }
                }
            }
            
            List<ProductDetail> selectedProducts = new ArrayList<>();
            if (selectedProductDetailIds != null && !selectedProductDetailIds.isEmpty()) {
                List<ProductDetail> allProducts = productDetailDAO.getAllProductDetails();
                for (ProductDetail product : allProducts) {
                    if (selectedProductDetailIds.contains(product.getProductDetailID())) {
                        selectedProducts.add(product);
                    }
                }
            }
            
            request.setAttribute("promotion", promo);
            request.setAttribute("selectedBranches", selectedBranches);
            request.setAttribute("selectedProducts", selectedProducts);
            
            request.getRequestDispatcher("/WEB-INF/jsp/admin/promotion_detail.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect("Promotion?error=invalid_id");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("create".equals(action)) {
            handleCreate(request, response);
        } else if ("update".equals(action)) {
            handleUpdate(request, response);
        } else {
            response.sendRedirect("Promotion");
        }
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String idParam = request.getParameter("id");
        
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect("Promotion?error=invalid_id");
            return;
        }
        
        try {
            int promotionId = Integer.parseInt(idParam);
            
            // Check if promotion exists before deleting
            Promotion promo = promotionDAO.getPromotionById(promotionId);
            if (promo == null) {
                response.sendRedirect("Promotion?error=not_found");
                return;
            }
            
            boolean deleted = promotionDAO.deletePromotion(promotionId);
            
            if (deleted) {
                response.sendRedirect("Promotion?success=delete");
            } else {
                response.sendRedirect("Promotion?error=delete_failed");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("Promotion?error=invalid_id");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("Promotion?error=delete_failed");
        }
    }

    private void handleCreate(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String promoName = request.getParameter("promoName");
        String discountPercent = request.getParameter("discountPercent");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String[] branchIds = request.getParameterValues("branchIds");
        String[] productDetailIds = request.getParameterValues("productDetailIds");
        
        // Validation
        String validationError = validatePromotionData(promoName, discountPercent, startDate, endDate);
        if (validationError != null) {
            response.sendRedirect("Promotion?error=" + validationError);
            return;
        }
        
        try {
            Promotion promo = new Promotion();
            promo.setPromoName(promoName.trim());
            promo.setDiscountPercent(new BigDecimal(discountPercent));
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            promo.setStartDate(sdf.parse(startDate));
            promo.setEndDate(sdf.parse(endDate));
            
            // Insert promotion
            boolean created = promotionDAO.insertPromotion(promo);
            
            if (created && promo.getPromotionId() > 0) {
                // Insert branches if any
                if (branchIds != null && branchIds.length > 0) {
                    List<Integer> branchIdList = Arrays.stream(branchIds)
                            .map(Integer::parseInt)
                            .collect(Collectors.toList());
                    promotionDAO.insertPromotionBranches(promo.getPromotionId(), branchIdList);
                }
                
                // Insert products if any
                if (productDetailIds != null && productDetailIds.length > 0) {
                    List<Integer> productDetailIdList = Arrays.stream(productDetailIds)
                            .map(Integer::parseInt)
                            .collect(Collectors.toList());
                    promotionDAO.insertPromotionProducts(promo.getPromotionId(), productDetailIdList);
                }
                
                response.sendRedirect("Promotion?success=create");
            } else {
                response.sendRedirect("Promotion?error=create_failed");
            }
        } catch (ParseException e) {
            response.sendRedirect("Promotion?error=invalid_date_format");
        } catch (NumberFormatException e) {
            response.sendRedirect("Promotion?error=invalid_discount");
        }
    }

    private void handleUpdate(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String idParam = request.getParameter("promotionId");
        String promoName = request.getParameter("promoName");
        String discountPercent = request.getParameter("discountPercent");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String[] branchIds = request.getParameterValues("branchIds");
        String[] productDetailIds = request.getParameterValues("productDetailIds");
        
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect("Promotion?error=invalid_id");
            return;
        }
        
        // Validation
        String validationError = validatePromotionData(promoName, discountPercent, startDate, endDate);
        if (validationError != null) {
            response.sendRedirect("Promotion?error=" + validationError);
            return;
        }
        
        try {
            int promotionId = Integer.parseInt(idParam);
            
            Promotion promo = new Promotion();
            promo.setPromotionId(promotionId);
            promo.setPromoName(promoName.trim());
            promo.setDiscountPercent(new BigDecimal(discountPercent));
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            promo.setStartDate(sdf.parse(startDate));
            promo.setEndDate(sdf.parse(endDate));
            
            // Update promotion
            boolean updated = promotionDAO.updatePromotion(promo);
            
            if (updated) {
                // Delete existing branches and products
                promotionDAO.deletePromotionBranches(promotionId);
                promotionDAO.deletePromotionProducts(promotionId);
                
                // Insert new branches if any
                if (branchIds != null && branchIds.length > 0) {
                    List<Integer> branchIdList = Arrays.stream(branchIds)
                            .map(Integer::parseInt)
                            .collect(Collectors.toList());
                    promotionDAO.insertPromotionBranches(promotionId, branchIdList);
                }
                
                // Insert new products if any
                if (productDetailIds != null && productDetailIds.length > 0) {
                    List<Integer> productDetailIdList = Arrays.stream(productDetailIds)
                            .map(Integer::parseInt)
                            .collect(Collectors.toList());
                    promotionDAO.insertPromotionProducts(promotionId, productDetailIdList);
                }
                
                response.sendRedirect("Promotion?success=update");
            } else {
                response.sendRedirect("Promotion?error=update_failed");
            }
        } catch (ParseException e) {
            response.sendRedirect("Promotion?error=invalid_date_format");
        } catch (NumberFormatException e) {
            response.sendRedirect("Promotion?error=invalid_id");
        }
    }

    private String validatePromotionData(String promoName, String discountPercent, 
                                          String startDate, String endDate) {
        // Check null/empty
        if (promoName == null || promoName.trim().isEmpty()) {
            return "empty_promo_name";
        }
        
        if (discountPercent == null || discountPercent.trim().isEmpty()) {
            return "empty_discount";
        }
        
        if (startDate == null || startDate.trim().isEmpty()) {
            return "empty_start_date";
        }
        
        if (endDate == null || endDate.trim().isEmpty()) {
            return "empty_end_date";
        }
        
        // Check length
        if (promoName.trim().length() > 255) {
            return "promo_name_too_long";
        }
        
        // Check discount value
        try {
            BigDecimal discount = new BigDecimal(discountPercent);
            if (discount.compareTo(BigDecimal.ZERO) < 0 || discount.compareTo(new BigDecimal("100")) > 0) {
                return "invalid_discount_range";
            }
        } catch (NumberFormatException e) {
            return "invalid_discount";
        }
        
        // Check dates
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);
            
            if (start.after(end)) {
                return "invalid_dates";
            }
        } catch (ParseException e) {
            return "invalid_date_format";
        }
        
        return null; // No error
    }
}


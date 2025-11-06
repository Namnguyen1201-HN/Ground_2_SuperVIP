/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Admin;

import DAL.SupplierDAO;
import Model.Supplier;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;

/**
 *
 * @author TieuPham
 */
@WebServlet(name = "SupplierController", urlPatterns = {"/Supplier"})
public class SupplierController extends HttpServlet {

    private SupplierDAO supplierDAO;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if ("getSupplierDetails".equals(action)) {
            handleGetSupplierDetails(request, response);
            return;
        }
        
        supplierDAO = new SupplierDAO();
        int pageSize = 10;
        int page;
        try {
            page = Integer.parseInt(request.getParameter("page"));
            if (page < 1) {
                page = 1;
            }
        } catch (Exception e) {
            page = 1;
        }

        int total = supplierDAO.getTotalSuppliers();
        int totalPages = (int) Math.ceil((double) total / pageSize);
        if (totalPages == 0) {
            totalPages = 1;
        }
        if (page > totalPages) {
            page = totalPages;
        }

        List<Supplier> suppliers = supplierDAO.getSuppliersPaged(page, pageSize);
        int startIndex = total == 0 ? 0 : (page - 1) * pageSize + 1;
        int endIndex = Math.min(page * pageSize, total);

        request.setAttribute("suppliers", suppliers);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalSuppliers", total);
        request.setAttribute("startSupplier", startIndex);
        request.setAttribute("endSupplier", endIndex);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/admin/supplier.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            System.out.println("[DEBUG] ========== doPost called ==========");
            System.out.println("[DEBUG] Request URI: " + request.getRequestURI());
            System.out.println("[DEBUG] Request method: " + request.getMethod());
            
            request.setCharacterEncoding("UTF-8");
            
            String action = request.getParameter("action");
            System.out.println("[DEBUG] Action parameter: " + action);
            
            if ("updateSupplier".equals(action)) {
                System.out.println("[DEBUG] updateSupplier action detected");
                if (!response.isCommitted()) {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setCharacterEncoding("UTF-8");
                    response.setStatus(HttpServletResponse.SC_OK);
                    System.out.println("[DEBUG] Response headers set for JSON");
                }
                handleUpdateSupplier(request, response);
                return;
            }
            
            if ("search".equalsIgnoreCase(action)) {
                handleSearch(request, response);
                return;
            }
            
            System.out.println("[DEBUG] Other action, forwarding to doGet");
            response.setContentType("text/html;charset=UTF-8");
            doGet(request, response);
        } catch (Exception e) {
            System.out.println("[DEBUG] Exception in doPost: " + e.getClass().getName());
            System.out.println("[DEBUG] Exception message: " + e.getMessage());
            e.printStackTrace();
            if (!response.isCommitted()) {
                response.setContentType("application/json;charset=UTF-8");
                response.setCharacterEncoding("UTF-8");
                try {
                    PrintWriter out = response.getWriter();
                    JSONObject json = new JSONObject();
                    json.put("success", false);
                    json.put("message", "Lỗi server: " + e.getMessage());
                    out.print(json.toString());
                    out.flush();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    private void handleSearch(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String keyword = request.getParameter("keyword");
        if (keyword == null) {
            // Nếu không có keyword trong request, lấy lại từ session (khi chuyển trang)
            keyword = (String) request.getSession().getAttribute("searchKeyword");
            if (keyword == null) {
                keyword = "";
            }
        } else {
            // Lưu keyword mới vào session
            request.getSession().setAttribute("searchKeyword", keyword);
        }

        int page = 1;
        int pageSize = 10;
        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            page = Integer.parseInt(pageParam);
        }

        SupplierDAO dao = new SupplierDAO();
        int totalSuppliers = dao.getTotalSuppliersByKeyword(keyword);
        int totalPages = (int) Math.ceil((double) totalSuppliers / pageSize);
        List<Supplier> suppliers = dao.searchSuppliersPaged(keyword, page, pageSize);

        int startSupplier = (page - 1) * pageSize + 1;
        int endSupplier = Math.min(page * pageSize, totalSuppliers);

        request.setAttribute("suppliers", suppliers);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalSuppliers", totalSuppliers);
        request.setAttribute("startSupplier", startSupplier);
        request.setAttribute("endSupplier", endSupplier);
        request.setAttribute("keyword", keyword); // để giữ lại trong ô input

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/admin/supplier.jsp");
        dispatcher.forward(request, response);
    }

    private void handleGetSupplierDetails(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        
        PrintWriter out = null;
        JSONObject json = new JSONObject();

        try {
            System.out.println("[DEBUG] handleGetSupplierDetails - Starting");
            
            if (response.isCommitted()) {
                System.out.println("[DEBUG] Response already committed");
                return;
            }
            
            out = response.getWriter();
            String supplierIdParam = request.getParameter("supplierId");
            System.out.println("[DEBUG] supplierIdParam: " + supplierIdParam);
            
            if (supplierIdParam == null || supplierIdParam.trim().isEmpty()) {
                json.put("success", false);
                json.put("message", "Mã nhà cung cấp không hợp lệ");
                out.print(json.toString());
                out.flush();
                return;
            }
            
            int supplierId = Integer.parseInt(supplierIdParam);
            System.out.println("[DEBUG] Parsed supplierId: " + supplierId);
            
            supplierDAO = new SupplierDAO();
            Supplier supplier = supplierDAO.getSupplierById(supplierId);
            
            if (supplier == null) {
                System.out.println("[DEBUG] Supplier not found");
                json.put("success", false);
                json.put("message", "Không tìm thấy nhà cung cấp");
                out.print(json.toString());
                out.flush();
                return;
            }
            
            System.out.println("[DEBUG] Supplier retrieved: Found");
            System.out.println("[DEBUG] Supplier ID: " + supplier.getSupplierId());
            System.out.println("[DEBUG] Supplier Name: " + supplier.getSupplierName());
            System.out.println("[DEBUG] Email: " + supplier.getEmail());
            
            // Format createdAt
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            String createdAtStr = supplier.getCreatedAt() != null 
                ? dateFormat.format(supplier.getCreatedAt()) 
                : "";
            
            json.put("success", true);
            json.put("supplierID", supplier.getSupplierId());
            json.put("supplierName", supplier.getSupplierName());
            json.put("contactName", supplier.getContactName());
            json.put("email", supplier.getEmail());
            json.put("phone", supplier.getPhone());
            json.put("createdAt", createdAtStr);
            
            System.out.println("[DEBUG] JSON created successfully");
            String jsonString = json.toString();
            System.out.println("[DEBUG] Sending JSON response: " + jsonString);
            out.print(jsonString);
            out.flush();
            System.out.println("[DEBUG] Response sent successfully");
            
        } catch (Exception e) {
            System.out.println("[DEBUG] Exception in handleGetSupplierDetails: " + e.getClass().getName());
            System.out.println("[DEBUG] Exception message: " + e.getMessage());
            e.printStackTrace();
            if (out != null && !response.isCommitted()) {
                try {
                    try {
                        json.put("success", false);
                    } catch (JSONException ex) {
                        Logger.getLogger(SupplierController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    json.put("message", "Lỗi khi lấy thông tin nhà cung cấp: " + e.getMessage());
                    out.print(json.toString());
                    out.flush();
                } catch (JSONException ex) {
                    Logger.getLogger(SupplierController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void handleUpdateSupplier(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        if (!response.isCommitted()) {
            response.setContentType("application/json;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
        }
        
        PrintWriter out = null;
        JSONObject json = new JSONObject();

        try {
            if (response.isCommitted()) {
                System.out.println("[DEBUG] Response already committed in handleUpdateSupplier");
                return;
            }
            
            out = response.getWriter();
            System.out.println("[DEBUG] handleUpdateSupplier - Starting");
            
            // Log all parameters for debugging
            System.out.println("[DEBUG] All request parameters in handleUpdateSupplier:");
            request.getParameterMap().forEach((key, values) -> {
                System.out.println("[DEBUG]   " + key + " = " + java.util.Arrays.toString(values));
            });
            
            // Validate supplier ID
            String supplierIdParam = request.getParameter("supplierId");
            System.out.println("[DEBUG] supplierIdParam from request: " + supplierIdParam);
            
            if (supplierIdParam == null || supplierIdParam.trim().isEmpty()) {
                json.put("success", false);
                json.put("message", "Mã nhà cung cấp không hợp lệ");
                out.print(json.toString());
                out.flush();
                return;
            }

            int supplierId = Integer.parseInt(supplierIdParam);

            // Get and validate fields
            String supplierName = request.getParameter("supplierName");
            String contactName = request.getParameter("contactName");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");

            // Validation - Tên công ty
            if (supplierName == null || supplierName.trim().isEmpty()) {
                json.put("success", false);
                json.put("message", "Tên công ty không được để trống");
                out.print(json.toString());
                out.flush();
                return;
            }
            supplierName = supplierName.trim();
            if (supplierName.length() < 2 || supplierName.length() > 100) {
                json.put("success", false);
                json.put("message", "Tên công ty phải có độ dài từ 2 đến 100 ký tự");
                out.print(json.toString());
                out.flush();
                return;
            }

            // Validation - Người giao dịch
            if (contactName == null || contactName.trim().isEmpty()) {
                json.put("success", false);
                json.put("message", "Tên người giao dịch không được để trống");
                out.print(json.toString());
                out.flush();
                return;
            }
            contactName = contactName.trim();
            if (contactName.length() < 2 || contactName.length() > 100) {
                json.put("success", false);
                json.put("message", "Tên người giao dịch phải có độ dài từ 2 đến 100 ký tự");
                out.print(json.toString());
                out.flush();
                return;
            }

            // Validation - Email
            if (email == null || email.trim().isEmpty()) {
                json.put("success", false);
                json.put("message", "Email không được để trống");
                out.print(json.toString());
                out.flush();
                return;
            }
            email = email.trim().toLowerCase();
            if (email.length() > 100) {
                json.put("success", false);
                json.put("message", "Email không được vượt quá 100 ký tự");
                out.print(json.toString());
                out.flush();
                return;
            }
            // Validate email format
            try {
                if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                    json.put("success", false);
                    json.put("message", "Email không hợp lệ. Vui lòng nhập đúng định dạng email");
                    out.print(json.toString());
                    out.flush();
                    return;
                }
            } catch (Exception e) {
                if (!email.contains("@") || !email.contains(".")) {
                    json.put("success", false);
                    json.put("message", "Email không hợp lệ");
                    out.print(json.toString());
                    out.flush();
                    return;
                }
            }

            // Validation - Số điện thoại
            if (phone == null || phone.trim().isEmpty()) {
                json.put("success", false);
                json.put("message", "Số điện thoại không được để trống");
                out.print(json.toString());
                out.flush();
                return;
            }
            phone = phone.trim().replaceAll("\\s+", "");
            String phoneDigits = phone.replaceAll("[^0-9]", "");
            if (phoneDigits.length() < 10 || phoneDigits.length() > 11) {
                json.put("success", false);
                json.put("message", "Số điện thoại phải có từ 10 đến 11 chữ số");
                out.print(json.toString());
                out.flush();
                return;
            }
            // Chuẩn hóa số điện thoại
            if (phone.startsWith("+84")) {
                phone = "0" + phoneDigits.substring(2);
            } else {
                phone = phoneDigits;
            }

            // Get existing supplier to preserve createdAt
            supplierDAO = new SupplierDAO();
            Supplier existingSupplier = supplierDAO.getSupplierById(supplierId);
            
            if (existingSupplier == null) {
                System.out.println("[DEBUG] Existing supplier not found");
                json.put("success", false);
                json.put("message", "Không tìm thấy nhà cung cấp");
                out.print(json.toString());
                out.flush();
                return;
            }

            // Update supplier
            Supplier supplier = new Supplier();
            supplier.setSupplierId(supplierId);
            supplier.setSupplierName(supplierName);
            supplier.setContactName(contactName);
            supplier.setEmail(email);
            supplier.setPhone(phone);
            supplier.setCreatedAt(existingSupplier.getCreatedAt());

            System.out.println("[DEBUG] Updating supplier...");
            boolean success = supplierDAO.updateSupplier(supplier);
            System.out.println("[DEBUG] Update result: " + success);

            if (success) {
                json.put("success", true);
                json.put("message", "Cập nhật thông tin nhà cung cấp thành công");
            } else {
                json.put("success", false);
                json.put("message", "Cập nhật thông tin nhà cung cấp thất bại");
            }
            
            String jsonString = json.toString();
            System.out.println("[DEBUG] Sending JSON response (update): " + jsonString);
            out.print(jsonString);
            out.flush();
            System.out.println("[DEBUG] Response sent successfully (update)");

        } catch (NumberFormatException e) {
            try {
                System.out.println("[DEBUG] NumberFormatException in update: " + e.getMessage());
                e.printStackTrace();
                json.put("success", false);
                json.put("message", "Mã nhà cung cấp không hợp lệ");
                try {
                    if (!response.isCommitted() && out != null) {
                        out.print(json.toString());
                        out.flush();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            } catch (JSONException ex) {
                Logger.getLogger(SupplierController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IllegalStateException e) {
            System.out.println("[DEBUG] IllegalStateException in update: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            try {
                System.out.println("[DEBUG] Exception in handleUpdateSupplier: " + e.getClass().getName());
                System.out.println("[DEBUG] Exception message: " + e.getMessage());
                e.printStackTrace();
                json.put("success", false);
                String errorMsg = e.getMessage();
                if (errorMsg == null || errorMsg.isEmpty()) {
                    errorMsg = "Lỗi khi cập nhật nhà cung cấp: " + e.getClass().getSimpleName();
                }
                json.put("message", errorMsg);
                try {
                    json.put("exceptionType", e.getClass().getName());
                } catch (JSONException ex) {
                    Logger.getLogger(SupplierController.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    if (!response.isCommitted()) {
                        if (out == null) {
                            out = response.getWriter();
                        }
                        if (out != null) {
                            response.setContentType("application/json;charset=UTF-8");
                            out.print(json.toString());
                            out.flush();
                            System.out.println("[DEBUG] Error JSON sent successfully");
                        }
                    }
                } catch (Exception e2) {
                    System.out.println("[DEBUG] Error sending error JSON: " + e2.getMessage());
                    e2.printStackTrace();
                }
            } catch (JSONException ex) {
                Logger.getLogger(SupplierController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

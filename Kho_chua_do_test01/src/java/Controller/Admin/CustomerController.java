package Controller.Admin;

import DAL.CustomerDAO;
import DAL.BranchDAO;
import Model.Customer;
import Model.Branch;
import Model.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.json.JSONObject;

@WebServlet(name = "CustomerController", urlPatterns = {"/Customer"})
public class CustomerController extends HttpServlet {
    private CustomerDAO customerDAO;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check if this is an AJAX request for customer details
        String action = request.getParameter("action");
        if ("getCustomerDetails".equals(action)) {
            handleGetCustomerDetails(request, response);
            return;
        }

        customerDAO = new CustomerDAO();
        BranchDAO branchDAO = new BranchDAO();

        // --- Lấy user đăng nhập ---
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("currentUser");

        // --- Lấy các param lọc/tìm kiếm ---
        String keyword = request.getParameter("keyword");
        String gender = request.getParameter("gender");
        String branchParam = request.getParameter("branchId");
        String minSpentParam = request.getParameter("minSpent");
        String maxSpentParam = request.getParameter("maxSpent");

        Integer branchId = null;
        Double minSpent = null, maxSpent = null;

        try {
            if (branchParam != null && !branchParam.isEmpty()) {
                branchId = Integer.parseInt(branchParam);
            }
            if (minSpentParam != null && !minSpentParam.isEmpty()) {
                minSpent = Double.parseDouble(minSpentParam);
            }
            if (maxSpentParam != null && !maxSpentParam.isEmpty()) {
                maxSpent = Double.parseDouble(maxSpentParam);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Nếu người dùng bấm “Xóa bộ lọc” => reset về null
        if ("clear".equalsIgnoreCase(action)) {
            keyword = null;
            gender = "all";
            branchId = 0;
            minSpent = null;
            maxSpent = null;
        }

        // --- Phân trang ---
        int pageSize = 10;
        int page;
        try {
            page = Integer.parseInt(request.getParameter("page"));
            if (page < 1) page = 1;
        } catch (Exception e) {
            page = 1;
        }

        // --- Lấy dữ liệu filter ---
        List<Customer> customers = customerDAO.searchAndFilterCustomers(keyword, gender, branchId, minSpent, maxSpent);

        // --- Tính phân trang ---
        int totalCustomers = customers.size();
        int totalPages = (int) Math.ceil((double) totalCustomers / pageSize);
        if (totalPages == 0) totalPages = 1;
        if (page > totalPages) page = totalPages;

        int fromIndex = Math.min((page - 1) * pageSize, totalCustomers);
        int toIndex = Math.min(page * pageSize, totalCustomers);
        List<Customer> pagedCustomers = customers.subList(fromIndex, toIndex);

        int startIndex = totalCustomers == 0 ? 0 : fromIndex + 1;
        int endIndex = toIndex;

        // --- Gửi dữ liệu sang JSP ---
        request.setAttribute("customers", pagedCustomers);
        request.setAttribute("branches", branchDAO.getAllBranches());
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalCustomers", totalCustomers);
        request.setAttribute("startIndex", startIndex);
        request.setAttribute("endIndex", endIndex);

        // Giữ lại giá trị lọc để hiển thị lại
        request.setAttribute("keyword", keyword);
        request.setAttribute("gender", gender);
        request.setAttribute("branchId", branchId);
        request.setAttribute("minSpent", minSpent);
        request.setAttribute("maxSpent", maxSpent);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/admin/customer.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            System.out.println("[DEBUG] ========== doPost called ==========");
            System.out.println("[DEBUG] Request URI: " + request.getRequestURI());
            System.out.println("[DEBUG] Request method: " + request.getMethod());
            System.out.println("[DEBUG] Response isCommitted: " + response.isCommitted());
            
            request.setCharacterEncoding("UTF-8");
            
            // Get action parameter - check both parameter and form data
            String action = request.getParameter("action");
            System.out.println("[DEBUG] Action parameter (getParameter): " + action);
            
            // Also check if sent via FormData
            if (action == null || action.isEmpty()) {
                // Try reading from request body for FormData
                String contentType = request.getContentType();
                System.out.println("[DEBUG] Content-Type: " + contentType);
                if (contentType != null && contentType.contains("multipart/form-data")) {
                    // For multipart/form-data, parameters should still work
                    action = request.getParameter("action");
                    System.out.println("[DEBUG] Action from multipart: " + action);
                }
            }
            
            if ("updateCustomer".equals(action)) {
                System.out.println("[DEBUG] updateCustomer action detected");
                // Set JSON content type BEFORE calling handleUpdateCustomer
                // MUST be set before getWriter() is called
                if (!response.isCommitted()) {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setCharacterEncoding("UTF-8");
                    response.setStatus(HttpServletResponse.SC_OK);
                    System.out.println("[DEBUG] Response headers set for JSON");
                    System.out.println("[DEBUG] Content-Type: " + response.getContentType());
                } else {
                    System.out.println("[DEBUG] WARNING: Response already committed before setting headers!");
                    System.out.println("[DEBUG] Current Content-Type: " + response.getContentType());
                }
                
                System.out.println("[DEBUG] Calling handleUpdateCustomer...");
                handleUpdateCustomer(request, response);
                System.out.println("[DEBUG] handleUpdateCustomer returned");
                return;
            }

            // For other actions, use HTML content type
            System.out.println("[DEBUG] Other action, forwarding to doGet");
            response.setContentType("text/html;charset=UTF-8");
            doGet(request, response);
        } catch (Exception e) {
            System.out.println("[DEBUG] Exception in doPost: " + e.getClass().getName());
            System.out.println("[DEBUG] Exception message: " + e.getMessage());
            e.printStackTrace();
            // Still try to return JSON even on exception
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

    private void handleGetCustomerDetails(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        // Set response headers first to prevent HTML error page
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        
        PrintWriter out = null;
        JSONObject json = new JSONObject();

        try {
            System.out.println("[DEBUG] handleGetCustomerDetails - Starting");
            
            // Ensure response is not committed
            if (response.isCommitted()) {
                System.out.println("[DEBUG] Response already committed");
                return;
            }
            
            out = response.getWriter();
            String customerIdParam = request.getParameter("customerId");
            System.out.println("[DEBUG] customerIdParam: " + customerIdParam);
            
            if (customerIdParam == null || customerIdParam.trim().isEmpty()) {
                json.put("success", false);
                json.put("message", "Mã khách hàng không hợp lệ");
                return;
            }

            int customerId = Integer.parseInt(customerIdParam);
            System.out.println("[DEBUG] Parsed customerId: " + customerId);
            
            customerDAO = new CustomerDAO();
            System.out.println("[DEBUG] CustomerDAO created");
            
            Customer customer = customerDAO.getCustomerById(customerId);
            System.out.println("[DEBUG] Customer retrieved: " + (customer != null ? "Found" : "Null"));

            if (customer == null) {
                json.put("success", false);
                json.put("message", "Không tìm thấy khách hàng với ID: " + customerId);
                return;
            }

            System.out.println("[DEBUG] Customer ID: " + customer.getCustomerID());
            System.out.println("[DEBUG] BranchId: " + customer.getBranchId());
            System.out.println("[DEBUG] Fullname: " + customer.getFullname());
            System.out.println("[DEBUG] Email: " + customer.getEmail());

            json.put("success", true);
            json.put("customerID", customer.getCustomerID());
            json.put("fullname", customer.getFullname() != null ? customer.getFullname() : "");
            json.put("phoneNumber", customer.getPhoneNumber() != null ? customer.getPhoneNumber() : "");
            json.put("email", customer.getEmail() != null ? customer.getEmail() : "");
            json.put("address", customer.getAddress() != null ? customer.getAddress() : "");
            json.put("gender", customer.getGender() != null && customer.getGender() ? "Nam" : "Nữ");
            
            // Handle branchId - Get from Orders (JOIN in query)
            Integer branchId = customer.getBranchId();
            if (branchId != null) {
                json.put("branchId", branchId);
            } else {
                json.put("branchId", 0);
                System.out.println("[DEBUG] WARNING: BranchId is null for customer " + customerId + " (no orders found)");
            }
            
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                if (customer.getCreatedAt() != null) {
                    json.put("createdAt", dateFormat.format(customer.getCreatedAt()));
                } else {
                    json.put("createdAt", "");
                }
            } catch (Exception e) {
                System.out.println("[DEBUG] Error formatting createdAt: " + e.getMessage());
                json.put("createdAt", "");
            }
            
            try {
                if (customer.getTotalSpent() != null) {
                    json.put("totalSpent", String.format("%,.0f", customer.getTotalSpent()).replace(",", ".") + " ₫");
                } else {
                    json.put("totalSpent", "0 ₫");
                }
            } catch (Exception e) {
                System.out.println("[DEBUG] Error formatting totalSpent: " + e.getMessage());
                json.put("totalSpent", "0 ₫");
            }
            
            System.out.println("[DEBUG] JSON created successfully");

        } catch (NumberFormatException e) {
            System.out.println("[DEBUG] NumberFormatException: " + e.getMessage());
            e.printStackTrace();
            json.put("success", false);
            json.put("message", "Mã khách hàng không hợp lệ");
        } catch (IllegalStateException e) {
            // Response already committed - log error
            System.out.println("[DEBUG] IllegalStateException: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("[DEBUG] Exception in handleGetCustomerDetails: " + e.getClass().getName());
            System.out.println("[DEBUG] Exception message: " + e.getMessage());
            e.printStackTrace();
            json.put("success", false);
            String errorMsg = e.getMessage();
            if (errorMsg == null || errorMsg.isEmpty()) {
                errorMsg = "Lỗi khi lấy thông tin khách hàng: " + e.getClass().getSimpleName();
            }
            json.put("message", errorMsg);
            json.put("exceptionType", e.getClass().getName());
        } finally {
            try {
                if (out != null && !response.isCommitted()) {
                    String jsonString = json.toString();
                    System.out.println("[DEBUG] Sending JSON response: " + jsonString);
                    out.print(jsonString);
                    out.flush();
                    System.out.println("[DEBUG] Response sent successfully");
                } else {
                    System.out.println("[DEBUG] Cannot send response - out=" + (out != null) + ", committed=" + response.isCommitted());
                }
            } catch (Exception e) {
                System.out.println("[DEBUG] Error in finally block: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void handleUpdateCustomer(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        // Response headers should already be set in doPost, but ensure they're set
        if (!response.isCommitted()) {
            response.setContentType("application/json;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
        }
        
        PrintWriter out = null;
        JSONObject json = new JSONObject();

        try {
            // Ensure response is not committed
            if (response.isCommitted()) {
                System.out.println("[DEBUG] Response already committed in handleUpdateCustomer");
                return;
            }
            
            out = response.getWriter();
            System.out.println("[DEBUG] handleUpdateCustomer - Starting");
            
            // Log all parameters for debugging
            System.out.println("[DEBUG] All request parameters in handleUpdateCustomer:");
            request.getParameterMap().forEach((key, values) -> {
                System.out.println("[DEBUG]   " + key + " = " + java.util.Arrays.toString(values));
            });
            
            // Validate customer ID
            String customerIdParam = request.getParameter("customerId");
            System.out.println("[DEBUG] customerIdParam from request: " + customerIdParam);
            
            if (customerIdParam == null || customerIdParam.trim().isEmpty()) {
                json.put("success", false);
                json.put("message", "Mã khách hàng không hợp lệ");
                String jsonString = json.toString();
                System.out.println("[DEBUG] Sending JSON response: " + jsonString);
                out.print(jsonString);
                out.flush();
                return;
            }

            int customerId = Integer.parseInt(customerIdParam);

            // Get and validate fields
            String fullname = request.getParameter("fullname");
            String phoneNumber = request.getParameter("phoneNumber");
            String email = request.getParameter("email");
            String address = request.getParameter("address");
            String genderStr = request.getParameter("gender");

            // Validation - Tên khách hàng
            if (fullname == null || fullname.trim().isEmpty()) {
                json.put("success", false);
                json.put("message", "Tên khách hàng không được để trống");
                out.print(json.toString());
                out.flush();
                return;
            }
            fullname = fullname.trim();
            if (fullname.length() < 2 || fullname.length() > 100) {
                json.put("success", false);
                json.put("message", "Tên khách hàng phải có độ dài từ 2 đến 100 ký tự");
                out.print(json.toString());
                out.flush();
                return;
            }
            // Kiểm tra ký tự hợp lệ (chữ, số, khoảng trắng, dấu tiếng Việt)
            try {
                if (!fullname.matches("^[\\p{L}\\p{N}\\s'.-]+$")) {
                    json.put("success", false);
                    json.put("message", "Tên khách hàng chứa ký tự không hợp lệ");
                    out.print(json.toString());
                    out.flush();
                    return;
                }
            } catch (Exception e) {
                // Fallback validation nếu regex có vấn đề
                if (fullname.length() < 2 || fullname.length() > 100) {
                    json.put("success", false);
                    json.put("message", "Tên khách hàng không hợp lệ");
                    out.print(json.toString());
                    out.flush();
                    return;
                }
            }

            // Validation - Số điện thoại
            if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                json.put("success", false);
                json.put("message", "Số điện thoại không được để trống");
                out.print(json.toString());
                out.flush();
                return;
            }
            phoneNumber = phoneNumber.trim().replaceAll("\\s+", ""); // Xóa khoảng trắng
            // Kiểm tra chỉ chứa số và các ký tự +, -, (, )
            try {
                if (!phoneNumber.matches("^[0-9+\\-()]+$")) {
                    json.put("success", false);
                    json.put("message", "Số điện thoại chỉ được chứa số và các ký tự +, -, (, )");
                    out.print(json.toString());
                    out.flush();
                    return;
                }
            } catch (Exception e) {
                // Fallback nếu regex có vấn đề
            }
            // Xóa các ký tự đặc biệt để kiểm tra độ dài
            String phoneDigits = phoneNumber.replaceAll("[^0-9]", "");
            if (phoneDigits.length() < 10 || phoneDigits.length() > 11) {
                json.put("success", false);
                json.put("message", "Số điện thoại phải có từ 10 đến 11 chữ số");
                out.print(json.toString());
                out.flush();
                return;
            }
            // Kiểm tra định dạng số điện thoại Việt Nam (bắt đầu bằng 0 hoặc +84)
            if (!phoneDigits.startsWith("0") && !phoneNumber.startsWith("+84")) {
                json.put("success", false);
                json.put("message", "Số điện thoại không hợp lệ. Phải bắt đầu bằng 0 hoặc +84");
                out.print(json.toString());
                out.flush();
                return;
            }
            // Chuẩn hóa số điện thoại: nếu bắt đầu bằng +84 thì chuyển thành 0
            if (phoneNumber.startsWith("+84")) {
                phoneNumber = "0" + phoneDigits.substring(2); // Bỏ +84, thêm 0
            } else {
                phoneNumber = phoneDigits; // Chỉ lấy số
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
                // Fallback nếu regex có vấn đề
                if (!email.contains("@") || !email.contains(".")) {
                    json.put("success", false);
                    json.put("message", "Email không hợp lệ");
                    out.print(json.toString());
                    out.flush();
                    return;
                }
            }

            // Validation - Địa chỉ
            if (address == null || address.trim().isEmpty()) {
                json.put("success", false);
                json.put("message", "Địa chỉ không được để trống");
                out.print(json.toString());
                out.flush();
                return;
            }
            address = address.trim();
            if (address.length() < 5 || address.length() > 200) {
                json.put("success", false);
                json.put("message", "Địa chỉ phải có độ dài từ 5 đến 200 ký tự");
                out.print(json.toString());
                out.flush();
                return;
            }

            // Parse gender
            Boolean gender = null;
            if (genderStr != null && !genderStr.trim().isEmpty()) {
                gender = genderStr.equalsIgnoreCase("Nam") || genderStr.equalsIgnoreCase("male") || genderStr.equals("true");
            }

            // Get existing customer to preserve branchId
            System.out.println("[DEBUG] Getting existing customer with ID: " + customerId);
            customerDAO = new CustomerDAO();
            Customer existingCustomer = customerDAO.getCustomerById(customerId);
            
            if (existingCustomer == null) {
                System.out.println("[DEBUG] Existing customer not found");
                json.put("success", false);
                json.put("message", "Không tìm thấy khách hàng");
                out.print(json.toString());
                out.flush();
                return;
            }

            System.out.println("[DEBUG] Existing customer found - BranchId: " + existingCustomer.getBranchId());
            System.out.println("[DEBUG] Existing customer - Fullname: " + existingCustomer.getFullname());

            // Update customer
            Customer customer = new Customer();
            customer.setCustomerID(customerId);
            customer.setFullname(fullname);
            customer.setPhoneNumber(phoneNumber);
            customer.setEmail(email);
            customer.setAddress(address);
            customer.setGender(gender);
            customer.setDateOfBirth(existingCustomer.getDateOfBirth()); // Preserve date of birth

            System.out.println("[DEBUG] Updating customer...");
            boolean success = customerDAO.updateCustomer(customer);
            System.out.println("[DEBUG] Update result: " + success);

            if (success) {
                json.put("success", true);
                json.put("message", "Cập nhật thông tin khách hàng thành công");
            } else {
                json.put("success", false);
                json.put("message", "Cập nhật thông tin khách hàng thất bại");
            }
            
            // Send JSON response immediately after setting success/failure
            String jsonString = json.toString();
            System.out.println("[DEBUG] Sending JSON response (update): " + jsonString);
            out.print(jsonString);
            out.flush();
            System.out.println("[DEBUG] Response sent successfully (update)");

        } catch (NumberFormatException e) {
            System.out.println("[DEBUG] NumberFormatException in update: " + e.getMessage());
            e.printStackTrace();
            json.put("success", false);
            json.put("message", "Mã khách hàng không hợp lệ");
            // Send JSON even on exception
            try {
                if (!response.isCommitted() && out != null) {
                    out.print(json.toString());
                    out.flush();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        } catch (IllegalStateException e) {
            // Response already committed - log error
            System.out.println("[DEBUG] IllegalStateException in update: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("[DEBUG] Exception in handleUpdateCustomer: " + e.getClass().getName());
            System.out.println("[DEBUG] Exception message: " + e.getMessage());
            e.printStackTrace();
            json.put("success", false);
            String errorMsg = e.getMessage();
            if (errorMsg == null || errorMsg.isEmpty()) {
                errorMsg = "Lỗi khi cập nhật khách hàng: " + e.getClass().getSimpleName();
            }
            json.put("message", errorMsg);
            json.put("exceptionType", e.getClass().getName());
            // Send JSON even on exception
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
        }
    }
}

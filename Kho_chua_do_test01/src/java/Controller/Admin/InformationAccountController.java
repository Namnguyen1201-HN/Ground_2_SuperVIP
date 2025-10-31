package Controller.Admin;

import DAL.BranchDAO;
import DAL.UserDAO;
import DAL.WarehouseDAO;
import Model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@WebServlet(name = "InformationAccountController", urlPatterns = {"/InformationAccount"})
public class InformationAccountController extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final BranchDAO branchDAO = new BranchDAO();
    private final WarehouseDAO warehouseDAO = new WarehouseDAO();

    // Regex
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final String PHONE_REGEX = "^0\\d{9}$";  // 10 số, bắt đầu bằng 0
    private static final String CCCD_REGEX  = "^\\d{12}$";  // đúng 12 số
    private static final String DOB_FMT     = "yyyy-MM-dd";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        // Phải đăng nhập
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect("Login");
            return;
        }

        User loggedUser = (User) session.getAttribute("currentUser");

        // Cập nhật lại user đầy đủ từ DB (đảm bảo luôn “fresh”)
        User fresh = userDAO.getUserFullById(loggedUser.getUserId());
        session.setAttribute("currentUser", fresh);

        // Thu thập số liệu hiển thị
        loadCounts(request);

        request.setAttribute("user", fresh);
        request.getRequestDispatcher("/WEB-INF/jsp/admin/information_account.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect("Login");
            return;
        }

        // Lấy user từ DB theo id trong form (ưu tiên id form để tránh mismatch)
        Integer userId = parseIntSafe(request.getParameter("userId"));
        if (userId == null) {
            request.setAttribute("error", "Thiếu mã người dùng.");
            doGet(request, response);
            return;
        }

        User user = userDAO.getUserFullById(userId);
        if (user == null) {
            request.setAttribute("error", "Người dùng không tồn tại!");
            doGet(request, response);
            return;
        }

        // ---- Lấy & chuẩn hoá dữ liệu từ form ----
        String fullName        = trim(request.getParameter("fullName"));
        String email           = trim(request.getParameter("email"));
        String phone           = trim(request.getParameter("phone"));
        String address         = trim(request.getParameter("address"));
        String identificationId= trim(request.getParameter("identificationId"));
        String taxNumber       = trim(request.getParameter("taxNumber"));
        String webUrl          = trim(request.getParameter("webUrl"));
        String dobStr          = trim(request.getParameter("dob"));
        String genderStr       = trim(request.getParameter("gender"));
        Integer isActive       = parseIntSafe(request.getParameter("isActive"));

        // ---- Validate tập trung ----
        List<String> errors = new ArrayList<>();

        // Họ tên bắt buộc
        if (isBlank(fullName)) {
            errors.add("Tên không được để trống!");
        } else if (!fullName.matches("^[\\p{L} .'-]{2,100}$")) {
            errors.add("Họ và tên không hợp lệ (chỉ gồm chữ và khoảng trắng, 2–100 ký tự).");
        }

        // Email bắt buộc + format
        if (isBlank(email)) {
            errors.add("Email không được để trống!");
        } else if (!email.matches(EMAIL_REGEX)) {
            errors.add("Định dạng email không hợp lệ!");
        }

        // Điện thoại (chuẩn 10 số bắt đầu 0)
        if (isBlank(phone) || !phone.matches(PHONE_REGEX)) {
            errors.add("Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0!");
        }

        // CCCD: không bắt buộc; nếu nhập phải 12 số
        if (!isBlank(identificationId) && !identificationId.matches(CCCD_REGEX)) {
            errors.add("CCCD phải gồm đúng 12 chữ số!");
        }

        // --- Check trùng (chỉ khi đổi) ---
        if (!isBlank(email) && !email.equalsIgnoreCase(user.getEmail()) && userDAO.isEmailExists(email)) {
            errors.add("Email này đã được sử dụng bởi tài khoản khác!");
        }
        if (!isBlank(phone) && !phone.equals(user.getPhone()) && userDAO.isPhoneExists(phone)) {
            errors.add("Số điện thoại này đã được sử dụng bởi tài khoản khác!");
        }
        if (!isBlank(identificationId)) {
            String oldCccd = trim(user.getIdentificationId());
            if (!identificationId.equals(oldCccd) && userDAO.isIdentificationIdExists(identificationId)) {
                errors.add("CMND/CCCD đã tồn tại trong hệ thống!");
            }
        }

        // Nếu có lỗi → trả về view kèm dữ liệu đã nhập
        if (!errors.isEmpty()) {
            request.setAttribute("error", String.join("<br/>", errors));
            // trả lại dữ liệu người dùng vừa nhập (không commit DB)
            bindFormToUser(user, fullName, email, phone, address, identificationId, taxNumber, webUrl, dobStr, genderStr, isActive);
            loadCounts(request);
            request.setAttribute("user", user);
            request.getRequestDispatcher("/WEB-INF/jsp/admin/information_account.jsp").forward(request, response);
            return;
        }

        // ---- Gán & lưu DB ----
        bindFormToUser(user, fullName, email, phone, address, identificationId, taxNumber, webUrl, dobStr, genderStr, isActive);

        boolean updated = userDAO.updateUser(user);
        if (updated) {
            User refreshed = userDAO.getUserFullById(userId);
            request.getSession().setAttribute("currentUser", refreshed);
            request.setAttribute("success", "✅ Cập nhật thông tin thành công!");
            request.setAttribute("user", refreshed);
        } else {
            request.setAttribute("error", "❌ Cập nhật thất bại. Vui lòng thử lại sau!");
            request.setAttribute("user", user); // dữ liệu đã bind
        }

        loadCounts(request);
        request.getRequestDispatcher("/WEB-INF/jsp/admin/information_account.jsp").forward(request, response);
    }

    // ================== Helpers ==================

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static Integer parseIntSafe(String s) {
        if (isBlank(s)) return null;
        try { return Integer.valueOf(s.trim()); } catch (Exception e) { return null; }
    }

    private static Date parseDate(String s) throws ParseException {
        if (isBlank(s)) return null;
        return new SimpleDateFormat(DOB_FMT).parse(s);
    }

    private static Boolean boolFromStr(String s) {
        // form hiện đang gửi "true"/"false"
        if (isBlank(s)) return null;
        return "true".equalsIgnoreCase(s) || "1".equals(s);
    }

    /** Bind dữ liệu từ form vào User (đã qua validate cơ bản). */
    private void bindFormToUser(User user,
                                String fullName, String email, String phone, String address,
                                String identificationId, String taxNumber, String webUrl,
                                String dobStr, String genderStr, Integer isActive) {

        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setAddress(address);
        user.setIdentificationId(identificationId);
        user.setTaxNumber(taxNumber);
        user.setWebUrl(webUrl);

        // isActive: nếu null thì giữ nguyên; nếu có thì set
        if (isActive != null) user.setIsActive(isActive);

        // gender: "true"/"false" hoặc "1"/"0"
        Boolean g = boolFromStr(genderStr);
        if (g != null) user.setGender(g);

        // DOB: cho phép clear khi để trống
        try {
            if (!isBlank(dobStr)) {
                user.setDob(parseDate(dobStr));
            } else {
                user.setDob(null);
            }
        } catch (ParseException e) {
            // không ném ra ngoài; view đã validate trước đó
            user.setDob(null);
        }
    }

    /** Đếm số branch/warehouse cho dashboard nhỏ. */
    private void loadCounts(HttpServletRequest request) {
        int branchCount = branchDAO.getAllBranches().size();
        int warehouseCount = warehouseDAO.getAllWarehouses().size();
        request.setAttribute("branchCount", branchCount);
        request.setAttribute("warehouseCount", warehouseCount);
    }
}

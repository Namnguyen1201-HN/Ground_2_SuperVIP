package Controller.Branch;

import DAL.BranchDAO;
import DAL.WarehouseDAO;
import DAL.UserDAO;
import Model.User;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "InformationAccountBMController", urlPatterns = {"/InformationAccountBM"})
public class InformationAccountBMController extends HttpServlet {

    private static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_REGEX = Pattern.compile("^(0\\d{9}|\\+84\\d{9})$");
    private static final SimpleDateFormat DOB_FMT = new SimpleDateFormat("yyyy-MM-dd");
    private static final Pattern CCCD_12_DIGITS = Pattern.compile("^\\d{12}$");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User uSession = (session == null) ? null : (User) session.getAttribute("currentUser");

        // Chỉ cho BM
        if (uSession == null || uSession.getRoleId() != 1) {
            request.getRequestDispatcher("/WEB-INF/jsp/includes/Login.jsp")
                    .forward(request, response);
            return;
        }

        UserDAO userDAO = new UserDAO();
        // Lấy user có JOIN để có sẵn BranchName/WarehouseName
        User user = userDAO.getUserById(uSession.getUserId());
        if (user == null) {
            request.getRequestDispatcher("/WEB-INF/jsp/includes/Login.jsp")
                    .forward(request, response);
            return;
        }

        // Làm tươi session
        session.setAttribute("currentUser", user);

        // Thống kê chuỗi cửa hàng
        BranchDAO branchDAO = new BranchDAO();
        WarehouseDAO warehouseDAO = new WarehouseDAO();
        int branchCount = 0;
        int warehouseCount = 0;
        try {
            branchCount = branchDAO.getAllBranches().size();
        } catch (Exception ignored) {
        }
        try {
            warehouseCount = warehouseDAO.getAllWarehouses().size();
        } catch (Exception ignored) {
        }

        // Thông tin cửa hàng (HIỂN THỊ) theo BranchID
        String storeName = safe(user.getBranchName());  // tên cửa hàng = tên chi nhánh
        String branchAddress = "—";
        Integer currentBranchId = normalizeNullableId(user.getBranchId()); // nếu =0 -> null
        String currentBranchName = storeName.isBlank() ? "—" : storeName;

        if (currentBranchId != null) {
            try {
                var b = branchDAO.getBranchById(currentBranchId);
                if (b != null) {
                    if (b.getBranchName() != null && !b.getBranchName().isBlank()) {
                        currentBranchName = b.getBranchName();
                        storeName = b.getBranchName();
                    }
                    if (b.getAddress() != null && !b.getAddress().isBlank()) {
                        branchAddress = b.getAddress();
                    }
                }
            } catch (Exception ignored) {
            }
        }

        // Gửi xuống JSP
        request.setAttribute("user", user);
        request.setAttribute("branchCount", branchCount);
        request.setAttribute("warehouseCount", warehouseCount);
        request.setAttribute("storeName", storeName);
        request.setAttribute("currentBranchName", currentBranchName);
        request.setAttribute("currentBranchAddress", branchAddress);
        request.setAttribute("currentBranchId", currentBranchId);
        request.setAttribute("identificationId", safe(user.getIdentificationId()));

        request.getRequestDispatcher("/WEB-INF/jsp/manager/information_account_bm.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User uSession = (session == null) ? null : (User) session.getAttribute("currentUser");

        if (uSession == null || uSession.getRoleId() != 1) {
            request.getRequestDispatcher("/WEB-INF/jsp/includes/Login.jsp")
                    .forward(request, response);
            return;
        }

        UserDAO userDAO = new UserDAO();
        // Lấy lại từ DB để có đầy đủ các field bắt buộc khi update
        User dbUser = userDAO.getUserById(uSession.getUserId());
        if (dbUser == null) {
            request.setAttribute("error", "Không tìm thấy tài khoản để cập nhật.");
            doGet(request, response);
            return;
        }

        // ===== Lấy dữ liệu form (CHỈ phần cá nhân & liên hệ) =====
        String fullName = trim(request.getParameter("fullName"));
        String genderStr = trim(request.getParameter("gender"));
        String dobStr = trim(request.getParameter("dob"));
        String identificationId = trim(request.getParameter("identificationId"));
        String email = trim(request.getParameter("email"));
        String phone = trim(request.getParameter("phone"));
        String address = trim(request.getParameter("address"));

        // ==== Validate cơ bản ====
        if (isEmpty(fullName)) {
            setErr("Họ và tên không được để trống.", request, response);
            return;
        }
        if (!isEmpty(email) && !EMAIL_REGEX.matcher(email).matches()) {
            setErr("Email không hợp lệ.", request, response);
            return;
        }
        if (!isEmpty(phone) && !PHONE_REGEX.matcher(phone).matches()) {
            setErr("Số điện thoại không hợp lệ. Ví dụ: 0XXXXXXXXX hoặc +84XXXXXXXXX.", request, response);
            return;
        }

        // Nếu muốn BẮT BUỘC nhập CCCD:
        if (isEmpty(identificationId)) {
            setErr("Vui lòng nhập CCCD.", request, response);
            return;
        }

        if (!CCCD_12_DIGITS.matcher(identificationId).matches()) {
            setErr("CCCD phải gồm đúng 12 chữ số.", request, response);
            return;
        }

        Timestamp dob = null;
        if (!isEmpty(dobStr)) {
            try {
                dob = new Timestamp(DOB_FMT.parse(dobStr).getTime());
            } catch (ParseException e) {
                setErr("Ngày sinh không hợp lệ (yyyy-MM-dd).", request, response);
                return;
            }
        }

        // ---- CHỐT TRÙNG (chỉ check khi giá trị MỚI khác DB) ----
        String dbEmail = safeStr(dbUser.getEmail());
        String dbPhone = safeStr(dbUser.getPhone());
        String dbId = safeStr(dbUser.getIdentificationId());

        String newEmail = safeStr(email);
        String newPhone = safeStr(phone);
        String newId = safeStr(identificationId);

// Email: so sánh không phân biệt hoa thường
        boolean emailChanged = !dbEmail.equalsIgnoreCase(newEmail);
        if (emailChanged && !newEmail.isEmpty() && userDAO.isEmailExists(newEmail)) {
            setErr("Email đã được sử dụng bởi tài khoản khác.", request, response);
            return;
        }

// Phone
        boolean phoneChanged = !dbPhone.equals(newPhone);
        if (phoneChanged && !newPhone.isEmpty() && userDAO.isPhoneExists(newPhone)) {
            setErr("Số điện thoại đã được sử dụng bởi tài khoản khác.", request, response);
            return;
        }

// CCCD/Hộ chiếu
        boolean idChanged = !dbId.equals(newId);
        if (idChanged && !newId.isEmpty() && userDAO.isIdentificationIdExists(newId)) {
            setErr("CCCD/Hộ chiếu đã tồn tại.", request, response);
            return;
        }

        // ===== CHUẨN HÓA ID NULLABLE (0 -> null) =====
        Integer safeBranchId = normalizeNullableId(dbUser.getBranchId());
        Integer safeWarehouseId = normalizeNullableId(dbUser.getWarehouseId());

        // ===== GÁN LÊN OBJ để update =====
        User u = new User();
        u.setUserId(dbUser.getUserId());

        // giữ nguyên các trường mà BM không được đổi
        u.setRoleId(dbUser.getRoleId());
        u.setIsActive(dbUser.getIsActive());
        u.setBranchId(safeBranchId);
        u.setWarehouseId(safeWarehouseId);
        u.setTaxNumber(dbUser.getTaxNumber());
        u.setWebUrl(dbUser.getWebUrl());

        // cập nhật phần được phép
        u.setFullName(fullName);
        u.setEmail(email);
        u.setPhone(phone);
        u.setAddress(address);
        if (!isEmpty(genderStr)) {
            u.setGender(Boolean.valueOf(genderStr));
        } else {
            u.setGender(null);
        }
        u.setDob(dob);
        u.setIdentificationId(identificationId);

        boolean ok = false;
        try {
            ok = userDAO.updateUser(u);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (!ok) {
            // Nếu rows==0 (không có thay đổi) DAO vẫn trả false → cho UX thân thiện hơn:
            if (!hasChanged(dbUser, u)) {
                request.setAttribute("success", "Không có thay đổi nào để lưu.");
                doGet(request, response);
                return;
            }
            request.setAttribute("error", "Cập nhật thất bại. Vui lòng thử lại.");
            doGet(request, response);
            return;
        }

        request.setAttribute("success", "Đã lưu thay đổi.");
        session.setAttribute("currentUser", userDAO.getUserById(u.getUserId()));
        doGet(request, response);
    }

    // ===== Helpers =====
    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    private static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String safe(String s) {
        return (s == null || s.isBlank()) ? "—" : s;
    }

    /**
     * Nếu id == null hoặc id == 0 thì trả về null (để không vi phạm FK).
     */
    private static Integer normalizeNullableId(Integer id) {
        if (id == null) {
            return null;
        }
        return (id == 0) ? null : id;
    }

    /**
     * So sánh xem trước/sau có đổi gì không (để thông báo “không có thay đổi”).
     */
    private static boolean hasChanged(User before, User after) {
        if (!safeStr(before.getFullName()).equals(safeStr(after.getFullName()))) {
            return true;
        }
        if (!safeStr(before.getEmail()).equals(safeStr(after.getEmail()))) {
            return true;
        }
        if (!safeStr(before.getPhone()).equals(safeStr(after.getPhone()))) {
            return true;
        }
        if (!safeStr(before.getAddress()).equals(safeStr(after.getAddress()))) {
            return true;
        }
        if (!safeStr(before.getIdentificationId()).equals(safeStr(after.getIdentificationId()))) {
            return true;
        }

        // Gender
        Boolean g1 = before.getGender(), g2 = after.getGender();
        if ((g1 == null) != (g2 == null)) {
            return true;
        }
        if (g1 != null && !g1.equals(g2)) {
            return true;
        }

        // DOB: dùng java.util.Date (bao trùm java.sql.Date/Timestamp)
        java.util.Date d1 = before.getDob();
        java.util.Date d2 = after.getDob();
        long t1 = (d1 == null ? -1L : d1.getTime());
        long t2 = (d2 == null ? -1L : d2.getTime());
        return t1 != t2;
    }

    private static String safeStr(String s) {
        return s == null ? "" : s;
    }

    private void setErr(String msg, HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("error", msg);
        doGet(req, resp);
    }
}

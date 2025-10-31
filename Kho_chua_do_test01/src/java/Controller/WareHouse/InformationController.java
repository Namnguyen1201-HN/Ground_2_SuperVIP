package Controller.WareHouse;

import DAL.UserDAO;
import DAL.WarehouseDAO;
import Model.User;
import Model.Warehouse;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet(name = "InformationController", urlPatterns = {"/Information"})
public class InformationController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("Login");
            return;
        }
        Integer userId = (Integer) session.getAttribute("userID");
        if (userId == null) {
            response.sendRedirect("Login");
            return;
        }

        UserDAO dao = new UserDAO();
        WarehouseDAO wdao = new WarehouseDAO();
        String warehouseAddress = null;

        User user = dao.getUserFullById(userId);

        if (user != null && user.getWarehouseId() != null) {
            Warehouse w = wdao.getWarehouseById(user.getWarehouseId());
            if (w != null) {
                warehouseAddress = w.getAddress();
            }
        }

        request.setAttribute("user", user);
        request.setAttribute("warehouseAddress", warehouseAddress);
        request.getRequestDispatcher("/WEB-INF/jsp/warehouse/Information.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("Login");
            return;
        }
        Integer userId = (Integer) session.getAttribute("userID");
        if (userId == null) {
            response.sendRedirect("Login");
            return;
        }

        try {
            UserDAO dao = new UserDAO();

            // User hiện tại trong DB (để so sánh trùng lặp)
            User current = dao.getUserFullById(userId);
            if (current == null) {
                request.setAttribute("msgType", "danger");
                request.setAttribute("msg", "❌ Không tìm thấy tài khoản.");
                request.getRequestDispatcher("/WEB-INF/jsp/warehouse/Information.jsp").forward(request, response);
                return;
            }

            // === Lấy input từ form ===
            String fullName = trimOrNull(request.getParameter("fullName"));
            String email = trimOrNull(request.getParameter("email"));
            String phone = trimOrNull(request.getParameter("phone"));
            String address = trimOrNull(request.getParameter("address"));
            String identification = trimOrNull(request.getParameter("identificationId"));
            String genderStr = request.getParameter("gender");
            String dobStr = request.getParameter("dob");

            // === Validate & check trùng ===
            StringBuilder errors = new StringBuilder();

            // Họ và tên: bắt buộc, tối thiểu 2 ký tự
            if (fullName == null || fullName.isEmpty()) {
                errors.append("• Họ và tên là bắt buộc.<br/>");
            } else {
                // (khuyến nghị) chỉ cho phép chữ, khoảng trắng và một số ký tự tên riêng; dài 2–100
                if (!fullName.matches("^[\\p{L} .'-]{2,100}$")) {
                    errors.append("• Họ và tên không hợp lệ (chỉ gồm chữ và khoảng trắng, 2–100 ký tự).<br/>");
                }
            }

            // CCCD: đúng 12 chữ số
            if (identification != null && !identification.isEmpty()) {
                if (!identification.matches("^\\d{12}$")) {
                    errors.append("• CCCD phải gồm đúng 12 chữ số.<br/>");
                } else {
                    // chỉ check DB nếu khác giá trị hiện tại
                    String oldId = current.getIdentificationId();
                    if ((oldId == null && identification != null)
                            || (oldId != null && !oldId.equals(identification))) {
                        if (dao.isIdentificationIdExists(identification)) {
                            errors.append("• CCCD đã tồn tại trong hệ thống.<br/>");
                        }
                    }
                }
            }

            // Email: format + trùng (case-insensitive)
            if (email != null && !email.isEmpty()) {
                if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                    errors.append("• Email không hợp lệ.<br/>");
                } else {
                    String oldEmail = current.getEmail();
                    boolean diff = (oldEmail == null && email != null)
                            || (oldEmail != null && !oldEmail.equalsIgnoreCase(email));
                    if (diff && dao.isEmailExists(email)) {
                        errors.append("• Email đã tồn tại trong hệ thống.<br/>");
                    }
                }
            }

            // Số điện thoại: 10 số bắt đầu bằng 0 + check trùng
            if (phone != null && !phone.isEmpty()) {
                if (!phone.matches("^0\\d{9}$")) {
                    errors.append("• Số điện thoại phải gồm 10 số và bắt đầu bằng 0.<br/>");
                } else {
                    String oldPhone = current.getPhone();
                    boolean diff = (oldPhone == null && phone != null)
                            || (oldPhone != null && !oldPhone.equals(phone));
                    if (diff && dao.isPhoneExists(phone)) {
                        errors.append("• Số điện thoại đã tồn tại trong hệ thống.<br/>");
                    }
                }
            }

            // Có lỗi -> trả về form, giữ dữ liệu người dùng nhập
            if (errors.length() > 0) {
                request.setAttribute("msgType", "danger");
                request.setAttribute("msg", errors.toString());

                // đồng bộ lại object để giữ giá trị người dùng vừa nhập
                current.setFullName(fullName);
                current.setEmail(email);
                current.setPhone(phone);
                current.setAddress(address);
                current.setIdentificationId(identification);

                if (genderStr != null) {
                    current.setGender("1".equals(genderStr));
                }
                if (dobStr != null && !dobStr.isEmpty()) {
                    try {
                        Date dob = new SimpleDateFormat("yyyy-MM-dd").parse(dobStr);
                        current.setDob(dob);
                    } catch (Exception ignore) {
                    }
                }

                // set attribute & warehouseAddress rồi forward
                request.setAttribute("user", current);

                WarehouseDAO wdao = new WarehouseDAO();
                String warehouseAddress = null;
                if (current.getWarehouseId() != null) {
                    Warehouse w = wdao.getWarehouseById(current.getWarehouseId());
                    if (w != null) {
                        warehouseAddress = w.getAddress();
                    }
                }
                request.setAttribute("warehouseAddress", warehouseAddress);

                request.getRequestDispatcher("/WEB-INF/jsp/warehouse/Information.jsp").forward(request, response);
                return;
            }

            // === Không có lỗi -> cập nhật DB ===
            current.setFullName(fullName);
            current.setEmail(email);
            current.setPhone(phone);
            current.setAddress(address);
            current.setIdentificationId(identification);
            if (genderStr != null) {
                current.setGender("1".equals(genderStr));
            }

            if (dobStr != null && !dobStr.isEmpty()) {
                Date dob = new SimpleDateFormat("yyyy-MM-dd").parse(dobStr);
                current.setDob(dob);
            } else {
                current.setDob(null);
            }

            boolean success = dao.updateUser(current);
            request.setAttribute("msgType", success ? "success" : "danger");
            request.setAttribute("msg", success ? "✅ Cập nhật thông tin thành công!" : "❌ Cập nhật thất bại!");

            // nạp lại user + địa chỉ kho để hiển thị
            User latest = dao.getUserFullById(userId);
            request.setAttribute("user", latest);

            WarehouseDAO wdao = new WarehouseDAO();
            String warehouseAddress = null;
            if (latest != null && latest.getWarehouseId() != null) {
                Warehouse w = wdao.getWarehouseById(latest.getWarehouseId());
                if (w != null) {
                    warehouseAddress = w.getAddress();
                }
            }
            request.setAttribute("warehouseAddress", warehouseAddress);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("msgType", "danger");
            request.setAttribute("msg", "❌ Lỗi: " + e.getMessage());
        }

        request.getRequestDispatcher("/WEB-INF/jsp/warehouse/Information.jsp").forward(request, response);
    }

    // tiện ích nhỏ để trim chuỗi
    private String trimOrNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

}

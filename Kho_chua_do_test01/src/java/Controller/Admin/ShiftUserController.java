package Controller.Admin;

import DAL.ShiftDAO;
import Model.Shift;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet(name = "ShiftUserController", urlPatterns = {"/ShiftUser"})
public class ShiftUserController extends HttpServlet {

    private static final int PAGE_SIZE = 5;
    private static final LocalTime MAX_TIME = LocalTime.of(23, 59);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        ShiftDAO dao = new ShiftDAO();

        // --- Tìm kiếm ---
        String search = trimOrNull(request.getParameter("search"));
        List<Shift> all = dao.getAll();
        if (search != null) {
            String kw = search.toLowerCase();
            all = all.stream()
                    .filter(s -> s.getShiftName() != null && s.getShiftName().toLowerCase().contains(kw))
                    .collect(Collectors.toList());
            request.setAttribute("search", search);
        }

        // --- Phân trang ---
        int total = all.size();
        int totalPages = Math.max(1, (int) Math.ceil(total / (double) PAGE_SIZE));
        int page = safeParseInt(request.getParameter("page"), 1);
        page = Math.min(Math.max(page, 1), totalPages); // clamp

        int start = (page - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, total);
        List<Shift> pageItems = total == 0 ? Collections.emptyList() : all.subList(start, end);

        // --- Gửi attribute ---
        request.setAttribute("shiftList", pageItems);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("startShift", total == 0 ? 0 : start + 1);
        request.setAttribute("endShift", end);
        request.setAttribute("totalShift", total);

        request.getRequestDispatcher("/WEB-INF/jsp/admin/ShiftUser.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String action = trimOrNull(request.getParameter("action"));
        if (action == null) {
            response.sendRedirect("ShiftUser");
            return;
        }

        ShiftDAO dao = new ShiftDAO();

        try {
            switch (action) {
                case "create": {
                    ValidationResult vr = buildAndValidateShift(request, false);
                    if (!vr.ok) {
                        request.setAttribute("error", vr.message);
                        doGet(request, response);
                        return;
                    }
                    dao.insert(vr.shift);
                    break;
                }
                case "update": {
                    ValidationResult vr = buildAndValidateShift(request, true);
                    if (!vr.ok) {
                        request.setAttribute("error", vr.message);
                        doGet(request, response);
                        return;
                    }
                    dao.update(vr.shift);
                    break;
                }
                case "delete": {
                    int id = safeParseInt(request.getParameter("shiftID"), -1);
                    if (id <= 0) {
                        request.setAttribute("error", "Thiếu hoặc sai ShiftID để xoá.");
                        doGet(request, response);
                        return;
                    }
                    dao.delete(id);
                    break;
                }
                default:
                    // hành động không hỗ trợ
                    request.setAttribute("error", "Hành động không hợp lệ.");
                    doGet(request, response);
                    return;
            }

            // PRG sau thao tác thành công
            response.sendRedirect("ShiftUser");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            doGet(request, response);
        }
    }

    // ----------------- Helpers -----------------

    /** Dựng Shift từ request + validate giờ làm; requireId=true cho update. */
    private ValidationResult buildAndValidateShift(HttpServletRequest req, boolean requireId) {
        // Tên ca
        String name = trimOrNull(req.getParameter("shiftName"));
        if (name == null || name.isEmpty()) {
            return ValidationResult.fail("Tên ca làm không được để trống.");
        }

        // Parse giờ
        LocalTime start, end;
        try {
            start = LocalTime.parse(req.getParameter("startTime"));
            end   = LocalTime.parse(req.getParameter("endTime"));
        } catch (DateTimeParseException ex) {
            return ValidationResult.fail("Định dạng giờ không hợp lệ (hh:mm).");
        }

        // Validate logic giờ
        String timeErr = validateTimes(start, end);
        if (timeErr != null) return ValidationResult.fail(timeErr);

        // Build model
        Shift s = new Shift();
        s.setShiftName(name);
        s.setStartTime(start);
        s.setEndTime(end);

        if (requireId) {
            int id = safeParseInt(req.getParameter("shiftID"), -1);
            if (id <= 0) return ValidationResult.fail("Thiếu hoặc sai ShiftID.");
            s.setShiftID(id);
        }
        return ValidationResult.ok(s);
    }

    /** Validate thời gian: end > start và end <= 23:59. */
    private String validateTimes(LocalTime start, LocalTime end) {
        if (!end.isAfter(start)) {
            return "Giờ kết thúc phải sau giờ bắt đầu.";
        }
        if (end.isAfter(MAX_TIME)) {
            return "Giờ kết thúc không được vượt quá 23:59.";
        }
        return null;
    }

    private static int safeParseInt(String s, int fallback) {
        if (s == null) return fallback;
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return fallback; }
    }

    private static String trimOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    /** Gói kết quả validate để trả về cả lỗi và Shift. */
    private static class ValidationResult {
        final boolean ok;
        final String message;
        final Shift shift;

        private ValidationResult(boolean ok, String message, Shift shift) {
            this.ok = ok; this.message = message; this.shift = shift;
        }

        static ValidationResult ok(Shift s) { return new ValidationResult(true, null, s); }
        static ValidationResult fail(String msg) { return new ValidationResult(false, msg, null); }
    }
}

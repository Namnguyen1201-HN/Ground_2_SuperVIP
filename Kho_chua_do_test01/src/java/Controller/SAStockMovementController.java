package Controller;

import DAL.StockMovementDAO;
import Model.StockMovementsRequest;
import Model.StockMovementDetail;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/** Controller quản lý Yêu cầu nhập/chuyển hàng (SA). */
@WebServlet(name = "SAStockMovementController", urlPatterns = {"/sa-stock"})
public class SAStockMovementController extends HttpServlet {

    private StockMovementDAO dao;

    @Override
    public void init() {
        dao = new StockMovementDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String action = nvl(req.getParameter("action"), "list");
        try {
            switch (action) {
                case "add"    -> showCreateForm(req, resp);
                case "view"   -> viewDetail(req, resp);
                case "edit"   -> showEditHeaderForm(req, resp);
                case "delete" -> deleteMove(req, resp);
                case "list"   -> listMoves(req, resp);
                default       -> listMoves(req, resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Đã xảy ra lỗi khi xử lý yêu cầu");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String action = nvl(req.getParameter("action"), "list");
        try {
            switch (action) {
                case "create"       -> createMove(req, resp);
                case "updateHeader" -> updateHeader(req, resp);
                // (tuỳ chọn) case "replaceDetails" -> replaceDetails(req, resp);
                default             -> doGet(req, resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Đã xảy ra lỗi khi xử lý dữ liệu gửi lên");
        }
    }

    /* ========================= LIST ========================= */
    private void listMoves(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String type = trimOrNull(req.getParameter("type"));
        Integer branchId    = parseIntOrNull(req.getParameter("branchId"));
        Integer warehouseId = parseIntOrNull(req.getParameter("warehouseId"));

        Timestamp from = parseTimestamp(req.getParameter("from"));
        Timestamp to   = parseTimestamp(req.getParameter("to"));

        int page     = parseIntOrDefault(req.getParameter("page"), 1);
        int pageSize = parseIntOrDefault(req.getParameter("pageSize"), 20);

        StockMovementDAO.PagedMoves paged = dao.list(
                type, branchId, warehouseId, from, to, page, pageSize
        );

        req.setAttribute("moves", paged.items);
        req.setAttribute("total", paged.total);
        req.setAttribute("page", paged.page);
        req.setAttribute("pageSize", paged.pageSize);

        req.setAttribute("type", type);
        req.setAttribute("branchId", branchId);
        req.setAttribute("warehouseId", warehouseId);
        req.setAttribute("from", req.getParameter("from"));
        req.setAttribute("to", req.getParameter("to"));

        req.getRequestDispatcher("/WEB-INF/jsp/sale/SAMoveList.jsp").forward(req, resp);
    }

    /* ========================= CREATE ========================= */
    private void showCreateForm(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        // TODO: nạp dropdown suppliers/branches/warehouses/productDetails nếu cần
        req.getRequestDispatcher("/WEB-INF/jsp/sale/SAMoveForm.jsp").forward(req, resp);
    }

    private void createMove(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Integer fromSupplierId  = parseIntOrNull(req.getParameter("fromSupplierId"));
        Integer fromBranchId    = parseIntOrNull(req.getParameter("fromBranchId"));
        Integer fromWarehouseId = parseIntOrNull(req.getParameter("fromWarehouseId"));
        Integer toBranchId      = parseIntOrNull(req.getParameter("toBranchId"));
        Integer toWarehouseId   = parseIntOrNull(req.getParameter("toWarehouseId"));
        String  movementType    = nvl(req.getParameter("movementType"), "IMPORT");
        String  note            = trimOrNull(req.getParameter("note"));

        Integer createdBy = (Integer) req.getSession().getAttribute("userId");
        if (createdBy == null) createdBy = 1; // đảm bảo tồn tại trong Users để không lỗi FK

        List<StockMovementDetail> details = parseDetailsFromRequest(req);

        List<String> errors = validateHeader(movementType, fromSupplierId, fromBranchId, fromWarehouseId, toBranchId, toWarehouseId);
        if (details == null || details.isEmpty()) {
            errors.add("Cần ít nhất 1 dòng chi tiết (ProductDetailID & Số lượng).");
        }
        if (!errors.isEmpty()) {
            req.setAttribute("errorList", errors);
            keepHeaderInput(req, movementType, fromSupplierId, fromBranchId, fromWarehouseId, toBranchId, toWarehouseId, note);
            showCreateForm(req, resp);
            return;
        }

        StockMovementsRequest r = new StockMovementsRequest();
        r.setFromSupplierId(fromSupplierId);
        r.setFromBranchId(fromBranchId);
        r.setFromWarehouseId(fromWarehouseId);
        r.setToBranchId(toBranchId);
        r.setToWarehouseId(toWarehouseId);
        r.setMovementType(movementType);
        r.setCreatedBy(createdBy);
        r.setNote(note);
        r.setDetails(details);

        int newId = dao.create(r);
        if (newId <= 0) {
            req.setAttribute("error", "Tạo yêu cầu thất bại (không nhận được ID).");
            showCreateForm(req, resp);
            return;
        }
        resp.sendRedirect(req.getContextPath() + "/sa-stock?action=view&id=" + newId);
    }

    private List<StockMovementDetail> parseDetailsFromRequest(HttpServletRequest req) {
        String[] pdIds = req.getParameterValues("productDetailId");
        String[] qtys  = req.getParameterValues("quantity");

        List<StockMovementDetail> list = new ArrayList<>();
        if (pdIds == null || qtys == null) return list;

        int n = Math.min(pdIds.length, qtys.length);
        for (int i = 0; i < n; i++) {
            Integer pdId = parseIntOrNull(pdIds[i]);
            Integer q    = parseIntOrNull(qtys[i]);
            if (pdId == null || q == null || q <= 0) continue;

            StockMovementDetail d = new StockMovementDetail();
            d.setProductDetailId(pdId);
            d.setQuantity(q);
            list.add(d);
        }
        return list;
    }

    /* ========================= VIEW / EDIT ========================= */
    private void viewDetail(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Integer id = parseIntOrNull(req.getParameter("id"));
        if (id == null) { resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu id"); return; }

        var r = dao.getById(id);
        if (r == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy yêu cầu"); return; }

        req.setAttribute("move", r);
        req.getRequestDispatcher("/WEB-INF/jsp/sale/SAMoveDetail.jsp").forward(req, resp);
    }

    private void showEditHeaderForm(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Integer id = parseIntOrNull(req.getParameter("id"));
        if (id == null) { resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu id"); return; }

        var r = dao.getById(id);
        if (r == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy yêu cầu"); return; }

        req.setAttribute("move", r);
        req.getRequestDispatcher("/WEB-INF/jsp/sale/SAMoveForm.jsp").forward(req, resp);
    }

    private void updateHeader(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Integer id = parseIntOrNull(req.getParameter("id"));
        if (id == null) { resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu id"); return; }

        // đọc tất cả field header từ form
        Integer fromSupplierId  = parseIntOrNull(req.getParameter("fromSupplierId"));
        Integer fromBranchId    = parseIntOrNull(req.getParameter("fromBranchId"));
        Integer fromWarehouseId = parseIntOrNull(req.getParameter("fromWarehouseId"));
        Integer toBranchId      = parseIntOrNull(req.getParameter("toBranchId"));
        Integer toWarehouseId   = parseIntOrNull(req.getParameter("toWarehouseId"));
        String  movementType    = nvl(req.getParameter("movementType"), "IMPORT");
        String  note            = trimOrNull(req.getParameter("note"));

        List<String> errors = validateHeader(movementType, fromSupplierId, fromBranchId, fromWarehouseId, toBranchId, toWarehouseId);
        if (!errors.isEmpty()) {
            req.setAttribute("errorList", errors);
            // nạp lại đối tượng để form hiển thị
            var r = dao.getById(id);
            req.setAttribute("move", r);
            req.getRequestDispatcher("/WEB-INF/jsp/sale/SAMoveForm.jsp").forward(req, resp);
            return;
        }

        boolean ok = dao.updateHeaderFull(id,
                fromSupplierId, fromBranchId, fromWarehouseId,
                toBranchId, toWarehouseId, movementType, note);

        if (!ok) {
            req.setAttribute("error", "Cập nhật thất bại.");
            var r = dao.getById(id);
            req.setAttribute("move", r);
            req.getRequestDispatcher("/WEB-INF/jsp/sale/SAMoveForm.jsp").forward(req, resp);
            return;
        }
        resp.sendRedirect(req.getContextPath() + "/sa-stock?action=view&id=" + id);
    }

    /* ========================= DELETE ========================= */
    private void deleteMove(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Integer id = parseIntOrNull(req.getParameter("id"));
        if (id == null) { resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu id"); return; }
        dao.delete(id);
        resp.sendRedirect(req.getContextPath() + "/sa-stock?action=list");
    }

    /* ========================= Helpers ========================= */

    private List<String> validateHeader(String movementType,
                                        Integer fromSupplierId, Integer fromBranchId, Integer fromWarehouseId,
                                        Integer toBranchId, Integer toWarehouseId) {
        List<String> errors = new ArrayList<>();
        boolean hasSource = (fromSupplierId != null) || (fromBranchId != null) || (fromWarehouseId != null);
        boolean hasTarget = (toBranchId != null) || (toWarehouseId != null);

        if ("IMPORT".equalsIgnoreCase(movementType)) {
            if (!(fromSupplierId != null || fromWarehouseId != null)) {
                errors.add("IMPORT: cần chọn FromSupplier hoặc FromWarehouse.");
            }
            if (!hasTarget) {
                errors.add("IMPORT: cần chọn điểm đến (ToBranch hoặc ToWarehouse).");
            }
        } else if ("TRANSFER".equalsIgnoreCase(movementType)) {
            if (!(fromBranchId != null || fromWarehouseId != null)) {
                errors.add("TRANSFER: cần chọn điểm đi (FromBranch hoặc FromWarehouse).");
            }
            if (!hasTarget) {
                errors.add("TRANSFER: cần chọn điểm đến (ToBranch hoặc ToWarehouse).");
            }
        } else {
            // nếu có thêm loại khác, bổ sung ở đây
            if (!hasSource || !hasTarget) {
                errors.add("Cần chọn điểm đi và điểm đến hợp lệ.");
            }
        }
        return errors;
    }

    private void keepHeaderInput(HttpServletRequest req, String movementType,
                                 Integer fromSupplierId, Integer fromBranchId, Integer fromWarehouseId,
                                 Integer toBranchId, Integer toWarehouseId, String note) {
        req.setAttribute("mv_movementType", movementType);
        req.setAttribute("mv_fromSupplierId", fromSupplierId);
        req.setAttribute("mv_fromBranchId", fromBranchId);
        req.setAttribute("mv_fromWarehouseId", fromWarehouseId);
        req.setAttribute("mv_toBranchId", toBranchId);
        req.setAttribute("mv_toWarehouseId", toWarehouseId);
        req.setAttribute("mv_note", note);
    }

    private Integer parseIntOrNull(String s) {
        try { return (s == null || s.isBlank()) ? null : Integer.valueOf(s.trim()); }
        catch (Exception e) { return null; }
    }

    private int parseIntOrDefault(String s, int def) {
        try { return (s == null || s.isBlank()) ? def : Integer.parseInt(s.trim()); }
        catch (Exception e) { return def; }
    }

    private String trimOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
        }

    // "yyyy-MM-dd" hoặc "yyyy-MM-dd HH:mm" -> Timestamp
    private Timestamp parseTimestamp(String s) {
        String v = trimOrNull(s);
        if (v == null) return null;
        try {
            LocalDateTime ldt;
            if (v.length() == 10) {
                ldt = LocalDateTime.parse(v + " 00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            } else {
                ldt = LocalDateTime.parse(v, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            }
            return Timestamp.valueOf(ldt);
        } catch (Exception e) {
            return null;
        }
    }

    private String nvl(String s, String def) {
        return (s == null || s.isBlank()) ? def : s;
    }
}

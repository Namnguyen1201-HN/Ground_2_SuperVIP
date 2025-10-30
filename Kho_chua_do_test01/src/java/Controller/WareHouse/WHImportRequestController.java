package Controller.WareHouse;

import DAL.StockMovementsRequestDAO;
import DAL.StockMovementDetailDAO;
import Model.StockMovementsRequest;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "WHImportRequestController", urlPatterns = {"/wh-import"})
public class WHImportRequestController extends HttpServlet {

    // ===== PAGE SIZE CỐ ĐỊNH =====
    private static final int PAGE_SIZE = 6; // đổi số này nếu muốn nhiều/ít dòng hơn

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1) Session
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("warehouseId") == null) {
            response.sendRedirect("login");
            return;
        }
        String warehouseId = String.valueOf(session.getAttribute("warehouseId"));

        // 2) Filters
        String fromDate   = nvl(request.getParameter("fromDate"));
        String toDate     = nvl(request.getParameter("toDate"));
        String branchId   = nvl(request.getParameter("branchId"));
        String supplierId = nvl(request.getParameter("supplierId"));
        String status     = nvl(request.getParameter("status")); // "", pending, processing, completed

        // 3) Pagination (chỉ có currentPage, page size cố định)
        int currentPage  = parseIntOrDefault(request.getParameter("page"), 1);
        if (currentPage < 1) currentPage = 1;

        StockMovementsRequestDAO dao = new StockMovementsRequestDAO();
        StockMovementDetailDAO ddao  = new StockMovementDetailDAO();

        try {
            
            // =====================================================================================
            // ===== [NEW] — TÍNH STATUS TRƯỚC → LỌC THEO STATUS ĐÃ TÍNH → PHÂN TRANG =====
            final int BIG_PAGE = 1_000_000; // lấy “đủ lớn” để lọc nội bộ (nếu cần tối ưu sẽ refactor DAO)
            // KHÔNG lọc status ở DB
            List<StockMovementsRequest> all = dao.getImportRequestsWithFilter(
                    warehouseId, fromDate, toDate, branchId, supplierId,
                    /*status=*/"", 1, BIG_PAGE
            );

            // Tính trạng thái hiển thị theo scan
            for (StockMovementsRequest m : all) {
                String computed = ddao.computeStatusByScanned(m.getMovementId());
                m.setResponseStatus(computed);
            }

            // Lọc theo trạng thái đã tính (nếu có chọn)
            List<StockMovementsRequest> filtered = new ArrayList<>();
            if (status.isEmpty()) {
                filtered = all;
            } else {
                for (StockMovementsRequest m : all) {
                    if (status.equalsIgnoreCase(nvl(m.getResponseStatus()))) {
                        filtered.add(m);
                    }
                }
            }

            // PHÂN TRANG (page size cố định PAGE_SIZE)
            int totalItems = filtered.size();
            int totalPages = totalItems == 0 ? 1 : (int) Math.ceil((double) totalItems / PAGE_SIZE);
            if (currentPage > totalPages) currentPage = totalPages;

            int fromIdx = (currentPage - 1) * PAGE_SIZE;                 // inclusive
            int toIdx   = Math.min(fromIdx + PAGE_SIZE, totalItems);      // exclusive
            if (fromIdx < 0) fromIdx = 0;
            if (fromIdx > toIdx) fromIdx = toIdx;

            List<StockMovementsRequest> pageItems = filtered.subList(fromIdx, toIdx);

            int startItem = totalItems == 0 ? 0 : fromIdx + 1;
            int endItem   = totalItems == 0 ? 0 : toIdx;

            // Gắn attribute cho JSP
            request.setAttribute("importRequests", pageItems);

            // filter echo
            request.setAttribute("fromDate", fromDate);
            request.setAttribute("toDate", toDate);
            request.setAttribute("branchId", branchId);
            request.setAttribute("supplierId", supplierId);
            request.setAttribute("status", status);

            // paging echo
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalItems", totalItems);
            request.setAttribute("itemsPerPage", PAGE_SIZE); // để hiện summary thôi
            request.setAttribute("startItem", startItem);
            request.setAttribute("endItem", endItem);

            // base URL (không gồm page)
            String baseQuery = buildQueryString(fromDate, toDate, branchId, supplierId, status);
            request.setAttribute("baseQuery", baseQuery);

            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/warehouse/wh-import.jsp");
            rd.forward(request, response);
            // =====================================================================================

        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("error", "Lỗi khi tải danh sách đơn nhập hàng: " + ex.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/warehouse/wh-import.jsp").forward(request, response);
        }
    }

    // ===== Helpers =====
    private static String nvl(String s) {
        return (s == null) ? "" : s.trim();
    }

    private static int parseIntOrDefault(String s, int d) {
        try { return Integer.parseInt(s); } catch (Exception e) { return d; }
    }

    private static String enc(String v) {
        return URLEncoder.encode(nvl(v), StandardCharsets.UTF_8);
    }

    /** Tạo query giữ nguyên filter, bỏ qua page (page sẽ append sau khi render link) */
    private static String buildQueryString(String fromDate, String toDate,
                                           String branchId, String supplierId,
                                           String status) {
        StringBuilder sb = new StringBuilder();
        if (!fromDate.isEmpty())   sb.append("&fromDate=").append(enc(fromDate));
        if (!toDate.isEmpty())     sb.append("&toDate=").append(enc(toDate));
        if (!branchId.isEmpty())   sb.append("&branchId=").append(enc(branchId));
        if (!supplierId.isEmpty()) sb.append("&supplierId=").append(enc(supplierId));
        if (!status.isEmpty())     sb.append("&status=").append(enc(status));
        return sb.toString();
    }

    @Override
    public String getServletInfo() {
        return "Danh sách đơn nhập hàng (phân trang với page size cố định).";
    }
}

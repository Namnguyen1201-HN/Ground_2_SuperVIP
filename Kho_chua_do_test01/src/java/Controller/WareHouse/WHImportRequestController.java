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
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "WHImportRequestController", urlPatterns = {"/wh-import"})
public class WHImportRequestController extends HttpServlet {

    private static final int DEFAULT_PAGE_SIZE = 10;

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
        String fromDate = nvl(request.getParameter("fromDate"));
        String toDate = nvl(request.getParameter("toDate"));
        String branchId = nvl(request.getParameter("branchId"));
        String supplierId = nvl(request.getParameter("supplierId"));
        String status = nvl(request.getParameter("status")); // "", pending, processing, completed

        // 3) Pagination
        int currentPage = parseIntOrDefault(request.getParameter("page"), 1);
        int itemsPerPage = parseIntOrDefault(request.getParameter("recordsPerPage"), DEFAULT_PAGE_SIZE);
        if (currentPage < 1) {
            currentPage = 1;
        }
        if (itemsPerPage < 1) {
            itemsPerPage = DEFAULT_PAGE_SIZE;
        }

        StockMovementsRequestDAO dao = new StockMovementsRequestDAO();
        StockMovementDetailDAO ddao = new StockMovementDetailDAO();

        try {
            // ========================== OPTION A (GIỮ DAO NHƯ CŨ) ==========================
            // DAO vẫn nhận status; controller sẽ TÍNH LẠI trạng thái để đảm bảo hiển thị đúng.
            int totalItems = dao.getImportRequestsCount(
                    warehouseId, fromDate, toDate, branchId, supplierId, status
            );

            int totalPages = totalItems > 0
                    ? (int) Math.ceil((double) totalItems / itemsPerPage)
                    : 1;

            if (currentPage > totalPages && totalPages > 0) {
                currentPage = totalPages;
                String redirect = buildRedirectUrl("wh-import", fromDate, toDate, branchId, supplierId, status, itemsPerPage, currentPage);
                response.sendRedirect(redirect);
                return;
            }

            List<StockMovementsRequest> importRequests = dao.getImportRequestsWithFilter(
                    warehouseId, fromDate, toDate, branchId, supplierId, status, currentPage, itemsPerPage
            );

            // === TÍNH LẠI TRẠNG THÁI THEO SCANNED CHO MỖI PHIẾU ===
            // Quy ước:
            // - pending: mọi dòng QuantityScanned = 0 (hoặc không có dòng)
            // - processing: có dòng đã quét (>0) nhưng chưa đủ tất cả
            // - completed: mọi dòng quét đủ (>= Quantity)
            for (StockMovementsRequest m : importRequests) {
                String computed = ddao.computeStatusByScanned(m.getMovementId());
                m.setResponseStatus(computed); // Ghi đè để JSP hiển thị badge đúng
            }

            int startItem = totalItems > 0 ? ((currentPage - 1) * itemsPerPage) + 1 : 0;
            int endItem = Math.min(currentPage * itemsPerPage, totalItems);

            // 6) Set attrs
            request.setAttribute("importRequests", importRequests);
            request.setAttribute("fromDate", fromDate);
            request.setAttribute("toDate", toDate);
            request.setAttribute("branchId", branchId);
            request.setAttribute("supplierId", supplierId);
            request.setAttribute("status", status);

            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalItems", totalItems);
            request.setAttribute("itemsPerPage", itemsPerPage);
            request.setAttribute("startItem", startItem);
            request.setAttribute("endItem", endItem);

            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/warehouse/wh-import.jsp");
            rd.forward(request, response);

            // ========================== OPTION B (NẾU DAO CHƯA FILTER THEO STATUS MỚI) ==========================
            // 1) Gọi DAO KHÔNG truyền status để lấy raw-list (toàn bộ) + đếm
            // 2) Controller tự computeStatusByScanned() cho từng phiếu, rồi TỰ LỌC theo status
            // 3) TỰ PHÂN TRANG lại trên danh sách đã lọc
            //
            // // Ví dụ (bật khi cần):
            // List<StockMovementsRequest> all = dao.getImportRequestsWithFilter(warehouseId, fromDate, toDate, branchId, supplierId, "", 1, Integer.MAX_VALUE);
            // List<StockMovementsRequest> filtered = new ArrayList<>();
            // for (StockMovementsRequest m : all) {
            //     String computed = ddao.computeStatusByScanned(m.getMovementID());
            //     m.setResponseStatus(computed);
            //     if (status.isEmpty() || status.equalsIgnoreCase(computed)) {
            //         filtered.add(m);
            //     }
            // }
            // int totalItemsB = filtered.size();
            // int totalPagesB = totalItemsB > 0 ? (int)Math.ceil((double)totalItemsB/itemsPerPage) : 1;
            // if (currentPage > totalPagesB && totalPagesB > 0) currentPage = totalPagesB;
            // int fromIdx = Math.max(0, (currentPage-1)*itemsPerPage);
            // int toIdx   = Math.min(totalItemsB, currentPage*itemsPerPage);
            // List<StockMovementsRequest> pageList = filtered.subList(fromIdx, toIdx);
            // // rồi set attribute giống như trên
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

    private static int parseIntOrDefault(String s, int def) {
        try {
            return (s == null || s.trim().isEmpty()) ? def : Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private static String buildRedirectUrl(String base,
            String fromDate, String toDate,
            String branchId, String supplierId, String status,
            int itemsPerPage, int page) {
        StringBuilder sb = new StringBuilder(base).append("?page=").append(page)
                .append("&recordsPerPage=").append(itemsPerPage);
        if (!fromDate.isEmpty()) {
            sb.append("&fromDate=").append(fromDate);
        }
        if (!toDate.isEmpty()) {
            sb.append("&toDate=").append(toDate);
        }
        if (!branchId.isEmpty()) {
            sb.append("&branchId=").append(branchId);
        }
        if (!supplierId.isEmpty()) {
            sb.append("&supplierId=").append(supplierId);
        }
        if (!status.isEmpty()) {
            sb.append("&status=").append(status);
        }
        return sb.toString();
    }

    @Override
    public String getServletInfo() {
        return "Hiển thị danh sách phiếu nhập kho (quản lý kho)";
    }
}

package Controller.WareHouse;

import DAL.SerialNumberDAO;
import DAL.StockMovementsRequestDAO;
import DAL.StockMovementDetailDAO;
import DAL.StockMovementResponseDAO;
import Model.ProductDetailSerialNumber;
import Model.StockMovementsRequest;
import Model.StockMovementDetail;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "WHStockSerialCheckController", urlPatterns = {"/wh-import-export-detail", "/serial-check"})
public class WHStockSerialCheckController extends HttpServlet {

    private final StockMovementsRequestDAO requestDAO = new StockMovementsRequestDAO();
    private final StockMovementDetailDAO detailDAO = new StockMovementDetailDAO();
    private final StockMovementResponseDAO responseDAO = new StockMovementResponseDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // 1) Lấy ID đơn và loại (import/export)
            String idParam = firstNonEmpty(request.getParameter("id"), request.getParameter("movementId"));
            Integer movementId = tryParseInt(idParam);
            String movementType = request.getParameter("movementType");
            if (movementType == null || movementType.isEmpty()) {
                movementType = "import"; // default
            }

            if (movementId == null) {
                if ("export".equalsIgnoreCase(movementType)) {
                    response.sendRedirect("warehouse-export-orders");
                } else {
                    response.sendRedirect("wh-import");
                }
                return;
            }

            // 2) Header phiếu - check both import and export
            StockMovementsRequest movement = null;
            if ("export".equalsIgnoreCase(movementType)) {
                movement = requestDAO.getExportRequestById(movementId);
            } else {
                movement = requestDAO.getImportRequestById(movementId);
            }

            if (movement == null) {
                String errorMsg = "export".equalsIgnoreCase(movementType) 
                    ? "Không tìm thấy đơn xuất hàng #" + movementId
                    : "Không tìm thấy đơn nhập hàng #" + movementId;
                request.setAttribute("errorMessage", errorMsg);
                if ("export".equalsIgnoreCase(movementType)) {
                    request.getRequestDispatcher("/WEB-INF/jsp/warehouse/wh-export-orders.jsp").forward(request, response);
                } else {
                    request.getRequestDispatcher("/WEB-INF/jsp/warehouse/wh-import.jsp").forward(request, response);
                }
                return;
            }

            // 3) Chi tiết + serials
            List<StockMovementDetail> movementDetails = detailDAO.getMovementDetailsByMovementId(movementId);
            SerialNumberDAO snDao = new SerialNumberDAO();
            for (StockMovementDetail d : movementDetails) {
                d.setSerials(snDao.getSerialsByMovementDetailId(d.getMovementDetailId()));
            }

            // 4) Gán dữ liệu cho JSP
            request.setAttribute("movement", movement);
            request.setAttribute("movementDetails", movementDetails);
            request.setAttribute("movementID", movementId);
            request.setAttribute("movementType", movementType);
            request.setAttribute("totalItems", movementDetails.size());
            request.setAttribute("startItem", movementDetails.isEmpty() ? 0 : 1);
            request.setAttribute("endItem", movementDetails.size());
            request.setAttribute("currentPage", 1);
            request.setAttribute("totalPages", 1);
            request.setAttribute("itemsPerPage", movementDetails.size());

            // Trạng thái hiển thị ở header trang chi tiết
            String movementStatus = detailDAO.computeStatusByScanned(movementId);
            request.setAttribute("movementStatus", movementStatus);

            // có thể hoàn tất không
            boolean canComplete = detailDAO.isAllDetailsCompleted(movementId);
            request.setAttribute("canComplete", canComplete);
            request.setAttribute("id", movementId);

            request.getRequestDispatcher("/WEB-INF/jsp/warehouse/wh-import-export-detail.jsp")
                    .forward(request, response);

        } catch (Exception ex) {
            ex.printStackTrace();
            String movementType = request.getParameter("movementType");
            String errorMsg = "export".equalsIgnoreCase(movementType) 
                ? "Lỗi khi tải chi tiết đơn xuất hàng: " + ex.getMessage()
                : "Lỗi khi tải chi tiết đơn nhập hàng: " + ex.getMessage();
            request.setAttribute("errorMessage", errorMsg);
            if ("export".equalsIgnoreCase(movementType)) {
                request.getRequestDispatcher("/WEB-INF/jsp/warehouse/wh-export-orders.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("/WEB-INF/jsp/warehouse/wh-import.jsp").forward(request, response);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // Tham số an toàn
        String action = nz(request.getParameter("action")); // addSerial | delSerial
        String serial = nz(request.getParameter("serialNumber")).trim();
        Integer movementId = tryParseInt(firstNonEmpty(request.getParameter("id"),
                request.getParameter("movementId")));
        Integer movementDetailId = tryParseInt(request.getParameter("movementDetailId"));

        String movementType = request.getParameter("movementType");
        if (movementType == null || movementType.isEmpty()) {
            movementType = "import";
        }

        if (movementId == null || movementDetailId == null) {
            request.getSession().setAttribute("errorMessage", "Thiếu tham số id hoặc movementDetailId.");
            if ("export".equalsIgnoreCase(movementType)) {
                response.sendRedirect("warehouse-export-orders");
            } else {
                response.sendRedirect("wh-import");
            }
            return;
        }

        // Chặn nếu phiếu đã completed
        try {
            String latest = responseDAO.getLatestStatusByMovementId(movementId);
            if ("completed".equalsIgnoreCase(latest) || "Hoàn thành".equalsIgnoreCase(latest)) {
                request.getSession().setAttribute("errorMessage", "Đơn đã hoàn thành, không thể chỉnh serial.");
                String redirectUrl = "serial-check?id=" + movementId + "&movementType=" + movementType;
                response.sendRedirect(redirectUrl);
                return;
            }
        } catch (Exception ignore) {
        }

        // Chuẩn bị DAO
        SerialNumberDAO snDao = new SerialNumberDAO();
        StockMovementDetailDAO ddao = new StockMovementDetailDAO();
        boolean ok = false;

        try {
            if ("addSerial".equalsIgnoreCase(action)) {
                // chặn overflow dòng
                if (ddao.isDetailCompleted(movementDetailId)) {
                    request.getSession().setAttribute("errorMessage", "Dòng này đã đủ số lượng, không thể thêm serial.");
                    String redirectUrl = "serial-check?id=" + movementId + "&movementType=" + movementType;
                    response.sendRedirect(redirectUrl);
                    return;
                }

                // productDetailId từ form hoặc fallback
                Integer productDetailId = tryParseInt(request.getParameter("productDetailId"));
                if (productDetailId == null) {
                    try {
                        productDetailId = detailDAO.getProductDetailIdByMovementDetailId(movementDetailId);
                    } catch (Exception ignore) {
                    }
                }

                if (serial.isEmpty()) {
                    request.getSession().setAttribute("errorMessage", "Serial rỗng.");
                } else {
                    ProductDetailSerialNumber s = new ProductDetailSerialNumber();
                    s.setMovementDetailID(movementDetailId);
                    s.setSerialNumber(serial);
                    s.setStatus(Boolean.TRUE);
                    if (productDetailId != null) {
                        s.setProductDetailID(productDetailId);
                    }

                    ok = snDao.insertSerial(s); // DAO đã chặn trùng + transaction +1 scanned
                    request.getSession().setAttribute(ok ? "successMessage" : "errorMessage",
                            ok ? "Đã thêm serial." : "Không thêm được serial (trùng/đủ số lượng/ lỗi).");
                }

            } else if ("delSerial".equalsIgnoreCase(action)) {
                if (serial.isEmpty()) {
                    request.getSession().setAttribute("errorMessage", "Thiếu serial để xoá.");
                } else {
                    ok = snDao.deleteSingleSerial(movementDetailId, serial); // DAO tự -1 scanned
                    request.getSession().setAttribute(ok ? "successMessage" : "errorMessage",
                            ok ? "Đã xoá serial." : "Không xoá được serial.");
                }

            } else {
                request.getSession().setAttribute("errorMessage", "Action không hợp lệ.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Có lỗi khi xử lý serial.");
        }

        String redirectUrl = "serial-check?id=" + movementId + "&movementType=" + movementType;
        response.sendRedirect(redirectUrl);
    }

    // ===== Helpers (trong class, ngoài phương thức) =====
    private static String nz(String s) {
        return s == null ? "" : s;
    }

    private static String firstNonEmpty(String a, String b) {
        if (a != null && !a.trim().isEmpty()) {
            return a;
        }
        if (b != null && !b.trim().isEmpty()) {
            return b;
        }
        return null;
    }

    private static Integer tryParseInt(String s) {
        try {
            return (s == null) ? null : Integer.valueOf(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String getServletInfo() {
        return "Xem chi tiết phiếu nhập/xuất (bao gồm sản phẩm và serials)";
    }
}

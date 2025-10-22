package Controller.WareHouse;

import DAL.ProductDetailSerialDAO;
import DAL.StockMovementDAO;
import DAL.StockMovementDetailDAO;
import Model.StockMovementDetail;
import Model.StockMovementsRequest;
import Model.User;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "NhapHangDetailController", urlPatterns = {"/NhapHangDetail"})
public class NhapHangDetailController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect("NhapHang");
            return;
        }

        int movementId = Integer.parseInt(idParam);
        StockMovementDAO dao = new StockMovementDAO();
        StockMovementsRequest movement = dao.getMovementById(movementId);

        StockMovementDetailDAO detailDAO = new StockMovementDetailDAO();
        List<String> productList = detailDAO.getProductListByMovement("default", movementId);

        // 🚫 Nếu đơn đã bị hủy => chặn truy cập
        if ("Đã hủy".equalsIgnoreCase(movement.getResponseStatus())) {
            response.sendRedirect("DanhSachNhapHang?error=cancelled");
            return;
        }

        // Lấy danh sách chi tiết
        List<StockMovementDetail> listDetails = dao.getMovementDetailsByMovementId(movementId);

        String productFilter = request.getParameter("productFilter");
        String status = request.getParameter("status");

        if (productFilter != null && !productFilter.isEmpty()) {
            listDetails.removeIf(d -> !d.getProductName().equalsIgnoreCase(productFilter));
        }
        if (status != null) {
            switch (status) {
                case "completed":
                    listDetails.removeIf(d -> d.getQuantityScanned() < d.getQuantity());
                    break;
                case "pending":
                    listDetails.removeIf(d -> d.getQuantityScanned() > 0);
                    break;
                case "processing":
                    listDetails.removeIf(d -> d.getQuantityScanned() == 0 || d.getQuantityScanned() >= d.getQuantity());
                    break;
            }
        }

        ProductDetailSerialDAO serialDAO = new ProductDetailSerialDAO();
        for (StockMovementDetail d : listDetails) {
            d.setSerials(serialDAO.getSerialsByMovementDetailId(d.getMovementDetailId()));
        }

        request.setAttribute("movement", movement);
        request.setAttribute("listDetails", listDetails);
        request.setAttribute("productList", productList);

        request.getRequestDispatcher("/WEB-INF/jsp/warehouse/NhapHangDetail.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        StockMovementDAO movementDAO = new StockMovementDAO();
        ProductDetailSerialDAO serialDAO = new ProductDetailSerialDAO();
        StockMovementDetailDAO detailDAO = new StockMovementDetailDAO();

        int movementId = Integer.parseInt(request.getParameter("movementId"));
        String action = request.getParameter("action");

        Integer userId = (Integer) request.getSession().getAttribute("userId");
        if (userId == null) {
            userId = 0; // hoặc bắt buộc login
        }
        if ("confirm".equals(action)) {
            var listDetails = movementDAO.getMovementDetailsByMovementId(movementId);
            boolean allDone = listDetails.stream()
                    .allMatch(d -> d.getQuantityScanned() != null && d.getQuantityScanned() >= d.getQuantity());

            if (!allDone) {
                request.setAttribute("error", "❌ Vẫn còn sản phẩm chưa nhập đủ serial, không thể hoàn tất!");
            } else {
                // cập nhật vào bảng Responses
                movementDAO.addOrUpdateResponse(movementId, "Hoàn thành", userId);
                request.setAttribute("success", "✅ Hoàn thành đơn nhập thành công!");
            }
        } else if ("cancel".equals(action)) {
            movementDAO.addOrUpdateResponse(movementId, "Đã hủy", userId);
            request.setAttribute("error", "Đơn hàng đã bị hủy!");
        } else {
            String serial = request.getParameter("serial");
            int movementDetailId = Integer.parseInt(request.getParameter("movementDetailId"));
            int productDetailId = Integer.parseInt(request.getParameter("productDetailId"));

            int current = movementDAO.getQuantityScanned(movementDetailId);
            int required = movementDAO.getQuantityRequired(movementDetailId);

            if (current >= required) {
                request.setAttribute("error", "⚠️ Số lượng serial đã đủ, không thể nhập thêm!");
            } else {
                boolean ok = serialDAO.insertSerial(productDetailId, movementDetailId, serial);
                if (!ok) {
                    request.setAttribute("error", "⚠️ Serial đã tồn tại hoặc có lỗi khi thêm!");
                } else {
                    movementDAO.updateQuantityScanned(movementDetailId);
                    // đánh dấu “Đang xử lý” trên bảng Responses
                    movementDAO.addOrUpdateResponse(movementId, "Đang xử lý", userId);
                    request.setAttribute("success", "✅ Đã thêm serial thành công!");
                }
            }
        }

        // nạp lại dữ liệu để hiển thị
        var movement = movementDAO.getMovementById(movementId);
        var listDetails = movementDAO.getMovementDetailsByMovementId(movementId);
        for (var d : listDetails) {
            d.setSerials(serialDAO.getSerialsByMovementDetailId(d.getMovementDetailId()));
        }
        var productList = detailDAO.getProductListByMovement("default", movementId);

        request.setAttribute("movement", movement);
        request.setAttribute("listDetails", listDetails);
        request.setAttribute("productList", productList);
        request.getRequestDispatcher("/WEB-INF/jsp/warehouse/NhapHangDetail.jsp").forward(request, response);
    }
}

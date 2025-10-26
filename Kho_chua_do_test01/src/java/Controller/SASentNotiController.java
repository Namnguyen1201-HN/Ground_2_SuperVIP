package Controller;

import DAL.AnnouncementDAO;
import Model.Announcement;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "SASentNotiController", urlPatterns = {"/SAThongBao"})
public class SASentNotiController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        Integer userId = (Integer) session.getAttribute("userID");
        Integer warehouseId = (Integer) session.getAttribute("BrandID");

        AnnouncementDAO dao = new AnnouncementDAO();

        // 🟢 Lấy thông báo đã gửi và nhận
        List<Announcement> sentList = dao.getSentAnnouncements(warehouseId, userId);
        List<Announcement> receivedList = dao.getReceivedAnnouncements(warehouseId);

        request.setAttribute("sentList", sentList);
        request.setAttribute("receivedList", receivedList);

        request.getRequestDispatcher("/WEB-INF/jsp/sale/SASentNoti.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        Integer userId = (Integer) session.getAttribute("userID");
        Integer branchId = (Integer) session.getAttribute("branchID");
        

        String title = request.getParameter("title");
        String content = request.getParameter("content");

        Announcement a = new Announcement();
        a.setFromUserId(userId);
        a.setFromBranchId(branchId);
        
        a.setToWarehouseId(null); // gửi toàn hệ thống hoặc bạn có thể chọn kho khác
        a.setTitle(title);
        a.setDescription(content);

        AnnouncementDAO dao = new AnnouncementDAO();
        boolean success = dao.insertAnnouncement(a);

        if (success) {
            request.setAttribute("msg", "✅ Gửi thông báo thành công!");
        } else {
            request.setAttribute("msg", "❌ Gửi thông báo thất bại!");
        }

        doGet(request, response);
    }
}

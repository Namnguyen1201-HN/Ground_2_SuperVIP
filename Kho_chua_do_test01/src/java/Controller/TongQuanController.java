package Controller;

import DAL.AnnouncementDAO;
import Model.Announcement;
import Model.AnnouncementDTO;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 *
 * @author TieuPham
 */
@WebServlet(name = "TongQuanController", urlPatterns = {"/TongQuan"})
public class TongQuanController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        AnnouncementDAO dao = new AnnouncementDAO();
        // lấy danh sách thông báo mới nhất
        List<Announcement> announcements = dao.getLatestAnnouncements(4);

        // Lấy danh sách hoạt động gần đây
        List<AnnouncementDTO> activityLogs = dao.getRecentActivities(4);

        // Gán vào request      
        request.setAttribute("announcements", announcements);
        request.setAttribute("activityLogs", activityLogs);
        
        request.getRequestDispatcher("/WEB-INF/jsp/admin/TongQuan.jsp")
                .forward(request, response);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

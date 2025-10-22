package Controller.Admin;

import DAL.ShiftDAO;
import Model.Shift;
import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ShiftUserController", urlPatterns = {"/ShiftUser"})
public class ShiftUserController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        ShiftDAO dao = new ShiftDAO();
        List<Shift> list = dao.getAll();

        // --- Tìm kiếm ---
        String search = request.getParameter("search");
        if (search != null && !search.trim().isEmpty()) {
            String keyword = search.trim().toLowerCase();
            list = list.stream()
                    .filter(s -> s.getShiftName().toLowerCase().contains(keyword))
                    .collect(Collectors.toList());
            request.setAttribute("search", search);
        }

        // --- Phân trang ---
        int pageSize = 5;
        int page = 1;
        String pageStr = request.getParameter("page");
        if (pageStr != null) {
            try {
                page = Integer.parseInt(pageStr);
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        int totalShift = list.size();
        int totalPages = (int) Math.ceil((double) totalShift / pageSize);
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, totalShift);
        List<Shift> listPage = list.subList(start, end);

        request.setAttribute("shiftList", listPage);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("startShift", totalShift == 0 ? 0 : start + 1);
        request.setAttribute("endShift", end);
        request.setAttribute("totalShift", totalShift);

        request.getRequestDispatcher("/WEB-INF/jsp/admin/ShiftUser.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String action = request.getParameter("action");
        ShiftDAO dao = new ShiftDAO();

        try {
            if (action == null) {
                response.sendRedirect("ShiftUser");
                return;
            }

            switch (action) {
                case "create": {
                    String name = request.getParameter("shiftName");
                    LocalTime start = LocalTime.parse(request.getParameter("startTime"));
                    LocalTime end = LocalTime.parse(request.getParameter("endTime"));

                    Shift s = new Shift();
                    s.setShiftName(name);
                    s.setStartTime(start);
                    s.setEndTime(end);
                    dao.insert(s);
                    break;
                }
                case "update": {
                    int id = Integer.parseInt(request.getParameter("shiftID"));
                    String name = request.getParameter("shiftName");
                    LocalTime start = LocalTime.parse(request.getParameter("startTime"));
                    LocalTime end = LocalTime.parse(request.getParameter("endTime"));

                    Shift s = new Shift();
                    s.setShiftID(id);
                    s.setShiftName(name);
                    s.setStartTime(start);
                    s.setEndTime(end);
                    dao.update(s);
                    break;
                }
                case "delete": {
                    int id = Integer.parseInt(request.getParameter("shiftID"));
                    dao.delete(id);
                    break;
                }
            }

            // Sau khi thao tác xong, load lại trang chính
            response.sendRedirect("ShiftUser");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}

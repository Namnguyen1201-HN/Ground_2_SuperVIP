package Controller.WareHouse;

import DAL.UserDAO;
import Model.User;
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
        Integer userId = (Integer) session.getAttribute("userID");

        if (userId == null) {
            response.sendRedirect("Login");
            return;
        }

        UserDAO dao = new UserDAO();
        User user = dao.getUserFullById(userId);
        request.setAttribute("user", user);
        request.getRequestDispatcher("/WEB-INF/jsp/warehouse/Information.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        Integer userId = (Integer) session.getAttribute("userID");

        if (userId == null) {
            response.sendRedirect("Login");
            return;
        }

        try {
            UserDAO dao = new UserDAO();
            User u = dao.getUserFullById(userId);

            u.setFullName(request.getParameter("fullName"));
            u.setEmail(request.getParameter("email"));
            u.setPhone(request.getParameter("phone"));
            u.setAddress(request.getParameter("address"));
            u.setIdentificationId(request.getParameter("identificationId"));

            String genderStr = request.getParameter("gender");
            if (genderStr != null) {
                u.setGender("1".equals(genderStr));
            }

            String dobStr = request.getParameter("dob");
            if (dobStr != null && !dobStr.isEmpty()) {
                Date dob = new SimpleDateFormat("yyyy-MM-dd").parse(dobStr);
                u.setDob(dob);
            }

            boolean success = dao.updateUser(u);

            if (success) {
                request.setAttribute("msg", "✅ Cập nhật thông tin thành công!");
            } else {
                request.setAttribute("msg", "❌ Cập nhật thất bại!");
            }

            request.setAttribute("user", dao.getUserFullById(userId));
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("msg", "❌ Lỗi: " + e.getMessage());
        }

        request.getRequestDispatcher("/WEB-INF/jsp/warehouse/Information.jsp").forward(request, response);
    }
}

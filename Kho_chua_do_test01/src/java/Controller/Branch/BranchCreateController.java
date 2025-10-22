package Controller.Branch;

import Controller.*;
import DAL.BranchDAO;
import Model.Branch;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "BranchCreateController", urlPatterns = {"/BranchCreate"})
public class BranchCreateController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/admin/branch_create.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String name = request.getParameter("branchName");
        String address = request.getParameter("address");
        String phone = request.getParameter("phone");

        // Validation cơ bản
        if (name == null || name.trim().isEmpty()
                || address == null || address.trim().isEmpty()
                || phone == null || phone.trim().isEmpty()) {
            request.setAttribute("message", "Vui lòng nhập đầy đủ thông tin!");
            request.setAttribute("msgType", "danger");
            request.getRequestDispatcher("/WEB-INF/jsp/admin/branch_create.jsp").forward(request, response);
            return;
        }

        Branch b = new Branch();
        b.setBranchName(name.trim());
        b.setAddress(address.trim());
        b.setPhone(phone.trim());
        b.setActive(true);

        BranchDAO dao = new BranchDAO();
        boolean success = dao.insertBranch(b);

        if (success) {
            // ✅ Lưu thành công → quay lại trang quản lý
            request.getSession().setAttribute("flashMessage", "Tạo chi nhánh mới thành công!");
            request.getSession().setAttribute("flashType", "success");
            response.sendRedirect("BranchManagement");
        } else {
            // ❌ Lỗi → hiển thị lại form
            request.setAttribute("message", "Không thể tạo chi nhánh. Vui lòng thử lại!");
            request.setAttribute("msgType", "danger");
            request.getRequestDispatcher("/WEB-INF/jsp/admin/branch_create.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Tạo chi nhánh mới";
    }
}

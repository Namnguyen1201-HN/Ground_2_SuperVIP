package Controller.Admin;

import DAL.BranchDAO;
import Model.Branch;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "BranchManagementController", urlPatterns = {"/BranchManagement"})
public class BranchManagementController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        BranchDAO dao = new BranchDAO();
        List<Branch> branches = dao.getAllBranches();
        request.setAttribute("branches", branches);

        request.getRequestDispatcher("/WEB-INF/jsp/admin/branch_management.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        BranchDAO dao = new BranchDAO();

        String action = request.getParameter("action");
        if ("delete".equals(action)) {
            int id = Integer.parseInt(request.getParameter("branchId"));
            boolean success = dao.deleteBranch(id);
            request.setAttribute("message", success ? "Xóa chi nhánh thành công!" : "Không thể xóa chi nhánh!");
            request.setAttribute("msgType", success ? "success" : "danger");
        } else if ("update".equals(action)) {
            int id = Integer.parseInt(request.getParameter("branchId"));
            String name = request.getParameter("branchName");
            String address = request.getParameter("address");
            String phone = request.getParameter("phone");
            boolean isActive = request.getParameter("isActive") != null;

            Branch b = new Branch(id, name, address, phone, isActive);
            boolean success = dao.updateBranch(b);
            request.setAttribute("message", success ? "Cập nhật chi nhánh thành công!" : "Cập nhật thất bại!");
            request.setAttribute("msgType", success ? "success" : "danger");
        }

        // reload list
        List<Branch> branches = dao.getAllBranches();
        request.setAttribute("branches", branches);
        request.getRequestDispatcher("/WEB-INF/jsp/admin/branch_management.jsp").forward(request, response);
    }
}

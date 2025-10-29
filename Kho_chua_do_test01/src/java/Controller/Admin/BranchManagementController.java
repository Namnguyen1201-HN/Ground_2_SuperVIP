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

        try {
            if ("delete".equals(action)) {
                int id = Integer.parseInt(request.getParameter("branchId"));
                boolean ok = dao.deleteBranch(id);
                response.sendRedirect("BranchManagement?" + (ok ? "success=delete" : "error=delete_failed"));
            } else if ("update".equals(action)) {
                int id = Integer.parseInt(request.getParameter("branchId"));
                String name = request.getParameter("branchName");
                String address = request.getParameter("address");
                String phone = request.getParameter("phone");
                boolean active = request.getParameter("isActive") != null;

                if (name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
                    response.sendRedirect("BranchManagement?error=empty_fields");
                    return;
                }

                if (!phone.matches("^0\\d{8,10}$")) {
                    response.sendRedirect("BranchManagement?error=invalid_phone");
                    return;
                }

                Branch current = dao.getBranchById(id);
                if (current != null && !current.getPhone().equals(phone) && dao.isPhoneExists(phone)) {
                    response.sendRedirect("BranchManagement?error=duplicate_phone");
                    return;
                }

                Branch b = new Branch();
                b.setBranchId(id);
                b.setBranchName(name);
                b.setAddress(address);
                b.setPhone(phone);
                b.setActive(active);

                boolean ok = dao.updateBranch(b);
                response.sendRedirect("BranchManagement?" + (ok ? "success=update" : "error=update_failed"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("BranchManagement?error=exception");
        }
    }
}

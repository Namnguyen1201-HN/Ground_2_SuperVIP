package Controller.Admin;

import DAL.BranchDAO;
import DAL.CashFlowDAO;
import DAL.OrderDAO;
import DAL.UserDAO;
import Model.Branch;
import Model.CashFlowReportDTO;
import Model.RevenueReportDTO;
import Model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "ReportController", urlPatterns = {"/Report"})
public class ReportController extends HttpServlet {

    private CashFlowDAO cashFlowDAO;
    private OrderDAO orderDAO;
    private BranchDAO branchDAO;
    private UserDAO userDAO;

    @Override
    public void init() {
        cashFlowDAO = new CashFlowDAO();
        orderDAO = new OrderDAO();
        branchDAO = new BranchDAO();
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect("Login");
            return;
        }

        String action = request.getParameter("action");
        if ("loadEmployees".equals(action)) {
            handleLoadEmployees(request, response);
            return;
        }

        String reportType = request.getParameter("type");
        if (reportType == null || reportType.trim().isEmpty()) {
            reportType = "revenue"; // Default to revenue
        }

        // Load branches for filter
        List<Branch> branches = branchDAO.getAllBranches();
        request.setAttribute("branches", branches);

        // Parse filter parameters
        String fromDateStr = request.getParameter("fromDate");
        String toDateStr = request.getParameter("toDate");
        String branchIdParam = request.getParameter("branchId");
        String employeeIdParam = request.getParameter("employeeId");
        String paymentMethod = request.getParameter("paymentMethod");

        Date fromDate = null;
        Date toDate = null;
        Integer branchId = null;
        String employeeName = null;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        try {
            if (fromDateStr != null && !fromDateStr.trim().isEmpty()) {
                fromDate = sdf.parse(fromDateStr);
            }
            if (toDateStr != null && !toDateStr.trim().isEmpty()) {
                toDate = sdf.parse(toDateStr);
            }
        } catch (ParseException e) {
            // Invalid date format, ignore
        }

        try {
            if (branchIdParam != null && !branchIdParam.trim().isEmpty()) {
                branchId = Integer.parseInt(branchIdParam);
            }
        } catch (NumberFormatException e) {
            // Invalid branch ID, ignore
        }

        // Get employee ID if provided
        Integer employeeId = null;
        if (employeeIdParam != null && !employeeIdParam.trim().isEmpty()) {
            try {
                employeeId = Integer.parseInt(employeeIdParam);
                User employee = userDAO.getUserById(employeeId);
                if (employee != null) {
                    employeeName = employee.getFullName();
                }
            } catch (NumberFormatException e) {
                // Invalid employee ID, ignore
            }
        }

        // Load employees for selected branch
        List<User> employees = new ArrayList<>();
        if (branchId != null && branchId > 0) {
            employees = cashFlowDAO.getEmployeesByBranch(branchId);
        } else {
            // If no branch selected, load all active employees
            employees = userDAO.getAllUsers().stream()
                    .filter(u -> u.getIsActive() == 1)
                    .collect(Collectors.toList());
        }
        request.setAttribute("employees", employees);

        // Handle different report types
        if ("expense".equals(reportType)) {
            // Get expense reports from CashFlows
            List<CashFlowReportDTO> reports = cashFlowDAO.getExpenseReports(fromDate, toDate, branchId, employeeName, paymentMethod);
            BigDecimal totalAmount = cashFlowDAO.getTotalExpense(fromDate, toDate, branchId, employeeName, paymentMethod);
            
            request.setAttribute("reports", reports);
            request.setAttribute("totalAmount", totalAmount);
            request.setAttribute("reportType", "expense");
        } else {
            // Get revenue reports from Orders
            List<RevenueReportDTO> revenueReports = orderDAO.getRevenueReports(fromDate, toDate, branchId, employeeId, paymentMethod);
            BigDecimal totalAmount = orderDAO.getTotalRevenue(fromDate, toDate, branchId, employeeId, paymentMethod);
            
            request.setAttribute("revenueReports", revenueReports);
            request.setAttribute("reports", revenueReports); // For backward compatibility
            request.setAttribute("totalAmount", totalAmount);
            request.setAttribute("reportType", "revenue");
        }

        // Keep filter values for form
        request.setAttribute("fromDate", fromDateStr);
        request.setAttribute("toDate", toDateStr);
        request.setAttribute("selectedBranchId", branchIdParam);
        request.setAttribute("selectedEmployeeId", employeeIdParam);
        request.setAttribute("selectedPaymentMethod", paymentMethod != null ? paymentMethod : "all");

        request.getRequestDispatcher("/WEB-INF/jsp/admin/report.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Handle filter form submission
        doGet(request, response);
    }
    
    private void handleLoadEmployees(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String branchIdParam = request.getParameter("branchId");
        List<User> employees = new ArrayList<>();
        
        if (branchIdParam != null && !branchIdParam.trim().isEmpty()) {
            try {
                int branchId = Integer.parseInt(branchIdParam);
                employees = cashFlowDAO.getEmployeesByBranch(branchId);
            } catch (NumberFormatException e) {
                // Invalid branch ID
            }
        }
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print("[");
        boolean first = true;
        for (User emp : employees) {
            if (!first) out.print(",");
            String fullName = emp.getFullName() != null ? emp.getFullName().replace("\"", "\\\"").replace("\n", "\\n") : "";
            out.print("{\"id\":" + emp.getUserId() + ",\"name\":\"" + fullName + "\"}");
            first = false;
        }
        out.print("]");
        out.flush();
    }
}

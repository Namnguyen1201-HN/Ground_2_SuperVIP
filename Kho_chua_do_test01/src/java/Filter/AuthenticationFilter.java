package Filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;

@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {"/*"})
public class AuthenticationFilter implements Filter {

    // 🔒 Bảng quyền truy cập cho từng role
    private final Map<Integer, List<String>> roleAccessMap = new HashMap<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // ✅ Role 0 – Admin / System Manager
        roleAccessMap.put(0, Arrays.asList(
                "/TongQuan", "/NhanVien", "/AddUser", "/BranchManagement", "/ChangePassWord", "/Customer", "/EditUser", "/ShiftUser",
                "/WarehouseManagement", "/InformationAccount", "/Logout", "/product", "/BranchCreate", "/InventoryMoves", "/Orders",
                "/Supplier", "/WareHouseCreate", "/Promotion", "/Report",
                "/warehouse-export-orders", "/serial-check", "/wh-import-export-detail", "/wh-complete-request"
        ));

        // ✅ Role 1 – Branch Manager
        roleAccessMap.put(1, Arrays.asList(
                "/TongQuan", "/Orders", "/Customer", "/Supplier", "/Promotion", "/StaffManagement", "/Report",
                "/InformationAccountBM", "/ChangePassWord", "/Logout", "/import-request", "/stock-movements",
                "/warehouse-export-orders", "/serial-check", "/wh-import-export-detail", "/wh-complete-request"
        ));

        // ✅ Role 2 – Sale
        roleAccessMap.put(2, Arrays.asList(
                "/sale", "/", "/", "/", "/Logout",
                "/warehouse-export-orders", "/serial-check", "/wh-import-export-detail", "/wh-complete-request"
        ));

        // ✅ Role 3 – Warehouse Manager
        roleAccessMap.put(3, Arrays.asList(
                "/WareHouseProduct", "/NhapHang", "/XuatHang", "/ThongBao", "/Information", "/Logout",
                "/warehouse-export-orders", "/serial-check", "/wh-import-export-detail", "/wh-complete-request", "/wh-import"
        ));
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());

        // ✅ Cho phép truy cập công khai (public pages + static files)
        if (isPublicURL(path)) {
            chain.doFilter(request, response);
            return;
        }

        // ✅ Kiểm tra đăng nhập
        if (session == null || session.getAttribute("currentUser") == null) {
            httpResponse.sendRedirect(contextPath + "/Login?error=needLogin");
            return;
        }

        // ✅ Lấy roleID của người dùng hiện tại
        Integer roleID = (Integer) session.getAttribute("roleID");
        if (roleID == null) {
            httpResponse.sendRedirect(contextPath + "/Login");
            return;
        }

        // ✅ Kiểm tra quyền truy cập của role
        if (hasAccess(roleID, path)) {
            chain.doFilter(request, response);
        } else {
            httpResponse.sendRedirect(contextPath + "/AccessDenied.jsp");
        }
    }

    // 🟢 Các đường dẫn public (ai cũng vào được)
    private boolean isPublicURL(String path) {
        // Các trang public
        String[] publicPaths = {
            "/Login", "/Register", "/ForgotPassword", "/Dashboard", "/", "/", ""
        };
        for (String p : publicPaths) {
            if (path.equals(p) || path.startsWith(p)) {
                return true;
            }
        }

        // File tĩnh
        String[] staticExts = {
            ".css", ".js", ".png", ".jpg", ".jpeg", ".gif", ".ico", ".woff", ".woff2", ".ttf", ".eot", ".svg"
        };
        for (String ext : staticExts) {
            if (path.endsWith(ext)) {
                return true;
            }
        }

        // Thư mục tĩnh
        String[] publicFolders = {"/css/", "/js/", "/images/", "/fonts/", "/assets/", "/lib/"};
        for (String folder : publicFolders) {
            if (path.startsWith(folder)) {
                return true;
            }
        }

        return false;
    }

    // 🔐 Kiểm tra xem role có quyền truy cập URL không
    private boolean hasAccess(int roleID, String path) {
        List<String> allowed = roleAccessMap.get(roleID);
        if (allowed == null) {
            return false;
        }

        for (String allow : allowed) {
            if (path.startsWith(allow)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void destroy() {
    }
}

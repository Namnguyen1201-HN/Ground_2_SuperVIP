/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {"/*"})
public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());

        // Allow public URLs (CSS, JS, images, fonts)
        if (isPublicURL(path)) {
            chain.doFilter(request, response);
            return;
        }

        // Allow login, register, forgot password pages
        if (path.equals("/Login") || path.equals("/Register") || path.equals("/ForgotPassword") || 
            path.isEmpty() || path.equals("/") || path.equals("")) {
            chain.doFilter(request, response);
            return;
        }

        // Check if user is logged in
        if (session == null || session.getAttribute("currentUser") == null) {
            // User chưa login, redirect về Login
            httpResponse.sendRedirect(contextPath + "/Login");
            return;
        }

        // User đã login, cho phép truy cập TẤT CẢ trang (không block)
        chain.doFilter(request, response);
    }

    /**
     * Check if URL is public (CSS, JS, images, fonts)
     */
    private boolean isPublicURL(String path) {
        if (path.isEmpty() || path.equals("/")) {
            return true;
        }
        
        // Check file extensions
        String[] publicExtensions = {".css", ".js", ".png", ".jpg", ".jpeg", ".gif", ".ico", ".woff", ".woff2", ".ttf", ".eot", ".svg"};
        for (String ext : publicExtensions) {
            if (path.endsWith(ext)) {
                return true;
            }
        }
        
        // Check folders
        String[] publicFolders = {"/css/", "/js/", "/images/", "/fonts/", "/assets/", "/lib/"};
        for (String folder : publicFolders) {
            if (path.startsWith(folder)) {
                return true;
            }
        }
        
        return false;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}

package Controller.WareHouse;

import DAL.StockMovementDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@WebServlet(name = "NhapHangController", urlPatterns = {"/NhapHang"})
public class NhapHangController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");       

        request.getRequestDispatcher("/WEB-INF/jsp/warehouse/NhapHang.jsp").forward(request, response);
    }
    
}

package Controller.WareHouse;

import DAL.ProductDetailSerialDAO;
import DAL.StockMovementDAO;
import DAL.StockMovementDetailDAO;
import Model.StockMovementDetail;
import Model.StockMovementsRequest;
import Model.User;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "NhapHangDetailController", urlPatterns = {"/NhapHangDetail"})
public class NhapHangDetailController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {       

        request.getRequestDispatcher("/WEB-INF/jsp/warehouse/NhapHangDetail.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
       
        request.getRequestDispatcher("/WEB-INF/jsp/warehouse/NhapHangDetail.jsp").forward(request, response);
    }
}

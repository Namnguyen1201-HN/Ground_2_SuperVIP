package Controller;

import DAL.BranchDAO;
import DAL.StockMovementDAO;
import Model.StockMovementDetail;
import Model.StockMovementsRequest;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@WebServlet(name = "InventoryMovesController", urlPatterns = {"/InventoryMoves"})
public class InventoryMovesController extends HttpServlet {
    private StockMovementDAO moveDAO;
    private BranchDAO branchDAO;

    @Override public void init(){ moveDAO = new StockMovementDAO(); branchDAO = new BranchDAO(); }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action"); if (action == null) action = "list";
        switch (action) {
            case "productInfo":
                resp.setContentType("application/json;charset=UTF-8");
                try {
                    String idParam = req.getParameter("id");
                    DAL.ProductDetailDAO pdd = new DAL.ProductDetailDAO();
                    DAL.ProductDetailDAO.DetailInfo info;
                    try {
                        int pid = Integer.parseInt(idParam);
                        java.util.Set<Integer> ids = new java.util.HashSet<>(); ids.add(pid);
                        java.util.Map<Integer, DAL.ProductDetailDAO.DetailInfo> infos = pdd.getInfoByIds(ids);
                        info = infos.get(pid);
                    } catch (NumberFormatException ex) {
                        info = pdd.getInfoByCode(idParam);
                    }
                    if (info == null) { resp.getWriter().write("{}"); return; }
                    String safeName = info.getName()==null?"":info.getName().replace("\"","\\\"");
                    String safeSku = info.getSku()==null?"":info.getSku().replace("\"","\\\"");
                    String safeImg = info.getImageUrl()==null?"":info.getImageUrl().replace("\"","\\\"");
                    resp.getWriter().write("{\"name\":\""+safeName+"\",\"sku\":\""+safeSku+"\",\"image\":\""+safeImg+"\"}");
                } catch (Exception e) { resp.getWriter().write("{}"); }
                return;
            case "create":
                req.setAttribute("branches", branchDAO.getAllBranches());
                req.getRequestDispatcher("/WEB-INF/jsp/admin/inventory_move_form.jsp").forward(req, resp);
                return;
            case "detail":
                try {
                    int id = Integer.parseInt(req.getParameter("id"));
                    Model.StockMovementsRequest mv = moveDAO.getById(id);
                    // Resolve product info for display names
                    java.util.Set<Integer> pdIds = new java.util.HashSet<>();
                    if (mv != null && mv.getDetails() != null) {
                        for (Model.StockMovementDetail d : mv.getDetails()) pdIds.add(d.getProductDetailId());
                    }
                    DAL.ProductDetailDAO pdd = new DAL.ProductDetailDAO();
                    java.util.Map<Integer, DAL.ProductDetailDAO.DetailInfo> pdInfos = pdd.getInfoByIds(pdIds);
                    req.setAttribute("pdInfos", pdInfos);
                    req.setAttribute("move", mv);
                    req.getRequestDispatcher("/WEB-INF/jsp/admin/inventory_move_detail.jsp").forward(req, resp);
                    return;
                } catch (Exception ignored) {}
            default:
                int page = 1; try { page = Integer.parseInt(req.getParameter("page")); } catch (Exception ignored) {}
                int pageSize = 10;
                String type = req.getParameter("type");
                java.sql.Timestamp from = null, to = null;
                try { String fd = req.getParameter("from"); if (fd!=null && !fd.isBlank()) from = java.sql.Timestamp.valueOf(fd + " 00:00:00"); } catch(Exception ignored) {}
                try { String td = req.getParameter("to"); if (td!=null && !td.isBlank()) to = java.sql.Timestamp.valueOf(td + " 23:59:59"); } catch(Exception ignored) {}
                StockMovementDAO.PagedMoves pm = moveDAO.list(type, null, null, from, to, page, pageSize);
                req.setAttribute("items", pm.items);
                req.setAttribute("total", pm.total);
                req.setAttribute("page", pm.page);
                req.setAttribute("pageSize", pm.pageSize);
                req.setAttribute("type", type);
                req.setAttribute("from", req.getParameter("from"));
                req.setAttribute("to", req.getParameter("to"));
                req.getRequestDispatcher("/WEB-INF/jsp/admin/inventory_moves.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("delete".equals(action)) {
            try { int id = Integer.parseInt(req.getParameter("id")); moveDAO.delete(id); } catch (Exception ignored) {}
            resp.sendRedirect("InventoryMoves");
            return;
        }
        if ("update".equals(action)) {
            try {
                int id = Integer.parseInt(req.getParameter("id"));
                String note = req.getParameter("note");
                // Do not allow changing type - keep original value
                StockMovementsRequest current = moveDAO.getById(id);
                String type = current != null ? current.getMovementType() : null;
                moveDAO.updateHeader(id, type, note);
                resp.sendRedirect("InventoryMoves?action=detail&id=" + id);
                return;
            } catch (Exception ignored) {}
        }
        if ("create".equals(action)) {
            try {
                StockMovementsRequest r = new StockMovementsRequest();
                r.setMovementType(req.getParameter("movementType"));
                String fb = req.getParameter("fromBranchId"); if (fb!=null && !fb.isBlank()) r.setFromBranchId(Integer.parseInt(fb));
                String fw = req.getParameter("fromWarehouseId"); if (fw!=null && !fw.isBlank()) r.setFromWarehouseId(Integer.parseInt(fw));
                String fs = req.getParameter("fromSupplierId"); if (fs!=null && !fs.isBlank()) r.setFromSupplierId(Integer.parseInt(fs));
                String tb = req.getParameter("toBranchId"); if (tb!=null && !tb.isBlank()) r.setToBranchId(Integer.parseInt(tb));
                String tw = req.getParameter("toWarehouseId"); if (tw!=null && !tw.isBlank()) r.setToWarehouseId(Integer.parseInt(tw));
                // Use session user if available; fallback to seeded user id 1
                Integer creatorId = null;
                try {
                    jakarta.servlet.http.HttpSession s = req.getSession(false);
                    if (s != null && s.getAttribute("currentUser") != null) {
                        Model.User u = (Model.User) s.getAttribute("currentUser");
                        creatorId = u.getUserId();
                    }
                } catch (Exception ignored) {}
                r.setCreatedBy(creatorId != null ? creatorId : 1);
                r.setNote(req.getParameter("note"));

                String[] pids = req.getParameterValues("productDetailId");
                String[] qtys = req.getParameterValues("quantity");
                if (pids != null && pids.length > 0) {
                    r.setDetails(new ArrayList<>());
                    for (int i=0;i<pids.length;i++) {
                        try {
                            StockMovementDetail d = new StockMovementDetail();
                            d.setProductDetailId(Integer.parseInt(pids[i]));
                            d.setQuantity(Integer.parseInt(qtys[i]));
                            r.getDetails().add(d);
                        } catch (Exception ignored) {}
                    }
                }
                int id = moveDAO.create(r);
                resp.sendRedirect("InventoryMoves?action=detail&id=" + id);
                return;
            } catch (Exception e) {
                throw new ServletException(e);
            }
        }
        resp.sendRedirect("InventoryMoves");
    }
}



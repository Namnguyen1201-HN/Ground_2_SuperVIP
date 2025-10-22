<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Chi tiết đơn nhập hàng</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <style>
            body {
                background-color: #f8f9fa;
            }
            .filter-box {
                background-color: #fff;
                border-radius: 8px;
                padding: 20px;
                box-shadow: 0 0 5px rgba(0,0,0,0.1);
            }
            .table-container {
                background-color: #fff;
                border-radius: 8px;
                padding: 20px;
                box-shadow: 0 0 5px rgba(0,0,0,0.1);
            }
            .btn-scan {
                background-color: #ff9800;
                color: #fff;
            }
            .btn-scan:hover {
                background-color: #e68900;
            }
        </style>
    </head>

    <body>
        <%@ include file="../warehouse/header-warehouse.jsp" %>

        <div class="container-fluid mt-4">
            <div class="row d-flex justify-content-center">

                <!-- Nội dung chính -->
                <div class="col-md-9">
                    <div class="row">
                        <!-- Bộ lọc -->
                        <div class="col-md-3">
                            <div class="filter-box">
                                <h6 class="mb-3 fw-bold">Bộ lọc</h6>
                                <form method="get" action="NhapHangDetail">
                                    <input type="hidden" name="id" value="${movement.movementId}">
                                    <div class="mb-3">
                                        <label class="form-label">Sản phẩm:</label>
                                        <select class="form-select" name="productFilter">
                                            <option value="">--Tất cả sản phẩm--</option>
                                            <c:forEach var="p" items="${productList}">
                                                <option value="${p}" <c:if test="${param.productFilter == p}">selected</c:if>>${p}</option>
                                            </c:forEach>
                                        </select>
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label">Trạng thái:</label>
                                        <div>
                                            <div class="form-check">
                                                <input class="form-check-input" type="radio" name="status" value="all" checked>
                                                <label class="form-check-label">Tất cả</label>
                                            </div>
                                            <div class="form-check">
                                                <input class="form-check-input" type="radio" name="status" value="completed">
                                                <label class="form-check-label">Hoàn thành</label>
                                            </div>
                                            <div class="form-check">
                                                <input class="form-check-input" type="radio" name="status" value="pending">
                                                <label class="form-check-label">Chờ xử lý</label>
                                            </div>
                                            <div class="form-check">
                                                <input class="form-check-input" type="radio" name="status" value="processing">
                                                <label class="form-check-label">Đang xử lý</label>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="d-flex justify-content-between">
                                        <button type="submit" class="btn btn-primary w-50 me-1">Áp dụng lọc</button>
                                        <button type="reset" class="btn btn-secondary w-50">Reset</button>
                                    </div>
                                </form>
                            </div>
                        </div>

                        <!-- Chi tiết đơn nhập hàng -->
                        <div class="col-md-9">
                            <div class="table-container">
                                <div class="d-flex justify-content-between align-items-center mb-3">
                                    <h5 class="fw-bold">Chi tiết đơn nhập hàng #${movement.movementId}</h5>
                                    <div>
                                        <c:if test="${movement.responseStatus != 'Hoàn thành' && movement.responseStatus != 'Đã hủy'}">
                                            <form method="post" action="NhapHangDetail" style="display:inline;">
                                                <input type="hidden" name="action" value="confirm">
                                                <input type="hidden" name="movementId" value="${movement.movementId}">
                                                <button class="btn btn-success">Hoàn tất nhập</button>
                                            </form>
                                        </c:if>   
                                        <a href="NhapHang" class="btn btn-outline-secondary">Quay lại</a>
                                    </div>
                                </div>

                                <c:if test="${movement.responseStatus == 'Đang xử lý'}">
                                    <div class="alert alert-info">Trạng thái: <strong>Đang xử lý</strong></div>
                                </c:if>
                                <c:if test="${movement.responseStatus == 'Hoàn thành'}">
                                    <div class="alert alert-success">Trạng thái: <strong>Hoàn thành</strong></div>
                                </c:if>
                                <c:if test="${movement.responseStatus == 'Chờ xử lý'}">
                                    <div class="alert alert-warning">Trạng thái: <strong>Chờ xử lý</strong></div>
                                </c:if>

                                <!-- Thông báo lỗi -->
                                <c:if test="${not empty success}">
                                    <div class="alert alert-success text-center"><i class="fa-solid me-1"></i>${success}</div>
                                    </c:if>
                                    <c:if test="${not empty error}">
                                    <div class="alert alert-danger text-center"><i class="fa-solid fa-triangle-exclamation me-1"></i>${error}</div>
                                    </c:if>

                                <table class="table table-bordered align-middle">
                                    <thead class="table-light">
                                        <tr>
                                            <th>STT</th>
                                            <th>Tên sản phẩm</th>
                                            <th>Serial number</th>
                                            <th>Số lượng cần nhập</th>
                                            <th>Đã nhập</th>
                                            <th>Thao tác</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="d" items="${listDetails}" varStatus="st">
                                            <tr>
                                                <td>${st.index + 1}</td>
                                                <td>${d.productName}<br><small>(${d.productCode})</small></td>

                                                <!-- Hiển thị serial -->
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${not empty d.serials}">
                                                            <c:forEach var="s" items="${d.serials}">
                                                                <span class="badge bg-secondary">${s.serialNumber}</span>
                                                            </c:forEach>
                                                        </c:when>
                                                        <c:otherwise>
                                                            Chưa có serial nào
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>

                                                <td>${d.quantity}</td>
                                                <td>${d.quantityScanned}/${d.quantity}</td>
                                                <td>
                                                    <button class="btn btn-scan btn-sm"
                                                            ${d.quantityScanned >= d.quantity ? "disabled" : ""}
                                                            onclick="openSerialModal(${movement.movementId}, ${d.movementDetailId}, ${d.productDetailId})">
                                                        Quét
                                                    </button>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>

                                <div class="d-flex justify-content-between">
                                    <p>Hiển thị ${listDetails.size()} sản phẩm</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal nhập serial -->
        <div class="modal fade" id="serialModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form action="NhapHangDetail" method="post">
                        <div class="modal-header">
                            <h5 class="modal-title">Nhập Serial Sản phẩm</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="movementId" id="movementId">
                            <input type="hidden" name="movementDetailId" id="movementDetailId">
                            <input type="hidden" name="productDetailId" id="productDetailId">

                            <label for="serialInput" class="form-label">Serial Number:</label>
                            <input type="text" class="form-control mb-3" id="serialInput" name="serial"
                                   placeholder="Nhập serial hoặc quét QR..." required>

                            <div class="d-flex justify-content-center gap-2">
                                <button type="button" class="btn btn-primary">
                                    <i class="fa-solid fa-qrcode me-1"></i> Quét QR Code
                                </button>
                                <button type="submit" class="btn btn-success">
                                    <i class="fa-solid fa-plus me-1"></i> Thêm Serial
                                </button>
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                                    <i class="fa-solid fa-xmark me-1"></i> Hủy
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- JS: mở modal và truyền ID -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
                                                                function openSerialModal(movementId, movementDetailId, productDetailId) {
                                                                    document.getElementById("movementId").value = movementId;
                                                                    document.getElementById("movementDetailId").value = movementDetailId;
                                                                    document.getElementById("productDetailId").value = productDetailId;
                                                                    var modal = new bootstrap.Modal(document.getElementById('serialModal'));
                                                                    modal.show();
                                                                }
        </script>

    </body>
</html>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết phiếu #${movement.movementId}</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }

        .container {
            max-width: 1200px;
            margin: 0 auto;
            background: white;
            border-radius: 15px;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
            overflow: hidden;
        }

        .header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 15px;
        }

        .header h1 {
            font-size: 26px;
            font-weight: 600;
        }

        .btn-back {
            padding: 10px 20px;
            background: rgba(255, 255, 255, 0.2);
            color: white;
            text-decoration: none;
            border-radius: 8px;
            font-weight: 500;
            border: 1px solid rgba(255, 255, 255, 0.3);
            transition: all 0.3s ease;
        }

        .btn-back:hover {
            background: rgba(255, 255, 255, 0.3);
            transform: translateY(-2px);
        }

        .content {
            padding: 30px;
        }

        .info-section {
            background: #f8f9fa;
            border-radius: 10px;
            padding: 25px;
            margin-bottom: 30px;
        }

        .info-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin-top: 15px;
        }

        .info-item {
            background: white;
            padding: 15px;
            border-radius: 8px;
            border-left: 4px solid #667eea;
        }

        .info-label {
            font-size: 12px;
            color: #6c757d;
            margin-bottom: 5px;
            text-transform: uppercase;
            font-weight: 600;
        }

        .info-value {
            font-size: 16px;
            color: #333;
            font-weight: 500;
        }

        .section-title {
            font-size: 20px;
            color: #333;
            margin-bottom: 20px;
            padding-bottom: 10px;
            border-bottom: 2px solid #667eea;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .badge {
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
            display: inline-block;
        }

        .badge-import {
            background: #d4edda;
            color: #155724;
        }

        .badge-export {
            background: #cce5ff;
            color: #004085;
        }

        .details-table {
            width: 100%;
            border-collapse: collapse;
            background: white;
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }

        .details-table thead {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }

        .details-table th {
            padding: 15px;
            text-align: left;
            font-weight: 600;
        }

        .details-table td {
            padding: 15px;
            border-bottom: 1px solid #dee2e6;
        }

        .details-table tbody tr:hover {
            background: #f8f9fa;
        }

        .details-table tbody tr:last-child td {
            border-bottom: none;
        }

        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #6c757d;
            font-size: 16px;
        }

        .summary-box {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px;
            border-radius: 10px;
            margin-top: 20px;
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
        }

        .summary-item {
            text-align: center;
        }

        .summary-value {
            font-size: 32px;
            font-weight: 700;
            margin-bottom: 5px;
        }

        .summary-label {
            font-size: 14px;
            opacity: 0.9;
        }

        .route-display {
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 20px;
            background: white;
            padding: 25px;
            border-radius: 10px;
            margin: 20px 0;
            font-size: 16px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }

        .route-point {
            padding: 15px 25px;
            background: #667eea;
            color: white;
            border-radius: 10px;
            font-weight: 600;
        }

        .route-arrow {
            font-size: 24px;
            color: #667eea;
        }

        @media (max-width: 768px) {
            .header {
                flex-direction: column;
                text-align: center;
            }

            .info-grid {
                grid-template-columns: 1fr;
            }

            .details-table {
                font-size: 13px;
            }

            .details-table th,
            .details-table td {
                padding: 10px 8px;
            }

            .route-display {
                flex-direction: column;
                gap: 10px;
            }

            .route-arrow {
                transform: rotate(90deg);
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>📋 Chi tiết phiếu #${movement.movementId}</h1>
            <a href="${pageContext.request.contextPath}/stock-movements" class="btn-back">
                ⬅️ Quay lại
            </a>
        </div>

        <div class="content">
            <!-- Movement Info -->
            <div class="info-section">
                <h2 class="section-title">
                    📦 Thông tin phiếu
                    <c:choose>
                        <c:when test="${movement.movementType eq 'Import'}">
                            <span class="badge badge-import">📥 Nhập kho</span>
                        </c:when>
                        <c:otherwise>
                            <span class="badge badge-export">📤 Xuất kho</span>
                        </c:otherwise>
                    </c:choose>
                </h2>

                <!-- Route Display -->
                <div class="route-display">
                    <div class="route-point">
                        <c:choose>
                            <c:when test="${not empty movement.fromSupplierName}">
                                🏭 ${movement.fromSupplierName}
                            </c:when>
                            <c:when test="${not empty movement.fromWarehouseName}">
                                🏢 ${movement.fromWarehouseName}
                            </c:when>
                            <c:when test="${not empty movement.fromBranchName}">
                                🏪 ${movement.fromBranchName}
                            </c:when>
                            <c:otherwise>
                                --
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="route-arrow">➜</div>
                    <div class="route-point">
                        <c:choose>
                            <c:when test="${not empty movement.toWarehouseName}">
                                🏢 ${movement.toWarehouseName}
                            </c:when>
                            <c:when test="${not empty movement.toBranchName}">
                                🏪 ${movement.toBranchName}
                            </c:when>
                            <c:otherwise>
                                --
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <div class="info-grid">
                    <div class="info-item">
                        <div class="info-label">Người tạo</div>
                        <div class="info-value">
                            <c:choose>
                                <c:when test="${not empty movement.creatorName}">
                                    👤 ${movement.creatorName}
                                </c:when>
                                <c:otherwise>
                                    User #${movement.createdBy}
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <div class="info-item">
                        <div class="info-label">Ngày tạo</div>
                        <div class="info-value">
                            <fmt:formatDate value="${movement.createdAt}" pattern="dd/MM/yyyy HH:mm:ss"/>
                        </div>
                    </div>

                    <div class="info-item">
                        <div class="info-label">Ghi chú</div>
                        <div class="info-value">
                            <c:choose>
                                <c:when test="${not empty movement.note}">
                                    ${movement.note}
                                </c:when>
                                <c:otherwise>
                                    <span style="color: #6c757d; font-style: italic;">Không có ghi chú</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Details Table -->
            <h2 class="section-title">📝 Danh sách sản phẩm</h2>
            
            <c:choose>
                <c:when test="${empty details}">
                    <div class="empty-state">
                        📦 Không có sản phẩm nào trong phiếu này
                    </div>
                </c:when>
                <c:otherwise>
                    <table class="details-table">
                        <thead>
                            <tr>
                                <th>STT</th>
                                <th>Mã sản phẩm</th>
                                <th>Tên sản phẩm</th>
                                <th>Đơn giá</th>
                                <th>Số lượng</th>
                                <th>Đã quét</th>
                                <th>Thành tiền</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:set var="totalQuantity" value="0" />
                            <c:set var="totalAmount" value="0" />
                            <c:forEach var="detail" items="${details}" varStatus="status">
                                <c:set var="subtotal" value="${detail.costPrice * detail.quantity}" />
                                <tr>
                                    <td>${status.index + 1}</td>
                                    <td><strong>${detail.productCode}</strong></td>
                                    <td>${detail.productName}</td>
                                    <td>
                                        <fmt:formatNumber value="${detail.costPrice}" type="currency" currencySymbol="₫"/>
                                    </td>
                                    <td><strong>${detail.quantity}</strong></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${detail.quantityScanned ne null}">
                                                ${detail.quantityScanned}
                                            </c:when>
                                            <c:otherwise>
                                                <span style="color: #6c757d;">--</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <strong>
                                            <fmt:formatNumber value="${subtotal}" type="currency" currencySymbol="₫"/>
                                        </strong>
                                    </td>
                                </tr>
                                <c:set var="totalQuantity" value="${totalQuantity + detail.quantity}" />
                                <c:set var="totalAmount" value="${totalAmount + subtotal}" />
                            </c:forEach>
                        </tbody>
                    </table>

                    <!-- Summary -->
                    <div class="summary-box">
                        <div class="summary-item">
                            <div class="summary-value">${details.size()}</div>
                            <div class="summary-label">Loại sản phẩm</div>
                        </div>
                        <div class="summary-item">
                            <div class="summary-value">${totalQuantity}</div>
                            <div class="summary-label">Tổng số lượng</div>
                        </div>
                        <div class="summary-item">
                            <div class="summary-value">
                                <fmt:formatNumber value="${totalAmount}" pattern="#,##0"/>₫
                            </div>
                            <div class="summary-label">Tổng giá trị</div>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</body>
</html>

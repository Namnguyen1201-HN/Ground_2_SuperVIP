<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý Nhà Cung Cấp - SWP391</title>
        <link rel="stylesheet" href="css/admin/Supplier.css">
        <style>
            body {
                padding-top: 70px;
            }
            .table-container {
                background: white;
                border-radius: 8px;
                box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
                padding: 20px;
                margin-top: 20px;
            }
            .suppliers-table {
                width: 100%;
                border-collapse: collapse;
            }
            .suppliers-table th, .suppliers-table td {
                padding: 12px;
                text-align: left;
                border-bottom: 1px solid #ddd;
            }
            .suppliers-table th {
                background-color: #f2f2f2;
            }
            .search-container {
                display: flex;
                align-items: center;
                margin-bottom: 20px;
                padding: 0px !important
            }
            .search-input {
                padding: 10px;
                border: 1px solid #ccc;
                border-radius: 4px;
                margin-right: 10px;
                flex: 1;
            }
            .search-input:focus {
                border-color: #007bff;
                outline: none;
            }
            .pagination-container {
                margin-top: 20px;
            }
            .page-btn {
                padding: 6px 10px;
                border: 1px solid #ddd;
                border-radius: 4px;
                margin: 0 3px;
                text-decoration: none;
                color: #333;
            }
            .page-btn.active {
                background-color: #007bff;
                color: white;
                border-color: #007bff;
            }
            .page-btn.disabled {
                pointer-events: none;
                opacity: 0.5;
            }
            .supplier-row:hover {
                background-color: #f5f5f5;
            }
            .supplier-detail-container {
                display: none;
                margin-top: 20px;
                padding: 20px;
                background: white;
                border-radius: 8px;
                box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            }
            .supplier-detail-container.visible {
                display: block;
            }
            .supplier-detail-header {
                font-size: 18px;
                font-weight: bold;
                margin-bottom: 20px;
                color: #333;
            }
            .supplier-form {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 15px;
            }
            .form-group {
                display: flex;
                flex-direction: column;
            }
            .form-group.full-width {
                grid-column: 1 / -1;
            }
            .form-group label {
                margin-bottom: 5px;
                font-weight: 500;
                color: #555;
            }
            .form-group input,
            .form-group select {
                padding: 8px;
                border: 1px solid #ddd;
                border-radius: 4px;
                font-size: 14px;
            }
            .form-group input:focus,
            .form-group select:focus {
                border-color: #007bff;
                outline: none;
            }
            .form-group input[readonly] {
                background-color: #f5f5f5;
                cursor: not-allowed;
            }
            .error-message {
                color: red;
                font-size: 12px;
                margin-top: 5px;
            }
            .btn-save {
                grid-column: 1 / -1;
                padding: 10px 20px;
                background-color: #007bff;
                color: white;
                border: none;
                border-radius: 4px;
                cursor: pointer;
                font-size: 16px;
                margin-top: 10px;
            }
            .btn-save:hover {
                background-color: #0056b3;
            }

        </style>
    </head>
    <body>
        <%@ include file="header_admin.jsp" %>

        <div class="main-container">
            <!-- Main Content -->
            <main class="main-content">
                <div class="page-header">
                    <h1>Nhà Cung Cấp</h1>
                    <div class="header-actions">
                        <div class="search-container">
                            <form action="Supplier" method="post" id="searchForm">
                                <input type="hidden" name="action" value="search">
                                <input type="text" name="keyword" id="searchInput"
                                       value="${keyword}"
                                       placeholder="Tìm kiếm theo mã, tên"
                                       class="search-input" />
                                <button type="submit" class="search-btn" style="background:none; border:none; cursor:pointer;">
                                    <i class="fas fa-search"></i>
                                </button>
                            </form>
                        </div>
                    </div>
                </div>

                <!-- Suppliers Table -->
                <div class="table-container">
                    <table class="suppliers-table" id="suppliersTable">
                        <thead>
                            <tr>
                                <th>Mã Nhà Cung Cấp</th>
                                <th>Tên Công Ty</th>
                                <th>Người Giao Dịch</th>
                                <th>Số Điện Thoại</th>
                                <th>Gmail</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty suppliers}">
                                    <tr>
                                        <td colspan="5" style="text-align:center; color:#888;">
                                            Không tìm thấy nhà cung cấp phù hợp.
                                        </td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="s" items="${suppliers}">
                                        <tr class="supplier-row" data-supplier-id="${s.supplierId}" style="cursor: pointer;">
                                            <td>${s.supplierId}</td>
                                            <td>${s.supplierName}</td>
                                            <td>${s.contactName}</td>
                                            <td>${s.phone}</td>
                                            <td>${s.email}</td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>

                <!-- Pagination -->
                <div class="pagination-container mt-3 d-flex justify-content-between align-items-center">
                    <div class="pagination-info">
                        Hiển thị ${startSupplier} - ${endSupplier} / Tổng số ${totalSuppliers} Nhà Cung Cấp
                    </div>
                    <div class="pagination">
                        <c:choose>
                            <c:when test="${not empty keyword}">
                                <div class="pagination">
                                    <a href="Supplier?action=search&page=1&keyword=${keyword}" class="page-btn ${currentPage == 1 ? 'disabled' : ''}">
                                        <i class="fas fa-angle-double-left"></i>
                                    </a>
                                    <a href="Supplier?action=search&page=${currentPage - 1}&keyword=${keyword}" class="page-btn ${currentPage == 1 ? 'disabled' : ''}">
                                        <i class="fas fa-angle-left"></i>
                                    </a>
                                    <c:forEach begin="1" end="${totalPages}" var="i">
                                        <a href="Supplier?action=search&page=${i}&keyword=${keyword}" class="page-btn ${i == currentPage ? 'active' : ''}">${i}</a>
                                    </c:forEach>
                                    <a href="Supplier?action=search&page=${currentPage + 1}&keyword=${keyword}" class="page-btn ${currentPage == totalPages ? 'disabled' : ''}">
                                        <i class="fas fa-angle-right"></i>
                                    </a>
                                    <a href="Supplier?action=search&page=${totalPages}&keyword=${keyword}" class="page-btn ${currentPage == totalPages ? 'disabled' : ''}">
                                        <i class="fas fa-angle-double-right"></i>
                                    </a>
                                </div>
                            </c:when>

                            <c:otherwise>
                                <div class="pagination">
                                    <a href="Supplier?page=1" class="page-btn ${currentPage == 1 ? 'disabled' : ''}">
                                        <i class="fas fa-angle-double-left"></i>
                                    </a>
                                    <a href="Supplier?page=${currentPage - 1}" class="page-btn ${currentPage == 1 ? 'disabled' : ''}">
                                        <i class="fas fa-angle-left"></i>
                                    </a>
                                    <c:forEach begin="1" end="${totalPages}" var="i">
                                        <a href="Supplier?page=${i}" class="page-btn ${i == currentPage ? 'active' : ''}">${i}</a>
                                    </c:forEach>
                                    <a href="Supplier?page=${currentPage + 1}" class="page-btn ${currentPage == totalPages ? 'disabled' : ''}">
                                        <i class="fas fa-angle-right"></i>
                                    </a>
                                    <a href="Supplier?page=${totalPages}" class="page-btn ${currentPage == totalPages ? 'disabled' : ''}">
                                        <i class="fas fa-angle-double-right"></i>
                                    </a>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <!-- Supplier Detail Form -->
                <div class="supplier-detail-container" id="supplierDetailContainer">
                    <div class="supplier-detail-header">Thông tin nhà cung cấp</div>
                    <form id="supplierForm" class="supplier-form">
                        <input type="hidden" id="supplierId" name="supplierId">
                        
                        <div class="form-group">
                            <label>Mã NCC:</label>
                            <input type="text" id="supplierIdDisplay" readonly>
                        </div>

                        <div class="form-group">
                            <label>Tên công ty: <span style="color: red;">*</span></label>
                            <input type="text" id="supplierName" name="supplierName" required>
                            <span class="error-message" id="supplierNameError"></span>
                        </div>

                        <div class="form-group">
                            <label>Người giao dịch: <span style="color: red;">*</span></label>
                            <input type="text" id="contactName" name="contactName" required>
                            <span class="error-message" id="contactNameError"></span>
                        </div>

                        <div class="form-group">
                            <label>Email: <span style="color: red;">*</span></label>
                            <input type="email" id="email" name="email" required>
                            <span class="error-message" id="emailError"></span>
                        </div>

                        <div class="form-group">
                            <label>SĐT: <span style="color: red;">*</span></label>
                            <input type="text" id="phone" name="phone" required>
                            <span class="error-message" id="phoneError"></span>
                        </div>

                        <div class="form-group">
                            <label>Ngày tạo:</label>
                            <input type="text" id="createdAt" readonly>
                        </div>

                        <button type="submit" class="btn-save">
                            <i class="fas fa-save"></i> Lưu
                        </button>
                    </form>
                </div>
            </main>
        </div>

        <script>
            // Double-click event on table rows
            document.addEventListener('DOMContentLoaded', function() {
                const supplierRows = document.querySelectorAll('.supplier-row');
                
                supplierRows.forEach(row => {
                    row.addEventListener('dblclick', function() {
                        const supplierId = this.getAttribute('data-supplier-id');
                        console.log('Double-clicked row, supplierId:', supplierId);
                        if (supplierId) {
                            loadSupplierDetails(supplierId);
                        } else {
                            console.error('No supplier ID found in row');
                            alert('Lỗi: Không tìm thấy mã nhà cung cấp');
                        }
                    });
                });

                // Form submission
                document.getElementById('supplierForm').addEventListener('submit', function(e) {
                    e.preventDefault();
                    saveSupplier();
                });

                // Close form when clicking outside
                document.addEventListener('click', function(e) {
                    const container = document.getElementById('supplierDetailContainer');
                    if (container && container.classList.contains('visible')) {
                        if (!container.contains(e.target) && !e.target.closest('.supplier-row')) {
                            container.classList.remove('visible');
                        }
                    }
                });
            });

            function loadSupplierDetails(supplierId) {
                console.log('Loading supplier details for ID:', supplierId);
                
                const container = document.getElementById('supplierDetailContainer');
                container.style.display = 'block';
                container.classList.add('visible');
                
                // Show loading state - disable form instead of clearing
                const form = document.getElementById('supplierForm');
                if (form) {
                    form.style.opacity = '0.5';
                    form.style.pointerEvents = 'none';
                }
                
                const url = 'Supplier?action=getSupplierDetails&supplierId=' + supplierId;
                console.log('Fetching from:', url);
                
                fetch(url)
                    .then(response => {
                        console.log('Response status:', response.status);
                        if (!response.ok) {
                            throw new Error('HTTP error! status: ' + response.status);
                        }
                        const contentType = response.headers.get('content-type');
                        if (!contentType || !contentType.includes('application/json')) {
                            return response.text().then(text => {
                                console.error('Expected JSON but got:', text.substring(0, 200));
                                throw new Error('Response is not JSON. Got: ' + text.substring(0, 100));
                            });
                        }
                        return response.json();
                    })
                    .then(data => {
                        console.log('Response data:', data);
                        if (data.success) {
                            if (form) {
                                form.style.opacity = '1';
                                form.style.pointerEvents = 'auto';
                            }
                            populateForm(data);
                        } else {
                            alert('Lỗi: ' + data.message);
                            container.classList.remove('visible');
                        }
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        alert('Lỗi khi tải thông tin nhà cung cấp');
                        container.classList.remove('visible');
                        if (form) {
                            form.style.opacity = '1';
                            form.style.pointerEvents = 'auto';
                        }
                    });
            }

            function populateForm(data) {
                document.getElementById('supplierId').value = data.supplierID;
                document.getElementById('supplierIdDisplay').value = data.supplierID;
                document.getElementById('supplierName').value = data.supplierName || '';
                document.getElementById('contactName').value = data.contactName || '';
                document.getElementById('email').value = data.email || '';
                document.getElementById('phone').value = data.phone || '';
                document.getElementById('createdAt').value = data.createdAt || '';

                // Clear previous errors
                clearErrors();
            }

            function clearErrors() {
                document.querySelectorAll('.error-message').forEach(el => {
                    el.textContent = '';
                });
            }

            function showError(fieldId, message) {
                const errorEl = document.getElementById(fieldId + 'Error');
                if (errorEl) {
                    errorEl.textContent = message;
                }
            }

            function validateForm() {
                clearErrors();
                let isValid = true;

                const supplierName = document.getElementById('supplierName').value.trim();
                if (!supplierName || supplierName.length < 2 || supplierName.length > 100) {
                    showError('supplierName', 'Tên công ty phải có độ dài từ 2 đến 100 ký tự');
                    isValid = false;
                }

                const contactName = document.getElementById('contactName').value.trim();
                if (!contactName || contactName.length < 2 || contactName.length > 100) {
                    showError('contactName', 'Tên người giao dịch phải có độ dài từ 2 đến 100 ký tự');
                    isValid = false;
                }

                const email = document.getElementById('email').value.trim().toLowerCase();
                if (!email) {
                    showError('email', 'Email không được để trống');
                    isValid = false;
                } else if (email.length > 100) {
                    showError('email', 'Email không được vượt quá 100 ký tự');
                    isValid = false;
                } else if (!/^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/.test(email)) {
                    showError('email', 'Email không hợp lệ');
                    isValid = false;
                }

                const phone = document.getElementById('phone').value.trim();
                if (!phone) {
                    showError('phone', 'Số điện thoại không được để trống');
                    isValid = false;
                } else {
                    const phoneDigits = phone.replace(/[^0-9]/g, '');
                    if (phoneDigits.length < 10 || phoneDigits.length > 11) {
                        showError('phone', 'Số điện thoại phải có từ 10 đến 11 chữ số');
                        isValid = false;
                    }
                }

                return isValid;
            }

            function saveSupplier() {
                if (!validateForm()) {
                    return;
                }

                const supplierId = document.getElementById('supplierId').value;
                
                console.log('Saving supplier with ID:', supplierId);
                
                if (!supplierId || supplierId.trim() === '') {
                    alert('Lỗi: Không tìm thấy mã nhà cung cấp');
                    return;
                }
                
                // Get form values
                const supplierName = document.getElementById('supplierName').value;
                const contactName = document.getElementById('contactName').value;
                const email = document.getElementById('email').value;
                let phone = document.getElementById('phone').value;
                
                // Chuẩn hóa số điện thoại
                if (phone) {
                    phone = phone.trim().replace(/\s+/g, '');
                    if (phone.startsWith('+84')) {
                        phone = '0' + phone.substring(3).replace(/[^0-9]/g, '');
                    } else {
                        phone = phone.replace(/[^0-9]/g, '');
                    }
                }
                
                // Create URLSearchParams
                const params = new URLSearchParams();
                params.append('action', 'updateSupplier');
                params.append('supplierId', supplierId);
                params.append('supplierName', supplierName);
                params.append('contactName', contactName);
                params.append('email', email.toLowerCase());
                params.append('phone', phone);
                
                // Log all form data
                console.log('Sending parameters:');
                for (let [key, value] of params.entries()) {
                    console.log('  ' + key + ':', value);
                }

                // Send as URL-encoded form data
                const url = 'Supplier?action=updateSupplier';
                console.log('Sending to URL:', url);

                fetch(url, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'
                    },
                    body: params.toString()
                })
                    .then(response => {
                        console.log('Update response status:', response.status);
                        console.log('Update response content-type:', response.headers.get('content-type'));
                        if (!response.ok) {
                            throw new Error('HTTP error! status: ' + response.status);
                        }
                        const contentType = response.headers.get('content-type');
                        if (!contentType || !contentType.includes('application/json')) {
                            return response.text().then(text => {
                                console.error('Expected JSON but got:', text.substring(0, 200));
                                throw new Error('Response is not JSON. Got: ' + text.substring(0, 100));
                            });
                        }
                        return response.json();
                    })
                    .then(data => {
                        if (data.success) {
                            alert(data.message);
                            location.reload();
                        } else {
                            alert('Lỗi: ' + data.message);
                        }
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        alert('Lỗi khi cập nhật thông tin nhà cung cấp');
                    });
            }
        </script>
    </body>
</html>

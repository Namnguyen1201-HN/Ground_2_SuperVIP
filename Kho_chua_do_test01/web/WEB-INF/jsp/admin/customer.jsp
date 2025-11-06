<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý Khách Hàng - SWP391</title>
        <link rel="stylesheet" href="css/admin/Supplier.css">
        <style>
            body {
                background-color: #f5f7fa;
                font-family: 'Inter', sans-serif;
                padding-top: 70px;
            }
            .main-container {
                display: flex;
                gap: 25px;
                padding: 25px;
            }

            /* 🎨 FILTER SIDEBAR */
            .filter-container {
                width: 280px;
                background: #fff;
                border-radius: 16px;
                box-shadow: 0 4px 12px rgba(0,0,0,0.07);
                padding: 22px;
                transition: all 0.3s ease;
            }
            .filter-container:hover {
                box-shadow: 0 6px 16px rgba(0,0,0,0.1);
            }

            .filter-header {
                display: flex;
                align-items: center;
                gap: 8px;
                margin-bottom: 20px;
            }
            .filter-header i {
                color: #007bff;
                font-size: 18px;
            }
            .filter-header h2 {
                font-size: 17px;
                font-weight: 600;
                margin: 0;
                color: #333;
            }

            .filter-group {
                margin-bottom: 22px;
            }
            .filter-group h3 {
                font-size: 14px;
                font-weight: 600;
                color: #555;
                margin-bottom: 10px;
                display: flex;
                align-items: center;
                gap: 6px;
            }
            .filter-group h3 i {
                color: #007bff;
            }

            .filter-container input[type="number"],
            .filter-container select {
                width: 100%;
                padding: 9px 10px;
                border-radius: 8px;
                border: 1px solid #d0d7de;
                background-color: #fafbfc;
                font-size: 14px;
                transition: 0.2s;
            }
            .filter-container input:focus,
            .filter-container select:focus {
                outline: none;
                border-color: #007bff;
                box-shadow: 0 0 0 3px rgba(0,123,255,0.15);
                background-color: #fff;
            }

            .radio-group {
                padding-left: 4px;
            }
            .radio-group label {
                display: flex;
                align-items: center;
                gap: 6px;
                margin-bottom: 6px;
                font-size: 14px;
                cursor: pointer;
            }
            .radio-group input {
                accent-color: #007bff;
            }

            .filter-container button {
                width: 100%;
                padding: 10px 0;
                border-radius: 10px;
                border: none;
                color: white;
                font-weight: 500;
                font-size: 14px;
                cursor: pointer;
                transition: background 0.3s ease;
            }
            .btn-clear {
                background-color: #adb5bd;
                margin-bottom: 10px;
            }
            .btn-clear:hover {
                background-color: #9aa1a7;
            }
            .btn-apply {
                background-color: #007bff;
            }
            .btn-apply:hover {
                background-color: #0069d9;
            }

            /* TABLE */
            .table-container {
                flex: 1;
                background: white;
                border-radius: 16px;
                box-shadow: 0 4px 12px rgba(0,0,0,0.07);
                padding: 25px;
            }

            .search-container {
                display: flex;
                justify-content: flex-end;
                margin-bottom: 15px;
                padding: 0px !important;
                
            }
            .search-input {
                padding: 8px 10px;
                border-radius: 8px;
                border: 1px solid #ccc;
                width: 260px;
            }
            .suppliers-table {
                width: 100%;
                border-collapse: collapse;
            }
            .suppliers-table th, .suppliers-table td {
                padding: 12px;
                border-bottom: 1px solid #eee;
            }
            .suppliers-table th {
                background: #f1f3f5;
                color: #333;
                font-weight: 600;
            }
            .suppliers-table tr:hover {
                background-color: #f9fafb;
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
            .nav-item.dropdown {
                position: relative;
            }

            .nav-item.dropdown .dropdown-toggle {
                display: flex;
                align-items: center;
                justify-content: space-between;
                text-decoration: none;
                color: #333;
                padding: 10px 15px;
                cursor: pointer;
            }

            .nav-item.dropdown .dropdown-menu {
                display: none;
                position: absolute;
                top: 100%;
                left: 0;
                background: white;
                border: 1px solid #ddd;
                border-radius: 6px;
                box-shadow: 0 2px 8px rgba(0,0,0,0.1);
                min-width: 180px;
                z-index: 1000;
            }

            .nav-item.dropdown:hover .dropdown-menu {
                display: block;
            }

            .dropdown-item {
                display: block;
                padding: 10px 15px;
                color: #333;
                text-decoration: none;
                transition: background 0.2s;
            }

            .dropdown-item:hover {
                background-color: #f2f2f2;
            }

            .dropdown-item.active {
                background-color: #007bff;
                color: white;
            }

            /* Customer Detail Form */
            .customer-detail-container {
                margin-top: 25px;
                background: white;
                border-radius: 16px;
                box-shadow: 0 4px 12px rgba(0,0,0,0.07);
                padding: 25px;
                display: none;
            }

            .customer-detail-container.show {
                display: block;
            }

            .customer-detail-header {
                font-size: 18px;
                font-weight: 600;
                color: #333;
                margin-bottom: 20px;
                padding-bottom: 10px;
                border-bottom: 2px solid #eee;
            }

            .customer-form {
                display: grid;
                grid-template-columns: repeat(2, 1fr);
                gap: 20px;
            }

            .form-group {
                display: flex;
                flex-direction: column;
            }

            .form-group label {
                font-size: 14px;
                font-weight: 500;
                color: #555;
                margin-bottom: 6px;
            }

            .form-group input,
            .form-group select {
                padding: 10px;
                border-radius: 8px;
                border: 1px solid #d0d7de;
                background-color: #fafbfc;
                font-size: 14px;
                transition: 0.2s;
            }

            .form-group input:focus,
            .form-group select:focus {
                outline: none;
                border-color: #007bff;
                box-shadow: 0 0 0 3px rgba(0,123,255,0.15);
                background-color: #fff;
            }

            .form-group input[readonly] {
                background-color: #f5f5f5;
                cursor: not-allowed;
            }

            .form-group.full-width {
                grid-column: 1 / -1;
            }

            .error-message {
                color: #dc3545;
                font-size: 12px;
                margin-top: 4px;
                display: none;
            }

            .error-message.show {
                display: block;
            }

            .btn-save {
                grid-column: 1 / -1;
                padding: 12px 24px;
                background-color: #28a745;
                color: white;
                border: none;
                border-radius: 8px;
                font-size: 14px;
                font-weight: 500;
                cursor: pointer;
                transition: background 0.3s ease;
                margin-top: 10px;
            }

            .btn-save:hover {
                background-color: #218838;
            }

            .suppliers-table tr.customer-row:hover {
                background-color: #e3f2fd;
            }
        </style>
    </head>
    <body>
        <%@ include file="header_admin.jsp" %>

        <div class="main-container">
            <!-- 🎯 FILTER SIDEBAR -->
            <div class="filter-container">
                <div class="filter-header">
                    <i class="fas fa-filter"></i>
                    <h2>Bộ lọc khách hàng</h2>
                </div>
                <form action="Customer" method="get">
                    <div class="filter-group">
                        <h3><i class="fas fa-dollar-sign"></i> Khoảng chi tiêu</h3>
                        <input type="number" name="minSpent" placeholder="Từ..." value="${param.minSpent}">
                        <input type="number" name="maxSpent" placeholder="Đến..." value="${param.maxSpent}">
                    </div>

                    <div class="filter-group">
                        <h3><i class="fas fa-venus-mars"></i> Giới tính</h3>
                        <div class="radio-group">
                            <label><input type="radio" name="gender" value="all" ${param.gender == 'all' || empty param.gender ? 'checked' : ''}> Tất cả</label>
                            <label><input type="radio" name="gender" value="male" ${param.gender == 'male' ? 'checked' : ''}> Nam</label>
                            <label><input type="radio" name="gender" value="female" ${param.gender == 'female' ? 'checked' : ''}> Nữ</label>
                        </div>
                    </div>

                    <div class="filter-group">
                        <h3><i class="fas fa-store"></i> Chi nhánh</h3>
                        <select name="branchId">
                            <option value="0">-- Tất cả chi nhánh --</option>
                            <c:forEach var="b" items="${branches}">
                                <option value="${b.branchId}" ${param.branchId == b.branchId ? 'selected' : ''}>${b.branchName}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <button type="submit" class="btn-clear" name="action" value="clear">
                        <i class="fas fa-rotate-left"></i> Xóa bộ lọc
                    </button>
                    <button type="submit" class="btn-apply" name="action" value="filter">
                        <i class="fas fa-check"></i> Áp dụng lọc
                    </button>
                </form>
            </div>

            <!-- TABLE -->
            <div class="table-container">
                <div class="search-container">
                    <form action="Customer" method="get">
                        <input type="text" name="keyword" class="search-input" placeholder="Theo mã, tên khách hàng..." value="${param.keyword}">
                        <button type="submit" style="background:none; border:none; cursor:pointer;">
                            <i class="fas fa-search"></i>
                        </button>
                    </form>
                </div>

                <table class="suppliers-table">
                    <thead>
                        <tr>
                            <th>Mã KH</th>
                            <th>Tên khách hàng</th>
                            <th>Số điện thoại</th>
                            <th>Địa chỉ</th>
                            <th>Tổng chi tiêu</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty customers}">
                                <tr><td colspan="5" style="text-align:center; color:#888;">Không tìm thấy khách hàng phù hợp.</td></tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="c" items="${customers}">
                                    <tr class="customer-row" data-customer-id="${c.customerID}" style="cursor: pointer;">
                                        <td>${c.customerID}</td>
                                        <td>${c.fullname}</td>
                                        <td>${c.phoneNumber}</td>
                                        <td>${c.address}</td>
                                        <td>${c.totalSpent}</td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>

                <!-- Customer Detail Form -->
                <div class="customer-detail-container" id="customerDetailContainer">
                    <div class="customer-detail-header">Thông tin khách hàng</div>
                    <form id="customerForm" class="customer-form">
                        <input type="hidden" id="customerId" name="customerId">
                        
                        <div class="form-group">
                            <label>Mã KH:</label>
                            <input type="text" id="customerIdDisplay" readonly>
                        </div>

                        <div class="form-group">
                            <label>Mã Chi nhánh:</label>
                            <input type="text" id="branchIdDisplay" readonly>
                        </div>

                        <div class="form-group">
                            <label>Tên khách: <span style="color: red;">*</span></label>
                            <input type="text" id="fullname" name="fullname" required>
                            <span class="error-message" id="fullnameError"></span>
                        </div>

                        <div class="form-group">
                            <label>Email: <span style="color: red;">*</span></label>
                            <input type="email" id="email" name="email" required>
                            <span class="error-message" id="emailError"></span>
                        </div>

                        <div class="form-group">
                            <label>Giới tính:</label>
                            <select id="gender" name="gender">
                                <option value="Nam">Nam</option>
                                <option value="Nữ">Nữ</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label>SĐT: <span style="color: red;">*</span></label>
                            <input type="text" id="phoneNumber" name="phoneNumber" required>
                            <span class="error-message" id="phoneNumberError"></span>
                        </div>

                        <div class="form-group full-width">
                            <label>Địa chỉ: <span style="color: red;">*</span></label>
                            <input type="text" id="address" name="address" required>
                            <span class="error-message" id="addressError"></span>
                        </div>

                        <div class="form-group">
                            <label>Ngày tạo:</label>
                            <input type="text" id="createdAt" readonly>
                        </div>

                        <div class="form-group">
                            <label>Tổng chi tiêu:</label>
                            <input type="text" id="totalSpent" readonly>
                        </div>

                        <button type="submit" class="btn-save">
                            <i class="fas fa-save"></i> Lưu
                        </button>
                    </form>
                </div>
            </div>
        </div>

        <script>
            // Double-click event on table rows
            document.addEventListener('DOMContentLoaded', function() {
                const customerRows = document.querySelectorAll('.customer-row');
                
                customerRows.forEach(row => {
                    row.addEventListener('dblclick', function() {
                        const customerId = this.getAttribute('data-customer-id');
                        console.log('Double-clicked row, customerId:', customerId);
                        if (customerId) {
                            loadCustomerDetails(customerId);
                        } else {
                            console.error('No customer ID found in row');
                            alert('Lỗi: Không tìm thấy mã khách hàng');
                        }
                    });
                });

                // Form submission
                document.getElementById('customerForm').addEventListener('submit', function(e) {
                    e.preventDefault();
                    saveCustomer();
                });
            });

            function loadCustomerDetails(customerId) {
                console.log('Loading customer details for ID:', customerId);
                
                // Show container
                const container = document.getElementById('customerDetailContainer');
                const form = document.getElementById('customerForm');
                
                if (!container || !form) {
                    console.error('Container or form not found');
                    alert('Lỗi: Không tìm thấy form');
                    return;
                }
                
                container.classList.add('show');
                
                // Show loading state on form
                form.style.opacity = '0.5';
                form.style.pointerEvents = 'none';

                // Fetch customer details
                const url = 'Customer?action=getCustomerDetails&customerId=' + customerId;
                console.log('Fetching from:', url);
                
                fetch(url)
                    .then(response => {
                        console.log('Response status:', response.status);
                        if (!response.ok) {
                            throw new Error('HTTP error! status: ' + response.status);
                        }
                        return response.json();
                    })
                    .then(data => {
                        console.log('Response data:', data);
                        if (data.success) {
                            populateForm(data);
                            form.style.opacity = '1';
                            form.style.pointerEvents = 'auto';
                            // Scroll to form
                            container.scrollIntoView({ behavior: 'smooth', block: 'start' });
                        } else {
                            alert('Lỗi: ' + data.message);
                            container.classList.remove('show');
                            form.style.opacity = '1';
                            form.style.pointerEvents = 'auto';
                        }
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        alert('Lỗi khi tải thông tin khách hàng: ' + error.message);
                        container.classList.remove('show');
                        form.style.opacity = '1';
                        form.style.pointerEvents = 'auto';
                    });
            }

            function populateForm(data) {
                const form = document.getElementById('customerForm');
                if (!form) {
                    console.error('Form not found');
                    return;
                }

                document.getElementById('customerId').value = data.customerID;
                document.getElementById('customerIdDisplay').value = data.customerID;
                document.getElementById('branchIdDisplay').value = data.branchId || '0';
                document.getElementById('fullname').value = data.fullname || '';
                document.getElementById('email').value = data.email || '';
                document.getElementById('phoneNumber').value = data.phoneNumber || '';
                document.getElementById('address').value = data.address || '';
                document.getElementById('gender').value = data.gender || 'Nam';
                document.getElementById('createdAt').value = data.createdAt || '';
                document.getElementById('totalSpent').value = data.totalSpent || '0 ₫';

                // Clear previous errors
                clearErrors();
            }

            function clearErrors() {
                const errorMessages = document.querySelectorAll('.error-message');
                errorMessages.forEach(error => {
                    error.classList.remove('show');
                    error.textContent = '';
                });
            }

            function showError(fieldId, message) {
                const errorElement = document.getElementById(fieldId + 'Error');
                if (errorElement) {
                    errorElement.textContent = message;
                    errorElement.classList.add('show');
                }
            }

            function validateForm() {
                let isValid = true;
                clearErrors();

                const fullname = document.getElementById('fullname').value.trim();
                const email = document.getElementById('email').value.trim();
                let phoneNumber = document.getElementById('phoneNumber').value.trim();
                const address = document.getElementById('address').value.trim();

                // Validation - Tên khách hàng
                if (!fullname) {
                    showError('fullname', 'Tên khách hàng không được để trống');
                    isValid = false;
                } else if (fullname.length < 2 || fullname.length > 100) {
                    showError('fullname', 'Tên khách hàng phải có độ dài từ 2 đến 100 ký tự');
                    isValid = false;
                } else if (!/^[\p{L}\p{N}\s'.-]+$/u.test(fullname)) {
                    showError('fullname', 'Tên khách hàng chứa ký tự không hợp lệ');
                    isValid = false;
                }

                // Validation - Email
                if (!email) {
                    showError('email', 'Email không được để trống');
                    isValid = false;
                } else if (email.length > 100) {
                    showError('email', 'Email không được vượt quá 100 ký tự');
                    isValid = false;
                } else if (!email.match(/^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/)) {
                    showError('email', 'Email không hợp lệ. Vui lòng nhập đúng định dạng email');
                    isValid = false;
                }

                // Validation - Số điện thoại
                if (!phoneNumber) {
                    showError('phoneNumber', 'Số điện thoại không được để trống');
                    isValid = false;
                } else {
                    // Xóa khoảng trắng
                    phoneNumber = phoneNumber.replace(/\s+/g, '');
                    // Kiểm tra chỉ chứa số và các ký tự +, -, (, )
                    if (!phoneNumber.match(/^[0-9+\-()]+$/)) {
                        showError('phoneNumber', 'Số điện thoại chỉ được chứa số và các ký tự +, -, (, )');
                        isValid = false;
                    } else {
                        // Xóa các ký tự đặc biệt để kiểm tra độ dài
                        const phoneDigits = phoneNumber.replace(/[^0-9]/g, '');
                        if (phoneDigits.length < 10 || phoneDigits.length > 11) {
                            showError('phoneNumber', 'Số điện thoại phải có từ 10 đến 11 chữ số');
                            isValid = false;
                        } else if (!phoneDigits.startsWith('0') && !phoneNumber.startsWith('+84')) {
                            showError('phoneNumber', 'Số điện thoại phải bắt đầu bằng 0 hoặc +84');
                            isValid = false;
                        }
                    }
                }

                // Validation - Địa chỉ
                if (!address) {
                    showError('address', 'Địa chỉ không được để trống');
                    isValid = false;
                } else if (address.length < 5 || address.length > 200) {
                    showError('address', 'Địa chỉ phải có độ dài từ 5 đến 200 ký tự');
                    isValid = false;
                }

                return isValid;
            }

            function saveCustomer() {
                if (!validateForm()) {
                    return;
                }

                const form = document.getElementById('customerForm');
                const customerId = document.getElementById('customerId').value;
                
                console.log('Saving customer with ID:', customerId);
                
                if (!customerId || customerId.trim() === '') {
                    alert('Lỗi: Không tìm thấy mã khách hàng');
                    return;
                }
                
                // Get form values
                const fullname = document.getElementById('fullname').value;
                const phoneNumberEl = document.getElementById('phoneNumber');
                let phoneNumber = phoneNumberEl ? phoneNumberEl.value : '';
                const email = document.getElementById('email').value;
                const address = document.getElementById('address').value;
                const gender = document.getElementById('gender').value;
                
                // Chuẩn hóa số điện thoại
                if (phoneNumber) {
                    phoneNumber = phoneNumber.trim().replace(/\s+/g, '');
                    if (phoneNumber.startsWith('+84')) {
                        phoneNumber = '0' + phoneNumber.substring(3).replace(/[^0-9]/g, '');
                    } else {
                        phoneNumber = phoneNumber.replace(/[^0-9]/g, '');
                    }
                }
                
                // Create URLSearchParams instead of FormData for better servlet compatibility
                const params = new URLSearchParams();
                params.append('action', 'updateCustomer');
                params.append('customerId', customerId);
                params.append('fullname', fullname);
                params.append('phoneNumber', phoneNumber);
                params.append('email', email);
                params.append('address', address);
                params.append('gender', gender);
                
                // Log all form data
                console.log('Sending parameters:');
                for (let [key, value] of params.entries()) {
                    console.log('  ' + key + ':', value);
                }

                // Send as URL-encoded form data
                const url = 'Customer?action=updateCustomer';
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
                            // Reload page to show updated data
                            location.reload();
                        } else {
                            alert('Lỗi: ' + data.message);
                        }
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        alert('Lỗi khi cập nhật thông tin khách hàng');
                    });
            }
        </script>
    </body>
</html>

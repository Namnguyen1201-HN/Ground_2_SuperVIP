<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="java.util.List"%>
<%@page import="Model.Shift"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Quản lý ca làm việc</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">

        <style>
            body {
                background-color: #f5f6fa;
            }
            .card {
                border-radius: 12px;
                box-shadow: 0 3px 10px rgba(0,0,0,0.1);
            }
            .table th, .table td {
                vertical-align: middle;
            }
            .btn-edit {
                background-color: #ffb300;
                color: white;
            }
            .btn-delete {
                background-color: #f44336;
                color: white;
            }
            .btn-add {
                background-color: #2196F3;
                color: white;
            }
            .btn-add:hover {
                background-color: #1976D2;
            }
            .time-display {
                color: #1976D2;
                font-weight: 600;
            }
        </style>
    </head>
    <body>

        <%@ include file="../admin/header_admin.jsp" %>

        <div class="container mt-4">
            <div class="card p-4" style="margin: 85px;">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h5 class="mb-0 fw-bold">Quản lý ca làm việc</h5>

                    <div class="d-flex gap-2">
                        <form class="d-flex" method="get" action="ShiftUser">
                            <input type="text" name="search" value="<%= request.getParameter("search") != null ? request.getParameter("search") : "" %>"
                                   placeholder="Tìm kiếm theo tên ca làm việc"
                                   class="form-control form-control-sm me-2" style="min-width: 250px;">
                            <button class="btn btn-outline-primary btn-sm"><i class="fa fa-search"></i></button>
                        </form>

                        <button class="btn btn-add btn-sm" id="btnAddShift">
                            <i class="fa fa-plus"></i> Thêm ca làm việc
                        </button>

                        <a href="NhanVien" class="btn btn-primary btn-sm">
                            <i class="fa fa-users"></i> Nhân viên
                        </a>
                    </div>
                </div>

                <!-- Bảng ca làm việc -->
                <table class="table table-bordered table-hover align-middle">
                    <thead class="table-light">
                        <tr>
                            
                            <th>Mã ca</th>
                            <th>Tên ca làm việc</th>
                            <th>Giờ bắt đầu</th>
                            <th>Giờ kết thúc</th>
                            <th>Thời lượng</th>
                            <th class="text-center">Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            List<Shift> list = (List<Shift>) request.getAttribute("shiftList");
                            if (list == null || list.isEmpty()) {
                        %>
                        <tr>
                            <td colspan="7" class="text-center text-muted">Không có ca làm việc nào.</td>
                        </tr>
                        <%
                            } else {
                                for (Shift shift : list) {
                                    java.time.LocalTime start = shift.getStartTime();
                                    java.time.LocalTime end = shift.getEndTime();
                                    java.time.Duration duration = java.time.Duration.between(start, end);
                                    long hours = duration.toHours();
                                    long minutes = duration.toMinutes() % 60;
                                    String timeLength = hours + " giờ" + (minutes > 0 ? " " + minutes + " phút" : "");
                        %>
                        <tr>
                            
                            <td><%=shift.getShiftID()%></td>
                            <td><%=shift.getShiftName()%></td>
                            <td class="time-display"><%=shift.getStartTime()%></td>
                            <td class="time-display"><%=shift.getEndTime()%></td>
                            <td><%=timeLength%></td>
                            <td class="text-center">
                                <button class="btn btn-edit btn-sm btnEdit"
                                        data-id="<%=shift.getShiftID()%>"
                                        data-name="<%=shift.getShiftName()%>"
                                        data-start="<%=shift.getStartTime()%>"
                                        data-end="<%=shift.getEndTime()%>">
                                    <i class="fa fa-pen"></i> Sửa
                                </button>

                                <form action="ShiftUser" method="post" style="display:inline"
                                      onsubmit="return confirm('Bạn có chắc chắn muốn xóa ca này?')">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="shiftID" value="<%=shift.getShiftID()%>">
                                    <button type="submit" class="btn btn-delete btn-sm">
                                        <i class="fa fa-trash"></i> Xóa
                                    </button>
                                </form>
                            </td>
                        </tr>
                        <% } } %>
                    </tbody>
                </table>

                <!-- Phân trang -->
                <%
                    Integer currentPage = (Integer) request.getAttribute("currentPage");
                    Integer totalPages = (Integer) request.getAttribute("totalPages");
                    Integer startShift = (Integer) request.getAttribute("startShift");
                    Integer endShift = (Integer) request.getAttribute("endShift");
                    Integer totalShift = (Integer) request.getAttribute("totalShift");
                    if (currentPage == null) currentPage = 1;
                    if (totalPages == null) totalPages = 1;
                    if (startShift == null) startShift = 0;
                    if (endShift == null) endShift = 0;
                    if (totalShift == null) totalShift = 0;
                %>
                <div class="d-flex justify-content-between align-items-center">
                    <p class="text-muted small mb-0">
                        Hiển thị <%=startShift%> - <%=endShift%> / Tổng số <%=totalShift%> ca làm việc
                    </p>
                    <div>
                        <%
                            for (int i = 1; i <= totalPages; i++) {
                                String btnClass = (i == currentPage) ? "btn btn-sm btn-primary" : "btn btn-sm btn-outline-secondary";
                        %>
                        <a href="ShiftUser?page=<%=i%>" class="<%=btnClass%>"><%=i%></a>
                        <% } %>
                    </div>
                </div>
            </div>
        </div>

        <!-- Form tạo/sửa ca -->
        <div class="modal fade" id="shiftModal" tabindex="-1">
            <div class="modal-dialog">
                <form method="post" action="ShiftUser" class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="modalTitle">Thêm ca làm việc</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <input type="hidden" name="action" id="formAction" value="create">
                        <input type="hidden" name="shiftID" id="shiftID">

                        <div class="mb-3">
                            <label class="form-label">Tên ca làm việc</label>
                            <input type="text" name="shiftName" id="shiftName" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Giờ bắt đầu</label>
                            <input type="time" name="startTime" id="startTime" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Giờ kết thúc</label>
                            <input type="time" name="endTime" id="endTime" class="form-control" required>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="submit" class="btn btn-primary" id="submitBtn">Lưu</button>
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                    </div>
                </form>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
                                          const modal = new bootstrap.Modal(document.getElementById('shiftModal'));
                                          const btnAdd = document.getElementById('btnAddShift');
                                          const title = document.getElementById('modalTitle');
                                          const action = document.getElementById('formAction');
                                          const submitBtn = document.getElementById('submitBtn');
                                          const idInput = document.getElementById('shiftID');
                                          const nameInput = document.getElementById('shiftName');
                                          const startInput = document.getElementById('startTime');
                                          const endInput = document.getElementById('endTime');

                                          btnAdd.addEventListener('click', () => {
                                              title.textContent = 'Thêm ca làm việc mới';
                                              action.value = 'create';
                                              submitBtn.textContent = 'Tạo mới';
                                              idInput.value = '';
                                              nameInput.value = '';
                                              startInput.value = '';
                                              endInput.value = '';
                                              modal.show();
                                          });

                                          document.querySelectorAll('.btnEdit').forEach(btn => {
                                              btn.addEventListener('click', () => {
                                                  title.textContent = 'Chỉnh sửa ca làm việc';
                                                  action.value = 'update';
                                                  submitBtn.textContent = 'Cập nhật';
                                                  idInput.value = btn.dataset.id;
                                                  nameInput.value = btn.dataset.name;
                                                  startInput.value = btn.dataset.start;
                                                  endInput.value = btn.dataset.end;
                                                  modal.show();
                                              });
                                          });

                                          document.getElementById('selectAll').addEventListener('change', e => {
                                              document.querySelectorAll('input[name="shiftCheck"]').forEach(cb => cb.checked = e.target.checked);
                                          });
        </script>
    </body>
</html>

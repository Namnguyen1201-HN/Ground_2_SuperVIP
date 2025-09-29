<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Warehouse Management Dashboard</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        body { font-family: 'Inter', Arial, sans-serif; background: linear-gradient(120deg,#e0e7ff 0%,#f9fafb 100%); min-height:100vh; }
        .dashboard-container { display:flex; min-height:100vh; }
        .sidebar { width:260px; background:#fff; box-shadow:0 4px 24px rgba(0,0,0,0.07); padding:2rem 1rem; border-radius:0 2rem 2rem 0; }
        .sidebar h2 { font-size:1.3rem; color:#2563eb; font-weight:700; margin-bottom:2rem; text-align:center; }
        .sidebar .role { font-size:1.1rem; color:#374151; font-weight:600; margin-bottom:1.2rem; margin-top:2rem; }
        .sidebar ul { list-style:none; padding:0; margin:0; }
        .sidebar li { margin-bottom:1rem; }
        .sidebar a { color:#374151; text-decoration:none; font-weight:500; font-size:1rem; display:flex; align-items:center; gap:0.7rem; padding:0.5rem 0.7rem; border-radius:0.5rem; transition:background 0.2s; }
        .sidebar a:hover { background:#e0e7ff; color:#2563eb; }
        .main-content { flex:1; padding:2rem; }
        .dashboard-header { display:flex; align-items:center; justify-content:space-between; margin-bottom:2rem; }
        .dashboard-header h1 { font-size:2rem; color:#2563eb; font-weight:700; }
        .dashboard-header .user { font-size:1rem; color:#374151; font-weight:600; }
        .cards { display:grid; grid-template-columns:repeat(auto-fit,minmax(260px,1fr)); gap:2rem; }
        .card { background:#fff; border-radius:1.5rem; box-shadow:0 4px 24px rgba(59,130,246,0.07); padding:2rem 1.5rem; display:flex; flex-direction:column; align-items:flex-start; gap:1rem; }
        .card .card-icon { font-size:2rem; color:#3b82f6; margin-bottom:0.5rem; }
        .card-title { font-size:1.2rem; font-weight:700; color:#2563eb; margin-bottom:0.5rem; }
        .card-desc { color:#555; font-size:1rem; margin-bottom:0.5rem; }
        .card-link { color:#3b82f6; text-decoration:none; font-weight:600; margin-top:auto; }
        .card-link:hover { text-decoration:underline; }
        .btn { display:inline-flex; align-items:center; gap:0.5rem; padding:0.6rem 1rem; border-radius:0.75rem; font-weight:600; text-decoration:none; border:1px solid transparent; transition:all 0.15s ease; box-shadow:0 4px 14px rgba(37,99,235,0.12); }
        .btn:hover { text-decoration:none; transform:translateY(-1px); }
        .btn-primary { background:#4f46e5; color:#fff; border-color:#4f46e5; }
        .btn-primary:hover { background:#4338ca; box-shadow:0 6px 18px rgba(79,70,229,0.28); }
        .btn-outline { background:#fff; color:#4f46e5; border-color:#4f46e5; }
        .btn-outline:hover { background:#eef2ff; color:#4338ca; box-shadow:0 6px 18px rgba(37,99,235,0.22); }
        @media (max-width:900px) { .dashboard-container { flex-direction:column; } .sidebar { width:100%; border-radius:0; } .main-content { padding:1rem; } .cards { gap:1rem; } }
    </style>
</head>
<body>
    <div class="dashboard-container">
        <aside class="sidebar">
            <h2><i class="fas fa-warehouse"></i> Warehouse Management</h2>
            <div class="role">Shop owner</div>
            <ul>
                <li><a href="#"><i class="fas fa-users"></i> Staff management</a></li>
                <li><a href="product"><i class="fas fa-box"></i> Product management</a></li>
                <li><a href="#"><i class="fas fa-shopping-cart"></i> Order management</a></li>
                <li><a href="#"><i class="fas fa-bullhorn"></i> Promotion management</a></li>
                <li><a href="#"><i class="fas fa-exchange-alt"></i> Import/Export info</a></li>
                <li><a href="#"><i class="fas fa-chart-line"></i> Activity log & revenue report</a></li>
                <li><a href="#"><i class="fas fa-truck-loading"></i> Warehouse import request</a></li>
            </ul>
            <div class="role">Super admin</div>
            <ul>
                <li><a href="#"><i class="fas fa-user-shield"></i> Subscription management</a></li>
                <li><a href="#"><i class="fas fa-user-cog"></i> Shop owner accounts</a></li>
                <li><a href="#"><i class="fas fa-tachometer-alt"></i> System overview</a></li>
            </ul>
            <div class="role">Warehouse manager</div>
            <ul>
                <li><a href="#"><i class="fas fa-bell"></i> Import/Export notification</a></li>
                <li><a href="#"><i class="fas fa-database"></i> Warehouse data</a></li>
            </ul>
            <div class="role">Branch manager</div>
            <ul>
                <li><a href="#"><i class="fas fa-clipboard-list"></i> Branch inventory</a></li>
                <li><a href="#"><i class="fas fa-file-alt"></i> Branch revenue & log</a></li>
            </ul>
            <div class="role">Sale staffs</div>
            <ul>
                <li><a href="#"><i class="fas fa-info-circle"></i> Product info</a></li>
                <li><a href="#"><i class="fas fa-user"></i> Customer data</a></li>
                <li><a href="#"><i class="fas fa-bell"></i> Notification</a></li>
            </ul>
        </aside>
        <main class="main-content">
            <div class="dashboard-header">
                <h1>Welcome to Warehouse Management</h1>
                <div style="display:flex;align-items:center;gap:1rem;">
                    <a href="login" class="btn btn-outline"><i class="fas fa-sign-in-alt"></i> Đăng nhập</a>
                    <a href="register" class="btn btn-primary"><i class="fas fa-user-plus"></i> Đăng ký</a>
                
                </div>
            </div>
            <div class="cards">
                <div class="card">
                    <div class="card-icon"><i class="fas fa-users"></i></div>
                    <div class="card-title">Staff Management</div>
                    <div class="card-desc">Quản lý nhân viên, phân quyền, theo dõi hoạt động.</div>
                    <a href="#" class="card-link">Xem chi tiết</a>
                </div>
                <div class="card">
                    <div class="card-icon"><i class="fas fa-box"></i></div>
                    <div class="card-title">Product Management</div>
                    <div class="card-desc">Quản lý sản phẩm, nhập/xuất kho, cập nhật tồn kho.</div>
                    <a href="#" class="card-link">Xem chi tiết</a>
                </div>
                <div class="card">
                    <div class="card-icon"><i class="fas fa-shopping-cart"></i></div>
                    <div class="card-title">Order Management</div>
                    <div class="card-desc">Quản lý đơn hàng, trạng thái, lịch sử giao dịch.</div>
                    <a href="#" class="card-link">Xem chi tiết</a>
                </div>
                <div class="card">
                    <div class="card-icon"><i class="fas fa-bullhorn"></i></div>
                    <div class="card-title">Promotion Management</div>
                    <div class="card-desc">Quản lý chương trình khuyến mãi, ưu đãi, voucher.</div>
                    <a href="#" class="card-link">Xem chi tiết</a>
                </div>
                <div class="card">
                    <div class="card-icon"><i class="fas fa-exchange-alt"></i></div>
                    <div class="card-title">Import/Export Info</div>
                    <div class="card-desc">Theo dõi nhập/xuất kho, thông báo, xử lý yêu cầu.</div>
                    <a href="#" class="card-link">Xem chi tiết</a>
                </div>
                <div class="card">
                    <div class="card-icon"><i class="fas fa-chart-line"></i></div>
                    <div class="card-title">Activity Log & Revenue</div>
                    <div class="card-desc">Báo cáo doanh thu, nhật ký hoạt động, thống kê.</div>
                    <a href="#" class="card-link">Xem chi tiết</a>
                </div>
            </div>
        </main>
    </div>
</body>
</html>

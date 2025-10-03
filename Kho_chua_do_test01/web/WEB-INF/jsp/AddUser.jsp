<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>

        <form action="AddUser" method="post">
            <label>Họ tên:</label>
            <input type="text" name="fullName" required />

            <label>Username:</label>
            <input type="text" name="username" required />

            <label>Email:</label>
            <input type="email" name="email" />

            <label>Điện thoại:</label>
            <input type="text" name="phone" />

            <label>Chức vụ:</label>
            <select name="roleId">
                <option value="1">Admin</option>
                <option value="2">InventoryManager</option>
                <option value="3">StoreManager</option>
                <option value="4">Supplier</option>
                <option value="5">Salesperson</option>
            </select>

            <label>Phòng ban:</label>
            <select name="departmentId">
                <!-- load từ DB -->
            </select>

            <button type="submit">Thêm nhân viên</button>
        </form>


    </body>
</html>

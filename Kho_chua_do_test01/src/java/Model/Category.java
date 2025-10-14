package Model;

/**
 * Model tương ứng với bảng Categories
 * CREATE TABLE Categories (
 *     CategoryID INT PRIMARY KEY,
 *     CategoryName NVARCHAR(100)
 * );
 */
public class Category {
    private int categoryID;
    private String categoryName;

    // --- Constructors ---
    public Category() {
    }

    public Category(int categoryID, String categoryName) {
        this.categoryID = categoryID;
        this.categoryName = categoryName;
    }

    // --- Getters và Setters ---
    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    // --- toString() để debug ---
    @Override
    public String toString() {
        return "Category{" +
                "categoryID=" + categoryID +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}

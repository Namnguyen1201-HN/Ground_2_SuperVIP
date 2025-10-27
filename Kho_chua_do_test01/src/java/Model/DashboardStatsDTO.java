package Model;

public class DashboardStatsDTO {

    private double todayRevenue;
    private int returnCount;
    private double compareYesterday; // %
    private double compareLastMonth; // %
    
    // Thêm các thống kê mới
    private int todayOrders;      // Tổng đơn hàng hôm nay
    private int weekOrders;       // Tổng đơn hàng tuần này
    private int monthOrders;      // Tổng đơn hàng tháng này
    private double weekRevenue;   // Doanh thu tuần này
    private double monthRevenue;  // Doanh thu tháng này

    public DashboardStatsDTO() {
    }

    public DashboardStatsDTO(double todayRevenue, int returnCount, double compareYesterday, double compareLastMonth) {
        this.todayRevenue = todayRevenue;
        this.returnCount = returnCount;
        this.compareYesterday = compareYesterday;
        this.compareLastMonth = compareLastMonth;
    }

    public double getTodayRevenue() {
        return todayRevenue;
    }

    public void setTodayRevenue(double todayRevenue) {
        this.todayRevenue = todayRevenue;
    }

    public int getReturnCount() {
        return returnCount;
    }

    public void setReturnCount(int returnCount) {
        this.returnCount = returnCount;
    }

    public double getCompareYesterday() {
        return compareYesterday;
    }

    public void setCompareYesterday(double compareYesterday) {
        this.compareYesterday = compareYesterday;
    }

    public double getCompareLastMonth() {
        return compareLastMonth;
    }

    public void setCompareLastMonth(double compareLastMonth) {
        this.compareLastMonth = compareLastMonth;
    }

    public int getTodayOrders() {
        return todayOrders;
    }

    public void setTodayOrders(int todayOrders) {
        this.todayOrders = todayOrders;
    }

    public int getWeekOrders() {
        return weekOrders;
    }

    public void setWeekOrders(int weekOrders) {
        this.weekOrders = weekOrders;
    }

    public int getMonthOrders() {
        return monthOrders;
    }

    public void setMonthOrders(int monthOrders) {
        this.monthOrders = monthOrders;
    }

    public double getWeekRevenue() {
        return weekRevenue;
    }

    public void setWeekRevenue(double weekRevenue) {
        this.weekRevenue = weekRevenue;
    }

    public double getMonthRevenue() {
        return monthRevenue;
    }

    public void setMonthRevenue(double monthRevenue) {
        this.monthRevenue = monthRevenue;
    }
}

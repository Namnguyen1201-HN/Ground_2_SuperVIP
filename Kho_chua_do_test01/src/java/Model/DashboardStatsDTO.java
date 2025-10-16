package Model;

public class DashboardStatsDTO {

    private double todayRevenue;
    private int returnCount;
    private double compareYesterday; // %
    private double compareLastMonth; // %

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
}

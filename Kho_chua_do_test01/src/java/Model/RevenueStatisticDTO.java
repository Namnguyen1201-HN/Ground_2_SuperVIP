package Model;

import java.util.Date;

public class RevenueStatisticDTO {

    private String label;    // ngày / giờ / thứ
    private double totalRevenue;

    public RevenueStatisticDTO(String label, double totalRevenue) {
        this.label = label;
        this.totalRevenue = totalRevenue;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}

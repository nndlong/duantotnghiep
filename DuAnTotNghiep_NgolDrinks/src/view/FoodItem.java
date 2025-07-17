
package view;
import javax.swing.*;
public class FoodItem {
    private JPanel panel;
    private String tenMon;
    private int gia;
    private String anhMon;
    private JSpinner quantitySpinner; // Thêm biến lưu JSpinner

    public FoodItem(JPanel panel, String tenMon, int gia, String anhMon, JSpinner quantitySpinner) {
        this.panel = panel;
        this.tenMon = tenMon;
        this.gia = gia;
        this.anhMon = anhMon;
        this.quantitySpinner = quantitySpinner; // Lưu JSpinner vào món ăn
    }

    public JPanel getPanel() {
        return panel;
    }

    public String getTenMon() {
        return tenMon;
    }

    public int getGia() {
        return gia;
    }

    public String getAnhMon() {
        return anhMon;
    }

    public JSpinner getQuantitySpinner() { // Getter để lấy số lượng
        return quantitySpinner;
    }
}
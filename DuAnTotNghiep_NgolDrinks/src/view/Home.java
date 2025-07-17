package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;


public class Home extends javax.swing.JPanel {

    private List<ImageIcon> images; // Danh sách ảnh
    private JLabel imageLabel; // Label để hiển thị ảnh
    private Timer timer; // Timer để tự động chuyển ảnh
    private int currentImageIndex = 0; // Chỉ số ảnh hiện tại

    public Home() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Khởi tạo danh sách ảnh
        images = new ArrayList<>();
        images.add(new ImageIcon(getClass().getResource("/images/yfnOhvIRQTWXggitFmpBWA.jfif"))); // Thay đổi đường dẫn ảnh
        images.add(new ImageIcon(getClass().getResource("/images/NuxikQBcTOGp4YNEriEIpw.jpg"))); // Thay đổi đường dẫn ảnh
        images.add(new ImageIcon(getClass().getResource("/images/UEgFPqSMSJGMNHTF-LSyvA.jfif"))); // Thay đổi đường dẫn ảnh
        images.add(new ImageIcon(getClass().getResource("/images/sm-hqK0IRGWJ7apX1i51ug.jfif"))); // Thay đổi đường dẫn ảnh
        images.add(new ImageIcon(getClass().getResource("/images/ZqWQRHLMRWWcuWjpH39KsA.jfif"))); // Thay đổi đường dẫn ảnh
        images.add(new ImageIcon(getClass().getResource("/images/Qqkq1TGETs-6VI_hVTI8Ng.jfif"))); // Thay đổi đường dẫn ảnh
        images.add(new ImageIcon(getClass().getResource("/images/BRVN7Nk-RkCEYJEmEC0Bhg.jfif"))); // Thay đổi đường dẫn ảnh

        // Label để hiển thị ảnh lớn
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        add(imageLabel, BorderLayout.CENTER);

        // Thêm ComponentListener để cập nhật kích thước ảnh khi JPanel thay đổi kích thước
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateImage(); // Cập nhật ảnh khi kích thước JPanel thay đổi
            }
        });

        // Tạo Timer để tự động chuyển ảnh mỗi 3 giây
        timer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentImageIndex = (currentImageIndex + 1) % images.size(); // Chuyển sang ảnh tiếp theo
                updateImage(); // Cập nhật ảnh
            }
        });
        timer.start(); // Bắt đầu Timer
    }

private void updateImage() {
    if (images.isEmpty()) return; // Kiểm tra nếu danh sách ảnh rỗng

    ImageIcon icon = images.get(currentImageIndex);
    Image img = icon.getImage();

    // Lấy kích thước của JPanel (trang chủ)
    int panelWidth = getWidth();
    int panelHeight = getHeight();

    // Kiểm tra nếu kích thước JPanel hợp lệ
    if (panelWidth <= 0 || panelHeight <= 0) return;

    // Thay đổi kích thước ảnh để chiếm toàn bộ JPanel
    Image scaledImg = img.getScaledInstance(panelWidth, panelHeight, Image.SCALE_SMOOTH);
    imageLabel.setIcon(new ImageIcon(scaledImg));
}

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

// public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> new trangchu().setVisible(true));
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}

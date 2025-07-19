/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import Connect.DBConnection;
import dao.TaiKhoanDAO;
import model.TaiKhoan;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
/**
 *
 * @author Admin
 */
public class DangNhap extends javax.swing.JFrame {

    /**
     * Creates new form DangNhap
     */
    private JTextField txtEmail;
    private JPasswordField txtMatKhau;
    private JButton btnDangNhap, btnQuenMatKhau;

    public DangNhap() {
        setTitle("NgolDrinks - Đăng nhập");
        setSize(500, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Ảnh nền
        ImageIcon bgImage = new ImageIcon("src/view/banner.jpg"); // đổi tên file theo ảnh bạn
        JLabel lblBackground = new JLabel(bgImage);
        lblBackground.setBounds(0, 0, 500, 350);

        // Panel form
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(0, 0, 0, 150)); // nền đen trong suốt
        panel.setBounds(80, 50, 340, 220);
        panel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        JLabel lblTitle = new JLabel("NgolDrinks");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setBounds(100, 10, 200, 30);
        panel.add(lblTitle);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setForeground(Color.WHITE);
        lblEmail.setBounds(30, 50, 80, 25);
        panel.add(lblEmail);

        txtEmail = new JTextField();
        txtEmail.setBounds(110, 50, 200, 25);
        panel.add(txtEmail);

        JLabel lblMatKhau = new JLabel("Mật khẩu:");
        lblMatKhau.setForeground(Color.WHITE);
        lblMatKhau.setBounds(30, 90, 80, 25);
        panel.add(lblMatKhau);

        txtMatKhau = new JPasswordField();
        txtMatKhau.setBounds(110, 90, 200, 25);
        panel.add(txtMatKhau);

        btnDangNhap = new JButton("Đăng nhập");
        btnDangNhap.setBackground(new Color(52, 152, 219));
        btnDangNhap.setForeground(Color.WHITE);
        btnDangNhap.setBounds(110, 130, 200, 30);
        panel.add(btnDangNhap);

        btnQuenMatKhau = new JButton("Quên mật khẩu");
        btnQuenMatKhau.setBackground(Color.WHITE);
        btnQuenMatKhau.setForeground(new Color(52, 152, 219));
        btnQuenMatKhau.setBounds(110, 170, 200, 30);
        panel.add(btnQuenMatKhau);

        add(panel);
        add(lblBackground);

        // Sự kiện nút
        btnDangNhap.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dangNhap();
            }
        });

        btnQuenMatKhau.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ResetPasswordForm().setVisible(true);
            }
        });

        setLayout(null);
    }

    private void dangNhap() {
        String email = txtEmail.getText();
        String password = new String(txtMatKhau.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnect()) {
            String sql = "SELECT * FROM Taikhoan WHERE Email = ? AND Matkhau = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, email);
            stm.setString(2, password);
            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                // ✅ Nếu là admin
                if (email.equalsIgnoreCase("admin@gmail.com")) {
                    JOptionPane.showMessageDialog(null, "Đăng nhập thành công (Admin)");
                    new AdminForm().setVisible(true);
                } else {
                    // ✅ Người dùng thường
                    JOptionPane.showMessageDialog(null, "Đăng nhập thành công (User)");
                    new Home().setVisible(true); // hoặc UserForm bạn thiết kế
                }
                dispose(); // Đóng form đăng nhập
            } else {
                JOptionPane.showMessageDialog(null, "Sai tài khoản hoặc mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi kết nối!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }        
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DangNhap.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DangNhap.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DangNhap.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DangNhap.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DangNhap().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}

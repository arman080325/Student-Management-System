package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Random;
import com.toedter.calendar.JDateChooser;

public class InsertData1 extends JFrame implements ActionListener {

    JTextField textName, textfather, textAddress, textPhone, textemail, textM10, textM12, textAadhar;
    JLabel empText;
    JDateChooser cdob;
    JComboBox<String> courseBox, departmentBox;
    JButton submit, cancel;
    Random ran = new Random();
    Long f4 = Math.abs((ran.nextLong() % 9000L) + 1000L);

    InsertData1() {
        getContentPane().setBackground(new Color(186, 255, 249));
        setLayout(null);


        ImageIcon i1=new ImageIcon(ClassLoader.getSystemResource("icons/logos.png"));
        Image i2=i1.getImage().getScaledInstance(480,143,Image.SCALE_DEFAULT);
        ImageIcon i3=new ImageIcon(i2);
        JLabel image=new JLabel(i3);
        image.setBounds(350,10,480,143);
        add(image);


        JLabel heading = new JLabel("New Student Details");
        heading.setBounds(450, 190, 500, 50);
        heading.setFont(new Font("serif", Font.BOLD, 40));
        add(heading);

        addField("Name", 150, 300, textName = new JTextField());
        addField("Father Name", 650, 300, textfather = new JTextField());

        addLabel("Roll Number", 150, 370);
        empText = new JLabel("1409" + f4);
        empText.setBounds(370, 370, 200, 30);
        empText.setFont(new Font("serif", Font.BOLD, 25));
        add(empText);

        addLabel("Date of Birth", 650, 370);
        cdob = new JDateChooser();
        cdob.setBounds(800, 370, 250, 30);
        add(cdob);

        addField("Address", 150, 440, textAddress = new JTextField());
        addField("Phone", 650, 440, textPhone = new JTextField());
        addField("Email", 150, 510, textemail = new JTextField());
        addField("SIC", 650, 510, textM10 = new JTextField());
        addField("Class XII (%)", 150, 580, textM12 = new JTextField());
        addField("Aadhar Number", 650, 580, textAadhar = new JTextField());

        addLabel("Course", 150, 650);
        String[] course = {"B.Tech", "BBA", "BCA", "BSC", "MSC", "MBA", "MCA", "MCom", "MA", "BA"};
        courseBox = new JComboBox<>(course);
        courseBox.setBounds(300, 650, 250, 30);
        courseBox.setBackground(Color.WHITE);
        add(courseBox);

        addLabel("Branch", 650, 650);
        String[] department = {
                "Computer Science & Engineering(CSE)",
                "Computer Science & Technology(CST)",
                "Electronics & Communication Engineering(ECE)",
                "Electrical & Electronics Engineering(EEE)",
                "Electronics & Instrumentation Engineering(EIE)",
        };
        departmentBox = new JComboBox<>(department);
        departmentBox.setBounds(800, 650, 250, 30);
        departmentBox.setBackground(Color.WHITE);
        add(departmentBox);

        submit = new JButton("Submit");
        submit.setBounds(450, 720, 120, 30);
        submit.setBackground(Color.BLACK);
        submit.setForeground(Color.WHITE);
        submit.addActionListener(this);
        add(submit);

        cancel = new JButton("Cancel");
        cancel.setBounds(600, 720, 120, 30);
        cancel.setBackground(Color.BLACK);
        cancel.setForeground(Color.WHITE);
        cancel.addActionListener(this);
        add(cancel);

        // Hover effect for Submit button
        submit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                submit.setBackground(Color.GREEN);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                submit.setBackground(Color.BLACK);
            }
        });

        // Hover effect for Cancel button
        cancel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                cancel.setBackground(Color.RED);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                cancel.setBackground(Color.BLACK);
            }
        });

        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    void addLabel(String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, 200, 30);
        label.setFont(new Font("serif", Font.BOLD, 20));
        add(label);
    }

    void addField(String labelText, int x, int y, JTextField field) {
        addLabel(labelText, x, y);
        field.setBounds(x + 150, y, 250, 30);
        add(field);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submit) {
            String name = textName.getText();
            String fname = textfather.getText();
            String rollno = empText.getText();
            String dob = ((JTextField) cdob.getDateEditor().getUiComponent()).getText();
            String address = textAddress.getText();
            String phone = textPhone.getText();
            String email = textemail.getText();
            String sic = textM10.getText();
            String xii = textM12.getText();
            String aadhar = textAadhar.getText();
            String course = (String) courseBox.getSelectedItem();
            String department = (String) departmentBox.getSelectedItem();

            try {
                Class.forName("oracle.jdbc.driver.OracleDriver");
                Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "system", "arman0803");
                String q = "INSERT INTO student_data1 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = con.prepareStatement(q);

                pstmt.setString(1, name);
                pstmt.setString(2, fname);
                pstmt.setString(3, rollno);
                pstmt.setString(4, dob);
                pstmt.setString(5, address);
                pstmt.setString(6, phone);
                pstmt.setString(7, email);
                pstmt.setString(8, sic);
                pstmt.setString(9, xii);
                pstmt.setString(10, aadhar);
                pstmt.setString(11, course);
                pstmt.setString(12, department);

                pstmt.executeUpdate();
                con.close();

                JOptionPane.showMessageDialog(null, "Details Inserted Successfully");
                dispose(); // Close this frame
                new DisplayFrame(); // Show updated data
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            dispose();
            new DisplayFrame(); // Return to display even if cancel pressed
        }
    }

    public static void main(String[] args) {
        new InsertData1();
    }
}

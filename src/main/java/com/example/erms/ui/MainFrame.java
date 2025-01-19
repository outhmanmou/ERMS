package com.example.erms.ui;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.example.erms.service.AuditService;
import com.example.erms.service.EmployeeService;

import net.miginfocom.swing.MigLayout;

public class MainFrame extends JFrame {
    private final JPanel contentPanel;
    private final JTabbedPane tabbedPane;
    private final EmployeePanel employeePanel;
    private final SearchPanel searchPanel;
    private final ReportsPanel reportsPanel;
    private final EmployeeService employeeService;
    private final AuditService auditService;


    public MainFrame(EmployeeService employeeService, AuditService auditService) {
        this.employeeService = employeeService;
        this.auditService = auditService;
        setTitle("Employee Records Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1024, 768));

        // Initialize components
        contentPanel = new JPanel(new MigLayout("fill"));
        tabbedPane = new JTabbedPane();
        employeePanel = new EmployeePanel(employeeService);
        searchPanel = new SearchPanel(employeeService);
        reportsPanel = new ReportsPanel(employeeService, auditService);

        initComponents();
        setupLayout();
        
        // Center on screen
        pack();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        // Setup menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu helpMenu = new JMenu("Help");

        // File menu items
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> handleExit());
        fileMenu.add(exitItem);

        // Help menu items
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        // Add panels to tabbed pane
        tabbedPane.addTab("Employees", new ImageIcon(), employeePanel, "Manage Employees");
        tabbedPane.addTab("Search", new ImageIcon(), searchPanel, "Search Employees");
        tabbedPane.addTab("Reports", new ImageIcon(), reportsPanel, "Generate Reports");
    }

    private void setupLayout() {
        contentPanel.add(tabbedPane, "grow");
        add(contentPanel);
    }

    private void handleExit() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to exit?",
            "Exit Application",
            JOptionPane.YES_NO_OPTION
        );
        
        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(
            this,
            """
            Employee Records Management System
            Version 1.0.0
            
            A comprehensive solution for managing employee records.
            
            © 2024 Your Company
            """,
            "About",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
}
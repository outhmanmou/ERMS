package com.example.erms.ui;

import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXDatePicker;

import com.example.erms.dao.AuditLog;
import com.example.erms.dao.Employee;
import com.example.erms.service.AuditService;
import com.example.erms.service.EmployeeService;

import net.miginfocom.swing.MigLayout;

public class ReportsPanel extends JPanel {
    private final EmployeeService employeeService;
    private final AuditService auditService;
    private final JComboBox<String> reportType;
    private final JXDatePicker startDate;
    private final JXDatePicker endDate;
    private final JTextArea reportPreview;
    private final JTable auditTable;
    private final DefaultTableModel auditTableModel;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ReportsPanel(EmployeeService employeeService, AuditService auditService) {
        this.employeeService = employeeService;
        this.auditService = auditService;
        setLayout(new MigLayout("fill", "[grow]", "[]10[]"));

        // Initialize components
        reportType = new JComboBox<>(new String[]{
            "Employee Directory",
            "Department Summary",
            "Status Summary",
            "New Hires Report",
            "Employee Changes Log"
        });

        startDate = new JXDatePicker(new Date());
        endDate = new JXDatePicker(new Date());

        reportPreview = new JTextArea(20, 40);
        reportPreview.setEditable(false);
        reportPreview.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        // Initialize audit table
        String[] columns = {
            "Date", "Action", "Entity Type", "Entity ID", "Modified By", "Changes"
        };
        auditTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        auditTable = new JTable(auditTableModel);
        auditTable.getColumnModel().getColumn(5).setPreferredWidth(300);

        initComponents();
        loadInitialAuditLogs();
    }

    private void initComponents() {
        // Report Configuration Panel
        JPanel configPanel = new JPanel(new MigLayout("fillx", "[][grow]", "[][]"));
        configPanel.setBorder(BorderFactory.createTitledBorder("Report Configuration"));

        configPanel.add(new JLabel("Report Type:"));
        configPanel.add(reportType, "growx, wrap");

        configPanel.add(new JLabel("Date Range:"));
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        datePanel.add(startDate);
        datePanel.add(new JLabel("to"));
        datePanel.add(endDate);
        configPanel.add(datePanel, "wrap");

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton generateButton = new JButton("Generate Report");
        JButton exportButton = new JButton("Export Report");
        buttonPanel.add(generateButton);
        buttonPanel.add(exportButton);
        configPanel.add(buttonPanel, "span 2, align right");

        // Create tabbed pane for reports and audit logs
        JTabbedPane tabbedPane = new JTabbedPane();

        // Report Preview Panel
        JPanel previewPanel = new JPanel(new MigLayout("fill", "[grow]", "[grow]"));
        previewPanel.add(new JScrollPane(reportPreview), "grow");

        // Audit Logs Panel
        JPanel auditPanel = new JPanel(new MigLayout("fill", "[grow]", "[grow]"));
        auditPanel.add(new JScrollPane(auditTable), "grow");

        // Add panels to tabbed pane
        tabbedPane.addTab("Report Preview", previewPanel);
        tabbedPane.addTab("Audit Logs", auditPanel);

        // Add all components to main panel
        add(configPanel, "grow, wrap");
        add(tabbedPane, "grow");

        // Add action listeners
        generateButton.addActionListener(e -> generateReport());
        exportButton.addActionListener(e -> exportReport());

        // Add refresh button for audit logs
        JButton refreshAuditButton = new JButton("Refresh Audit Logs");
        refreshAuditButton.addActionListener(e -> loadInitialAuditLogs());
        buttonPanel.add(refreshAuditButton);
    }

    private void generateReport() {
        try {
            LocalDate start = startDate.getDate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate end = endDate.getDate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();

            String report = switch (reportType.getSelectedItem().toString()) {
                case "Employee Directory" -> generateEmployeeDirectory();
                case "Department Summary" -> generateDepartmentSummary();
                case "Status Summary" -> generateStatusSummary();
                case "New Hires Report" -> generateNewHiresReport(start, end);
                case "Employee Changes Log" -> generateChangesLog(start, end);
                default -> "Invalid report type selected";
            };

            reportPreview.setText(report);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error generating report: " + ex.getMessage(),
                "Report Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private String generateEmployeeDirectory() {
        List<Employee> employees = employeeService.findEmployees(null, null);
        StringBuilder report = new StringBuilder();
        report.append("EMPLOYEE DIRECTORY REPORT\n");
        report.append("Generated on: ").append(LocalDateTime.now().format(dateFormatter)).append("\n\n");
        report.append(String.format("%-40s %-20s %-15s %-20s\n", 
            "Name", "Department", "Status", "Email"));
        report.append("-".repeat(100)).append("\n");

        employees.forEach(emp -> 
            report.append(String.format("%-40s %-20s %-15s %-20s\n",
                emp.getFullName(),
                emp.getDepartment(),
                emp.getEmploymentStatus(),
                emp.getEmail()))
        );

        return report.toString();
    }

    private String generateDepartmentSummary() {
        List<Employee> employees = employeeService.findEmployees(null, null);
        Map<String, Long> departmentCounts = employees.stream()
            .collect(Collectors.groupingBy(Employee::getDepartment, Collectors.counting()));

        StringBuilder report = new StringBuilder();
        report.append("DEPARTMENT SUMMARY REPORT\n");
        report.append("Generated on: ").append(LocalDateTime.now().format(dateFormatter)).append("\n\n");
        report.append(String.format("%-20s %-10s\n", "Department", "Count"));
        report.append("-".repeat(35)).append("\n");

        departmentCounts.forEach((dept, count) ->
            report.append(String.format("%-20s %-10d\n", dept, count))
        );

        return report.toString();
    }

    private String generateStatusSummary() {
        List<Employee> employees = employeeService.findEmployees(null, null);
        Map<String, Long> statusCounts = employees.stream()
            .collect(Collectors.groupingBy(Employee::getEmploymentStatus, Collectors.counting()));

        StringBuilder report = new StringBuilder();
        report.append("EMPLOYMENT STATUS SUMMARY REPORT\n");
        report.append("Generated on: ").append(LocalDateTime.now().format(dateFormatter)).append("\n\n");
        report.append(String.format("%-15s %-10s\n", "Status", "Count"));
        report.append("-".repeat(30)).append("\n");

        statusCounts.forEach((status, count) ->
            report.append(String.format("%-15s %-10d\n", status, count))
        );

        return report.toString();
    }

    private String generateNewHiresReport(LocalDate startDate, LocalDate endDate) {
        List<Employee> employees = employeeService.findEmployees(null, null);
        List<Employee> newHires = employees.stream()
            .filter(emp -> !emp.getHireDate().isBefore(startDate) && !emp.getHireDate().isAfter(endDate))
            .collect(Collectors.toList());

        StringBuilder report = new StringBuilder();
        report.append("NEW HIRES REPORT\n");
        report.append("Period: ").append(startDate).append(" to ").append(endDate).append("\n");
        report.append("Generated on: ").append(LocalDateTime.now().format(dateFormatter)).append("\n\n");
        report.append(String.format("%-30s %-20s %-15s %-15s\n", 
            "Name", "Department", "Position", "Hire Date"));
        report.append("-".repeat(85)).append("\n");

        newHires.forEach(emp ->
            report.append(String.format("%-30s %-20s %-15s %-15s\n",
                emp.getFullName(),
                emp.getDepartment(),
                emp.getJobTitle(),
                emp.getHireDate()))
        );

        return report.toString();
    }

    private String generateChangesLog(LocalDate startDate, LocalDate endDate) {
        List<AuditLog> auditLogs = auditService.getAuditLogsByDateRange(startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        
        StringBuilder report = new StringBuilder();
        report.append("EMPLOYEE CHANGES LOG\n");
        report.append("Period: ").append(startDate).append(" to ").append(endDate).append("\n");
        report.append("Generated on: ").append(LocalDateTime.now().format(dateFormatter)).append("\n\n");
        report.append(String.format("%-20s %-10s %-20s %-20s %-30s\n",
            "Date", "Action", "Entity Type", "Modified By", "Changes"));
        report.append("-".repeat(100)).append("\n");

        auditLogs.forEach(log ->
            report.append(String.format("%-20s %-10s %-20s %-20s %-30s\n",
                log.getModifiedAt().format(dateFormatter),
                log.getAction(),
                log.getEntityType(),
                log.getModifiedBy(),
                log.getChanges()))
        );

        return report.toString();
    }

    private void loadInitialAuditLogs() {
        try {
            // Clear existing rows
            while (auditTableModel.getRowCount() > 0) {
                auditTableModel.removeRow(0);
            }

            // Load last 50 audit logs
            List<AuditLog> auditLogs = auditService.getAuditLogs(null , null , null);
            
            for (AuditLog log : auditLogs) {
                auditTableModel.addRow(new Object[]{
                    log.getModifiedAt().format(dateFormatter),
                    log.getAction(),
                    log.getEntityType(),
                    log.getEntityId(),
                    log.getModifiedBy(),
                    log.getChanges()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error loading audit logs: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void exportReport() {
        if (reportPreview.getText().isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Please generate a report first.",
                "Export",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Report");
        fileChooser.setSelectedFile(new File("report.txt"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                java.nio.file.Files.writeString(file.toPath(), reportPreview.getText());
                JOptionPane.showMessageDialog(
                    this,
                    "Report exported successfully to: " + file.getAbsolutePath(),
                    "Export Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Error exporting report: " + ex.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}
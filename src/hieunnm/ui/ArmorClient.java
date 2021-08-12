/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hieunnm.ui;

import hieunnm.dtos.ArmorDTO;
import hieunnm.rmi.ArmorInterface;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.table.DefaultTableModel;

public class ArmorClient extends javax.swing.JFrame {

    private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";
    private final String HOST = "localhost";
    private final int PORT = 12340;
    private Registry registry;
    private final DefaultTableModel model;

    private ArmorInterface service;

    private void initArmorService() {
        try {
            registry = LocateRegistry.getRegistry(HOST, PORT);
            service = (ArmorInterface) registry.lookup(ArmorInterface.class.getSimpleName());
        } catch (NotBoundException | RemoteException e) {
            JOptionPane.showMessageDialog(this, "Could not connect to server. Please start your server first.");
            System.exit(0);
        }
    }

    public ArmorClient() {
        initArmorService();
        initComponents();
        this.setTitle("Armor Management");
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        model = (DefaultTableModel) tblArmor.getModel();
    }

    private ArmorDTO getArmorDTO() throws NumberFormatException {
        ArmorDTO dto = new ArmorDTO();
        dto.setId(txtArmorID.getText().trim());
        dto.setClassification(txtClassification.getText().trim());
        dto.setDefense(Integer.parseInt(txtDefense.getText()));
        dto.setStatus(txtStatus.getText().trim());
        dto.setDescription(txtDescription.getText().trim());
        dto.setTimeOfCreate(new Date(System.currentTimeMillis()));
        return dto;
    }

    private void updateArmorToServer() {
        if (txtArmorID.isEditable()) {
            JOptionPane.showMessageDialog(this, "Please select an armor before updating.");
            return;
        }
        try {
            ArmorDTO armor = getArmorDTO();
            if (validateArmor(armor.getId(), armor.getClassification(),
                    armor.getDescription(), armor.getDefense())) {
                boolean check = service.updateArmor(armor);
                if (check) {
                    JOptionPane.showMessageDialog(this, "Update armor successfully");
                } else {
                    JOptionPane.showMessageDialog(this, "Update armor failed.");
                }
                getAllArmor();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Armor defense must be an integer number.");
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void findByArmorID(String id) {
        if (id == null) {
            id = txtArmorID.getText().trim();
            if (!validateArmorID(id)) {
                return;
            }
        }

        try {
            ArmorDTO dto = service.findByArmorID(id);
            if (dto != null) {
                txtArmorID.setEditable(false);
                txtArmorID.setText(dto.getId());
                txtClassification.setText(dto.getClassification());
                txtDefense.setText(String.valueOf(dto.getDefense()));
                txtTimeOfCreate.setText(new SimpleDateFormat(DATE_FORMAT).format(dto.getTimeOfCreate()));
                txtDescription.setText(dto.getDescription());
                txtStatus.setText(dto.getStatus());
            } else {
                JOptionPane.showMessageDialog(this, "Armor not found");
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void getAllArmor() {
        List<ArmorDTO> list;

        try {
            list = service.showAllArmor();
            model.setRowCount(0);
            for (int i = 0; i < list.size(); i++) {
                ArmorDTO armor = list.get(i);
                model.addRow(new Object[]{armor.getId(), armor.getClassification(),
                    new SimpleDateFormat(DATE_FORMAT).format(armor.getTimeOfCreate()),
                    armor.getDefense()});
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }

    }

    private boolean validateArmor(String armorId, String classification, String des, int def) {
        return validateArmorID(armorId) && validateClassification(classification)
                && validateDefense(def) && validateDescription(des) && validateStatus(des);
    }

    private boolean validateArmorID(String armorId) {
        if (!armorId.matches("[A-za-z0-9\\s\\w+]{1,10}") || armorId.contains(",")) {
            JOptionPane.showMessageDialog(this, "Invalid armor ID (Max length is 10, not contains special characters (Comma char, @, #, $,...))");
            return false;
        }
        return true;
    }

    private boolean validateClassification(String classification) {
        if (!classification.matches("[A-za-z0-9\\s]{1,30}")) {
            JOptionPane.showMessageDialog(this, "Invalid classification (Max length is 30)");
            return false;
        }
        return true;
    }

    private boolean validateDescription(String des) {
        if (!des.matches("[A-za-z0-9\\s]{1,300}")) {
            JOptionPane.showMessageDialog(this, "Invalid description (Max length is 300)");
            return false;
        }
        return true;
    }

    private boolean validateDefense(int defense) {
        if (defense < 1) {
            JOptionPane.showMessageDialog(this, "Defense is an integer more than 0");
            return false;
        }
        return true;
    }

    private boolean validateStatus(String status) {
        if (status.contains(",")) {
            JOptionPane.showMessageDialog(this, "Comma char is not allowed");
            return false;
        }
        return true;
    }

    private void clearArmorInputFields() {
        txtArmorID.setEditable(true);
        txtArmorID.setText("");
        txtClassification.setText("");
        txtDefense.setText("");
        txtDescription.setText("");
        txtTimeOfCreate.setText("");
        txtStatus.setText("");

    }

    private void insertArmorToServer() {
        try {
            ArmorDTO dto = getArmorDTO();
            if (validateArmor(dto.getId(), dto.getClassification(),
                    dto.getDescription(), dto.getDefense())) {
                if (service.createArmor(dto)) {
                    JOptionPane.showMessageDialog(this, "Successfully inserted a new armor.");
                    clearArmorInputFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to insert a new armor. Armor ID has existed");
                }
                getAllArmor();
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Only accept defense as integer number.");
        }
    }

    private void removeArmorFromServer() {
        if (txtArmorID.isEditable()) {
            JOptionPane.showMessageDialog(this, "Please select an armor to delete.");
            return;
        }
        String id = txtArmorID.getText().trim();
        if (validateArmorID(id)) {
            int choice = JOptionPane.showConfirmDialog(this, "Are you sure want to delete this armor ?",
                    "Confirm delete", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (choice == 0) {
                try {
                    if (service.removeArmor(id)) {
                        JOptionPane.showMessageDialog(this, "Successfully deleted armor.");
                        getAllArmor();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete armor.");
                    }
                } catch (RemoteException e) {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                }
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            javax.swing.UIManager.setLookAndFeel(new MetalLookAndFeel());
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(ArmorClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ArmorClient().setVisible(true);
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lblArmorID = new javax.swing.JLabel();
        lblClassification = new javax.swing.JLabel();
        txtClassification = new javax.swing.JTextField();
        lblTimeOfCreate = new javax.swing.JLabel();
        lblDefense = new javax.swing.JLabel();
        lblDescription = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        txtTimeOfCreate = new javax.swing.JTextField();
        txtDefense = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextArea();
        txtStatus = new javax.swing.JTextField();
        btnCreate = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        btnFindByArmorID = new javax.swing.JButton();
        txtArmorID = new javax.swing.JTextField();
        btnClear = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblArmor = new javax.swing.JTable();
        btnGetAll = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Armor's Detail:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14), new java.awt.Color(255, 51, 255))); // NOI18N

        lblArmorID.setText("Armor ID:");

        lblClassification.setText("Classification:");

        lblTimeOfCreate.setText("TimeOfCreate:");

        lblDefense.setText("Defense:");

        lblDescription.setText("Description:");

        lblStatus.setText("Status:");

        txtTimeOfCreate.setEnabled(false);

        txtDescription.setColumns(20);
        txtDescription.setRows(5);
        jScrollPane1.setViewportView(txtDescription);

        btnCreate.setText("Create");
        btnCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateActionPerformed(evt);
            }
        });

        btnUpdate.setText("Update");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnRemove.setText("Remove");
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });

        btnFindByArmorID.setText("Find ID");
        btnFindByArmorID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFindByArmorIDActionPerformed(evt);
            }
        });

        btnClear.setText("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(btnCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(62, 62, 62)
                        .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(43, 43, 43)
                        .addComponent(btnRemove, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(lblClassification))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblStatus)
                                    .addComponent(lblDefense)
                                    .addComponent(lblDescription))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtClassification)
                            .addComponent(txtArmorID)
                            .addComponent(txtDefense)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                            .addComponent(txtStatus)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblArmorID)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblTimeOfCreate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtTimeOfCreate)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnClear, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                    .addComponent(btnFindByArmorID, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnFindByArmorID)
                    .addComponent(lblArmorID)
                    .addComponent(txtArmorID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblClassification)
                    .addComponent(txtClassification, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTimeOfCreate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTimeOfCreate))
                .addGap(13, 13, 13)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDefense, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDefense, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDescription)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStatus)
                    .addComponent(btnClear))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnUpdate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnCreate)
                        .addComponent(btnRemove)))
                .addGap(37, 37, 37))
        );

        tblArmor.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Classification", "TimeOfCreate", "Defense"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblArmor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tblArmorMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblArmorMouseReleased(evt);
            }
        });
        jScrollPane2.setViewportView(tblArmor);

        btnGetAll.setText("Get all");
        btnGetAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGetAllActionPerformed(evt);
            }
        });

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 204, 255));
        jLabel1.setText("Armor Client");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(104, 104, 104)
                        .addComponent(btnGetAll, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(366, 366, 366)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnGetAll)
                        .addGap(62, 62, 62))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateActionPerformed
        insertArmorToServer();
    }//GEN-LAST:event_btnCreateActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        updateArmorToServer();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnFindByArmorIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFindByArmorIDActionPerformed
        findByArmorID(null);

    }//GEN-LAST:event_btnFindByArmorIDActionPerformed

    private void btnGetAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGetAllActionPerformed
        getAllArmor();
    }//GEN-LAST:event_btnGetAllActionPerformed

    private void tblArmorMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblArmorMousePressed
        int index = tblArmor.getSelectedRow();
        String armorId = String.valueOf(model.getValueAt(index, 0));
        findByArmorID(armorId);
    }//GEN-LAST:event_tblArmorMousePressed

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
        removeArmorFromServer();
    }//GEN-LAST:event_btnRemoveActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
        clearArmorInputFields();

    }//GEN-LAST:event_btnClearActionPerformed

    private void tblArmorMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblArmorMouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_tblArmorMouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnCreate;
    private javax.swing.JButton btnFindByArmorID;
    private javax.swing.JButton btnGetAll;
    private javax.swing.JButton btnRemove;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblArmorID;
    private javax.swing.JLabel lblClassification;
    private javax.swing.JLabel lblDefense;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblTimeOfCreate;
    private javax.swing.JTable tblArmor;
    private javax.swing.JTextField txtArmorID;
    private javax.swing.JTextField txtClassification;
    private javax.swing.JTextField txtDefense;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextField txtStatus;
    private javax.swing.JTextField txtTimeOfCreate;
    // End of variables declaration//GEN-END:variables
}

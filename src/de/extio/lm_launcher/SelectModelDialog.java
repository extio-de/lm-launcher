package de.extio.lm_launcher;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

public class SelectModelDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;
	
	private final JPanel contentPanel = new JPanel();
	
	JScrollPane scrollPane;
	
	JList list;
	
	DefaultListModel<String> listModel;
	
	/**
	 * Create the dialog.
	 */
	public SelectModelDialog(final Frame parent, final Consumer<String> modelPathConsumer) {
		super(parent);
		this.setTitle("Select Model");
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setModal(true);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		this.setBounds(100, 100, 578, 251);
		this.getContentPane().setLayout(new BorderLayout());
		this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.getContentPane().add(this.contentPanel, BorderLayout.CENTER);
		this.contentPanel.setLayout(new BorderLayout(0, 0));
		{
			this.scrollPane = new JScrollPane();
			this.contentPanel.add(this.scrollPane, BorderLayout.CENTER);
			{
				this.listModel = new DefaultListModel<>();
				this.list = new JList(this.listModel);
				this.list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				this.scrollPane.setViewportView(this.list);
			}
		}
		{
			final JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			this.getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				final JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(final ActionEvent e) {
						if (SelectModelDialog.this.list.getSelectedIndex() == -1) {
							JOptionPane.showMessageDialog(null, "Error: Select a model first", "Error Message", JOptionPane.ERROR_MESSAGE);
							return;
						}
						SelectModelDialog.this.setVisible(false);
						modelPathConsumer.accept(SelectModelDialog.this.listModel.getElementAt(SelectModelDialog.this.list.getSelectedIndex()));
						SelectModelDialog.this.dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				this.getRootPane().setDefaultButton(okButton);
			}
			{
				final JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(final ActionEvent e) {
						SelectModelDialog.this.dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		this.initialize();
	}
	
	private void initialize() {
		Data.scanModels().forEach(this.listModel::addElement);
	}
	
}

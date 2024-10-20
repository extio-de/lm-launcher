package de.extio.lm_launcher;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

public class ModelPropertiesDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;
	
	private final JPanel contentPanel = new JPanel();
	
	JLabel lblName;
	
	JLabel lblModelname;
	
	JLabel lblMaxContextLength;
	
	JTextField textField;
	
	JLabel lblGpuLayers;
	
	JTextField textField_1;
	
	JLabel lblThreads;
	
	JTextField textField_2;
	JLabel lblContextLength;
	JTextField textField_3;
	JLabel lblPromptTemplate;
	JTextField txtPrompttemplate;
	
	/**
	 * Create the dialog.
	 */
	public ModelPropertiesDialog(final Frame parent, final String modelPath, final Model editModel, final Consumer<Model> modelConsumer) {
		super(parent);
		this.setTitle("Model properties");
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setModal(true);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		this.setBounds(100, 100, 678, 191);
		this.getContentPane().setLayout(new BorderLayout());
		this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.getContentPane().add(this.contentPanel, BorderLayout.CENTER);
		this.contentPanel.setLayout(new GridLayout(0, 2, 0, 0));
		{
			this.lblName = new JLabel("Name");
			this.contentPanel.add(this.lblName);
		}
		{
			this.lblModelname = new JLabel("ModelName");
			this.contentPanel.add(this.lblModelname);
		}
		{
			this.lblContextLength = new JLabel("Context Length");
			contentPanel.add(this.lblContextLength);
		}
		{
			this.textField_3 = new JTextField();
			this.textField_3.setText("16000");
			contentPanel.add(this.textField_3);
			this.textField_3.setColumns(10);
		}
		{
			this.lblMaxContextLength = new JLabel("Max Context Length");
			this.contentPanel.add(this.lblMaxContextLength);
		}
		{
			this.textField = new JTextField();
			this.textField.setText("128000");
			this.contentPanel.add(this.textField);
			this.textField.setColumns(10);
		}
		{
			this.lblThreads = new JLabel("Threads");
			this.contentPanel.add(this.lblThreads);
		}
		{
			this.textField_2 = new JTextField();
			this.textField_2.setText("8");
			this.contentPanel.add(this.textField_2);
			this.textField_2.setColumns(10);
		}
		{
			this.lblGpuLayers = new JLabel("GPU Layers");
			this.contentPanel.add(this.lblGpuLayers);
		}
		{
			this.textField_1 = new JTextField();
			this.textField_1.setText("99");
			this.contentPanel.add(this.textField_1);
			this.textField_1.setColumns(10);
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
						ModelPropertiesDialog.this.setVisible(false);
						modelConsumer.accept(
								new Model(modelPath,
										Integer.parseInt(ModelPropertiesDialog.this.textField_3.getText()),
										Integer.parseInt(ModelPropertiesDialog.this.textField.getText()),
										Integer.parseInt(ModelPropertiesDialog.this.textField_1.getText()),
										Integer.parseInt(ModelPropertiesDialog.this.textField_2.getText()),
										ModelPropertiesDialog.this.txtPrompttemplate.getText()));
						ModelPropertiesDialog.this.dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				this.getRootPane().setDefaultButton(okButton);
			}
		}
		
		this.lblModelname.setText(Path.of(modelPath).getFileName().toString());
		{
			this.lblPromptTemplate = new JLabel("Prompt Template");
			contentPanel.add(this.lblPromptTemplate);
		}
		{
			this.txtPrompttemplate = new JTextField();
			contentPanel.add(this.txtPrompttemplate);
			this.txtPrompttemplate.setColumns(10);
		}
		if (editModel != null) {
			this.textField.setText(String.valueOf(editModel.maxContextSize()));
			this.textField_1.setText(String.valueOf(editModel.gpuLayers()));
			this.textField_2.setText(String.valueOf(editModel.threads()));
			this.textField_3.setText(String.valueOf(editModel.contextSize()));
			this.txtPrompttemplate.setText(editModel.promptTemplate());
		}
	}
	
}

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
import javax.swing.JOptionPane;
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
	JLabel lblTemperature;
	JTextField txtTemperature;
	JLabel lblTopP;
	JTextField txtTopP;
	JLabel lblTopK;
	JTextField txtTopK;
	JLabel lblMinP;
	JTextField txtMinP;
	
	/**
	 * Create the dialog.
	 */
	public ModelPropertiesDialog(final Frame parent, final String modelPath, final Model editModel, final Consumer<Model> modelConsumer) {
		super(parent);
		final Model modelToEdit = editModel != null ? editModel : Data.defaultModel(modelPath);
		this.setTitle("Model properties");
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setModal(true);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		this.setBounds(UIScaler.scale(100), UIScaler.scale(100), UIScaler.scale(678), UIScaler.scale(320));
		this.getContentPane().setLayout(new BorderLayout());
		this.contentPanel.setBorder(new EmptyBorder(UIScaler.scale(5), UIScaler.scale(5), UIScaler.scale(5), UIScaler.scale(5)));
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
			this.textField_2.setText("6");
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
			this.lblTemperature = new JLabel("Temperature");
			this.contentPanel.add(this.lblTemperature);
		}
		{
			this.txtTemperature = new JTextField();
			this.txtTemperature.setText(Model.formatDecimal(Model.DEFAULT_TEMPERATURE));
			this.contentPanel.add(this.txtTemperature);
			this.txtTemperature.setColumns(10);
		}
		{
			this.lblTopP = new JLabel("Top P");
			this.contentPanel.add(this.lblTopP);
		}
		{
			this.txtTopP = new JTextField();
			this.txtTopP.setText(Model.formatDecimal(Model.DEFAULT_TOP_P));
			this.contentPanel.add(this.txtTopP);
			this.txtTopP.setColumns(10);
		}
		{
			this.lblTopK = new JLabel("Top K");
			this.contentPanel.add(this.lblTopK);
		}
		{
			this.txtTopK = new JTextField();
			this.txtTopK.setText(String.valueOf(Model.DEFAULT_TOP_K));
			this.contentPanel.add(this.txtTopK);
			this.txtTopK.setColumns(10);
		}
		{
			this.lblMinP = new JLabel("Min P");
			this.contentPanel.add(this.lblMinP);
		}
		{
			this.txtMinP = new JTextField();
			this.txtMinP.setText(Model.formatDecimal(Model.DEFAULT_MIN_P));
			this.contentPanel.add(this.txtMinP);
			this.txtMinP.setColumns(10);
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
						try {
							final int contextSize = ModelPropertiesDialog.clampInt(ModelPropertiesDialog.parseIntField(ModelPropertiesDialog.this.textField_3, "Context Length"), 1,
									Integer.MAX_VALUE);
							final int maxContextSize = ModelPropertiesDialog.clampInt(ModelPropertiesDialog.parseIntField(ModelPropertiesDialog.this.textField, "Max Context Length"), 1,
									Integer.MAX_VALUE);
							final int gpuLayers = Math.max(0, ModelPropertiesDialog.parseIntField(ModelPropertiesDialog.this.textField_1, "GPU Layers"));
							final int threads = Math.max(0, ModelPropertiesDialog.parseIntField(ModelPropertiesDialog.this.textField_2, "Threads"));
							final double temperature = ModelPropertiesDialog.clampDouble(ModelPropertiesDialog.parseDoubleField(ModelPropertiesDialog.this.txtTemperature, "Temperature"),
									Model.MIN_TEMPERATURE, Model.MAX_TEMPERATURE);
							final double topP = ModelPropertiesDialog.clampDouble(ModelPropertiesDialog.parseDoubleField(ModelPropertiesDialog.this.txtTopP, "Top P"), Model.MIN_TOP_P,
									Model.MAX_TOP_P);
							final int topK = ModelPropertiesDialog.clampInt(ModelPropertiesDialog.parseIntField(ModelPropertiesDialog.this.txtTopK, "Top K"), Model.MIN_TOP_K,
									Model.MAX_TOP_K);
							final double minP = ModelPropertiesDialog.clampDouble(ModelPropertiesDialog.parseDoubleField(ModelPropertiesDialog.this.txtMinP, "Min P"), Model.MIN_MIN_P,
									Model.MAX_MIN_P);
							ModelPropertiesDialog.this.setVisible(false);
							modelConsumer.accept(
									new Model(modelPath, contextSize, maxContextSize, gpuLayers, threads, temperature, topP, topK, minP, ModelPropertiesDialog.this.txtPrompttemplate.getText(),
											modelToEdit.ctime()));
							ModelPropertiesDialog.this.dispose();
						}
						catch (final NumberFormatException nfe) {
							JOptionPane.showMessageDialog(ModelPropertiesDialog.this, nfe.getMessage(), "Invalid model property", JOptionPane.WARNING_MESSAGE);
						}
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
		this.textField.setText(String.valueOf(modelToEdit.maxContextSize()));
		this.textField_1.setText(String.valueOf(modelToEdit.gpuLayers()));
		this.textField_2.setText(String.valueOf(modelToEdit.threads()));
		this.textField_3.setText(String.valueOf(modelToEdit.contextSize()));
		this.txtTemperature.setText(modelToEdit.temperatureDisplay());
		this.txtTopP.setText(modelToEdit.topPDisplay());
		this.txtTopK.setText(modelToEdit.topKDisplay());
		this.txtMinP.setText(modelToEdit.minPDisplay());
		this.txtPrompttemplate.setText(modelToEdit.promptTemplate());
		
		// Apply font scaling to all components
		UIScaler.scaleComponentFonts(this);
	}
	
	private static int parseIntField(final JTextField field, final String fieldName) {
		try {
			return Integer.parseInt(field.getText().trim());
		}
		catch (final NumberFormatException nfe) {
			throw new NumberFormatException(fieldName + " must be a whole number.");
		}
	}
	
	private static double parseDoubleField(final JTextField field, final String fieldName) {
		try {
			return Double.parseDouble(field.getText().trim());
		}
		catch (final NumberFormatException nfe) {
			throw new NumberFormatException(fieldName + " must be a decimal number.");
		}
	}
	
	private static int clampInt(final int value, final int minValue, final int maxValue) {
		return Math.max(minValue, Math.min(maxValue, value));
	}
	
	private static double clampDouble(final double value, final double minValue, final double maxValue) {
		return Math.max(minValue, Math.min(maxValue, value));
	}
	
}
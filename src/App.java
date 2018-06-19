import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.text.DefaultEditorKit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import file_edit.Configs;
import file_edit.Controllers;
import file_edit.Models;
import file_edit.Replace;
import file_edit.Views;
import object.ItemAttribute;
import utils.Utils;

public class App extends JFrame {
	private static final long serialVersionUID = 1L;
	private CardLayout cl = new CardLayout();
	private JPanel mainPanel = new JPanel(cl);
	private JPanel attributeCenterPanel = new JPanel(new GridBagLayout());
	private JPanel aboutCenterPanel = new JPanel(new GridBagLayout());

	// Fields for the config panel
	private JLabel configHeaderLabel;
	private JTextField siteField = new JTextField("");
	private JTextField authorField = new JTextField("");
	private JTextField itemField = new JTextField("");
	private JTextField nbField = new JTextField("");
	private JTextField databaseField = new JTextField("");
	private JTextField[] categoryFields;
	private String[] categories = new String[] {};
	private JCheckBox checkbox = new JCheckBox("Item type?");

	// Fields for the attribute panel
	private JLabel attributeHeaderLabel;
	private JLabel fieldHeader = new JLabel("Field name", JLabel.CENTER);
	private JLabel tooltipHeader = new JLabel("Tooltip", JLabel.CENTER);
	private JLabel searchHeader = new JLabel("Strict", JLabel.CENTER);
	private JLabel typeHeader = new JLabel("Type of data", JLabel.CENTER);

	// Arrays for the fields
	private JTextField[] columns;
	private JComboBox<String>[] boxes;
	private JTextField[] tooltips;
	private JCheckBox[] filters;

	// Combobox values
	private String[] attributeTypes = new String[] { "string", "url", "float", "boolean", "reference", "comment", "pdf", "image" };
	private String[] attributeTypesAlt = new String[] { "string", "url", "number"};

	private JTextArea aboutField = new JTextArea("");

	public App() {
		setSize(800, 800);
		setLayout(new BorderLayout());
		prepareGUI();
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					InputMap im = (InputMap) UIManager.get("Button.focusInputMap");
					im.put(KeyStroke.getKeyStroke("ENTER"), "pressed");
					im.put(KeyStroke.getKeyStroke("released ENTER"), "released");

					break;
				}
			}
		} catch (Exception ex) {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception ex1) {
				ex1.printStackTrace();
			}
		}
		App appInstance = new App();
		appInstance.setVisible(true);
	}

	private void prepareGUI() {
		// Initial screen with general information of the website
		setTitle("De Duve Website Code Generator");
		
		
		

		configHeaderLabel = new JLabel(" ", JLabel.CENTER);
		attributeHeaderLabel = new JLabel(" ", JLabel.CENTER);

		getContentPane().add(mainPanel, BorderLayout.CENTER);

		JPanel configPanel = new JPanel(new BorderLayout());
		JPanel configCenterPanel = new JPanel(new GridBagLayout());
		JPanel configButtonPanel = new JPanel();

		configButtonPanel.setLayout(new FlowLayout());
		JScrollPane configScrollPane = new JScrollPane(configCenterPanel);
		configScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		configScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		configPanel.add(configScrollPane, BorderLayout.CENTER);
		configPanel.add(configButtonPanel, BorderLayout.SOUTH);

		JPanel attributePanel = new JPanel(new BorderLayout());

		JPanel attributeButtonPanel = new JPanel();
		attributeButtonPanel.setLayout(new FlowLayout());

		JScrollPane attributeScrollPane = new JScrollPane(attributeCenterPanel);
		attributeScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		attributeScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		attributePanel.add(attributeScrollPane, BorderLayout.CENTER);
		attributePanel.add(attributeButtonPanel, BorderLayout.SOUTH);

		JPanel aboutPanel = new JPanel(new BorderLayout());

		JPanel aboutButtonPanel = new JPanel();
		aboutButtonPanel.setLayout(new FlowLayout());

		JScrollPane aboutScrollPane = new JScrollPane(aboutCenterPanel);
		aboutScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		aboutScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		aboutPanel.add(aboutScrollPane, BorderLayout.CENTER);
		aboutPanel.add(aboutButtonPanel, BorderLayout.SOUTH);

		configHeaderLabel.setText("");
		configHeaderLabel.setPreferredSize(new Dimension(100, 20));

		attributeHeaderLabel.setText("");
		attributeHeaderLabel.setPreferredSize(new Dimension(100, 20));

		JLabel siteLabel = new JLabel("Site Name");
		JLabel authorLabel = new JLabel("Author");
		JLabel itemLabel = new JLabel("Item listed in the table");
		JLabel nbLabel = new JLabel("Number of columns");
		JLabel databaseLabel = new JLabel("Name of the database");

		JButton nextButton = new JButton("Next");
		JButton clearButton = new JButton("Clear");

		clearButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				configHeaderLabel.setText("");
				authorField.setText("");
				siteField.setText("");
				itemField.setText("");
				nbField.setText("");
				databaseField.setText("");
				categoryFields[0].setText("");
				categoryFields[1].setText("");
				categoryFields[2].setText("");
				categoryFields[3].setText("");
				categoryFields[4].setText("");
				categoryFields[5].setText("");
				categoryFields[6].setText("");
			}
		});

		nextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!checkFields()) {
					configHeaderLabel.setText("Please fill all the fields.");
					configHeaderLabel.setForeground(Color.red);
				} else if (!StringUtils.isNumeric(nbField.getText())) {
					configHeaderLabel.setText("You must type a number for the column number.");
					configHeaderLabel.setForeground(Color.red);
				} else if (!checkCategories()) {
					configHeaderLabel.setText("Put at least 2 types for the item or uncheck item type checkbox");
					configHeaderLabel.setForeground(Color.red);
				} else {
					showAttribute();
				}
			}
		});

		configButtonPanel.add(clearButton);
		configButtonPanel.add(nextButton);

		JButton nextButton2 = new JButton("Next");
		nextButton2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!checkColumns()) {
					attributeHeaderLabel.setText("Please fill all the fields.");
					attributeHeaderLabel.setForeground(Color.red);
				} else {
					textAbout();
				}

			}
		});

		JButton previousButton1 = new JButton("Previous");
		previousButton1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cl.show(mainPanel, "CONFIG");
			}
		});

		attributeButtonPanel.add(previousButton1);
		attributeButtonPanel.add(nextButton2);

		JButton finishButton = new JButton("Finish");
		finishButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				generate();
			}
		});

		JButton previousButton2 = new JButton("Previous");
		previousButton2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cl.show(mainPanel, "ATTRIBUTES");
			}
		});

		aboutButtonPanel.add(previousButton2);
		aboutButtonPanel.add(finishButton);

		categoryFields = new JTextField[7];
		for (int cf = 0; cf < 7; cf++) {
			categoryFields[cf] = new JTextField("");
		}

		int y = 0;
		configCenterPanel.add(configHeaderLabel, new GridBagConstraints(0, y++, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 10, 5, 10), 0, 0));
		configCenterPanel.add(siteLabel, new GridBagConstraints(0, y++, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 10, 5, 10), 0, 0));
		configCenterPanel.add(siteField, new GridBagConstraints(0, y++, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 10, 5, 10), 0, 0));
		configCenterPanel.add(authorLabel, new GridBagConstraints(0, y++, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 10, 5, 10), 0, 0));
		configCenterPanel.add(authorField, new GridBagConstraints(0, y++, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 10, 5, 10), 0, 0));
		configCenterPanel.add(itemLabel, new GridBagConstraints(0, y++, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 10, 5, 10), 0, 0));
		configCenterPanel.add(itemField, new GridBagConstraints(0, y++, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 10, 5, 10), 0, 0));
		configCenterPanel.add(nbLabel, new GridBagConstraints(0, y++, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 10, 5, 10), 0, 0));
		configCenterPanel.add(nbField, new GridBagConstraints(0, y++, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 10, 5, 10), 0, 0));
		configCenterPanel.add(databaseLabel, new GridBagConstraints(0, y++, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 10, 5, 10), 0, 0));
		configCenterPanel.add(databaseField, new GridBagConstraints(0, y++, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 10, 5, 10), 0, 0));
		configCenterPanel.add(checkbox, new GridBagConstraints(0, y++, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 10, 5, 10), 0, 0));
		configCenterPanel.add(categoryFields[0], new GridBagConstraints(0, y++, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 10, 5, 10), 0, 0));
		configCenterPanel.add(categoryFields[1], new GridBagConstraints(0, y++, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 10, 5, 10), 0, 0));
		configCenterPanel.add(categoryFields[2], new GridBagConstraints(0, y++, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 10, 5, 10), 0, 0));
		configCenterPanel.add(categoryFields[3], new GridBagConstraints(0, y++, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 10, 5, 10), 0, 0));
		configCenterPanel.add(categoryFields[4], new GridBagConstraints(0, y++, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 10, 5, 10), 0, 0));
		configCenterPanel.add(categoryFields[5], new GridBagConstraints(0, y++, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 10, 5, 10), 0, 0));
		configCenterPanel.add(categoryFields[6], new GridBagConstraints(0, y++, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 10, 5, 10), 0, 0));
		configCenterPanel.add(new JPanel(), new GridBagConstraints(0, y++, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, new Insets(5, 10, 5, 10), 0, 0));

		checkbox.setSelected(true);

		checkbox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.DESELECTED) {
					categoryFields[0].setVisible(false);
					categoryFields[0].setText("");
					categoryFields[1].setVisible(false);
					categoryFields[1].setText("");
					categoryFields[2].setVisible(false);
					categoryFields[2].setText("");
					categoryFields[3].setVisible(false);
					categoryFields[3].setText("");
					categoryFields[4].setVisible(false);
					categoryFields[4].setText("");
					categoryFields[5].setVisible(false);
					categoryFields[5].setText("");
					categoryFields[6].setVisible(false);
					categoryFields[6].setText("");
				} else {
					categoryFields[0].setVisible(true);
					categoryFields[1].setVisible(true);
					categoryFields[2].setVisible(true);
					categoryFields[3].setVisible(true);
					categoryFields[4].setVisible(true);
					categoryFields[5].setVisible(true);
					categoryFields[6].setVisible(true);
				}

			}
		});

		mainPanel.add(configPanel, "CONFIG");
		mainPanel.add(attributePanel, "ATTRIBUTES");
		mainPanel.add(aboutPanel, "ABOUT");
		cl.show(mainPanel, "CONFIG");
	}

	@SuppressWarnings("unchecked")
	private void showAttribute() {
		attributeCenterPanel.removeAll();
		attributeHeaderLabel.setText("");
		configHeaderLabel.setText("");
		int nbColumns = Integer.parseInt(nbField.getText());
		columns = new JTextField[nbColumns];
		boxes = new JComboBox[nbColumns];
		tooltips = new JTextField[nbColumns];
		filters = new JCheckBox[nbColumns];

		// Headers of the attribute panel
		attributeCenterPanel.add(attributeHeaderLabel, new GridBagConstraints(0, 0, 5, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 10, 5, 10), 0, 0));

		attributeCenterPanel.add(fieldHeader, new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 10, 5, 10), 0, 0));

		attributeCenterPanel.add(tooltipHeader, new GridBagConstraints(2, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 10, 5, 10), 0, 0));

		attributeCenterPanel.add(searchHeader, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 10, 5, 10), 0, 0));

		attributeCenterPanel.add(typeHeader, new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 10, 5, 10), 0, 0));

		// Create fields based on the number input from the user
		for (int i = 0; i < nbColumns; i++) {
			final int finalI = i;
			attributeCenterPanel.add(new JLabel((i + 1) + "."), new GridBagConstraints(0, i + 2, 1, 1, 0.0, 0.0,
					GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 10, 5, 10), 0, 0));
			columns[i] = new JTextField();
			if (i == 0) {
				boxes[0] = new JComboBox<>(attributeTypesAlt);
			} else {
				boxes[i] = new JComboBox<>(attributeTypes);
			}

			tooltips[i] = new JTextField();
			filters[i] = new JCheckBox("");

			boxes[i].addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getItem() == "reference") {
						columns[finalI].setText("reference");
						columns[finalI].setEditable(false);
						tooltips[finalI].setEditable(false);
					} else if (e.getItem() == "comment") {
						columns[finalI].setText("comment");
						columns[finalI].setEditable(false);
						tooltips[finalI].setEditable(false);
					}  else if (e.getItem() == "pdf") {
						columns[finalI].setText("pdf");
						columns[finalI].setEditable(false);
						tooltips[finalI].setEditable(false);
					}  else if (e.getItem() == "image") {
						columns[finalI].setText("image");
						columns[finalI].setEditable(false);
						tooltips[finalI].setEditable(false);
					}else {
						columns[finalI].setEditable(true);
						tooltips[finalI].setEditable(true);
					}

				}
			});

			attributeCenterPanel.add(columns[i], new GridBagConstraints(1, i + 2, 1, 1, 1.0, 0.0,
					GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 10, 5, 10), 0, 0));
			attributeCenterPanel.add(tooltips[i], new GridBagConstraints(2, i + 2, 1, 1, 1.0, 0.0,
					GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 10, 5, 10), 0, 0));
			attributeCenterPanel.add(filters[i], new GridBagConstraints(3, i + 2, 1, 1, 0.0, 0.0,
					GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 10, 5, 10), 0, 0));
			attributeCenterPanel.add(boxes[i], new GridBagConstraints(4, i + 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(5, 10, 5, 10), 0, 0));
		}
		attributeCenterPanel.add(new JPanel(), new GridBagConstraints(0, nbColumns + 2, 5, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 10, 5, 10), 0, 0));

		// If the item type checkbox was checked in the config panel: second field is
		// always the type
		if (checkbox.isSelected()) {
			columns[1].setText("type");
			columns[1].setEditable(false);
			tooltips[1].setEditable(false);
			boxes[1].setEnabled(false);
		}

		cl.show(mainPanel, "ATTRIBUTES");

	}

	private void textAbout() {
		aboutCenterPanel.removeAll();
		aboutCenterPanel.add(new JLabel("About text"), new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 10, 5, 10), 0, 0));
		aboutCenterPanel.add(aboutField, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, new Insets(5, 10, 5, 10), 0, 0));
		cl.show(mainPanel, "ABOUT");
	}

	private void generate() {
		String path = System.getProperty("user.dir");
		String source = path + "/base";
		File srcDir = new File(source);

		String destination = path + "/copy";
		File destDir = new File(destination);

		int nbParams = Integer.parseInt(nbField.getText());

		try {
			FileUtils.copyDirectory(srcDir, destDir);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String item = Utils.formatString(itemField.getText());
		String site = Utils.formatString(siteField.getText());
		String author = Utils.formatString(authorField.getText());
		String database = databaseField.getText().toLowerCase();
		String about = aboutField.getText();

		ArrayList<ItemAttribute> params = new ArrayList<ItemAttribute>();

		for (int i = 0, j = 0; i < nbParams; i++) {
			if (boxes[i].getSelectedItem().toString().equals("url")) {
				ItemAttribute newIA = new ItemAttribute(columns[i].getText(), "url", tooltips[i].getText());
				if (filters[i].isSelected()) {
					newIA.setFilter("strict_filter");
					newIA.setTooltip(newIA.getTooltip() + "<br>(Search is strict)");
				}
				params.add(i + j, newIA);
				params.add(i + j + 1, new ItemAttribute(columns[i].getText() + "Url", "urlValue", ""));
				j += 1;
			} else {
				ItemAttribute newIA = new ItemAttribute(columns[i].getText(), boxes[i].getSelectedItem().toString(),
						tooltips[i].getText());
				if (filters[i].isSelected()) {
					newIA.setFilter("strict_filter");
					newIA.setTooltip(newIA.getTooltip() + "<br>(Search is strict)");
				}
				params.add(i + j, newIA);
				if (boxes[i].getSelectedItem().toString().equals("reference")) {
					params.add(i + 1, new ItemAttribute("fullReference", "fullReference", ""));
					params.add(i + 2, new ItemAttribute("url", "urlValue", ""));
					j += 2;
				}
			}
		}

		if (checkbox.isSelected()) {
			String temp[] = new String[7];
			int cfCounter = 0;
			;
			for (int cf = 0; cf < 7; cf++) {
				if (!categoryFields[cf].getText().equals("")) {
					temp[cfCounter] = categoryFields[cf].getText();
					cfCounter++;
				}
			}
			categories = new String[cfCounter];
			for (int cf = 0; cf < cfCounter; cf++) {
				categories[cf] = temp[cf];
			}
		}

		Controllers.changeFile(path, item, params);
		Models.changeFile(path, item, params);
		Views.changeViews(path, params, item, categories, site, author, about);
		Configs.changeConfigs(path, database, item);

		File dir = new File("copy");
		Replace.renameFileorDirectory(dir, site);
		System.out.println("Website " + site + " files have been successfully generated");
	}

	private boolean checkFields() {
		if (siteField.getText().equals("") || authorField.getText().equals("") || itemField.getText().equals("")
				|| nbField.getText().equals("") || databaseField.getText().equals("")) {
			return false;
		} else {
			return true;
		}
	}

	private boolean checkCategories() {
		if (checkbox.isSelected()) {
			int fieldcounter = 0;
			for (JTextField field : categoryFields) {
				if (!field.getText().equals("")) {
					fieldcounter++;
					if (fieldcounter >= 2) {
						return true;
					}
				}
			}
			return false;
		} else {
			return true;
		}

	}

	private boolean checkColumns() {
		for (JTextField field : columns) {
			if (field.getText().equals("")) {
				return false;
			}
		}
		return true;
	}

}
/*
 * John Bidlack
 * CMSC 330 6380
 * 2/6/22
 * 
 * Purpose: Create a program to parse a text file into a GUI
 */

package cmsc330Proj1JBidlack;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import javax.swing.*;



// Parser class used to input a .txt file and parse the components of a GUI
public class Parser {
	private int width, height;
	private BufferedReader br;
	private ArrayList<String> tokenList;
	private boolean frame = false;
	private String text = "";
	private int index = 0;
	private JFrame window;
	private JPanel panel;
	private String widgetText;
	private JTextField jText;
	private ButtonGroup group;
	private JRadioButton radio;

	
	// Constructor 
	public Parser() throws Exception {
		tokenList = new ArrayList<String>();
		readFile();
	}
	
	// method to open a file selector and handle the selected option. once the file is imported, 
	// the text in the file is read in and parsed as appropriate
	private void readFile() throws Exception {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int result = fileChooser.showOpenDialog(fileChooser);

		if (result == JFileChooser.APPROVE_OPTION) {
			File fileSelected = fileChooser.getSelectedFile();
			try {
				br = new BufferedReader(new FileReader(fileSelected));

				createList();
				GUIParser();
			}
			catch (IOException e) {
				System.out.println("An error has occurred.");
			}
			catch (Exception e) {
				System.out.println("File is in improper format.");
			}
		}
	}
	
	
	// method to separate the various parts of the text file by delimiting characters
	private void createList() {
		String file;
		
		// regex to set the characters to act as separators while still keeping the characters in the array
		final String DELIM = "((?<=[(),\"\'.;:])|(?=[(),\".;:])| )";
		boolean quotes = false;
		
		try {
			while((file = br.readLine()) != null) {
			String [] split = file.trim().split(DELIM,0);
			for (int i =0; i<split.length; i++) {
				split[i] = split[i].trim();
				
				if (quotes) {
					if(split[i].equals("\"")) {
						tokenList.add(text.trim());
						text = "";
						tokenList.add(split[i]);
					}
					else {
						text += split[i] + " ";
					}
				}
				else if(split[i].trim().length()>0) {
					tokenList.add(split[i].trim());
					
				}
				if (split[i].equals("\"")) {
					quotes = !quotes;
				}
			}
			}
		} catch (IOException e) {
			System.out.println("A problem occured with the selected file.");
			return;
		}
	}
	
	// method which parses the tokens into a GUI
	private void GUIParser() throws Exception {
// the first word found should be "window" if found, a new jframe is created, and the index is advanced to the next token
			if(tokenList.get(index).equalsIgnoreCase("Window")){
				frame = true;
				window = new JFrame();
				window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				index++;
// the following tokens should be the title of the window separated by "" when a single " is found, the 
// entire string inside it is added as the window until the enxt " is found

				if (tokenList.get(index).equals("\"")){
					index++;
					window.setTitle(tokenList.get(index));
					index++;
					if(tokenList.get(index).equals("\"")) {
						index++;
						
// similar to the title, the dimensions are found between the ()separated by a ,
						if(tokenList.get(index).equals("(")) {
							index++;
							try {
								width = Integer.parseInt(tokenList.get(index));
							}
							catch(NumberFormatException e) {
								System.out.println("Invalid width. " + tokenList.get(index) + " is not a valid width.");
								return;
							}
							
							index++;
							
							if(tokenList.get(index).equals(",")) {
								index++;
								
								try {
									height = Integer.parseInt(tokenList.get(index));
								}
								catch(NumberFormatException e) {
									System.out.println("Invalid width. " + tokenList.get(index) + " is not a valid height.");
									return;
								}
								index++;
								
								if(tokenList.get(index).equals(")")) {
									window.setSize(width, height);
									index++;
// We call a boolean type method to check if a valid layout exists before continuing
									if(layout()) {
// similarly, we check for any and all proper widgets
										if(widgets()) {
											if(tokenList.get(index).equalsIgnoreCase("End")) {
												index++;
// Once the program finds the final token "." the window is set to visible completing the program												
												if(tokenList.get(index).equals(".")) {
													window.setVisible(true);
												}
												else {
													throw new Exception();
												}
											}
											else {
												throw new Exception();
											}
										}
										else {
											throw new Exception();
										}
									}
									else {
										throw new Exception();
									}
								}
								else {
									throw new Exception();
								}
							}
							else {
								throw new Exception();
							}
						}
						else {
							throw new Exception();
						}
					}
					else {
						throw new Exception();
					}
				}
				else {
					throw new Exception();
				}
			}
			else {
				throw new Exception();
			}
		}

	// boolean type  method to check for the word layout and call another method to implement the layout type
	private boolean layout() {
		if(tokenList.get(index).equalsIgnoreCase("Layout")) {
			index++;
			if(layoutType()) {
				if(tokenList.get(index).equals(":")) {
					index++;
					return true;
				}
			}
		}
		
		return false;
	}
	// boolean method to parse the layout type and return true if valid
	private boolean layoutType() {
		int rows = 0, columns=0, cspace=0, rspace=0;
		
		if(tokenList.get(index).equalsIgnoreCase("Flow")) {
			if(frame) {
				window.setLayout(new FlowLayout());
			}
			else {
				panel.setLayout(new FlowLayout());
			}
			index++;
			return true;
		}
		else if (tokenList.get(index).equalsIgnoreCase("Grid")) {
			index++;
			if(tokenList.get(index).equals("(")) {
				index++;
				
				try {
					rows = Integer.parseInt(tokenList.get(index));
				}
				catch(NumberFormatException e) {
					System.out.println("Invalid format. " + tokenList.get(index) + " is not a valid row value");
					return false;
				}
				
				index++;
				
				if(tokenList.get(index).equals(",")) {
					
					index++;
				
					try {
						columns = Integer.parseInt(tokenList.get(index));
					}
					catch(NumberFormatException e) {
						System.out.println("Invalid format. " + tokenList.get(index) + " is not a valid column value");
						return false;
					}
					
					index++;
					
					if(tokenList.get(index).equals(")")) {
						if(frame) {
							window.setLayout(new GridLayout(rows, columns));
						}
						else {
							panel.setLayout(new GridLayout(rows, columns));
						}
						index++;
						return true;
					}
					else if(tokenList.get(index).equals(",")) {
						
						index++;
						try {
							cspace = Integer.parseInt(tokenList.get(index));
						}
						catch(NumberFormatException e) {
							System.out.println("Invalidformat. " + tokenList.get(index) + " is not a valid column spacing value.");
							return false;
						}
						
						index++;
						
						if(tokenList.get(index).equals(",")) {
							
							index++;
							
							try {
								rspace = Integer.parseInt(tokenList.get(index));
							}
							catch(NumberFormatException e) {
								System.out.println("Invalidformat. " + tokenList.get(index) + " is not a valid row spacing value.");
								return false;
							}
							
							index++;
							
							if(tokenList.get(index).equals(")")) {
								if(frame) {
									window.setLayout(new GridLayout(rows, columns, cspace, rspace));
								}
								else {
									panel.setLayout(new GridLayout(rows, columns, cspace, rspace));
								}
								index++;
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	
	//boolean recursive method which first checks the parsing of a single widget and returns true if found
	// if one is found, the method recursively calls itself to see if any more are present
	private boolean widgets() {
			if(parseWidget()) {
				if(widgets()){
					return true;
				}
				return true;
			}
		return false;
	}
	
	// boolean type method to parse a widget
	private boolean parseWidget() {
		if(tokenList.get(index).equalsIgnoreCase("Button")) {
			index++;
			
			if(tokenList.get(index).equals("\"")) {
				index++;
				widgetText = tokenList.get(index);
				index++;
				
				if(tokenList.get(index).equals("\"")) {
					index++;
					if(tokenList.get(index).equals(";")) {
						if(frame) {
							JButton button = new JButton(widgetText);

							button.addActionListener((ActionEvent e) -> {
								jText.setText(jText.getText() + button.getText());
							});
							window.add(button);
						}
						else {
							JButton button = new JButton(widgetText);

							button.addActionListener((ActionEvent e) -> {
								
								jText.setText(jText.getText() + button.getText());
							});
							panel.add(button);
						}
						
						index++;
						return true;
					}
				}
			}
		}
		else if(tokenList.get(index).equalsIgnoreCase("Group")) {
			group = new ButtonGroup();
			
			index++;
			
			if(radioButton()) {
				if(tokenList.get(index).equalsIgnoreCase("End")) {
					index++;
					if(tokenList.get(index).equals(";")) {
						index++;
						return true;
					}
				}
			}
		}
		else if(tokenList.get(index).equalsIgnoreCase("Label")) {
			index++;
			if(tokenList.get(index).equals("\"")) {
				index++;
				widgetText = tokenList.get(index);
				index++;
				if(tokenList.get(index).equals("\"")) {
					index++;
					if(frame) {
						window.add(new JLabel(widgetText));
					}
					else {
						panel.add(new JLabel(widgetText));
					}
					index++;
					return true;
				}
			}
		}
		else if(tokenList.get(index).equalsIgnoreCase("Panel")) {
			if(frame) {
				window.add(panel = new JPanel());
			}
			else {
				panel.add(panel = new JPanel());
			}
			frame = false;
			index++;
			if(layout()) {
				if(widgets()) {
					if(tokenList.get(index).equals("End")) {
						index++;
						if(tokenList.get(index).equals(";")) {
							index++;
							return true;
						}
					}
				}
			}
		}
		else if(tokenList.get(index).equalsIgnoreCase("Textfield")) {
			int size = 0;
			index++;
			try {
				size = Integer.parseInt(tokenList.get(index));
			}
			catch(NumberFormatException e) {
				System.out.println("Improper format. " + tokenList.get(index) + " is an improper text field size");
			}
			index++;
			if(tokenList.get(index).equals(";")) {
				if (frame) {
					window.add(jText = new JTextField(size));
					jText.setEditable(false);
				}
				else {
					panel.add(jText = new JTextField(size));
					jText.setEditable(false);
				}
				index++;
				return true;
			}
		}
		return false;
	}
	
	private boolean radioButton() {
		if(parsedRadioButton()) {
			if(radioButton()) {
				return true;
			}
			return true;
		}
		return false;
	}
	
	private boolean parsedRadioButton() {
		
		if(tokenList.get(index).equalsIgnoreCase("Radio")) {
			index++;
			
			if(tokenList.get(index).equals("\"")) {
				
				index++;
				widgetText = tokenList.get(index);
				index++;
				
				if(tokenList.get(index).equals("\"")) {
					index++;
					if(tokenList.get(index).equals(";")) {
						radio = new JRadioButton(widgetText);
						group.add(radio);
						if(frame) {
							window.add(radio);
						}
						else {
							panel.add(radio);
						}
						index++;
						return true;
					}
				}
			}
		}
		
		return false;
	}
}

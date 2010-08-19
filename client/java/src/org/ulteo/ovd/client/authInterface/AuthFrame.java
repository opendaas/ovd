/*
 * Copyright (C) 2010 Ulteo SAS
 * http://www.ulteo.com
 * Author Guillaume DUPAS <guillaume@ulteo.com> 2010
 *
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; version 2
 * of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.ulteo.ovd.client.authInterface;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.ulteo.ovd.client.I18n;

public class AuthFrame implements ActionListener {
	
	private JFrame mainFrame = new JFrame();
	private boolean desktopLaunched = false;
	
	private JLabel login = new JLabel(I18n._("Login"));
	private JLabel password = new JLabel(I18n._("Password"));
	private JLabel host = new JLabel(I18n._("Host"));
	private JTextField loginTextField = new JTextField();
	private JPasswordField passwordTextField = new JPasswordField();
	private String loginStr = null;
	
	private JTextField hostTextField = new JTextField();
	private JButton startButton = new JButton(I18n._("Start !"));
	private boolean startButtonClicked = false;
	private JButton moreOption = new JButton();
	private Image frameLogo = null;
	private ImageIcon ulteoLogo = null;
	private ImageIcon optionLogo = null;
	private ImageIcon userLogo = null;
	private ImageIcon passwordLogo = null;
	private ImageIcon hostLogo = null;
	private ImageIcon showOption = null;
	private ImageIcon hideOption = null;
	private JLabel logoLabel = new JLabel();
	private JLabel userLogoLabel = new JLabel();
	private JLabel passwordLogoLabel = new JLabel();
	private JLabel hostLogoLabel = new JLabel();
	private boolean optionClicked;
	private JLabel optionLogoLabel = new JLabel();
	private JLabel mode = new JLabel(I18n._("Mode"));
	private JLabel resolution = new JLabel(I18n._("Resolution"));
	private JLabel language = new JLabel(I18n._("Language"));
	private JLabel keyboard = new JLabel(I18n._("Keyboard"));
	private JComboBox sessionModeBox = null;
	private JComboBoxItem itemModeAuto = new JComboBoxItem(I18n._("Auto"));
	private JComboBoxItem itemModeApplication = new JComboBoxItem(I18n._("Application"));
	private JComboBoxItem itemModeDesktop = new JComboBoxItem(I18n._("Desktop"));
	private JSlider resBar = new JSlider(0, 4, 4);
	private JLabel resolutionValue = new JLabel(I18n._("Fullscreen"));
	private JComboBox languageBox = new JComboBox();
	private JComboBox keyboardBox = new JComboBox();
	private JCheckBox rememberMe = new JCheckBox(I18n._("Remember me"));
	private JCheckBox autoPublish = new JCheckBox(I18n._("Auto-publish shortcuts"));
	private JCheckBox useLocalCredentials = new JCheckBox(I18n._("Use local credentials"));
	private boolean displayUserLocalCredentials = (System.getProperty("os.name").startsWith("Windows"));
	private ActionListener optionListener = null;
	
	private ActionListener obj = null;
	
	private GridBagConstraints gbc = null;
	
	public AuthFrame(ActionListener obj_) {
		this.obj = obj_;
		
		Object[] items = new Object[3];
		items[0] = this.itemModeAuto;
		items[1] = this.itemModeApplication;
		items[2] = this.itemModeDesktop;
		this.sessionModeBox = new JComboBox(items);
		this.sessionModeBox.setRenderer(new JComboBoxItem(""));
		this.sessionModeBox.addActionListener(this);
		
		this.init();
	}
	
	public void init() {
		this.optionClicked = false;

		this.mainFrame.setVisible(false);
		mainFrame.setTitle("OVD Native Client");
		mainFrame.setSize(500,450);
		mainFrame.setResizable(false);
		mainFrame.setBackground(Color.white);
		frameLogo = mainFrame.getToolkit().getImage(getClass().getClassLoader().getResource("pics/ulteo.png"));
		ulteoLogo = new ImageIcon(mainFrame.getToolkit().getImage(getClass().getClassLoader().getResource("pics/logo_small.png")));
		optionLogo = new ImageIcon(mainFrame.getToolkit().getImage(getClass().getClassLoader().getResource("pics/options.png")));
		userLogo = new ImageIcon(mainFrame.getToolkit().getImage(getClass().getClassLoader().getResource("pics/users.png")));
		passwordLogo = new ImageIcon(mainFrame.getToolkit().getImage(getClass().getClassLoader().getResource("pics/password.png")));
		hostLogo = new ImageIcon(mainFrame.getToolkit().getImage(getClass().getClassLoader().getResource("pics/server.png")));
		showOption = new ImageIcon(mainFrame.getToolkit().getImage(getClass().getClassLoader().getResource("pics/show.png")));
		hideOption = new ImageIcon(mainFrame.getToolkit().getImage(getClass().getClassLoader().getResource("pics/hide.png")));
		
		mainFrame.setIconImage(frameLogo);
		logoLabel.setIcon(ulteoLogo);
		userLogoLabel.setIcon(userLogo);
		passwordLogoLabel.setIcon(passwordLogo);
		hostLogoLabel.setIcon(hostLogo);
		optionLogoLabel.setIcon(optionLogo);
		
		moreOption.setIcon(showOption);
		moreOption.setText(I18n._("More options ..."));
		
		this.useLocalCredentials.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				toggleLocalCredentials();
			}
		});
		
		resBar.setMajorTickSpacing(1);
		resBar.setPaintTicks(true);
		resBar.setSnapToTicks(true);
		resBar.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent ce) {
				int value = resBar.getValue();
				
				switch(value) {
				case 0 :
					resolutionValue.setText("800x600");
					break;
				case 1 :
					resolutionValue.setText("1024x768");
					break;
				case 2 :
					resolutionValue.setText("1280x678");
					break;
				case 3 :
					resolutionValue.setText(I18n._("Maximized"));
					break;
				case 4 :
					resolutionValue.setText(I18n._("Fullscreen"));
					break;
				}
			}
		});
		
		optionListener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (! optionClicked) {
					gbc.anchor = GridBagConstraints.CENTER;
					gbc.gridwidth = 2;
					gbc.weightx = 1;
					gbc.weighty = 1;
					gbc.gridx = 0;
					gbc.gridy = 9;
					gbc.insets.top = 30;
					mainFrame.add(optionLogoLabel, gbc);
					
					gbc.anchor = GridBagConstraints.LINE_START;
					gbc.weightx = 0;
					gbc.weighty = 0;
					gbc.gridwidth = 1;
					gbc.insets.top = 0;
					gbc.insets.left = 0;
					gbc.gridx = 1;
					gbc.gridy = 10;
					mainFrame.add(mode, gbc);
					
					/*gbc.gridy = 13;
					mainFrame.add(language, gbc);
					
					gbc.gridy = 14;
					mainFrame.add(keyboard, gbc);*/
					
					gbc.gridwidth = 2;
					gbc.gridx = 2;
					gbc.gridy = 10;
					gbc.fill = GridBagConstraints.HORIZONTAL;
					mainFrame.add(sessionModeBox, gbc);
					
					sessionModeBox.validate();
					
					/*gbc.gridx = 2;
					gbc.gridwidth = 2;
					gbc.gridy = 13;
					gbc.fill = GridBagConstraints.HORIZONTAL;
					mainFrame.add(languageBox, gbc);
					
					gbc.gridy = 14;
					mainFrame.add(keyboardBox, gbc);*/
					
					gbc.gridwidth = 1;
					gbc.fill = GridBagConstraints.NONE;
					moreOption.setIcon(hideOption);
					moreOption.setText(I18n._("Fewer options"));
					mainFrame.pack();
					optionClicked = true;
					toggleSessionMode();
					
				} else {
					mainFrame.remove(optionLogoLabel);
					mainFrame.remove(mode);
					mainFrame.remove(resolution);
					mainFrame.remove(language);
					mainFrame.remove(keyboard);
					mainFrame.remove(sessionModeBox);
					mainFrame.remove(resBar);
					mainFrame.remove(resolutionValue);
					mainFrame.remove(languageBox);
					mainFrame.remove(keyboardBox);
					mainFrame.remove(autoPublish);
					
					moreOption.setIcon(showOption);
					moreOption.setText(I18n._("More options ..."));
					mainFrame.pack();
					optionClicked = false;
				}
				
			}
		};
		
		moreOption.addActionListener(optionListener);
		
		mainFrame.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		startButton.setPreferredSize(new Dimension(150, 25));
		startButton.addActionListener(this.obj);
		
		gbc.gridx = gbc.gridy = 0;
		gbc.insets = new Insets(7, 7, 25, 0);
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.weightx = 1;
		gbc.weighty = 1;
		mainFrame.add(logoLabel, gbc);
		
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.insets.left = 0;
		gbc.insets.top = 0;
		gbc.insets.bottom = 5;
		mainFrame.add(userLogoLabel, gbc);
		
		gbc.gridy = 4;
		mainFrame.add(passwordLogoLabel, gbc);
		
		int pos = 5;
		if (this.displayUserLocalCredentials)
			pos++;
		
		gbc.gridy = pos;
		mainFrame.add(hostLogoLabel, gbc);
		
		pos = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets.left = 5;
		gbc.gridx = 1;
		gbc.gridy = 3;
		mainFrame.add(login, gbc);
		
		gbc.gridy = 4;
		mainFrame.add(password, gbc);      
		
		pos = 5;
		if (this.displayUserLocalCredentials)
			pos++;
		
		gbc.gridy = pos;
		mainFrame.add(host, gbc);
		
		gbc.gridwidth = GridBagConstraints.REMAINDER;;
		gbc.gridheight = GridBagConstraints.REMAINDER;
		gbc.insets.top = 25;
		gbc.gridx = 0;
		gbc.gridy = 14;
		mainFrame.add(moreOption, gbc);
		
		gbc.gridwidth = 0;
		gbc.gridheight = 1;
		gbc.insets.top = 0;
		gbc.gridx = 2;
		gbc.gridy = 3;
		gbc.insets.left = 0;
		gbc.insets.right = 15;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		mainFrame.add(loginTextField, gbc);
		
		gbc.gridy = 4;
		mainFrame.add(passwordTextField, gbc);
		
		pos = 5;
		if (this.displayUserLocalCredentials) {
			gbc.gridy = pos++;
			mainFrame.add(this.useLocalCredentials, gbc);
		}
		
		gbc.gridy = pos++;
		mainFrame.add(hostTextField, gbc);
		
		gbc.gridy = pos++;
		gbc.anchor = GridBagConstraints.CENTER;
		mainFrame.add(rememberMe, gbc);
		
		gbc.gridx = 2;
		gbc.gridy = pos++;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		mainFrame.add(startButton, gbc);
		
		KeyListener keyListener = new KeyListener() {

			public synchronized void keyTyped(KeyEvent ke) {
				if ((ke.getKeyChar() == KeyEvent.VK_ENTER) && (! startButtonClicked)) {
					startButtonClicked = true;
					startButton.doClick();
				}
			}

			public void keyPressed(KeyEvent ke) {}
			public void keyReleased(KeyEvent ke) {}

		};
		for (Component c : this.mainFrame.getContentPane().getComponents()) {
			if (c.getClass() != JLabel.class) {
				c.addKeyListener(keyListener);
			}
		}
		
		mainFrame.pack();
		mainFrame.setLocationRelativeTo(null);
		this.showWindow();
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	protected void toggleLocalCredentials() {
		if (this.useLocalCredentials.isSelected()) {
			this.loginStr = this.loginTextField.getText();
			this.loginTextField.setText(System.getProperty("user.name"));
			this.loginTextField.setEnabled(false);
			this.login.setEnabled(false);
			this.passwordTextField.setEnabled(false);
			this.password.setEnabled(false);
			this.userLogoLabel.setEnabled(false);
			this.passwordLogoLabel.setEnabled(false);
		}
		else {
			if (this.loginStr != null)
				this.loginTextField.setText(this.loginStr);
			this.loginTextField.setEnabled(true);
			this.login.setEnabled(true);
			this.passwordTextField.setEnabled(true);
			this.password.setEnabled(true);
			this.userLogoLabel.setEnabled(true);
			this.passwordLogoLabel.setEnabled(true);
		}
	}
	
	public void toggleSessionMode() {
		if (! this.optionClicked)
			return;
		
		this.mainFrame.remove(this.autoPublish);
		this.mainFrame.remove(this.resolutionValue);
		this.mainFrame.remove(this.resolution);
		this.mainFrame.remove(this.resBar);
		
		this.gbc.anchor = GridBagConstraints.LINE_START;
		this.gbc.insets.left = 0;
		this.gbc.gridwidth = 1;
		
		if (this.sessionModeBox.getSelectedItem() == this.itemModeApplication) {	
			this.gbc.gridx = 2;
			this.gbc.gridy = 11;
			this.gbc.gridwidth = 2;
			this.gbc.anchor = GridBagConstraints.LINE_START;
			this.gbc.fill = GridBagConstraints.NONE;
			this.mainFrame.add(autoPublish, gbc);
		}
		
		else if (this.sessionModeBox.getSelectedItem() == this.itemModeDesktop) {
			this.gbc.insets.left = 0;
			this.gbc.gridx = 1;
			this.gbc.gridy = 11;
			this.mainFrame.add(resolution, gbc);
			
			this.gbc.anchor = GridBagConstraints.LINE_START;
			this.gbc.gridx = 2;
			this.gbc.gridy = 11;
			this.gbc.gridwidth = 1;
			this.gbc.fill = GridBagConstraints.NONE;
			this.resBar.setSize(new Dimension(sessionModeBox.getWidth(), 33));
			this.resBar.setPreferredSize(new Dimension(sessionModeBox.getWidth(), 33));
			this.mainFrame.add(resBar, gbc);
			
			this.gbc.insets.left = 0;
			this.gbc.gridy = 12;
			
			this.gbc.anchor = GridBagConstraints.CENTER;
			this.mainFrame.add(resolutionValue, gbc);
		}
		this.mainFrame.pack();
	}
	
	public void showWindow() {
		KeyboardFocusManager.setCurrentKeyboardFocusManager(null);
		this.startButtonClicked = false;
		this.toggleLocalCredentials();
		mainFrame.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent ev) {
		if (ev.getSource() == this.sessionModeBox)
			this.toggleSessionMode();
	}
	
	public void hideWindow() {
		mainFrame.setVisible(false);
	}
	
	public JTextField getLogin() {
		return loginTextField;
	}

	public void setLogin(String login_) {
		if (login_ == null)
			return;

		this.loginTextField.setText(login_);
	}

	public JPasswordField getPassword() {
		return passwordTextField;
	}

	public JTextField getHost() {
		return hostTextField;
	}

	public void setHost(String host_) {
		if (host_ == null)
			return;
		
		this.hostTextField.setText(host_);
	}

	public JSlider getResBar() {
		return resBar;
	}

	public void setResolution(int resolution_) {
		this.resBar.setValue(resolution_);
	}

	public JComboBox getSessionModeBox() {
		return this.sessionModeBox;
	}
	public JLabel getItemModeApplication() {
		return this.itemModeApplication;
	}
	public JLabel getItemModeAuto() {
		return this.itemModeAuto;
	}
	public JLabel getItemModeDesktop() {
		return this.itemModeDesktop;
	}
	
	public JFrame getMainFrame() {
		return mainFrame;
	}
	
	public boolean isDesktopLaunched() {
		return desktopLaunched;
	}
	
	public void setDesktopLaunched(boolean desktopLaunched) {
		this.desktopLaunched = desktopLaunched;
	}
	
	public boolean isRememberMeChecked() {
		return this.rememberMe.isSelected();
	}

	public void setRememberMeChecked(boolean checked_) {
		this.rememberMe.setSelected(checked_);
	}
	
	public boolean isAutoPublishChecked() {
		return this.autoPublish.isSelected();
	}

	public void setAutoPublishChecked(boolean autoPublish_) {
		this.autoPublish.setSelected(autoPublish_);
	}
	
	public void setUseLocalCredentials(boolean useLocalCredentials_) {
		this.useLocalCredentials.setSelected(useLocalCredentials_);
	}
	
	public boolean isUseLocalCredentials() {
		return this.useLocalCredentials.isSelected();
	}
	
	public JButton getOptionButton() {
		return moreOption;
	}
	
	public JButton GetStartButton() {
		return this.startButton;
	}
}
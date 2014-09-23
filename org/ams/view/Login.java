package org.ams.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.ams.model.LoginModel;
import org.ams.model.UserVerifyNotification;

public class Login extends JFrame implements MouseListener, Observer{

	private JTextField username;
	private JPasswordField password;
	private JLabel info;
	private LoginModel lm;
	private boolean islocked = false;
	
	public Login(){
		lm = new LoginModel();
		lm.addObserver(this);
		setLayout(null);
		add(launchLoginPanel());	
		add(createBackground());
		setTitle("AMS v0.8 beta");
		setSize(800, 600);
		Dimension dem=Toolkit.getDefaultToolkit().getScreenSize();
		  int sHeight=dem.height;
		  int sWidth=dem.width;
		  this.setLocation((sWidth-800)/2, (sHeight-600)/2);
		setBackground(Color.WHITE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);
	}
	
	private JPanel createBackground(){
		JPanel bg = new JPanel(){
			@Override
			protected void paintComponent(Graphics g) {
				ImageIcon icon = new ImageIcon("image/loginPage_wallpaper.jpg");
				Image image = icon.getImage();
				g.drawImage(image,0, 0, getWidth(), getHeight(), this);
			}
		};
		bg.setSize(800,575);
		return bg;
	}
	
	
	
	private JPanel launchLoginPanel(){
		JPanel panel = new JPanel(new GridLayout(3, 2,2,2));
		JLabel label;
		
		label = new JLabel("Username");
		username = new JTextField();
		username.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if(e.getKeyChar() == KeyEvent.VK_ENTER){
					login();
				}
			}
		});
		panel.add(label);
		panel.add(username);
		
		label = new JLabel("Password");
		password = new JPasswordField();
		password.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if(e.getKeyChar() == KeyEvent.VK_ENTER){
					login();
				}
			}
		});
		panel.add(label);
		panel.add(password);
		panel.setOpaque(false);
		info = new JLabel("Welcome!");
		panel.add(info);
		JButton login = new JButton("LogIn");
		login.setBorder(null);
		
		login.setFont(new Font("Calibri", Font.BOLD, 25));
		login.setHorizontalAlignment(JLabel.CENTER);
		login.addMouseListener(this);
		panel.add(login);
		panel.setSize(250, 150);
		panel.setLocation(500,400);
		return panel;
	}
	
	private void login(){
		if(!islocked){
		new Thread(){
			
			@Override
			public void run() {
				
				islocked = true;
					String uname = username.getText();
					String pwd = new String(password.getPassword());
					if(uname.equals("")){
						info.setText("No username!");
					}
					else{
						if(pwd.equals("")){
							info.setText("No password!");
						}
						else{
							info.setText("Verifing..");
							lm.verifyUser(uname, pwd);
						}
					}
					info.updateUI();
				
					islocked = false;
			}		
		}.start();
		}
	}
	
	@Override
	public void update(Observable o, Object obj) {
		if(obj instanceof UserVerifyNotification){
			UserVerifyNotification un = (UserVerifyNotification)obj;
			if(un.getUser() == null){
				if(un.getResult() == UserVerifyNotification.USER_NOT_EXIST){
					username.setBackground(Color.PINK);
					username.updateUI();
					info.setText("User dose not exist!");
					info.updateUI();;
				}
				else if(un.getResult() == UserVerifyNotification.PASSWORD_UNCORRECT){
					password.setBackground(Color.PINK);
					password.updateUI();
					info.setText("Password incorrect!");
					info.updateUI();
				}
			}
			else{
				info.setText("Login successful!");
				info.updateUI();
				setVisible(false);
				new MainUI(un.getUser());
			}
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		login();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

}

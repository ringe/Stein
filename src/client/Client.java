package client;

import java.net.MalformedURLException;

import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.UnknownHostException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import server.Server;

import base.Player;
import base.Srv;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * Client object GUI to connect with the server and create a player and a game
 * @author  runar & alex
 * @version  1.0
 */
public class Client extends Shell {

	private Composite composite;
	private Text userName;
	private Label lblWelcomeToStein;
	private Text serverName;
	private static Display display;
	private Label lblStatus;
	private Label status;
	private static List list;
	private Composite composite_2;
	private Button btnPlay;
	private Button btnQuit;
	private Label lblChooseAPlayer;
	private String selected;
	/**
	 * @uml.property  name="server"
	 * @uml.associationEnd  
	 */
	private Srv server;
	/**
	 * @uml.property  name="player"
	 * @uml.associationEnd  
	 */
	private Player player;
	private Composite composite_1;
	private Button btnCancel;
	private Button btnRock;
	private Button btnScissors;
	private Button btnPaper;
	private Label lblWins;
	private Label lblDraws;
	private Label lblDefeats;
	private static Cursor cursor = new Cursor(display, SWT.CURSOR_ARROW);
	private static Cursor cursor_busy = new Cursor(display, SWT.CURSOR_WAIT);
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			Client shell = new Client(display);
			shell.setCursor(cursor);
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the shell.
	 * 
	 * @param disp					The Display to create this Client on.
	 * @throws RemoteException 
	 * @throws NotBoundException 
	 * @throws MalformedURLException 
	 */
	public Client(Display disp) throws RemoteException, MalformedURLException, NotBoundException {
		super(disp, SWT.SHELL_TRIM);
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		display = disp;
		System.out.println("Started");
		
		list = new List(this, SWT.BORDER);
		list.setItems(new String[] {"player list"});
		list.setEnabled(false);
		list.setBounds(250, 7, 174, 245);
		createContents();

		status = new Label(this, SWT.NONE);
		status.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		status.setFont(SWTResourceManager.getFont("Arial", 11, SWT.NORMAL));
		status.setBounds(66, 237, 178, 15);
		status.setText("Ready");

		lblStatus = new Label(this, SWT.NONE);
		lblStatus.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblStatus.setFont(SWTResourceManager.getFont("Arial", 11, SWT.NORMAL));
		lblStatus.setBounds(10, 237, 50, 17);
		lblStatus.setText("Status: ");
		
		LoadWelcomePane();
		//LoadNewGamePane();
		//LoadGamePane();
		createContents();
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("Stein!");
		setSize(450, 300);

	}

	/**
	 * Loading the welcome screen 
	 */
	private void LoadWelcomePane() {
		composite = new Composite(this, SWT.NONE);
		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite.setBounds(9, 10, 235, 221);

		sayWelcome(composite);

		serverName = new Text(composite, SWT.BORDER);
		serverName.setBounds(124, 95, 110, 21);
		serverName.setText("localhost");
		
		userName = new Text(composite, SWT.BORDER);
		userName.setBounds(124, 122, 110, 21);
		userName.setText("per");

		Label lblName = new Label(composite, SWT.NONE);
		lblName.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblName.setBounds(10, 125, 118, 15);
		lblName.setText("Your name");

		Label lblServer = new Label(composite, SWT.NONE);
		lblServer.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblServer.setBounds(10, 98, 118, 15);
		lblServer.setText("Server");

		Button btnJoinGame = new Button(composite, SWT.NONE);
		btnJoinGame.setFont(SWTResourceManager.getFont("Arial", 11, SWT.NORMAL));
		btnJoinGame.setBounds(148, 151, 82, 29);
		btnJoinGame.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				final String name = userName.getText();
				status.setText("Connecting");
				
				composite.setCursor(cursor_busy);
				display.asyncExec(new Runnable() {
					public void run() {
						try {
							// Connect to Server
							server = (Srv)Naming.lookup("rmi://" + serverName.getText() + "/SteinEngine");
							
							// Create a new Player
							player = new Gamer(name, server, getShell(), display);
		
							// Update player list
							updateList();
							
							// Dispose the register view
							composite.dispose();
							
							// Load the game view
							LoadNewGamePane();
							status.setText("Game on!");
						}
						catch (ConnectException ce) {
							errorPopup("No Server found at the given location.");
							status.setText("Is the Server running?");
							composite.setCursor(cursor);
						}
						catch (UnknownHostException ue) {
							errorPopup("Host not found: \"" + ue.detail.getMessage() + "\".");
							status.setText("Choose a different server.");
							composite.setCursor(cursor);
						}
						catch (RemoteException ce) {
							errorPopup(ce.detail.getMessage());
							status.setText("Choose a different name.");
							composite.setCursor(cursor);
						}
						catch (Exception e) {
							errorPopup("Serious error! Contact the author of this lousy game!");
							display.dispose();
							e.printStackTrace();
						}
					}
				});
			};
		});
		btnJoinGame.setText("Join Game");
	}
	
	/**
	 * Loads the pane to create a game
	 */
	public void LoadNewGamePane()
	{	
		composite_2 = new Composite(this, SWT.NONE);
		composite_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite_2.setBounds(9, 10, 235, 221);
		composite_2.setCursor(cursor);
		
		list.setEnabled(true);

		btnPlay = new Button(composite_2, SWT.NONE);
		btnPlay.setToolTipText("You have to choose a player!");
		btnPlay.setEnabled(false);
		btnPlay.setBounds(10, 196, 139, 25);
		btnPlay.setText("Can't play yet!");

		btnQuit = new Button(composite_2, SWT.NONE);
		btnQuit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				quit();
			}
		});
		btnQuit.setBounds(155, 196, 75, 25);
		btnQuit.setText("Quit");

		sayWelcome(composite_2);

		lblChooseAPlayer = new Label(composite_2, SWT.NONE);
		lblChooseAPlayer.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblChooseAPlayer.setAlignment(SWT.CENTER);
		lblChooseAPlayer.setFont(SWTResourceManager.getFont("Comic Sans MS", 14, SWT.NORMAL));
		lblChooseAPlayer.setBounds(0, 98, 230, 71);
		lblChooseAPlayer.setText("Choose a player\nfrom the list!");

		/**
		 *  Choose a player from the list of players
		 */
		list.addMouseListener(new MouseAdapter() {	
			@Override
			public void mouseUp(MouseEvent e) {
				if (list.getSelection().length != 0)
					selected = list.getSelection()[0];
				else
					return;

				/**
				 *  Enable gameplay, when other user is selected.
				 */
				try {
					if (selected.equals(player.name())) {
						status.setText("Schizophrenia?");
						btnPlay.setEnabled(false);
						btnPlay.setText("Can't play yet!");
					} else {
						status.setText("Selected "+selected);
						btnPlay.setText("Play with "+selected);
						btnPlay.setEnabled(true);
					}
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
			}
		});

		/**
		 *  Start a game with the selected player
		 */
		btnPlay.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					player.newGame(selected);
					LoadGamePane();
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Prepare the game board
	 * @throws RemoteException
	 */
	public void LoadGamePane() throws RemoteException {
		composite_2.dispose();
		
		if (player.agressive())
			status.setText("You challenged " + player.opponent());
		else
			status.setText("You got challenged by " + player.opponent());

		composite_1 = new Composite(this, SWT.NONE);
		composite_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite_1.setBounds(10, 10, 230, 221);

		btnCancel = new Button(composite_1, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				composite_1.dispose();
				try {
					player.withdraw();
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
				LoadNewGamePane();
			}
		});
		btnCancel.setBounds(118, 192, 112, 29);
		btnCancel.setText("Withdraw");

		btnRock = new Button(composite_1, SWT.NONE);
		btnRock.setToolTipText("Rock");
		btnRock.setImage(SWTResourceManager.getImage(Client.class, "/client/rock.png"));
		addButtonMove(btnRock, "Rock");
		btnRock.setBounds(0, 113, 112, 107);

		btnScissors = new Button(composite_1, SWT.NONE);
		btnScissors.setToolTipText("Scissors");
		btnScissors.setImage(SWTResourceManager.getImage(Client.class, "/client/scissors.png"));
		addButtonMove(btnScissors, "Scissors");
		btnScissors.setBounds(0, 0, 112, 107);

		btnPaper = new Button(composite_1, SWT.NONE);
		btnPaper.setToolTipText("Paper");
		btnPaper.setImage(SWTResourceManager.getImage(Client.class, "/client/paper.png"));
		addButtonMove(btnPaper, "Paper");
		btnPaper.setBounds(118, 0, 112, 107);
		
		lblWins = new Label(composite_1, SWT.NONE);
		lblWins.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblWins.setBounds(118, 113, 75, 15);
		lblWins.setText("0 WINS");
		
		lblDefeats = new Label(composite_1, SWT.NONE);
		lblDefeats.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblDefeats.setText("0 DEFEATS");
		lblDefeats.setBounds(118, 171, 75, 15);
		
		lblDraws = new Label(composite_1, SWT.NONE);
		lblDraws.setBounds(118, 142, 75, 15);
		lblDraws.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblDraws.setText("0 DRAWS");
	}
	
	
	/**
	 * Add a player move listener to the given button
	 * @param btn
	 * @param move
	 */
	private void addButtonMove(Button btn, final String move) {
		btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				gameButtonsSwitch(false);
				try {
					player.move(move);
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Load the welcome label
	 * @param composite
	 */
	public void sayWelcome(Composite composite)
	{
		lblWelcomeToStein = new Label(composite, SWT.NONE);
		lblWelcomeToStein.setAlignment(SWT.CENTER);
		lblWelcomeToStein.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblWelcomeToStein.setFont(SWTResourceManager.getFont("Comic Sans MS", 22, SWT.NORMAL));
		lblWelcomeToStein.setBounds(0, 10, 230, 82);
		lblWelcomeToStein.setText("Welcome\nto Stein!");
	}
	
	/**
	 * Toggle all buttons in game pane
	 * @param on
	 */
	public void gameButtonsSwitch(boolean on){
		btnRock.setEnabled(on);
		btnPaper.setEnabled(on);
		btnScissors.setEnabled(on);
		btnCancel.setEnabled(on);
	}
	
	/**
	 * Display error Popup message
	 * @param message
	 */
	private void errorPopup(String message)
	{
		MessageBox b = new MessageBox(this, SWT.ICON_WARNING | SWT.OK);
		b.setText(Server.SERVICE_NAME);
		b.setMessage(message);
		b.open();
	}
	

	/**
	 * Leave the game, close the window
	 */
	public void quit()
	{
		try {
			server.leave(player);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		this.close();
	}
	
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	
	/**
	 * Method to update the list of players
	 * @throws RemoteException
	 */
	public void updateList() throws RemoteException {
		list.removeAll();
		Object[] gamers = server.getPlayers();
		for(int i=0; i<gamers.length; i++){
			Player g = (Player)gamers[i];
			list.add(g.name());
		}
	}
		
	/**
	 * Method to update the score status
	 * @throws RemoteException
	 */
	public void statusUpdate() throws RemoteException {
		lblWins.setText(player.wins() + " wins");
		lblDraws.setText(player.draws() + " draws");
		lblDefeats.setText(player.defeats() + " defeats");		
	}
	
	/**
	 * Method to update client when the opponent leaves the game
	 * @throws RemoteException
	 */
	public void opponentLeft() throws RemoteException {
		errorPopup(player.opponent() + " left the building.");
		composite_1.dispose();
		LoadNewGamePane();
	}
}

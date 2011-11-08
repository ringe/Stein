package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

import base.Game;
import base.Player;

/**
*  Play
*  
*  This is where the game rules and everything with the game is.
*  
* @author runar & alex
* @version 1.0
*/
public class Play extends UnicastRemoteObject implements Game {

	private static final long serialVersionUID = -4068485307729874645L;
	/**
	 * @uml.property  name="opponent"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private Player opponent;
	/**
	 * @uml.property  name="opponentWins"
	 */
	private int opponentWins;
	/**
	 * @uml.property  name="owner"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private Player owner;
	/**
	 * @uml.property  name="ownerWins"
	 */
	private int ownerWins;
	/**
	 * @uml.property  name="draws"
	 */
	private int draws;
	/**
	 * @uml.property  name="title"
	 */
	private String title;
	/**
	 * @uml.property  name="owM"
	 */
	private String owM = null;
	/**
	 * @uml.property  name="opM"
	 */
	private String opM = null;

	
	/**
	 * Create a game between two players
	 * 
	 * @param _opponent 		opponent's name
	 * @param _owner 			player's name
	 * @throws RemoteException
	 */
	protected Play(Player _opponent, Player _owner) throws RemoteException {
		opponent = _opponent;
		owner = _owner;
		setTitle();
		opponent.challenge(owner.name());
	}

	/* (non-Javadoc)
	 * @see base.Game#owner()
	 */
	@Override
	public String owner() throws RemoteException {
		return owner.name();
	}

	/* (non-Javadoc)
	 * @see base.Game#opponent(java.lang.String)
	 */
	@Override
	public String opponent(String name) throws RemoteException {
		if (owner.name().equals(name))
			return opponent.name();
		return owner.name();
	}
	
	/**
	 * Create the name of the game
	 * 
	 * @throws RemoteException
	 */
	public void setTitle() throws RemoteException {
		String[] players = {opponent.name(), owner.name()};
		Arrays.sort(players);
		title = players[0] + players[1];
		System.out.println("Game created: " +title);
	}

	/* (non-Javadoc)
	 * @see base.Game#title()
	 */
	@Override
	public String title() throws RemoteException {
		return title;
	}

	/* (non-Javadoc)
	 * @see base.Game#move(java.lang.String, java.lang.String)
	 */
	@Override
	public void move(String name, String move) throws RemoteException {
		if (owner.name().equals(name))
			owM = move;
		if (opponent.name().equals(name))
			opM = move;

		// If both moves made, calculate result
		if (owM != null && opM != null) { // Owner might win!
			if ((owM.equals("Rock") && opM.equals("Scissors")) ||
					(owM.equals("Scissors") && opM.equals("Paper")) ||
					(owM.equals("Paper") && opM.equals("Rock")))
				ownerWins++;
			else // But if he didn't and the players made the same move, it's a draw
				if (owM.equals(opM))
					draws++;
				else // Or, the opponent win
					opponentWins++;
			
			// Round finished
			// TODO: update Players
			owner.gameUpdate();
			opponent.gameUpdate();
			owM = opM = null;
		}
	}

	/* (non-Javadoc)
	 * @see base.Game#amIstupid(java.lang.String)
	 */
	@Override
	public boolean amIstupid(String name) throws RemoteException {
		return owner.name().equals(name);
	}

	/* (non-Javadoc)
	 * @see base.Game#wins(java.lang.String)
	 */
	@Override
	public int wins(String name) throws RemoteException {
		if (owner.name().equals(name))
			return ownerWins;
		return opponentWins;
	}

	/* (non-Javadoc)
	 * @see base.Game#draws(java.lang.String)
	 */
	@Override
	public int draws(String name) throws RemoteException {
		return draws;
	}

	/* (non-Javadoc)
	 * @see base.Game#defeats(java.lang.String)
	 */
	@Override
	public int defeats(String name) throws RemoteException {
		if (owner.name().equals(name))
			return opponentWins;
		return ownerWins;
	}

	/* (non-Javadoc)
	 * @see base.Game#withdraw(java.lang.String)
	 */
	@Override
	public void withdraw(String name) throws RemoteException {
		if (owner.name().equals(name))
			opponent.endGame();
		else
			owner.endGame();
	}

}

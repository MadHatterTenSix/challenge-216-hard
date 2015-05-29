/**
 *
 * @author __MadHatter (alias used on https://www.reddit.com/r/dailyprogrammer)
 */

/* Player.java */

import java.util.ArrayList;

public final class Player {

  public static enum Type {
    CPU,
    HUMAN
  }

  private boolean hasFolded;
  private String name;
  private Type type;
  private ArrayList<Card> hand;

  public Player(Type type, String name) {
    hasFolded = false;
    hand      = new ArrayList<>();
    this.type = type;
    this.name = name;
  }

  public boolean hasFolded()
    { return hasFolded; }

  public void fold()
    { hasFolded = true; }

  public String getName()
    { return name; }

  public Type getType()
    { return type; }

  public ArrayList<Card> getHand()
    { return hand; }

  public int getNumberOfCards()
    { return hand.size(); }

  public void receiveCard(Card card) {
    if (card != null) {
      hand.add(card);
    }
  }

  public String handToString() {
    String s = "";
    for (int i = 0; i < hand.size(); i++) {
      s += hand.get(i).toString() + " ";
    }
    return s;
  }

  public void printHand() {
    String msg = name + "'s cards: ";
    msg += handToString();
    System.out.println(msg);
  }

}

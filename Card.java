/**
 *
 * @author __MadHatter (alias used on https://www.reddit.com/r/dailyprogrammer)
 */

/* Card.java */

import java.util.ArrayList;
import java.util.Comparator;

public final class Card {

  public static final String SUITS[] = {
    "UNKNOWN_SUIT",
    "♣", "♦", "♥", "♠" /* rank of suits in ascending order */
  };

  public static final String WORD_VALUES[] = {
    "UNKNOWN_WORD_VALUE", /* 0 value is meaningless */
    "UNKNOWN_WORD_VALUE", /* 1 (ace) value moved to highest card */
    "2", "3", "4", "5",
    "6", "7", "8", "9", "10",
    "J", "Q", "K", "A"
  };

  private int val;
  private int suit;

  public Card(int val, int suit) {

    if (val <= 0 || val >= WORD_VALUES.length)
      { this.val = 0; }
    else
      { this.val  = val; }

    if (suit <= 0 || suit >= SUITS.length)
      { this.suit = 0; }
    else
      { this.suit = suit; }

  }

  public int getSuit()
    { return suit; }

  public int getValue()
    { return val; }

  public String valueToString()
    { return WORD_VALUES[val]; }

  public String suitToString()
    { return SUITS[suit]; }

  @Override
  public String toString() {
    String s = "[";
    s += suitToString();
    if (val != 10) {
      s += " ";
    }
    s += valueToString() + "]";
    return s;
  }

  public static Comparator<Card> CardValueComparator = new Comparator<Card>() {
    @Override
    public int compare(Card o1, Card o2) {
      /* Sort using descending order. */
      return (o2.getValue() - o1.getValue());
    }
  };

  public static Comparator<Card> CardSuitComparator = new Comparator<Card>() {
    @Override
    public int compare(Card o1, Card o2) {
      /* Sort using descending order. */
      return (o2.getSuit() - o1.getSuit());
    }
  };

  public static void printCards(ArrayList<Card> listOfCards) {
    for (Card card : listOfCards) {
      System.out.print(card.toString() + " ");
    }
    System.out.println("");
  }

  /* Return copy of specified card array list. */
  public static ArrayList<Card> getCopyOfCards(ArrayList<Card> source) {
    ArrayList<Card> destination = new ArrayList<>();
    for (int i = 0; i < source.size(); i++) {
      destination.add(source.get(i));
    }
    return destination;
  }

}

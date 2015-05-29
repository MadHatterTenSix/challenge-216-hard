/**
 *
 * @author __MadHatter (alias used on https://www.reddit.com/r/dailyprogrammer)
 */

/* Deck.java */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public final class Deck {

  private Random random;
  private ArrayList<Card> deck;

  public Deck() {
    deck   = new ArrayList<>();
    random = new Random();
    initializeDeck();
  }

  private void addCard(Card card)
    { deck.add(card); }

  public Card drawCard() {
    if (deck.size() > 0) {
      Card topCard = deck.get(0);
      deck.remove(0);
      return topCard;
    }
    else {
      System.out.println("Cannot draw any more cards. This deck is empty.");
      return null;
    }
  }

  public void burnTopCard()
    { drawCard(); }

  public int numberOfCardsLeft()
    { return deck.size(); }

  public void initializeDeck() {
    deck.clear();
    for (int i = 1; i <= 4; i++) {    /* suits */
      for (int j = 2; j <= 14; j++) {
        Card card;
        card = new Card(j, i);
        addCard(card);
      }
    }
  }

  public void print() {
    String msg = "Cards in deck: \n";
    for (Card card : deck) {
      msg += "  " + card.toString();
    }
    System.out.println(msg);
  }

  public void shuffle() {
    int card1;
    int card2;
    for (int i = 0; i < deck.size() * 5; i++) { /* shuffle a few times */
      card1 = random.nextInt(deck.size());
      card2 = random.nextInt(deck.size());
      Collections.swap(deck, card1, card2);
    }
  }

}

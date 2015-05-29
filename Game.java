/**
 *
 * @author __MadHatter (alias used on https://www.reddit.com/r/dailyprogrammer)
 */

/* Game.java */

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public final class Game {

  private static final int MIN_PLAYERS = 2;
  private static final int MAX_PLAYERS = 8;

  private Deck deck;
  private ArrayList<Player> players; /* this list includes CPU players */
  private ArrayList<Card> communityCards;
  private Scanner in;

  public Game() {
    deck           = new Deck();
    players        = new ArrayList<>();
    communityCards = new ArrayList<>();
    in             = new Scanner(System.in);
  }

  public void start() {

    int numberOfPlayers;
    int numberOfGames;
    int numberOfGamesPlayed = 0;
    int[] wins = new int[8];
    int numberOfTimesBestHandsWon = 0;
    ArrayList<Integer> listOfWinners = new ArrayList<>();
    ArrayList<Integer> listOfUnfoldedWinners = new ArrayList<>();
    ArrayList<String> listOfWinningHands = new ArrayList<>();
    ArrayList<Integer> occurencesOfWinningHands = new ArrayList<>();
    Random random = new Random();

    /* Read number of players. */
    numberOfPlayers = 0;
    while (numberOfPlayers < MIN_PLAYERS || numberOfPlayers > MAX_PLAYERS) {
      System.out.print("How many players ("
        + MIN_PLAYERS + "-"
        + MAX_PLAYERS + ")? ");
      numberOfPlayers = in.nextInt();
    }
    /* Read number of games to be played. */
    numberOfGames = 0;
    while (numberOfGames < 1) {
      System.out.print("How many games should we simulate? ");
      numberOfGames = in.nextInt();
    }
    System.out.println("");
    numberOfGamesPlayed = numberOfGames;

    for (int i = 0; i < 8; i++) {
      wins[i] = 0;
    }

    for (int n = 0; n < numberOfGames; n++) {

      players.clear();
      communityCards.clear();
      listOfWinners.clear();
      listOfUnfoldedWinners.clear();

  //    /* Add human player. */
  //    addPlayer(Player.Type.HUMAN, "");

      /* Add CPU players. */
      for (int i = 0; i < numberOfPlayers; i++) {
        addPlayer(Player.Type.CPU, "");
      }

      /* Initialize deck. */
      deck.initializeDeck();
      deck.shuffle();

      /* Deal cards to players and display. */
      for (int i = 0; i < players.size(); i++) {
        dealCard(i);
        dealCard(i);
      }
      printPlayersCards();
      System.out.println();

      /* Flop */
      System.out.print("Flop:  ");
      deck.burnTopCard();
      for (int i = 0; i < 3; i++) {
        dealCard();
      }
      printCommunityCards();

      /* Turn */
      System.out.print("Turn:  ");
      deck.burnTopCard();
      dealCard();
      printCommunityCards(communityCards.size()-1);

      /* CPUs decide whether they want to fold or not. */
      for (int i = 0; i < players.size(); i++) {
        Rank rank = new Rank(communityCards, players.get(i).getHand());
        if (rank.getDegree() >= 8) {
          if (random.nextInt(100) >= 30) {
            players.get(i).fold();
            System.out.println(players.get(i).getName() + " has folded.");
          }
        }
      }

      /* River */
      System.out.print("River: ");
      deck.burnTopCard();
      dealCard();
      printCommunityCards(communityCards.size()-1);

      /* Display hands with ranks. */
      System.out.println("");
      for (int i = 0; i < players.size(); i++) {
        Rank rank = new Rank(communityCards, players.get(i).getHand());
        System.out.print(players.get(i).getName());
        if (players.get(i).hasFolded()) {
          System.out.print(" would've had: ");
        }
        else {
          System.out.print(" has: ");
        }
        System.out.println(rank.toString());
      }

      /* Display winners. */
      boolean winningHandCounted = false;
      System.out.println("\nWinners: ");
      listOfWinners = getWinners();
      for (int i = 0; i < listOfWinners.size(); i++) {
        System.out.println(players.get(listOfWinners.get(i)).getName());
        wins[listOfWinners.get(i)]++;
        Rank rank = new Rank(communityCards, players.get(listOfWinners.get(i)).getHand());
        if (!winningHandCounted) {
          if (listOfWinningHands.indexOf(rank.toMethodString()) == -1) {
            listOfWinningHands.add(rank.toMethodString());
            occurencesOfWinningHands.add(1);
          }
          else {
            int index = listOfWinningHands.indexOf(rank.toMethodString());
            occurencesOfWinningHands.set(index, occurencesOfWinningHands.get(index)+1);
          }
          winningHandCounted = true;
        }
      }

      /* Number of times the best hand equals the highest hand. */
      if (listOfWinners.size() > 0) {
        listOfUnfoldedWinners = getUnfoldedWinners();
        Rank rank = new Rank(communityCards, players.get(listOfWinners.get(0)).getHand());
        for (int i = 0; i < listOfUnfoldedWinners.size(); i++) {
          Rank rank2 = new Rank(communityCards, players.get(listOfUnfoldedWinners.get(i)).getHand());
          if (rank.toMethodString().equalsIgnoreCase(rank2.toMethodString())
              && i < listOfWinners.size()
              && i != listOfWinners.get(i)) {
            numberOfTimesBestHandsWon++;
          }
        }
      }
      else {
        numberOfGamesPlayed--;
      }

      System.out.println("");

    }

    /* Print report. */
    System.out.println("\nSimulation report:");
    System.out.println("Total number of games with winners: " + numberOfGamesPlayed);
    System.out.println("Total wins for each player: ");
    for (int i = 0; i < players.size(); i++) {
      double percent = (double)wins[i] / (double)numberOfGamesPlayed * (double)100;
      System.out.print("  " + players.get(i).getName() + ": " + wins[i]);
      System.out.printf(" (%.1f%%)\n", percent);
    }
    System.out.println("Number of times the best hand was the highest hand: " + numberOfTimesBestHandsWon);
    System.out.println("Winning hand count: ");
    for (int i = 0; i < occurencesOfWinningHands.size(); i++) {
      System.out.printf("%8d  ", occurencesOfWinningHands.get(i));
      System.out.println(listOfWinningHands.get(i));
    }
  }

  public void addPlayer(Player.Type type, String name) {
    String newName = name;
    if (!newName.equalsIgnoreCase("") && type == Player.Type.CPU) {
      newName = "[CPU] " + newName;
    }
    else if (newName.equalsIgnoreCase("") && type == Player.Type.CPU) {
      newName = "[CPU] Player " + (players.size() + 1);
    }
    else if (newName.equalsIgnoreCase("") && type == Player.Type.HUMAN) {
      newName = "Player " + (players.size() + 1);
    }
    players.add(new Player(type, newName));
  }

  public void removePlayer(int id) {
    if (id >= 0 && id < players.size()) {
      players.remove(id);
    }
  }

  public void removePlayer(String name) {
    for (int i = 0; i < players.size(); i++) {
      if (players.get(i).getName().equalsIgnoreCase(name)) {
        players.remove(i);
        break;
      }
    }
  }

  public void printCommunityCards() {
    String msg = "";
    for (int i = 0; i < communityCards.size(); i++) {
      msg += communityCards.get(i).toString() + " ";
    }
    System.out.println(msg);
  }

  public void printCommunityCards(int startIndex) {
    String msg = "";
    for (int i = startIndex; i < communityCards.size(); i++) {
      msg += communityCards.get(i).toString() + " ";
    }
    System.out.println(msg);
  }

  public void printPlayersCards() {
    for (int i = 0; i < players.size(); i++) {
      players.get(i).printHand();
    }
  }

  /* Deal community card. */
  private void dealCard() {
    Card newCard = deck.drawCard();
    if (newCard != null) {
      communityCards.add(newCard);
    }
  }

  /* Deal card to specific player. */
  private void dealCard(int player) {
    Card newCard = deck.drawCard();
    if (newCard != null) {
      players.get(player).receiveCard(newCard);
    }
  }

  private ArrayList<Integer> getWinners() {
    int x;
    ArrayList<Integer> listOfWinners = new ArrayList<>();
    int lowestDegree;
    int tmpDegree;
    Rank highestRank;
    Rank tmpRank;

    /* Find first player that hasn't folded. */
    for (x = 0; x < players.size(); x++) {
      if (!players.get(x).hasFolded()) {
        break;
      }
    }

    if (x == players.size()) {
      return listOfWinners;
    }

    lowestDegree = new Rank(communityCards, players.get(x).getHand()).getDegree();
    highestRank = new Rank(communityCards, players.get(x).getHand());
    listOfWinners.add(x);

    for (int i = x+1; i < players.size(); i++) {
      if (!players.get(i).hasFolded()) {
        tmpRank = new Rank(communityCards, players.get(i).getHand());
        tmpDegree = tmpRank.getDegree();

        /* Current player with highest hand. */
        if (tmpDegree < lowestDegree) {
          listOfWinners.clear();
          listOfWinners.add(i);
          lowestDegree = tmpDegree;
          highestRank = tmpRank;
        }
        /* Tie? */
        else if (tmpDegree == lowestDegree) {
          if (tmpRank.getCards().get(0).getValue() > highestRank.getCards().get(0).getValue()) {
            listOfWinners.clear();
            listOfWinners.add(i);
            lowestDegree = tmpDegree;
            highestRank = tmpRank;
          }
          else if (tmpRank.getCards().get(0).getValue() == highestRank.getCards().get(0).getValue()) {
            if (tmpRank.getCards().size() >= 4 && highestRank.getCards().size() >= 4) {
              if (tmpRank.getCards().get(3).getValue() > highestRank.getCards().get(3).getValue()) {
                listOfWinners.clear();
                listOfWinners.add(i);
                lowestDegree = tmpDegree;
                highestRank = tmpRank;
              }
              else if (tmpRank.getCards().get(3).getValue() == highestRank.getCards().get(3).getValue()) {
                listOfWinners.add(i);
              }
            }
            else {
              listOfWinners.add(i);
            }
          }
        }
      }
    }

    return listOfWinners;
  }

  private ArrayList<Integer> getUnfoldedWinners() {
    ArrayList<Integer> listOfWinners = new ArrayList<>();
    int winner = 0;
    int lowestDegree = new Rank(communityCards, players.get(winner).getHand()).getDegree();
    int tmpDegree;
    Rank highestRank = new Rank(communityCards, players.get(winner).getHand());
    Rank tmpRank;

    listOfWinners.add(0);

    for (int i = 1; i < players.size(); i++) {
      tmpRank = new Rank(communityCards, players.get(i).getHand());
      tmpDegree = tmpRank.getDegree();

      /* Current player with highest hand. */
      if (tmpDegree < lowestDegree) {
        listOfWinners.clear();
        listOfWinners.add(i);
        lowestDegree = tmpDegree;
        highestRank = tmpRank;
      }
      /* Tie? */
      else if (tmpDegree == lowestDegree) {
        if (tmpRank.getCards().get(0).getValue() > highestRank.getCards().get(0).getValue()) {
          listOfWinners.clear();
          listOfWinners.add(i);
          lowestDegree = tmpDegree;
          highestRank = tmpRank;
        }
        else if (tmpRank.getCards().get(0).getValue() == highestRank.getCards().get(0).getValue()) {
          if (tmpRank.getCards().size() >= 4 && highestRank.getCards().size() >= 4) {
            if (tmpRank.getCards().get(3).getValue() > highestRank.getCards().get(3).getValue()) {
              listOfWinners.clear();
              listOfWinners.add(i);
              lowestDegree = tmpDegree;
              highestRank = tmpRank;
            }
            else if (tmpRank.getCards().get(3).getValue() == highestRank.getCards().get(3).getValue()) {
              listOfWinners.add(i);
            }
          }
          else {
            listOfWinners.add(i);
          }
        }
      }
    }

    return listOfWinners;
  }

}

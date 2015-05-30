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

  public Game() {
    deck           = new Deck();
    players        = new ArrayList<>();
    communityCards = new ArrayList<>();
  }

  public void start() {

    int numberOfPlayers;
    int numberOfGames;
    int numberOfGamesPlayed;
    int numberOfTimesBestHandWasWinningHand;
    int[] wins = new int[8];
    ArrayList<Integer> listOfWinners = new ArrayList<>();
    ArrayList<Integer> listOfAllWinners = new ArrayList<>();
    ArrayList<String> listOfWinningHands = new ArrayList<>();
    ArrayList<Integer> occurencesOfWinningHands = new ArrayList<>();
    Random random = new Random();
    Scanner in;

    in = new Scanner(System.in);
    /* Read number of players. */
    numberOfPlayers = 0;
    while (numberOfPlayers < MIN_PLAYERS || numberOfPlayers > MAX_PLAYERS) {
      System.out.print("How many players ("
        + MIN_PLAYERS + "-"
        + MAX_PLAYERS + ")? ");
      numberOfPlayers = Integer.parseInt(in.nextLine());
    }
    /* Read number of games to be played. */
    numberOfGames = 0;
    while (numberOfGames < 1) {
      System.out.print("How many games should we simulate? ");
      numberOfGames = Integer.parseInt(in.nextLine());
    }
    System.out.println("");
//    numberOfPlayers = 8;
//    numberOfGames = 1000;

    numberOfGamesPlayed = numberOfGames;
    numberOfTimesBestHandWasWinningHand = 0;

    for (int i = 0; i < 8; i++) {
      wins[i] = 0;
    }

    for (int n = 0; n < numberOfGames; n++) {

      System.out.println("Starting new game...\n");

      players.clear();
      communityCards.clear();

      /* Add human player. */
//      addPlayer(Player.Type.HUMAN, "");

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
          if (random.nextInt(100) >= 50) {
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
      for (int winner : listOfWinners) {
        wins[winner]++;
        System.out.println(players.get(winner).getName());
        Rank rank = new Rank(communityCards, players.get(winner).getHand());
        if (!winningHandCounted) {
          if (listOfWinningHands.indexOf(rank.getDegreeString()) == -1) {
            listOfWinningHands.add(rank.getDegreeString());
            occurencesOfWinningHands.add(1);
          }
          else {
            int index = listOfWinningHands.indexOf(rank.getDegreeString());
            occurencesOfWinningHands.set(index, occurencesOfWinningHands.get(index)+1);
          }
          winningHandCounted = true;
        }
      }

      /* Number of times the best hand equals the highest hand. */
      listOfAllWinners = getAllWinners();
      if (listOfWinners.size() > 0) {
        Rank winningRank = new Rank(communityCards, players.get(listOfWinners.get(0)).getHand());
        Rank allRank = new Rank(communityCards, players.get(listOfAllWinners.get(0)).getHand());
        if (winningRank.toString().equalsIgnoreCase(allRank.toString())) {
          numberOfTimesBestHandWasWinningHand++;
        }
      }
      else {
        numberOfGamesPlayed--;
//          /*
//           *  START - Uncomment this section to see most common highest hand.
//           */
//        Rank rank = new Rank(communityCards, players.get(listOfAllWinners.get(0)).getHand());
//        if (listOfWinningHands.indexOf(rank.getDegreeString()) == -1) {
//          listOfWinningHands.add(rank.getDegreeString());
//          occurencesOfWinningHands.add(1);
//        }
//        else {
//          int index = listOfWinningHands.indexOf(rank.getDegreeString());
//          occurencesOfWinningHands.set(index, occurencesOfWinningHands.get(index)+1);
//        }
//          /*
//           *  END - Uncomment this section to see most common highest hand.
//           */
      }

      System.out.println("");

    }

    /* Print report. */
    System.out.println("----- Simulation Report -----");
    System.out.println("Number of total rounds/games played out: " + numberOfGamesPlayed);
    System.out.println("Number of wins-losses for each player:");
    for (int i = 0; i < players.size(); i++) {
      double percent = (double)wins[i] / (double)numberOfGames * (double)100;
      System.out.print("  " + players.get(i).getName() + ": " + wins[i] + "-" + (numberOfGames-wins[i]));
      System.out.printf(" (%.1f%%)\n", percent);
    }
    System.out.println("Number of times best hand was highest hand: "+ numberOfTimesBestHandWasWinningHand);
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

  public void printCommunityCards() {
    String msg = "";
    for (Card card : communityCards) {
      msg += card.toString() + " ";
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
    for (Player player : players) {
      player.printHand();
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

  private ArrayList<Integer> getAllWinners() {
    int x;
    int lowestDegree;
    int tmpDegree;
    Rank highestRank;
    Rank tmpRank;
    ArrayList<Integer> listOfWinners = new ArrayList<>();

    x = 0;

    lowestDegree = new Rank(communityCards, players.get(x).getHand()).getDegree();
    highestRank = new Rank(communityCards, players.get(x).getHand());
    listOfWinners.add(x);

    for (int i = x+1; i < players.size(); i++) {
      tmpRank = new Rank(communityCards, players.get(i).getHand());
      tmpDegree = tmpRank.getDegree();

      /* Current player with highest hand. */
      if (tmpDegree < lowestDegree) {
        listOfWinners.clear();
        listOfWinners.add(i);
        lowestDegree = tmpDegree;
        highestRank = tmpRank;
      }
      /* Tie with Straights? */
      else if (tmpDegree == lowestDegree && lowestDegree == 5) {
        /* Include comparisons of 1-5 straight vs regular straight.
         * In this case we need to compare the lowest cards because Ace will be the highest as
         * a false positive.
         */
        if (Rank.getLowestCard(tmpRank.getCards()).getValue() > Rank.getLowestCard(highestRank.getCards()).getValue()) {
          listOfWinners.clear();
          listOfWinners.add(i);
          lowestDegree = tmpDegree;
          highestRank = tmpRank;
        }
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

  private ArrayList<Integer> getWinners() {
    int x;
    int lowestDegree;
    int tmpDegree;
    Rank highestRank;
    Rank tmpRank;
    ArrayList<Integer> listOfWinners = new ArrayList<>();

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
        /* Tie with Straights? */
        else if (tmpDegree == lowestDegree && lowestDegree == 5) {
          /* Include comparisons of 1-5 straight vs regular straight.
           * In this case we need to compare the lowest cards because Ace will be the highest as
           * a false positive.
           */
          if (Rank.getLowestCard(tmpRank.getCards()).getValue() > Rank.getLowestCard(highestRank.getCards()).getValue()) {
            listOfWinners.clear();
            listOfWinners.add(i);
            lowestDegree = tmpDegree;
            highestRank = tmpRank;
          }
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

}

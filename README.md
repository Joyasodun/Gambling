# Casino Simulator

A Java-based casino simulation that combines financial account management with two classic casino games: Blackjack and Roulette. This project demonstrates object-oriented programming principles, data structures, input validation, and game logic implementation in a command-line environment.

## Overview

The Casino Simulator allows users to manage a virtual bankroll while participating in casino games. Players can deposit and withdraw funds, place bets, and track their performance throughout a session. The application emphasizes realistic game mechanics and robust user interaction.

## Features

### Account Management
- Randomized starting balance between $100 and $499
- Deposit and withdraw functionality
- Continuous balance tracking
- Validation to prevent invalid transactions
- Automatic game-over detection when funds are depleted

### Blackjack
- Full 52-card deck implementation
- Card shuffling and unique card dealing
- Dynamic Ace valuation (1 or 11)
- Dealer behavior following standard casino rules
- Betting and payout system
- Hidden dealer card mechanic
- Tracking of the last 10 game outcomes
- History viewing during gameplay

### Roulette
- Simulated European roulette wheel (0–36)
- Supported wagers:
  - Red
  - Black
  - Odd
  - Even
- Randomized wheel spins
- Immediate payout calculations

### User Experience
- Comprehensive input validation
- Clear menu-driven navigation
- Real-time game updates
- Simulated dealing and spinning animations

## Technologies Used

- Java
- Object-Oriented Programming (OOP)
- Java Collections Framework
  - ArrayList
  - HashSet
  - ArrayDeque
- Random Number Generation
- Exception Handling
- Command-Line Interface (CLI)

## Project Structure

```text
Gambling.java
│
├── Banking Operations
│   ├── Deposit
│   ├── Withdraw
│   └── Balance Tracking
│
├── Blackjack
│   ├── Card Class
│   ├── Deck Class
│   ├── Hand Evaluation
│   ├── Dealer Logic
│   └── History Tracking
│
├── Roulette
│   ├── Bet Validation
│   ├── Spin Simulation
│   └── Payout Logic
│
└── Main Menu System
```

## Installation and Execution

### Clone the Repository

```bash
git clone https://github.com/yourusername/casino-simulator.git
cd casino-simulator
```

### Compile the Program

```bash
javac Gambling.java
```

### Run the Program

```bash
java Gambling
```

## Sample Workflow

1. Launch the application.
2. Receive a randomized starting balance.
3. Deposit or withdraw funds as desired.
4. Select a casino game from the menu.
5. Place bets and play rounds of Blackjack or Roulette.
6. Monitor balance changes throughout the session.
7. Exit the application at any time.

## Learning Outcomes

This project was developed to strengthen understanding of:

- Object-oriented design and encapsulation
- Custom class implementation
- Collection data structures
- Randomized simulations
- User input validation
- State management
- Game development fundamentals in Java

## Future Enhancements

Potential improvements include:

- Additional casino games (Poker, Craps, Slots)
- Persistent user accounts and balance storage
- Leaderboards and statistics tracking
- Enhanced Blackjack features (splitting, doubling down, insurance)
- Expanded Roulette betting options
- Graphical User Interface (GUI)
- Multiplayer support

## Author

**John Oyasodun**  
Computer Science & Economics  
Washington University in St. Louis

---

This project was developed as part of coursework and independent programming practice to apply object-oriented programming concepts and software development fundamentals in Java.

package com.hokm.personal.my.hokm.model

import android.util.Log

class Game(val animation: HakemAnimation){

    var deck = Deck()
    var hakem = Direction.BOTTOM
    var hokm = Suit.SPADE
    var direction: Direction = Direction.BOTTOM
    var hands: Array<MutableList<Card>> = Array(4){ mutableListOf<Card>() }
    var players: MutableList<Player> = mutableListOf()
    var teams: MutableList<Team> = mutableListOf()
    var table: MutableList<Card> = mutableListOf<Card>()
    fun determineHakem(){
        var j = 0
        var card: Card = deck.getTopCard()
        animation.oneCardDealtForDeterminingHakem(card, direction)


        while (card.value != Card.ACE){
            j++
            card = deck.getTopCard()
            Log.i("CARD", "card "+ card.value + " " + card.suit + " " + j)
            direction = getNextDirection(direction)
            animation.oneCardDealtForDeterminingHakem(card, direction)
        }
        hakem = direction
        animation.hakemDetermined()
        getNewDeck()
        Log.i("ACE", "card ace"+ card.value + " " + card.suit + " " + j)
    }

     fun getNextDirection(direction: Direction): Direction{
        return when(direction){
            Direction.BOTTOM -> Direction.RIGHT
            Direction.RIGHT -> Direction.TOP
            Direction.TOP -> Direction.LEFT
            Direction.LEFT -> Direction.BOTTOM
        }
    }

    var lastIndex = deck.deck.lastIndex+1
    fun dealCards(numCards: Int){

        var dir = hakem
        for(i in 0 .. 3){
            hands[dir.value].addAll(deck.deck.subList(lastIndex-numCards, lastIndex))
            lastIndex -= numCards
            dir = getNextDirection(dir)
        }

        if(numCards == 5){
            var humanPlayer = PlayerHuman("Arash", hands[Direction.BOTTOM.value])
            var rightAI = PlayerAI("right", Direction.RIGHT, hands[Direction.RIGHT.value])
            var topAI = PlayerAI("top", Direction.TOP, hands[Direction.TOP.value])
            var leftAI = PlayerAI("left", Direction.LEFT, hands[Direction.LEFT.value])

            players.add(Direction.BOTTOM.value, humanPlayer)
            players.add(Direction.RIGHT.value, rightAI)
            players.add(Direction.TOP.value, topAI)
            players.add(Direction.LEFT.value, leftAI)

            teams.add(0, Team(players[Direction.BOTTOM.value], players[Direction.TOP.value]))
            teams.add(1, Team(players[Direction.RIGHT.value], players[Direction.LEFT.value]))
        }
        else{
            players[Direction.BOTTOM.value].addHand(hands[Direction.BOTTOM.value])
            players[Direction.RIGHT.value].addHand(hands[Direction.RIGHT.value])
            players[Direction.TOP.value].addHand(hands[Direction.TOP.value])
            players[Direction.LEFT.value].addHand(hands[Direction.LEFT.value])
        }

        animation.cardsDealt(numCards, hakem, hands)
    }

    private fun getNewDeck(){
        deck = Deck()
    }

    fun cardPlayed(card: Card, direction: Direction){
        players[direction.value].hand.remove(card)
        table.add(card)
        if(table.size == 4){
            val winnerTeam = determineTableWinner()
            animation.tableComplete(winnerTeam.playerA?.direction, winnerTeam.score, table)
            table.clear()
        }
    }

    private fun determineTableWinner(): Team {
       val sameSuit = table.all { it.suit == table[0].suit } // if all are of the same suit
        if(sameSuit) {
           return getWinner(table)
        }
        else{
            val hokmCards = table.filter { it.suit == hokm}
            if(hokmCards.any()){
               return getWinner(hokmCards)
            }
            else{
                // high card with the same suit as the first card wins.
                var sameSuitAsFirstCard = table.filter { it.suit == table[0].suit }
                return getWinner(sameSuitAsFirstCard)
            }
        }
    }

    private fun getWinner(cards: List<Card>): Team{
        val winnerCard = cards.maxBy { it.value }
        val winnerPlayer = players.first { it.hand.contains(winnerCard) }
        val winnerTeam = teams.first { it.playerA == winnerPlayer || it.playerB == winnerPlayer }//TODO: check this comparison
        winnerTeam.score++
        return winnerTeam
    }


}

enum class Direction(val value: Int) {
    BOTTOM(0),
    RIGHT(1),
    TOP(2),
    LEFT(3),
}

interface HakemAnimation{
     fun oneCardDealtForDeterminingHakem(card: Card, direction: Direction)
    fun hakemDetermined()
    fun cardsDealt(numCards: Int, hakem: Direction, hands: Array<MutableList<Card>>)
    fun tableComplete(winnerDirection: Direction?, winnerScore: Int, tableCards: List<Card>)
}
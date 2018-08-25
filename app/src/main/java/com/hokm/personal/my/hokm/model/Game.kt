package com.hokm.personal.my.hokm.model

import android.util.Log

class Game(val animation: HakemAnimation){

    var deck = Deck()
    var hakem = Direction.BOTTOM
    var direction: Direction = Direction.BOTTOM
    var hands: Array<MutableList<Card>> = Array(4){ mutableListOf<Card>() }
    var players: MutableList<Player> = mutableListOf()

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
}
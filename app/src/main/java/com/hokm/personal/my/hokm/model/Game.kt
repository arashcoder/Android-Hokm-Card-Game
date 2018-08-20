package com.hokm.personal.my.hokm.model

import android.util.Log

class Game(val animation: HakemAnimation){

    var deck = Deck()
    var hakem = Direction.BOTTOM
    var direction: Direction = Direction.BOTTOM
    var hands: MutableList<List<Card>> = mutableListOf()
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

    fun dealCards(){
        var lastIndex = deck.deck.lastIndex+1
        for(i in 0 .. 3){
            hands.add(deck.deck.subList(lastIndex-5, lastIndex))
            lastIndex -= 5
        }

        animation.fiveCardsDealt(hakem, hands)
    }

    private fun getNewDeck(){
        deck = Deck()
    }



}

enum class Direction {
    BOTTOM,
    RIGHT,
    TOP,
    LEFT,
}

interface HakemAnimation{
     fun oneCardDealtForDeterminingHakem(card: Card, direction: Direction)
    fun hakemDetermined()
    fun fiveCardsDealt(hakem: Direction, hands: MutableList<List<Card>>)
}
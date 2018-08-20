package com.hokm.personal.my.hokm.model

import android.util.Log

class Game(deck: Deck, val animation: HakemAnimation){

    var deck = deck
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
            direction = getNextDirection()
            animation.oneCardDealtForDeterminingHakem(card, direction)
        }
        hakem = direction
        animation.hakemDetermined()
        Log.i("ACE", "card ace"+ card.value + " " + card.suit + " " + j)
    }

    private fun getNextDirection(): Direction{
        return when(direction){
            Direction.BOTTOM -> Direction.RIGHT
            Direction.RIGHT -> Direction.TOP
            Direction.TOP -> Direction.LEFT
            Direction.LEFT -> Direction.BOTTOM
        }
    }

    fun dealCards(){
        deck = Deck()
        var lastIndex = deck.deck.lastIndex
        for(i in 0 .. 3){
            hands[i] = deck.deck.subList(lastIndex-5, lastIndex)
            lastIndex -= 5
        }
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
    fun fiveCardsDealt(hands: MutableList<List<Card>>)
}
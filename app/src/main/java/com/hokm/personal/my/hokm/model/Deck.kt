package com.hokm.personal.my.hokm.model

class Deck {
    var deck: MutableList<Card> = mutableListOf()
    init{
        for(suit in Suit.values()) {
            for (value in Card.MINVALUE .. Card.MAXVALUE) {
                deck.add(Card(suit, value))
            }
        }

        deck.shuffle()
    }

    fun getTopCard(): Card{
        val topCard = deck.last()
        deck.removeAt(deck.lastIndex)
        return topCard
    }

}
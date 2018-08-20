package com.hokm.personal.my.hokm.model

class Card(val suit: Suit, val value: Int) {
    val TWO = 2
    val THREE = 3
    val FOUR = 4
    val FIVE = 5
    val SIX = 6
    val SEVEN = 7
    val EIGHT = 8
    val NINE = 9
    val TEN = 10
    val JACK = 11
    val QUEEN = 12
    val KING = 13


    companion object {
        val MINVALUE = 2
        val MAXVALUE = 14
        val ACE = 14
    }

    fun determineImageName(): String{
        var valName = ""
        when(this.value) {
            TWO -> valName = "two"
            THREE -> valName = "three"
            FOUR -> valName = "four"
            FIVE -> valName = "five"
            SIX -> valName = "six"
            SEVEN -> valName = "seven"
            EIGHT -> valName = "eight"
            NINE -> valName = "nine"
            TEN -> valName = "ten"
            JACK -> valName = "jack"
            QUEEN -> valName = "queen"
            KING -> valName = "king"
            ACE -> valName = "ace"
        }

        var suitName = ""
        suitName = when(this.suit){
            Suit.CLUB -> "c"
            Suit.DIAMOND -> "d"
            Suit.HEART -> "h"
            Suit.SPADE -> "s"
        }

        val res = valName + suitName
//        return getResources().getIdentifier(res, "drawable", "ksmori.hu.ait.spades");
        return res

    }
}
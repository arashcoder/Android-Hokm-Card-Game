package com.hokm.personal.my.hokm.model

abstract class Player {
    val name: String
    val hand: MutableList<Card> = mutableListOf()

    internal abstract var direction: Direction
    protected var toLeft: Int = 0
    protected var toRight: Int = 0
    protected var toAcross: Int = 0

    constructor(name: String, hand: MutableList<Card>) {
        this.name = name
        //this.hand = hand
        addHand(hand)
    }

     fun addHand(newHand: MutableList<Card>) {
         hand.clear()
         //newHand.forEach { it.player = this }
         hand.addAll(newHand)
    }

    abstract fun play(): Card
    fun determineHokm(): Suit {
        return hand[0].suit
    }


}
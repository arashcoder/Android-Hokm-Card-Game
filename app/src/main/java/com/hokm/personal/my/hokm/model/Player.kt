package com.hokm.personal.my.hokm.model

abstract class Player {
    val name: String
    val hand: MutableList<Card>

    internal abstract var direction: Direction
    protected var toLeft: Int = 0
    protected var toRight: Int = 0
    protected var toAcross: Int = 0

    constructor(name: String, hand: MutableList<Card>) {
        this.name = name
        this.hand = hand
    }



     fun addHand(hand: MutableList<Card>) {
         this.hand.addAll(hand)
    }
}
package com.hokm.personal.my.hokm.model

import java.util.*

class PlayerAI(name: String, direction: Direction, cards: MutableList<Card>): Player(name, cards) {
    override var direction: Direction = direction

    override fun play(): Card{
        return hand[Random().nextInt(hand.size)]
    }
}
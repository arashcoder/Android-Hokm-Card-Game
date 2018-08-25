package com.hokm.personal.my.hokm.model

class PlayerAI(name: String, direction: Direction, cards: MutableList<Card>): Player(name, cards) {
    override var direction: Direction = direction
}
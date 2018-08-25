package com.hokm.personal.my.hokm.model

class PlayerHuman(name: String, cards: MutableList<Card>): Player(name, cards) {
    override var direction: Direction = Direction.BOTTOM
}
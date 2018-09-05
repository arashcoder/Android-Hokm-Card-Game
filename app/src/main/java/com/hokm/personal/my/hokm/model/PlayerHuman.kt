package com.hokm.personal.my.hokm.model

class PlayerHuman(name: String, cards: MutableList<Card>): Player(name, cards) {
    override fun play(table: MutableList<Card>,
                      tableHistory: MutableList<MutableList<Card>>,
                      teams: MutableList<Team>,
                      hokm: Suit): Card {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override var direction: Direction = Direction.BOTTOM
}
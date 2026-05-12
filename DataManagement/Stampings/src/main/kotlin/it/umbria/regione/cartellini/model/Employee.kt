package it.umbria.regione.cartellini.model

class Employee(val name: String) : Comparable<Employee> {

    val stampings = mutableListOf<Stamping>()

    override fun compareTo(other: Employee): Int {
        return name.compareTo(other.name)
    }

    override fun toString(): String {
        return name
    }

    fun cleanUnfinishedStampings() {
        stampings.removeIf { !it.isComplete }
    }
}


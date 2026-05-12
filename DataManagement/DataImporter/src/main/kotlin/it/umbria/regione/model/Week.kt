package it.umbria.regione.model

data class Week(val week: Int, val year: Int) {

    override fun toString(): String {
        return "$year/${week.pad()}"
    }
}


package it.umbria.regione.cartellini

import it.umbria.regione.cartellini.model.Employee
import it.umbria.regione.cartellini.model.Stamping
import it.umbria.regione.model.ModelDate
import it.umbria.regione.model.ModelTime
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class StampingsTest {

    @Test
    fun testModelings() {
        val mr = Employee("Mario Rossi")
        val t1 = Stamping(
            employee = mr,
            inDate = ModelDate(22, 4),
            inTime = ModelTime(8, 30),
            outDate = ModelDate(22, 4),
            outTime = ModelTime(16, 30)
        )
        val t2 = Stamping(
            employee = mr,
            inDate = ModelDate(23, 4),
            inTime = ModelTime(8, 30),
            outDate = ModelDate(23, 4),
            outTime = ModelTime(16, 30)
        )
        val list = listOf(t1, t2)
        Assertions.assertEquals(0, t2.days)
        Assertions.assertEquals(ModelTime(16, 0), list.totalDuration())
        Assertions.assertEquals(true, satisfiedRest(t1, t2))

        val t3 = Stamping(
            employee = mr,
            inDate = ModelDate(23, 4),
            inTime = ModelTime(20, 0),
            outDate = ModelDate(24, 4),
            outTime = ModelTime(8, 0)
        )
        Assertions.assertEquals(1, t3.days)
        Assertions.assertEquals(ModelTime(12, 0), t3.duration)
    }
}

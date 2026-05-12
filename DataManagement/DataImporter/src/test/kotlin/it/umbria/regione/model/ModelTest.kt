package it.umbria.regione.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.Calendar

class ModelTest {

    @Test
    fun testDate() {
        val d1 = ModelDate(22, 4)
        val d2 = ModelDate(24, 4)
        Assertions.assertEquals(0, d2.daysBetween(d2))
        Assertions.assertEquals(2, d2.daysBetween(d1))
        Assertions.assertEquals(-2, d1.daysBetween(d2))
        Assertions.assertEquals("22/04/2024", d1.toString())
        Assertions.assertEquals(d1, ModelDate(22, 4))
        Assertions.assertEquals("2024-05-04".toModelDate(), ModelDate(4, 5, 2024))
        Assertions.assertEquals("04/05/2024".toModelDate(), ModelDate(4, 5, 2024))
    }

    @Test
    fun testTime() {
        val o1 = ModelTime(8, 30)
        val o2 = ModelTime(16, 30)
        Assertions.assertEquals("08:30", o1.toString())
        Assertions.assertEquals(ModelTime(8, 0), o2 - o1)
        Assertions.assertEquals(ModelTime(25, 0), o2 + o1)
        Assertions.assertEquals("12:34".toModelTime(), ModelTime(12, 34))
    }

    @Test
    fun testDateTime() {
        Assertions.assertEquals(
            "2024-05-04T12:34".toModelDateTime(),
            ModelDateTime(ModelDate(4, 5, 2024), ModelTime(12, 34))
        )
        Assertions.assertEquals(
            "04/05/2024T12:34".toModelDateTime(),
            ModelDateTime(ModelDate(4, 5, 2024), ModelTime(12, 34))
        )
        Assertions.assertEquals("2024-05-04".toModelDateTime(), ModelDateTime(date = ModelDate(4, 5, 2024)))
        Assertions.assertEquals("12:34".toModelDateTime(), ModelDateTime(time = ModelTime(12, 34)))

        val cal = Calendar.getInstance().apply { set(Calendar.SECOND, 0) }
        val md = ModelDateTime()
        Assertions.assertEquals(cal.time.toString(), md.toJavaDate().toString())
    }
}

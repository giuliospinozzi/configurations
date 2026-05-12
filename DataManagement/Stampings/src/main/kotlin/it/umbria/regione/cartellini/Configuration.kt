package it.umbria.regione.cartellini

import it.umbria.regione.model.ModelTime
import java.io.File

val maxDailyShiftDuration = ModelTime(7, 12)
val maxWeeklyShiftDuration = ModelTime(36, 0)
val minShiftRest = ModelTime(11, 0)
val minTimeForValidShift = ModelTime(1, 0)
val minTimeBeforeNextShift = ModelTime(2, 0)
val dayDuration = ModelTime(24, 0)
const val anonymous = false

internal val workingDir = File("${System.getProperty("user.home")}/OneDrive/Documents/Work/Regione Umbria/Cartellini")
internal val inputFile = File(workingDir, /*"TracciatoRecord-presenze.xlsx")*/ "CartelliniAutisti-ORIGINALE.xlsx")


package it.umbria.regione.config

import it.umbria.regione.model.ModelDate
import it.umbria.regione.model.ModelDateTime
import it.umbria.regione.model.toModelDate
import it.umbria.regione.model.toModelDateTime
import org.kohsuke.args4j.CmdLineException
import org.kohsuke.args4j.CmdLineParser
import org.kohsuke.args4j.Option
import org.kohsuke.args4j.OptionDef
import org.kohsuke.args4j.spi.OneArgumentOptionHandler
import org.kohsuke.args4j.spi.Setter
import java.io.File

open class DefaultParameters {

    private companion object {
        init {
            CmdLineParser.registerHandler(ModelDate::class.java, StampDateHandler::class.java)
            CmdLineParser.registerHandler(ModelDateTime::class.java, StampDateTimeHandler::class.java)
        }
    }

    @Option(name = "--help", help = true, usage = "Show this help message and exit")
    var help: Boolean = false

    @Option(name = "--config", usage = "Configuration file")
    var configFile: String = "config.properties"

    fun printConfig() {
        println(this)
    }

    fun mergeArguments(file: File, args: Array<String>) {
        println("Using configuration from ${file.absolutePath}")
        val lines = file.readLines()
        val fileArgs = lines.asSequence().filterNot {
            it.isBlank() // removes empty lines
        }.map {
            "--$it" // add --
        }.map {
            it.split("=") // split =
        }.filterNot {
            args.contains(it[0])// removed custom args
        }.flatten().toList().toTypedArray()
        readFromArgs(fileArgs + args)
    }

    fun readFromArgs(args: Array<String>) {
        val parser = CmdLineParser(this)
        try {
            parser.parseArgument(*args)
        } catch (ex: CmdLineException) {
            parser.printUsage(System.out)
            throw RuntimeException(ex)
        }
    }

    fun printUsage() {
        val parser = CmdLineParser(this)
        parser.printUsage(System.out)
    }
}


class StampDateHandler(parser: CmdLineParser, option: OptionDef, setter: Setter<in ModelDate>) :
    OneArgumentOptionHandler<ModelDate>(parser, option, setter) {

    override fun parse(argument: String): ModelDate {
        return argument.toModelDate()
    }
}

class StampDateTimeHandler(parser: CmdLineParser, option: OptionDef, setter: Setter<in ModelDateTime>) :
    OneArgumentOptionHandler<ModelDateTime>(parser, option, setter) {

    override fun parse(argument: String): ModelDateTime {
        return argument.toModelDateTime()
    }
}

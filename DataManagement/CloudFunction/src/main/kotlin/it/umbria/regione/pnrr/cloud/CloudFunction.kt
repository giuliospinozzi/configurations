package it.umbria.regione.pnrr.cloud

import com.google.cloud.functions.HttpFunction
import com.google.cloud.functions.HttpRequest
import com.google.cloud.functions.HttpResponse
import it.umbria.regione.pnrr.db.PNRRDatabase
import it.umbria.regione.pnrr.io.ImportPNRRFromExcel

class CloudFunction : HttpFunction {

    override fun service(request: HttpRequest, response: HttpResponse) {
        val param = CloudParameters(request.inputStream)
        PNRRDatabase(param).use { db ->
            val im = ImportPNRRFromExcel(db)
            im.importExcel(param)
            response.writer.write("Data imported")
        }
    }
}

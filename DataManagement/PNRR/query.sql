SELECT
    P.`__id` AS `__id`,
    P.`dataAggiornamento` AS `DataAggiornamento`,
    P.`codiceIdentificativoProgetto` AS `CodiceIdentificativoProgetto`,
    P.`descrizioneProgetto` AS `DescrizioneProgetto`,
    P.`CUP` AS `CUP`,
    P.`naturaCUP` AS `NaturaCUP`,
    P.`CLP` AS `CLP`,
    P.`statoAvanzamento` AS `StatoAvanzamento`,
    P.`dataInizioPrevista` AS `DataInizioPrevista`,
    P.`dataFinePrevista` AS `DataFinePrevista`,
    P.`dataInizioEffettiva` AS `DataInizioEffettiva`,
    P.`dataFineEffettiva` AS `DataFineEffettiva`,
    P.`settore` AS `Settore`,
    P.`ministeroTitolare` AS `MinisteroTitolare`,
    P.`strutturaCompetenteAttuatore` AS `StrutturaCompetenteAttuatore`,
    P.`esitoPrevalidazione` AS `EsitoPrevalidazione`,
    P.`dataUltimaPrevalidazione` AS `DataUltimaPrevalidazione`,
    P.`esitoValidazione` AS `EsitoValidazione`,
    P.`dataUltimaValidazione` AS `DataUltimaValidazione`,
    S.`nome` AS `SoggettoAttuatore`,
    S.`provincia` AS `Provincia`,
    F.`risorseFinanziarie` AS `RisorseFinanziarie`,
    F.`impegniTotali` AS `ImpegniTotali`,
    F.`pagamentiTotali` AS `PagamentiTotali`,
    F.`importoDaRealizzare` AS `ImportoDaRealizzare`,
    F.`importoRealizzatoAnno` AS `ImportoRealizzatoAnno`,
    F.`finanziamentoTotale` AS `FinanziamentoTotale`,
    F.`finanziamento` AS `FinanziamentoPNRR`,
    F.`importoPagamentoValidatoRGS` AS `ImportoPagamentoValidatoRGS`,
    F.`finanziamentoStatoFOI` AS `FinanziamentoStatoFOI`,
    Missione.`nome` AS `Missione`,
    Missione.`descrizione` AS `DescrizioneMissione`,
    Com.`nome` AS `Componente`,
    Com.`descrizione` AS `DescrizioneComponente`,
    Mis.`nome` AS `Misura`,
    Mis.`descrizione` AS `DescrizioneMisura`,
    Sub.`nome` AS `Submisura`,
    Sub.`descrizione` AS `DescrizioneSubmisura`
FROM
    `Progetti` P LEFT JOIN `Finanziamenti` F ON P.`finanziamentoPNRR` = F.`__id`
                 LEFT JOIN `Soggetti` AS S ON P.`soggettoAttuatore` = S.`__id`
                 LEFT JOIN `ProgettiInterventi` AS `ProgettiInterventi` ON P.`__id` = `ProgettiInterventi`.`progetto`
                 LEFT JOIN `PNRRSubmisure` AS Sub ON `ProgettiInterventi`.`riferimentoIntervento` = Sub.`nome`
                 LEFT JOIN `PNRRMisure` AS Mis ON Sub.`parent` = Mis.`nome`
                 LEFT JOIN `PNRRComponenti` AS Com ON Mis.`parent` = Com.`nome`
                 LEFT JOIN `PNRRMissioni` AS Missione ON Com.`parent` = Missione.`nome`
ORDER BY P.dataAggiornamento, p.codiceIdentificativoProgetto





SELECT
    SubQuery.`codiceIdentificativoProgetto`,
    SubQuery.`CUP`,
    SubQuery.`FinanziamentoTotalePrecedente` AS `FinanziamentoTotaleGiugno`,
    SubQuery.`finanziamentoTotale` AS `FinanziamentoTotaleLuglio`
FROM
    (
        SELECT
            P.`codiceIdentificativoProgetto`,
            P.`CUP`,
            P.`dataAggiornamento`,
            F.`finanziamentoTotale`,
            LAG(F.`finanziamentoTotale`) OVER (PARTITION BY P.`codiceIdentificativoProgetto` ORDER BY P.`dataAggiornamento`) AS `FinanziamentoTotalePrecedente`
        FROM
            `Progetti` P
                LEFT JOIN
            `Finanziamenti` F ON P.`finanziamentoPNRR` = F.`__id`
    ) AS SubQuery
WHERE
    SubQuery.`finanziamentoTotale` <> SubQuery.`FinanziamentoTotalePrecedente`
ORDER BY
    ABS(`FinanziamentoTotaleLuglio` - `FinanziamentoTotaleGiugno`) DESC,
    SubQuery.`codiceIdentificativoProgetto`,
    SubQuery.`dataAggiornamento`;

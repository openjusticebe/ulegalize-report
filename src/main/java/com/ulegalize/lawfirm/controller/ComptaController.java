package com.ulegalize.lawfirm.controller;

import com.ulegalize.enumeration.EnumVCOwner;
import com.ulegalize.lawfirm.model.LawfirmToken;
import com.ulegalize.lawfirm.service.LawfirmService;
import com.ulegalize.lawfirm.utils.CalendarEventsUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.LocalJasperReportsContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.*;

@RestController
@RequestMapping("/v1/compta")
@Slf4j
public class ComptaController {
    @Autowired
    LawfirmService lawfirmService;

    @Autowired
    private DataSource dataSource;

    @GetMapping
    public ResponseEntity<Resource> reportComptaList(@RequestParam(required = false) String vcKey,
                                                     @RequestParam(required = false) String searchCriteriaClient,
                                                     @RequestParam(required = false) String searchCriteriaYear,
                                                     @RequestParam(required = false) Long searchCriteriaNumber,
                                                     @RequestParam(required = false) String searchCriteriaPoste,
                                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate,
                                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endDate) {
        LawfirmToken lawfirmToken = (LawfirmToken) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        log.info("Entering generate reportComptaList vckey {}", lawfirmToken.getVcKey());

        try {
            String reportPath = "reports/compta/";

            InputStream jasperfile = this.getClass().getClassLoader().getResourceAsStream(reportPath + "compta_list.jasper");

            LocalJasperReportsContext ctx = new LocalJasperReportsContext(DefaultJasperReportsContext.getInstance());
            ctx.setClassLoader(this.getClass().getClassLoader());
            // load the precompiled .jasper files
            JasperReport mainJasperReport = (JasperReport) JRLoader.loadObject(jasperfile);

            JasperFillManager fillmgr = JasperFillManager.getInstance(ctx);
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("currency", lawfirmToken.getSymbolCurrency());
            parameters.put("vcKey", lawfirmToken.getVcKey());
            parameters.put("userId", lawfirmToken.getUserId().intValue());
            parameters.put("title", "Liste de Comptabilités");
            parameters.put("numberLbl", "Totaux: ");
            parameters.put("tvacLbl", "TVAC: ");
            parameters.put("htvaLbl", "HTVA: ");
            parameters.put("tvaLbl", "TVA: ");
            parameters.put("costLbl", "Cout total: ");
            parameters.put("timeLbl", "Temps total: ");
            List<Integer> vckeyShare = new ArrayList<>();
            vckeyShare.add(EnumVCOwner.OWNER_VC.getId());
            vckeyShare.add(EnumVCOwner.NOT_OWNER_VC.getId());
            parameters.put("isShareDossier", vckeyShare);
            parameters.put("searchCriteriaClient", searchCriteriaClient != null ? searchCriteriaClient : "");
            parameters.put("searchCriteriaYear", searchCriteriaYear);
            parameters.put("searchCriteriaNumber", searchCriteriaNumber != null ? searchCriteriaNumber.intValue() : null);
            parameters.put("searchCriteriaPoste", searchCriteriaPoste != null && !searchCriteriaPoste.isEmpty() ? searchCriteriaPoste : null);
            parameters.put("startDate", CalendarEventsUtil.convertToDateViaInstant(startDate));
            parameters.put("endDate", CalendarEventsUtil.convertToDateViaInstant(endDate));
            parameters.put("SUBREPORT_DIR", reportPath);

            Connection connection = dataSource.getConnection();
            JasperPrint jasperPrint = fillmgr.fill(mainJasperReport, parameters, connection);

            byte[] pdf = Base64.getEncoder().encode(JasperExportManager.exportReportToPdf(jasperPrint));
            ByteArrayResource fileResponse = new ByteArrayResource(pdf);

            connection.close();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=prestation_list.pdf");
            log.info("Report compta generated vckey {}", lawfirmToken.getVcKey());

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body((fileResponse))
                    ;
        } catch (JRException ex) {
            log.error("erreur de reporting", ex);
        } catch (SQLException e) {
            log.error("SQLException ********", e);
        }
        return null;
    }

}

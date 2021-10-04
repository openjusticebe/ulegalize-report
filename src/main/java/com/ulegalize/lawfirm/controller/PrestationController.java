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
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.*;

@RestController
@RequestMapping("/prestation")
@Slf4j
public class PrestationController {
    @Autowired
    LawfirmService lawfirmService;

    @Autowired
    private DataSource dataSource;

    @GetMapping
    public ResponseEntity<Resource> reportPrestationList(@RequestParam Boolean isShareVcKey,
                                                         @RequestParam(required = false) Integer responsableId,
                                                         @RequestParam(required = false) Integer clientId,
                                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate,
                                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endDate) {
        LawfirmToken lawfirmToken = (LawfirmToken) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Entering generate reportPrestationList vckey {}", lawfirmToken.getVcKey());
        String reportPath = "reports/prestation/";

        InputStream jasperfile = this.getClass().getClassLoader().getResourceAsStream(reportPath + "prestation_list.jasper");

        boolean isShare = isShareVcKey != null ? isShareVcKey : false;
        ByteArrayResource fileResponse = generatereportPrestationGlobal(isShare, startDate, endDate, reportPath, jasperfile, responsableId, clientId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=prestation_list.pdf");
        log.info("Report prestation generated vckey {}", lawfirmToken.getVcKey());

        return ResponseEntity
                .ok()
//                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body((fileResponse))
                ;
    }

    @GetMapping(path = "/dossier/{dossierId}")
    public ResponseEntity<Resource> reportPrestationListByDossier(@PathVariable Integer dossierId,
                                                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate,
                                                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endDate) {
        LawfirmToken lawfirmToken = (LawfirmToken) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Entering generate reportPrestationListByDossier vckey {} and dossierId {}", lawfirmToken.getVcKey(), dossierId);
        String reportPath = "reports/prestation/dossier/";

        InputStream jasperfile = this.getClass().getClassLoader().getResourceAsStream(reportPath + "prestation_list_dossier.jasper");

        ByteArrayResource fileResponse = generatereportPrestationByDossier(startDate, endDate, reportPath, jasperfile, dossierId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=prestation_list_dossier.pdf");
        log.info("Report prestation generated vckey {}", lawfirmToken.getVcKey());

        return ResponseEntity
                .ok()
//                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body((fileResponse))
                ;
    }

    private ByteArrayResource generatereportPrestationGlobal(boolean isShareVcKey, ZonedDateTime startDate, ZonedDateTime endDate, String reportPath, InputStream jasperfile, Integer responsibleId, Integer clientId) {
        return generatereportPrestation(isShareVcKey, startDate, endDate, reportPath, jasperfile, null, responsibleId, clientId);
    }

    private ByteArrayResource generatereportPrestationByDossier(ZonedDateTime startDate, ZonedDateTime endDate, String reportPath, InputStream jasperfile, Integer dossierId) {
        return generatereportPrestation(false, startDate, endDate, reportPath, jasperfile, dossierId, null, null);
    }

    private ByteArrayResource generatereportPrestation(boolean isShareVcKey, ZonedDateTime startDate, ZonedDateTime endDate, String reportPath, InputStream jasperfile, Integer dossierId, Integer responsibleId, Integer clientId) {
        try {
            LawfirmToken lawfirmToken = (LawfirmToken) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            log.info("Entering generate reportPrestationList vckey {}", lawfirmToken.getVcKey());


//             report context
            LocalJasperReportsContext ctx = new LocalJasperReportsContext(DefaultJasperReportsContext.getInstance());
            ctx.setClassLoader(this.getClass().getClassLoader());
            // load the precompiled .jasper files
            JasperReport mainJasperReport = (JasperReport) JRLoader.loadObject(jasperfile);

            JasperFillManager fillmgr = JasperFillManager.getInstance(ctx);
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("currency", lawfirmToken.getSymbolCurrency());
            parameters.put("vcKey", lawfirmToken.getVcKey());
            parameters.put("userId", lawfirmToken.getUserId().intValue());
            parameters.put("startDate", CalendarEventsUtil.convertToDateViaInstant(startDate));
            parameters.put("endDate", CalendarEventsUtil.convertToDateViaInstant(endDate));
            parameters.put("title", "Prestation");
            parameters.put("periodLbl", "Période: ");
            parameters.put("numberLbl", "Nombre: ");
            parameters.put("costLbl", "Cout total: ");
            parameters.put("timeLbl", "Temps total: ");
            List<Integer> vckeyShare = new ArrayList<>();
            vckeyShare.add(EnumVCOwner.OWNER_VC.getId());
            if (isShareVcKey) {
                vckeyShare.add(EnumVCOwner.NOT_OWNER_VC.getId());
            }
            parameters.put("isShareDossier", vckeyShare);
            parameters.put("dossierId", dossierId);
            parameters.put("responsibleId", responsibleId);
            parameters.put("clientId", clientId);
            parameters.put("SUBREPORT_DIR", reportPath);

            Connection connection = dataSource.getConnection();
            JasperPrint jasperPrint = fillmgr.fill(mainJasperReport, parameters, connection);

            byte[] pdf = Base64.getEncoder().encode(JasperExportManager.exportReportToPdf(jasperPrint));
            ByteArrayResource fileResponse = new ByteArrayResource(pdf);

            connection.close();
            return fileResponse;

        } catch (JRException ex) {
            log.error("erreur de reporting", ex);
        } catch (SQLException e) {
            log.error("SQLException ********", e);
        }
        return null;
    }

}

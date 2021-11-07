package com.ulegalize.lawfirm.controller;

import com.ulegalize.lawfirm.model.LawfirmToken;
import com.ulegalize.lawfirm.service.LawfirmService;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.LocalJasperReportsContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/etatHonoraire")
@Slf4j
public class EtatHonoraireController {
    @Autowired
    LawfirmService lawfirmService;

    @Autowired
    private DataSource dataSource;

    @RequestMapping(path = "/{dossierId}", method = RequestMethod.GET)
    public ResponseEntity<Resource> report1(@PathVariable Integer dossierId) {
        try {
            LawfirmToken lawfirmToken = (LawfirmToken) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            log.info("Entering generate etat & frais honoraire dossierId {} and vckey {}", dossierId, lawfirmToken.getVcKey());
            String reportPath = "reports/etathonoraire/";

            InputStream jasperfile = this.getClass().getClassLoader().getResourceAsStream(reportPath + "etat_frais_hono.jasper");

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
            parameters.put("dossierId", dossierId);
            parameters.put("provLbl", "Total honoraire HTVA");
            parameters.put("honorLbl", "Total factures émises HTVA");
            parameters.put("collLbl", "Frais de collaboration");
            parameters.put("balanceLbl", "Balance dossier (à facturer HTVA)");
            parameters.put("deboursLbl", "Débours");
            parameters.put("prestLbl", "Prestation");
            parameters.put("tvaLbl", "TVA");
            parameters.put("fraisLbl", "Frais administratifs ");
            parameters.put("error", "Sauf erreur ou omission");
            parameters.put("details", "Détails");
            parameters.put("tvacLbl", "Total TVAC");
            parameters.put("honoraire", "Honoraires");
            parameters.put("ourRef", "Nos références : ");
            parameters.put("concerne", "Concerne : ");
            parameters.put("title", "État de frais et honoraires");
            parameters.put("total", "Total :");
            parameters.put("SUBREPORT_DIR", reportPath);

            Connection connection = dataSource.getConnection();

            JasperPrint jasperPrint = fillmgr.fill(mainJasperReport, parameters, connection);

            byte[] pdf = Base64.getEncoder().encode(JasperExportManager.exportReportToPdf(jasperPrint));
            ByteArrayResource fileResponse = new ByteArrayResource(pdf);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=invoice.pdf");
            log.info("Report etat & honoraire generated dossierId {}", dossierId);
            connection.close();

            return ResponseEntity
                    .ok()
                    .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
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

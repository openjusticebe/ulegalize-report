package com.ulegalize.lawfirm.service.impl;

import com.ulegalize.lawfirm.model.LawfirmToken;
import com.ulegalize.lawfirm.service.LawfirmService;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@Transactional
public class LawfirmServiceImpl implements LawfirmService {
    @Value("${app.ulegalize.lawfirm.api}")
    String URL_LAWFIRM_API;

    @Autowired
    private DataSource dataSource;

    @Override
    public ByteArrayResource getInvoiceReport(Integer id) throws ResponseStatusException {
        try {
            LawfirmToken lawfirmToken = (LawfirmToken) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            log.info("Entering generate invoice id {} and vckey {}", id, lawfirmToken.getVcKey());
            String reportPath = "reports/invoice/";

            InputStream jasperfile = this.getClass().getClassLoader().getResourceAsStream(reportPath + "invoice_1.jasper");

//             report context
            JasperReportsContext reportsContext = new SimpleJasperReportsContext();
            // load the precompiled .jasper files
            JasperReport mainJasperReport = (JasperReport) JRLoader.loadObject(jasperfile);

            JasperFillManager fillmgr = JasperFillManager.getInstance(reportsContext);
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("currency", lawfirmToken.getSymbolCurrency());
            parameters.put("vcKey", lawfirmToken.getVcKey());
            parameters.put("id_facture", id);
            parameters.put("label_tva", "Montant TVA");
            parameters.put("label_htva", "Montant HTVA");
            parameters.put("label_total", "Le montant total à payer");
            parameters.put("label_details", "Détails");
            parameters.put("label_prestation", "Prestation");
            parameters.put("label_resume", "Résumé");
            parameters.put("validate", "À VALIDER");
            parameters.put("label_invoice_done", "Facture acquittée le ");
            parameters.put("label_dossier", "Affaire");
            parameters.put("label_echeance", "Échéance");
            parameters.put("label_ref", "Nos références");
            parameters.put("SUBREPORT_DIR", reportPath);

            Connection connection = dataSource.getConnection();
            JasperPrint jasperPrint = fillmgr.fill(mainJasperReport, parameters, connection);

            byte[] pdf = Base64.getEncoder().encode(JasperExportManager.exportReportToPdf(jasperPrint));
            // close the new instance
            connection.close();

            return new ByteArrayResource(pdf);
        } catch (JRException ex) {
            log.error("Erreur de reporting", ex);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error while getInvoiceReport " + ex.getMessage());
        } catch (SQLException e) {
            log.error("SQLException ********", e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error while getInvoiceReport " + e.getMessage());

        } catch (Exception e) {
            log.error("Error while getInvoiceReport {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error while getInvoiceReport " + e.getMessage());

        }

    }
}

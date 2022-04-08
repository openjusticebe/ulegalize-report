package com.ulegalize.lawfirm.controller;

import com.ulegalize.enumeration.EnumLanguage;
import com.ulegalize.enumeration.EnumVCOwner;
import com.ulegalize.lawfirm.model.LawfirmToken;
import com.ulegalize.lawfirm.model.enumeration.EnumTranslate;
import com.ulegalize.lawfirm.service.InvoiceService;
import com.ulegalize.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.LocalJasperReportsContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
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
import java.util.*;

@RestController
@RequestMapping("/dossier")
@Slf4j
public class DossierController {
    @Autowired
    InvoiceService invoiceService;

    @Autowired
    private DataSource dataSource;

    @GetMapping
    public ResponseEntity<Resource> reportDossierList(@RequestParam(required = false) String vcKey,
                                                      @RequestParam(required = false) String language,
                                                      @RequestParam(required = false) String searchCriteriaClient,
                                                      @RequestParam(required = false) String searchCriteriaYear,
                                                      @RequestParam(required = false) Long searchCriteriaNumber,
                                                      @RequestParam(required = false) String searchCriteriaInitiale,
                                                      @RequestParam(required = false) Boolean searchCriteriaBalance,
                                                      @RequestParam(required = false) Boolean searchArchived) {
        LawfirmToken lawfirmToken = (LawfirmToken) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        log.info("Entering generate reportDossierList vckey {}", lawfirmToken.getVcKey());

        try {
            String reportPath = "reports/dossier/";

            InputStream jasperfile = this.getClass().getClassLoader().getResourceAsStream(reportPath + "dossier_list.jasper");

            LocalJasperReportsContext ctx = new LocalJasperReportsContext(DefaultJasperReportsContext.getInstance());
            ctx.setClassLoader(this.getClass().getClassLoader());
            // load the precompiled .jasper files
            JasperReport mainJasperReport = (JasperReport) JRLoader.loadObject(jasperfile);

            EnumLanguage enumLanguage = EnumLanguage.FR;
            if (language != null) {
                enumLanguage = EnumLanguage.fromshortCode(language);
            }

            JasperFillManager fillmgr = JasperFillManager.getInstance(ctx);
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("currency", lawfirmToken.getSymbolCurrency());
            parameters.put("vcKey", lawfirmToken.getVcKey());
            parameters.put("userId", lawfirmToken.getUserId().intValue());
            parameters.put("title", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_010.getLabelFr(),
                    EnumTranslate.M_010.getLabelEn(),
                    EnumTranslate.M_010.getLabelNl(),
                    EnumTranslate.M_010.getLabelDe()));
            parameters.put("numberLbl", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_011.getLabelFr(),
                    EnumTranslate.M_011.getLabelEn(),
                    EnumTranslate.M_011.getLabelNl(),
                    EnumTranslate.M_011.getLabelDe()));
            parameters.put("costLbl", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_007.getLabelFr(),
                    EnumTranslate.M_007.getLabelEn(),
                    EnumTranslate.M_007.getLabelNl(),
                    EnumTranslate.M_007.getLabelDe()));
            parameters.put("timeLbl", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_008.getLabelFr(),
                    EnumTranslate.M_008.getLabelEn(),
                    EnumTranslate.M_008.getLabelNl(),
                    EnumTranslate.M_008.getLabelDe()));
            List<Integer> vckeyShare = new ArrayList<>();
            vckeyShare.add(EnumVCOwner.OWNER_VC.getId());
            vckeyShare.add(EnumVCOwner.NOT_OWNER_VC.getId());
            parameters.put("isShareDossier", vckeyShare);
            parameters.put("searchCriteriaClient", searchCriteriaClient != null ? searchCriteriaClient : "");
            parameters.put("searchCriteriaYear", searchCriteriaYear);
            parameters.put("searchCriteriaNumber", searchCriteriaNumber != null ? searchCriteriaNumber.intValue() : null);
            parameters.put("searchCriteriaInitiale", searchCriteriaInitiale != null ? searchCriteriaInitiale : "");
            parameters.put("searchCriteriaBalance", searchCriteriaBalance);
            parameters.put("searchArchived", searchArchived);
            parameters.put("SUBREPORT_DIR", reportPath);

            Connection connection = dataSource.getConnection();
            JasperPrint jasperPrint = fillmgr.fill(mainJasperReport, parameters, connection);

            byte[] pdf = Base64.getEncoder().encode(JasperExportManager.exportReportToPdf(jasperPrint));
            ByteArrayResource fileResponse = new ByteArrayResource(pdf);

            connection.close();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=prestation_list.pdf");
            log.info("Report prestation generated vckey {}", lawfirmToken.getVcKey());

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

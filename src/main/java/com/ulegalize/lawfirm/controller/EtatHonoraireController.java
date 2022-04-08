package com.ulegalize.lawfirm.controller;

import com.ulegalize.enumeration.EnumLanguage;
import com.ulegalize.lawfirm.model.LawfirmToken;
import com.ulegalize.lawfirm.model.enumeration.EnumTranslate;
import com.ulegalize.lawfirm.service.ComptaService;
import com.ulegalize.utils.Utils;
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
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.io.InputStream;
import java.math.BigDecimal;
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
    private ComptaService comptaService;

    @Autowired
    private DataSource dataSource;

    @RequestMapping(path = "/{dossierId}", method = RequestMethod.GET)
    public ResponseEntity<Resource> report1(@PathVariable Long dossierId,
                                            @RequestParam(required = false) String language
    ) {
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

            EnumLanguage enumLanguage = EnumLanguage.FR;
            if (language != null) {
                enumLanguage = EnumLanguage.fromshortCode(language);
            }
            BigDecimal balanceDecimal = comptaService.sumBalanceCompteTiersByDossier(lawfirmToken.getVcKey(), dossierId);

            JasperFillManager fillmgr = JasperFillManager.getInstance(ctx);
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("currency", lawfirmToken.getSymbolCurrency());
            parameters.put("vcKey", lawfirmToken.getVcKey());
            parameters.put("userId", lawfirmToken.getUserId().intValue());
            parameters.put("dossierId", dossierId);
            parameters.put("balanceTiers", balanceDecimal);

            parameters.put("provLbl", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_012.getLabelFr(),
                    EnumTranslate.M_012.getLabelEn(),
                    EnumTranslate.M_012.getLabelNl(),
                    EnumTranslate.M_012.getLabelDe()));
            parameters.put("honorLbl", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_013.getLabelFr(),
                    EnumTranslate.M_013.getLabelEn(),
                    EnumTranslate.M_013.getLabelNl(),
                    EnumTranslate.M_013.getLabelDe()));
            parameters.put("collLbl", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_014.getLabelFr(),
                    EnumTranslate.M_014.getLabelEn(),
                    EnumTranslate.M_014.getLabelNl(),
                    EnumTranslate.M_014.getLabelDe()));
            parameters.put("balanceLbl", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_015.getLabelFr(),
                    EnumTranslate.M_015.getLabelEn(),
                    EnumTranslate.M_015.getLabelNl(),
                    EnumTranslate.M_015.getLabelDe()));
            parameters.put("deboursLbl", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_016.getLabelFr(),
                    EnumTranslate.M_016.getLabelEn(),
                    EnumTranslate.M_016.getLabelNl(),
                    EnumTranslate.M_016.getLabelDe()));
            parameters.put("prestLbl", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_017.getLabelFr(),
                    EnumTranslate.M_017.getLabelEn(),
                    EnumTranslate.M_017.getLabelNl(),
                    EnumTranslate.M_017.getLabelDe()));
            parameters.put("tvaLbl", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_006.getLabelFr(),
                    EnumTranslate.M_006.getLabelEn(),
                    EnumTranslate.M_006.getLabelNl(),
                    EnumTranslate.M_006.getLabelDe()));
            parameters.put("fraisLbl", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_018.getLabelFr(),
                    EnumTranslate.M_018.getLabelEn(),
                    EnumTranslate.M_018.getLabelNl(),
                    EnumTranslate.M_018.getLabelDe()));
            parameters.put("error", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_019.getLabelFr(),
                    EnumTranslate.M_019.getLabelEn(),
                    EnumTranslate.M_019.getLabelNl(),
                    EnumTranslate.M_019.getLabelDe()));
            parameters.put("details", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_020.getLabelFr(),
                    EnumTranslate.M_020.getLabelEn(),
                    EnumTranslate.M_020.getLabelNl(),
                    EnumTranslate.M_020.getLabelDe()));
            parameters.put("tvacLbl", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_021.getLabelFr(),
                    EnumTranslate.M_021.getLabelEn(),
                    EnumTranslate.M_021.getLabelNl(),
                    EnumTranslate.M_021.getLabelDe()));
            parameters.put("honoraire", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_022.getLabelFr(),
                    EnumTranslate.M_022.getLabelEn(),
                    EnumTranslate.M_022.getLabelNl(),
                    EnumTranslate.M_022.getLabelDe()));
            parameters.put("ourRef", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_023.getLabelFr(),
                    EnumTranslate.M_023.getLabelEn(),
                    EnumTranslate.M_023.getLabelNl(),
                    EnumTranslate.M_023.getLabelDe()));
            parameters.put("concerne", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_024.getLabelFr(),
                    EnumTranslate.M_024.getLabelEn(),
                    EnumTranslate.M_024.getLabelNl(),
                    EnumTranslate.M_024.getLabelDe()));
            parameters.put("title", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_025.getLabelFr(),
                    EnumTranslate.M_025.getLabelEn(),
                    EnumTranslate.M_025.getLabelNl(),
                    EnumTranslate.M_025.getLabelDe()));
            parameters.put("total", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_026.getLabelFr(),
                    EnumTranslate.M_026.getLabelEn(),
                    EnumTranslate.M_026.getLabelNl(),
                    EnumTranslate.M_026.getLabelDe()));

            parameters.put("balanceTiersLbl", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_049.getLabelFr(),
                    EnumTranslate.M_049.getLabelEn(),
                    EnumTranslate.M_049.getLabelNl(),
                    EnumTranslate.M_049.getLabelDe()));
            parameters.put("page_lbl", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_040.getLabelFr(),
                    EnumTranslate.M_040.getLabelEn(),
                    EnumTranslate.M_040.getLabelNl(),
                    EnumTranslate.M_040.getLabelDe()));
            parameters.put("prov_lbl", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_050.getLabelFr(),
                    EnumTranslate.M_050.getLabelEn(),
                    EnumTranslate.M_050.getLabelNl(),
                    EnumTranslate.M_050.getLabelDe()));

            parameters.put("SUBREPORT_DIR", reportPath);

            Connection connection = dataSource.getConnection();

            JasperPrint jasperPrint = fillmgr.fill(mainJasperReport, parameters, connection);

            byte[] pdf = Base64.getEncoder().encode(JasperExportManager.exportReportToPdf(jasperPrint));
            ByteArrayResource fileResponse = new ByteArrayResource(pdf);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=state.pdf");
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

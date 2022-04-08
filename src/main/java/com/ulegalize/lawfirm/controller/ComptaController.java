package com.ulegalize.lawfirm.controller;

import com.ulegalize.dto.LawfirmDTO;
import com.ulegalize.enumeration.EnumLanguage;
import com.ulegalize.enumeration.EnumVCOwner;
import com.ulegalize.lawfirm.model.LawfirmToken;
import com.ulegalize.lawfirm.model.dto.ComptaDTO;
import com.ulegalize.lawfirm.model.enumeration.EnumTranslate;
import com.ulegalize.lawfirm.service.ComptaService;
import com.ulegalize.lawfirm.service.LawfirmService;
import com.ulegalize.lawfirm.utils.CalendarEventsUtil;
import com.ulegalize.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
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
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.*;

@RestController
@RequestMapping("/v1/compta")
@Slf4j
public class ComptaController {
    @Autowired
    private LawfirmService lawfirmService;
    @Autowired
    private ComptaService comptaService;

    @Autowired
    private DataSource dataSource;

    @GetMapping
    public ResponseEntity<Resource> reportComptaList(@RequestParam(required = false) String vcKey,
                                                     @RequestParam(required = false) String searchCriteriaClient,
                                                     @RequestParam(required = false) String searchCriteriaYear,
                                                     @RequestParam(required = false) Long searchCriteriaNumber,
                                                     @RequestParam(required = false) String searchCriteriaPoste,
                                                     @RequestParam(required = false) Integer searchCriteriaType,
                                                     @RequestParam(required = false) Integer searchCriteriaCompte,
                                                     @RequestParam(required = false) String language,
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
                    EnumTranslate.M_009.getLabelFr(),
                    EnumTranslate.M_009.getLabelEn(),
                    EnumTranslate.M_009.getLabelNl(),
                    EnumTranslate.M_009.getLabelDe()));
            parameters.put("numberLbl", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_003.getLabelFr(),
                    EnumTranslate.M_003.getLabelEn(),
                    EnumTranslate.M_003.getLabelNl(),
                    EnumTranslate.M_003.getLabelDe()));
            parameters.put("tvacLbl", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_004.getLabelFr(),
                    EnumTranslate.M_004.getLabelEn(),
                    EnumTranslate.M_004.getLabelNl(),
                    EnumTranslate.M_004.getLabelDe()));
            parameters.put("htvaLbl", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_005.getLabelFr(),
                    EnumTranslate.M_005.getLabelEn(),
                    EnumTranslate.M_005.getLabelNl(),
                    EnumTranslate.M_005.getLabelDe()));
            parameters.put("tvaLbl", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_006.getLabelFr(),
                    EnumTranslate.M_006.getLabelEn(),
                    EnumTranslate.M_006.getLabelNl(),
                    EnumTranslate.M_006.getLabelDe()));
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
            parameters.put("searchCriteriaPoste", searchCriteriaPoste != null && !searchCriteriaPoste.isEmpty() ? searchCriteriaPoste : null);
            parameters.put("searchCriteriaType", searchCriteriaType);
            parameters.put("searchCriteriaCompte", searchCriteriaCompte);
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

    @GetMapping(value = "/tiers")
    public ResponseEntity<Resource> reportCompteTiers(@RequestParam(required = false) Integer balanceZero,
                                                      @RequestParam(required = false) Long balance,
                                                      @RequestParam(required = false) Long compteId,
                                                      @RequestParam(required = false) String language,
                                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate,
                                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endDate) {
        LawfirmToken lawfirmToken = (LawfirmToken) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        log.info("Entering generate reportCompteTiers vckey {}", lawfirmToken.getVcKey());
        BigDecimal balanceBig = balance != null ? new BigDecimal(balance) : null;

        List<ComptaDTO> comptaDTOList = comptaService.getBalanceCompteTiersWithDate(lawfirmToken.getVcKey(), startDate, endDate, balanceZero, compteId, balanceBig);
        List<ComptaDTO> comptaDTOGroupList = comptaService.getBalanceCompteTiersGroupByDossier(lawfirmToken.getVcKey(), startDate, endDate, balanceZero, compteId, balanceBig);

        BigDecimal balanceDecimal = (balanceZero == null || balanceZero != 0) ? comptaService.sumBalanceCompteTiers(lawfirmToken.getVcKey(), startDate, endDate, compteId) : BigDecimal.ZERO;

        LawfirmDTO lawfirmDTO = lawfirmService.getLawfirmByVcKey(lawfirmToken.getVcKey());
        try {
            String reportPath = "reports/compta/compte_tiers/";

            InputStream jasperfile = this.getClass().getClassLoader().getResourceAsStream(reportPath + "compte_tiers.jasper");
            JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(comptaDTOList);
            JRBeanCollectionDataSource jrBeanCollectionDataSourceGroup = new JRBeanCollectionDataSource(comptaDTOGroupList);
            LocalJasperReportsContext ctx = new LocalJasperReportsContext(DefaultJasperReportsContext.getInstance());
            ctx.setClassLoader(this.getClass().getClassLoader());
            // load the precompiled .jasper files
            JasperReport mainJasperReport = (JasperReport) JRLoader.loadObject(jasperfile);

            JasperFillManager fillmgr = JasperFillManager.getInstance(ctx);
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("currency", lawfirmToken.getSymbolCurrency());
            parameters.put("vcKey", lawfirmToken.getVcKey());
            if (lawfirmDTO != null) {
                parameters.put("social", lawfirmDTO.getObjetsocial());
            }
            EnumLanguage enumLanguage = EnumLanguage.FR;
            if (language != null) {
                enumLanguage = EnumLanguage.fromshortCode(language);
            }

            parameters.put("userId", lawfirmToken.getUserId().intValue());
            parameters.put("title", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_002.getLabelFr(),
                    EnumTranslate.M_002.getLabelEn(),
                    EnumTranslate.M_002.getLabelNl(),
                    EnumTranslate.M_002.getLabelDe()));
            parameters.put("numberLbl", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_003.getLabelFr(),
                    EnumTranslate.M_003.getLabelEn(),
                    EnumTranslate.M_003.getLabelNl(),
                    EnumTranslate.M_003.getLabelDe()));
            parameters.put("tvacLbl", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_004.getLabelFr(),
                    EnumTranslate.M_004.getLabelEn(),
                    EnumTranslate.M_004.getLabelNl(),
                    EnumTranslate.M_004.getLabelDe()));
            parameters.put("htvaLbl", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_005.getLabelFr(),
                    EnumTranslate.M_005.getLabelEn(),
                    EnumTranslate.M_005.getLabelNl(),
                    EnumTranslate.M_005.getLabelDe()));
            parameters.put("tvaLbl", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_006.getLabelFr(),
                    EnumTranslate.M_006.getLabelEn(),
                    EnumTranslate.M_006.getLabelNl(),
                    EnumTranslate.M_006.getLabelDe()));
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
            parameters.put("details_lbl", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_041.getLabelFr(),
                    EnumTranslate.M_041.getLabelEn(),
                    EnumTranslate.M_041.getLabelNl(),
                    EnumTranslate.M_041.getLabelDe()));
            parameters.put("groupe_dossier_lbl", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_042.getLabelFr(),
                    EnumTranslate.M_042.getLabelEn(),
                    EnumTranslate.M_042.getLabelNl(),
                    EnumTranslate.M_042.getLabelDe()));
            parameters.put("label_dossier", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_034.getLabelFr(),
                    EnumTranslate.M_034.getLabelEn(),
                    EnumTranslate.M_034.getLabelNl(),
                    EnumTranslate.M_034.getLabelDe()));
            parameters.put("account_lbl", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_043.getLabelFr(),
                    EnumTranslate.M_043.getLabelEn(),
                    EnumTranslate.M_043.getLabelNl(),
                    EnumTranslate.M_043.getLabelDe()));
            parameters.put("balance_lbl", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_044.getLabelFr(),
                    EnumTranslate.M_044.getLabelEn(),
                    EnumTranslate.M_044.getLabelNl(),
                    EnumTranslate.M_044.getLabelDe()));
            parameters.put("tiers_lbl", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_045.getLabelFr(),
                    EnumTranslate.M_045.getLabelEn(),
                    EnumTranslate.M_045.getLabelNl(),
                    EnumTranslate.M_045.getLabelDe()));
            parameters.put("note_lbl", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_046.getLabelFr(),
                    EnumTranslate.M_046.getLabelEn(),
                    EnumTranslate.M_046.getLabelNl(),
                    EnumTranslate.M_046.getLabelDe()));
            parameters.put("date_lbl", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_037.getLabelFr(),
                    EnumTranslate.M_037.getLabelEn(),
                    EnumTranslate.M_037.getLabelNl(),
                    EnumTranslate.M_037.getLabelDe()));
            parameters.put("poste_lbl", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_047.getLabelFr(),
                    EnumTranslate.M_047.getLabelEn(),
                    EnumTranslate.M_047.getLabelNl(),
                    EnumTranslate.M_047.getLabelDe()));
            parameters.put("type_lbl", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_048.getLabelFr(),
                    EnumTranslate.M_048.getLabelEn(),
                    EnumTranslate.M_048.getLabelNl(),
                    EnumTranslate.M_048.getLabelDe()));

            parameters.put("balanceZero", balanceZero);
            List<Integer> vckeyShare = new ArrayList<>();
            vckeyShare.add(EnumVCOwner.OWNER_VC.getId());
            vckeyShare.add(EnumVCOwner.NOT_OWNER_VC.getId());
            parameters.put("isShareDossier", vckeyShare);
            parameters.put("comptaDTOList", jrBeanCollectionDataSource);
            parameters.put("comptaDTOGroupList", jrBeanCollectionDataSourceGroup);
            parameters.put("balance", balanceDecimal);
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

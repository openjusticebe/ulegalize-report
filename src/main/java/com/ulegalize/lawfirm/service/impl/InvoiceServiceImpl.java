package com.ulegalize.lawfirm.service.impl;

import com.ulegalize.enumeration.EnumLanguage;
import com.ulegalize.lawfirm.model.LawfirmToken;
import com.ulegalize.lawfirm.model.converter.EntityToInvoiceConverter;
import com.ulegalize.lawfirm.model.dto.FraisAdminDTO;
import com.ulegalize.lawfirm.model.dto.InvoiceDTO;
import com.ulegalize.lawfirm.model.entity.LawfirmUsers;
import com.ulegalize.lawfirm.model.entity.TFactures;
import com.ulegalize.lawfirm.model.enumeration.EnumTranslate;
import com.ulegalize.lawfirm.repository.LawfirmUserRepository;
import com.ulegalize.lawfirm.repository.TDebourRepository;
import com.ulegalize.lawfirm.repository.TFacturesRepository;
import com.ulegalize.lawfirm.service.InvoiceService;
import com.ulegalize.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.DataSource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class InvoiceServiceImpl implements InvoiceService {
    private final LawfirmUserRepository lawfirmUserRepository;
    private final TFacturesRepository tFacturesRepository;
    private final TDebourRepository tDebourRepository;
    private final EntityToInvoiceConverter entityToInvoiceConverter;
    @Autowired
    private DataSource dataSource;

    public InvoiceServiceImpl(LawfirmUserRepository lawfirmUserRepository, TFacturesRepository tFacturesRepository, TDebourRepository tDebourRepository, EntityToInvoiceConverter entityToInvoiceConverter) {
        this.lawfirmUserRepository = lawfirmUserRepository;
        this.tFacturesRepository = tFacturesRepository;
        this.tDebourRepository = tDebourRepository;
        this.entityToInvoiceConverter = entityToInvoiceConverter;
    }

    @Override
    public ByteArrayResource getInvoiceReport(Long id, EnumLanguage enumLanguage) throws ResponseStatusException {
        try {
            LawfirmToken lawfirmToken = (LawfirmToken) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            log.info("Entering generate invoice id {} and vckey {}", id, lawfirmToken.getVcKey());

            Optional<TFactures> optionalTFactures = tFacturesRepository.findByIdFactureAndVcKey(id, lawfirmToken.getVcKey());

            if (optionalTFactures.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found");
            }

            InvoiceDTO invoiceDTO = entityToInvoiceConverter.apply(optionalTFactures.get(), enumLanguage);

            List<FraisAdminDTO> fraisAdminDTOList = getFraisAdminByDossierId(id);
            String reportPath = "reports/invoice/";

            InputStream jasperfile = this.getClass().getClassLoader().getResourceAsStream(reportPath + "invoice_1.jasper");
            JRBeanCollectionDataSource jrBeanCollectionDataSourceFraisAdmin = new JRBeanCollectionDataSource(fraisAdminDTOList, false);

            JasperReportsContext reportsContext = new SimpleJasperReportsContext();
            // load the precompiled .jasper files
            JasperReport mainJasperReport = (JasperReport) JRLoader.loadObject(jasperfile);

            JasperFillManager fillmgr = JasperFillManager.getInstance(reportsContext);
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("fraisAdminList", jrBeanCollectionDataSourceFraisAdmin);
            parameters.put("typeLabel", invoiceDTO.getTypeLabel());
            parameters.put("vat", invoiceDTO.getVat());
            parameters.put("countryLabel", invoiceDTO.getCountryLabel());
            parameters.put("reference", invoiceDTO.getReference());
            parameters.put("yearFacture", invoiceDTO.getYearFacture());
            parameters.put("numFacture", invoiceDTO.getNumFacture());
            String dateValue = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(invoiceDTO.getDateValue());
            String dateEcheance = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(invoiceDTO.getDateEcheance());
            parameters.put("dateValue", dateValue);
            parameters.put("dateEcheance", dateEcheance);
            parameters.put("dossierLabel", invoiceDTO.getDossierLabel());
            parameters.put("street", invoiceDTO.getStreet());
            parameters.put("clientFullName", invoiceDTO.getClientFullName());
            parameters.put("city", invoiceDTO.getCity());

            parameters.put("currency", lawfirmToken.getSymbolCurrency());
            parameters.put("vcKey", lawfirmToken.getVcKey());
            parameters.put("id_facture", Integer.parseInt(id.toString()));
            parameters.put("lbl_page", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_040.getLabelFr(),
                    EnumTranslate.M_040.getLabelEn(),
                    EnumTranslate.M_040.getLabelNl(),
                    EnumTranslate.M_040.getLabelDe()));
            parameters.put("label_tva", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_028.getLabelFr(),
                    EnumTranslate.M_028.getLabelEn(),
                    EnumTranslate.M_028.getLabelNl(),
                    EnumTranslate.M_028.getLabelDe()));
            parameters.put("label_htva", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_029.getLabelFr(),
                    EnumTranslate.M_029.getLabelEn(),
                    EnumTranslate.M_029.getLabelNl(),
                    EnumTranslate.M_029.getLabelDe()));
            parameters.put("label_total", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_030.getLabelFr(),
                    EnumTranslate.M_030.getLabelEn(),
                    EnumTranslate.M_030.getLabelNl(),
                    EnumTranslate.M_030.getLabelDe()));
            parameters.put("label_details", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_020.getLabelFr(),
                    EnumTranslate.M_020.getLabelEn(),
                    EnumTranslate.M_020.getLabelNl(),
                    EnumTranslate.M_020.getLabelDe()));
            parameters.put("label_prestation", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_017.getLabelFr(),
                    EnumTranslate.M_017.getLabelEn(),
                    EnumTranslate.M_017.getLabelNl(),
                    EnumTranslate.M_017.getLabelDe()));
            parameters.put("label_resume", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_031.getLabelFr(),
                    EnumTranslate.M_031.getLabelEn(),
                    EnumTranslate.M_031.getLabelNl(),
                    EnumTranslate.M_031.getLabelDe()));
            parameters.put("validate", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_032.getLabelFr(),
                    EnumTranslate.M_032.getLabelEn(),
                    EnumTranslate.M_032.getLabelNl(),
                    EnumTranslate.M_032.getLabelDe()));
            parameters.put("label_invoice_done", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_033.getLabelFr(),
                    EnumTranslate.M_033.getLabelEn(),
                    EnumTranslate.M_033.getLabelNl(),
                    EnumTranslate.M_033.getLabelDe()));
            parameters.put("label_dossier", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_034.getLabelFr(),
                    EnumTranslate.M_034.getLabelEn(),
                    EnumTranslate.M_034.getLabelNl(),
                    EnumTranslate.M_034.getLabelDe()));
            parameters.put("label_echeance", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_035.getLabelFr(),
                    EnumTranslate.M_035.getLabelEn(),
                    EnumTranslate.M_035.getLabelNl(),
                    EnumTranslate.M_035.getLabelDe()));
            parameters.put("label_ref", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_023.getLabelFr(),
                    EnumTranslate.M_023.getLabelEn(),
                    EnumTranslate.M_023.getLabelNl(),
                    EnumTranslate.M_023.getLabelDe()));
            parameters.put("lbl_annexe", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_036.getLabelFr(),
                    EnumTranslate.M_036.getLabelEn(),
                    EnumTranslate.M_036.getLabelNl(),
                    EnumTranslate.M_036.getLabelDe()));
            parameters.put("lbl_frais_admin", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_018.getLabelFr(),
                    EnumTranslate.M_018.getLabelEn(),
                    EnumTranslate.M_018.getLabelNl(),
                    EnumTranslate.M_018.getLabelDe()));
            parameters.put("lbl_debours", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_016.getLabelFr(),
                    EnumTranslate.M_016.getLabelEn(),
                    EnumTranslate.M_016.getLabelNl(),
                    EnumTranslate.M_016.getLabelDe()));
            parameters.put("lbl_date", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_037.getLabelFr(),
                    EnumTranslate.M_037.getLabelEn(),
                    EnumTranslate.M_037.getLabelNl(),
                    EnumTranslate.M_037.getLabelDe()));
            parameters.put("lbl_collab", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_014.getLabelFr(),
                    EnumTranslate.M_014.getLabelEn(),
                    EnumTranslate.M_014.getLabelNl(),
                    EnumTranslate.M_014.getLabelDe()));
            parameters.put("lbl_desc", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_038.getLabelFr(),
                    EnumTranslate.M_038.getLabelEn(),
                    EnumTranslate.M_038.getLabelNl(),
                    EnumTranslate.M_038.getLabelDe()));
            parameters.put("tax", Utils.getLabel(enumLanguage,
                    EnumTranslate.M_039.getLabelFr(),
                    EnumTranslate.M_039.getLabelEn(),
                    EnumTranslate.M_039.getLabelNl(),
                    EnumTranslate.M_039.getLabelDe()));
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


    private List<FraisAdminDTO> getFraisAdminByDossierId(Long invoiceId) {
        log.debug("getFraisAdminByDossierId with invoiceId {}", invoiceId);
        LawfirmToken lawfirmToken = (LawfirmToken) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<LawfirmUsers> lawfirmUsers = lawfirmUserRepository.findLawfirmUsersByVcKeyAndUserId(lawfirmToken.getVcKey(), lawfirmToken.getUserId());

        if (lawfirmUsers.isPresent()) {
            log.debug("Law firm list {} user id {}", lawfirmUsers.get().getId(), lawfirmToken.getUserId());

            List<Object[]> fraisAdminDTOS = tDebourRepository.findAllByInvoiceIdDossierId(invoiceId);

            return fraisAdminDTOS.stream().map(r -> {
                        String debourTypeDescription = (r[0] != null ? (String) r[0] : null);
                        BigDecimal longConvertPricePerUnit = r[1] != null ? BigDecimal.valueOf(((Double) r[1])) : null;
                        Integer unit = r[2] != null ? Integer.valueOf(((Short) r[2])) : null;
                        String mesureDesc = r[3] != null ? (String) r[3] : null;

                        return new FraisAdminDTO(
                                lawfirmToken.getVcKey(),
                                debourTypeDescription,
                                mesureDesc,
                                unit,
                                longConvertPricePerUnit
                        );
                    })
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

}

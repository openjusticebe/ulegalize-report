package com.ulegalize.lawfirm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ulegalize.enumeration.*;
import com.ulegalize.lawfirm.model.entity.*;
import com.ulegalize.lawfirm.model.enumeration.EnumValid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

@Slf4j
public abstract class EntityTest extends ConfigureTest {
    protected static String USER = "BEST";
    protected static String EMAIL = "my@gmail.com";
    protected static BigDecimal VAT = BigDecimal.valueOf(21);
    @Autowired
    protected TestEntityManager testEntityManager;

    protected ObjectMapper objectMapper;

    protected LawfirmEntity createLawfirm(String vcKey) {
        LawfirmEntity lawfirmEntity = new LawfirmEntity();
        lawfirmEntity.setVckey(vcKey);
        lawfirmEntity.setName("MYLAW name");
        lawfirmEntity.setAlias("MYLAW");
        lawfirmEntity.setEmail("MYLAW@");
        lawfirmEntity.setCity("my city");
        lawfirmEntity.setLicenseId(1);
        lawfirmEntity.setCouthoraire(100);
        lawfirmEntity.setDriveType(DriveType.openstack);
        lawfirmEntity.setDropboxToken("");
        lawfirmEntity.setCompanyNumber("0837777");
        lawfirmEntity.setUserUpd(USER);
        lawfirmEntity.setStartInvoiceNumber(10);
        lawfirmEntity.setNotification(true);

        lawfirmEntity.setLawfirmUsers(new ArrayList<>());
        createLawfirmUsers(lawfirmEntity, EMAIL);

        testEntityManager.persist(lawfirmEntity);

        return lawfirmEntity;
    }

    protected LawfirmUsers createLawfirmUsers(LawfirmEntity lawfirmEntity, String email) {
        LawfirmUsers lawfirmUsers = new LawfirmUsers();
        Date from = DateUtils.addDays(new Date(), -1);
        Date to = DateUtils.addYears(new Date(), 5);
        lawfirmUsers.setValidFrom(from);
        lawfirmUsers.setValidTo(to);
        lawfirmUsers.setIdRole(EnumRole.AVOCAT);
        lawfirmUsers.setLawfirm(lawfirmEntity);
        lawfirmUsers.setUser_upd(USER);
        lawfirmUsers.setPublic(true);
        lawfirmUsers.setSelected(true);
        lawfirmUsers.setActive(true);
        lawfirmUsers.setCouthoraire(100);
        lawfirmEntity.getLawfirmUsers().add(lawfirmUsers);

        TUsers user = createUser(email);
        lawfirmUsers.setUser(user);
        user.setLawfirmUsers(new ArrayList<>());
        user.getLawfirmUsers().add(lawfirmUsers);

        return lawfirmUsers;
    }

    protected TUsers createUser(String email) {
        TUsers user = new TUsers();
        user.setEmail(email);
        user.setFullname("my name");
        user.setAvatar(new byte[0]);
        user.setInitiales("me");
        user.setIdUser("me" + String.valueOf(UUID.randomUUID()).substring(0, 4));
        user.setHashkey("");
        user.setIdValid(EnumValid.VERIFIED);
        user.setLoginCount(0L);
        user.setUserpass("");
        user.setAliasPublic("my alias");
        user.setSpecialities("");
        user.setNotification(true);

        testEntityManager.persist(user);

        return user;

    }

    protected TDossiers createDossier(LawfirmEntity lawfirmEntity, EnumVCOwner enumVCOwner) {
        TDossiers tDossiers = new TDossiers();
        tDossiers.setYear_doss(String.valueOf(LocalDate.now().getYear()));
        tDossiers.setNum_doss(6789L);
        tDossiers.setDoss_type(EnumDossierType.DC.getDossType());
//        tDossiers.setClient_adv();
//        tDossiers.setClient_cab(createClient(lawfirmEntity));
        tDossiers.setVc_key(lawfirmEntity.getVckey());
        tDossiers.setDate_open(new Date());
        tDossiers.setDate_close(null);
        TClients client = createClient(lawfirmEntity);
        TClients clientAdverse = createClient(lawfirmEntity);

        // link client-dossier
        DossierContact dossierContact = new DossierContact();
        dossierContact.setDossiers(tDossiers);
        dossierContact.setClients(client);
        dossierContact.setContactTypeId(EnumDossierContactType.CLIENT);
        dossierContact.setCreUser(USER);
        tDossiers.getDossierContactList().add(dossierContact);

        // link client-dossier
        DossierContact dossierContactAdv = new DossierContact();
        dossierContactAdv.setDossiers(tDossiers);
        dossierContactAdv.setClients(clientAdverse);
        dossierContactAdv.setContactTypeId(EnumDossierContactType.OPPOSING);
        dossierContactAdv.setCreUser(USER);
        tDossiers.getDossierContactList().add(dossierContactAdv);


        TMatiereRubriques tMatiereRubriques = createTMatiereRubriques();
        tDossiers.setTMatiereRubriques(tMatiereRubriques);
        tDossiers.setKeywords("");
        tDossiers.setCouthoraire(100);
        tDossiers.setMemo("");
        tDossiers.setNote("");
        tDossiers.setUserUpd(USER);
        tDossiers.setSuccess_fee_perc(100);
        tDossiers.setSuccess_fee_montant(BigDecimal.ZERO);
        tDossiers.setId_user_resp(lawfirmEntity.getLawfirmUsers().get(0).getUser().getId());

        testEntityManager.persist(tDossiers);
        TDossierRights tDossierRights = new TDossierRights();
        tDossierRights.setDossierId(tDossiers.getIdDoss());
        tDossierRights.setVcUserId(lawfirmEntity.getLawfirmUsers().get(0).getId());
        tDossierRights.setVcOwner(enumVCOwner);
        tDossierRights.setRIGHTS("ACCESS");
        tDossierRights.setTDossiers(tDossiers);
        tDossierRights.setCreUser(USER);
        tDossierRights.setLawfirmUsers(lawfirmEntity.getLawfirmUsers().get(0));

        testEntityManager.persist(tDossierRights);

        tDossiers.setDossierRightsList(new ArrayList<>());
        tDossiers.getDossierRightsList().add(tDossierRights);
        testEntityManager.persist(tDossiers);

        return tDossiers;
    }

    protected TMatiereRubriques createTMatiereRubriques() {
        TMatiereRubriques matiereRubriques = new TMatiereRubriques();
        matiereRubriques.setMatiereRubriqueDesc("matiere desc");
        TMatieres tMatieres = new TMatieres();
        tMatieres.setIdMatiere(getRandomNumberUsingInts(0, 10000));
        tMatieres.setMatiereDesc("Maatiere");

        testEntityManager.persist(tMatieres);
        matiereRubriques.setIdMatiere(tMatieres.getIdMatiere());
        matiereRubriques.setMatieres(tMatieres);

        testEntityManager.persist(matiereRubriques);

        return matiereRubriques;
    }

    public int getRandomNumberUsingInts(int min, int max) {
        Random random = new Random();
        return random.ints(min, max)
                .findFirst()
                .getAsInt();
    }

    protected TClients createClient(LawfirmEntity lawfirmEntity) {
        TClients clients = new TClients();
        clients.setClient_type(EnumClientType.NATURAL_PERSON);
        clients.setF_nom("client nme");
        clients.setF_email("client@business.co,");
        clients.setId_lg("fr");
        if (clients.getVirtualcabClientList() == null) {
            clients.setVirtualcabClientList(new ArrayList<>());
        }

        VirtualcabClient virtualcabClient = new VirtualcabClient();
        virtualcabClient.setTClients(clients);
        virtualcabClient.setLawfirm(lawfirmEntity);
        virtualcabClient.setCreUser(USER);
        clients.getVirtualcabClientList().add(virtualcabClient);

        testEntityManager.persist(clients);

        return clients;
    }

    protected TFrais createTFrais(LawfirmEntity lawfirm, TDossiers dossier, RefCompte refCompte) {
        TClients client = createClient(lawfirm);

        TFrais tFrais = new TFrais();
        tFrais.setIdClient(client.getId_client());
        tFrais.setIdDoss(dossier.getIdDoss());
        tFrais.setTDossiers(dossier);
        tFrais.setVcKey(lawfirm.getVckey());
        tFrais.setIdCompte(refCompte.getIdCompte());
        tFrais.setRefCompte(refCompte);
        tFrais.setIdType(EnumTType.ENTREE);
        RefPoste refPoste = createRefPoste(lawfirm);
        tFrais.setIdPoste(refPoste.getIdPoste());
        tFrais.setRefPoste(refPoste);
        tFrais.setIdTransaction(EnumRefTransaction.fromId(1));
        tFrais.setMontant(BigDecimal.valueOf(121));
        tFrais.setMontantht(BigDecimal.valueOf(100));
        tFrais.setRatio(BigDecimal.valueOf(121));
        tFrais.setRef("");
        tFrais.setDateValue(ZonedDateTime.now());
        tFrais.setTva(VAT.intValue());
        tFrais.setDateUpd(LocalDateTime.now());


        testEntityManager.persist(tFrais);

        return tFrais;
    }

    protected TDebour createTDebour(LawfirmEntity lawfirm, TDossiers dossier) {
        TDebour tDebour = new TDebour();
        tDebour.setUnit(4);
        tDebour.setIdDoss(dossier.getIdDoss());
        tDebour.setDateAction(ZonedDateTime.now());
        tDebour.setPricePerUnit(BigDecimal.valueOf(40));
        TDebourType tDebourType = createTDebourType(lawfirm);
        tDebour.setIdMesureType(tDebourType.getIdMesureType());

        tDebour.setIdDebourType(tDebourType.getIdDebourType());
        tDebour.setDateUpd(LocalDateTime.now());
        tDebour.setUserUpd(USER);


        testEntityManager.persist(tDebour);

        return tDebour;
    }

    protected TDebourType createTDebourType(LawfirmEntity lawfirm) {
        TDebourType tDebourType = new TDebourType();
        TMesureType tMesureType = createTMesureType();
        tDebourType.setIdMesureType(tMesureType.getIdMesureType());
        tDebourType.setPricePerUnit(BigDecimal.valueOf(40));
        tDebourType.setVcKey(lawfirm.getVckey());
        tDebourType.setDescription("debour type desc");
        tDebourType.setUserUpd(USER);
        tDebourType.setArchived(false);
        tDebourType.setDateUpd(new Date());
        testEntityManager.persist(tDebourType);

        return tDebourType;
    }

    protected TMesureType createTMesureType() {
        TMesureType tMesureType = new TMesureType();
        tMesureType.setIdMesureType(1);
        tMesureType.setDescription("mesure type desc");
        testEntityManager.persist(tMesureType);

        return tMesureType;
    }

    protected RefCompte createRefCompte(LawfirmEntity lawfirmEntity, EnumAccountType accountTiers) {

        RefCompte refCompte = new RefCompte();
        refCompte.setVcKey(lawfirmEntity.getVckey());
        refCompte.setCompteNum("BE1234567");
        refCompte.setCompteRef("Compte honoraire");
        refCompte.setUserUpd(USER);
        refCompte.setCountable(true);
        refCompte.setArchived(false);
        refCompte.setAccountTypeId(accountTiers);
        refCompte.setDateUpd(new Date());


        testEntityManager.persist(refCompte);

        return refCompte;
    }

    protected RefPoste createRefPoste(LawfirmEntity lawfirmEntity) {

        RefPoste refPoste = new RefPoste();
        refPoste.setVcKey(lawfirmEntity.getVckey());
        refPoste.setRefPoste("yes");
        refPoste.setUserUpd(USER);
        refPoste.setFraisCollaboration(false);
        refPoste.setFraisProcedure(false);
        refPoste.setHonoraires(true);
        refPoste.setFacturable(false);
        refPoste.setArchived(false);
        refPoste.setDateUpd(new Date());


        testEntityManager.persist(refPoste);

        return refPoste;
    }

    protected TMatieres createMatieres() {

        TMatieres matieres = new TMatieres();
        matieres.setIdMatiere(100);
        matieres.setMatiereDesc("civil right");

        testEntityManager.persist(matieres);

        TMatiereRubriques matiereRubriques = new TMatiereRubriques();
        matiereRubriques.setIdMatiere(matieres.getIdMatiere());
        matiereRubriques.setMatiereRubriqueDesc("civil right private");
        matiereRubriques.setMatieres(matieres);
        testEntityManager.persist(matiereRubriques);

        TMatiereRubriques rubriques2 = new TMatiereRubriques();
        rubriques2.setIdMatiere(matieres.getIdMatiere());
        rubriques2.setMatiereRubriqueDesc("civil right public");
        rubriques2.setMatieres(matieres);

        testEntityManager.persist(rubriques2);

        matieres.setMatieresRubriquesList(new ArrayList<>());
        matieres.getMatieresRubriquesList().add(matiereRubriques);
        matieres.getMatieresRubriquesList().add(rubriques2);

        return matieres;
    }

    protected TTimesheetType createTTimesheetType(LawfirmEntity lawfirm) {

        TTimesheetType tTimesheetType = new TTimesheetType();
        tTimesheetType.setVcKey(lawfirm.getVckey());
        tTimesheetType.setDescription("timesheet type");
        tTimesheetType.setDateUpd(new Date());
        tTimesheetType.setUserUpd(USER);
        tTimesheetType.setArchived(false);

        testEntityManager.persist(tTimesheetType);

        return tTimesheetType;
    }

    protected TTimesheet createTTimesheet(LawfirmEntity lawfirm, TDossiers dossier) {
        log.info("dossier {}", dossier.getIdDoss());

        TTimesheetType tTimesheetType = createTTimesheetType(lawfirm);
        TTimesheet tTimesheet = new TTimesheet();
        tTimesheet.setIdDoss(dossier.getIdDoss());
        tTimesheet.setIdGest(lawfirm.getLawfirmUsers().get(0).getUser().getId());
        tTimesheet.setTsType(tTimesheetType.getIdTs());
        tTimesheet.setDateAction(ZonedDateTime.now());
        tTimesheet.setDateUpd(LocalDateTime.now());
        tTimesheet.setVat(VAT);
        tTimesheet.setCouthoraire(145);
        tTimesheet.setForfait(true);
        tTimesheet.setForfaitHt(new BigDecimal(100));

        testEntityManager.persist(tTimesheet);
        tTimesheet.setTTimesheetType(tTimesheetType);
        tTimesheet.setTDossiers(dossier);
        dossier.setTTimesheetList(new ArrayList<>());
        dossier.getTTimesheetList().add(tTimesheet);

        return tTimesheet;
    }


    protected TFactures createFacture(LawfirmEntity lawfirm) {
        TFactures tFactures = new TFactures();
        tFactures.setYearFacture(LocalDate.now().getYear());
        tFactures.setNumFacture(1);
        tFactures.setFactureRef("FV" + "-" + LocalDate.now().getYear() + "-" + "00" + tFactures.getNumFacture());
        tFactures.setVcKey(lawfirm.getVckey());
        tFactures.setDateValue(ZonedDateTime.now());
        tFactures.setMemo("memo test");
        tFactures.setMontant(new BigDecimal(500));
        tFactures.setIdFactureType(EnumFactureType.SELL);
        tFactures.setRef("ref test");
        tFactures.setDateEcheance(ZonedDateTime.now().plusDays(20));
        tFactures.setDateUpd(LocalDateTime.now());
        TClients client = createClient(lawfirm);
        tFactures.setIdTiers(client.getId_client());
        tFactures.setTClients(client);

        RefPoste refPoste = createRefPoste(lawfirm);
        refPoste.setRefPoste("Honoraires");
        refPoste.setFacturable(true);

        tFactures.setIdPoste(refPoste.getIdPoste());
        tFactures.setUserUpd(lawfirm.getUserUpd());

        TFactureEcheance tFactureEcheance = createFactureEcheance();
        tFactures.setIdEcheance(tFactureEcheance.getID());

        TDossiers tDossiers = createDossier(lawfirm, EnumVCOwner.OWNER_VC);
        testEntityManager.persist(tDossiers);
        tFactures.setIdDoss(tDossiers.getIdDoss());
        tFactures.setTDossiers(tDossiers);

        createTFactureDetails(tFactures);

        // prestation
        TTimesheet tTimesheet = createTTimesheet(lawfirm, tDossiers);
        TFactureTimesheet tFactureTimesheet = new TFactureTimesheet();
        tFactureTimesheet.setTsId(tTimesheet.getIdTs());
        tFactureTimesheet.setCreUser(USER);
        tFactures.addTFactureTimesheet(tFactureTimesheet);
        tFactureTimesheet.setTFactures(tFactures);

        // frais admin
        TDebour tDebour = createTDebour(lawfirm, tDossiers);
        FactureFraisAdmin factureFraisAdmin = new FactureFraisAdmin();
        factureFraisAdmin.setDeboursId(tDebour.getIdDebour());
        factureFraisAdmin.setCreUser(USER);
        tFactures.addFactureFraisAdmin(factureFraisAdmin);
        factureFraisAdmin.setTFactures(tFactures);

        RefCompte refCompte = createRefCompte(lawfirm, EnumAccountType.ACCOUNT_TIERS);
        // debours
        TFrais tFrais = createTFrais(lawfirm, tDossiers, refCompte);
        FactureFraisDebours factureFraisDebours = new FactureFraisDebours();
        factureFraisDebours.setFraisId(tFrais.getIdFrais());
        factureFraisDebours.setTFrais(tFrais);
        factureFraisDebours.setCreUser(USER);
        tFactures.addFactureFraisDebours(factureFraisDebours);
        factureFraisDebours.setTFactures(tFactures);

        // frais collaborat
        TFrais tFraisColl = createTFrais(lawfirm, tDossiers, refCompte);
        FactureFraisCollaboration factureFraisDeboursColla = new FactureFraisCollaboration();
        factureFraisDeboursColla.setFraisId(tFraisColl.getIdFrais());
        factureFraisDeboursColla.setTFrais(tFraisColl);
        factureFraisDeboursColla.setCreUser(USER);
        tFactures.addFactureFraisColl(factureFraisDeboursColla);
        factureFraisDeboursColla.setTFactures(tFactures);

        testEntityManager.persist(tFactures);

        return tFactures;
    }

    protected TFactureEcheance createFactureEcheance() {
        TFactureEcheance tFactureEcheance = new TFactureEcheance();
        tFactureEcheance.setID(1);
        tFactureEcheance.setDESCRIPTION("Comptant");
        testEntityManager.persist(tFactureEcheance);
        return tFactureEcheance;
    }


    protected TFactureDetails createTFactureDetails(TFactures tFactures) {
        BigDecimal vat = new BigDecimal("21");

        TFactureDetails factureDetails = new TFactureDetails();
        factureDetails.setDescription(" new details");
        factureDetails.setHtva(tFactures.getMontant().setScale(2, RoundingMode.HALF_UP).divide(vat, 2, RoundingMode.HALF_UP));
        factureDetails.setTtc(tFactures.getMontant());
        factureDetails.setTva(vat);
        factureDetails.setDateUpd(LocalDateTime.now());
        factureDetails.setUserUpd(USER);

        tFactures.addTFactureDetails(factureDetails);

        return factureDetails;

    }
}

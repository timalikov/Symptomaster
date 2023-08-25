package com.symptomaster.ws.service.impl;

import com.itextpdf.text.pdf.BaseFont;
import com.symptomaster.infra.database.TableNames;
import com.symptomaster.infra.dto.CouponDto;
import com.symptomaster.infra.dto.HistoryDto;
import com.symptomaster.infra.enumeration.EnumLocale;
import com.symptomaster.infra.utils.EmailExchanger;
import com.symptomaster.ws.models.Symptom;
import com.symptomaster.ws.models.SymptomsHistory;
import com.symptomaster.ws.models.Transaction;
import com.symptomaster.ws.service.SymptomService;
import com.symptomaster.ws.service.SymptomsHistoryService;
import com.symptomaster.ws.service.jdbc.mapper.SymptomsHistoryMapper;
import com.symptomaster.ws.service.jdbc.mapper.UserSymptomsHistoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikolay on 10/08/16.
 */
@Service("symptomsHistoryService")
public class SymptomsHistoryServiceImpl implements SymptomsHistoryService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SymptomService symptomService;

    @Override
    public List<SymptomsHistory> getSymptomsHistoryForGuest(EnumLocale locale, String transactionTag) {
        StringBuilder query = new StringBuilder().append(" SELECT _id, transaction_id, user_id, symptoms_sequence, result_id,timestamp,enabled FROM symptom_checker.users_symptoms_history").
                append(" WHERE transaction_id=? AND user_id=0");

        List<HistoryDto> dtoHistories = jdbcTemplate.query(query.toString(), new Object[]{transactionTag}, new SymptomsHistoryMapper());
        List<SymptomsHistory> symptomsHistories = new ArrayList<>();


        for(HistoryDto history : dtoHistories) {
            SymptomsHistory symptomsHistory = new SymptomsHistory();
            symptomsHistory.setId(history.getId());
            symptomsHistory.setTransactionId(history.getTransactionId());

            List<Symptom> symptoms = new ArrayList<>();

            for(int symptomId : history.getAnswersStack()) {
                symptoms.add(symptomService.getSymptomById(symptomId, locale));
            }

            symptomsHistory.setAnswersStack(symptoms);

            if(symptoms.size() > 0) {
                symptomsHistory.setResult(symptomService.getResultForSymptom(
                        (int) symptoms.get(symptoms.size() - 1).getId(),
                        locale));
            }

            symptomsHistory.setTimestamp(history.getTimestamp());
            symptomsHistory.setEnabled(history.getEnabled());

            symptomsHistories.add(symptomsHistory);
        }

        return symptomsHistories;
    }

    @Override
    public List<HistoryDto> getUserSymptomsHistory(int userId) {
        StringBuilder query = new StringBuilder().append(" SELECT ")
                .append("_id,")
                .append(" transaction_id,")
                .append(" user_id, ")
                .append("symptoms_sequence,")
                .append(" result_id,")
                .append("timestamp,")
                .append("enabled,")
                .append("result_id_encrypted,")
                .append("symptoms_sequence_encrypted,")
                .append("encryption_iv")
                .append(" FROM users_symptoms_history")
                .append(" WHERE user_id=? AND enabled = 1");

        try {
            List<HistoryDto> historyList = jdbcTemplate.query(query.toString(), new Object[] {userId}, new UserSymptomsHistoryMapper());
            return historyList;
        } catch (DataAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void updateEncryptedSymptomsHistoryInfo(HistoryDto h) {
        StringBuilder query = new StringBuilder("UPDATE users_symptoms_history SET ")
                .append("result_id_encrypted = ?,")
                .append("symptoms_sequence_encrypted=?,")
                .append("encryption_iv=? ")
                .append("WHERE _id=?");

        jdbcTemplate.update(query.toString(),
                h.getEncryptedResultId(),
                h.getEncryptedSerializedSequence(),
                h.getAesIV(),
                h.getId());
    }

    @Override
    public void disableSymptomsHistoryForUser(int userId) {
        StringBuilder query = new StringBuilder("UPDATE users_symptoms_history SET ")
                .append("enabled = 0 ")
                .append("WHERE _id=?");

        jdbcTemplate.update(query.toString(), userId);
    }

    @Override
    public void disableSymptomsHistoryForUser(String transactionId, int userId) {
        StringBuilder query = new StringBuilder("UPDATE users_symptoms_history SET ")
                .append("enabled = 0 ")
                .append("WHERE user_id=? AND transaction_id=?");

        jdbcTemplate.update(query.toString(), userId, transactionId);
    }

    @Override
    public void disableSymptomsHistoryForUser(int transactionEntryId, int userId) {
        StringBuilder query = new StringBuilder("UPDATE users_symptoms_history SET ")
                .append("enabled = 0 ")
                .append("WHERE user_id=? AND _id=?");

        jdbcTemplate.update(query.toString(), userId, transactionEntryId);
    }

    @Override
    public void saveTransaction(Transaction transaction, int userId) {
        StringBuilder query = new StringBuilder("INSERT INTO users_symptoms_history(transaction_id, ")
                .append("user_id,")
                .append("symptoms_sequence,")
                .append("result_id,")
                .append("result_id_encrypted,")
                .append("symptoms_sequence_encrypted,")
                .append("encryption_iv) VALUES(?,?,?,?,?,?,?)");

        jdbcTemplate.batchUpdate(query.toString(), new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, transaction.getTransactionId());
                ps.setInt(2, userId);
                ps.setString(3, transaction.getTransactionEntryList().get(i).getSymptomSequence());
                ps.setString(4, transaction.getTransactionEntryList().get(i).getResult());
                ps.setBytes(5, transaction.getTransactionEntryList().get(i).getResultIdEnc());
                ps.setBytes(6, transaction.getTransactionEntryList().get(i).getSymptomSequenceEnc());
                ps.setString(7, transaction.getTransactionEntryList().get(i).getIv());
            }

            @Override
            public int getBatchSize() {
                return transaction.getTransactionEntryList().size();
            }
        });
    }

    @Override
    public boolean isTransactionEntryBelongsUser(int symptomHistoryId, int userId) {
        StringBuilder stringBuilder = new StringBuilder("SELECT _id FROM users_symptoms_history WHERE _id = ? AND user_id = ?");

        try {
            int i = jdbcTemplate.queryForObject(stringBuilder.toString(), new Object[] {symptomHistoryId, userId}, Integer.class);
            return true;
        } catch (DataAccessException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean isTransactionBelongsUser(String transactionId, int userId) {
        StringBuilder stringBuilder = new StringBuilder("SELECT _id FROM users_symptoms_history WHERE transaction_id = ? AND user_id = ?");

        try {
            List<Integer> ids = jdbcTemplate.queryForList(stringBuilder.toString(), new Object[] {transactionId, userId}, Integer.class);
            return ids != null && ids.size() > 0;
        } catch (DataAccessException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void insertCoupon(CouponDto couponDto) {
        String sql = "INSERT INTO " + TableNames.COUPONS + "("
                + TableNames.COUPONS_COUPON_NUMBER + ","
                + TableNames.COUPONS_USER_ID + ","
                + TableNames.COUPONS_PARTNER_ID + ","
                + TableNames.COUPONS_TRANSACTION_ID + ","
                + TableNames.COUPONS_ACTIVATED_DATE + ") "
                + "VALUES(?,?,?,?,?)";

        jdbcTemplate.update(sql, couponDto.getCouponNumber(),
               couponDto.getUserId(),
                couponDto.getParnterId(),
                couponDto.getTransactionId(),
                couponDto.getActivatedDate());
    }

    @Override
    public boolean isCouponBelongsUser(String couponNumber, int userId) {
        StringBuilder stringBuilder = new StringBuilder("SELECT id FROM coupons WHERE coupon_number = ? AND user_id = ?");

        try {
            int i = jdbcTemplate.queryForObject(stringBuilder.toString(), new Object[] {couponNumber, userId}, Integer.class);
            return true;
        } catch (DataAccessException e) {
            e.printStackTrace();
        }

        return false;
    }

    private String getPathToCouponsFolder() {
        return "/opt/sm_filesystem/coupons/";
    }

    private String generateCouponPDF(String couponNumber) {
        couponNumber = couponNumber == null ? "" : couponNumber;
        String fileName = getPathToCouponsFolder() + couponNumber + ".pdf";


        if(!new File(fileName).exists()) {
            String html = generateHtmlCoupons(couponNumber);

            FileOutputStream fos = null;

            try {
                ITextRenderer renderer = new ITextRenderer();
                renderer.getFontResolver().addFont("/opt/sm_fonts/Arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                renderer.setDocumentFromString(html);
                renderer.layout();
                fos = new FileOutputStream(fileName);
                renderer.createPDF(fos);
                fos.close();
            }
            catch (Exception ex) {
                ex.printStackTrace();
                fileName = null;
            }
        }

        return fileName;
    }

    private String generateHtmlCoupons(String couponNumber) {
        String html = "";
        html += "<html>";
        html += "<head>";
        html += "<meta charset=\"utf-8\" />";
        html += "<style> ";
        html += "@page { size: A4 portrait;}";
        html += "</style>";
        html += "</head>";
        html += "<body>";
        html += generateHealthcityCoupon(couponNumber);
        html += "</body>";
        html += "</html>";
        return html;
    }

    private String generateHealthcityCoupon(String couponNumber) {
        String relativeUrl = "https://symptomaster.com/client/";
        String healthcityLogo = relativeUrl + "/resources/images/coupon/healthcity/healthcity-logo.jpg";
        String sharpIcon = relativeUrl + "/resources/images/coupon/healthcity/sharp-icon.jpg";
        String percentIcon = relativeUrl + "/resources/images/coupon/healthcity/percent-icon.jpg";
        String infoIcon = relativeUrl + "/resources/images/coupon/healthcity/info-icon.jpg";
        String printIcon = relativeUrl + "/resources/images/coupon/healthcity/print-icon.jpg";
        String squareIcon = relativeUrl + "/resources/images/coupon/healthcity/square-icon.jpg";

        String html = "";
        html += "<div style=\"font-family: Arial; margin-left: -10px; width: 1000px;\">";

        html += "<div style=\"background-color: #ececec; width: 100%; height: 380px;\">";
        html += "<img src=\"" + healthcityLogo + "\" style=\"width: 290px; height:65px; position: absolute; margin-left: 20px; margin-top: 20px;\" />";
        html += "<p style=\"position: absolute; right: 15px; padding: 7px 38px; margin-top: 27px; background-color: #854dc0; color: #ffffff; font-size: 20px;\">";

        html +=  "Номер купона" + ": " + couponNumber;

        html += "</p>";
        html += "<table style=\"border-spacing: 14px; font-size: 12px; width: 340px; position: absolute; margin-top: 90px; margin-left: 10px;\">";
        html += "<tr>";
        html += "<td>";
        html += "<img src=\"" + sharpIcon + "\" style=\"float: left; padding-right: 5pt;\" />";
        html += "</td>";
        html += "<td style=\"text-align: justify;\">";

        html += "<div style=\"margin-left: -15px;\">" + "Это Ваш уникальный 9-значный номер." + "</div>";

        html += "</td>";
        html += "</tr>";
        html += "<tr>";
        html += "<td>";
        html += "<img src=\"" + percentIcon + "\" style=\"float: left; padding-right: 5pt;\" />";
        html += "</td>";
        html += "<td style=\"text-align: justify;\">";

        html += "<div style=\"margin-left: -15px;\">" + "Предъявив данный купон в сети клиник HealthCity Вы получаете 10% скидку на весь перечень услуг в день Вашего обращения." + "</div>";

        html += "</td>";
        html += "</tr>";
        html += "<tr>";
        html += "<td>";
        html += "<img src=\"" + infoIcon + "\" style=\"float: left; padding-right: 5pt;\" />";
        html += "</td>";
        html += "<td style=\"text-align: justify;\">";

        html += "<div style=\"margin-left: -15px;\">" + "Более подробно о диагностических и лечебных возможностях HealthCity можно узнать на сайте: " + " www.healthcity.kz</div>";

        html += "</td>";
        html += "</tr>";
        html += "<tr>";
        html += "<td>";
        html += "<img src=\"" + printIcon + "\" style=\"float: left; padding-right: 5pt;\" />";
        html += "</td>";
        html += "<td style=\"text-align: justify;\">";

        html += "<div style=\"margin-left: -15px;\">" + "Вы можете распечатать, сохранить или сфотографировать данный купон и показать его регистратору HealthCity." + "</div>";

        html += "</td>";
        html += "</tr>";
        html += "</table>";

        html += "<div style=\"background-color: #ffffff; padding-right: 5px; font-size: 0.69em; position: absolute; right: 10px; margin-top: 80px; width: 320px;\">";
        html += "<ul style=\"list-style-image: url(" + squareIcon + "); margin-left: -15px;\">";

        html += "<li style=\"text-align: justify; margin-bottom: 5px;\">" + "Высокоточная диагностика: МРТ, КТ, УЗИ, Рентген, Маммограф" + "</li>";

        html += "<li style=\"text-align: justify; margin-bottom: 5px;\">" + "Самая надёжная лабораторная диагностика" + "</li>";

        html += "<li style=\"text-align: justify; margin-bottom: 5px;\">" + "Казахстанские и иностранные специалисты в области женского и детского здоровья, кардиологи, эндокринологи, невропатологи, гастроэнтерологи, аллергологи, урологи, ЛОР, и многие другие." + "</li>";

        html += "<li style=\"text-align: justify; margin-bottom: 5px;\">" + "Комплексное диагностическое обследование Check-up для взрослых и детеи" + "</li>";

        html += "</ul>";
        html += "</div>";

        html += "<div style=\"position: absolute; margin-top: 270px; margin-left: 370px; font-size: 11px;\">";
        html += "<p style=\"padding: 2pt; background-color: #854dc0; color: #ffffff;\">";

        html += "ДИАГНОСТИЧЕСКАЯ КЛИНИКА HEALTHCITY";

        html += "</p>";

        html += "<div>" + "ул. Мусабаева 8" + "</div>";

        html += "<div>" + "ул. Аскарова, выше пр. аль-Фараби" + "</div>";

        html += "<div>call center: +7 727 331 3 331</div>";
        html += "</div>";

        html += "</div>";

        html += "</div>";
        return html;
    }

    @Override
    public void sendCouponToEmail(String couponNumber, String email) {
        String fileName = generateCouponPDF(couponNumber);
        List<File> files = new ArrayList<>();
        files.add(new File(getPathToCouponsFolder() + couponNumber + ".pdf"));
        EmailExchanger.sendEmail(email, "Купон " + couponNumber, "Ваш купон прикреплен в этом письме", files);
    }

    @Override
    public String getCouponByTransactionId(String transactionId) {
        StringBuilder query = new StringBuilder().append(" SELECT ")
                .append("coupon_number FROM coupons ")
                .append("WHERE transaction_id = ?");

        try {
            String couponNumber = jdbcTemplate.queryForObject(query.toString(), new Object[] {transactionId}, String.class);
            return couponNumber;
        } catch (DataAccessException e) {
            e.printStackTrace();
        }

        return null;
    }
}

package com.example.workmate.service.account;

import com.example.workmate.dto.account.AccountDto;
import com.example.workmate.entity.account.Account;
import com.example.workmate.entity.account.Authority;
import com.example.workmate.entity.account.MailAuth;
import com.example.workmate.facade.AuthenticationFacade;
import com.example.workmate.repo.AccountRepo;
import com.example.workmate.repo.MailAuthRepo;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Properties;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {
    private static final Properties PROPERTIES = new Properties();

    @Value("${spring.mail.username}")
    private String USERNAME;   //change it
    @Value("${spring.mail.password}")
    private String PASSWORD;   //change it

    private final String HOST = "smtp.gmail.com";
    private final AccountRepo accountRepo;
    private final MailAuthRepo mailAuthRepo;
    private final AuthenticationFacade authFacade;

    // 문자열 자리
    private final int codeLen = 6;
    // 문자열에 문자를 포함할것인지
    private final boolean usedLetters = true;
    // 문자열에 숫자를 포함할것인지
    private final boolean usedNumbers = true;

    static {
        PROPERTIES.put("mail.smtp.host", "smtp.gmail.com");
        PROPERTIES.put("mail.smtp.port", "587");
        PROPERTIES.put("mail.smtp.auth", "true");
        PROPERTIES.put("mail.smtp.starttls.enable", "true");
    }

    public void send(String username, String authMail){
        Authenticator authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        };
        log.info("auth: {}",authenticator.getClass());
        // 랜덤 문자열 생성
        String securityCode = RandomStringUtils.random(codeLen, usedLetters, usedNumbers);
        // String securityCode = "안녕하세요";
        Session session = Session.getInstance(PROPERTIES, authenticator);
        session.setDebug(true);

        try {
            // create a message with headers
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(USERNAME));
            InternetAddress[] address = {new InternetAddress(authMail)};
            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject("Work Time auth Mail", "UTF-8");
            msg.setSentDate(new Date());

            // create message body
            Multipart mp = new MimeMultipart();
            MimeBodyPart mbp = new MimeBodyPart();
            mbp.setText(securityCode, "UTF-8");
            mp.addBodyPart(mbp);
            msg.setContent(mp);

            // send the message
            Transport.send(msg);
            Account account = accountRepo.findByUsername(username).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
            );

            MailAuth mailAuth = MailAuth.builder()
                    .authString(securityCode)
                    .account(account)
                    .sendTime(LocalDateTime.now())
                    .build();
            mailAuthRepo.save(mailAuth);
        } catch (MessagingException mex) {
            mex.printStackTrace();
            Exception ex = null;
            if ((ex = mex.getNextException()) != null) {
                ex.printStackTrace();
            }
        }
    }

    public boolean checkCode(String username, String code){
        Account account = accountRepo.findByUsername(username).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
        MailAuth mailAuth = mailAuthRepo.findTopByAccount_IdOrderBySendTimeDesc(account.getId());

        if (code.equals(mailAuth.getAuthString())){
            account.setMailAuth(true);

            if (account.getBusinessNumber() != null) {
                account.setAuthority(Authority.ROLE_BUSINESS_USER);
                log.info("Set Authority: {} ", account.getAuthority());
            } else {
                account.setAuthority(Authority.ROLE_USER);
                log.info("Set Authority: {} ", account.getAuthority());
            }
            accountRepo.save(account);
            return true;
        }
        else
            return false;
    }

    public boolean checkInfo(String username, String email) {
        Account account = accountRepo.findByUsername(authFacade.getAuth().getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        log.info("username: {}", account.getUsername());
        log.info("email: {}", account.getEmail());

        return username.equals(account.getUsername()) &&
                email.equals(account.getEmail());
    }

    public boolean checkMailAuth(String username) {
        Account account = accountRepo.findByUsername(username).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
        MailAuth mailAuth = mailAuthRepo.findTopByAccount_IdOrderBySendTimeDesc(account.getId());
        return mailAuth != null;
    }

    // 5분 후에 코드를 입력한 경우
    public boolean checkTimeLimit5L(String username) {
        Account account = accountRepo.findByUsername(username).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        MailAuth mailAuth = mailAuthRepo.findTopByAccount_IdOrderBySendTimeDesc(account.getId());
        
        LocalDateTime now = LocalDateTime.now();
        Duration diff = Duration.between(mailAuth.getSendTime(), now);
        Long diffMin = diff.toMinutes();
        log.info("id: {}", mailAuth.getId());
        log.info("diffMin: {}",diffMin);

        // 5분 후에 코드를 입력하면 실패이고 send()를 다시 함
        if(diffMin >= 5L){
            send(username, account.getEmail());
            log.info("re mail");
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            return false;
        }
        return true;
    }
}
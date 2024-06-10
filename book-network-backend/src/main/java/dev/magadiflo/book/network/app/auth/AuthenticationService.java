package dev.magadiflo.book.network.app.auth;

import dev.magadiflo.book.network.app.email.EmailService;
import dev.magadiflo.book.network.app.email.EmailTemplateName;
import dev.magadiflo.book.network.app.role.Role;
import dev.magadiflo.book.network.app.role.RoleRepository;
import dev.magadiflo.book.network.app.user.Token;
import dev.magadiflo.book.network.app.user.TokenRepository;
import dev.magadiflo.book.network.app.user.User;
import dev.magadiflo.book.network.app.user.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AuthenticationService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    public void register(RegistrationRequest request) throws MessagingException {
        Role defaultRoleDB = this.roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalArgumentException("El rol USER no fue encontrado"));
        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(this.passwordEncoder.encode(request.password()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(defaultRoleDB))
                .build();
        this.userRepository.save(user);
        this.sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        String activationCode = this.generateAndSaveActivationCode(user);
        this.emailService.sendEmail(user.getEmail(), user.fullName(), EmailTemplateName.ACTIVATE_ACCOUNT,
                this.activationUrl, activationCode, "Activación de cuenta");
    }

    private String generateAndSaveActivationCode(User user) {
        String activationCode = this.generateActivationCode(6);
        Token token = Token.builder()
                .token(activationCode)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .user(user)
                .build();
        this.tokenRepository.save(token);
        return activationCode;
    }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length()); // Obtenemos índices del [0..9]
            codeBuilder.append(characters.charAt(randomIndex)); // Obtenemos el carácter según el índice
        }
        return codeBuilder.toString();
    }
}
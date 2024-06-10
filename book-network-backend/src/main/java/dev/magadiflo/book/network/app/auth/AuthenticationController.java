package dev.magadiflo.book.network.app.auth;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "API de autenticación de usuario")
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping(path = "/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegistrationRequest request) throws MessagingException {
        this.authenticationService.register(request);
        return ResponseEntity.accepted().build();
    }

}

package org.codevoke.probnb.service;

import lombok.AllArgsConstructor;
import org.codevoke.probnb.dto.LoginDTO;
import org.codevoke.probnb.exceptions.UserException;
import org.codevoke.probnb.model.Auth;
import org.codevoke.probnb.repository.AuthRepository;
import org.codevoke.probnb.utils.JwtService;
import org.codevoke.probnb.utils.PasswordHasher;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class LoginService {
    private AuthRepository authRepository;
    private JwtService jwtService;

    public Map<String, String> login(LoginDTO loginDTO) {
        Optional<Auth> entity = authRepository.findByEmail(loginDTO.getEmail());
        Auth auth;
        if (entity.isEmpty())
            throw UserException.NotFoundByEmail();
        else
            auth = entity.get();
        if (!PasswordHasher.verify(loginDTO.getPassword(), auth.getPassword()))
            throw UserException.PasswordIncorrect();

        String accessToken = jwtService.generateAccessToken(auth, auth.getUser());
        String refreshToken = jwtService.generateRefreshToken(auth);

        return Map.of(
                "access_token", accessToken,
                "refresh_token", refreshToken
        );
    }
}

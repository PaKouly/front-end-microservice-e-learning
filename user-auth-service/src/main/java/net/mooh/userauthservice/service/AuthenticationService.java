package net.mooh.userauthservice.service;

import net.mooh.userauthservice.dtos.AuthenticationRequestDto;
import net.mooh.userauthservice.dtos.AuthenticationResponseDto;

public interface AuthenticationService {
    AuthenticationResponseDto authenticate(AuthenticationRequestDto request);
}

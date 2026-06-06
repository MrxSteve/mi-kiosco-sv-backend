package com.devplus.mikiosco_sv.application.usecase.usuario;

import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.UsuarioRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.response.UserResponse;
import com.devplus.mikiosco_sv.presentation.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetMyProfileUseCase {

    private final UsuarioRepository usuarioRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public UserResponse execute(AuthenticatedUser caller) {
        var usuario = usuarioRepository.findById(caller.userId())
                .orElseThrow(() -> NotFoundException.of("Usuario", caller.userId()));
        return userMapper.toResponse(usuario);
    }
}

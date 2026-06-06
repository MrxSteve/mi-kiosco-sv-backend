package com.devplus.mikiosco_sv.application.usecase.usuario;

import com.devplus.mikiosco_sv.infrastructure.persistence.entity.UsuarioEntity;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.UsuarioRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.response.UserResponse;
import com.devplus.mikiosco_sv.presentation.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListUsersUseCase {

    private final UsuarioRepository usuarioRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public List<UserResponse> execute(AuthenticatedUser caller) {
        List<UsuarioEntity> usuarios = caller.isSuperAdmin()
                ? usuarioRepository.findAll()
                : usuarioRepository.findAllByComedor_Id(caller.comedorId());

        return userMapper.toResponseList(usuarios);
    }
}

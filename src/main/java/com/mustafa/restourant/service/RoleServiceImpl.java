package com.mustafa.restourant.service;


import com.mustafa.restourant.entity.Role;
import com.mustafa.restourant.exception.NotFoundException;
import com.mustafa.restourant.repository.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService{
    public final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role findByRole(String roleName) {
       return roleRepository.findByRole(roleName).orElseThrow(() -> new NotFoundException("Rol bulunamadı"));
    
    }

    @Override
    public void save(Role role) {
        roleRepository.save(role);
    }

}

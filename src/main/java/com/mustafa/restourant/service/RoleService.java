package com.mustafa.restourant.service;

import com.mustafa.restourant.entity.Role;

public interface RoleService {

   Role findByRole(String roleName);
   void save(Role role);
}

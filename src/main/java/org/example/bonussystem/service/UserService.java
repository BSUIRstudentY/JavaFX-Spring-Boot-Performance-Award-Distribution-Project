package org.example.bonussystem.service;

import org.example.bonussystem.model.Role;
import org.example.bonussystem.model.User;
import org.example.bonussystem.repository.RoleRepository;
import org.example.bonussystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public Role findOrCreateRoleByName(String name, double bonusRate) {
        Optional<Role> role = roleRepository.findByName(name);
        if (role.isPresent()) {
            return role.get();
        } else {
            Role newRole = new Role();
            newRole.setName(name);
            newRole.setBaseBonusRate(bonusRate); // Устанавливаем bonusRate
            return roleRepository.save(newRole);
        }
    }
}
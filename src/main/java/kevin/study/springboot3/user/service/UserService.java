package kevin.study.springboot3.user.service;

import kevin.study.springboot3.user.domain.User;
import kevin.study.springboot3.user.dto.AddUserRequest;
import kevin.study.springboot3.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public Long save(AddUserRequest request) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return userRepository.save(User.builder()
                                       .email(request.getEmail())
                                       .password(bCryptPasswordEncoder.encode(request.getPassword()))
                                       .build())
                             .getId();
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                             .orElseThrow(() -> new IllegalArgumentException("not found user"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                             .orElseThrow(() -> new IllegalArgumentException("not found user"));
    }


}

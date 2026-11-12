package ru.kryuch.krtg.searcher.service.employee;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.kryuch.krtg.searcher.entity.employee.ProfileEntity;
import ru.kryuch.krtg.searcher.repository.employee.ProfileRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

    private final ProfileRepository profileRepository;

    public List<ProfileEntity> findAll() {
        return profileRepository.findAllByOrderByIdAscWithDetails();
    }

    public ProfileEntity findById(Long id) {
        return profileRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "Profile not found: " + id
            ));
    }

    @Transactional
    public ProfileEntity save(ProfileEntity entity) {
        return profileRepository.save(entity);
    }

    @Transactional
    public void deleteById(Long id) {
        profileRepository.deleteById(id);
    }
}

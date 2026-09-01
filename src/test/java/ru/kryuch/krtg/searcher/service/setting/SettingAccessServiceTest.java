package ru.kryuch.krtg.searcher.service.setting;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.kryuch.krtg.searcher.config.SettingConfig;
import ru.kryuch.krtg.searcher.dto.CurrentUser;
import ru.kryuch.krtg.searcher.dto.SettingDto;
import ru.kryuch.krtg.searcher.entity.SettingEntity;
import ru.kryuch.krtg.searcher.entity.setting.SettingValueEntity;
import ru.kryuch.krtg.searcher.exception.BusinessException;
import ru.kryuch.krtg.searcher.mapper.SettingMapper;
import ru.kryuch.krtg.searcher.repository.SettingRepository;
import ru.kryuch.krtg.searcher.repository.setting.SettingValueRepository;
import ru.kryuch.krtg.searcher.type.SettingType;
import ru.kryuch.krtg.searcher.util.UserUtil;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SettingAccessService Tests")
class SettingAccessServiceTest {

    @Mock
    private SettingValueRepository settingValueRepository;

    @Mock
    private SettingRepository settingRepository;

    @Mock
    private SettingMapper settingMapper;

    @Mock
    private SettingValueConverter settingValueConverter;

    @InjectMocks
    private SettingAccessService settingAccessService;

    private static final Integer TEST_USER_ID = 1;
    private static final Long TEST_SETTING_VALUE_ID = 100L;
    private static final Long TEST_SETTING_ID = 10L;
    private static final String TEST_SETTING_CODE = "test_setting";

    // ========== Helper Methods ==========

    private CurrentUser createCurrentUser(Integer id) {
        return CurrentUser.builder()
                .id(id)
                .username("test@test.com")
                .password("password")
                .enabled(true)
                .build();
    }

    private SettingEntity createSetting(Long id, String code) {
        SettingEntity entity = new SettingEntity();
        entity.setId(id);
        entity.setCode(code);
        entity.setType(SettingType.STRING);
        return entity;
    }

    private SettingValueEntity createSettingValue(Long id, Integer userId) {
        SettingValueEntity entity = new SettingValueEntity();
        entity.setId(id);
        entity.setUserId(userId);
        entity.setSetting(createSetting(TEST_SETTING_ID, TEST_SETTING_CODE));
        return entity;
    }

    private SettingDto createDto(String code, String value) {
        SettingDto dto = new SettingDto();
        dto.setCode(code);
        dto.setStringValue(value);
        return dto;
    }

    private List<SettingDto> createDtos(int count) {
        List<SettingDto> dtos = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            dtos.add(createDto("code_" + i, "value_" + i));
        }
        return dtos;
    }

    // ========== Tests ==========

    @Test
    @DisplayName("getAll - should return all settings with defaults")
    void getAll_shouldReturnAllSettingsWithDefaults() {
        try (MockedStatic<UserUtil> userUtilMock = mockStatic(UserUtil.class)) {
            userUtilMock.when(UserUtil::getCurrentUser).thenReturn(createCurrentUser(TEST_USER_ID));

            when(settingValueRepository.findAllByUserId(TEST_USER_ID)).thenReturn(new ArrayList<>());
            when(settingRepository.findAll()).thenReturn(Arrays.asList(
                    createSetting(1L, "code1"),
                    createSetting(2L, "code2")
            ));
            doReturn(createDtos(2)).when(settingMapper).fromEntityList((List<SettingValueEntity>) any());

            List<SettingDto> result = settingAccessService.getAll();

            assertThat(result).hasSize(2);
            verify(settingValueRepository).findAllByUserId(TEST_USER_ID);
            verify(settingRepository).findAll();
        }
    }

    @Test
    @DisplayName("getAll - should handle empty settings")
    void getAll_shouldHandleEmptySettings() {
        try (MockedStatic<UserUtil> userUtilMock = mockStatic(UserUtil.class)) {
            userUtilMock.when(UserUtil::getCurrentUser).thenReturn(createCurrentUser(TEST_USER_ID));

            when(settingValueRepository.findAllByUserId(TEST_USER_ID)).thenReturn(new ArrayList<>());
            when(settingRepository.findAll()).thenReturn(new ArrayList<>());
            doReturn(new ArrayList<>()).when(settingMapper).fromEntityList((List<SettingValueEntity>) any());

            List<SettingDto> result = settingAccessService.getAll();

            assertThat(result).isEmpty();
        }
    }

    @Test
    @DisplayName("add - should add new setting")
    void add_shouldAddNewSetting() {
        try (MockedStatic<UserUtil> userUtilMock = mockStatic(UserUtil.class)) {
            userUtilMock.when(UserUtil::getCurrentUser).thenReturn(createCurrentUser(TEST_USER_ID));

            SettingDto dto = createDto(TEST_SETTING_CODE, "value");
            SettingValueEntity entity = createSettingValue(TEST_SETTING_VALUE_ID, TEST_USER_ID);

            when(settingMapper.toEntity(dto)).thenReturn(entity);
            when(settingRepository.findByCode(TEST_SETTING_CODE)).thenReturn(Optional.of(createSetting(TEST_SETTING_ID, TEST_SETTING_CODE)));
            when(settingValueRepository.save(any())).thenReturn(entity);

            settingAccessService.add(dto);

            verify(settingMapper).toEntity(dto);
            verify(settingRepository).findByCode(TEST_SETTING_CODE);
            verify(settingValueRepository).save(any());
        }
    }

    @Test
    @DisplayName("add - should throw when code not found")
    void add_shouldThrowWhenCodeNotFound() {
        try (MockedStatic<UserUtil> userUtilMock = mockStatic(UserUtil.class)) {
            userUtilMock.when(UserUtil::getCurrentUser).thenReturn(createCurrentUser(TEST_USER_ID));

            SettingDto dto = createDto("unknown", "value");
            when(settingMapper.toEntity(any())).thenReturn(new SettingValueEntity());
            when(settingRepository.findByCode("unknown")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> settingAccessService.add(dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Неизвестный код настройки: unknown");
        }
    }

    @Test
    @DisplayName("update - should update existing setting")
    void update_shouldUpdateExistingSetting() {
        try (MockedStatic<UserUtil> userUtilMock = mockStatic(UserUtil.class)) {
            userUtilMock.when(UserUtil::getCurrentUser).thenReturn(createCurrentUser(TEST_USER_ID));

            SettingDto dto = createDto(TEST_SETTING_CODE, "new value");
            SettingValueEntity existing = createSettingValue(TEST_SETTING_VALUE_ID, TEST_USER_ID);

            when(settingValueRepository.findByIdAndUserId(TEST_SETTING_VALUE_ID, TEST_USER_ID))
                    .thenReturn(Optional.of(existing));
            when(settingMapper.mergeToEntity(dto, existing)).thenReturn(existing); // Возвращаем тот же объект
            when(settingValueRepository.save(existing)).thenReturn(existing); // Сохраняем тот же объект

            settingAccessService.update(dto, TEST_SETTING_VALUE_ID);

            verify(settingValueRepository).findByIdAndUserId(TEST_SETTING_VALUE_ID, TEST_USER_ID);
            verify(settingMapper).mergeToEntity(dto, existing);
            verify(settingValueRepository).save(existing);
        }
    }

    @Test
    @DisplayName("update - should throw when not found")
    void update_shouldThrowWhenNotFound() {
        try (MockedStatic<UserUtil> userUtilMock = mockStatic(UserUtil.class)) {
            userUtilMock.when(UserUtil::getCurrentUser).thenReturn(createCurrentUser(TEST_USER_ID));

            when(settingValueRepository.findByIdAndUserId(TEST_SETTING_VALUE_ID, TEST_USER_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> settingAccessService.update(new SettingDto(), TEST_SETTING_VALUE_ID))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Не существует сущности <<настройки>> с id=" + TEST_SETTING_VALUE_ID);
        }
    }

    @Test
    @DisplayName("save - should create new when not exists")
    void save_shouldCreateNewWhenNotExists() {
        try (MockedStatic<UserUtil> userUtilMock = mockStatic(UserUtil.class)) {
            userUtilMock.when(UserUtil::getCurrentUser).thenReturn(createCurrentUser(TEST_USER_ID));

            SettingDto dto = createDto(TEST_SETTING_CODE, "value");
            SettingEntity definition = createSetting(TEST_SETTING_ID, TEST_SETTING_CODE);

            when(settingRepository.findByCode(TEST_SETTING_CODE)).thenReturn(Optional.of(definition));
            when(settingValueRepository.findBySettingCodeAndUserId(TEST_SETTING_CODE, TEST_USER_ID))
                    .thenReturn(Optional.empty());
            when(settingValueRepository.save(any())).thenReturn(new SettingValueEntity());

            boolean result = settingAccessService.save(dto);

            assertThat(result).isTrue();
            verify(settingValueRepository).save(any());
        }
    }

    @Test
    @DisplayName("save - should update when value changed")
    void save_shouldUpdateWhenValueChanged() {
        try (MockedStatic<UserUtil> userUtilMock = mockStatic(UserUtil.class)) {
            userUtilMock.when(UserUtil::getCurrentUser).thenReturn(createCurrentUser(TEST_USER_ID));

            SettingDto dto = createDto(TEST_SETTING_CODE, "new value");
            SettingEntity definition = createSetting(TEST_SETTING_ID, TEST_SETTING_CODE);
            SettingValueEntity existing = createSettingValue(TEST_SETTING_VALUE_ID, TEST_USER_ID);
            existing.setStringValue("old value");

            when(settingRepository.findByCode(TEST_SETTING_CODE)).thenReturn(Optional.of(definition));
            when(settingValueRepository.findBySettingCodeAndUserId(TEST_SETTING_CODE, TEST_USER_ID))
                    .thenReturn(Optional.of(existing));
            when(settingValueRepository.save(any())).thenReturn(existing);

            boolean result = settingAccessService.save(dto);

            assertThat(result).isTrue();
            verify(settingValueRepository).save(existing);
        }
    }

    @Test
    @DisplayName("save - should not update when value not changed")
    void save_shouldNotUpdateWhenValueNotChanged() {
        try (MockedStatic<UserUtil> userUtilMock = mockStatic(UserUtil.class)) {
            userUtilMock.when(UserUtil::getCurrentUser).thenReturn(createCurrentUser(TEST_USER_ID));

            SettingDto dto = createDto(TEST_SETTING_CODE, "same");
            SettingEntity definition = createSetting(TEST_SETTING_ID, TEST_SETTING_CODE);
            SettingValueEntity existing = createSettingValue(TEST_SETTING_VALUE_ID, TEST_USER_ID);
            existing.setStringValue("same");

            when(settingRepository.findByCode(TEST_SETTING_CODE)).thenReturn(Optional.of(definition));
            when(settingValueRepository.findBySettingCodeAndUserId(TEST_SETTING_CODE, TEST_USER_ID))
                    .thenReturn(Optional.of(existing));

            boolean result = settingAccessService.save(dto);

            assertThat(result).isFalse();
            verify(settingValueRepository, never()).save(any());
        }
    }

    @Test
    @DisplayName("getByCode - should return setting when exists")
    void getByCode_shouldReturnSettingWhenExists() {
        try (MockedStatic<UserUtil> userUtilMock = mockStatic(UserUtil.class)) {
            userUtilMock.when(UserUtil::getCurrentUser).thenReturn(createCurrentUser(TEST_USER_ID));

            SettingValueEntity entity = createSettingValue(TEST_SETTING_VALUE_ID, TEST_USER_ID);
            SettingDto dto = createDto(TEST_SETTING_CODE, "value");

            when(settingValueRepository.findBySettingCodeAndUserId(TEST_SETTING_CODE, TEST_USER_ID))
                    .thenReturn(Optional.of(entity));
            when(settingMapper.fromEntity(entity)).thenReturn(dto);

            SettingDto result = settingAccessService.getByCode(TEST_SETTING_CODE);

            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(TEST_SETTING_CODE);
        }
    }

    @Test
    @DisplayName("getByCode - should return null when not found")
    void getByCode_shouldReturnNullWhenNotFound() {
        try (MockedStatic<UserUtil> userUtilMock = mockStatic(UserUtil.class)) {
            userUtilMock.when(UserUtil::getCurrentUser).thenReturn(createCurrentUser(TEST_USER_ID));

            when(settingValueRepository.findBySettingCodeAndUserId(TEST_SETTING_CODE, TEST_USER_ID))
                    .thenReturn(Optional.empty());

            SettingDto result = settingAccessService.getByCode(TEST_SETTING_CODE);

            assertThat(result).isNull();
        }
    }

    @Test
    @DisplayName("setValueByCode - should set value for current user")
    void setValueByCode_shouldSetValueForCurrentUser() {
        try (MockedStatic<UserUtil> userUtilMock = mockStatic(UserUtil.class)) {
            userUtilMock.when(UserUtil::getCurrentUser).thenReturn(createCurrentUser(TEST_USER_ID));

            String code = "test";
            String value = "test_value";
            SettingEntity definition = createSetting(TEST_SETTING_ID, code);
            SettingValueEntity entity = createSettingValue(TEST_SETTING_VALUE_ID, TEST_USER_ID);

            when(settingRepository.findByCode(code)).thenReturn(Optional.of(definition));
            when(settingValueRepository.findBySettingCodeAndUserId(code, TEST_USER_ID))
                    .thenReturn(Optional.of(entity));
            doNothing().when(settingValueConverter).apply(entity, definition.getType(), value);
            when(settingValueRepository.save(entity)).thenReturn(entity);

            settingAccessService.setValueByCode(code, value);

            verify(settingValueConverter).apply(entity, definition.getType(), value);
            verify(settingValueRepository).save(entity);
        }
    }

    @Test
    @DisplayName("setValueByCode - should create new when not exists")
    void setValueByCode_shouldCreateNewWhenNotExists() {
        try (MockedStatic<UserUtil> userUtilMock = mockStatic(UserUtil.class)) {
            userUtilMock.when(UserUtil::getCurrentUser).thenReturn(createCurrentUser(TEST_USER_ID));

            String code = "test";
            String value = "test_value";
            SettingEntity definition = createSetting(TEST_SETTING_ID, code);

            when(settingRepository.findByCode(code)).thenReturn(Optional.of(definition));
            when(settingValueRepository.findBySettingCodeAndUserId(code, TEST_USER_ID))
                    .thenReturn(Optional.empty());
            doNothing().when(settingValueConverter).apply(any(), any(), anyString());
            when(settingValueRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            settingAccessService.setValueByCode(code, value);

            verify(settingValueConverter).apply(any(SettingValueEntity.class), eq(definition.getType()), eq(value));
            verify(settingValueRepository).save(any());
        }
    }

    @Test
    @DisplayName("setValueByCode - should set value for specific user")
    void setValueByCode_shouldSetValueForSpecificUser() {
        String code = "test";
        String value = "test_value";
        Integer userId = 2;
        SettingEntity definition = createSetting(TEST_SETTING_ID, code);
        SettingValueEntity entity = createSettingValue(TEST_SETTING_VALUE_ID, userId);

        when(settingRepository.findByCode(code)).thenReturn(Optional.of(definition));
        when(settingValueRepository.findBySettingCodeAndUserId(code, userId))
                .thenReturn(Optional.of(entity));
        doNothing().when(settingValueConverter).apply(entity, definition.getType(), value);
        when(settingValueRepository.save(entity)).thenReturn(entity);

        settingAccessService.setValueByCode(code, value, userId);

        verify(settingValueRepository).findBySettingCodeAndUserId(code, userId);
        verify(settingValueConverter).apply(entity, definition.getType(), value);
        verify(settingValueRepository).save(entity);
    }

    @Test
    @DisplayName("setValueByCode - should throw when code not found")
    void setValueByCode_shouldThrowWhenCodeNotFound() {
        // Arrange
        String code = "unknown";
        String value = "test_value";
        Integer userId = 2;

        when(settingRepository.findByCode(code)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> settingAccessService.setValueByCode(code, value, userId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Неизвестный код настройки: " + code);
    }

    @Test
    @DisplayName("findAllCronEnabled - should return cron settings")
    void findAllCronEnabled_shouldReturnCronSettings() {
        // Arrange
        SettingValueEntity entity1 = createSettingValue(1L, 1);
        SettingValueEntity entity2 = createSettingValue(2L, 2);

        // Устанавливаем тип STRING у настроек
        entity1.getSetting().setType(SettingType.STRING);
        entity2.getSetting().setType(SettingType.STRING);

        List<SettingValueEntity> entities = Arrays.asList(entity1, entity2);

        // Создаем DTO с установленными значениями
        SettingDto dto1 = new SettingDto();
        dto1.setCode("cron1");
        dto1.setStringValue("true");
        // Устанавливаем тип в DTO тоже
        dto1.setType(SettingType.STRING);

        SettingDto dto2 = new SettingDto();
        dto2.setCode("cron2");
        dto2.setStringValue("false");
        dto2.setType(SettingType.STRING);

        when(settingValueRepository.findBySettingCode(SettingConfig.CRON_ENABLE_SETTING_CODE))
                .thenReturn(entities);
        when(settingMapper.fromEntity(entity1)).thenReturn(dto1);
        when(settingMapper.fromEntity(entity2)).thenReturn(dto2);

        // Act
        Map<Integer, String> result = settingAccessService.findAllCronEnabled();

        // Assert
        assertThat(result)
                .isNotNull()
                .hasSize(2)
                .containsEntry(1, "true")
                .containsEntry(2, "false");
    }

    @Test
    @DisplayName("findAllCronEnabled - should return empty when none")
    void findAllCronEnabled_shouldReturnEmptyWhenNone() {
        when(settingValueRepository.findBySettingCode(SettingConfig.CRON_ENABLE_SETTING_CODE))
                .thenReturn(new ArrayList<>());

        Map<Integer, String> result = settingAccessService.findAllCronEnabled();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getValueByCode - should return value")
    void getValueByCode_shouldReturnValue() {
        // Arrange
        String code = "test";
        Integer userId = 2;
        SettingValueEntity entity = createSettingValue(TEST_SETTING_VALUE_ID, userId);
        // Устанавливаем тип STRING у настройки
        entity.getSetting().setType(SettingType.STRING);

        SettingDto dto = new SettingDto();
        dto.setCode(code);
        dto.setStringValue("test_value");
        dto.setType(SettingType.STRING); // Важно: устанавливаем тип

        when(settingValueRepository.findBySettingCodeAndUserId(code, userId))
                .thenReturn(Optional.of(entity));
        when(settingMapper.fromEntity(entity)).thenReturn(dto);

        // Act
        String result = settingAccessService.getValueByCode(code, userId);

        // Assert
        assertThat(result).isEqualTo("test_value");
    }

    @Test
    @DisplayName("getValueByCode - should return null when not found")
    void getValueByCode_shouldReturnNullWhenNotFound() {
        when(settingValueRepository.findBySettingCodeAndUserId("test", 2))
                .thenReturn(Optional.empty());

        String result = settingAccessService.getValueByCode("test", 2);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("getUserIdByGmail - should return user id")
    void getUserIdByGmail_shouldReturnUserId() {
        String gmail = "test@test.com";
        when(settingValueRepository.findUserIdBySettingCodeAndStringValue(
                SettingConfig.GMAIL_VALUE_SETTING_CODE, gmail))
                .thenReturn(Arrays.asList(TEST_USER_ID));

        Integer result = settingAccessService.getUserIdByGmail(gmail);

        assertThat(result).isEqualTo(TEST_USER_ID);
    }

    @Test
    @DisplayName("getUserIdByGmail - should return null when multiple users")
    void getUserIdByGmail_shouldReturnNullWhenMultipleUsers() {
        String gmail = "test@test.com";
        when(settingValueRepository.findUserIdBySettingCodeAndStringValue(
                SettingConfig.GMAIL_VALUE_SETTING_CODE, gmail))
                .thenReturn(Arrays.asList(1, 2, 3));

        Integer result = settingAccessService.getUserIdByGmail(gmail);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("getUserIdByGmail - should return null when none")
    void getUserIdByGmail_shouldReturnNullWhenNone() {
        String gmail = "test@test.com";
        when(settingValueRepository.findUserIdBySettingCodeAndStringValue(
                SettingConfig.GMAIL_VALUE_SETTING_CODE, gmail))
                .thenReturn(new ArrayList<>());

        Integer result = settingAccessService.getUserIdByGmail(gmail);

        assertThat(result).isNull();
    }

    // ========== AbstractAccessService Tests ==========

    @Test
    @DisplayName("get - should return entity by id")
    void get_shouldReturnEntityById() {
        try (MockedStatic<UserUtil> userUtilMock = mockStatic(UserUtil.class)) {
            userUtilMock.when(UserUtil::getCurrentUser).thenReturn(createCurrentUser(TEST_USER_ID));

            SettingValueEntity entity = createSettingValue(TEST_SETTING_VALUE_ID, TEST_USER_ID);
            SettingDto dto = createDto(TEST_SETTING_CODE, "value");

            when(settingValueRepository.findByIdAndUserId(TEST_SETTING_VALUE_ID, TEST_USER_ID))
                    .thenReturn(Optional.of(entity));
            when(settingMapper.fromEntity(entity)).thenReturn(dto);

            SettingDto result = settingAccessService.get(TEST_SETTING_VALUE_ID);

            assertThat(result).isNotNull();
            verify(settingValueRepository).findByIdAndUserId(TEST_SETTING_VALUE_ID, TEST_USER_ID);
        }
    }

    @Test
    @DisplayName("get - should throw when not found")
    void get_shouldThrowWhenNotFound() {
        try (MockedStatic<UserUtil> userUtilMock = mockStatic(UserUtil.class)) {
            userUtilMock.when(UserUtil::getCurrentUser).thenReturn(createCurrentUser(TEST_USER_ID));

            when(settingValueRepository.findByIdAndUserId(TEST_SETTING_VALUE_ID, TEST_USER_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> settingAccessService.get(TEST_SETTING_VALUE_ID))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Не существует сущности <<настройки>> с id=" + TEST_SETTING_VALUE_ID);
        }
    }

    @Test
    @DisplayName("add - should add list of entities")
    void add_shouldAddListOfEntities() {
        try (MockedStatic<UserUtil> userUtilMock = mockStatic(UserUtil.class)) {
            userUtilMock.when(UserUtil::getCurrentUser).thenReturn(createCurrentUser(TEST_USER_ID));

            List<SettingDto> dtos = createDtos(3);
            List<SettingValueEntity> entities = Arrays.asList(
                    new SettingValueEntity(), new SettingValueEntity(), new SettingValueEntity()
            );

            when(settingMapper.toEntityList(dtos)).thenReturn(entities);
            when(settingValueRepository.saveAll(anyList())).thenReturn(entities);

            settingAccessService.add(dtos);

            verify(settingMapper).toEntityList(dtos);
            verify(settingValueRepository).saveAll(anyList());
        }
    }

    @Test
    @DisplayName("delete - should delete entity")
    void delete_shouldDeleteEntity() {
        try (MockedStatic<UserUtil> userUtilMock = mockStatic(UserUtil.class)) {
            userUtilMock.when(UserUtil::getCurrentUser).thenReturn(createCurrentUser(TEST_USER_ID));

            doNothing().when(settingValueRepository).deleteByIdAndUserId(TEST_SETTING_VALUE_ID, TEST_USER_ID);

            settingAccessService.delete(TEST_SETTING_VALUE_ID);

            verify(settingValueRepository).deleteByIdAndUserId(TEST_SETTING_VALUE_ID, TEST_USER_ID);
        }
    }

    @Test
    @DisplayName("getCurrentUserId - should return current user id")
    void getCurrentUserId_shouldReturnCurrentUserId() {
        try (MockedStatic<UserUtil> userUtilMock = mockStatic(UserUtil.class)) {
            userUtilMock.when(UserUtil::getCurrentUser).thenReturn(createCurrentUser(TEST_USER_ID));

            Integer result = settingAccessService.getCurrentUserId();

            assertThat(result).isEqualTo(TEST_USER_ID);
        }
    }
}
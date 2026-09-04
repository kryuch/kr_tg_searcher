package ru.kryuch.krtg.searcher.service.setting;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.kryuch.krtg.searcher.dto.setting.SettingGroupDto;
import ru.kryuch.krtg.searcher.entity.setting.SettingGroupEntity;
import ru.kryuch.krtg.searcher.mapper.setting.SettingGroupMapper;
import ru.kryuch.krtg.searcher.repository.setting.SettingGroupRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SettingGroupService Tests")
class SettingGroupServiceTest {

    @Mock
    private SettingGroupRepository settingGroupRepository;

    @Mock
    private SettingGroupMapper settingGroupMapper;

    @InjectMocks
    private SettingGroupService settingGroupService;

    // ========== Helper Methods ==========

    private SettingGroupEntity createSettingGroupEntity(Long id, String code, String title) {
        SettingGroupEntity entity = new SettingGroupEntity();
        entity.setId(id);
        entity.setCode(code);
        entity.setTitle(title);
        entity.setActive(true);
        entity.setSortOrder(0);
        return entity;
    }

    private SettingGroupDto createSettingGroupDto(String code, String title) {
        SettingGroupDto dto = new SettingGroupDto();
        dto.setCode(code);
        dto.setTitle(title);
        return dto;
    }

    // ========== Tests ==========

    @Test
    @DisplayName("getAll - should return all setting groups")
    void getAll_shouldReturnAllSettingGroups() {
        // Arrange
        SettingGroupEntity entity1 = createSettingGroupEntity(1L, "GROUP_1", "Group 1");
        SettingGroupEntity entity2 = createSettingGroupEntity(2L, "GROUP_2", "Group 2");
        SettingGroupEntity entity3 = createSettingGroupEntity(3L, "GROUP_3", "Group 3");

        List<SettingGroupEntity> entities = Arrays.asList(entity1, entity2, entity3);

        SettingGroupDto dto1 = createSettingGroupDto("GROUP_1", "Group 1");
        SettingGroupDto dto2 = createSettingGroupDto("GROUP_2", "Group 2");
        SettingGroupDto dto3 = createSettingGroupDto("GROUP_3", "Group 3");

        List<SettingGroupDto> expectedDtos = Arrays.asList(dto1, dto2, dto3);

        when(settingGroupRepository.findAll()).thenReturn(entities);
        when(settingGroupMapper.fromEntityList(entities)).thenReturn(expectedDtos);

        // Act
        List<SettingGroupDto> result = settingGroupService.getAll();

        // Assert
        assertThat(result)
                .isNotNull()
                .hasSize(3)
                .containsExactly(dto1, dto2, dto3);

        verify(settingGroupRepository).findAll();
        verify(settingGroupMapper).fromEntityList(entities);
        verifyNoMoreInteractions(settingGroupRepository, settingGroupMapper);
    }

    @Test
    @DisplayName("getAll - should return empty list when no setting groups exist")
    void getAll_shouldReturnEmptyListWhenNoSettingGroups() {
        // Arrange
        List<SettingGroupEntity> entities = Collections.emptyList();

        when(settingGroupRepository.findAll()).thenReturn(entities);
        when(settingGroupMapper.fromEntityList(entities)).thenReturn(Collections.emptyList());

        // Act
        List<SettingGroupDto> result = settingGroupService.getAll();

        // Assert
        assertThat(result)
                .isNotNull()
                .isEmpty();

        verify(settingGroupRepository).findAll();
        verify(settingGroupMapper).fromEntityList(entities);
        verifyNoMoreInteractions(settingGroupRepository, settingGroupMapper);
    }

    @Test
    @DisplayName("getAll - should handle null values in entities gracefully")
    void getAll_shouldHandleNullValuesInEntitiesGracefully() {
        // Arrange
        SettingGroupEntity entity1 = createSettingGroupEntity(1L, "GROUP_1", "Group 1");
        SettingGroupEntity entity2 = null;
        SettingGroupEntity entity3 = createSettingGroupEntity(3L, "GROUP_3", "Group 3");

        List<SettingGroupEntity> entities = Arrays.asList(entity1, entity2, entity3);

        SettingGroupDto dto1 = createSettingGroupDto("GROUP_1", "Group 1");
        SettingGroupDto dto3 = createSettingGroupDto("GROUP_3", "Group 3");
        List<SettingGroupDto> expectedDtos = Arrays.asList(dto1, dto3);

        when(settingGroupRepository.findAll()).thenReturn(entities);
        when(settingGroupMapper.fromEntityList(entities)).thenReturn(expectedDtos);

        // Act
        List<SettingGroupDto> result = settingGroupService.getAll();

        // Assert
        assertThat(result)
                .isNotNull()
                .hasSize(2)
                .containsExactly(dto1, dto3);

        verify(settingGroupRepository).findAll();
        verify(settingGroupMapper).fromEntityList(entities);
    }

    @Test
    @DisplayName("getAll - should correctly map entity fields to DTO")
    void getAll_shouldCorrectlyMapEntityFieldsToDto() {
        // Arrange
        SettingGroupEntity entity1 = createSettingGroupEntity(1L, "ACTIVE", "Active Group");
        entity1.setSortOrder(1);
        entity1.setActive(true);

        SettingGroupEntity entity2 = createSettingGroupEntity(2L, "INACTIVE", "Inactive Group");
        entity2.setSortOrder(2);
        entity2.setActive(false);

        List<SettingGroupEntity> entities = Arrays.asList(entity1, entity2);

        SettingGroupDto dto1 = createSettingGroupDto("ACTIVE", "Active Group");
        SettingGroupDto dto2 = createSettingGroupDto("INACTIVE", "Inactive Group");
        List<SettingGroupDto> expectedDtos = Arrays.asList(dto1, dto2);

        when(settingGroupRepository.findAll()).thenReturn(entities);
        when(settingGroupMapper.fromEntityList(entities)).thenReturn(expectedDtos);

        // Act
        List<SettingGroupDto> result = settingGroupService.getAll();

        // Assert
        assertThat(result)
                .isNotNull()
                .hasSize(2);

        // Проверяем, что поля правильно мапятся
        assertThat(result.get(0).getCode()).isEqualTo("ACTIVE");
        assertThat(result.get(0).getTitle()).isEqualTo("Active Group");

        assertThat(result.get(1).getCode()).isEqualTo("INACTIVE");
        assertThat(result.get(1).getTitle()).isEqualTo("Inactive Group");

        // Важно: в DTO нет полей sortOrder и active, они игнорируются маппером

        verify(settingGroupRepository).findAll();
        verify(settingGroupMapper).fromEntityList(entities);
    }

    @Test
    @DisplayName("getAll - should handle large number of setting groups")
    void getAll_shouldHandleLargeNumberOfSettingGroups() {
        // Arrange
        int count = 100;
        List<SettingGroupEntity> entities = new java.util.ArrayList<>();
        List<SettingGroupDto> expectedDtos = new java.util.ArrayList<>();

        for (int i = 1; i <= count; i++) {
            SettingGroupEntity entity = createSettingGroupEntity((long) i, "GROUP_" + i, "Group " + i);
            entities.add(entity);

            SettingGroupDto dto = createSettingGroupDto("GROUP_" + i, "Group " + i);
            expectedDtos.add(dto);
        }

        when(settingGroupRepository.findAll()).thenReturn(entities);
        when(settingGroupMapper.fromEntityList(entities)).thenReturn(expectedDtos);

        // Act
        List<SettingGroupDto> result = settingGroupService.getAll();

        // Assert
        assertThat(result)
                .isNotNull()
                .hasSize(count);

        assertThat(result.get(0).getCode()).isEqualTo("GROUP_1");
        assertThat(result.get(count - 1).getCode()).isEqualTo("GROUP_" + count);

        verify(settingGroupRepository).findAll();
        verify(settingGroupMapper).fromEntityList(entities);
    }

    @Test
    @DisplayName("getAll - should verify mapper is called with correct list")
    void getAll_shouldVerifyMapperIsCalledWithCorrectList() {
        // Arrange
        SettingGroupEntity entity1 = createSettingGroupEntity(1L, "TEST_1", "Test 1");
        SettingGroupEntity entity2 = createSettingGroupEntity(2L, "TEST_2", "Test 2");

        List<SettingGroupEntity> entities = Arrays.asList(entity1, entity2);

        SettingGroupDto dto1 = createSettingGroupDto("TEST_1", "Test 1");
        SettingGroupDto dto2 = createSettingGroupDto("TEST_2", "Test 2");
        List<SettingGroupDto> expectedDtos = Arrays.asList(dto1, dto2);

        when(settingGroupRepository.findAll()).thenReturn(entities);
        when(settingGroupMapper.fromEntityList(entities)).thenReturn(expectedDtos);

        // Act
        settingGroupService.getAll();

        // Assert
        verify(settingGroupRepository).findAll();
        verify(settingGroupMapper).fromEntityList(argThat(list -> {
            List<SettingGroupEntity> argList = (List<SettingGroupEntity>) list;
            return argList.size() == 2 &&
                    argList.get(0).getId().equals(1L) &&
                    argList.get(0).getCode().equals("TEST_1") &&
                    argList.get(1).getId().equals(2L) &&
                    argList.get(1).getCode().equals("TEST_2");
        }));
    }

    @Test
    @DisplayName("getAll - should propagate repository exception")
    void getAll_shouldPropagateRepositoryException() {
        // Arrange
        when(settingGroupRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThatThrownBy(() -> settingGroupService.getAll())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Database error");

        verify(settingGroupRepository).findAll();
        verify(settingGroupMapper, never()).fromEntityList(any());
    }

    @Test
    @DisplayName("getAll - should handle entities with different active states")
    void getAll_shouldHandleEntitiesWithDifferentActiveStates() {
        // Arrange
        SettingGroupEntity entity1 = createSettingGroupEntity(1L, "ACTIVE_1", "Active 1");
        entity1.setActive(true);

        SettingGroupEntity entity2 = createSettingGroupEntity(2L, "INACTIVE_1", "Inactive 1");
        entity2.setActive(false);

        SettingGroupEntity entity3 = createSettingGroupEntity(3L, "ACTIVE_2", "Active 2");
        entity3.setActive(true);

        List<SettingGroupEntity> entities = Arrays.asList(entity1, entity2, entity3);

        SettingGroupDto dto1 = createSettingGroupDto("ACTIVE_1", "Active 1");
        SettingGroupDto dto2 = createSettingGroupDto("INACTIVE_1", "Inactive 1");
        SettingGroupDto dto3 = createSettingGroupDto("ACTIVE_2", "Active 2");
        List<SettingGroupDto> expectedDtos = Arrays.asList(dto1, dto2, dto3);

        when(settingGroupRepository.findAll()).thenReturn(entities);
        when(settingGroupMapper.fromEntityList(entities)).thenReturn(expectedDtos);

        // Act
        List<SettingGroupDto> result = settingGroupService.getAll();

        // Assert
        assertThat(result)
                .isNotNull()
                .hasSize(3);

        // DTO не содержит поле active, поэтому проверяем только code и title
        assertThat(result.get(0).getCode()).isEqualTo("ACTIVE_1");
        assertThat(result.get(1).getCode()).isEqualTo("INACTIVE_1");
        assertThat(result.get(2).getCode()).isEqualTo("ACTIVE_2");

        verify(settingGroupRepository).findAll();
        verify(settingGroupMapper).fromEntityList(entities);
    }

    @Test
    @DisplayName("getAll - should handle entities with sortOrder")
    void getAll_shouldHandleEntitiesWithSortOrder() {
        // Arrange
        SettingGroupEntity entity1 = createSettingGroupEntity(1L, "FIRST", "First Group");
        entity1.setSortOrder(1);

        SettingGroupEntity entity2 = createSettingGroupEntity(2L, "SECOND", "Second Group");
        entity2.setSortOrder(2);

        SettingGroupEntity entity3 = createSettingGroupEntity(3L, "THIRD", "Third Group");
        entity3.setSortOrder(3);

        List<SettingGroupEntity> entities = Arrays.asList(entity1, entity2, entity3);

        SettingGroupDto dto1 = createSettingGroupDto("FIRST", "First Group");
        SettingGroupDto dto2 = createSettingGroupDto("SECOND", "Second Group");
        SettingGroupDto dto3 = createSettingGroupDto("THIRD", "Third Group");
        List<SettingGroupDto> expectedDtos = Arrays.asList(dto1, dto2, dto3);

        when(settingGroupRepository.findAll()).thenReturn(entities);
        when(settingGroupMapper.fromEntityList(entities)).thenReturn(expectedDtos);

        // Act
        List<SettingGroupDto> result = settingGroupService.getAll();

        // Assert
        assertThat(result)
                .isNotNull()
                .hasSize(3);

        // Проверяем, что сортировка сохранилась (порядок из репозитория)
        assertThat(result.get(0).getCode()).isEqualTo("FIRST");
        assertThat(result.get(1).getCode()).isEqualTo("SECOND");
        assertThat(result.get(2).getCode()).isEqualTo("THIRD");

        verify(settingGroupRepository).findAll();
        verify(settingGroupMapper).fromEntityList(entities);
    }
}
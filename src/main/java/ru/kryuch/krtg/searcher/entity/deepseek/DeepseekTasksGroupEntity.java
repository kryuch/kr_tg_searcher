package ru.kryuch.krtg.searcher.entity.deepseek;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.kryuch.krtg.searcher.entity.BasedAccessEntity;

@Entity
@Table(name = "krrg_deepseek_tasks_group")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeepseekTasksGroupEntity extends BasedAccessEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

}

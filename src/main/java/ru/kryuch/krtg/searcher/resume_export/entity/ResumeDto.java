package ru.kryuch.krtg.searcher.resume_export.entity;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
public class ResumeDto {

    private Integer schemaVersion;
    private Person person;
    private List<Education> education;
    private List<Profile> profiles;

    @Data
    public static class Person {
        private String lastName;
        private String firstName;
        private String middleName;
        private LocalDate birthDate;
        private Gender gender;
        private String citizenship;
    }

    @Data
    public static class Education {
        private String university;
        private String specialty;
        private String educationType;
        private Integer startYear;
        private Integer endYear;
    }

    @Data
    public static class Profile {
        private String direction;
        private String grade;
        private List<Skill> skills;
        private List<Project> projects;
    }

    @Data
    public static class Skill {
        private String name;
        private SkillCategory category;
        private Integer level;
    }

    @Data
    public static class Project {
        private String organization;
        private String position;
        private LocalDate startDate;
        private LocalDate endDate;
        private String description;
        private List<String> technologies;
    }

    @RequiredArgsConstructor
    @Getter
    public enum Gender {
        MALE("Мужчина"),
        FEMALE("Женщина");

        private final String label;
    }

    @RequiredArgsConstructor
    @Getter
    public enum SkillCategory {
        LANGUAGE("язык программирования"),
        FRAMEWORK("фреймворк"),
        DATABASE("база данных"),
        TOOL("инструмент"),
        METHODOLOGY("методология"),
        SOFT_SKILL("soft skill"),
        OTHER("другое");

        private final String label;
    }
}

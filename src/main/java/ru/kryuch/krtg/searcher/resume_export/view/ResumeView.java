package ru.kryuch.krtg.searcher.resume_export.view;

import ru.kryuch.krtg.searcher.dto.resume.ResumeDto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


// TODO: разделить логику, суперспагеттикласс
public record ResumeView(
        String fullName,
        String meta,
        List<EducationView> education,
        List<ProfileView> profiles
) {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter MONTH_YEAR_FORMAT = DateTimeFormatter.ofPattern("MM.yyyy");

    private static final String FALLBACK_TITLE = "Резюме";
    private static final String FALLBACK_EDUCATION = "Учебное заведение";
    private static final String FALLBACK_PROFILE = "Профиль";
    private static final String FALLBACK_SKILL = "Навык";
    private static final String FALLBACK_PROJECT = "Проект";
    private static final String UNKNOWN_START = "…";
    private static final String PRESENT = "наст. время";

    public static ResumeView from(ResumeDto resume) {
        if (resume == null) {
            return new ResumeView(FALLBACK_TITLE, null, List.of(), List.of());
        }
        return new ResumeView(
                fullName(resume.getPerson()),
                meta(resume.getPerson()),
                mapList(resume.getEducation(), ResumeView::toEducationView),
                mapList(resume.getProfiles(), ResumeView::toProfileView)
        );
    }

    public boolean isMetaVisible() {
        return !isBlank(meta);
    }

    public boolean isEducationVisible() {
        return !education.isEmpty();
    }

    public boolean isProfilesVisible() {
        return !profiles.isEmpty();
    }

    private static String fullName(ResumeDto.Person person) {
        if (person == null) {
            return FALLBACK_TITLE;
        }
        String name = joinNonBlank(" ", person.getLastName(), person.getFirstName(), person.getMiddleName());
        return defaultIfBlank(name, FALLBACK_TITLE);
    }

    private static String meta(ResumeDto.Person person) {
        if (person == null) {
            return null;
        }
        return joinNonBlank(" · ",
                formatDate(person.getBirthDate()),
                labelOf(person.getGender(), ResumeDto.Gender::getLabel),
                person.getCitizenship()
        );
    }

    private static EducationView toEducationView(ResumeDto.Education education) {
        return new EducationView(
                defaultIfBlank(education.getUniversity(), FALLBACK_EDUCATION),
                joinNonBlank(", ", education.getEducationType(), education.getSpecialty()),
                formatRange(yearLabel(education.getStartYear()), yearLabel(education.getEndYear()))
        );
    }

    private static ProfileView toProfileView(ResumeDto.Profile profile) {
        String title = joinNonBlank(" · ", profile.getDirection(), profile.getGrade());
        return new ProfileView(
                defaultIfBlank(title, FALLBACK_PROFILE),
                mapList(profile.getSkills(), ResumeView::toSkillView),
                mapList(profile.getProjects(), ResumeView::toProjectView)
        );
    }

    private static SkillView toSkillView(ResumeDto.Skill skill) {
        return new SkillView(
                defaultIfBlank(skill.getName(), FALLBACK_SKILL),
                skill.getLevel() == null ? null : skill.getLevel() + "/5",
                labelOf(skill.getCategory(), ResumeDto.SkillCategory::getLabel)
        );
    }

    private static ProjectView toProjectView(ResumeDto.Project project) {
        String title = joinNonBlank(" — ", project.getOrganization(), project.getPosition());
        return new ProjectView(
                defaultIfBlank(title, FALLBACK_PROJECT),
                formatRange(formatMonthYear(project.getStartDate()), formatMonthYear(project.getEndDate())),
                project.getDescription(),
                joinNonBlank(", ", project.getTechnologies())
        );
    }

    private static String formatDate(LocalDate date) {
        return date == null ? null : DATE_FORMAT.format(date);
    }

    private static String formatMonthYear(LocalDate date) {
        return date == null ? null : MONTH_YEAR_FORMAT.format(date);
    }

    private static String formatRange(String start, String end) {
        if (start == null && end == null) {
            return null;
        }
        return defaultIfBlank(start, UNKNOWN_START) + " — " + defaultIfBlank(end, PRESENT);
    }

    private static String yearLabel(Integer year) {
        return year == null ? null : String.valueOf(year);
    }

    private static <T, R> List<R> mapList(Collection<T> source, Function<T, R> mapper) {
        if (source == null) {
            return List.of();
        }
        return source.stream()
                .filter(Objects::nonNull)
                .map(mapper)
                .toList();
    }

    private static <T> String labelOf(T value, Function<T, String> label) {
        return value == null ? null : label.apply(value);
    }

    private static String joinNonBlank(String delimiter, Collection<?> values) {
        return values == null ? "" : joinNonBlank(delimiter, values.toArray());
    }

    private static String joinNonBlank(String delimiter, Object... values) {
        return Stream.of(values)
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .collect(Collectors.joining(delimiter));
    }

    private static String defaultIfBlank(String value, String fallback) {
        return isBlank(value) ? fallback : value.trim();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    // Вынести нестед классы в отдельные файлы не стал, т.к. они используются только в ResumeView и не имеют смысла без него.
    public record EducationView(String university, String details, String period) {

        public boolean isDetailsVisible() {
            return !isBlank(details);
        }

        public boolean isPeriodVisible() {
            return !isBlank(period);
        }
    }

    public record ProfileView(String title, List<SkillView> skills, List<ProjectView> projects) {

        public boolean isSkillsVisible() {
            return !skills.isEmpty();
        }

        public boolean isProjectsVisible() {
            return !projects.isEmpty();
        }
    }

    public record SkillView(String name, String levelLabel, String category) {

        public boolean isLevelVisible() {
            return !isBlank(levelLabel);
        }

        public boolean isCategoryVisible() {
            return !isBlank(category);
        }
    }

    public record ProjectView(String title, String period, String description, String technologies) {

        public boolean isPeriodVisible() {
            return !isBlank(period);
        }

        public boolean isDescriptionVisible() {
            return !isBlank(description);
        }

        public boolean isTechnologiesVisible() {
            return !isBlank(technologies);
        }
    }
}

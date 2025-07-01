package de.gaz.eedu.course.subject;

import de.gaz.eedu.course.model.CourseModel;
import de.gaz.eedu.course.subject.model.SubjectCreateModel;
import de.gaz.eedu.course.subject.model.SubjectModel;
import de.gaz.eedu.entity.EntityController;
import de.gaz.eedu.exception.CreationException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/v1/course/subject")
@RequiredArgsConstructor
public class SubjectController extends EntityController<String, SubjectService, SubjectModel, SubjectCreateModel>
{
    @Getter(AccessLevel.PROTECTED) private final SubjectService service;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority(T(de.gaz.eedu.user.privileges.SystemPrivileges).SUBJECT_CREATE.toString())") @Override
    public @NotNull ResponseEntity<SubjectModel[]> create(@NotNull @RequestBody SubjectCreateModel[] model) throws CreationException
    {
        return super.create(model);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority(T(de.gaz.eedu.user.privileges.SystemPrivileges).SUBJECT_DELETE.toString())") @Override
    public @NotNull ResponseEntity<Void> delete(@NotNull @PathVariable String[] id)
    {
        return super.delete(id);
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("hasAuthority(T(de.gaz.eedu.user.privileges.SystemPrivileges).SUBJECT_GET.toString())") @Override
    public @NotNull ResponseEntity<SubjectModel> getData(@NotNull @PathVariable String id)
    {
        return super.getData(id);
    }

    @GetMapping("/get/all")
    @PreAuthorize("hasAuthority(T(de.gaz.eedu.user.privileges.SystemPrivileges).SUBJECT_GET.toString())") @Override
    public @NotNull ResponseEntity<Set<SubjectModel>> fetchAll()
    {
        return super.fetchAll();
    }

    @GetMapping("/courses/{subjects}") @PreAuthorize(
            "hasAuthority(T(de.gaz.eedu.user.privileges.SystemPrivileges).SUBJECT_GET.toString()) and hasAuthority(T(de.gaz.eedu.user.privileges.SystemPrivileges).COURSE_GET.toString())"
    ) public @NotNull ResponseEntity<CourseModel[]> getCourses(@NotNull @PathVariable String[] subjects)
    {
        return ResponseEntity.ok(getService().loadCourses(subjects));
    }
}

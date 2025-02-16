package de.gaz.eedu.course.subject;

import de.gaz.eedu.course.subject.model.SubjectCreateModel;
import de.gaz.eedu.course.subject.model.SubjectModel;
import de.gaz.eedu.entity.EntityController;
import de.gaz.eedu.exception.CreationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

//TODO manage access

@Slf4j
@RestController
@RequestMapping("/api/v1/course/subject")
@RequiredArgsConstructor
public class SubjectController extends EntityController<SubjectService, SubjectModel, SubjectCreateModel>
{
    private final SubjectService subjectService;

    @Override
    protected @NotNull SubjectService getService()
    {
        return subjectService;
    }

    //@PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/create") @Override public @NotNull ResponseEntity<SubjectModel[]> create(@NotNull @RequestBody SubjectCreateModel[] model) throws CreationException
    {
        log.info("WORK???");
        return super.create(model);
    }

    //@PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/delete/{id}")
    @Override
    public @NotNull Boolean delete(@NotNull @PathVariable Long id)
    {
        return super.delete(id);
    }

    //@PreAuthorize("isAuthenticated()")
    @GetMapping("/get/{id}")
    @Override
    public @NotNull ResponseEntity<SubjectModel> getData(@NotNull @PathVariable Long id)
    {
        return super.getData(id);
    }

    @GetMapping("/get/all")
    @Override public @NotNull ResponseEntity<Set<SubjectModel>> fetchAll()
    {
        return super.fetchAll();
    }
}

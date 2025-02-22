package de.gaz.eedu.course.subjects;

import de.gaz.eedu.course.subjects.model.SubjectCreateModel;
import de.gaz.eedu.course.subjects.model.SubjectModel;
import de.gaz.eedu.entity.EntityController;
import de.gaz.eedu.exception.CreationException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//TODO manage access

@RestController
@RequestMapping("/course/subject")
@RequiredArgsConstructor
public class SubjectController extends EntityController<Long, SubjectService, SubjectModel, SubjectCreateModel>
{
    private final SubjectService subjectService;

    @Override
    protected @NotNull SubjectService getService()
    {
        return subjectService;
    }

    //@PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/create")
    @Override
    public @NotNull ResponseEntity<SubjectModel[]> create(@NotNull @RequestBody SubjectCreateModel[] model) throws CreationException
    {
        return super.create(model);
    }

    //@PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/delete/{id}")
    @Override
    public @NotNull Boolean delete(@NotNull @PathVariable Long[] id)
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
}

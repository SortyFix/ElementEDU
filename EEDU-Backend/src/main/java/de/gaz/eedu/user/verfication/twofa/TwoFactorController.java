package de.gaz.eedu.user.verfication.twofa;

import de.gaz.eedu.entity.EntityController;
import de.gaz.eedu.user.verfication.twofa.model.TwoFactorCreateModel;
import de.gaz.eedu.user.verfication.twofa.model.TwoFactorModel;
import de.gaz.eedu.user.verfication.twofa.model.TwoFactorRequestModel;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/twofa") public class TwoFactorController extends EntityController<TwoFactorService,
        TwoFactorModel, TwoFactorCreateModel>
{
    public TwoFactorController(@Autowired @NotNull TwoFactorService entityService)
    {
        super(entityService);
    }

    /*@PreAuthorize("hasAuthority('$ADVANCED_USER_MANAGEMENT') or hasAuthority('$TWO_FACTOR_CREATE')")*/
    @PostMapping("/request") public @NotNull ResponseEntity<?> requestTwoFactor(@NotNull @RequestBody TwoFactorRequestModel twoFactorRequestModel)
    {
        return ResponseEntity.ok(getEntityService().setup(twoFactorRequestModel.userId(), twoFactorRequestModel.twoFactorMethod()));
    }

    @PreAuthorize("hasAuthority('$ADVANCED_USER_MANAGEMENT') or hasAuthority('$TWO_FACTOR_CREATE')") @PostMapping(
            "/create") @Override public @NotNull ResponseEntity<TwoFactorModel> create(@NotNull @RequestBody TwoFactorCreateModel model)
    {
        return super.create(model);
    }

    @PreAuthorize("hasAuthority('$ADVANCED_USER_MANAGEMENT') or hasAuthority('ADMIN')") @GetMapping("/delete/{id}") @Override public @NotNull Boolean delete(@NotNull @PathVariable Long id)
    {
        return super.delete(id);
    }
}

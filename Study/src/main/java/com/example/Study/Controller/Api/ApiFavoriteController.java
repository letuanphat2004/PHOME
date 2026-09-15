package com.example.Study.Controller.Api;

import com.example.Study.Model.DTO.RoomDTO;
import com.example.Study.Service.FavoriteService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/favorites")
@PreAuthorize("hasAuthority('Tenant')")
public class ApiFavoriteController {
    private final FavoriteService favorites;

    public ApiFavoriteController(FavoriteService favorites) {
        this.favorites = favorites;
    }

    @GetMapping
    public List<RoomDTO> favorites(Authentication authentication) {
        return favorites.getFavorites(authentication.getName());
    }

    @PostMapping("/{roomId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void add(@PathVariable long roomId, Authentication authentication) {
        favorites.addFavorite(authentication.getName(), roomId);
    }

    @DeleteMapping("/{roomId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable long roomId, Authentication authentication) {
        favorites.removeFavorite(authentication.getName(), roomId);
    }
}

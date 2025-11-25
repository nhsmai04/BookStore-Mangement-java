package org.librarymanagement.service;

import org.librarymanagement.entity.Genre;

import java.util.Set;

public interface GenreService {
    Genre findOrCreateGenre(String genreName);

}

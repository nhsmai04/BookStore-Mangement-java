package org.librarymanagement.service;

import org.librarymanagement.entity.Genre;

public interface GenreService {
    Genre findOrCreateGenre(String genreName);
}

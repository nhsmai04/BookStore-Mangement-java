package org.librarymanagement.service.impl;

import org.librarymanagement.entity.Genre;
import org.librarymanagement.repository.GenreRepository;
import org.librarymanagement.repository.PublisherRepository;
import org.librarymanagement.service.GenreService;
import org.librarymanagement.service.SlugService;
import org.springframework.stereotype.Service;

@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    public GenreServiceImpl(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public Genre findOrCreateGenre(String genreName){
        return genreRepository.findByName(genreName)
                .orElseGet(() -> {
                    Genre gen = new Genre();
                    gen.setName(genreName);
                    return genreRepository.save(gen);
                });
    }
}

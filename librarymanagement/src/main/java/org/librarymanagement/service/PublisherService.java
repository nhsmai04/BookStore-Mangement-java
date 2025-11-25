package org.librarymanagement.service;

import org.librarymanagement.dto.response.ResponseObject;
import org.librarymanagement.entity.Publisher;

import java.util.Set;

public interface PublisherService {
    Publisher findOrCreatePublisher(String publisherName);
    ResponseObject getPublisher(String slug);
    public Publisher findOrCreatePublisherInMemory(
            String name,
            Set<String> usedSlugs
    );
}

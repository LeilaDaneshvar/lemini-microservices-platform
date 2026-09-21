package com.lemini.users.ui.model.response;

import java.util.List;


public record UserPageResponse(
    List<UserRest> users,
    int pageNumber,
    int pageSize,
    long totalElements,
    int totalPages,
    boolean last
) {
}

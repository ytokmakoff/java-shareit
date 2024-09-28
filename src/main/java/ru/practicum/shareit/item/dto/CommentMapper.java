package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;

public class CommentMapper {
    private CommentMapper() {
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getUser().getName(),
                comment.getItem(),
                comment.getCreated());
    }
}

package ru.practicum.explore_with_me.comment.service;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore_with_me.comment.dto.CommentDto;
import ru.practicum.explore_with_me.comment.dto.NewCommentRequest;
import ru.practicum.explore_with_me.comment.dto.UpdateCommentRequest;
import ru.practicum.explore_with_me.comment.mapper.CommentMapper;
import ru.practicum.explore_with_me.comment.model.Comment;
import ru.practicum.explore_with_me.comment.model.CommentStatus;
import ru.practicum.explore_with_me.comment.model.QComment;
import ru.practicum.explore_with_me.comment.repository.CommentRepository;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.event.repository.EventRepository;
import ru.practicum.explore_with_me.exception.NotFoundException;
import ru.practicum.explore_with_me.user.model.User;
import ru.practicum.explore_with_me.user.repository.UserRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public CommentDto getCommentById(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new NotFoundException("Комментарий с id = " + id + " не найден"));
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public List<CommentDto> getCommentsByEventId(Long eventId, Integer from, Integer size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by("created").descending());
        List<Comment> commentList = commentRepository.findAllByEventId(eventId, pageable);
        return CommentMapper.toCommentDtoList(commentList);
    }

    @Override
    public List<CommentDto> getMyComments(Long userId, Integer from, Integer size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by("created").descending());
        List<Comment> commentList = commentRepository.findAllByAuthorId(userId, pageable);
        return CommentMapper.toCommentDtoList(commentList);
    }

    @Transactional
    @Override
    public CommentDto createComment(Long userId, NewCommentRequest dto) {
        User author = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        Event event = eventRepository.findById(dto.getEvent()).orElseThrow(() -> new NotFoundException("Событие с id = " + dto.getEvent() + " не найдено"));
        Comment comment = CommentMapper.toComment(dto, event, author);
        comment = commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }

    @Transactional
    @Override
    public CommentDto updateComment(Long userId, Long commentId, UpdateCommentRequest updateCommentRequest) {
        Comment comment = commentRepository.findByIdAndAuthorId(userId, commentId).orElseThrow(() -> new NotFoundException(
                "Комментарий не найден или не принадлежит пользователю"));
        CommentMapper.updateCommentFields(comment, updateCommentRequest);
        commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }

    @Transactional
    @Override
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findByIdAndAuthorId(commentId, userId).orElseThrow(() -> new NotFoundException(
                "Комментарий не найден или не принадлежит пользователю"));
        comment.setStatus(CommentStatus.DELETED);
        commentRepository.save(comment);
    }

    @Override
    public List<CommentDto> getAllComment(Long userId, Long eventId, CommentStatus status, Integer from, Integer size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by("created").descending());

        BooleanBuilder builder = new BooleanBuilder();

        if (userId != null) {
            builder.and(QComment.comment.author.id.eq(userId));
        }

        if (eventId != null) {
           builder.and(QComment.comment.event.id.eq(eventId));
        }

        if (status != null) {
            builder.and(QComment.comment.status.eq(status));
        }


       List<Comment> comments = commentRepository.findAll(builder,pageable).toList();


        return CommentMapper.toCommentDtoList(comments);
    }


}

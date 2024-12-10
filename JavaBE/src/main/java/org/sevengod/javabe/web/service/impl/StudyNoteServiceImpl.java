package org.sevengod.javabe.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.sevengod.javabe.entity.StudyNote;
import org.sevengod.javabe.entity.po.StudyNotePo;
import org.sevengod.javabe.entity.vo.StudyNoteVo;
import org.sevengod.javabe.entity.dto.StudyNoteDto;
import org.sevengod.javabe.web.mapper.StudyNoteMapper;
import org.sevengod.javabe.web.service.StudyNoteService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudyNoteServiceImpl extends ServiceImpl<StudyNoteMapper, StudyNote> implements StudyNoteService {

    @Override
    @Transactional
    public StudyNoteDto createNote(Long userId, StudyNotePo notePo) {
        StudyNote note = convertToEntity(notePo);
        note.setUserId(userId);
        note.setIsActive(true);
        save(note);
        return convertToDto(note);
    }

    @Override
    @Transactional
    public StudyNoteDto updateNote(Long userId, StudyNoteVo noteVo) {
        StudyNote note = findNoteByIdAndUser(noteVo.getNoteId(), userId);
        BeanUtils.copyProperties(noteVo, note);
        updateById(note);
        return convertToDto(note);
    }

    @Override
    @Transactional
    public boolean deleteNote(Long userId, Long noteId) {
        return update(new LambdaUpdateWrapper<StudyNote>()
                .eq(StudyNote::getNoteId, noteId)
                .eq(StudyNote::getUserId, userId)
                .set(StudyNote::getIsActive, false));
    }

    @Override
    public List<StudyNoteDto> getUserNotes(Long userId) {
        return list(new LambdaQueryWrapper<StudyNote>()
                .eq(StudyNote::getUserId, userId)
                .eq(StudyNote::getIsActive, true)
                .orderByDesc(StudyNote::getCreatedAt))
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudyNoteDto> getChapterNotes(Long userId, Long chapterId) {
        return list(new LambdaQueryWrapper<StudyNote>()
                .eq(StudyNote::getChapterId, chapterId)
                .eq(StudyNote::getUserId, userId)
                .eq(StudyNote::getIsActive, true)
                .orderByDesc(StudyNote::getCreatedAt))
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isNoteOwner(Long userId, Long noteId) {
        StudyNote note = getOne(new LambdaQueryWrapper<StudyNote>()
                .eq(StudyNote::getNoteId, noteId)
                .eq(StudyNote::getUserId, userId)
                .eq(StudyNote::getIsActive, true));
        return note != null;
    }

    private StudyNoteDto convertToDto(StudyNote note) {
        StudyNoteDto dto = new StudyNoteDto();
        BeanUtils.copyProperties(note, dto);
        return dto;
    }

    private StudyNote convertToEntity(StudyNoteVo noteVo) {
        StudyNote note = new StudyNote();
        BeanUtils.copyProperties(noteVo, note);
        return note;
    }
    private StudyNote convertToEntity(StudyNotePo notePo) {
        StudyNote note = new StudyNote();
        BeanUtils.copyProperties(notePo, note);
        return note;
    }

    private StudyNote findNoteByIdAndUser(Long noteId, Long userId) {
        StudyNote note = getOne(new LambdaQueryWrapper<StudyNote>()
                .eq(StudyNote::getNoteId, noteId)
                .eq(StudyNote::getUserId, userId)
                .eq(StudyNote::getIsActive, true));
        if (note == null) {
            throw new RuntimeException("笔记不存在或无权限修改");
        }
        return note;
    }
} 
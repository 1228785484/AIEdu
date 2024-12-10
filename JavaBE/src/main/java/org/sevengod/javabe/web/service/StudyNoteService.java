package org.sevengod.javabe.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sevengod.javabe.entity.StudyNote;
import org.sevengod.javabe.entity.po.StudyNotePo;
import org.sevengod.javabe.entity.vo.StudyNoteVo;
import org.sevengod.javabe.entity.dto.StudyNoteDto;
import java.util.List;

public interface StudyNoteService extends IService<StudyNote> {
    
    // 创建笔记
    StudyNoteDto createNote(Long userId, StudyNotePo notePo);
    
    // 更新笔记
    StudyNoteDto updateNote(Long userId, StudyNoteVo noteVo);
    
    // 删除笔记
    boolean deleteNote(Long userId, Long noteId);

    // 检查用户是否是笔记的所有者
    boolean isNoteOwner(Long userId, Long noteId);
    
    // 获取用户的所有笔记
    List<StudyNoteDto> getUserNotes(Long userId);
    
    // 获取章节的笔记
    List<StudyNoteDto> getChapterNotes(Long userId, Long chapterId);
} 
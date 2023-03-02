package com.example.aupo.controller.group;

import com.example.aupo.controller.dto.ResponseList;
import com.example.aupo.controller.guide.GuideRepository;
import com.example.aupo.controller.teacher.TeacherRepository;
import com.example.aupo.exception.NotFoundException;
import com.example.aupo.exception.ValidationException;
import com.example.aupo.tables.Parallel;
import com.example.aupo.tables.daos.GroupDao;
import com.example.aupo.tables.daos.ParallelDao;
import com.example.aupo.tables.pojos.Group;
import com.example.aupo.tables.pojos.Teacher;
import lombok.AllArgsConstructor;
import org.jooq.Condition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.example.aupo.tables.Group.GROUP;
import static com.example.aupo.tables.Parallel.PARALLEL;
import static com.example.aupo.tables.Teacher.TEACHER;

@Service
@AllArgsConstructor
public class GroupRestService {

    private final GroupRepository groupRepository;

    private final TeacherRepository teacherRepository;

    private final GroupDao groupDao;

    private final ParallelDao parallelDao;

    private final GuideRepository guideRepository;

    public ResponseList<Group> list(
            Integer page,
            Integer pageSize,
            Long parallelEntityId,
            Long teacherEntityId,
            Integer year
    ) {
        Condition condition = GROUP.DATETIME_OF_DELETE.isNull();
        if(Objects.nonNull(parallelEntityId)){
            condition = condition.and(GROUP.PARALLEL_ENTITY_ID.eq(parallelEntityId));
        }
        if(Objects.nonNull(teacherEntityId)){
            condition = condition.and(GROUP.TEACHER_ENTITY_ID.eq(teacherEntityId));
        }
        if(Objects.nonNull(parallelEntityId)){
            condition = condition.and(GROUP.YEAR.eq(year));
        }

        List<Group> items = groupRepository.fetch(condition, page, pageSize);
        Long total = groupRepository.getCount(condition);

        ResponseList<Group> result = new ResponseList<>();
        result.setItems(items);
        result.setTotal(total);
        return result;
    }

    public Group getOne(Long entityId) {
        return groupRepository.fetchActualByEntityId(entityId).orElseThrow(NotFoundException::new);
    }

    public void create(GroupCreateDto groupCreateDto) {
        validate(groupCreateDto);
        Group group = getPojoFromCreateDto(groupCreateDto);
        Long entityId = groupRepository.getNextId();
        group.setId(entityId);
        group.setEntityId(entityId);
        groupDao.insert(group);
    }

    @Transactional
    public void update(Group group) {
        Condition condition = GROUP.ENTITY_ID.eq(group.getEntityId()).and(GROUP.DATETIME_OF_DELETE.isNull());
        group.setId(null);

        // Метка удаления старой версии сущности
        groupRepository.updateDateTimeOfDeleteByCondition(condition, LocalDateTime.now());
        groupDao.insert(group);
    }

    protected Group getPojoFromCreateDto(GroupCreateDto groupCreateDto){
        Group group = new Group();
        group.setTeacherEntityId(groupCreateDto.getTeacherEntityId());
        group.setParallelEntityId(groupCreateDto.getParallelEntityId());
        group.setYear(groupCreateDto.getYear());
        group.setDatetimeOfCreation(LocalDateTime.now());
        return group;
    }

    private void validate(GroupCreateDto groupCreateDto){
        StringBuilder stringBuilder = new StringBuilder();
        if(!teacherRepository.exists(TEACHER.ENTITY_ID.eq(groupCreateDto.getTeacherEntityId())
                .and(TEACHER.DATETIME_OF_DELETE.isNull()))){
            stringBuilder.append("Учителя не существует");
            stringBuilder.append("\n");
        }
        if(!guideRepository.existsParallels(PARALLEL.ENTITY_ID.eq(groupCreateDto.getParallelEntityId()))){
            stringBuilder.append("Параллели не существует");
            stringBuilder.append("\n");
        }
        if(!stringBuilder.isEmpty()){
            throw new ValidationException(stringBuilder.toString());
        }
    }

}

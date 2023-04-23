package com.example.aupo.service;

import com.example.aupo.Tables;
import com.example.aupo.controller.dto.ResponseList;
import com.example.aupo.dto.GroupCreateDto;
import com.example.aupo.repository.GuideRepository;
import com.example.aupo.repository.TeacherRepository;
import com.example.aupo.exception.NotFoundException;
import com.example.aupo.exception.ValidationException;
import com.example.aupo.repository.GroupRepository;
import com.example.aupo.tables.daos.GroupDao;
import com.example.aupo.tables.pojos.Group;
import com.example.aupo.tables.pojos.Parallel;
import com.example.aupo.tables.pojos.Teacher;
import com.example.aupo.tables.pojos.Year;
import com.example.aupo.tables.records.GroupRecord;
import com.example.aupo.util.CSVUtil;
import lombok.AllArgsConstructor;
import org.jooq.Condition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static com.example.aupo.tables.Group.GROUP;
import static com.example.aupo.tables.Parallel.PARALLEL;
import static com.example.aupo.tables.Teacher.TEACHER;

@Service
@AllArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;

    private final TeacherRepository teacherRepository;

    private final GroupDao groupDao;

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
            condition = condition.and(GROUP.YEAR_ENTITY_ID.eq(year));
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
        group.setYearEntityId(groupCreateDto.getYearEntityId());
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

    public void saveFromCSV(String fileContent) {
        List<String[]> values = CSVUtil.parseCSV(fileContent, ";");
        List<GroupRecord> groupRecordList = values.stream().map(this::getGroupRecordFromCSVLine).toList();
        validateList(groupRecordList);
        groupRepository.updateDateTimeOfDeleteByIds(
                groupRecordList.stream().map(GroupRecord::getEntityId).toList(),
                LocalDateTime.now());
        groupRepository.batchInsert(groupRecordList);
    }

    private void validateList(List<GroupRecord> groupRecordList) {
        StringBuilder stringBuilder = new StringBuilder();

        List<Long> groupParallels = groupRecordList.stream().map(GroupRecord::getParallelEntityId).toList();
        List<Long> existingParallels = guideRepository.fetchParallels(PARALLEL.ENTITY_ID.in(groupParallels))
                .stream().map(Parallel::getEntityId).toList();
        if(!new HashSet<>(existingParallels).equals(new HashSet<>(groupParallels))){
            stringBuilder.append("Несуществующая параллель");
            stringBuilder.append("\n");
        }

        List<Long> groupTeachers = groupRecordList.stream().map(GroupRecord::getTeacherEntityId).toList();
        List<Long> existingTeachers = teacherRepository.fetch(TEACHER.DATETIME_OF_DELETE.isNull()
                                .and(TEACHER.ENTITY_ID.in(groupTeachers)))
                .stream().map(Teacher::getEntityId).toList();
        if(!new HashSet<>(existingTeachers).equals(new HashSet<>(groupTeachers))){
            stringBuilder.append("Несуществующий учитель");
            stringBuilder.append("\n");
        }

        List<Long> groupYears = groupRecordList.stream().map(GroupRecord::getTeacherEntityId).toList();
        List<Long> existingYears = guideRepository.fetchYears(Tables.YEAR.ENTITY_ID.in(groupYears))
                .stream().map(Year::getEntityId).toList();
        if(!new HashSet<>(existingYears).equals(new HashSet<>(groupYears))){
            stringBuilder.append("Несуществующий учебный год");
            stringBuilder.append("\n");
        }

        if(!stringBuilder.isEmpty()){
            throw new ValidationException(stringBuilder.toString());
        }
    }

    private GroupRecord getGroupRecordFromCSVLine(String[] elements){

        try {
            GroupRecord groupRecord = new GroupRecord();
            groupRecord.setEntityId(Long.parseLong(elements[0]));
            groupRecord.setParallelEntityId(Long.parseLong(elements[1]));
            groupRecord.setTeacherEntityId(Long.parseLong(elements[2]));
            groupRecord.setYearEntityId(Integer.parseInt(elements[3]));
            groupRecord.setDatetimeOfCreation(LocalDateTime.now());
            return groupRecord;
        }
        catch (NumberFormatException e){
            throw new ValidationException("Неверный формат данных в файле");
        }
    }

    public void delete(Long id) {
        groupRepository.updateDateTimeOfDeleteByIds(List.of(id), LocalDateTime.now());
    }

}

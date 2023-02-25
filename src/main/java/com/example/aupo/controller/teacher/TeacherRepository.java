package com.example.aupo.controller.teacher;

import com.example.aupo.tables.pojos.Teacher;
import com.example.aupo.tables.records.TeacherRecord;
import lombok.AllArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.aupo.Tables.PUPIL;
import static com.example.aupo.Tables.TEACHER;

@Repository
@AllArgsConstructor
public class TeacherRepository {

    private final DSLContext dslContext;

    public List<Teacher> fetch(Condition condition, Integer page, Integer pageSize){
        return dslContext
                .selectFrom(TEACHER)
                .where(condition)
                .limit(pageSize)
                .offset(pageSize * (page - 1))
                .fetchInto(Teacher.class);
    }

    public Teacher fetchActualByEntityId(Long entityId){
        return dslContext
                .selectFrom(TEACHER)
                .where(TEACHER.ENTITY_ID.eq(entityId),
                        TEACHER.DATETIME_OF_DELETE.isNull())
                .fetchOneInto(Teacher.class);
    }

    public void batchInsert(List<TeacherRecord> teacherRecordList){
        dslContext.batchInsert(teacherRecordList).execute();
    }

    public void updateDateTimeOfDeleteByIds(List<Long> entityIds, LocalDateTime localDateTime) {
        dslContext
                .update(TEACHER)
                .set(TEACHER.DATETIME_OF_DELETE, localDateTime)
                .where(TEACHER.ENTITY_ID.in(entityIds))
                .execute();
    }

}

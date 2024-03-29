package com.example.aupo.repository;

import com.example.aupo.tables.pojos.Teacher;
import lombok.AllArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.SortField;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.aupo.Sequences.TEACHER_ID_SEQ;
import static com.example.aupo.Tables.TEACHER;
import static org.jooq.impl.DSL.selectFrom;

@Repository
@AllArgsConstructor
public class TeacherRepository {

    private final DSLContext dslContext;

    public List<Teacher> fetch(Condition condition) {
        return dslContext
                .selectFrom(TEACHER)
                .where(condition)
                .fetchInto(Teacher.class);
    }

    public List<Teacher> fetch(Condition condition, Integer page, Integer pageSize, SortField<?> sortField) {
        return dslContext
                .selectFrom(TEACHER)
                .where(condition)
                .orderBy(sortField)
                .limit(pageSize)
                .offset(pageSize * (page - 1))
                .fetchInto(Teacher.class);
    }

    public Optional<Teacher> fetchActualByEntityId(Long entityId) {
        return Optional.ofNullable(
                dslContext
                        .selectFrom(TEACHER)
                        .where(TEACHER.ENTITY_ID.eq(entityId),
                                TEACHER.DATETIME_OF_DELETE.isNull())
                        .fetchOneInto(Teacher.class)
        );
    }

    public Long getCount(Condition condition) {
        return dslContext
                .selectCount()
                .from(TEACHER)
                .where(condition)
                .fetchOneInto(Long.class);
    }

    public void updateDateTimeOfDeleteByCondition(Condition condition, LocalDateTime localDateTime) {
        dslContext
                .update(TEACHER)
                .set(TEACHER.DATETIME_OF_DELETE, localDateTime)
                .where(condition)
                .execute();
    }

    public Long getNextId() {
        return dslContext.nextval(TEACHER_ID_SEQ);
    }

    public boolean exists(Condition condition) {
        return dslContext
                .fetchExists(
                        selectFrom(TEACHER)
                                .where(condition));
    }

    public void updateDateTimeOfDeleteByIds(List<Long> ids, LocalDateTime now) {
        dslContext
                .update(TEACHER)
                .set(TEACHER.DATETIME_OF_DELETE, now)
                .where(TEACHER.ENTITY_ID.in(ids))
                .execute();
    }
}

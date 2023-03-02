package com.example.aupo.controller.group;

import static com.example.aupo.Sequences.GROUP_ID_SEQ;
import static com.example.aupo.Tables.PUPIL;
import static com.example.aupo.tables.Group.GROUP;
import static org.jooq.impl.DSL.selectFrom;

import com.example.aupo.tables.pojos.Group;
import lombok.AllArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class GroupRepository {

    private final DSLContext dslContext;

    public List<Group> fetch(Condition condition) {
        return dslContext
                .selectFrom(GROUP)
                .where(condition)
                .fetchInto(Group.class);
    }

    public List<Group> fetch(Condition condition, Integer page, Integer pageSize) {
        return dslContext
                .selectFrom(GROUP)
                .where(condition)
                .limit(pageSize)
                .offset((page - 1) * pageSize)
                .fetchInto(Group.class);
    }

    public Optional<Group> fetchActualByEntityId(Long entityId) {
        return Optional.ofNullable(dslContext
                .selectFrom(GROUP)
                .where(GROUP.ENTITY_ID.eq(entityId),
                        GROUP.DATETIME_OF_DELETE.isNull())
                .fetchOneInto(Group.class)
        );
    }

    public Long getCount(Condition condition) {
        return dslContext
                .selectCount()
                .from(GROUP)
                .where(condition)
                .fetchOneInto(Long.class);
    }

    public Long getNextId() {
        return dslContext.nextval(GROUP_ID_SEQ);
    }

    public void updateDateTimeOfDeleteByCondition(Condition condition, LocalDateTime localDateTime) {
        dslContext
                .update(GROUP)
                .set(GROUP.DATETIME_OF_DELETE, localDateTime)
                .where(condition)
                .execute();
    }

    public boolean exists(Condition condition) {
        return dslContext
                .fetchExists(
                        selectFrom(GROUP)
                                .where(condition));
    }
}

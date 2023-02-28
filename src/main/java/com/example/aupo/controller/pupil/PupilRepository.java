package com.example.aupo.controller.pupil;

import com.example.aupo.tables.pojos.Pupil;
import com.example.aupo.tables.records.PupilRecord;
import lombok.AllArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.aupo.Sequences.PUPIL_ID_SEQ;
import static com.example.aupo.Tables.PUPIL;


@Repository
@AllArgsConstructor
public class PupilRepository {

    private final DSLContext dslContext;

    public List<Pupil> fetch(Condition condition, Integer page, Integer pageSize) {
        return dslContext
                .selectFrom(PUPIL)
                .where(condition)
                .limit(pageSize)
                .offset(pageSize * (page - 1))
                .fetchInto(Pupil.class);
    }

    public void batchInsert(List<PupilRecord> pupilRecordList) {
        dslContext
                .batchInsert(pupilRecordList).execute();
    }

    public void updateDateTimeOfDeleteByIds(List<Long> entityIds, LocalDateTime localDateTime) {
        dslContext
                .update(PUPIL)
                .set(PUPIL.DATETIME_OF_DELETE, localDateTime)
                .where(PUPIL.ENTITY_ID.in(entityIds))
                .execute();
    }

    public Optional<Pupil> fetchActualByEntityId(Long entityId) {
        return Optional.ofNullable(
                dslContext
                .selectFrom(PUPIL)
                .where(PUPIL.ENTITY_ID.eq(entityId),
                        PUPIL.DATETIME_OF_DELETE.isNull())
                .fetchOneInto(Pupil.class)
        );
    }

    public Long getCount(Condition condition) {
        return dslContext
                .selectCount()
                .from(PUPIL)
                .where(condition)
                .fetchOneInto(Long.class);
    }

    public void updateDateTimeOfDeleteByCondition(Condition condition, LocalDateTime localDateTime) {
        dslContext
                .update(PUPIL)
                .set(PUPIL.DATETIME_OF_DELETE, localDateTime)
                .where(condition)
                .execute();
    }

    public Long getNextId() {
        return dslContext.nextval(PUPIL_ID_SEQ);
    }
}

package com.example.aupo.controller.pupil;

import com.example.aupo.tables.pojos.Pupil;
import com.example.aupo.tables.records.PupilRecord;
import lombok.AllArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.aupo.Tables.PUPIL;


@Repository
@AllArgsConstructor
public class PupilRepository {

    private final DSLContext dslContext;

    public List<Pupil> fetch(Condition condition, Integer page, Integer pageSize){
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
}

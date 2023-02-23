package com.example.aupo.controller.pupil;

import com.example.aupo.tables.pojos.Pupil;
import lombok.AllArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.aupo.Tables.PUPIL;


@Repository
@AllArgsConstructor
public class PupilRepository {

    private final DSLContext dslContext;

    public List<Pupil> getList(Condition condition){
        return dslContext
                .selectFrom(PUPIL)
                .where(condition)
                .fetchInto(Pupil.class);
    }
}

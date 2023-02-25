package com.example.aupo.controller.guide;

import com.example.aupo.Tables;
import com.example.aupo.tables.pojos.Level;
import com.example.aupo.tables.pojos.Parallel;
import lombok.AllArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class GuideRepository {

    private final DSLContext dslContext;

    public List<Parallel> fetchParallels(Condition condition){
        return dslContext
                .selectFrom(Tables.PARALLEL)
                .where(condition)
                .fetchInto(Parallel.class);
    }

    public List<Level> fetchLevels(Condition condition){
        return dslContext
                .selectFrom(Tables.LEVEL)
                .where(condition)
                .fetchInto(Level.class);
    }

}

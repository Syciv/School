package com.example.aupo.repository;

import com.example.aupo.Tables;
import com.example.aupo.tables.pojos.Level;
import com.example.aupo.tables.pojos.Parallel;
import com.example.aupo.tables.pojos.Year;
import lombok.AllArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.jooq.impl.DSL.selectFrom;

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

    public List<Year> fetchYears(Condition condition){
        return dslContext
                .selectFrom(Tables.YEAR)
                .where(condition)
                .fetchInto(Year.class);
    }

    public boolean existsParallels(Condition condition){
        return dslContext
                .fetchExists(
                        selectFrom(Tables.PARALLEL)
                                .where(condition));
    }

}

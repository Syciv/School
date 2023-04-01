package com.example.aupo.controller.user;

import com.example.aupo.Tables;
import com.example.aupo.tables.pojos.User;
import lombok.AllArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.jooq.impl.DSL.selectFrom;

@Repository
@AllArgsConstructor
public class UserRepository {

    private final DSLContext dslContext;

    public Boolean fetchExists(String login) {
        return dslContext
                .fetchExists(
                        selectFrom(Tables.USER)
                                .where(Tables.USER.LOGIN.eq(login)
                                        .and(Tables.USER.DATETIME_OF_DELETE.isNull()))
                );
    }

    public List<User> fetch(Condition condition, Integer page, Integer pageSize){
        return dslContext
                .selectFrom(Tables.USER)
                .where(condition)
                .limit(pageSize)
                .offset(pageSize * (page - 1))
                .fetchInto(User.class);
    }

    public User fetchActual(String login){
        return dslContext
                .selectFrom(Tables.USER)
                .where(Tables.USER.LOGIN.eq(login)
                        .and(Tables.USER.DATETIME_OF_DELETE.isNull()))
                .fetchOneInto(User.class);
    }

    public Long getCount(Condition condition) {
        return dslContext
                .selectCount()
                .from(Tables.USER)
                .where(condition)
                .fetchOneInto(Long.class);
    }
}

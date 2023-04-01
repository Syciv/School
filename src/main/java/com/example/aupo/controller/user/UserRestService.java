package com.example.aupo.controller.user;

import com.example.aupo.Tables;
import com.example.aupo.controller.dto.ResponseList;
import com.example.aupo.exception.ValidationException;
import com.example.aupo.tables.daos.UserDao;
import com.example.aupo.tables.pojos.User;
import lombok.AllArgsConstructor;
import org.jooq.Condition;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class UserRestService {

    private final UserDao userDao;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public ResponseList<User> list(Integer page, Integer pageSize) {
        Condition condition = Tables.USER.DATETIME_OF_DELETE.isNull();

        List<User> items = userRepository.fetch(condition, page, pageSize);
        Long total = userRepository.getCount(condition);
        ResponseList<User> result = new ResponseList<>();
        result.setItems(items);
        result.setTotal(total);
        return result;
    }

    public void create(UserCreateDto userCreateDto){
        validateUser(userCreateDto);
        User user = new User();
        user.setLogin(userCreateDto.getLogin());
        user.setPassword(passwordEncoder.encode(userCreateDto.getPassword()));
        user.setName(userCreateDto.getName());
        user.setSurname(userCreateDto.getSurname());
        user.setPatronymic(userCreateDto.getPatronymic());
        user.setIsAdmin(userCreateDto.getIsAdmin());
        user.setIsActive(true);
        userDao.insert(user);
    }

    @Transactional
    public void update(UserCreateDto userCreateDto) {
        User user = userRepository.fetchActual(userCreateDto.getLogin());
        user.setDatetimeOfDelete(LocalDateTime.now());
        userDao.update(user);
        create(userCreateDto);
    }

    private void validateUser(UserCreateDto user){
        if(userRepository.fetchExists(user.getLogin())){
            throw new ValidationException("Такой пользователь уже существует!");
        }
        // Смешной сложный пароль > 8 символов + разные регистры + цифры + спецсимволы
        if(!user.getPassword().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")){
            throw new ValidationException("Пароль слишком простой!");
        }
    }

}

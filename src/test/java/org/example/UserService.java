package org.example;

import org.example.dao.UserDao;
import org.example.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public void createUser(Scanner scanner) {
        System.out.print("Имя: ");
        String name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Возраст: ");
        Integer age = Integer.parseInt(scanner.nextLine());

        User user = new User(name, email, age);
        userDao.save(user);
        System.out.println("Пользователь создан с ID " + user.getId());
    }

    public void listUsers() {
        List<User> users = userDao.findAll();
        if (users.isEmpty()) {
            System.out.println("Пользователи не найдены.");
        } else {
            users.forEach(System.out::println);
        }
    }

    public void findUser(Scanner scanner) {
        System.out.print("ID пользователя: ");
        Long id = Long.parseLong(scanner.nextLine());
        Optional<User> user = userDao.findById(id);
        ((Optional<?>) user).ifPresentOrElse(
                System.out::println,
                () -> System.out.println("Пользователь не найден")
        );
    }

    public void updateUser(Scanner scanner) {
        System.out.print("ID пользователя для обновления: ");
        Long id = Long.parseLong(scanner.nextLine());
        Optional<User> optionalUser = userDao.findById(id);
        if (optionalUser.isEmpty()) {
            System.out.println("Пользователь не найден");
            return;
        }

        User user = optionalUser.get();

        System.out.print("Новое имя (" + user.getName() + "): ");
        String name = scanner.nextLine();
        if (!name.isBlank()) user.setName(name);

        System.out.print("Новый email (" + user.getEmail() + "): ");
        String email = scanner.nextLine();
        if (!email.isBlank()) user.setEmail(email);

        System.out.print("Новый возраст (" + user.getAge() + "): ");
        String ageStr = scanner.nextLine();
        if (!ageStr.isBlank()) user.setAge(Integer.parseInt(ageStr));

        userDao.update(user);
        System.out.println("Пользователь обновлен");
    }

    public void deleteUser(Scanner scanner) {
        System.out.print("ID пользователя для удаления: ");
        Long id = Long.parseLong(scanner.nextLine());
        userDao.delete(id);
        System.out.println("Пользователь удален (если существовал)");
    }
}

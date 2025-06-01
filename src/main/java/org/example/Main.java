package org.example;

import org.example.dao.UserDao;
import org.example.model.User;
import org.example.util.HibernateUtil;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private static final UserDao userDao = new UserDao();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // отображаем меню и обрабатываем ввод пользователя
        while (true) {
            System.out.println("\nВыберите действие:");
            System.out.println("1 - Создать пользователя");
            System.out.println("2 - Показать всех пользователей");
            System.out.println("3 - Найти пользователя по ID");
            System.out.println("4 - Обновить пользователя");
            System.out.println("5 - Удалить пользователя");
            System.out.println("0 - Выход");

            System.out.print("Ввод: ");
            String input = scanner.nextLine();

            try {
                // Обработка ввода через switch-case
                switch (input) {
                    case "1" -> createUser(scanner);
                    case "2" -> listUsers();
                    case "3" -> findUser(scanner);
                    case "4" -> updateUser(scanner);
                    case "5" -> deleteUser(scanner);
                    case "0" -> {
                        HibernateUtil.shutdown();
                        System.out.println("Выход...");
                        return;
                    }
                    default -> System.out.println("Некорректный ввод");
                }
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }
    // Метод создания нового пользователя
    private static void createUser(Scanner scanner) {
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
    // Вывод всех пользователей
    private static void listUsers() {
        List<User> users = userDao.findAll();
        if (users.isEmpty()) {
            System.out.println("Пользователи не найдены.");
        } else {
            users.forEach(System.out::println);
        }
    }
   // поиск пользователя по ID
    private static void findUser(Scanner scanner) {
        System.out.print("ID пользователя: ");
        Long id = Long.parseLong(scanner.nextLine());
        Optional<User> user = userDao.findById(id);
        user.ifPresentOrElse(
                System.out::println,
                () -> System.out.println("Пользователь не найден")
        );
    }
    // обновление данных существующего пользователя
    private static void updateUser(Scanner scanner) {
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
    // удаление пользователя по ID
    private static void deleteUser(Scanner scanner) {
        System.out.print("ID пользователя для удаления: ");
        Long id = Long.parseLong(scanner.nextLine());
        userDao.delete(id);
        System.out.println("Пользователь удален (если существовал)");
    }
}

